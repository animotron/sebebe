/*
 * Copyright (c) 2013 Public domain
 * http://animotron.org/sebebe
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package sebebe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author <a href="mailto:gazdovsky@gmail.com">Evgeny Gazdovsky</a>
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 */
public class Manager {
    
    static TasksQueue tasks = new TasksQueue();

    private Map<String, Function> map = new HashMap<String, Function>();

    public Manager(String name, Function... f) {
        register(f);
    }

    public void register(Function... f) {
        for (Function i : f) {
            map.put(i.getName(), i);
        }
    }

    public void execute(Stream stream, String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        
        exec(stream, mapper.readTree(json));
    };

    private void exec(Stream stream, JsonNode n) {
        
        System.out.println(n);
        
        if (n instanceof ObjectNode) {
            exec(stream, (ObjectNode) n);
        }
    };

    private void exec(Stream stream, ObjectNode node) {
        
        JsonNode callNode = node.get("_");
        
        if (callNode == null) {
            //wrap
            
            Iterator<Entry<String, JsonNode>> iter = node.fields();
            while (iter.hasNext()) {
                Entry<String, JsonNode> field = iter.next();
                
                callNode = field.getValue().get("_");
                
                if (callNode == null) {
                    
                    JsonNode value = field.getValue();
                    
                    if (value instanceof ObjectNode) {
                        
                        //make copy
                        ObjectNode obj = ((ObjectNode) value).deepCopy();
                        
                        ObjectNode paramsNode = node.objectNode();
                        paramsNode.put(field.getKey(), obj);

                        ObjectNode wrapNode = node.objectNode();
                        wrapNode.put("_",  paramsNode);

                        stream.write(wrapNode);
                    }
                
                } else {
                    
                    JsonNode value = field.getValue();
                    
                    if (value instanceof ObjectNode) {
                        
                        //make copy
                        ObjectNode obj = ((ObjectNode) value).deepCopy();
                        
                        //remove function call, leave params
                        obj.remove("_");
                        
                        
                        ObjectNode paramsNode = node.objectNode();
                        paramsNode.put(field.getKey(), obj);
                        
                        ObjectNode wrapNode = node.objectNode();

                        wrapNode.put("_",  paramsNode);

                        call(stream, wrapNode, callNode);
                    }
                }
            }
            
        } else {
            
            //direct call
            call(stream, null, callNode);
        }
    }
    
    private void call(Stream stream, ObjectNode wrapNode, JsonNode callNode) {
        
        Iterator<Map.Entry<String, JsonNode>> i = callNode.fields();
        while (i.hasNext()) {
            Map.Entry<String, JsonNode> entry = i.next();

            String name = entry.getKey();

            if (name != null && !name.isEmpty()) {

                final Function function = map.get(name);
            
                if (function == null) {
                    //XXX: log?
                    //throw new IllegalArgumentException("Function '"+name+"' not found.");
                    continue;
                }

                tasks.call(function, entry.getValue(), stream.wrap(wrapNode));
            }
        }
    }

    public void startup() throws InterruptedException {
        tasks.startup();
    }

    public void shutdown() throws InterruptedException {
        tasks.shutdown();
        
        Thread.sleep(100);
    }
}