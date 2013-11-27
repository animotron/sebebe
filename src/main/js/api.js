(function(){

    window.Api = function(protocol){

        var api = this;

        var port = "";
        if (host.indexOf(":") == -1) port = ":5711";

        function execute (o, level){
            function putAll(o, a) {
                for (var i in a)
                    if (a[i] instanceof Array) putAll(obj, a[i]);
                    else if (a[i] instanceof Object) obj[i] = a[i];
            }
            if (level > 1) return o;
            var obj = null;
            if (o instanceof Array)
                for (var i in o) execute(o[i]);
            else if (o instanceof Object)
                for (var name in o) {
                    if (name[0] == "$") {
                        var f = api[name.substring(1)];
                        if (obj == null) obj = {};
                        if (f instanceof  Function) putAll(obj, f(o[name]));
                    } else {
                        if (level == 0) name = "$" + name;
                        if (obj == null) obj = {};
                        obj[name] = o[i] instanceof Object ? execute(o[i], level + 1) : o[i];
                    }
                }
            return obj;
        }

        var socket;

        function newSocket(o){
            var a = [o];
            socket = new WebSocket('ws://' + location.host + port + '/ws', protocol);
            socket.queue = function(o){a.push(o)};
            socket.onclose = function(){console.log("close socket");};
            socket.onerror = function(e){console.log(e);};
            socket.onmessage = function(msg){
                var res = execute(JSON.parse(msg.data), 0);
                if (res != null) api.send(res);
            }
            socket.onopen = function(){
                for (var i in a) api.send(a[i]);
                a = null;
            };
        }

        this.send = function(o){
            if (socket)
                if (socket.readyState == 0)
                    socket.queue(o);
                else if (socket.readyState == 1)
                    socket.send(JSON.stringify(o));
                else newSocket(o);
            else newSocket(o);
        }

    }

})();
