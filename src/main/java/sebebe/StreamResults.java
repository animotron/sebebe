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

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 *
 */
public class StreamResults {

    Stream _stream;
    ObjectNode wrapNode;
    
    protected StreamResults(Stream stream, ObjectNode obj) {
        _stream = stream;
        wrapNode = obj;
    }

    public void put(String field, JsonNode value) {
        
        ObjectNode result;
        
        if (wrapNode == null) {
            
            result = instance.objectNode();
            result.put(field, value);

        } else {
            
            result = wrapNode.deepCopy();
            
            ObjectNode function = (ObjectNode) result.fields().next().getValue();
            ObjectNode params = (ObjectNode) function.fields().next().getValue();
            
            params.put(field, value);
        }
        
        _stream.write(result);
    }

}
