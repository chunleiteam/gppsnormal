
/**
 *   EasyService JavaScript framework, version 1.2.4
**/
var java;	
if (typeof EasyServiceRuntime == "undefined"){
	
	var EasyServiceRuntime = {};
	EasyServiceRuntime.supportProto = {}.__proto__ ? true : false;
	//supportProto = false;
	EasyServiceRuntime.objHandlerMap = new Array();
	EasyServiceRuntime.typeField = "_t_";
	EasyServiceRuntime.idField = "_i_";
	EasyServiceRuntime.refidField = "_r_";
	java = {};
	java.util = {};
	java.lang = {};
	java.lang.Object = {};
	java.lang.Exception = function(msg){
		this.message = msg;
		this[EasyServiceRuntime.typeField] = "java.lang.Exception";
	};
	StringBuilder = java.lang.StringBuilder = function(){
		this.value=[];
		this.count = 0;
		for (var i = 0; i < arguments.length; i++){
			var item = arguments[i];
			if (item == null){
				item = "null";
			}else {
				item = item.toString();
			}
			this.value.push(item);
			this.count += item.length;
		}
	};
	java.lang.StringBuilder.prototype.append = function(){
		for (var i = 0; i < arguments.length; i++){
			var item = arguments[i];
			if (item == null){
				item = "null";
			}else {
				item = item.toString();
			}
			this.value.push(item);
			this.count += item.length;
		}
		return this;
	};
	java.lang.StringBuilder.prototype.toString= function() {
		return this.value.join("");
	};
	
	java.lang.StringBuilder.prototype.length= function() {
		return this.count;
	};
	/*
	java.lang.StringBuilder.prototype.insert(i, item){
		var nv = new Array();
		nv.push(this.toString());
		this.value = nv;
	}
	*/
	
	java.util.Date = Date;

	java.util.List = function(){
		this[EasyServiceRuntime.typeField] = "java.util.ArrayList";
		this.values = new Array();
	};
	java.util.List.prototype.size = function() {
		return this.values.length;
	};
	java.util.List.prototype.get = function(i) {
		return this.values[i];
	};
	java.util.List.prototype.set = function(i, o) {
		var rt = this.values[i];
		this.values[i] = o;
		return rt;
	};
	java.util.List.prototype.add = function() {
		if (arguments.length == 1) {
			this.values.push(arguments[0]);
		}else if (arguments.length > 1) {
			this.values.splice(arguments[0], 0, arguments[1]);
		}
	};
	java.util.List.prototype.isEmpty = function() {
		return  this.size() == 0;
	};
	java.util.List.prototype.remove = function(i) {
		this.values.splice(i, 1);
	};
	
	java.util.List.prototype.clear = function(i) {
		this.values=[];
	};
	
	java.util.List.prototype.toArray = function(i) {
		var rt = new Array();
		for (var i = 0; i < this.values.length; i++){
			rt[i] = this.values[i];
		}
		return rt;
	};
	
	java.util.ArrayList = java.util.List;
	
	java.util.Map = function(){
		this[EasyServiceRuntime.typeField] = "java.util.HashMap";
		this.table = [];
	};
	
	java.util.Map.Entry = function(k, v) {
		this.key = k;
		this.value = v;
	};
	
	java.util.Map.prototype.find = function(k) {
		var len = this.table.length;
		var table = this.table;
		for (var i = 0; i < len; i++){
			if (table[i].key == k){
				return i;
			}
		}
		return -1;
	};
	
	java.util.Map.prototype.put = function(k, v) {
		var i = this.find(k);
		var table = this.table;
		var rt = i == -1 ? null : this.table[i].value;
		if (i == -1) {
			table.push(new java.util.Map.Entry(k,v));
		}else {
			this.table[i].value = v;
		}
		return rt;
	};
	
	java.util.Map.prototype.putAll = function(map) {
		if (map != null) {
			var len = map.table.length;
			var table = map.table;
			for (var i = 0; i < len; i++){
				this.table.push(new java.util.Map.Entry(table[i].key,table[i].value));
			}
		}
	};
	
	java.util.Map.prototype.remove = function(k) {
		var i = this.find(k);
		if (i == -1) return null;
		var table = this.table;
		return table.splice(i, 1)[0].value;
	};
	
	java.util.Map.prototype.get = function(k) {
		var i = this.find(k);
		if (i == -1) {
			return null;
		}
		return this.table[i].value;
	};
	
	java.util.Map.prototype.containsKey= function(k) {
		return this.find(k) != -1;
	};
	
	java.util.Map.prototype.size = function() {
		return this.table.length;
	};
	
	java.util.Map.prototype.clear = function() {
		this.table = [];
	};
	java.util.Map.prototype.keySet = function() {
		var keys = new Array();
		var len = this.table.length;
		var table = this.table;
		for (var i = 0; i < len; i++){
			keys.push(table[i].key);
		}
		return keys;
	};
	
	java.util.Map.prototype.valueSet = function() {
		var values = new Array();
		var len = this.table.length;
		var table = this.table;
		for (var i = 0; i < len; i++){
			values.push(table[i].value);
		}
		return values;
	};
	
	java.util.HashMap = java.util.Map;
	java.util.LinkedHashMap = java.util.HashMap;
	java.util.Set = function(){
		this[EasyServiceRuntime.typeField] = "java.util.HashSet";
		this.map = new java.util.HashMap();
	};
	
	java.util.HashSet = java.util.Set;
	
	java.util.Set.prototype.add = function(e){
		return this.map.put(e, "") == null;
	};
	java.util.Set.prototype.size = function() {
		return this.map.size();
	};
	java.util.Set.prototype.isEmpty = function() {
		return this.map.isEmpty();
	};
	java.util.Set.prototype.contains = function(e) {
		return this.map.containsKey(e);
	};
	java.util.Set.prototype.remove = function(e) {
		return this.map.remove(e);
	};
	java.util.Set.prototype.clear = function() {
		this.map.clear();
	};
	
	com = {};
	com.cnebula = {};
	com.cnebula.common = {};
	com.cnebula.common.str = {};
	ZStringUtils = com.cnebula.common.str.ZStringUtils = {};
	com.cnebula.common.str.ZStringUtils.escapeJavaScript = function(js, escapeUnicode, escapeSingleQuote) {
			if (js == null) {
				return null;
			}
			var rt = new StringBuilder();
			var len = js.length;
			for (var i = 0; i < len; i++) {
				var ch = js.charAt(i);
				var cc = js.charCodeAt(i);
				switch (ch) {
					case '\b' :
						rt.append('\\');
						rt.append('b');
						break;
					case '\n' :
						rt.append('\\');
						rt.append('n');
						break;
					case '\t' :
						rt.append('\\');
						rt.append('t');
						break;
					case '\f' :
						rt.append('\\');
						rt.append('f');
						break;
					case '\r' :
						rt.append('\\');
						rt.append('r');
						break;
					case '\'' :
						if (escapeSingleQuote)
							rt.append('\\');
						rt.append('\'');
						break;
					case '"' :
						rt.append('\\');
						rt.append('"');
						break;
					case '\\' :
						rt.append('\\');
						rt.append('\\');
						break;
					case '/' :
						rt.append('\\');
						rt.append('/');
						break;
					default : 
						if (escapeUnicode) {
							if (ch > 0xfff) {
								rt.append("\\u" + cc.toString(16));
							} else if (ch > 0xff) {
								rt.append("\\u0" + cc.toString(16));
							} else if (ch > 0x7f) {
								rt.append("\\u00" + cc.toString(16));
							} else if (ch < 32) {
										if (ch > 0xf) {
											rt.append("\\u00" + cc.toString(16));
										} else {
											rt.append("\\u000" + cc.toString(16));
										}
							}else {
								rt.append(ch);
							}
						} else {
							rt.append(ch);	
						}
						
				}
				// handle unicode
			}
			return rt.toString();
	};
	
	EasyServiceRuntime.createPackage = function(pkg) {
		var ns = pkg.split(/\./);
		var cns = window;
		for (var i = 0; i < ns.length; i++){
			var tns = cns[ns[i]];
			if (tns){
				cns = tns;
			}else{
				cns = cns[ns[i]] = {};
			}
		}
		return cns;
	};
	
	EasyServiceRuntime.createClass = function(className) {
		var p = className.lastIndexOf(".");
		var pkg = null;
		var rt;
		if ( p != -1){
			pkg = EasyServiceRuntime.createPackage(className.substring(0, p));
		}
		if (!pkg) {
			pkg = window;
		}
		var clz = className.substring(p+1);
		rt = pkg[clz];
		if (!rt){
			//var cstr = new StringBuilder('(function() { this[EasyServiceRuntime.typeField] = "', className, '";})').toString();
			rt = pkg[clz] = function(){
				this[EasyServiceRuntime.typeField] = className;
			};
		}
		return rt;
	};
	
	EasyServiceRuntime.objHandlerMap["java.lang.Object"] = EasyServiceRuntime.objHandlerMap["Object"] = {
		name : "java.lang.Object",
		fix : function(o, refmap) {
			if (!refmap){
				refmap = new Array();
			}
			if (o == null){
				return null;
			}
			if (o[EasyServiceRuntime.refidField]) {
				return refmap[ o[EasyServiceRuntime.refidField] ];
			}
			var t = typeof o;
			if (t != "object") {
				return o;
			}
			var f = o[EasyServiceRuntime.typeField];
			var k = null;
			var h;
			if (f) {
				k = EasyServiceRuntime.createClass(f);
				h = EasyServiceRuntime.objHandlerMap[f];
				if (h && h != EasyServiceRuntime.objHandlerMap["Object"] && h.fix !=  EasyServiceRuntime.objHandlerMap["Object"].fix) return h.fix(o);
			}	
			h = null;
			var rt = EasyServiceRuntime.supportProto || !k ? o : new k();
			if (EasyServiceRuntime.supportProto && k) {
				rt.__proto__ = new k();		
			}
			if (o[EasyServiceRuntime.idField]) {
				refmap[ o[EasyServiceRuntime.idField] ] = rt;
			}
			var oi;
			for (var i in o) {
				h = null;
				oi = o[i];
				if (oi == null){
					rt[i] = null;
				}else {
					t = typeof oi;
					if (t == "object") {
						f = oi[EasyServiceRuntime.typeField];
						if (f) {
							h = EasyServiceRuntime.objHandlerMap[f];
						}
						if (h == null) {
								h = EasyServiceRuntime.objHandlerMap["Object"];
						}
						rt[i] = h.fix(oi, refmap);
					}
					else if (!EasyServiceRuntime.supportProto && t != "function"){
						rt[i] = oi;
					}
				}
			}
			
			return rt;
		},
		ser : function(o, refmap) {
			if (o == null){
				return "null";
			}
			var rt = new StringBuilder();
			var t = typeof o;
			if (t == "function"){
				return rt.append("{", EasyServiceRuntime.typeField, "=\"java.lang.Object\"}").toString();
			}
			if (t != "object"){
				if (t == "string") {
					return rt.append("\"", ZStringUtils.escapeJavaScript(o), "\"").toString();
				}else {
					return o.toString();
				}
			}
			if (refmap == null) {
				refmap = new java.util.HashMap();
			}
			var refid = refmap.get(o);
			if (refid) {
				return rt.append("{", EasyServiceRuntime.typeField, ":\"" , o[EasyServiceRuntime.typeField] , "\"," ,  EasyServiceRuntime.refidField,  ":" , refid,  "}").toString();
			}
			
			var f = o[EasyServiceRuntime.typeField];
			var h;
			if (f) {
				h = EasyServiceRuntime.objHandlerMap[f];
			}else {
				h = EasyServiceRuntime.objHandlerMap[o.constructor];
			}
			if (h && h != EasyServiceRuntime.objHandlerMap["Object"] ) {
				return h.ser(o, refmap);
			}
			var id = refmap.size();
			refmap.put(o, id);
			rt.append("{");
			if (o[EasyServiceRuntime.typeField]){
				rt.append(EasyServiceRuntime.typeField , ":\"" , o[EasyServiceRuntime.typeField], "\",");
			}
			rt.append(EasyServiceRuntime.idField , ":" , id);
			var oi;
			for (var i in o) {
				f = null;
				h = null;
				if (i == EasyServiceRuntime.refidField || i == EasyServiceRuntime.idField || i == EasyServiceRuntime.typeField) {
					continue;
				}
				oi = o[i];
				if (oi == null){
					rt.append(",", i , ":null");	
				}else{
					t = typeof oi;
					if (t == "function") {
						continue;
					}
					f = oi[EasyServiceRuntime.typeField];
					if (f) {
						h = EasyServiceRuntime.objHandlerMap[f];
					}else {
						h = EasyServiceRuntime.objHandlerMap[oi.constructor];
					}
					if (h == null) {
						h = EasyServiceRuntime.objHandlerMap["Object"];
					}
					rt.append(",");
					rt.append(i , ":" , h.ser(oi, refmap));	
				}
				
			}
			rt.append( "}" );
			return rt.toString();
		}
	};
	
	EasyServiceRuntime.objHandlerMap[Array] = EasyServiceRuntime.objHandlerMap["java.lang.Array"] = EasyServiceRuntime.objHandlerMap["Array"] = {
		name : "java.lang.Array",
		fix : EasyServiceRuntime.objHandlerMap["java.lang.Object"].fix,
		ser : function(o, refmap){
			if (refmap == null) {
				refmap = {count : 0};
			}
			var rt = new StringBuilder("[");
			for (var i = 0; i < o.length; i++) {
				if (i > 0) rt.append(",");
				rt.append(EasyServiceRuntime.objHandlerMap["java.lang.Object"].ser(o[i], refmap));
			}
			rt.append("]");
			return rt.toString();
		}
	};
	
	
	EasyServiceRuntime.objHandlerMap[Date] = EasyServiceRuntime.objHandlerMap["java.util.Date"] = {
		name : "java.util.Date",
		fix : function(o) {
			var rt = new Date(o.time);
			//rt.setUTCMilliseconds(o.time);
			return rt;
		},
		ser : function(o) {
			var rt = new StringBuilder("{", EasyServiceRuntime.typeField, ":\"java.util.Date\",");
			rt.append("time:", o.getTime ());
			rt.append("}");
			return rt.toString();
		}
	};
	
	EasyServiceRuntime.objHandlerMap["java.util.LinkedHashMap"] = EasyServiceRuntime.objHandlerMap["java.util.HashMap"] = {
		fix : EasyServiceRuntime.objHandlerMap["java.lang.Object"].fix,
		ser : function(o, refmap) {
			var rt = new StringBuilder("{", EasyServiceRuntime.typeField, ":\"java.util.HashMap\",");
			var id = refmap.size();
			refmap.put(o, id);
			rt.append(EasyServiceRuntime.idField , ":" , id);
			rt.append(",table:[");
			var table = o.table;
			var objser = EasyServiceRuntime.objHandlerMap["java.lang.Object"].ser;
			for (var i = 0; i < table.length; i++){
				if ( i > 0){
					rt.append(",");
				}
				rt.append("{key:", objser(table[i].key, refmap), ",value:", objser(table[i].value, refmap), "}" )
			}
			rt.append("]}");
			return rt.toString();
		}
	};
	
	EasyServiceRuntime.objHandlerMap["Date"] = EasyServiceRuntime.objHandlerMap["java.util.Date"];
	
	
	

	//alert("EasyServiceRuntime.supportProto=" + EasyServiceRuntime.supportProto);
	//EasyServiceRuntime.supportProto = false;
	
	
	EasyServiceRuntime.toEJson = EasyServiceRuntime.objHandlerMap["java.lang.Object"].ser;
	EasyServiceRuntime.fix = EasyServiceRuntime.objHandlerMap["java.lang.Object"].fix;
	EasyServiceRuntime.toJSObject = function(s) {
		return EasyServiceRuntime.fix(eval("(" + s+")"));
	};
}


if (typeof EasyServiceClient == "undefined"){
	function  EasyServiceClient_Init(){ 
		this.idCount = 0;
		this.reqUserCallbacks = new Array();
		this.rts = new Array();
		this.proxyObjects = new Array();
		this.invokingMethods = new Array();
		this.debug = true;
		this.remoteProxyCaches = {};
	}
	
	
	
	EasyServiceClient = new EasyServiceClient_Init();
	
	EasyServiceClient.ajaxFactory = {
		creator : null,
		createRequest : function() {
			if (this.creator == null){
				var tests = [ function() {return new XMLHttpRequest();},
     		 					function() {return new ActiveXObject('Msxml2.XMLHTTP');},
     							function() {return new ActiveXObject('Microsoft.XMLHTTP');}
     						];
     			for (var i = 0; i < tests.length; i++){
     				try {
     					var rt = tests[i]();
     					this.creator = tests[i];
     				}catch(e){continue;}
     			}
				
			}
			return this.creator();
		}
	};
	
	EasyServiceClient._createAjaxRequest = function() {
		return EasyServiceClient.ajaxFactory.createRequest();
	};
	
	EasyServiceClient.setDebug = function(debug) {
		EasyServiceClient.debug = debug;
	};
	
	EasyServiceClient._throwError = function(e) {
		//alert(e);
		throw e;
	};
	
	EasyServiceClient._scriptCallback = function(id, rt) {
		try{
		  //alert("return");
		  //this.rts[id] = (result);
		  //	  alert("callback[" +id + "]" + this.reqUserCallbacks[id] + "")
		  	  rt = EasyServiceRuntime.fix(rt);
			  if (typeof rt.result != "undefined"){
							EasyServiceClient.reqUserCallbacks[id](rt.result, true, this.invokingMethods[id], id);
			  }else if (rt.exception){
							EasyServiceClient.reqUserCallbacks[id](rt.exception, false, this.invokingMethods[id], id);
		 	  }else { //void call
		 	  	if (EasyServiceClient.reqUserCallbacks[id] != null){
		 	  		EasyServiceClient.reqUserCallbacks[id](null, true, this.invokingMethods[id], id);
		 	  	}
		 	  }
		}finally{
			delete EasyServiceClient.reqUserCallbacks[id]; // free memory for global variable reference
			delete EasyServiceClient.invokingMethods[id];
			EasyServiceClient._pickOneSrcNodeAndRemoveIt(id);
		}
	};
	
	
	
	EasyServiceClient._sendRequest = function(url, method,  reqArgs, cb, proxy){
			//var rt;
			var id = new Date().getTime ()+"_"+(EasyServiceClient.idCount++)+"_id";
			var params = new Array();
			//this[id+"_rt"] = null;
			for (var i = 0; i < reqArgs.length; i++){
				params[i] = reqArgs[i];
			}
			if (cb) {
				EasyServiceClient.reqUserCallbacks[id] = cb;
				EasyServiceClient.invokingMethods[id] = method;
			}
			if (proxy){
				EasyServiceClient.proxyObjects[id] = proxy;
				proxy.id = id;
			}
			var tenantId = (proxy != null && proxy.tenantId != null ? ".tenantId="+encodeURIComponent(proxy.tenantId)+"&": "" );
			//alert(url.substring(0, "http://".length));
			//if (url.substring(0, "http://".length) == "http://"){ // across domain request
			if (EasyServiceClient._checkURLAcrossDomain(url)){
				//if (!cb){
				//	this._throwError( "across domain request must provide callback function");
				//}
				var reqbody = {"id" : id, "method" : method+"_"+params.length, "params" : params, "callback" : cb == EasyServiceClient._createProxyMethodCallback ? "EasyServiceClient._createProxyMethodCallback" : "EasyServiceClient._scriptCallback"};
				var curSrc = url + "?" + tenantId + ".pf=" + encodeURIComponent("es:ws-json-http") + "&" + "request=" + encodeURIComponent(EasyServiceRuntime.toEJson(reqbody));
				var s = document.createElement("script");
				s.setAttribute("src", curSrc);
				s.setAttribute("id", id);
				s.setAttribute("charset", "UTF-8");
				//document.body.insertBefore(document.body, s);
				document.getElementsByTagName("head")[0].appendChild(s);
			}else{
				var req = {
					asynchronous : cb != null,
					contentType : "x-application/es:ws-json-http",
					onFailure : function(transport){
						//alert('fail ' + transport.status);
						EasyServiceClient._throwError( transport.status);
					}
				};
				req.onSuccess = EasyServiceClient._createOnSuccessMethod(id, cb);
				req.postBody = EasyServiceRuntime.toEJson({"id" : id, "method" : method+"_"+params.length, "params" : params});
				var fullGetUrl = url + "?" + tenantId  + "request=" + encodeURIComponent(req.postBody);
				if (fullGetUrl.length < 2000){ //use get method
					req.postBody = null;
					url = fullGetUrl;
				}else{
					req.postBody = tenantId + req.postBody;
				}
				var ajaxReq = EasyServiceClient._createAjaxRequest();
				if (req.asynchronous){
					ajaxReq.onreadystatechange = function() {
      				  if (ajaxReq.readyState == 4 && ajaxReq.status == 200)
         				   req.onSuccess(ajaxReq);
   					  }
				}
				ajaxReq.open(req.postBody === null ? "GET" : "POST", url, req.asynchronous);
				ajaxReq.setRequestHeader("Content-type", req.contentType);
				//alert(req.postBody);
				ajaxReq.send(req.postBody);
				if (!req.asynchronous){
					req.onSuccess(ajaxReq);
				}
				//new Ajax.Request(url, req);
			}
			var rt = EasyServiceClient.rts[id];
			if (!cb && rt){
				try{
					if (typeof rt.result != "undefined"){
						return   rt.result;
					}else if (rt.exception){
						EasyServiceClient._throwError( rt.exception );
					}else { //void
					}
				}finally{
					delete EasyServiceClient.rts[id];
				}
			}else{
				return id;
			}
		};
	
	EasyServiceClient._pickOneSrcNodeAndRemoveIt = function(curId){
		var num = curId.substring(0, curId.indexOf('_'));
		var sn = document.getElementById( (num-10) + "_id" );
		if (sn){
			document.getElementsByTagName("head")[0].removeChild(sn);
		}
	};
		
	EasyServiceClient._createProxyMethodList = function(methods, proxy) {
		for (var index = 0, len = methods.length; index < len; ++index) {
			var m = methods[index];
			var f = EasyServiceClient._createProxyMethod(m, proxy);
			proxy[m.name]=f;
		}
	};	
		
	EasyServiceClient._createOnSuccessMethod = function(id, cb){
		var ss = function(transport) {
				EasyServiceClient.rts[id] = EasyServiceRuntime.toJSObject(transport.responseText);
				var rt = EasyServiceClient.rts[id];
				if (cb){
					try{
						if (rt.result){
							//this.reqUserCallbacks[id](rt.result, true, this.invokingMethods[id], id);
							cb(rt.result, true, EasyServiceClient.invokingMethods[id], id);
						}else if (rt.exception){
							cb(rt.exception, false, EasyServiceClient.invokingMethods[id], id);
						}else { //void
							cb(null, true, EasyServiceClient.invokingMethods[id], id);
						}
					}finally{
						delete EasyServiceClient.rts[id];
						delete EasyServiceClient.reqUserCallbacks[id]; // free memory for global variable reference
						delete EasyServiceClient.invokingMethods[id];
						EasyServiceClient._pickOneSrcNodeAndRemoveIt(id);
					}
				}
		};
		return ss;
	};
	
	EasyServiceClient._createProxyMethod = function(m, proxy) {
		var sf = function(){
			//alert("args : " + arguments[0])
			var cb = proxy.cb;
			var params = new Array();
//			if (arguments.length < m.params.length){
//					EasyServiceClient._throwError(m.name + " need " + (m.params.length + 1) + " arguments");
//			}
			if (arguments.length > 0){
				var arglen = arguments.length;
				if (typeof arguments[arglen-1] == "function"){
					cb = arguments[arglen-1];
					arglen--;
				}
				for (var i = 0; i < arglen; i++){
					params[i] = arguments[i];
				}
			}
			return EasyServiceClient._sendRequest(this.url, m.name, params,  cb, this);
		};
		return sf;
	};
	//use for across domain create method
	EasyServiceClient._createProxyMethodCallback = function(id, rt) {
		var proxy = EasyServiceClient.proxyObjects[id];
		try {
			var methods = null;
			if (typeof rt.result != "undefined"){
						methods =  rt.result;
			}else{
				if (rt.exception == null){ // no such service
					rt.exception = new java.lang.Exception("no such service: " + EasyServiceClient.proxyObjects[id].clz);
				}
				EasyServiceClient._throwError( rt.exception );
			}
			EasyServiceClient._createProxyMethodList(methods, proxy);
			if (proxy.cb){
				proxy.cb(proxy, true, "$lookup", "$lookup");
			}
		}finally{
			delete EasyServiceClient.proxyObjects[id];
			delete EasyServiceClient.reqUserCallbacks[id]; // free memory for global variable reference
			delete EasyServiceClient.invokingMethods[id];
			//document.getElementsByTagName("head")[0].removeChild(document.getElementById(id));
			EasyServiceClient._pickOneSrcNodeAndRemoveIt(id);
		}
	};
	
	EasyServiceClient._checkURLAcrossDomain = function(url){
		var curUrl = window.location.protocol + "//" + window.location.host + "/";
		return !(url.length > curUrl.length && url.substring(0, curUrl.length) == curUrl)
	};
	
	EasyServiceClient._getUrl = function(host, port, clz){
		var url = "";
		if (host){
			url += "http://" + host;
			if (port){
				url += ":" + port;
			}
		}else{
			url = window.location.protocol + "//" + window.location.host;
		}
		if (clz){
			url += "/easyservice/"+clz;
		}
		return url;
	};
	
	EasyServiceClient.RemoteServiceProxy = function(host, port, clz, cb) {
		if (cb){
			this.cb = cb;
		}
		var url = EasyServiceClient._getUrl(host, port);
		if (clz){
			var discovererUrl = url + "/easyservice/com.cnebula.common.remote.IRemoteServiceDiscoverer";
			url += "/easyservice/"+clz;
			this.url = url;
			this.clz = clz;
			//add all method
			var dargs = new Array();
			dargs[0] = clz;
			if (EasyServiceClient._checkURLAcrossDomain(url)) {
				this.acrossDomain = true;
				var id = EasyServiceClient._sendRequest(discovererUrl, "queryMethodDescriptors", dargs, EasyServiceClient._createProxyMethodCallback, this);
//				EasyServiceClient.proxyObjects[id] = this;
//				this.id = id;
			}else{
				var methods = EasyServiceClient._sendRequest(discovererUrl, "queryMethodDescriptors", dargs);
				var proxy = EasyServiceClient._remoteProxyRegister(url, methods);
				for(var key in proxy){
					this[key] = proxy[key];
				}
				//EasyServiceClient._createProxyMethodList(methods, this);
				if (cb){
					cb(this,true, "$lookup", "$lookup");
				}
			}
		}
		this.tenantId = null;
		//alert(this.url);
	};
	
	EasyServiceClient.injectTenantId = function(remoteService, tenantId){
		remoteService.tenantId = tenantId;
	};
	
	EasyServiceClient.getRemoteProxy = function(url){
		if (url.indexOf("://") < 0){
			url = window.location.protocol + "//" + window.location.host + url;
		}
		var proxy = EasyServiceClient.remoteProxyCaches[url];
		if (proxy == null){
			return null;
		}
		var proxyClone = new EasyServiceClient.RemoteServiceProxy();
		for(var key in proxy){
			proxyClone[key] = proxy[key];
		}
		return proxyClone;
	};
	
	EasyServiceClient._remoteProxyRegisterFromServer = function(url, methodsResult){
		if (url.indexOf("://") < 0){
			url = window.location.protocol + "//" + window.location.host + url;
		}
		return EasyServiceClient._remoteProxyRegister(url, methodsResult.result);
	};
	
	EasyServiceClient._remoteProxyRegister = function(url, methods){
		var proxy = EasyServiceClient.remoteProxyCaches[url];
		if (!proxy){
			proxy = new EasyServiceClient.RemoteServiceProxy();
			proxy.url = url;
			proxy.acrossDomain = EasyServiceClient._checkURLAcrossDomain(url);//url.substring(0, "http://".length) == "http://";
			EasyServiceClient._createProxyMethodList(methods, proxy);
			EasyServiceClient.remoteProxyCaches[url] = proxy;
		}
		return proxy;
	};
	
	
	EasyServiceClient.lookup = function(host, port, clz, cb){
		try{
			return new EasyServiceClient.RemoteServiceProxy(host, port, clz, cb);
		}
		catch(e){
			if (e == null){
				return null;
			}
			throw e;
		}
	};
	EasyServiceClient.lookupByURL = function(url, cb){
		var host, port, clz, http="http://";
		url = url.substring(http.length);
		var hostPortPart = url.substring(0, url.indexOf("/"));
		if (hostPortPart.indexOf(":") == -1) {
			host = hostPortPart;
			port = "80";
		} else {
			var tempArray = hostPortPart.split(":");
			host = tempArray[0];
			port = tempArray[1];
		}
		clz = url.substring(url.lastIndexOf("/")+1);
		
		EasyServiceClient.lookup(host, port, clz, cb);
	};
	
	EasyServiceClient.cloneWithNewTarget = function(easyservice, url){
		var proxyClone = EasyServiceClient.getRemoteProxy(url);
		if (proxyClone == null){
			if (url.indexOf("://") < 0){
				url = window.location.protocol + "//" + window.location.host + url;
			}
			var proxy = easyservice;
			proxyClone = new EasyServiceClient.RemoteServiceProxy();
			for(var key in proxy){
				proxyClone[key] = proxy[key];
			}
			proxyClone.url = url;
			proxyClone.acrossDomain = EasyServiceClient._checkURLAcrossDomain(url);
			EasyServiceClient.remoteProxyCaches[url] = proxyClone;
			proxyClone = EasyServiceClient.getRemoteProxy(url);
		}
		return proxyClone;
	};
	
	EasyServiceClient.getParamters = function(url) {
		var map = {};
		for ( var pe, pes = url.split(/[?&]/), i = pes.length - 1; i > 0; i--) {
			pe = pes[i].split(/[=#]/);
			var k = pe[0].toLowerCase();
			var v = pe.length > 1 ? decodeURIComponent((pe[1])) : "";
			map[k] = v;
		}
		return map;
	};

	EasyServiceClient.initContext = function() {
		var ctx = EasyServiceClient['ctx'];
		if (!ctx) {
			EasyServiceClient['ctx'] = ctx = {};
			ctx['param'] = EasyServiceClient.getParamters(window.location.href);
			ctx.registeredTasks = new java.util.HashMap();
		}
		return ctx;
	};

	EasyServiceClient.newContext = function() {
		var ctx = {};
		ctx['param'] = ejs.getParamters(window.location.href);
		ctx.registeredTasks = new java.util.HashMap();
		return ctx;
	};
	
	EasyServiceClient.BatchCall = function(ctx, url){
		var bs = EasyServiceClient.getRemoteProxy('/easyservice/com.cnebula.common.remote.batch.IEasyBatchService');
		if (url != null){
			bs = EasyServiceClient.cloneWithNewTarget(bs,url);
		}
		this.bs = bs;
		this.callIndexs = {};
		this.returnMap = ctx || EasyServiceClient.initContext();
		this.calls = [];
		this.add = function(k,  s, m, args){
			this.calls.push({
			_t_:'com.cnebula.common.remote.batch.SingleRequest',
			service: s,
			method: m,
			args: args
			});
			this.callIndexs[k] = this.calls.length-1; 
		};
		this._handleReturn = function(rts){
			for (var i in this.callIndexs){
				this.returnMap[i] = rts[this.callIndexs[i]];
			}
			return this.returnMap;
		};
		this.execute = function(cb){
			if (cb){
				var bc = this;
				this.bs.batchCall(this.calls, function(rts){
						cb(bc._handleReturn(rts));
					});
				return null;
			}
			var rts = this.bs.batchCall(this.calls);
			return this._handleReturn(rts);
		};
	};
	
}

EasyServiceClient._remoteProxyRegisterFromServer("/easyservice/com.cnebula.common.remote.batch.IEasyBatchService",{_t_: "com.cnebula.common.remote.Response",_i_:0,id:"\/easyservice\/com.cnebula.common.remote.batch.IEasyBatchService",result:[{_t_: "com.cnebula.common.remote.MethodDescriptor",_i_:1,name:"batchCall",params:[{_t_: "com.cnebula.common.remote.ParamDescriptor",_i_:2,name:"0",type:"[Lcom.cnebula.common.remote.batch.SingleRequest;"}],returnType:"[Ljava.lang.Object;"}]});

(function(){
    

	var rsplit = function(string, regex) {
		var result = regex.exec(string),retArr = new Array(), first_idx, last_idx, first_bit;
		while (result != null)
		{
			first_idx = result.index; last_idx = regex.lastIndex;
			if ((first_idx) != 0)
			{
				first_bit = string.substring(0,first_idx);
				retArr.push(string.substring(0,first_idx));
				string = string.slice(first_idx);
			}		
			retArr.push(result[0]);
			string = string.slice(result[0].length);
			result = regex.exec(string);	
		}
		if (! string == '')
		{
			retArr.push(string);
		}
		return retArr;
	},
	chop =  function(string){
	    return string.substr(0, string.length - 1);
	},
	extend = function(d, s){
	    for(var n in s){
	        if(s.hasOwnProperty(n))  d[n] = s[n]
	    }
	}


	EJS = function( options ){
		options = typeof options == "string" ? {view: options} : options
	    this.set_options(options);
		if(options.precompiled){
			this.template = {};
			this.template.process = options.precompiled;
			EJS.update(this.name, this);
			return;
		}
	    if(options.element)
		{
			if(typeof options.element == 'string'){
				var name = options.element
				options.element = document.getElementById(  options.element )
				if(options.element == null) throw name+'does not exist!'
			}
			if(options.element.value){
				this.text = options.element.value
			}else{
				this.text = options.element.innerHTML
			}
			this.name = options.element.id
			this.type = '['
		}else if(options.url){
	        options.url = EJS.endExt(options.url, this.extMatch);
			this.name = this.name ? this.name : options.url;
	        var url = options.url
	        //options.view = options.absolute_url || options.view || options.;
			var template = EJS.get(this.name /*url*/, this.cache);
			if (template) return template;
		    if (template == EJS.INVALID_PATH) return null;
	        try{
	            this.text = EJS.request( url+(this.cache ? '' : '?'+Math.random() ));
	        }catch(e){}

			if(this.text == null){
	            throw( {type: 'EJS', message: 'There is no template at '+url}  );
			}
			//this.name = url;
		}
		var template = new EJS.Compiler(this.text, this.type);

		template.compile(options, this.name);

		
		EJS.update(this.name, this);
		this.template = template;
	};
	/* @Prototype*/
	EJS.prototype = {
		/**
		 * Renders an object with extra view helpers attached to the view.
		 * @param {Object} object data to be rendered
		 * @param {Object} extra_helpers an object with additonal view helpers
		 * @return {String} returns the result of the string
		 */
	    render : function(object, extra_helpers){
	        object = object || {};
	        this._extra_helpers = extra_helpers;
			var v = new EJS.Helpers(object, extra_helpers || {});
			return this.template.process.call(object, object,v);
		},
	    update : function(element, options){
	        if(typeof element == 'string'){
				element = document.getElementById(element)
			}
			if(options == null){
				_template = this;
				return function(object){
					EJS.prototype.update.call(_template, element, object)
				}
			}
			if(typeof options == 'string'){
				params = {}
				params.url = options
				_template = this;
				params.onComplete = function(request){
					var object = eval( request.responseText )
					EJS.prototype.update.call(_template, element, object)
				}
				EJS.ajax_request(params)
			}else
			{
				element.innerHTML = this.render(options)
			}
	    },
		out : function(){
			return this.template.out;
		},
	    /**
	     * Sets options on this view to be rendered with.
	     * @param {Object} options
	     */
		set_options : function(options){
	        this.type = options.type || EJS.type;
			this.cache = options.cache != null ? options.cache : EJS.cache;
			this.text = options.text || null;
			this.name =  options.name || null;
			this.ext = options.ext || EJS.ext;
			this.extMatch = new RegExp(this.ext.replace(/\./, '\.'));
		}
	};
	EJS.endExt = function(path, match){
		if(!path) return null;
		match.lastIndex = 0
		return path+ (match.test(path) ? '' : this.ext )
	}




	/* @Static*/
	EJS.Scanner = function(source, left, right) {
		
	    extend(this,
	        {left_delimiter: 	left +'%',
	         right_delimiter: 	'%'+right,
	         double_left: 		left+'%%',
	         double_right:  	'%%'+right,
	         left_equal: 		left+'%=',
	         left_comment: 	left+'%#'})

		this.SplitRegexp = left=='[' ? /(\[%%)|(%%\])|(\[%=)|(\[%#)|(\[%)|(%\]\n)|(%\])|(\n)/ : new RegExp('('+this.double_left+')|(%%'+this.double_right+')|('+this.left_equal+')|('+this.left_comment+')|('+this.left_delimiter+')|('+this.right_delimiter+'\n)|('+this.right_delimiter+')|(\n)') ;
		
		this.source = source;
		this.stag = null;
		this.lines = 0;
	};

	EJS.Scanner.to_text = function(input){
		if(input == null || input === undefined)
	        return '';
	    if(input instanceof Date)
			return input.toDateString();
		if(input.toString) 
	        return input.toString();
		return '';
	};

	EJS.Scanner.prototype = {
	  scan: function(block) {
	     scanline = this.scanline;
		 regex = this.SplitRegexp;
		 if (! this.source == '')
		 {
		 	 var source_split = rsplit(this.source, /\n/);
		 	 for(var i=0; i<source_split.length; i++) {
			 	 var item = source_split[i];
				 this.scanline(item, regex, block);
			 }
		 }
	  },
	  scanline: function(line, regex, block) {
		 this.lines++;
		 var line_split = rsplit(line, regex);
	 	 for(var i=0; i<line_split.length; i++) {
		   var token = line_split[i];
	       if (token != null) {
			   	try{
		         	block(token, this);
			 	}catch(e){
					throw {type: 'EJS.Scanner', line: this.lines};
				}
	       }
		 }
	  }
	};


	EJS.Buffer = function(pre_cmd, post_cmd) {
		this.line = new Array();
		this.script = "";
		this.pre_cmd = pre_cmd;
		this.post_cmd = post_cmd;
		for (var i=0; i<this.pre_cmd.length; i++)
		{
			this.push(pre_cmd[i]);
		}
	};
	EJS.Buffer.prototype = {
		
	  push: function(cmd) {
		this.line.push(cmd);
	  },

	  cr: function() {
		this.script = this.script + this.line.join('; ');
		this.line = new Array();
		this.script = this.script + "\n";
	  },

	  close: function() {
		if (this.line.length > 0)
		{
			for (var i=0; i<this.post_cmd.length; i++){
				this.push(pre_cmd[i]);
			}
			this.script = this.script + this.line.join('; ');
			line = null;
		}
	  }
	 	
	};


	EJS.Compiler = function(source, left) {
	    this.pre_cmd = ['var ___ViewO = [];'];
		this.post_cmd = new Array();
		this.source = ' ';	
		if (source != null)
		{
			if (typeof source == 'string')
			{
			    source = source.replace(/\r\n/g, "\n");
	            source = source.replace(/\r/g,   "\n");
				this.source = source;
			}else if (source.innerHTML){
				this.source = source.innerHTML;
			} 
			if (typeof this.source != 'string'){
				this.source = "";
			}
		}
		left = left || '<';
		var right = '>';
		switch(left) {
			case '[':
				right = ']';
				break;
			case '<':
				break;
			case '{':
				right = '}';
				break;
			default:
				throw left+' is not a supported deliminator';
				break;
		}
		this.scanner = new EJS.Scanner(this.source, left, right);
		this.out = '';
	};
	EJS.Compiler.prototype = {
	  compile: function(options, name) {
	  	options = options || {};
		this.out = '';
		var put_cmd = "___ViewO.push(";
		var insert_cmd = put_cmd;
		var buff = new EJS.Buffer(this.pre_cmd, this.post_cmd);		
		var content = '';
		var clean = function(content)
		{
		    content = content.replace(/\\/g, '\\\\');
	        content = content.replace(/\n/g, '\\n');
	        content = content.replace(/"/g,  '\\"');
	        return content;
		};
		this.scanner.scan(function(token, scanner) {
			if (scanner.stag == null)
			{
				switch(token) {
					case '\n':
						content = content + "\n";
						buff.push(put_cmd + '"' + clean(content) + '");');
						buff.cr();
						content = '';
						break;
					case scanner.left_delimiter:
					case scanner.left_equal:
					case scanner.left_comment:
						scanner.stag = token;
						if (content.length > 0)
						{
							buff.push(put_cmd + '"' + clean(content) + '")');
						}
						content = '';
						break;
					case scanner.double_left:
						content = content + scanner.left_delimiter;
						break;
					default:
						content = content + token;
						break;
				}
			}
			else {
				switch(token) {
					case scanner.right_delimiter:
						switch(scanner.stag) {
							case scanner.left_delimiter:
								if (content[content.length - 1] == '\n')
								{
									content = chop(content);
									buff.push(content);
									buff.cr();
								}
								else {
									buff.push(content);
								}
								break;
							case scanner.left_equal:
								buff.push(insert_cmd + "(EJS.Scanner.to_text(" + content + ")))");
								break;
						}
						scanner.stag = null;
						content = '';
						break;
					case scanner.double_right:
						content = content + scanner.right_delimiter;
						break;
					default:
						content = content + token;
						break;
				}
			}
		});
		if (content.length > 0)
		{
			// Chould be content.dump in Ruby
			buff.push(put_cmd + '"' + clean(content) + '")');
		}
		buff.close();
		this.out = buff.script + ";";
		var to_be_evaled = '/*'+name+'*/this.process = function(_CONTEXT,_VIEW) { try { with(_VIEW) { with (_CONTEXT) {'+this.out+" return ___ViewO.join('');}}}catch(e){e.lineNumber=null;throw e;}};";
		
		try{
			eval(to_be_evaled);
		}catch(e){
			if(typeof JSLINT != 'undefined'){
				JSLINT(this.out);
				for(var i = 0; i < JSLINT.errors.length; i++){
					var error = JSLINT.errors[i];
					if(error.reason != "Unnecessary semicolon."){
						error.line++;
						var e = new Error();
						e.lineNumber = error.line;
						e.message = error.reason;
						if(options.view)
							e.fileName = options.view;
						throw e;
					}
				}
			}else{
				throw e;
			}
		}
	  }
	};


	EJS.config = function(options){
		EJS.cache = options.cache != null ? options.cache : EJS.cache;
		EJS.type = options.type != null ? options.type : EJS.type;
		EJS.ext = options.ext != null ? options.ext : EJS.ext;
		
		var templates_directory = EJS.templates_directory || {}; //nice and private container
		EJS.templates_directory = templates_directory;
		EJS.get = function(path, cache){
			if(cache == false) return null;
			if(templates_directory[path]) return templates_directory[path];
	  		return null;
		};
		
		EJS.update = function(path, template) { 
			if(path == null) return;
			templates_directory[path] = template ;
		};
		
		EJS.INVALID_PATH =  -1;
	};
	EJS.config( {cache: true, type: '<', ext: '.ejs' } );



	/**
	 * @constructor
	 * By adding functions to EJS.Helpers.prototype, those functions will be available in the 
	 * views.
	 * @init Creates a view helper.  This function is called internally.  You should never call it.
	 * @param {Object} data The data passed to the view.  Helpers have access to it through this._data
	 */
	EJS.Helpers = function(data, extras){
		this._data = data;
	    this._extras = extras;
	    extend(this, extras );
	};
	/* @prototype*/
	EJS.Helpers.prototype = {
	    /**
	     * Renders a new view.  If data is passed in, uses that to render the view.
	     * @param {Object} options standard options passed to a new view.
	     * @param {optional:Object} data
	     * @return {String}
	     */
		view: function(options, data, helpers){
	        if(!helpers) helpers = this._extras
			if(!data) data = this._data;
			return new EJS(options).render(data, helpers);
		},
	    /**
	     * For a given value, tries to create a human representation.
	     * @param {Object} input the value being converted.
	     * @param {Object} null_text what text should be present if input == null or undefined, defaults to ''
	     * @return {String} 
	     */
		to_text: function(input, null_text) {
		    if(input == null || input === undefined) return null_text || '';
		    if(input instanceof Date) return input.toDateString();
			if(input.toString) return input.toString().replace(/\n/g, '<br />').replace(/''/g, "'");
			return '';
		}
	};
	    EJS.newRequest = function(){
		   var factories = [function() { return new ActiveXObject("Msxml2.XMLHTTP"); },function() { return new XMLHttpRequest(); },function() { return new ActiveXObject("Microsoft.XMLHTTP"); }];
		   for(var i = 0; i < factories.length; i++) {
		        try {
		            var request = factories[i]();
		            if (request != null)  return request;
		        }
		        catch(e) { continue;}
		   }
		}
		
		EJS.request = function(path){
		   var request = new EJS.newRequest()
		   request.open("GET", path, false);
		   
		   try{request.send(null);}
		   catch(e){return null;}
		   
		   if ( request.status == 404 || request.status == 2 ||(request.status == 0 && request.responseText == '') ) return null;
		   
		   return request.responseText
		}
		EJS.ajax_request = function(params){
			params.method = ( params.method ? params.method : 'GET')
			
			var request = new EJS.newRequest();
			request.onreadystatechange = function(){
				if(request.readyState == 4){
					if(request.status == 200){
						params.onComplete(request)
					}else
					{
						params.onComplete(request)
					}
				}
			}
			request.open(params.method, params.url)
			request.send(null)
		}


	})();

/*
 * HTML Parser By John Resig (ejohn.org)
 * Original code by Erik Arvidsson, Mozilla Public License
 * http://erik.eae.net/simplehtmlparser/simplehtmlparser.js
 *
 */

(function(){

	// Regular Expressions for parsing tags and attributes
	var startTag = /^<(\w+)((?:\s+\w+(?:\s*=\s*(?:(?:"[^"]*")|(?:'[^']*')|[^>\s]+))?)*)\s*(\/?)>/,
		endTag = /^<\/(\w+)[^>]*>/,
		attr = /(\w+)(?:\s*=\s*(?:(?:"((?:\\.|[^"])*)")|(?:'((?:\\.|[^'])*)')|([^>\s]+)))?/g;
		
	// Empty Elements - HTML 4.01
	var empty = makeMap("area,base,basefont,br,col,frame,hr,img,input,isindex,link,meta,param,embed");

	// Block Elements - HTML 4.01
	var block = makeMap("address,applet,blockquote,button,center,dd,del,dir,div,dl,dt,fieldset,form,frameset,hr,iframe,ins,isindex,li,map,menu,noframes,noscript,object,ol,p,pre,script,table,tbody,td,tfoot,th,thead,tr,ul");

	// Inline Elements - HTML 4.01
	var inline = makeMap("a,abbr,acronym,applet,b,basefont,bdo,big,br,button,cite,code,del,dfn,em,font,i,iframe,img,input,ins,kbd,label,map,object,q,s,samp,script,select,small,span,strike,strong,sub,sup,textarea,tt,u,var");

	// Elements that you can, intentionally, leave open
	// (and which close themselves)
	var closeSelf = makeMap("colgroup,dd,dt,li,options,p,td,tfoot,th,thead,tr");

	// Attributes that have their values filled in disabled="disabled"
	var fillAttrs = makeMap("checked,compact,declare,defer,disabled,ismap,multiple,nohref,noresize,noshade,nowrap,readonly,selected");

	// Special Elements (can contain anything)
	var special = makeMap("script,style");

	var HTMLParser = this.HTMLParser = function( html, handler ) {
		var index, chars, match, stack = [], last = html;
		stack.last = function(){
			return this[ this.length - 1 ];
		};

		while ( html ) {
			chars = true;

			// Make sure we're not in a script or style element
			if ( !stack.last() || !special[ stack.last() ] ) {

				// Comment
				if ( html.indexOf("<!--") == 0 ) {
					index = html.indexOf("-->");
	
					if ( index >= 0 ) {
						if ( handler.comment )
							handler.comment( html.substring( 4, index ) );
						html = html.substring( index + 3 );
						chars = false;
					}
	
				// end tag
				} else if ( html.indexOf("</") == 0 ) {
					match = html.match( endTag );
	
					if ( match ) {
						html = html.substring( match[0].length );
						match[0].replace( endTag, parseEndTag );
						chars = false;
					}
	
				// start tag
				} else if ( html.indexOf("<") == 0 ) {
					match = html.match( startTag );
	
					if ( match ) {
						html = html.substring( match[0].length );
						match[0].replace( startTag, parseStartTag );
						chars = false;
					}
				}

				if ( chars ) {
					index = html.indexOf("<");
					
					var text = index < 0 ? html : html.substring( 0, index );
					html = index < 0 ? "" : html.substring( index );
					
					if ( handler.chars )
						handler.chars( text );
				}

			} else {
				html = html.replace(new RegExp("(.*)<\/" + stack.last() + "[^>]*>"), function(all, text){
					text = text.replace(/<!--(.*?)-->/g, "$1")
						.replace(/<!\[CDATA\[(.*?)]]>/g, "$1");

					if ( handler.chars )
						handler.chars( text );

					return "";
				});

				parseEndTag( "", stack.last() );
			}

			if ( html == last )
				throw "Parse Error: " + html;
			last = html;
		}
		
		// Clean up any remaining tags
		parseEndTag();

		function parseStartTag( tag, tagName, rest, unary ) {
			if ( block[ tagName ] ) {
				while ( stack.last() && inline[ stack.last() ] ) {
					parseEndTag( "", stack.last() );
				}
			}

			if ( closeSelf[ tagName ] && stack.last() == tagName ) {
				parseEndTag( "", tagName );
			}

			unary = empty[ tagName ] || !!unary;

			if ( !unary )
				stack.push( tagName );
			
			if ( handler.start ) {
				var attrs = [];
	
				rest.replace(attr, function(match, name) {
					var value = arguments[2] ? arguments[2] :
						arguments[3] ? arguments[3] :
						arguments[4] ? arguments[4] :
						fillAttrs[name] ? name : "";
					
					attrs.push({
						name: name,
						value: value,
						escaped: value.replace(/(^|[^\\])"/g, '$1\\\"') //"
					});
				});
	
				if ( handler.start )
					handler.start( tagName, attrs, unary );
			}
		}

		function parseEndTag( tag, tagName ) {
			// If no tag name is provided, clean shop
			if ( !tagName )
				var pos = 0;
				
			// Find the closest opened tag of the same type
			else
				for ( var pos = stack.length - 1; pos >= 0; pos-- )
					if ( stack[ pos ] == tagName )
						break;
			
			if ( pos >= 0 ) {
				// Close all the open elements, up the stack
				for ( var i = stack.length - 1; i >= pos; i-- )
					if ( handler.end )
						handler.end( stack[ i ] );
				
				// Remove the open elements from the stack
				stack.length = pos;
			}
		}
	};
	
	this.HTMLtoXML = function( html ) {
		var results = "";
		
		HTMLParser(html, {
			start: function( tag, attrs, unary ) {
				results += "<" + tag;
		
				for ( var i = 0; i < attrs.length; i++ )
					results += " " + attrs[i].name + '="' + attrs[i].escaped + '"';
		
				results += (unary ? "/" : "") + ">";
			},
			end: function( tag ) {
				results += "</" + tag + ">";
			},
			chars: function( text ) {
				results += text;
			},
			comment: function( text ) {
				results += "<!--" + text + "-->";
			}
		});
		
		return results;
	};
	
	this.HTMLtoDOM = function( html, doc ) {
		// There can be only one of these elements
		var one = makeMap("html,head,body,title");
		
		// Enforce a structure for the document
		var structure = {
			link: "head",
			base: "head"
		};
		
		var elems = [doc];
		
		// Find all the unique elements
		if ( doc.getElementsByTagName )
			for ( var i in one )
				one[ i ] = doc.getElementsByTagName( i )[0];
		
		// If we're working with a document, inject contents into
		// the body element
		var curParentNode = doc;
		
		HTMLParser( html, {
			start: function( tagName, attrs, unary ) {
				// If it's a pre-built element, then we can ignore
				// its construction
				if ( one[ tagName ] ) {
					curParentNode = one[ tagName ];
					return;
				}
			
				var elem = document.createElement( tagName );
				
				for ( var attr in attrs ){
					var an = attrs[ attr ].name;
					if (an == 'class'){
						elem.className =  attrs[ attr ].value;
					}else if (an == 'onclick'){
						var isIE =  (window.navigator.appName.toLowerCase().indexOf("microsoft") > -1) ;
						if (isIE){
							elem.onclick = new Function(attrs[ attr ].value);
						}else{
							elem.setAttribute( an, attrs[ attr ].value );
						}
					}else{
						elem.setAttribute( an, attrs[ attr ].value );
					}
				}
				
				if ( structure[ tagName ] && typeof one[ structure[ tagName ] ] != "boolean" )
					one[ structure[ tagName ] ].appendChild( elem );
				
				else if ( curParentNode && curParentNode.appendChild )
					curParentNode.appendChild( elem );
					
				if ( !unary ) {
					elems.push( elem );
					curParentNode = elem;
				}
			},
			end: function( tag ) {
				elems.length -= 1;
				
				// Init the new parentNode
				curParentNode = elems[ elems.length - 1 ];
			},
			chars: function( text ) {
				if (curParentNode)
				curParentNode.appendChild( document.createTextNode( text ) );
			},
			comment: function( text ) {
				// create comment node
			}
		});
		
		return doc;
	};

	function makeMap(str){
		var obj = {}, items = str.split(",");
		for ( var i = 0; i < items.length; i++ )
			obj[ items[i] ] = true;
		return obj;
	}
})();

var ejs;
( function() {

	ejs = ejs || {};

	ejs.getParamters = EasyServiceClient.getParamters;

	ejs.initContext = EasyServiceClient.initContext;

	ejs.newContext = EasyServiceClient.newContext;

	ejs.ctx = ejs.initContext();
		
	ejs.evalSimple = function(tpl, ctx) {
		ctx = ctx || ejs.initContext();
		return new EJS( {
			text : tpl
		}).render(ctx);
	};

	ejs.evalComplex = function(tpl, ctx) {
		ctx = ctx || ejs.initContext();
		tpl = tpl.replace(/&lt;/g, '<');
		tpl = tpl.replace(/&gt;/g, '>');
		tpl = tpl.replace(/\<\!\-\-\#/g, '<%');
		tpl = tpl.replace(/\#\-\-\>/g, '%>');
		tpl = tpl.replace(/\+\-/g, '<%=');
		tpl = tpl.replace(/\-\+/g, '%>');
		tpl = tpl.replace(/\%3C\%/g, '<%');
		tpl = tpl.replace(/\%\%3E/g, '%>');
		tpl = tpl.replace(/\&lt;\%/g, '<%');
		tpl = tpl.replace(/\%\&gt;/g, '%>');
		tpl = tpl.replace(/\&amp;/g, '&');
		var html = ejs.evalSimple(tpl, ctx);
		//get all script
		html = html.replace(/<script.*?>([\s\S]*?)<\/.*?script>/ig, function(s,
				c) {
			eval(c);
			return "\n";
		});
		return html;
	};
	
	ejs.getNode = function(n) {
		var t = typeof n;
		if (t == 'string') {
			var c = n.charAt(0);
			if (c == '#'){
				return document.getElementById(n.substring(1));
			}
			return document.getElementById(n.substring(1));
		}
		return n;
	}

	ejs.getNodeText = function(n) {
		var t = typeof n;
		if (t == 'string') {
			var c = n.charAt(0);
			if (c == '#'){
				n = document.getElementById(n.substring(1));
			}else if (c == '.' || c == '/' || n.indexOf('http://') == 0){
				var ar = EasyServiceClient._createAjaxRequest();
				ar.open("GET", n, false);
				ar.setRequestHeader("Content-type", "text");
				ar.send();
				return ar.responseText;
			}else{
				return n;
			}
			
		}
		return n.innerHTML;
	};

	/**
	 * f : 来源，可以是:1)id，#开头，2)node, 比如document.body, 3) url 比如 http:// 4) string
	 * ctx: ctx为空，则采用ejs.ctx
	 * t: 目标，可以是:1)id，#开头，2)node, 比如document.body, 为空则和f相同，
	 */
	ejs.renderSimpleNode = function(f, ctx, t) {
		ctx = ctx || ejs.initContext();
		var tpl = ejs.getNodeText(f);
		t = ejs.getNode(t);
		if (!t) {
			t = ejs.getNode(f);
		}
		t.innerHTML = ejs.evalSimple(tpl, ctx);
	};

	/**
	 * f : 来源，可以是:1)id，#开头，2)node, 比如document.body, 3) url 比如 http://www.pku.edu.cn/my.txt, ./my.txt,  /my.txt 4) string
	 * ctx: ctx为空，则采用ejs.ctx
	 * t: 目标，可以是:1)id，#开头，2)node, 比如document.body, 为空则和f相同，
	 */
	ejs.renderComplexNode = function(f, t, ctx, isFragment) {
		ctx = ctx || ejs.initContext();
		var tpl = ejs.getNodeText(f);
		t = ejs.getNode(t);
		if (!t) {
			t = ejs.getNode(f);
		}
		ctx.registeredTasks.clear();
		var html = ejs.evalComplex(tpl, ctx);
		if (isFragment){
			HTMLtoDOM(html, t);
		}else{
			t.innerHTML = html;
		}
		var keys = ctx.registeredTasks.keySet();
		var errors = [];
		for ( var i in keys) {
			try {
				var task = ctx.registeredTasks.remove(keys[i]);
				task();
			} catch (e) {
				errors.push(e);
			} finally {

			}
		}
		if (errors.length > 0) {
			throw errors;
		}
	};

	//添加运行任务，tid是id，t是task的function，ctx为空则采用ejs.ctx
	ejs.addTask = function(tid, t, ctx) {
		ctx = ctx || ejs.initContext();
		ctx.registeredTasks.put(tid, t);
	}

	ejs.renderBody = function() {
		ejs.renderComplexNode(document.body);
	};
	
	//创建一个批调用实例，如果ctx为空，则采用ejs.ctx
	ejs.BatchCall = EasyServiceClient.BatchCall;
	
	
})();