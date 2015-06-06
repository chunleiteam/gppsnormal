var submitService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ISubmitService");
var tpService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.thirdpay.IThirdPaySupportService");
_defaultDataTableOLanguage = {
		"sProcessing" : "<img src ='images/waiting.gif' height = 18/>正在查询中，请稍后......",
		"sLengthMenu" : "每页 _MENU_ 条记录",
		"sZeroRecords" : "无数据",
		"sInfo" : "当前第 _START_ 到  _END_ 条记录 共_TOTAL_条记录",
		"sInfoEmpty" : " ",
		"sSearch" : "查找： ",
		"oPaginate" : {
			"sFirst" : "",
			"sLast" : "",
			"sNext" : "下一页",
			"sPrevious" : "上一页"
		},
		"sInfoFiltered" : "(在 _MAX_ 条记录中查找)"
	};

_noDataTableOLanguage = {
		"sProcessing" : "<img src ='images/waiting.gif' height = 18/>正在查询中，请稍后......",
		"sLengthMenu" : "",
		"sZeroRecords" : "无数据",
		"sInfo" : "",
		"sInfoEmpty" : " ",
		"sSearch" : "查找： ",
		"oPaginate" : {
			"sFirst" : "",
			"sLast" : "",
			"sNext" : "下一页",
			"sPrevious" : "上一页"
		},
		"sInfoFiltered" : "(在 _MAX_ 条记录中查找)"
	};

var defaultSettings = {
				"bServerSide" : true,
				"bAutoWidth" : true,
				"bStateSave" : false, //保存状态到cookie ******很重要 ， 当搜索的时候页面一刷新会导致搜索的消失。使用这个属性设置为true就可避免了 
				"bPaginate" : true, // 是否使用分页
				"bProcessing" : false, //是否显示正在处理的提示 
				"bLengthChange" : true, //是否启用设置每页显示记录数 
				"iDisplayLength" : 10, //默认每页显示的记录数
				"aLengthMenu" : [ 10, 15, 25, 50 ],
				"bFilter" : false, //是否使用搜索 
				"bJQueryUI" : true, //页面风格使用jQuery.
				// "sScrollY": 200,//竖向滚动条 tbody区域的高度
				"sScrollX" : "500px", //横向滚动条 
				"sScrollXInner" : "100%",
				"bScrollCollapse" : true,
				"aoColumns" : [],
				"aaData" : [],
				"sPaginationType": "full_numbers", //分页样式
				"bAutoWidth" : true, //列的宽度会根据table的宽度自适应 
				"bSort" : false, //是否使用排序 
				"aoColumnDefs" : [ {
					"bSortable" : false,
					"aTargets" :["_all"]
				} ],
				"aaSorting" : [ [ 4, "desc" ] ]
				,
				"oLanguage" : _defaultDataTableOLanguage
			};
var defaultSettings_noCallBack = {
		"bServerSide" : false,
		"bAutoWidth" : true,
		"bStateSave" : false, //保存状态到cookie ******很重要 ， 当搜索的时候页面一刷新会导致搜索的消失。使用这个属性设置为true就可避免了 
		"bPaginate" : true, // 是否使用分页
		"bProcessing" : false, //是否显示正在处理的提示 
		"bLengthChange" : true, //是否启用设置每页显示记录数 
		"iDisplayLength" : 10, //默认每页显示的记录数
		"aLengthMenu" : [ 10, 15, 25, 50 ],
		"bFilter" : false, //是否使用搜索 
		"bJQueryUI" : false, //页面风格使用jQuery.
		// "sScrollY": 200,//竖向滚动条 tbody区域的高度
		"sScrollX" : "100%", //横向滚动条 
		"sScrollXInner" : "100%",
		"bScrollCollapse" : true,
		"aoColumns" : [],
		"aaData" : [],
		"sPaginationType": "full_numbers", //分页样式
		"bAutoWidth" : true, //列的宽度会根据table的宽度自适应 
		"bSort" : false, //是否使用排序 
		"aoColumnDefs" : [ {
			"bSortable" : false,
			"aTargets" :["_all"]
		} ],
		"aaSorting" : [ [ 4, "desc" ] ],
		"oLanguage" : _defaultDataTableOLanguage
	};



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

	formatDate = function(longt) {
		//yyyy-MM-dd T HH:mm:ss
		var r = _$fd(longt);
		var ldStr = r.yyyy + '-' + r.MM + '-' + r.dd + ' T ' + r.HH + ':' + r.mm + ':' + r.ss;
		return ldStr;
	};
	
	var cashstate = {
			0 : '充值',
			1 : '冻结',
			2 : '解冻',
			3 : '投标',
			4 : '还款',
			5 : '提现',
			6 : '存零',
			9 : '奖励',
			10 : '回购',
			11 : '购买',
			12 : '同步'
	}
	var productstate = {
			1:'融资中',
			2:'还款中',
			4:'流标',
			8:'还款完成',
			16:'还款中', 
			32:'还款完成',
			64:'还款完成'
	}
	
	var readletter=function(id){
		window.location.href="myaccountdetail.html?fid=mycenter&sid=letter-unread-mycenter";
		window.open('letter.html?lid='+id);
	}
	var afford = function(id){
		try{
	 		var submit = submitService.find(parseInt(id));
	 		
	 		//如果购买成功
	 		if(submit.state==2){
	 			alert('该投标已经支付成功！');
	 			window.location.href = "myaccountdetail.html?fid=submit&sid=submit-toafford";
	 			return;
	 		}
	 		
	        var transfer = null;
	        try{
	        transfer = tpService.getTransferToBuy(submit.id,''+submit.productId);
	        }catch(e){
	        	alert(e.message);
	        	return;
	        }
	        var content="<form id='buyform' action='"+transfer.baseUrl+"'method='post'>";
	        for(var o in transfer)
	       	{
	            if(o=="baseUrl")
	           	{
	            	continue;
	           	}
	            var len = o.length;
	            var name=o.substring(0,1).toUpperCase() + o.substring(1,len); 
	            content+="<input id='"+name+"' name='"+name+"' value='"+transfer[o]+"'/> ";
	       	}
	        content+="</form>";
	        $('#buyformdiv').html(content);
	        $('#buyform').submit();
	 		
	 		}catch(e){
	 			alert(e.message);
	 		}
	 		
//	 		/window.location.href = "myaccountdetail.html?fid=submit&sid=submit-all";
	 		
//	 		if(confirm('付款成功？')){
//				window.location.href = "myaccountdetail.html?fid=submit&sid=submit-toafford";
//			}else{
//				window.location.href = "myaccountdetail.html?fid=submit&sid=submit-toafford";
//			}
	}


	
	
	var mymaterial = function(container){
		
		var lenderService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ILenderService");
		
		var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="90%" cellspacing="0"></table>');
		table.append('<tbody></tbody>');
		
		var aaData = new Array();
		
		aaData.push(["登录名",user.loginId, "-"]);
		aaData.push(["姓名","<input style='width:99%;' type=text id='name' value="+user.name+"></input>", "<button id='namemodify'>修改</button>"]);
		aaData.push(["手机号",user.tel, "-"]);
		aaData.push(["邮箱","<input style='width:99%;' type=text id='email' value="+user.email+"></input>", "<button id='emailmodify'>修改</button>"]);
		aaData.push(["身份证",user.identityCard, "-"]);
		var addr = user.address==null?"":user.address;
		aaData.push(["住址","<input style='width:99%;' type=text id='address' value="+addr+"></input>", "<button id='addressmodify'>修改</button>"]);
		
		var thead = $('<thead style="display:none;"></thead>');
		var tr = $('<tr role="row"></tr>');
		tr.append('<th style="width: 135px;"></th>');
		tr.append('<th style="width: 102px;"></th>');
		tr.append('<th style="width:50px;"></th>');
		
		thead.append(tr);
		table.append(thead)
		
		
		table.append('<tbody></tbody>');
		
		container.append(table);
		
		table.addClass( 'nowrap' )
		.dataTable( {
			responsive: true,
			bFilter : false, //是否使用搜索 
			bSort : false,
			aaData : aaData,
			bPaginate : false, // 是否使用分页
			bLengthChange : false, //是否启用设置每页显示记录数 
			iDisplayLength : 10, //默认每页显示的记录数
			oLanguage : _noDataTableOLanguage,
			pagingType: "full"
		} );
		
		$('#namemodify').click(function(e){
			var name = $('#name').val();
			try{
				lenderService.changeAttri('name', name);
				alert('姓名修改成功！');
				window.location.href="myaccountdetail.html?fid=mycenter&sid=my-material";
			}catch(e){
				alert(e.message);
			}
		});
		
		$('#emailmodify').click(function(e){
			var email = $('#email').val();
			
			var emailreg = /^([\.a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+/;
			
			if(email==null || email==''){
				alert('邮箱不能为空！');
				return;
			}
			else if(!emailreg.test(email)){
				alert('*邮箱格式不正确');
				return;
			}else if(lenderService.isEmailExist(email)){
				alert('邮箱已经存在！');
				return;
			}
			
			
			try{
				lenderService.changeAttri('email', email);
				alert('邮箱修改成功！');
				window.location.href="myaccountdetail.html?fid=mycenter&sid=my-material";
			}catch(e){
				alert(e.message);
			}
		});
		
		$('#addressmodify').click(function(e){
			var address = $('#address').val();
			try{
				lenderService.changeAttri('address', address);
				alert('住址修改成功！');
				window.location.href="myaccountdetail.html?fid=mycenter&sid=my-material";
			}catch(e){
				alert(e.message);
			}
		});
		
	}
	
	var myscore = function(container){
		var lender=user;
		
		
		
		
		var aaData = new Array();
		
		aaData.push(["level5","10000000以上", "3个月", "1000000", "有最低消费"]);
		aaData.push(["level4","1000000-10000000", "3个月", "300000", "有最低消费"]);
		aaData.push(["level3","200000-1000000", "半年", "100000", "有最低消费"]);
		aaData.push(["level2","50000-200000", "一年", "10000", "有最低消费"]);
		aaData.push(["level1","10000-50000", "永久", "无", "无最低消费"]);
		aaData.push(["level0","0-10000", "永久", "无", "无最低消费"]);
		
		
		
		
		var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
		
		var thead = $('<thead></thead>');
		var tr = $('<tr role="row"></tr>');
		tr.append('<th style="width: 135px;">会员等级</th>');
		tr.append('<th style="width: 102px;">对应积分</th>');
		tr.append('<th style="width: 60px;">有效期</th>');
		tr.append('<th style="width: 60px;">最低贡献值</th>');
		tr.append('<th style="width: 102px;">说明</th>');
		
		thead.append(tr);
		table.append(thead)
		
		
		table.append('<tbody></tbody>');
		
		
		var name = lender.name==null?lender.loginId : lender.name
		container.append('<p>您好'+name+'，您的信用值是<span class="orange">'+lender.grade+'</span>分，信用等级为<span class="orange">level'+lender.level+'</span>， 通过投资购买可以提升您的积分和等级！</p>');
		container.append('<br><span class="orange">积分规则：</span>');
		container.append(table);
		
		table.addClass( 'nowrap' )
		.dataTable( {
			responsive: true,
			bFilter : false, //是否使用搜索 
			aaData : aaData,
			bPaginate : false, // 是否使用分页
			bLengthChange : false, //是否启用设置每页显示记录数 
			iDisplayLength : 10, //默认每页显示的记录数
			oLanguage : _noDataTableOLanguage,
			pagingType: "full"
		} );
	}
	
	
	
	
var myactivity = function(container){
	var refservice = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IActivityRefService");
	
	var actstate = {1 : '报名中', 2 : '进行中', 3 : '已结束'};
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "start")
				iDisplayStart = data.value;
			if (data.name == "length")
				iDisplayLength = data.value;
		}
		var res = null;
		res = refservice.findByLender(user.id, iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				result.aaData.push(['<a href="'+data.activity.url+'?id='+data.activity.id+'" target="_blank">'+data.activity.name+'</a>',
				             formatDate(data.activity.applystarttime),
				             formatDate(data.activity.starttime),
				             actstate[data.activity.state]]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		return res;
	}
	
	
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">活动标题</th>');
	tr.append('<th style="width: 150px;">申请截止时间</th>');
	tr.append('<th style="width: 150px;">正式活动时间</th>');
	tr.append('<th style="width: 42px;">状态</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		bServerSide : true,
		responsive: true,
		fnServerData : fnServerData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}

var submitall = function(container){
	var submitService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ISubmitService");
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "start")
				iDisplayStart = data.value;
			if (data.name == "length")
				iDisplayLength = data.value;
		}
		var res = null;
		res = submitService.findMyAllSubmitsByProductStates(-1,iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var item=items.get(i);
				result.aaData.push(["<a href='productdetail.html?pid="+item.product.id+"' >"+item.product.govermentOrder.title+"("+item.product.productSeries.title+")</a>",
				                    productstate[item.product.state],
				                    formatDateToDay(item.createtime),
				                    formatDateToDay(item.product.incomeEndtime),
				                    item.amount.value,
				                    item.repayedAmount.value,
				                    item.waitforRepayAmount.value]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);

		return res;
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">项目信息</th>');
	tr.append('<th style="width: 150px;">状态</th>');
	tr.append('<th style="width: 150px;">购买时间</th>');
	tr.append('<th style="width: 150px;">到期时间</th>');
	tr.append('<th style="width: 42px;">金额</th>');
	tr.append('<th style="width: 42px;">已还</th>');
	tr.append('<th style="width: 42px;">待还</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		bServerSide : true,
		responsive: true,
		fnServerData : fnServerData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}
var submitsubscribetoafford = function(container){
	var submitService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ISubmitService");
	var datas = null;
	datas = submitService.findMyAllWaitforPayingSubscribeSubmits();
	var aaData = new Array();
	for(var i=0; i<datas.size(); i++){
		var data=datas.get(i);
		aaData.push(["<a href='productdetail.html?pid="+data.product.id+"'>"+data.product.govermentOrder.title+"("+data.product.productSeries.title+")</a>",
		                    "预约成功待支付",
		                    formatDate(data.createtime),
		                    data.amount.value,
		                    formatDate(data.payExpiredTime),
		                    "<button id="+data.id+" class='submittoafford' href='javascript:void(0);'>立即支付</button>"]);
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 135px;">项目信息</th>');
	tr.append('<th style="width: 102px;">状态</th>');
	tr.append('<th style="width: 60px;">审核时间</th>');
	tr.append('<th style="width: 60px;">金额</th>');
	tr.append('<th style="width: 102px;">最迟支付时间</th>');
	tr.append('<th style="width: 42px;">操作</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	container.append(table);
	
	table.addClass( 'nowrap' )
	.dataTable( {
		responsive: true,
		bFilter : false, //是否使用搜索 
		aaData : aaData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
	
	$('button.submittoafford').click(function(e){
		afford($(this).attr('id'));
	});
	
}
var submittoafford = function(container){
	var submitService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ISubmitService");
	var datas = null;
	datas = submitService.findMyAllWaitforPayingSubmits();
	var aaData = new Array();
	for(var i=0; i<datas.size(); i++){
		var data=datas.get(i);
		aaData.push(["<a href='productdetail.html?pid="+data.product.id+"'>"+data.product.govermentOrder.title+"("+data.product.productSeries.title+")</a>",
		                    "待支付",
		                    formatDate(data.createtime),
		                    data.amount.value,
		                    formatDate(data.payExpiredTime),
		                    "<button id="+data.id+" class='submittoafford' href='javascript:void(0);'>立即支付</button>"]);
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 135px;">项目信息</th>');
	tr.append('<th style="width: 102px;">状态</th>');
	tr.append('<th style="width: 60px;">购买时间</th>');
	tr.append('<th style="width: 60px;">金额</th>');
	tr.append('<th style="width: 102px;">最迟支付时间</th>');
	tr.append('<th style="width: 42px;">操作</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	container.append(table);
	
	table.addClass( 'nowrap' )
	.dataTable( {
		responsive: true,
		bFilter : false, //是否使用搜索 
		aaData : aaData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
	
	$('button.submittoafford').click(function(e){
		afford($(this).attr('id'));
	});
}


var submitpayback = function(container){
	var submitService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ISubmitService");
	var paybackService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IPayBackService");
	var contractService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IContractService");
	var purchaseService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IPurchaseService");
	
	var paybackState={0: '待还款', 1 : '正在还款', 2: '已还款', 3: '延期' , 5: '待审核'};
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "start")
				iDisplayStart = data.value;
			if (data.name == "length")
				iDisplayLength = data.value;
		}
		var res = null;
		res = submitService.findMyAllSubmitsByProductStates((2+16),iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var item=items.get(i);
				
				var ff = contractService.submitContract(item.product.id, item.id);
				var con = ff==true?"<a href='/download/contract/"+item.product.id+"/"+item.id+"' target='_blank'>查看合同</a>":"尚未上传";
				
				result.aaData.push(["<a href='productdetail.html?pid="+item.product.id+"' >"+item.product.govermentOrder.title+"("+item.product.productSeries.title+")</a>",
				                    formatDateToDay(item.product.govermentOrder.financingEndtime),
				                    formatDateToDay(item.product.incomeEndtime),
				                    item.amount.value,
				                    item.repayedAmount.value,
				                    item.waitforRepayAmount.value,
				                    "<button class='repaydetail' id="+item.id+">查看</button>",
				                    "<button class='purchaseback' id="+item.id+">出售</button>",
				                    con]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		$('button.purchaseback').click(function(e){
			var itemid = parseInt($(this).attr('id'));
			var result = null;
			try{
				var time = (new Date()).getTime();
				purchaseService.canApplyPurchaseBack(itemid, time)
				result = purchaseService.preCalPurchaseBack(itemid, time);
			}catch(e){
				alert(e.message);
				return;
			}
			var str = "<table class='table' style='width:95%;'>";
			str+="<tr><td>投资本金</td><td>"+result.submitAmount.value+"</td></tr>";
			str+="<tr><td>已回款本金</td><td>"+result.chiefAlread.value+"</td></tr>";
			str+="<tr><td>已回款利息</td><td>"+result.interestAlready.value+"</td></tr>";
			str+="<tr><td>回购手续费</td><td>-"+result.purchaseBackFee.value+"</td></tr>";
			str+="<tr><td>出售金额</td><td><span class='orange'>"+result.purchaseAmount.value+"</span></td></tr>";
			str+="<tr><td colspan=2>持有期获得本息总计：<span class='orange'>"+result.totalAmount.value+"</span>元,共持有"+result.holdingDays+" 天，持有期年华利率<span class='orange'>"+result.rateAfterPB.value+"</span></td></tr>";
			
			str+="</table>";
			
			$('#pbdetail').html(str);
			$('#purchasebackdetail').modal({
				  keyboard: false,
				  backdrop: true
			});
			
			$('.confirm_purchaseback').attr('id', $(this).attr('id'));
		});
		$('button.repaydetail').click(function(e){
			var itemid = $(this).attr('id');
			var submititem = submitService.find(parseInt(itemid));
			var pays = paybackService.generatePayBacksBySubmit(submititem.id);
			var str = "<table class='table' style='width:95%;'>";
			str+="<tr><td colspan=5>本笔投资总金额为："+submititem.amount.value+"元, 预期还款明细如下：</td></tr>";
			str+="<tr><td>序号</td><td>还款日期</td><td>总额</td><td>本金</td><td>利息</td><td>状态</td></tr>";
			for(var i=0; i<pays.size(); i++){
				var pay = pays.get(i);
				str+="<tr><td>"+(i+1)+"</td><td>"+formatDateToDay(pay.deadline)+"</td><td>"+(parseFloat(pay.chiefAmount.value)+parseFloat(pay.interest.value)).toFixed(2)+"</td><td>"+pay.chiefAmount.value+"</td><td>"+pay.interest.value+"</td><td>"+paybackState[pay.state]+"</td></tr>";
			}
			str+="</table>";
			$('#rdetail').html(str);
			$('#repaydetail').modal({
				  keyboard: false,
				  backdrop: true
			});
		})

		return res;
	}
	
	
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 150px;">项目信息</th>');
	tr.append('<th style="width: 100px;">投标完成时间</th>');
	tr.append('<th style="width: 100px;">到期时间</th>');
	tr.append('<th style="width: 42px;">金额</th>');
	tr.append('<th style="width: 42px;">已回款</th>');
	tr.append('<th style="width: 42px;">待回款</th>');
	tr.append('<th style="width: 42px;">回款明细</th>');
	tr.append('<th style="width: 42px;">操作</th>');
	tr.append('<th style="width: 42px;">合同</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		bServerSide : true,
		responsive: true,
		fnServerData : fnServerData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}
var submitretreat = function(container){
	var submitService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ISubmitService");
	var columns = [ {
		"sTitle" : "项目信息",
			"code" : "info"
	}, {
		"sTitle" : "状态",
		"code" : "state"
	}, {
		"sTitle" : "退订时间",
		"code" : "lastmodifytime"
	}, {
		"sTitle" : "金额",
		"code" : "amount"
	}, {
		"sTitle" : "备注",
		"code" : "contract"
	}];
	var datas = null;
	datas = submitService.findMyAllRetreatSubmits();
	var aaData = new Array();
	for(var i=0; i<datas.size(); i++){
		var data=datas.get(i);
		aaData.push(["<a href='productdetail.html?pid="+data.product.id+"'>"+data.product.govermentOrder.title+"("+data.product.productSeries.title+")</a>",
		                    "已退订",
		                    formatDate(data.lastmodifytime),
		                    data.amount.value,
		                    "支付失败"]);
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 135px;">项目信息</th>');
	tr.append('<th style="width: 102px;">状态</th>');
	tr.append('<th style="width: 60px;">退订时间</th>');
	tr.append('<th style="width: 60px;">金额</th>');
	tr.append('<th style="width: 102px;">备注</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	container.append(table);
	
	table.addClass( 'nowrap' )
	.dataTable( {
		responsive: true,
		bFilter : false, //是否使用搜索 
		bSort : false,
		aaData : aaData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}
var submitdone = function(container){
	var submitService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ISubmitService");
	var contractService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IContractService");
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "start")
				iDisplayStart = data.value;
			if (data.name == "length")
				iDisplayLength = data.value;
		}
		var res = null;
		res = submitService.findMyAllSubmitsByProductStates((8+32+64),iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var item=items.get(i);
				
				var ff = contractService.submitContract(item.product.id, item.id);
				var con = ff==true?"<a href='/download/contract/"+item.product.id+"/"+item.id+"' target='_blank'>查看合同</a>":"尚未上传";
				
				
				result.aaData.push(["<a href='productdetail.html?pid="+item.product.id+"' >"+item.product.govermentOrder.title+"("+item.product.productSeries.title+")</a>",
				                    "还款完毕",
				                    formatDate(item.createtime),
				                    item.amount.value,
				                    item.repayedAmount.value,
				                    (parseFloat(item.amount.value)-parseFloat(item.repayedAmount.value)).toFixed(2),
				                    con]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);

		return res;
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">项目信息</th>');
	tr.append('<th style="width: 150px;">状态</th>');
	tr.append('<th style="width: 150px;">购买时间</th>');
	tr.append('<th style="width: 42px;">金额</th>');
	tr.append('<th style="width: 42px;">已回款</th>');
	tr.append('<th style="width: 42px;">待回款</th>');
	tr.append('<th style="width: 42px;">合同</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		bServerSide : true,
		responsive: true,
		fnServerData : fnServerData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}



var paybackall = function(container){
	var account = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IAccountService");
	var repayedDetail=account.getLenderRepayedDetail();
	var willBeRepayedDetail =account.getLenderWillBeRepayedDetail();
	
	var aaData = new Array();
	
	aaData.push(["已还款本金",
	             repayedDetail.get("oneyear").chiefAmount.value,
	             repayedDetail.get("halfyear").chiefAmount.value,
	             repayedDetail.get("threemonth").chiefAmount.value,
	             repayedDetail.get("twomonth").chiefAmount.value,
	             repayedDetail.get("onemonth").chiefAmount.value
                 ]);
	aaData.push(["已还款利息",
	             repayedDetail.get("oneyear").interest.value,
	             repayedDetail.get("halfyear").interest.value,
	             repayedDetail.get("threemonth").interest.value,
	             repayedDetail.get("twomonth").interest.value,
	             repayedDetail.get("onemonth").interest.value
                 ]);
	aaData.push(["已还款总计",
	             parseFloat(repayedDetail.get("oneyear").chiefAmount.value)+parseFloat(repayedDetail.get("oneyear").interest.value),
	             parseFloat(repayedDetail.get("halfyear").chiefAmount.value)+parseFloat(repayedDetail.get("halfyear").interest.value),
	             parseFloat(repayedDetail.get("threemonth").chiefAmount.value)+parseFloat(repayedDetail.get("threemonth").interest.value),
	             parseFloat(repayedDetail.get("twomonth").chiefAmount.value)+parseFloat(repayedDetail.get("twomonth").interest.value),
	             parseFloat(repayedDetail.get("onemonth").chiefAmount.value)+parseFloat(repayedDetail.get("onemonth").interest.value)
                 ]);
	
		aaData.push(["待还款本金",
		             	willBeRepayedDetail.get("oneyear").chiefAmount.value,
		             	willBeRepayedDetail.get("halfyear").chiefAmount.value,
		             	willBeRepayedDetail.get("threemonth").chiefAmount.value,
		             	willBeRepayedDetail.get("twomonth").chiefAmount.value,
		             	willBeRepayedDetail.get("onemonth").chiefAmount.value
	                    ]);
		aaData.push(["待还款利息",
		             	willBeRepayedDetail.get("oneyear").interest.value,
		             	willBeRepayedDetail.get("halfyear").interest.value,
		             	willBeRepayedDetail.get("threemonth").interest.value,
		             	willBeRepayedDetail.get("twomonth").interest.value,
		             	willBeRepayedDetail.get("onemonth").interest.value
	                    ]);
		aaData.push(["待还款总计",
		             parseFloat(willBeRepayedDetail.get("oneyear").chiefAmount.value)+parseFloat(willBeRepayedDetail.get("oneyear").interest.value),
		             parseFloat(willBeRepayedDetail.get("halfyear").chiefAmount.value)+parseFloat(willBeRepayedDetail.get("halfyear").interest.value),
		             parseFloat(willBeRepayedDetail.get("threemonth").chiefAmount.value)+parseFloat(willBeRepayedDetail.get("threemonth").interest.value),
		             parseFloat(willBeRepayedDetail.get("twomonth").chiefAmount.value)+parseFloat(willBeRepayedDetail.get("twomonth").interest.value),
		             parseFloat(willBeRepayedDetail.get("onemonth").chiefAmount.value)+parseFloat(willBeRepayedDetail.get("onemonth").interest.value)
	                    ]);
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 135px;">统计项</th>');
	tr.append('<th style="width: 102px;">近一年</th>');
	tr.append('<th style="width: 60px;">近半年</th>');
	tr.append('<th style="width: 60px;">近三个月</th>');
	tr.append('<th style="width: 102px;">近两个月</th>');
	tr.append('<th style="width: 42px;">近一个月</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	container.append(table);
	
	table.addClass( 'nowrap' )
	.dataTable( {
		responsive: true,
		bFilter : false, //是否使用搜索 
		aaData : aaData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
	
}


var paybackhave = function(container){
	var account = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IAccountService");
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "start")
				iDisplayStart = data.value;
			if (data.name == "length")
				iDisplayLength = data.value;
		}
		var res = null;
//		res = paybackService.findBorrowerPayBacks(2, -1, -1, iDisplayStart, iDisplayLength);
		res = account.findLenderRepayCashStream(iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var datas = res.get('result');
		if(datas)
		{
			for(var i=0; i<datas.size(); i++){
				var data=datas.get(i);
				result.aaData.push(["<a href='productdetail.html?pid="+data.submit.product.id+"' >"+data.submit.product.govermentOrder.title+"("+data.submit.product.productSeries.title+")</a>",
				                    (parseFloat(data.chiefamount.value)+parseFloat(data.interest.value)).toFixed(2),
				                    data.chiefamount.value,
				                    data.interest.value,
				                    formatDate(data.createtime)]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);

		return res;
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 135px;">项目信息</th>');
	tr.append('<th style="width: 102px;">还款额</th>');
	tr.append('<th style="width: 60px;">本金</th>');
	tr.append('<th style="width: 60px;">利息</th>');
	tr.append('<th style="width: 102px;">还款时间</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	container.append(table);
	
	table.addClass( 'nowrap' )
	.dataTable( {
		bServerSide : true,
		responsive: true,
		bFilter : false, //是否使用搜索 
		fnServerData : fnServerData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}




var paybackto = function(container){
	
	
	var account = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IAccountService");
	var paybacks = paybackService.findLenderWaitforRepay();
	
	var aaData = new Array();
	for(var i=0; i<paybacks.size(); i++){
		var data=paybacks.get(i);
		aaData.push(["<a href='productdetail.html?pid="+data.product.id+"' target='_blank'>"+data.product.govermentOrder.title+"("+data.product.productSeries.title+")</a>",
	                    (parseFloat(data.chiefAmount.value)+parseFloat(data.interest.value)).toFixed(2),
	                    data.chiefAmount.value,
	                    data.interest.value,
	                    formatDateToDay(data.deadline)
	                    ]);
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 135px;">项目信息</th>');
	tr.append('<th style="width: 102px;">还款额</th>');
	tr.append('<th style="width: 60px;">本金</th>');
	tr.append('<th style="width: 60px;">利息</th>');
	tr.append('<th style="width: 102px;">最迟还款时间</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	container.append(table);
	
	table.addClass( 'nowrap' )
	.dataTable( {
		bServerSide : false,
		responsive: true,
		bFilter : false, //是否使用搜索 
		aaData : aaData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}


var paybackto = function(container){
	var account = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IAccountService");
	var columns = [ {
		"sTitle" : "项目信息",
			"code" : "product"
	}, {
		"sTitle" : "还款额",
		"code" : "total"
	}, {
		"sTitle" : "本金",
		"code" : "bj"
	}, {
		"sTitle" : "利息",
		"code" : "lx"
	}, {
		"sTitle" : "预计还款时间",
		"code" : "time"
	}];
	var datas = null;
	datas = account.findLenderWaitforRepay();
	var aaData = new Array();
	for(var i=0; i<datas.size(); i++){
		var data=datas.get(i);
		aaData.push(["<a href='productdetail.html?pid="+data.product.id+"' >"+data.product.govermentOrder.title+"("+data.product.productSeries.title+")</a>",
		                    (parseFloat(data.chiefAmount.value)+parseFloat(data.interest.value)).toFixed(2),
		                    data.chiefAmount.value,
		                    data.interest.value,
		                    formatDateToDay(data.deadline)]);
	}
	var mySettings = $.extend({}, defaultSettings_noCallBack, {
		"aoColumns" : columns,
		"aaData" : aaData
	});
	var table = $('<table></table>');
	container.append(table);
	table.dataTable(mySettings);
}



var cashProcessor=function(action,state,container){
	var account = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IAccountService");
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "start")
				iDisplayStart = data.value;
			if (data.name == "length")
				iDisplayLength = data.value;
		}
		var res = null;
		res = account.findLenderCashStreamByActionAndState(action,state, iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var cashs = res.get('result');
		if(cashs){
		for(var i=0; i<cashs.size(); i++){
			result.aaData.push([formatDate(cashs.get(i).createtime), 
			                    (parseFloat(cashs.get(i).chiefamount.value)+parseFloat(cashs.get(i).interest.value)).toFixed(2), 
			                    cashs.get(i).chiefamount.value, 
			                    cashs.get(i).interest.value, 
			                    cashs.get(i).fee.value, 
			                    cashstate[cashs.get(i).action], 
			                    cashs.get(i).description]);
		}
		}
		
		result.sEcho = sEcho;
		
		fnCallback(result);
		
		return res;
	}
	
	
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 135px;">时间</th>');
	tr.append('<th style="width: 217px;">总金额</th>');
	tr.append('<th style="width: 102px;">本金</th>');
	tr.append('<th style="width: 42px;">利息</th>');
	tr.append('<th style="width: 93px;">手续费</th>');
	tr.append('<th style="width: 93px;">动作</th>');
	tr.append('<th style="width: 78px;">备注</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		bServerSide : true,
		responsive: true,
		bFilter : false, //是否使用搜索 
		fnServerData : fnServerData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}


var cashall = function(container){
	return cashProcessor(-1,2,container);
}

var cashrecharge = function(container){
	return cashProcessor(0,2,container);
}

var cashwithdraw = function(container){
	return cashProcessor(5,2,container);
}


var cashinvest = function(container){
	return cashProcessor(3,2,container);
}

var cashreceive = function(container){
	return cashProcessor(4,2,container);
}

var mynote = function(container){
	var content = $('<div></div>');
	var str = "";
	str += '<table class="table table-striped table-hover" style="min-width:300px;">';
	str += '<thead>';	
	str += '<tr><td style="min-width:100px;">标题</td><td style="min-width:50px;">发布日期</td><td style="min-width:100px;">公告类型</td></tr>';
	str += '</thead>';
	str += '<tbody>';
	str += '<tr><td><a href="#">新一期产品即将火爆抢购</td><td>2014-9-31</td><td>理财推荐</td></tr>';
	str += '<tr><td><a href="#">XXXX杯跑步活动</td><td>2014-10-5</td><td>春蕾活动</td></tr>';
	str += '<tr><td><a href="#">系统二期功能模块开放</td><td>2014-10-15</td><td>平台资讯</td></tr>';
	str += '</tbody>';
	str += '</table>';
	content.append(str);
	container.append(content);
}


var letterreaded = function(container){
	var letterS = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ILetterService");
	var columns = [ {
		"sTitle" : "标题",
			"code" : "time"
	}, {
		"sTitle" : "发送者",
		"code" : "total"
	}, {
		"sTitle" : "发送时间",
		"code" : "bj"
	}, {
		"sTitle" : "状态",
		"code" : "state"
	}, {
		"sTitle" : "已读时间",
		"code" : "lx"
	}];
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "start")
				iDisplayStart = data.value;
			if (data.name == "length")
				iDisplayLength = data.value;
		}
		var res = null;
		res = letterS.findMyLetters(1, iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var letters = res.get('result');
		if(letters){
		for(var i=0; i<letters.size(); i++){
			result.aaData.push([letters.get(i).title,
			                    "管理员",
			                    formatDate(letters.get(i).createtime), 
			                    letters.get(i).markRead==0?'未读':'已读',
			                    formatDate(letters.get(i).readtime)
			                   ]);
		}
		}
		result.sEcho = sEcho;
		fnCallback(result);

		return res;
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 135px;">标题</th>');
	tr.append('<th style="width: 102px;">发送者</th>');
	tr.append('<th style="width: 102px;">发送时间</th>');
	tr.append('<th style="width: 42px;">状态</th>');
	tr.append('<th style="width: 102px;">已读时间</th>');
	
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	container.append(table);
	
	table.addClass( 'nowrap' )
	.dataTable( {
		bServerSide : true,
		responsive: true,
		bFilter : false, //是否使用搜索 
		fnServerData : fnServerData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}






var letterunread_center = function(container){
	var letterS = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ILetterService");
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "start")
				iDisplayStart = data.value;
			if (data.name == "length")
				iDisplayLength = data.value;
		}
		var res = null;
		res = letterS.findMyLetters(0, iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var letters = res.get('result');
		if(letters)
		for(var i=0; i<letters.size(); i++){
			result.aaData.push([letters.get(i).title,
			                    "管理员",
			                    formatDateCN(letters.get(i).createtime), 
			                    letters.get(i).markRead==0?'未读':'已读',
			                    "<button class='readletter'  id='"+letters.get(i).id+"' value='阅读'>阅读</button>"
			                   ]);
		}
		result.sEcho = sEcho;
		fnCallback(result);
		$('.readletter').click(function(e){
			
			var letterService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ILetterService");
			var id = $(this).attr('id');
			var letter = letterService.find(parseInt(id));
			$('#myModalLabel').html(letter.title);
			$('#ldetail').html(letter.content);
			
			$('#letterdetail').modal({
				  keyboard: false,
				  backdrop: true
			});
		})
		
		

		return res;
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 135px;">标题</th>');
	tr.append('<th style="width: 102px;">发送者</th>');
	tr.append('<th style="width: 102px;">发送时间</th>');
	tr.append('<th style="width: 42px;">状态</th>');
	tr.append('<th style="width: 42px;">操作</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	container.append(table);
	
	table.addClass( 'nowrap' )
	.dataTable( {
		bServerSide : true,
		responsive: true,
		bFilter : false, //是否使用搜索 
		fnServerData : fnServerData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}

var letterunread = function(container){
	var letterS = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ILetterService");
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "start")
				iDisplayStart = data.value;
			if (data.name == "length")
				iDisplayLength = data.value;
		}
		var res = null;
		res = letterS.findMyLetters(0, iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var letters = res.get('result');
		if(letters){
		for(var i=0; i<letters.size(); i++){
			result.aaData.push([letters.get(i).title,
			                    "管理员",
			                    formatDate(letters.get(i).createtime), 
			                    letters.get(i).markRead==0?'未读':'已读',
			                    "<button class='readletter' id='"+letters.get(i).id+"' value='阅读'>阅读</a>"
			                   ]);
		}
		}
		result.sEcho = sEcho;
		fnCallback(result);

		$('.readletter').click(function(e){
			
			var letterService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ILetterService");
			var id = $(this).attr('id');
			var letter = letterService.find(parseInt(id));
			$('#myModalLabel').html(letter.title);
			$('#ldetail').html(letter.content);
			$('#letterdetail').modal({
				  keyboard: false,
				  backdrop: true
			});
		})	
		
		
		return res;
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 135px;">标题</th>');
	tr.append('<th style="width: 102px;">发送者</th>');
	tr.append('<th style="width: 102px;">发送时间</th>');
	tr.append('<th style="width: 42px;">状态</th>');
	tr.append('<th style="width: 42px;">操作</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	container.append(table);
	
	table.addClass( 'nowrap' )
	.dataTable( {
		bServerSide : true,
		responsive: true,
		bFilter : false, //是否使用搜索 
		fnServerData : fnServerData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}


var noticeview = function(container){
	var nservice = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.INoticeService");
	
	var userType = {0 : '全部', 1 : '投资方', 2 : '融资方'};
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		
		
		
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "start")
				iDisplayStart = data.value;
			if (data.name == "length")
				iDisplayLength = data.value;
		}
		
		
		var res = null;
		res = nservice.findAll(-1, iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				result.aaData.push([ (data.title.length>15)?(data.title.substring(0,15)+'..'):(data.title),
				             formatDate(data.publishtime),
				             userType[data.usefor],
				                    data.level,
				                    "<a href='detail.html?type=notice&stype="+data.publicType+"&id="+data.id+"' id='"+data.id+"' target='_blank'>查看</button>"]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		
		return res;
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">公告标题</th>');
	tr.append('<th style="width: 217px;">发布时间</th>');
	tr.append('<th style="width: 102px;">发布对象</th>');
	tr.append('<th style="width: 42px;">用户级别</th>');
	tr.append('<th style="width: 42px;">操作</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		bServerSide : true,
		responsive: true,
		fnServerData : fnServerData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}







var questionview = function(container){
	var helpservice = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IHelpService");
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "start")
				iDisplayStart = data.value;
			if (data.name == "length")
				iDisplayLength = data.value;
		}
		var res = null;
		res = helpservice.findMyHelps(-1, iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				
				var datatype = data.type;
				var answertype = "";
				var operation = "";
				if(datatype==1){
					answertype="未回答";
				}else{
					answertype="<font color=orange>已回答</font>";
				}
				
				operation = "<a class='viewanswer' href='detail.html?type=question&stype=-1&id="+data.id+"' target='_blank' id='"+data.id+"'>查看</a>";
				
				result.aaData.push([data.question,
				             formatDate(data.createtime),
				             data.questionerType,
				             data.questionerId,
				             answertype,
				                    operation]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		
		return res;
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">提问问题</th>');
	tr.append('<th style="width: 217px;">提问时间</th>');
	tr.append('<th style="width: 102px;">提问者类型</th>');
	tr.append('<th style="width: 42px;">提问者ID</th>');
	tr.append('<th style="width: 42px;">是否回答</th>');
	tr.append('<th style="width: 42px;">操作</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		bServerSide : true,
		responsive: true,
		fnServerData : fnServerData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}


var submittobepurchase = function(container){
	var submitService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ISubmitService");
	var paybackService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IPayBackService");
	var purchaseService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IPurchaseService");
	
	var paybackState={0: '待还款', 1 : '正在还款', 2: '已还款', 3: '延期' , 5: '待审核'};
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "start")
				iDisplayStart = data.value;
			if (data.name == "length")
				iDisplayLength = data.value;
		}
		var res = null;
		res = submitService.findAllSubmitsByStateAndProductStatesAndPurchaseFlag(2, (2+16), 1,iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var item=items.get(i);
				result.aaData.push(["<a href='productdetail.html?pid="+item.product.id+"' >"+item.product.govermentOrder.title+"("+item.product.productSeries.title+")</a>",
				                    formatDateToDay(item.product.govermentOrder.financingEndtime),
				                    formatDateToDay(item.product.incomeEndtime),
				                    item.amount.value,
				                    item.repayedAmount.value,
				                    item.waitforRepayAmount.value,
				                    "<button class='repaydetail' id="+item.id+">查看</button>",
				                    "<button class='applypurchase' id="+item.id+">购买</button>"]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		$('button.applypurchase').click(function(e){
			var itemid = parseInt($(this).attr('id'));
			var result = null;
			try{
				var time = (new Date()).getTime();
				purchaseService.canApplyPurchase(itemid, time)
				result = purchaseService.preCalPurchase(itemid, time);
			}catch(e){
				alert(e.message);
				return;
			}
			
			var str = "<table class='table' style='width:95%;'>";
			str+="<tr><td>投资本金</td><td>"+result.submitAmount.value+"</td></tr>";
			str+="<tr><td>已回款本金</td><td>"+result.chiefAlread.value+"</td></tr>";
			str+="<tr><td>剩余本金</td><td>"+result.chiefTo.value+"</td></tr>";
			str+="<tr><td>本还款周期持有</td><td>"+result.holdingDays+"天</td></tr>";
			str+="<tr><td>已积累利息</td><td>"+result.interestTo.value+"元</td></tr>";
			str+="<tr><td>购买金额</td><td><span class='orange'>"+result.purchaseAmount.value+"元</span></td></tr>";
			
			str+="</table>";
			
			$('#pdetail').html(str);
			$('#purchasedetail').modal({
				  keyboard: false,
				  backdrop: true
			});
			
			$('.confirm_purchase').attr('id', $(this).attr('id'));
		});
		$('button.repaydetail').click(function(e){
			var itemid = $(this).attr('id');
			var submititem = submitService.find(parseInt(itemid));
			var pays = paybackService.generatePayBacksBySubmit(submititem.id);
			var str = "<table class='table' style='width:95%;'>";
			str+="<tr><td colspan=5>本笔投资总金额为："+submititem.amount.value+"元, 预期还款明细如下：</td></tr>";
			str+="<tr><td>序号</td><td>还款日期</td><td>总额</td><td>本金</td><td>利息</td><td>状态</td></tr>";
			for(var i=0; i<pays.size(); i++){
				var pay = pays.get(i);
				str+="<tr><td>"+(i+1)+"</td><td>"+formatDateToDay(pay.deadline)+"</td><td>"+(parseFloat(pay.chiefAmount.value)+parseFloat(pay.interest.value)).toFixed(2)+"</td><td>"+pay.chiefAmount.value+"</td><td>"+pay.interest.value+"</td><td>"+paybackState[pay.state]+"</td></tr>";
			}
			str+="</table>";
			$('#rdetail').html(str);
			$('#repaydetail').modal({
				  keyboard: false,
				  backdrop: true
			});
		})

		return res;
	}
	
	
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 150px;">项目信息</th>');
	tr.append('<th style="width: 100px;">投标完成时间</th>');
	tr.append('<th style="width: 100px;">到期时间</th>');
	tr.append('<th style="width: 42px;">金额</th>');
	tr.append('<th style="width: 42px;">已回款</th>');
	tr.append('<th style="width: 42px;">待回款</th>');
	tr.append('<th style="width: 42px;">回款明细</th>');
	tr.append('<th style="width: 42px;">操作</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		bServerSide : true,
		responsive: true,
		fnServerData : fnServerData,
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
	
}


var nav2funtion = {
		"my-material" : mymaterial,
		"my-score" : myscore,
		"my-activity" : myactivity,
		"my-note" : mynote,
		"submit-all" : submitall,
		"submit-toafford" : submittoafford,
		"submit-subscribe-toafford" : submitsubscribetoafford,
		"submit-payback" : submitpayback,
		"submit-done" : submitdone,
		"submit-retreat" : submitretreat,
		"payback-all" : paybackall,
		"payback-have" : paybackhave,
		"payback-to" : paybackto,
		"cash-all" : cashall,
		"cash-recharge" : cashrecharge,
		"cash-withdraw" : cashwithdraw,
		"cash-invest" : cashinvest,
		"cash-receive" : cashreceive,
		"tools-fluxility" : "tools-fluxility",
		"tools-receive-statistics" : "tools-receive-statistics",
		
		
		"letter-unread-mycenter" : letterunread_center,
		"letter-unread" : letterunread,
		"letter-readed" : letterreaded,
		"notice-view" : noticeview,
		"question-view" : questionview,
		
		"purchase" : submittobepurchase,
		"purchaseback" : submitpayback
}