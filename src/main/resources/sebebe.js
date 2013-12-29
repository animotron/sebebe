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

/**
 * @author <a href="mailto:gazdovsky@gmail.com">Evgeny Gazdovsky</a>
 *
 */

sebebe = function(api){

    function isObject(o){
        return o instanceof Object && ! (o instanceof Array || o instanceof Function);
    }

    function _(o){
        if (!isObject(o)) return;
        var name = o.key(0);
        var param = o[name];
        if (!isObject(param)) return;
        var res = api[name](param);
        if (!isObject(res)) return;
        return res;
    }

    function call(o){
        var name = o.key(0);
        var param = o[name];
        if (!isObject(param)) return;
        var obj = {};
        if (param._) {
            var p = _(param._);
            for (var i in p) obj[i] = p[i];
        }
        for (var i in param) if (i != "_") obj[i] = param[i];
        var res = {};
        res[name] = obj;
        return {_ : res};
    }

    return function(o){
        if (!isObject(o)) return;
        if (o._) return _(o._);
        return call(o)
    };

};
