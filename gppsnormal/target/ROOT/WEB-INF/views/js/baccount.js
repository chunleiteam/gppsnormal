var paybackService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IPayBackService");
_defaultDataTableOLanguage = {
		"sProcessing" : "<img src ='images/waiting.gif' height = 18/>正在查询中，请稍后......",
		"sLengthMenu" : "每页 _MENU_ 条记录",
		"sZeroRecords" : "无数据",
		"sInfo" : "当前第 _START_ 到  _END_ 条记录 共_TOTAL_条记录",
		"sInfoEmpty" : " ",
		"sSearch" : "查找： ",
		"oPaginate" : {
			"sFirst" : "首页",
			"sLast" : "末页",
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
		"bJQueryUI" : true, //页面风格使用jQuery.
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
	
	var cashstate = {
			0 : '充值',
			1 : '冻结',
			2 : '解冻',
			3 : '投标',
			4 : '还款',
			5 : '提现',
			6 : '存零'
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
	var orderstate = {
			1: "融资中",
			2:"预发布",
			4:"还款中",
			8:"待关闭",
			16:"流标",
			32:"已关闭"
	}
	
var apply = function(id){
	if(confirm('提前还款一旦确认就无法更改，确认要提前本产品的还款？')){
	try{
		paybackService.applyRepayInAdvance(parseInt(id));
		window.location.href="baccountdetail.html?fid=payback&sid=payback-canpay";
	}catch(e){
		alert(e.message);
		window.location.href="baccountdetail.html?fid=payback&sid=payback-canapply";
	}
	}
	
}


var myscore = function(container){
	var bService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IBorrowerService");
	var borrower=bService.getCurrentUser();
	
	
	
	
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
	
	
	var name = borrower.name==null?borrower.loginId : borrower.name
	container.append('<p>您好'+name+'，您的信用值是<span class="orange">'+borrower.creditValue+'</span>分，信用等级为<span class="orange">level'+borrower.level+'</span></p>');
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
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}

var requestall = function(container){
	var bService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IBorrowerService");
	var requests = bService.findMyFinancingRequests(-1);
	
	var columns = [ {
		"sTitle" : "订单名称",
			"code" : "name"
	}, {
		"sTitle" : "申请额度",
		"code" : "state"
	}, {
		"sTitle" : "预期利率",
		"code" : "financingEndtime"
	}, {
		"sTitle" : "申请时间",
		"code" : "amount"
	}, {
		"sTitle" : "状态",
		"code" : "repayed"
	}];
	var aaData = new Array();
	for(var i=0; i<requests.size(); i++){
		var data=requests.get(i);
		var str = '待审核';
		if(data.state==1){
			str = '审核通过';
		}else if(data.state==2){
			str = '审核未通过';
		}
		aaData.push([data.govermentOrderName,
		                    data.applyFinancingAmount,
		                    data.rate,
		                    formatDate(data.createtime),
		                    str]);
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">订单名称</th>');
	tr.append('<th style="width: 217px;">申请额度</th>');
	tr.append('<th style="width: 102px;">预期利率</th>');
	tr.append('<th style="width: 42px;">申请时间</th>');
	tr.append('<th style="width: 42px;">状态</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		responsive: true,
		aaData : aaData,
		bFilter : false, //是否使用搜索 
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
	
}

var requesttohandle = function(container){
	var bService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IBorrowerService");
	var requests = bService.findMyFinancingRequests(0);
	
	var columns = [ {
		"sTitle" : "订单名称",
			"code" : "name"
	}, {
		"sTitle" : "申请额度",
		"code" : "state"
	}, {
		"sTitle" : "预期利率",
		"code" : "financingEndtime"
	}, {
		"sTitle" : "申请时间",
		"code" : "amount"
	}, {
		"sTitle" : "状态",
		"code" : "repayed"
	}];
	var aaData = new Array();
	for(var i=0; i<requests.size(); i++){
		var data=requests.get(i);
		aaData.push([data.govermentOrderName,
		                    data.applyFinancingAmount,
		                    data.rate,
		                    formatDate(data.createtime),
		                    '待审核']);
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">订单名称</th>');
	tr.append('<th style="width: 217px;">申请额度</th>');
	tr.append('<th style="width: 102px;">预期利率</th>');
	tr.append('<th style="width: 42px;">申请时间</th>');
	tr.append('<th style="width: 42px;">状态</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		responsive: true,
		aaData : aaData,
		bFilter : false, //是否使用搜索 
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}


var requestpass = function(container){
	var bService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IBorrowerService");
	var requests = bService.findMyFinancingRequests(1);
	
	var columns = [ {
		"sTitle" : "订单名称",
			"code" : "name"
	}, {
		"sTitle" : "申请额度",
		"code" : "state"
	}, {
		"sTitle" : "预期利率",
		"code" : "financingEndtime"
	}, {
		"sTitle" : "申请时间",
		"code" : "amount"
	}, {
		"sTitle" : "状态",
		"code" : "repayed"
	}];
	var aaData = new Array();
	for(var i=0; i<requests.size(); i++){
		var data=requests.get(i);
		
		aaData.push([data.govermentOrderName,
		                    data.applyFinancingAmount,
		                    data.rate,
		                    formatDate(data.createtime),
		                    '审核通过']);
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">订单名称</th>');
	tr.append('<th style="width: 217px;">申请额度</th>');
	tr.append('<th style="width: 102px;">预期利率</th>');
	tr.append('<th style="width: 42px;">申请时间</th>');
	tr.append('<th style="width: 42px;">状态</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		responsive: true,
		aaData : aaData,
		bFilter : false, //是否使用搜索 
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}


var requestrefuse = function(container){
	var bService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IBorrowerService");
	var requests = bService.findMyFinancingRequests(2);
	
	var columns = [ {
		"sTitle" : "订单名称",
			"code" : "name"
	}, {
		"sTitle" : "申请额度",
		"code" : "state"
	}, {
		"sTitle" : "预期利率",
		"code" : "financingEndtime"
	}, {
		"sTitle" : "申请时间",
		"code" : "amount"
	}, {
		"sTitle" : "状态",
		"code" : "repayed"
	}];
	var aaData = new Array();
	for(var i=0; i<requests.size(); i++){
		var data=requests.get(i);
		aaData.push([data.govermentOrderName,
		                    data.applyFinancingAmount,
		                    data.rate,
		                    formatDate(data.createtime),
		                    '审核未通过']);
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">订单名称</th>');
	tr.append('<th style="width: 217px;">申请额度</th>');
	tr.append('<th style="width: 102px;">预期利率</th>');
	tr.append('<th style="width: 42px;">申请时间</th>');
	tr.append('<th style="width: 42px;">状态</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		responsive: true,
		aaData : aaData,
		bFilter : false, //是否使用搜索 
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}

var orderall = function(container){
	var orderService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IGovermentOrderService");
	var productService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IProductService");
	var orders = orderService.findBorrowerOrderByStates(-1);
	var aaData = new Array();
	for(var i=0; i<orders.size(); i++){
		var data=orders.get(i);
		var products = data.products;
		var totalamount = 0;
		var real = 0;
		for(var j=0; j<products.size(); j++){
			totalamount += parseInt(products.get(j).expectAmount.value);
			real += parseInt(products.get(j).realAmount.value);
		}
		aaData.push([data.title,
		             formatDate(data.financingStarttime),
		             formatDate(data.financingEndtime),
		                    totalamount,
		                    real,
		                    orderstate[data.state],
		                    "<a class='vieworder' href='productdetail.html?pid="+products.get(0).id+"' target='_blank' id='"+data.id+"'>查看</a>"]);
	}
	
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">订单名称</th>');
	tr.append('<th style="width: 217px;">融资起始时间</th>');
	tr.append('<th style="width: 102px;">融资截止时间</th>');
	tr.append('<th style="width: 42px;">预期金额</th>');
	tr.append('<th style="width: 42px;">已融金额</th>');
	tr.append('<th style="width: 42px;">状态</th>');
	tr.append('<th style="width: 42px;">详情</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		responsive: true,
		aaData : aaData,
		bFilter : false, //是否使用搜索 
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}

var orderpreview = function(container){
	var orderService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IGovermentOrderService");
	var productService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IProductService");
	var orders = orderService.findBorrowerOrderByStates(2);
	
	var aaData = new Array();
	for(var i=0; i<orders.size(); i++){
		var data=orders.get(i);
		var products = data.products;
		var totalamount = 0;
		var real = 0;
		for(var j=0; j<products.size(); j++){
			totalamount += parseInt(products.get(j).expectAmount.value);
			real += parseInt(products.get(j).realAmount.value);
		}
		aaData.push([data.title,
		             formatDate(data.financingStarttime),
		             formatDate(data.financingEndtime),
		                    totalamount,
		                    real,
		                    orderstate[data.state],
		                    "<a class='vieworder' href='productdetail.html?pid="+products.get(0).id+"' target='_blank' id='"+data.id+"'>查看</a>"]);
	}
	
var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">订单名称</th>');
	tr.append('<th style="width: 217px;">融资起始时间</th>');
	tr.append('<th style="width: 102px;">融资截止时间</th>');
	tr.append('<th style="width: 42px;">预期金额</th>');
	tr.append('<th style="width: 42px;">已融金额</th>');
	tr.append('<th style="width: 42px;">状态</th>');
	tr.append('<th style="width: 42px;">详情</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		responsive: true,
		aaData : aaData,
		bFilter : false, //是否使用搜索 
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
	
}


var orderfinancing = function(container){
	var orderService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IGovermentOrderService");
	var productService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IProductService");
	var orders = orderService.findBorrowerOrderByStates(1);
	
	var aaData = new Array();
	for(var i=0; i<orders.size(); i++){
		var data=orders.get(i);
		var products = data.products;
		var totalamount = 0;
		var real = 0;
		for(var j=0; j<products.size(); j++){
			totalamount += parseInt(products.get(j).expectAmount.value);
			real += parseInt(products.get(j).realAmount.value);
		}
		aaData.push([data.title,
		             formatDate(data.financingStarttime),
		             formatDate(data.financingEndtime),
		                    totalamount,
		                    real,
		                    orderstate[data.state],
		                    "<a class='vieworder' href='productdetail.html?pid="+products.get(0).id+"' target='_blank' id='"+data.id+"'>查看</a>"]);
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">订单名称</th>');
	tr.append('<th style="width: 217px;">融资起始时间</th>');
	tr.append('<th style="width: 102px;">融资截止时间</th>');
	tr.append('<th style="width: 42px;">预期金额</th>');
	tr.append('<th style="width: 42px;">已融金额</th>');
	tr.append('<th style="width: 42px;">状态</th>');
	tr.append('<th style="width: 42px;">详情</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		responsive: true,
		aaData : aaData,
		bFilter : false, //是否使用搜索 
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
	
}

var orderpaying = function(container){
	var orderService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IGovermentOrderService");
	var productService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IProductService");
	var orders = orderService.findBorrowerOrderByStates(4);
	
	var aaData = new Array();
	for(var i=0; i<orders.size(); i++){
		var data=orders.get(i);
		var products = data.products;
		var real = 0;
		for(var j=0; j<products.size(); j++){
			real += parseInt(products.get(j).realAmount.value);
		}
		aaData.push([data.title,
		             formatDate(data.incomeStarttime),
		                    real,
		                    orderstate[data.state],
		                    "<a class='vieworder' href='productdetail.html?pid="+products.get(0).id+"' target='_blank' id='"+data.id+"'>查看</a>"]);
	}
	
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">订单名称</th>');
	tr.append('<th style="width: 217px;">计息起始时间</th>');
	tr.append('<th style="width: 102px;">实际融资金额</th>');
	tr.append('<th style="width: 42px;">状态</th>');
	tr.append('<th style="width: 42px;">详情</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		responsive: true,
		aaData : aaData,
		bFilter : false, //是否使用搜索 
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}

var ordertoclose = function(container){
	var orderService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IGovermentOrderService");
	var productService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IProductService");
	var orders = orderService.findBorrowerOrderByStates(8);
	
	var aaData = new Array();
	for(var i=0; i<orders.size(); i++){
		var data=orders.get(i);
		var products = data.products;
		var real = 0;
		for(var j=0; j<products.size(); j++){
			real += parseInt(products.get(j).realAmount.value);
		}
		aaData.push([data.title,
		             formatDate(data.lastModifytime),
		                    real,
		                    orderstate[data.state],
		                    "<a class='vieworder' href='productdetail.html?pid="+products.get(0).id+"' target='_blank' id='"+data.id+"'>查看</a>"]);
	}
	
var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">订单名称</th>');
	tr.append('<th style="width: 217px;">还款完成时间</th>');
	tr.append('<th style="width: 102px;">实际融资金额</th>');
	tr.append('<th style="width: 42px;">状态</th>');
	tr.append('<th style="width: 42px;">详情</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		responsive: true,
		aaData : aaData,
		bFilter : false, //是否使用搜索 
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}


var orderquit = function(container){

	var orderService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IGovermentOrderService");
	var productService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IProductService");
	var orders = orderService.findBorrowerOrderByStates(16);
	
	var aaData = new Array();
	for(var i=0; i<orders.size(); i++){
		var data=orders.get(i);
		var products = data.products;
		var expect = 0;
		var real = 0;
		for(var j=0; j<products.size(); j++){
			expect += parseInt(products.get(j).expectAmount.value);
			real += parseInt(products.get(j).realAmount.value);
		}
		aaData.push([data.title,
		             formatDate(data.lastModifytime),
		             		expect,
		                    real,
		                    orderstate[data.state],
		                    "<a class='vieworder' href='productdetail.html?pid="+products.get(0).id+"' target='_blank' id='"+data.id+"'>查看</a>"]);
	}
	
var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">订单名称</th>');
	tr.append('<th style="width: 217px;">流标时间</th>');
	tr.append('<th style="width: 102px;">预期融资金额</th>');
	tr.append('<th style="width: 102px;">实际融资金额</th>');
	tr.append('<th style="width: 42px;">状态</th>');
	tr.append('<th style="width: 42px;">详情</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		responsive: true,
		aaData : aaData,
		bFilter : false, //是否使用搜索 
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}


var orderclosed = function(container){

	var orderService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IGovermentOrderService");
	var productService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IProductService");
	var orders = orderService.findBorrowerOrderByStates(32);
	
	var aaData = new Array();
	for(var i=0; i<orders.size(); i++){
		var data=orders.get(i);
		var products = data.products;
		var real = 0;
		for(var j=0; j<products.size(); j++){
			real += parseInt(products.get(j).realAmount.value);
		}
		aaData.push([data.title,
		             formatDate(data.lastModifytime),
		                    real,
		                    orderstate[data.state],
		                    "<a class='vieworder' href='productdetail.html?pid="+products.get(0).id+"' target='_blank' id='"+data.id+"'>查看</a>"]);
	}
	var table = $('<table role="grid" id="example" class="display nowrap dataTable dtr-inline" width="99%" cellspacing="0"></table>');
	
	var thead = $('<thead></thead>');
	var tr = $('<tr role="row"></tr>');
	tr.append('<th style="width: 235px;">订单名称</th>');
	tr.append('<th style="width: 217px;">关闭时间</th>');
	tr.append('<th style="width: 102px;">实际融资金额</th>');
	tr.append('<th style="width: 42px;">状态</th>');
	tr.append('<th style="width: 42px;">详情</th>');
	
	thead.append(tr);
	table.append(thead)
	
	
	table.append('<tbody></tbody>');
	
	container.append(table);
	
	
	table.addClass( 'nowrap' )
	.dataTable( {
		responsive: true,
		aaData : aaData,
		bFilter : false, //是否使用搜索 
		oLanguage : _defaultDataTableOLanguage,
		pagingType: "full"
	} );
}





var paybackall = function(container){
	var account = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IAccountService");
	var repayedDetail=account.getBorrowerRepayedDetail();
	var willBeRepayedDetail =account.getBorrowerWillBeRepayedDetail();
	
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

var paybackcanpay = function(container){

	var paybackService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IPayBackService");
	var paybacks = paybackService.findBorrowerCanBeRepayedPayBacks();
	
	var aaData = new Array();
	for(var i=0; i<paybacks.size(); i++){
		var data=paybacks.get(i);
		aaData.push(["<a href='productdetail.html?pid="+data.product.id+"' target='_blank'>"+data.product.govermentOrder.title+"("+data.product.productSeries.title+")</a>",
	                    (parseFloat(data.chiefAmount.value)+parseFloat(data.interest.value)).toFixed(2),
	                    data.chiefAmount.value,
	                    data.interest.value,
	                    formatDateToDay(data.deadline),
	                    "<a class='pay' href='javascript:void(0);' id="+data.id+">还款</a>"
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
	$('a.pay').click(function(e){
		var id = $(this).attr('id');
		if(user.authorizeTypeOpen!=2){
			$('#authorize-div').modal({
				  keyboard: false,
				  backdrop: true
			});	
			return;
		}
		
		if(confirm('确认要执行本次还款？')){
		try{
			paybackService.repay(parseInt(id));
			alert('申请还款成功，请等待管理员审核！');
			window.location.href="baccountdetail.html?fid=payback&sid=payback-toaudit";
		}catch(e){
			alert(e.message);
			window.location.href="baccountdetail.html?fid=payback&sid=payback-canpay";
		}
		}
	});
}

var paybackcanapply = function(container){
	var paybackService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IPayBackService");
	var paybacks = paybackService.findBorrowerCanBeRepayedInAdvancePayBacks();
	var aaData = new Array();
	for(var i=0; i<paybacks.size(); i++){
		var data=paybacks.get(i);
		aaData.push(["<a href='productdetail.html?pid="+data.product.id+"' target='_blank'>"+data.product.govermentOrder.title+"("+data.product.productSeries.title+")</a>",
	                    (parseFloat(data.chiefAmount.value)+parseFloat(data.interest.value)).toFixed(2),
	                    data.chiefAmount.value,
	                    data.interest.value,
	                    formatDateToDay(data.deadline),
	                    "<a class='apply' onclick='apply("+data.id+")' id="+data.id+">申请提前</a>"
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
}
var paybacktoaudit = function(container){
	var paybackService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IPayBackService");
	
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
		res = paybackService.findBorrowerPayBacks(5, -1, -1, iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var datas = res.get('result');
		if(datas)
		{
			for(var i=0; i<datas.size(); i++){
				var data=datas.get(i);
				result.aaData.push(["<a href='productdetail.html?pid="+data.product.id+"' target='_blank'>"+data.product.govermentOrder.title+"("+data.product.productSeries.title+")</a>",
			                    (parseFloat(data.chiefAmount.value)+parseFloat(data.interest.value)).toFixed(2),
			                    data.chiefAmount.value,
			                    data.interest.value,
			                    formatDateToDay(data.checktime)]);
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
var paybackhave = function(container){
	var paybackService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IPayBackService");
	
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
		res = paybackService.findBorrowerPayBacks(2, -1, -1, iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var datas = res.get('result');
		if(datas)
		{
			for(var i=0; i<datas.size(); i++){
				var data=datas.get(i);
				result.aaData.push(["<a href='productdetail.html?pid="+data.product.id+"' target='_blank'>"+data.product.govermentOrder.title+"("+data.product.productSeries.title+")</a>",
			                    (parseFloat(data.chiefAmount.value)+parseFloat(data.interest.value)).toFixed(2),
			                    data.chiefAmount.value,
			                    data.interest.value,
			                    formatDateToDay(data.realtime)]);
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
	
	
	var paybackService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IPayBackService");
	var paybacks = paybackService.findBorrowerWaitForRepayed();
	
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
		res = account.findBorrowerCashStreamByActionAndState(action,state, iDisplayStart, iDisplayLength);
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


var cashfinancing = function(container){
	return cashProcessor(3,2,container);
}

var cashpayback = function(container){
	return cashProcessor(4,2,container);
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
			                    		"<button class='readletter' id='"+letters.get(i).id+"' value='阅读'>阅读</button>"
			                  //  "<a class='readletter' style='color:red;' href='javascript:void(0);' id='"+letters.get(i).id+"'>阅读</a>"
			                   ]);
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


var bnav2funtion = {
		"my-score" : myscore,
		"request-all" : requestall,
		"request-tohandle" : requesttohandle,
		"request-pass" : requestpass,
		"request-refuse" : requestrefuse,
		"payback-all" : paybackall,
		"payback-canpay" : paybackcanpay,
		"payback-canapply" : paybackcanapply,
		"payback-to" : paybackto,
		"payback-have" : paybackhave,
		"payback-toaudit" : paybacktoaudit,
		"cash-all" : cashall,
		"cash-recharge" : cashrecharge,
		"cash-withdraw" : cashwithdraw,
		"cash-financing" : cashfinancing,
		"cash-payback" : cashpayback,
		"order-all" : orderall,
		"order-preview" : orderpreview,
		"order-financing" : orderfinancing,
		"order-paying" : orderpaying,
		"order-toclose" : ordertoclose,
		"order-closed" : orderclosed,
		"order-quit" : orderquit,
		
		"question-view" : questionview,
		"letter-unread-mycenter" : letterunread_center,
		"notice-view" : noticeview
}