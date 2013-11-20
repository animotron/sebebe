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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 
 * @author <a href="mailto:gazdovsky@gmail.com">Evgeny Gazdovsky</a>
 * @author <a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>
 */
public class TasksQueue {
    
    private static final int NTHREDS = 100;
    
    private ExecutorService executor;
    
    public TasksQueue() {
        executor = Executors.newFixedThreadPool(NTHREDS);
    }
    
    public void startup() {
        executor = Executors.newFixedThreadPool(NTHREDS);
    }

    public void shutdown() throws InterruptedException {
        executor.shutdown();
    }
    
    public void call(Function function, JsonNode data, StreamResults sr) {
        executor.execute(new FunctionTask(function, data, sr));
    }
    
    class FunctionTask implements Runnable {
        
        Function function;
        JsonNode data;
        StreamResults sr;
        
        public FunctionTask(Function function, JsonNode data, StreamResults sr) {
            
            this.function = function;
            this.data = data;
            this.sr = sr;
        }

        @Override
        public void run() {
            function.execute(data, sr);
        }
    }
}
