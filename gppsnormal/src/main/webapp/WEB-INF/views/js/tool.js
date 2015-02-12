var aCity={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江 ",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北 ",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏 ",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外 "}

cidInfo = function(sId){
var iSum=0
var info=""

	sId=sId.replace(/X$/i,"x");
	
var reg = /^\d{17}(\d|x)$/;

if(!reg.test(sId))
	return false;
sId=sId.replace(/x$/i,"a");
if(aCity[parseInt(sId.substr(0,2))]==null)
	//return "Error:非法地区";
	return false;
sBirthday=sId.substr(6,4)+"-"+Number(sId.substr(10,2))+"-"+Number(sId.substr(12,2));
var d=new Date(sBirthday.replace(/-/g,"/"))
if(sBirthday!=(d.getFullYear()+"-"+ (d.getMonth()+1) + "-" + d.getDate()))
	//return "Error:非法生日";
	return false;
for(var i = 17;i>=0;i --) iSum += (Math.pow(2,i) % 11) * parseInt(sId.charAt(17 - i),11)
if(iSum%11!=1)
	//return "Error:非法证号";
	return false;
//return aCity[parseInt(sId.substr(0,2))]+","+sBirthday+","+(sId.substr(16,1)%2?"男":"女")
	return true;
}

_$fd = function(longt) {
		var date = null;
		if (typeof longt == 'number') {
			date = new Date(longt);
		} else if (longt instanceof Date) {
			date = longt;
		} else {
			return null;
		}
		var yyyy = date.getFullYear();
		var MM = date.getMonth() + 1 >= 10 ? date.getMonth() + 1 : '0' + (date.getMonth() + 1);
		var dd = date.getDate() >= 10 ? date.getDate() : '0' + date.getDate();
		var HH = date.getHours() >= 10 ? date.getHours() : '0' + date.getHours();
		var mm = date.getMinutes() >= 10 ? date.getMinutes() : '0' + date.getMinutes();
		var ss = date.getSeconds() >= 10 ? date.getSeconds() : '0' + date.getSeconds();
		return {
			'yyyy' : yyyy,
			'MM' : MM,
			'dd' : dd,
			'HH' : HH,
			'mm' : mm,
			'ss' : ss
		}
	};

	formatDateToDay = function(longt) {
		//yyyy-MM-dd
		var r = _$fd(longt);
		var ldStr = r.yyyy + '-' + r.MM + '-' + r.dd;
		return ldStr;
	};
	
	formatForDatePicker = function(longt){
		var r = _$fd(longt);
		var ldStr = r.MM+'/'+r.dd+'/'+r.yyyy;
		return ldStr;
	}
	
	formatDate = function(longt) {
		//yyyy-MM-dd T HH:mm:ss
		var r = _$fd(longt);
		var ldStr = r.yyyy + '-' + r.MM + '-' + r.dd + ' T ' + r.HH + ':' + r.mm + ':' + r.ss;
		return ldStr;
	};
	
	formatDateCN = function(longt) {
		//yyyy-MM-dd T HH:mm:ss
		var r = _$fd(longt);
		//r.yyyy + '年' + 
		var ldStr = r.MM + '月' + r.dd + '日' + r.HH + '时' + r.mm + '分' + r.ss + '秒';
		return ldStr;
	};
	
	explorer = function(){
		if(navigator.userAgent.indexOf("Opera") != -1) { 
			alert('Opera'); 
			} 
			else if(navigator.userAgent.indexOf("MSIE") != -1) { 
			alert('Internet Explorer'); 
			} 
			else if(navigator.userAgent.indexOf("Firefox") != -1) { 
			alert('Firefox'); 
			} 
			else if(navigator.userAgent.indexOf("Netscape") != -1) { 
			alert('Netscape'); 
			} 
			else if(navigator.userAgent.indexOf("Safari") != -1) { 
			alert('Safari'); 
			} 
			else{ 
			alert('无法识别的浏览器。'); 
			} 
	}
	
	greet = function(){
		var now = new Date();
		var hour = now.getHours(); 
		var greeting = '';
		if(hour < 6){greeting = "凌晨好！";} 
		else if (hour < 9){greeting = "早上好！";} 
		else if (hour < 12){greeting = "上午好！";} 
		else if (hour < 14){greeting = "中午好！";} 
		else if (hour < 17){greeting = "下午好！";} 
		else if (hour < 19){greeting = "傍晚好！";} 
		else if (hour < 22){greeting = "晚上好！";} 
		else {greeting = "夜里好！";} 
		return greeting;
	}
	
	
	function getQueryString(name) { 
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
		var r = window.location.search.substr(1).match(reg); 
		if (r != null) return decodeURI(r[2]); return null; 
		}