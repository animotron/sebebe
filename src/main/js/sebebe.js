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
