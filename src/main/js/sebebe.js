sebebe = function(){

    function isObject(o) {
        return o instanceof Object && !(o instanceof Array);
    }

    function call(o) {

    }

    var self = {
        execute : function(o) {
            if (isObject(o))
                if (o._) call(o._);
                else {

                }
        }
    };

    return self;

};

Sebebe.prototype.execute = function(o){



};


(function(){

    window.sebebe = {};

    window.


           Manager : function(){
               var api = this;



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
           }

    }

})();
