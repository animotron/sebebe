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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:gazdovsky@gmail.com">Evgeny Gazdovsky</a>
 * 
 */
public class JsTest {

    private static Manager api = new Manager("some",
            new Function("ping") {
                @Override
                public void execute(JsonNode data, StreamResults stream) {
                    stream.put("msg", data);
                }
            },
            new Function("pank") {
                @Override
                public void execute(JsonNode data, StreamResults stream) {
                }
            },
            new Function("blah") {
                
                @Override
                public void execute(JsonNode data, StreamResults stream) {
                    
                    String msg = data.get("msg").asText();
                    
                    for (int i = 1; i <= 2; i++) {
                        stream.put("msg"+i, TextNode.valueOf(msg));
                    }
                }
            }
    );
    
    class StreamImpl extends Stream {
        
        public List<ObjectNode> recs = new ArrayList<ObjectNode>();

        @Override
        public void write(ObjectNode node) {
            recs.add(node);
        }
    };
    
    @Test
    public void test01() throws Exception {
        StreamImpl stream = new StreamImpl();
        
        api.startup();

        api.execute(stream, "{\"pong\":{\"ping\":\"hello\"}}");
        
        api.shutdown();
        
        assertEquals(1, stream.recs.size());
        assertEquals(
            "{\"_\":{\"pong\":{\"ping\":\"hello\"}}}", 
            stream.recs.get(0).toString()
        );
    }

    @Test
    public void test02() throws Exception {
        StreamImpl stream = new StreamImpl();

        api.startup();

        api.execute(stream, "{\"pong\":{\"_\": {\"ping\":\"hello\"}}}");
        
        api.shutdown();
        
        assertEquals(1, stream.recs.size());
        assertEquals(
            "{\"_\":{\"pong\":{\"msg\":\"hello\"}}}", 
            stream.recs.get(0).toString()
        );
    }

    @Test
    public void test03() throws Exception {
        StreamImpl stream = new StreamImpl();

        api.startup();

        api.execute(stream, "{\"_\":{\"ping\":\"hello\"}}");
        
        api.shutdown();
        
        assertEquals(1, stream.recs.size());
        assertEquals(
            "{\"msg\":\"hello\"}", 
            stream.recs.get(0).toString()
        );
    }

    @Test
    public void test04() throws Exception {
        StreamImpl stream = new StreamImpl();

        api.startup();

        api.execute(stream, "{\"pong\":{\"pong\":{\"_\":{\"ping\":\"hello\"}}}}");
        
        api.shutdown();
        
        //XXX: ??? assertEquals("{\"pong\":{\"pong\":{\"msg\":\"hello\"}}}", result);
        assertEquals(1, stream.recs.size());
        assertEquals(
            "{\"_\":{\"pong\":{\"pong\":{\"_\":{\"ping\":\"hello\"}}}}}", 
            stream.recs.get(0).toString()
        );
    }

    @Test
    public void test05() throws Exception {
        StreamImpl stream = new StreamImpl();

        api.startup();

        api.execute(stream, "{\"_\":{\"pank\":\"\"}}");
        
        api.shutdown();
        
        assertEquals(0, stream.recs.size());
    }

    @Test
    public void test06() throws Exception {
        StreamImpl stream = new StreamImpl();

        api.startup();

        api.execute(stream, "{\"pong\":{\"_\":{\"ping\":\"hello\"}, \"param\":\"test\"}}");
        
        api.shutdown();
        
        assertEquals(1, stream.recs.size());
        assertEquals(
            "{\"_\":"
                + "{\"pong\":"
                    + "{"
                        + "\"param\":\"test\","
                        + "\"msg\":\"hello\""
                    + "}"
                + "}"
            + "}", 
            stream.recs.get(0).toString()
        );
    }

    @Test
    public void test07() throws Exception {
        StreamImpl stream = new StreamImpl();

        api.startup();

        api.execute(stream, "{\"pong\":{\"_\":{\"blah\":{\"msg\":\"hello\"}}, \"param\":\"test\"}}");
        
        api.shutdown();
        
        assertEquals(2, stream.recs.size());
        assertEquals(
            "{\"_\":"
                + "{"
                    + "\"pong\":"
                    + "{"
                        + "\"param\":\"test\","
                        + "\"msg1\":\"hello\""
                    + "}"
                + "}"
            + "}", 
            stream.recs.get(0).toString()
        );
        assertEquals(
                "{\"_\":"
                    + "{"
                        + "\"pong\":"
                        + "{"
                            + "\"param\":\"test\","
                            + "\"msg2\":\"hello\""
                        + "}"
                    + "}"
                + "}", 
                stream.recs.get(1).toString()
            );
    }

    @Test
    public void test08() throws Exception {
        StreamImpl stream = new StreamImpl();

        api.startup();

        api.execute(stream,
            "{\"pong\":"
                + "{\"_\":"
                    + "{"
                        + "\"blah\":"
                        + "{"
                            + "\"msg\":\"hello\""
                        + "}"
                    + "},"
                + "\"param\":\"test\""
                + "}"
            + "}");
        
        api.shutdown();
        
        assertEquals(2, stream.recs.size());
        assertEquals(
            "[{\"_\":"
                + "{\"pong\":"
                    + "{"
                    + "\"param\":\"test\","
                    + "\"msg1\":\"hello\""
                    + "}"
                + "}"
            + "}, "
            + "{\"_\":"
                + "{\"pong\":"
                    + "{"
                    + "\"param\":\"test\","
                    + "\"msg2\":\"hello\""
                    + "}"
                + "}"
            + "}]",
            Arrays.toString(stream.recs.toArray()));
    }
}
