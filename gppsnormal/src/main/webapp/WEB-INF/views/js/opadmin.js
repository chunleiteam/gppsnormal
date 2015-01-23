
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

var borrowerstate = {
		10 : '新注册',
		11 : '申请尽调',
		12 : '尽调通过',
		14 : '尽调拒绝'
}

var cashstate = {
		0 : '充值',
		1 : '冻结',
		2 : '解冻',
		3 : '投标',
		4 : '还款',
		5 : '提现'
}
var productstate = {
		0:'未发布',
		1:'融资中',
		2:'还款中',
		4:'流标',
		8:'还款完成',
		16:'还款中', 
		32:'还款完成',
		64:'还款完成'
}
var orderstate = {
		0:'未发布',
		1: "融资中",
		2:"预发布",
		4:"还款中",
		8:"待关闭",
		16:"流标",
		32:"已关闭"
}
	
var createAdminNavLevel2 = function(nav){
	var toService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IWaitToDoStatisticsService");
	
	var ul = $('<ul class="nav nav-second nav-tabs" style="float:right;" role="tablist"></ul>');
	if(nav=='tohandle'){
		var res = toService.getStatistics();
		var tbr = res.applyBorrowerCount;
		var li1 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="tohandle-borrower-request">申请尽调企业<font color=red>('+tbr+')</font></a></li>');
		var frc = res.financingRequestCount;
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="tohandle-request">待处理融资申请<font color=red>('+frc+')</font></a></li>');
		var pre = res.prepublishOrderCount;
		var li3 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="tohandle-order-preview">待启动融资订单<font color=red>('+pre+')</font></a></li>');
		var tof = res.unCheckedOrderCount;
		var li4 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="tohandle-order-financing">待审核融资订单<font color=red>('+tof+')</font></a></li>');
		var tclo = res.waitingCloseOrderCount;
		var li5 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="tohandle-order-toclose">待关闭订单<font color=red>('+tclo+')</font></a></li>');
		var taud = res.toAuditPaybackCount;
		var li6 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="payback-toaudit">待审核还款<font color=red>('+taud+')</font></a></li>');
		ul.append(li1);
		ul.append(li2);
		ul.append(li3);
		ul.append(li4);
		ul.append(li5);
		ul.append(li6);
	}
	else if(nav=='borrower'){
		var li1 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="borrower-all">全部企业</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="borrower-new">新注册企业</a></li>');
		var li3 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="borrower-request">申请尽调企业</a></li>');
		var li4 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="borrower-pass">审核通过企业</a></li>');
		var li5 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="borrower-refuse">审核拒绝企业</a></li>');
		ul.append(li1);
		ul.append(li2);
		ul.append(li3);
		ul.append(li4);
		ul.append(li5);
	}else if(nav=='request'){
		var li1 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="request-all">全部</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="request-tohandle">待处理</a></li>');
		var li3 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="request-pass">审核通过</a></li>');
		var li4 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="request-refuse">拒绝</a></li>');
		ul.append(li1);
		ul.append(li2);
		ul.append(li3);
		ul.append(li4);
	}else if(nav=='order'){
		var li1 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="order-all">全部</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="order-unpublish">未发布</a></li>');
		var li3 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="order-preview">预览</a></li>');
		var li4 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="order-financing">融资中</a></li>');
		var li5 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="order-paying">还款中</a></li>');
		var li6 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="order-toclose">待关闭</a></li>');
		var li7 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="order-closed">已关闭</a></li>');
		var li8 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="order-quit">流标</a></li>');
		ul.append(li1);
		ul.append(li2);
		ul.append(li3);
		ul.append(li4);
		ul.append(li5);
		ul.append(li6);
		ul.append(li7);
		ul.append(li8);
	}
	else if(nav=='notice'){
		var li1 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="notice-write">发布公告</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="notice-view">公告查看</a></li>');
		ul.append(li1);
		ul.append(li2);
	}else if(nav=='news'){
		var li1 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="news-write">发布新闻</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="news-view">新闻查看</a></li>');
		ul.append(li1);
		ul.append(li2);
	}else if(nav=='help'){
		var li1 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="help-write">发布帮助</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="help-view">查看帮助</a></li>');
		var li3 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="help-question">待回答问题</a></li>');
		var li4 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="help-answered">已回答问题</a></li>');
		ul.append(li1);
		ul.append(li2);
		ul.append(li3);
		ul.append(li4);
	}
	else if(nav=='other'){
		var li1 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="lender-view">用户浏览</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="activity">活动管理</a></li>');
		var li3 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="validate-tp">系统校验</a></li>');
		var li4 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="message">短信管理</a></li>')
		ul.append(li1);
		ul.append(li2);
		ul.append(li3);
		ul.append(li4);
	}
	
	return ul;
}


var borrowerall = function(container){

	var bService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IBorrowerService");
	var letterService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ILetterService");
	var columns = [ {
		"sTitle" : "企业名称",
			"code" : "name"
	}, {
		"sTitle" : "营业执照",
		"code" : "state"
	}, {
		"sTitle" : "手机号",
		"code" : "phone"
	}, {
		"sTitle" : "申请时间",
		"code" : "requesttime"
	}, {
		"sTitle" : "状态",
		"code" : "repayed"
	}, {
		"sTitle" : "发站内信",
		"code" : "repayed"
	}];
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "iDisplayStart")
				iDisplayStart = data.value;
			if (data.name == "iDisplayLength")
				iDisplayLength = data.value;
		}
		var res = null;
		res = bService.findByPrivilegeWithPaging(-1,iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				result.aaData.push([data.companyName,
				                    data.license,
				                    data.tel,
				                    formatDate(data.createtime),
				                    borrowerstate[data.privilege],
				                    "<button id='"+data.id+"' class='sendletter'>发站内信</button>"]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		
		
		$('button.sendletter').click(function(e){
			var id = $(this).attr('id');
			
			
			var ntr = $(this).parents('tr').next('tr');
			if(ntr.prop("className")=='information'){
				ntr.remove();
				return;
			}
			
			var letterdiv1 = $('<div class="row" style="max-width:500px;"></div>');
			letterdiv1.append('<div class="col-md-3">站内信标题</div>');
			letterdiv1.append('<div class="col-md-9"><input type="text" id="lettertitle" style="width:100%"></input></div>');
			var letterdiv2 = $('<div class="row" style="max-width:500px;"></div>');
			letterdiv2.append('<div class="col-md-3">站内信内容</div>');
			letterdiv2.append('<div class="col-md-9"><textarea id="lettercontent" style="width:100%; min-height:300px;"></textarea></div>');
			var letterdiv3 = $('<div class="row" style="max-width:500px;"></div>');
			var button = $('<button style="width:100%;">发送</button>');
			letterdiv3.append(button);
			button.click(function(){
				
				var title = $(this).parent().parent().find('input#lettertitle').val();
				var content = $(this).parent().parent().find('textarea#lettercontent').val();
				if(title==null || title==''){
					alert('站内信标题不能为空！');
					return;
				}else if(content==null || content==''){
					alert('站内信内容不能为空！');
					return;
				}
				try{
					var letter = {'_t_':'gpps.model.Letter', 'title':title, 'content':content, 'markRead':0, 'receiverId':parseInt(id), 'receivertype':1}
					letterService.create(letter);
					alert("站内信已成功发送");
					var fftr = $(this).parents('tr');
					fftr.remove();
				}catch(e){
					alert(e.message);
				}
				
			})
			
			
			var ftr = $('<tr class="information"></tr>');
			var ftd = $('<td align=center colspan=6></td>');
			ftr.append(ftd);
			ftd.append(letterdiv1);
			ftd.append(letterdiv2);
			ftd.append(letterdiv3);
			
			$(this).parents('tr').after(ftr);
		});

		return res;
	}
	var mySettings = $.extend({}, defaultSettings, {
		"aoColumns" : columns,
		"fnServerData" : fnServerData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
}





var borrowernew = function(container){

	var bService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IBorrowerService");
		var borrowers = bService.findFinancingBorrower(10);
		
		
		var columns = [ {
			"sTitle" : "企业名称",
				"code" : "name"
		}, {
			"sTitle" : "营业执照",
			"code" : "state"
		}, {
			"sTitle" : "手机号",
			"code" : "phone"
		}, {
			"sTitle" : "申请时间",
			"code" : "requesttime"
		}, {
			"sTitle" : "状态",
			"code" : "repayed"
		}];
		var aaData = new Array();
		for(var i=0; i<borrowers.size(); i++){
			var data=borrowers.get(i);
			aaData.push([data.companyName,
			                    data.license,
			                    data.tel,
			                    formatDate(data.createtime),
			                    borrowerstate[data.privilege]]);
		}
		var mySettings = $.extend({}, defaultSettings_noCallBack, {
			"aoColumns" : columns,
			"aaData" : aaData
		});
		var content = $('<div></div>');
		var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
		container.append(content);
		table.dataTable(mySettings);
}

var borrowerrequest = function(container){
	var bService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IBorrowerService");
		var borrowers = bService.findFinancingBorrower(11);
		
		
		var columns = [ {
			"sTitle" : "企业名称",
				"code" : "name"
		}, {
			"sTitle" : "营业执照",
			"code" : "state"
		}, {
			"sTitle" : "手机号",
			"code" : "phone"
		}, {
			"sTitle" : "申请时间",
			"code" : "requesttime"
		}, {
			"sTitle" : "状态",
			"code" : "repayed"
		}, {
			"sTitle" : "编辑",
			"code" : "编辑"
		}, {
			"sTitle" : "操作",
			"code" : "operate"
		}];
		var aaData = new Array();
		for(var i=0; i<borrowers.size(); i++){
			var data=borrowers.get(i);
			aaData.push([data.companyName,
			                    data.license,
			                    data.tel,
			                    formatDate(data.createtime),
			                    borrowerstate[data.privilege],
			                    '<button id='+data.id+' class="borroweredit">编辑</button>',
			                    '<button id='+data.id+' class="borrowerpass">通过</button><button id='+data.id+' class="borrowerrefuse">拒绝</button>']);
		}
		var mySettings = $.extend({}, defaultSettings_noCallBack, {
			"aoColumns" : columns,
			"aaData" : aaData
		});
		var content = $('<div></div>');
		var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
		container.append(content);
		table.dataTable(mySettings);
		
		$('button.borroweredit').click(function(e){
			var id = $(this).attr('id');
			window.open("borroweredit.html?id="+id);
		});
		
		$('button.borrowerpass').click(function(e){
			try{
			bService.passFundingApplying(parseInt($(this).attr('id')));
			window.location.href="opadmin.html?fid=borrower&sid=borrower-pass";
			}catch(e){
				alert(e.message);
			}
		});
		
		$('button.borrowerrefuse').click(function(e){
			try{
			bService.refuseFundingApplying(parseInt($(this).attr('id')));
			window.location.href="opadmin.html?fid=borrower&sid=borrower-refuse";
			}catch(e){
				alert(e.message);
			}
		});
}

var borrowerpass = function(container){
	var bService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IBorrowerService");
		var borrowers = bService.findFinancingBorrower(12);
		
		
		var columns = [ {
			"sTitle" : "企业名称",
				"code" : "name"
		}, {
			"sTitle" : "营业执照",
			"code" : "state"
		}, {
			"sTitle" : "手机号",
			"code" : "phone"
		}, {
			"sTitle" : "申请时间",
			"code" : "requesttime"
		}, {
			"sTitle" : "状态",
			"code" : "repayed"
		}, {
			"sTitle" : "编辑",
			"code" : "编辑"
		}];
		var aaData = new Array();
		for(var i=0; i<borrowers.size(); i++){
			var data=borrowers.get(i);
			aaData.push([data.companyName,
			                    data.license,
			                    data.tel,
			                    formatDate(data.createtime),
			                    borrowerstate[data.privilege],
			                    '<button id='+data.id+' class="borroweredit">编辑</button>']);
		}
		var mySettings = $.extend({}, defaultSettings_noCallBack, {
			"aoColumns" : columns,
			"aaData" : aaData
		});
		var content = $('<div></div>');
		var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
		container.append(content);
		table.dataTable(mySettings);
		
		$('button.borroweredit').click(function(e){
			var id = $(this).attr('id');
			window.open("borroweredit.html?id="+id);
		});
}

var borrowerrefuse = function(container){
	var bService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IBorrowerService");
		var borrowers = bService.findFinancingBorrower(14);
		
		
		var columns = [ {
			"sTitle" : "企业名称",
				"code" : "name"
		}, {
			"sTitle" : "营业执照",
			"code" : "state"
		}, {
			"sTitle" : "手机号",
			"code" : "phone"
		}, {
			"sTitle" : "申请时间",
			"code" : "requesttime"
		}, {
			"sTitle" : "状态",
			"code" : "repayed"
		}];
		var aaData = new Array();
		for(var i=0; i<borrowers.size(); i++){
			var data=borrowers.get(i);
			aaData.push([data.companyName,
			                    data.license,
			                    data.tel,
			                    formatDate(data.createtime),
			                    borrowerstate[data.privilege]]);
		}
		var mySettings = $.extend({}, defaultSettings_noCallBack, {
			"aoColumns" : columns,
			"aaData" : aaData
		});
		var content = $('<div></div>');
		var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
		container.append(content);
		table.dataTable(mySettings);
}

var lenderview = function(container){
	var lenderService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ILenderService");
	var letterService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.ILetterService");
	var inviteService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IInviteService");
	var columns = [ {
		"sTitle" : "姓名",
			"code" : "name"
	}, {
		"sTitle" : "电话",
		"code" : "tel"
	}, {
		"sTitle" : "身份证",
		"code" : "identityCard"
	}, {
		"sTitle" : "级别",
		"code" : "level"
	}, {
		"sTitle" : "汇付账户",
		"code" : "thirdPartyAccount"
	}, {
		"sTitle" : "发站内信",
		"code" : "sendletter"
	}, {
		"sTitle" : "发邀请码",
		"code" : "sendinvite"
	}];
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "iDisplayStart")
				iDisplayStart = data.value;
			if (data.name == "iDisplayLength")
				iDisplayLength = data.value;
		}
		var res = null;
		res = lenderService.findByPrivilegeWithPaging(-1,iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				result.aaData.push([data.name==null?"":data.name,
				                    data.tel,
				                    data.identityCard==null?"":data.name,
				                    data.level,
				                    data.thirdPartyAccount==null?"":data.thirdPartyAccount,
				                    "<button id='"+data.id+"' class='sendletter'>发站内信</button>",
				                    "<button id='"+data.id+"' class='sendinvite'>发邀请码</button>"]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		
		$('button.sendinvite').click(function(e){
			var uid = $(this).attr('id');
			try{
			inviteService.allocate(parseInt(uid), 5);
			alert('邀请码分配成功！');
			}catch(e){
				alert(e.message);
			}
		});
		
		$('button.sendletter').click(function(e){
			var id = $(this).attr('id');
			
			
			var ntr = $(this).parents('tr').next('tr');
			if(ntr.prop("className")=='information'){
				ntr.remove();
				return;
			}
			
			var letterdiv1 = $('<div class="row" style="max-width:500px;"></div>');
			letterdiv1.append('<div class="col-md-3">站内信标题</div>');
			letterdiv1.append('<div class="col-md-9"><input type="text" id="lettertitle" style="width:100%"></input></div>');
			var letterdiv2 = $('<div class="row" style="max-width:500px;"></div>');
			letterdiv2.append('<div class="col-md-3">站内信内容</div>');
			letterdiv2.append('<div class="col-md-9"><textarea id="lettercontent" style="width:100%; min-height:300px;"></textarea></div>');
			var letterdiv3 = $('<div class="row" style="max-width:500px;"></div>');
			var button = $('<button style="width:100%;">发送</button>');
			letterdiv3.append(button);
			button.click(function(){
				
				var title = $(this).parent().parent().find('input#lettertitle').val();
				var content = $(this).parent().parent().find('textarea#lettercontent').val();
				if(title==null || title==''){
					alert('站内信标题不能为空！');
					return;
				}else if(content==null || content==''){
					alert('站内信内容不能为空！');
					return;
				}
				try{
					var letter = {'_t_':'gpps.model.Letter', 'title':title, 'content':content, 'markRead':0, 'receiverId':parseInt(id), 'receivertype':0}
					letterService.create(letter);
					alert("站内信已成功发送");
					var fftr = $(this).parents('tr');
					fftr.remove();
				}catch(e){
					alert(e.message);
				}
				
			})
			
			
			var ftr = $('<tr class="information"></tr>');
			var ftd = $('<td align=center colspan=6></td>');
			ftr.append(ftd);
			ftd.append(letterdiv1);
			ftd.append(letterdiv2);
			ftd.append(letterdiv3);
			
			$(this).parents('tr').after(ftr);
		});

		return res;
	}
	var mySettings = $.extend({}, defaultSettings, {
		"aoColumns" : columns,
		"fnServerData" : fnServerData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
	
	
}

var handlerequest = function(state, container){

	var bService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IBorrowerService");
	var columns = [ {
		"sTitle" : "订单名称",
			"code" : "name"
	},{
		"sTitle" : "融资企业",
		"code" : "company"
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
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "iDisplayStart")
				iDisplayStart = data.value;
			if (data.name == "iDisplayLength")
				iDisplayLength = data.value;
		}
		var res = null;
		res = bService.findAllFinancingRequests(state,iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				var str = '待审核';
				if(data.state==1){
					str = '审核通过';
				}else if(data.state==2){
					str = '审核未通过';
				}
				result.aaData.push([data.govermentOrderName,
				                    data.borrower.companyName,
				                    data.applyFinancingAmount,
				                    data.rate,
				                    formatDate(data.createtime),
				                    str]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);

		return res;
	}
	var mySettings = $.extend({}, defaultSettings, {
		"aoColumns" : columns,
		"fnServerData" : fnServerData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
}


var requestall = function(container){
	handlerequest(-1, container);
}
var requesttohandle = function(container){
	var bService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IBorrowerService");
	var columns = [ {
		"sTitle" : "订单名称",
			"code" : "name"
	}, {
		"sTitle" : "融资企业",
		"code" : "company"
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
	}, {
		"sTitle" : "编辑",
		"code" : "edit"
	}, {
		"sTitle" : "操作",
		"code" : "operation"
	}];
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "iDisplayStart")
				iDisplayStart = data.value;
			if (data.name == "iDisplayLength")
				iDisplayLength = data.value;
		}
		var res = null;
		res = bService.findAllFinancingRequests(0,iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				var str = '待审核';
				if(data.state==1){
					str = '审核通过';
				}else if(data.state==2){
					str = '审核未通过';
				}
				result.aaData.push([data.govermentOrderName,
				                    data.borrower.companyName,
				                    data.applyFinancingAmount,
				                    data.rate,
				                    formatDate(data.createtime),
				                    str,
				                    '<button id='+data.id+' class="requestedit">编辑</button>',
				                    '<button id='+data.id+' class="requestpass">通过</button><button id='+data.id+' class="requestrefuse">拒绝</button>']);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);

		return res;
	}
	var mySettings = $.extend({}, defaultSettings, {
		"aoColumns" : columns,
		"fnServerData" : fnServerData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
	
	$('button.requestedit').click(function(e){
		window.open('requestedit.html?id='+$(this).attr('id'));
	});
	$('button.requestpass').click(function(e){
		var rid = $(this).attr('id');
		try{
		bService.passFinancingRequest(parseInt(rid));
		window.location.href='opadmin.html?fid=request&sid=request-pass';
		}catch(e){
			alert(e.message);
		}
	});
	
	$('button.requestrefuse').click(function(e){
		var rid = $(this).attr('id');
		try{
		bService.refuseFinancingRequest(parseInt(rid));
		window.location.href='opadmin.html?fid=request&sid=request-refuse';
		}catch(e){
			alert(e.message);
		}
	});
	
}

var requestpass = function(container){
	handlerequest(1, container);
}

var requestrefuse = function(container){
	handlerequest(2, container);
}


var ordershow = function(state, container){
	var orderService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IGovermentOrderService");
	var orderDao = EasyServiceClient.getRemoteProxy("/easyservice/gpps.dao.IGovermentOrderDao");
	var productService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IProductService");
	var columns = [ {
		"sTitle" : "订单名称",
			"code" : "name"
	}, {
		"sTitle" : "融资起始时间",
		"code" : "state"
	}, {
		"sTitle" : "融资截止时间",
		"code" : "financingEndtime"
	}, {
		"sTitle" : "预期金额",
		"code" : "expect"
	}, {
		"sTitle" : "已融金额",
		"code" : "real"
	}, {
		"sTitle" : "状态",
		"code" : "repayed"
	}, {
		"sTitle" : "详情",
		"code" : "view"
	}];
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "iDisplayStart")
				iDisplayStart = data.value;
			if (data.name == "iDisplayLength")
				iDisplayLength = data.value;
		}
		var res = null;
		res = orderService.findByStatesByPage(state,iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				var products = data.products;
				var totalamount = 0;
				var real = 0;
				for(var j=0; j<products.size(); j++){
					totalamount += parseInt(products.get(j).expectAmount.value);
					real += parseInt(products.get(j).realAmount.value);
				}
				result.aaData.push([data.title,
				             formatDate(data.financingStarttime),
				             formatDate(data.financingEndtime),
				                    totalamount,
				                    real,
				                    orderstate[data.state],
				                    "<button class='vieworder' id='"+data.id+"'>查看</button>"]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		
		
		
		
		$('button.vieworder').click(function(e){
			var ntr = $(this).parents('tr').next('tr');
			if(ntr.prop("className")=='information'){
				$(this).parents('tr').removeAttr("style");
				ntr.remove();
				return;
			}
			
			$(this).parents('tr').css('backgroundColor', 'orange');
			
			var orderid = parseInt($(this).attr('id'));
			var order = orderDao.find(orderid);
			var products = productService.findByGovermentOrder(orderid);
			
		
			var table = $('<table class="ui-list-invest" id="products" style="width:95%"></table>');
			var tr = $('<tr id="header" style="padding-left:0px; padding-right:0px;"></tr>');
			tr.append('<td class="color-gray-text text-center">产品类型</td>');
			tr.append('<td class="color-gray-text text-center">年利率</td>');
			tr.append('<td class="color-gray-text text-center">预期金额</td>');
			tr.append('<td class="color-gray-text text-center">已融金额</td>');
			tr.append('<td class="color-gray-text text-center">期限</td>');
			tr.append('<td class="color-gray-text text-center">进度</td>');
			tr.append('<td class="color-gray-text text-center">状态</td>');
			table.append('<tr><td class="color-gray-text text-center" colspan=7>'+order.description+'</td></tr>');
			table.append(tr);
			
	    	   for(var i=0; i<products.size(); i++){
	    		   var product = products.get(i);
	    		   product.govermentOrder = order;
	    		   table.append(createSingleSubProduct(product));
	    	   }
			
			
			var ftr = $('<tr class="information"></tr>');
			var ftd = $('<td colspan=7 align=center></td>');
			ftr.append(ftd);
			ftd.append(table);
			
			
			$(this).parents('tr').after(ftr);
			
		});
		
		
		

		return res;
	}
	var mySettings = $.extend({}, defaultSettings, {
		"aoColumns" : columns,
		"fnServerData" : fnServerData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
}


var orderall = function(container){
	ordershow(-1, container)
}

var orderunpublish = function(container){

	var orderService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IGovermentOrderService");
	var orderDao = EasyServiceClient.getRemoteProxy("/easyservice/gpps.dao.IGovermentOrderDao");
	var productService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IProductService");
	var orders = orderService.findAllUnpublishOrders();
	
	var columns = [ {
		"sTitle" : "订单名称",
			"code" : "name"
	}, {
		"sTitle" : "融资起始时间",
		"code" : "state"
	}, {
		"sTitle" : "融资截止时间",
		"code" : "financingEndtime"
	}, {
		"sTitle" : "预期金额",
		"code" : "expect"
	}, {
		"sTitle" : "已融金额",
		"code" : "real"
	}, {
		"sTitle" : "状态",
		"code" : "repayed"
	}, {
		"sTitle" : "详情",
		"code" : "view"
	}];
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
		                    "<button class='vieworder' id='"+data.id+"'>查看</button>"]);
	}
	var mySettings = $.extend({}, defaultSettings_noCallBack, {
		"aoColumns" : columns,
		"aaData" : aaData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
	
	$('button.vieworder').click(function(e){
		var ntr = $(this).parents('tr').next('tr');
		if(ntr.prop("className")=='information'){
			$(this).parents('tr').removeAttr("style");
			ntr.remove();
			return;
		}
		
		$(this).parents('tr').css('backgroundColor', 'orange');
		
		var orderid = parseInt($(this).attr('id'));
		var order = orderDao.find(orderid);
		var products = productService.findByGovermentOrder(orderid);
		
	
		var table = $('<table class="ui-list-invest" id="products" style="width:95%"></table>');
		var tr = $('<tr id="header" style="padding-left:0px; padding-right:0px;"></tr>');
		tr.append('<td class="color-gray-text text-center">产品类型</td>');
		tr.append('<td class="color-gray-text text-center">年利率</td>');
		tr.append('<td class="color-gray-text text-center">预期金额</td>');
		tr.append('<td class="color-gray-text text-center">已融金额</td>');
		tr.append('<td class="color-gray-text text-center">期限</td>');
		tr.append('<td class="color-gray-text text-center">进度</td>');
		tr.append('<td class="color-gray-text text-center">状态</td>');
		table.append('<tr><td class="color-gray-text text-center" colspan=7>'+order.description+'</td></tr>');
		table.append(tr);
		
    	   for(var i=0; i<products.size(); i++){
    		   var product = products.get(i);
    		   product.govermentOrder = order;
    		   table.append(createUnpublishProduct(product));
    	   }
		
		
		var ftr = $('<tr class="information"></tr>');
		var ftd = $('<td colspan=7 align=center></td>');
		ftr.append(ftd);
		ftd.append(table);
		
		
		$(this).parents('tr').after(ftr);
		
	});
}

var orderpreview = function(container){

	var orderService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IGovermentOrderService");
	var orderDao = EasyServiceClient.getRemoteProxy("/easyservice/gpps.dao.IGovermentOrderDao");
	var productService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IProductService");
	var columns = [ {
		"sTitle" : "订单名称",
			"code" : "name"
	}, {
		"sTitle" : "融资起始时间",
		"code" : "state"
	}, {
		"sTitle" : "融资截止时间",
		"code" : "financingEndtime"
	}, {
		"sTitle" : "预期金额",
		"code" : "expect"
	}, {
		"sTitle" : "已融金额",
		"code" : "real"
	}, {
		"sTitle" : "状态",
		"code" : "repayed"
	}, {
		"sTitle" : "详情",
		"code" : "view"
	}, {
		"sTitle" : "操作",
		"code" : "operate"
	}];
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "iDisplayStart")
				iDisplayStart = data.value;
			if (data.name == "iDisplayLength")
				iDisplayLength = data.value;
		}
		var res = null;
		res = orderService.findByStatesByPage(2,iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				var products = data.products;
				var totalamount = 0;
				var real = 0;
				for(var j=0; j<products.size(); j++){
					totalamount += parseInt(products.get(j).expectAmount.value);
					real += parseInt(products.get(j).realAmount.value);
				}
				result.aaData.push([data.title,
				             formatDate(data.financingStarttime),
				             formatDate(data.financingEndtime),
				                    totalamount,
				                    real,
				                    orderstate[data.state],
				                    "<button class='vieworder' id='"+data.id+"'>查看</button>",
				                    "<button class='startfinancing' id='"+data.id+"'>开始融资</button>"]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		
		
		$('button.startfinancing').click(function(e){
			var orderid = parseInt($(this).attr('id'));
			try{
			orderService.startFinancing(orderid);
			window.location.href="opadmin.html?fid=order&sid=order-financing";
			}catch(e){
				alert(e.message);
			}
		});
		
		$('button.vieworder').click(function(e){
			var ntr = $(this).parents('tr').next('tr');
			if(ntr.prop("className")=='information'){
				$(this).parents('tr').removeAttr("style");
				ntr.remove();
				return;
			}
			
			$(this).parents('tr').css('backgroundColor', 'orange');
			
			var orderid = parseInt($(this).attr('id'));
			var order = orderDao.find(orderid);
			var products = productService.findByGovermentOrder(orderid);
			
		
			var table = $('<table class="ui-list-invest" id="products" style="width:95%"></table>');
			var tr = $('<tr id="header" style="padding-left:0px; padding-right:0px;"></tr>');
			tr.append('<td class="color-gray-text text-center">产品类型</td>');
			tr.append('<td class="color-gray-text text-center">年利率</td>');
			tr.append('<td class="color-gray-text text-center">预期金额</td>');
			tr.append('<td class="color-gray-text text-center">已融金额</td>');
			tr.append('<td class="color-gray-text text-center">期限</td>');
			tr.append('<td class="color-gray-text text-center">进度</td>');
			tr.append('<td class="color-gray-text text-center">状态</td>');
			table.append('<tr><td class="color-gray-text text-center" colspan=7>'+order.description+'</td></tr>');
			table.append(tr);
			
	    	   for(var i=0; i<products.size(); i++){
	    		   var product = products.get(i);
	    		   product.govermentOrder = order;
	    		   table.append(createSingleSubProduct(product));
	    	   }
			
			
			var ftr = $('<tr class="information"></tr>');
			var ftd = $('<td colspan=7 align=center></td>');
			ftr.append(ftd);
			ftd.append(table);
			
			
			$(this).parents('tr').after(ftr);
			
		});
		
		
		

		return res;
	}
	var mySettings = $.extend({}, defaultSettings, {
		"aoColumns" : columns,
		"fnServerData" : fnServerData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
}

var orderfinancing = function(container){
	var orderService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IGovermentOrderService");
	var orderDao = EasyServiceClient.getRemoteProxy("/easyservice/gpps.dao.IGovermentOrderDao");
	var productService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IProductService");
	var columns = [ {
		"sTitle" : "订单名称",
			"code" : "name"
	}, {
		"sTitle" : "融资起始时间",
		"code" : "state"
	}, {
		"sTitle" : "融资截止时间",
		"code" : "financingEndtime"
	}, {
		"sTitle" : "预期金额",
		"code" : "expect"
	}, {
		"sTitle" : "已融金额",
		"code" : "real"
	}, {
		"sTitle" : "状态",
		"code" : "repayed"
	}, {
		"sTitle" : "详情",
		"code" : "view"
	}, {
		"sTitle" : "操作",
		"code" : "operate"
	}];
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "iDisplayStart")
				iDisplayStart = data.value;
			if (data.name == "iDisplayLength")
				iDisplayLength = data.value;
		}
		var res = null;
		res = orderService.findByStatesByPage(1,iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				var products = data.products;
				var totalamount = 0;
				var real = 0;
				for(var j=0; j<products.size(); j++){
					totalamount += parseInt(products.get(j).expectAmount.value);
					real += parseInt(products.get(j).realAmount.value);
				}
				result.aaData.push([data.title,
				             formatDate(data.financingStarttime),
				             formatDate(data.financingEndtime),
				                    totalamount,
				                    real,
				                    orderstate[data.state],
				                    "<button class='vieworder' id='"+data.id+"'>查看</button>",
				                    "<button class='startpay' id='"+data.id+"'>开始还款</button><button class='quit' id='"+data.id+"'>流标</button>"]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		
		
		$('button.startpay').click(function(e){
			var orderid = parseInt($(this).attr('id'));
//			var products = productService.findByGovermentOrder(orderid);
//			for(var i=0; i<products.size(); i++){
//				productService.startRepaying(products.get(i).id);
//			}
			if(confirm('您确认融资完成，要启动还款吗？'))
				{
			try{
			orderService.startRepaying(orderid);
			window.location.href="opadmin.html?fid=order&sid=order-paying";
			}catch(e){
				alert(e.message);
			}
				}
		});
		
		$('button.quit').click(function(e){
			var orderid = parseInt($(this).attr('id'));
			if(confirm('您确认融资未完成，要执行流标吗？'))
			{
			try{
			orderService.quitFinancing(orderid);
			window.location.href="opadmin.html?fid=order&sid=order-quit";
			}catch(e){
				alert(e.message);
			}
			}
		});
		
		$('button.vieworder').click(function(e){
			var ntr = $(this).parents('tr').next('tr');
			if(ntr.prop("className")=='information'){
				$(this).parents('tr').removeAttr("style");
				ntr.remove();
				return;
			}
			
			$(this).parents('tr').css('backgroundColor', 'orange');
			
			var orderid = parseInt($(this).attr('id'));
			var order = orderDao.find(orderid);
			var products = productService.findByGovermentOrder(orderid);
			
		
			var table = $('<table class="ui-list-invest" id="products" style="width:95%"></table>');
			var tr = $('<tr id="header" style="padding-left:0px; padding-right:0px;"></tr>');
			tr.append('<td class="color-gray-text text-center">产品类型</td>');
			tr.append('<td class="color-gray-text text-center">年利率</td>');
			tr.append('<td class="color-gray-text text-center">预期金额</td>');
			tr.append('<td class="color-gray-text text-center">已融金额</td>');
			tr.append('<td class="color-gray-text text-center">期限</td>');
			tr.append('<td class="color-gray-text text-center">进度</td>');
			tr.append('<td class="color-gray-text text-center">状态</td>');
			tr.append('<td class="color-gray-text text-center">合同</td>');
			table.append('<tr><td class="color-gray-text text-center" colspan=8>'+order.description+'</td></tr>');
			table.append(tr);
			
	    	   for(var i=0; i<products.size(); i++){
	    		   var product = products.get(i);
	    		   product.govermentOrder = order;
	    		   table.append(createSingleSubProductForFinancing(product));
	    	   }
			
			
			var ftr = $('<tr class="information"></tr>');
			var ftd = $('<td colspan=7 align=center></td>');
			ftr.append(ftd);
			ftd.append(table);
			
			
			$(this).parents('tr').after(ftr);
			
		});
		
		
		

		return res;
	}
	var mySettings = $.extend({}, defaultSettings, {
		"aoColumns" : columns,
		"fnServerData" : fnServerData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
}

var orderpaying = function(container){
	ordershow(4, container)
}


var paybacktoaudit = function(container){

	var paybackService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IPayBackService");
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
		"sTitle" : "还款时间",
		"code" : "time"
	},{
		"sTitle" : "审核",
		"code" : "check"
	},{
		"sTitle" : "操作",
		"code" : "audit"
	}
	];
	var datas = null;
	datas = paybackService.findWaitforCheckPayBacks();
	var aaData = new Array();
	for(var i=0; i<datas.size(); i++){
		var data=datas.get(i);
		var ischecked = data.checkResult;
		var checkedstr = null;
		var auditstr = null;
		if(ischecked==0){
			checkedstr = "<button class='check' id='"+data.id+"'>审核</button>";
			auditstr = "<button class='audit' id='"+data.id+"' disabled='disabled'>执行还款</button>";
		}else if(ischecked==1){
			checkedstr = "审核通过";
			auditstr = "<button class='audit' id='"+data.id+"'>执行还款</button>";
		}else if(ischecked==2){
			checkedstr = "审核未通过";
			auditstr = "<button class='audit' id='"+data.id+"' disabled='disabled'>执行还款</button>";
		}
		aaData.push(["<a href='productdetail.html?pid="+data.product.id+"' target='_blank'>"+data.product.govermentOrder.title+"("+data.product.productSeries.title+")</a>",
	                    (parseFloat(data.chiefAmount.value)+parseFloat(data.interest.value)).toFixed(2),
	                    data.chiefAmount.value,
	                    data.interest.value,
	                    formatDateToDay(data.deadline),
	                    checkedstr,
	                    auditstr]);
	}
	var mySettings = $.extend({}, defaultSettings_noCallBack, {
		"aoColumns" : columns,
		"aaData" : aaData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
	
	$('button.check').click(function(e){
		var paybackid = parseInt($(this).attr('id'));
		try{
			var res = paybackService.checkoutPayBack(paybackid);
			var table = $('<table width="100%"></table>');
			table.append('<tr><th>投资者姓名</th><th>还款本金</th><th>还款利息</th><th>投资金额</th></tr>')
			for(var i=0; i<res.size(); i++){
				var spb = res.get(i);
				table.append('<tr><td>'+spb.toname+'</td><td>'+spb.chief.value+'</td><td>'+spb.interest.value+'</td><td>'+spb.submitAmount.value+'</td></tr>');
			}
			$('#paybackDetail').html('');
			$('#paybackDetail').append(table);
			$('#paybackDetailDialog').modal({
				  keyboard: false,
				  backdrop: true
			});	
//			window.location.href="opadmin.html?fid=tohandle&sid=payback-toaudit";
		}catch(e){
			alert(e.message);
		}
	});
	
	$('button.audit').click(function(e){
		if(confirm('确认还款无误，审核通过？')){
			var paybackid = parseInt($(this).attr('id'));
			try{
			paybackService.check(paybackid);
			alert('还款审核通过，开始执行还款！');
			window.location.href="opadmin.html?fid=tohandle&sid=payback-toaudit";
			}catch(e){
				alert(e.message);
			}
		}
	});
}


var ordertoclose = function(container){
	var orderService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IGovermentOrderService");
	var orderDao = EasyServiceClient.getRemoteProxy("/easyservice/gpps.dao.IGovermentOrderDao");
	var productService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IProductService");
	var columns = [ {
		"sTitle" : "订单名称",
			"code" : "name"
	}, {
		"sTitle" : "融资起始时间",
		"code" : "state"
	}, {
		"sTitle" : "融资截止时间",
		"code" : "financingEndtime"
	}, {
		"sTitle" : "预期金额",
		"code" : "expect"
	}, {
		"sTitle" : "已融金额",
		"code" : "real"
	}, {
		"sTitle" : "状态",
		"code" : "repayed"
	}, {
		"sTitle" : "详情",
		"code" : "view"
	}, {
		"sTitle" : "操作",
		"code" : "operate"
	}];
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "iDisplayStart")
				iDisplayStart = data.value;
			if (data.name == "iDisplayLength")
				iDisplayLength = data.value;
		}
		var res = null;
		res = orderService.findByStatesByPage(8,iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				var products = data.products;
				var totalamount = 0;
				var real = 0;
				for(var j=0; j<products.size(); j++){
					totalamount += parseInt(products.get(j).expectAmount.value);
					real += parseInt(products.get(j).realAmount.value);
				}
				result.aaData.push([data.title,
				             formatDate(data.financingStarttime),
				             formatDate(data.financingEndtime),
				                    totalamount,
				                    real,
				                    orderstate[data.state],
				                    "<button class='vieworder' id='"+data.id+"'>查看</button>",
				                    "<button class='closeorder' id='"+data.id+"'>关闭</button>"]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		
		
		$('button.closeorder').click(function(e){
			var orderid = parseInt($(this).attr('id'));
			orderService.closeComplete(orderid);
			window.location.href="opadmin.html?fid=order&sid=order-closed";
		});
		
		
		$('button.vieworder').click(function(e){
			var ntr = $(this).parents('tr').next('tr');
			if(ntr.prop("className")=='information'){
				$(this).parents('tr').removeAttr("style");
				ntr.remove();
				return;
			}
			
			$(this).parents('tr').css('backgroundColor', 'orange');
			
			var orderid = parseInt($(this).attr('id'));
			var order = orderDao.find(orderid);
			var products = productService.findByGovermentOrder(orderid);
			
		
			var table = $('<table class="ui-list-invest" id="products" style="width:95%"></table>');
			var tr = $('<tr id="header" style="padding-left:0px; padding-right:0px;"></tr>');
			tr.append('<td class="color-gray-text text-center">产品类型</td>');
			tr.append('<td class="color-gray-text text-center">年利率</td>');
			tr.append('<td class="color-gray-text text-center">预期金额</td>');
			tr.append('<td class="color-gray-text text-center">已融金额</td>');
			tr.append('<td class="color-gray-text text-center">期限</td>');
			tr.append('<td class="color-gray-text text-center">进度</td>');
			tr.append('<td class="color-gray-text text-center">状态</td>');
			table.append('<tr><td class="color-gray-text text-center" colspan=7>'+order.description+'</td></tr>');
			table.append(tr);
			
	    	   for(var i=0; i<products.size(); i++){
	    		   var product = products.get(i);
	    		   product.govermentOrder = order;
	    		   table.append(createSingleSubProduct(product));
	    	   }
			
			
			var ftr = $('<tr class="information"></tr>');
			var ftd = $('<td colspan=7 align=center></td>');
			ftr.append(ftd);
			ftd.append(table);
			
			
			$(this).parents('tr').after(ftr);
			
		});
		
		
		

		return res;
	}
	var mySettings = $.extend({}, defaultSettings, {
		"aoColumns" : columns,
		"fnServerData" : fnServerData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
}

var orderclosed = function(container){
	ordershow(32, container)
}

var orderquit = function(container){
	ordershow(16, container)
}

var newsview = function(container){
	var newsservice = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.INewsService");
	var columns = [ {
		"sTitle" : "新闻标题",
			"code" : "name"
	},{
		"sTitle" : "新闻类型",
		"code" : "category"
	}, {
		"sTitle" : "发布时间",
		"code" : "state"
	}, {
		"sTitle" : "操作",
		"code" : "operate"
	}];
	
	var categoryType = {0:'行业资讯',1:'财经新闻'};
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "iDisplayStart")
				iDisplayStart = data.value;
			if (data.name == "iDisplayLength")
				iDisplayLength = data.value;
		}
		var res = null;
		res = newsservice.findAll(-1, iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				result.aaData.push([data.title,
				                    categoryType[data.publicType],
				             formatDate(data.publishtime),
				                    "<button class='viewnews' id='"+data.id+"'>查看</button>"]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		
		$('button.viewnews').click(function(e){
			var newsid = $(this).attr('id');
			var news = newsservice.find(parseInt(newsid));
			$('#nlabel').html(news.title);
			$('#ndetail').html(news.content);
			$('#noticedetail').modal({
				  keyboard: false,
				  backdrop: true
			});
		})

		return res;
	}
	var mySettings = $.extend({}, defaultSettings, {
		"aoColumns" : columns,
		"fnServerData" : fnServerData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
	
}

var helpquestion = function(container){
	var helpservice = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IHelpService");
	var columns = [ {
		"sTitle" : "提问问题",
			"code" : "question"
	}, {
		"sTitle" : "提问时间",
		"code" : "time"
	}, {
		"sTitle" : "提问者类型",
		"code" : "time"
	}, {
		"sTitle" : "提问者ID",
		"code" : "time"
	}
	, {
		"sTitle" : "操作",
		"code" : "operate"
	}];
	
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "iDisplayStart")
				iDisplayStart = data.value;
			if (data.name == "iDisplayLength")
				iDisplayLength = data.value;
		}
		var res = null;
		res = helpservice.findPrivateHelps(1, iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				result.aaData.push([data.question,
				             formatDate(data.createtime),
				             data.questionerType,
				             data.questionerId,
				                    "<button class='answerphelp' id='"+data.id+"'>回答</button>"]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		
		$('button.answerphelp').click(function(e){
			var helpid = $(this).attr('id');
			
			var help = helpservice.find(parseInt(helpid));
			$('#alabel').html(help.question);
			
			$('button#answer').attr('data-sk', helpid);
			
			$('#answerdetail').modal({
				  keyboard: false,
				  backdrop: true
			});
		})

		return res;
	}
	var mySettings = $.extend({}, defaultSettings, {
		"aoColumns" : columns,
		"fnServerData" : fnServerData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
	
}

var helpanswered = function(container){
	var helpservice = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IHelpService");
	var columns = [ {
		"sTitle" : "提问问题",
			"code" : "question"
	}, {
		"sTitle" : "提问时间",
		"code" : "time"
	}, {
		"sTitle" : "提问者类型",
		"code" : "time"
	}, {
		"sTitle" : "提问者ID",
		"code" : "time"
	}
	, {
		"sTitle" : "操作",
		"code" : "operate"
	}];
	
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "iDisplayStart")
				iDisplayStart = data.value;
			if (data.name == "iDisplayLength")
				iDisplayLength = data.value;
		}
		var res = null;
		res = helpservice.findPrivateHelps(2, iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				result.aaData.push([data.question,
				             formatDate(data.createtime),
				             data.questionerType,
				             data.questionerId,
				                    "<button class='viewanswer' id='"+data.id+"'>查看</button>"]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		
		$('button.viewanswer').click(function(e){
			var helpid = $(this).attr('id');
			
			var help = helpservice.find(parseInt(helpid));
			$('#nlabel').html(help.question);
			$('#ndetail').html(help.answer);
			
			$('#noticedetail').modal({
				  keyboard: false,
				  backdrop: true
			});
		})

		return res;
	}
	var mySettings = $.extend({}, defaultSettings, {
		"aoColumns" : columns,
		"fnServerData" : fnServerData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
	
}

var helpview = function(container){
	var helpservice = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IHelpService");
	var columns = [ {
		"sTitle" : "所属分类",
		"code" : "category"
	},{
		"sTitle" : "帮助标题",
			"code" : "name"
	}, {
		"sTitle" : "发布时间",
		"code" : "state"
	}, {
		"sTitle" : "操作",
		"code" : "operate"
	}];
	
	var cat = {
			0: '新手帮助',
			1: '常见问题',
			2: '交易管理',
			3: '投资融资',
			4: '免责声明'
	}
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "iDisplayStart")
				iDisplayStart = data.value;
			if (data.name == "iDisplayLength")
				iDisplayLength = data.value;
		}
		var res = null;
		res = helpservice.findPublicHelps(-1, iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				result.aaData.push([cat[data.publicType],
				                    data.question,
				             formatDate(data.createtime),
				                    "<button class='viewhelp' id='"+data.id+"'>查看</button>"]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		
		$('button.viewhelp').click(function(e){
			var helpid = $(this).attr('id');
			var help = helpservice.find(parseInt(helpid));
			$('#nlabel').html(help.question);
			$('#ndetail').html(help.answer==null?'-':help.answer);
			$('#noticedetail').modal({
				  keyboard: false,
				  backdrop: true
			});
		})

		return res;
	}
	var mySettings = $.extend({}, defaultSettings, {
		"aoColumns" : columns,
		"fnServerData" : fnServerData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
	
}


var activity = function(container){
	var actservice = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IActivityService");
	var columns = [ {
		"sTitle" : "活动标题",
			"code" : "name"
	}, {
		"sTitle" : "申请截止时间",
		"code" : "state"
	}, {
		"sTitle" : "正式活动时间",
		"code" : "state"
	}, {
		"sTitle" : "状态",
		"code" : "state"
	}, {
		"sTitle" : "操作",
		"code" : "operate"
	}];
	
	var actstate = {1 : '报名中', 2 : '进行中', 3 : '已结束'};
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "iDisplayStart")
				iDisplayStart = data.value;
			if (data.name == "iDisplayLength")
				iDisplayLength = data.value;
		}
		var res = null;
		res = actservice.findByState(-1, iDisplayStart, iDisplayLength);
		var result = {};
		result.iTotalRecords = res.get('total');
		result.iTotalDisplayRecords = res.get('total');
		result.aaData = new Array();
		var items = res.get('result');
		if(items)
		{
			for(var i=0; i<items.size(); i++){
				var data=items.get(i);
				result.aaData.push(['<a href="'+data.url+'?id='+data.id+'" target="_blank">'+data.name+'</a>',
				             formatDate(data.applystarttime),
				             formatDate(data.starttime),
				             actstate[data.state],
				              "<button class='editactivity' id='"+data.id+"'>编辑详情</button>"]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		
		$('button.editactivity').click(function(e){
			var id = $(this).attr('id');
			window.open('editactivity.html?id='+id);
		})

		return res;
	}
	var mySettings = $.extend({}, defaultSettings, {
		"aoColumns" : columns,
		"fnServerData" : fnServerData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append("<div style='float:right; margin-top:10px;'><button id='addactivity'>添加活动</button></div>");
	container.append(content);
	table.dataTable(mySettings);
	
	$('#addactivity').click(function(e){
		window.open('addactivity.html');
	});
	
}

var noticeview = function(container){
	var nservice = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.INoticeService");
	var columns = [ {
		"sTitle" : "公告标题",
			"code" : "name"
	},{
		"sTitle" : "分类",
		"code" : "category"
	}, {
		"sTitle" : "发布时间",
		"code" : "state"
	}, {
		"sTitle" : "发布对象",
		"code" : "state"
	}, {
		"sTitle" : "用户级别",
		"code" : "state"
	}, {
		"sTitle" : "操作",
		"code" : "operate"
	}];
	
	var userType = {0 : '全部', 1 : '投资方', 2 : '融资方'};
	var categoryType = {0 : '平台公告', 1 : '企业公告', 2:'活动公告'};
	
	var fnServerData = function(sSource, aoData, fnCallback, oSettings) {
		var sEcho = "";
		var iDisplayStart = 0;
		var iDisplayLength = 0;
		for ( var i = 0; i < aoData.length; i++) {
			var data = aoData[i];
			if (data.name == "sEcho")
				sEcho = data.value;
			if (data.name == "iDisplayStart")
				iDisplayStart = data.value;
			if (data.name == "iDisplayLength")
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
				result.aaData.push([data.title,
				                    categoryType[data.publicType],
				             formatDate(data.publishtime),
				             userType[data.usefor],
				                    data.level,
				                    "<button class='viewnotice' id='"+data.id+"'>查看</button>"]);
			}
		}
		result.sEcho = sEcho;
		fnCallback(result);
		
		$('button.viewnotice').click(function(e){
			var letterid = $(this).attr('id');
			var notice = nservice.find(parseInt(letterid));
			$('#nlabel').html(notice.title);
			$('#ndetail').html(notice.content);
			$('#noticedetail').modal({
				  keyboard: false,
				  backdrop: true
			});
		})

		return res;
	}
	var mySettings = $.extend({}, defaultSettings, {
		"aoColumns" : columns,
		"fnServerData" : fnServerData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
}

var newswrite = function(container){
	var newsservice = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.INewsService");
	var total = '<div class="container-fluid" style="width:800px;">';
		total += '<div class="row" style="margin-bottom:20px; margin-top:20px;padding-left:20px;">';
	
	var title = '<div class="form-group has-success has-feedback" style="margin-top:5px;">';
		title+='<label class="control-label col-sm-3" for="inputSuccess3">新闻标题</label>';
		title+='<div class="col-sm-9">';
		title+='<input type="text" class="form-control" id="notice-title"></div></div>';
		
		total += title;
		
		
		
		var category = '<div class="form-group has-success has-feedback" style="margin-top:5px;">';
		category+='<label class="control-label col-sm-3" for="inputSuccess3">新闻类型</label>';
		category+='<div class="col-sm-9">';
		category+='<select class="form-control" id="news-category">';
		category+='<option value=0>行业资讯</option>';
		category+='<option value=1>财经新闻</option>';
		category+='</select></div></div>';
		
		total += category;
	
		
	var content = '<div class="form-group has-success has-feedback" style="margin-top:5px;">';
	content += '<label class="control-label col-sm-3" for="inputSuccess3">新闻内容</label>';
	content += '<div class="col-sm-9">';
	content += '<textarea class="form-control" id="notice-content" style="min-height:400px;"></textarea></div></div>';
	
	total += content;
	
	total += '<button id="notice-create" class="btn btn-lg btn-success btn-block">创建</button>'
	
	total += "</div></div>";
	
	container.html(total);
	
	$('#notice-create').click(function(e){
		var title = $('#notice-title').val();
		var content = $('#notice-content').val();
		var category = parseInt($('#news-category').val());
		
		
		
		if(title==null||title==''){
			alert('新闻标题不能为空');
			return;
		}else if(content==null||content==''){
			alert('新闻内容不能为空');
			return;
		}
		var news = {'_t_':'gpps.model.News', 'title':title, 'content': content, 'publishtime': (new Date()).getTime(), 'publicType' : category};
		newsservice.create(news);
		window.location.href="opadmin.html?fid=news&sid=news-view";
	});
	
}

var helpwrite = function(container){
	var helpservice = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.IHelpService");
	var total = '<div class="container-fluid" style="width:800px;">';
		total += '<div class="row" style="margin-bottom:20px; margin-top:20px;padding-left:20px;">';
		
		var category = '<div class="form-group has-success has-feedback" style="margin-top:5px;">';
		category+='<label class="control-label col-sm-3" for="inputSuccess3">帮助类型</label>';
		category+='<div class="col-sm-9">';
		category+='<select class="form-control" id="help-category">';
		category+='<option value=0>新手帮助</option>';
		category+='<option value=1>常见问题</option>';
		category+='<option value=2>交易管理</option>';
		category+='<option value=3>投资融资</option>';
		category+='<option value=4>免责声明</option>';
		category+='</select></div></div>';
		
		total += category;
		
		
		
		
	
	var title = '<div class="form-group has-success has-feedback" style="margin-top:5px;">';
		title+='<label class="control-label col-sm-3" for="inputSuccess3">帮助标题</label>';
		title+='<div class="col-sm-9">';
		title+='<input type="text" class="form-control" id="help-title"></div></div>';
		
		total += title;
		
	var content = '<div class="form-group has-success has-feedback" style="margin-top:5px;">';
	content += '<label class="control-label col-sm-3" for="inputSuccess3">帮助内容</label>';
	content += '<div class="col-sm-9">';
	content += '<textarea class="form-control" id="help-content" style="min-height:400px;"></textarea></div></div>';
	
	total += content;
	
	total += '<button id="help-create" class="btn btn-lg btn-success btn-block">创建</button>'
	
	total += "</div></div>";
	
	container.html(total);
	
	$('#help-create').click(function(e){
		var category = $('#help-category').val();
		var title = $('#help-title').val();
		var content = $('#help-content').val();
		
		
		
		if(title==null||title==''){
			alert('帮助标题不能为空');
			return;
		}else if(content==null||content==''){
			alert('帮助内容不能为空');
			return;
		}
		var help = {'_t_':'gpps.model.Help','type':0, 'publicType':parseInt(category), 'question':title, 'answer': content, 'createtime': (new Date()).getTime()};
		helpservice.createPublic(help);
		window.location.href="opadmin.html?fid=help&sid=help-view";
	});
	
}

var noticewrite = function(container){
	var nservice = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.INoticeService");
	var total = '<div class="container-fluid" style="width:800px;">';
		total += '<div class="row" style="margin-bottom:20px; margin-top:20px;padding-left:20px;">';
	
	var title = '<div class="form-group has-success has-feedback" style="margin-top:5px;">';
		title+='<label class="control-label col-sm-3" for="inputSuccess3">公告标题</label>';
		title+='<div class="col-sm-9">';
		title+='<input type="text" class="form-control" id="notice-title"></div></div>';
		
		total += title;
		
		
		var category = '<div class="form-group has-success has-feedback" style="margin-top:5px;">';
		category+='<label class="control-label col-sm-3" for="inputSuccess3">公告类型</label>';
		category+='<div class="col-sm-9">';
		category+='<select class="form-control" id="notice-category">';
		category+='<option value=0>平台公告</option>';
		category+='<option value=1>企业公告</option>';
		category+='<option value=2>活动公告</option>';
		category+='</select></div></div>';
		
		total += category;
		
		
	var content = '<div class="form-group has-success has-feedback" style="margin-top:5px;">';
	content += '<label class="control-label col-sm-3" for="inputSuccess3">公告内容</label>';
	content += '<div class="col-sm-9">';
	content += '<textarea class="form-control" id="notice-content" style="min-height:400px;"></textarea></div></div>';
	
	total += content;
	
	var usertype = '<div class="form-group has-success" style="margin-top:5px;">';
	usertype += '<label class="control-label col-sm-3" for="addr"></label>';
	usertype += '<div class="col-sm-9">';
	usertype += '<label class="radio-inline"><input type="radio" name="usertype"  checked="checked" value="0"> 全部</label>';
	usertype += '<label class="radio-inline"><input type="radio" name="usertype" value="1"> 投资方</label>';
	usertype += '<label class="radio-inline"><input type="radio" name="usertype" value="2"> 融资方</label>';
	usertype += '</div></div>';
	
	total += usertype;
	
	var level = '<div class="form-group has-success has-feedback" style="margin-top:5px;">';
	level+='<label class="control-label col-sm-3" for="inputSuccess3">用户级别</label>';
	level+='<div class="col-sm-9">';
	level+='<select class="form-control" id="level">';
	level+='<option value=-1>不限</option>';
	level+='<option value=0>级别0</option>';
	level+='<option value=1>级别1</option>';
	level+='<option value=2>级别2</option>';
	level+='<option value=3>级别3</option>';
	level+='<option value=4>级别4</option>';
	level+='<option value=5>级别5</option>';
	level+='<option value=6>级别6</option>';
	level+='</select></div></div>';
	
	total += level;
	
	total += '<button id="notice-create" class="btn btn-lg btn-success btn-block">创建</button>'
	
	total += "</div></div>";
	
	container.html(total);
	
	$('#notice-create').click(function(e){
		var title = $('#notice-title').val();
		var content = $('#notice-content').val();
		var usertype = $("input[name=usertype]:checked").attr("value");
		
		var noticecategory = parseInt($('#notice-category').val());
		
		var level = $('#level').val();
		
		
		
		if(title==null||title==''){
			alert('公告标题不能为空');
			return;
		}else if(content==null||content==''){
			alert('公告内容不能为空');
			return;
		}
		var notice = {'_t_':'gpps.model.Notice', 'title':title, 'content': content, 'publishtime': (new Date()).getTime(), 'usefor':parseInt(usertype), 'level': parseInt(level), 'publicType':noticecategory};
		nservice.create(notice);
		window.location.href="opadmin.html?fid=notice&sid=notice-view";
	});
	
}

var message = function(container){
	var messageService = EasyServiceClient.getRemoteProxy("/easyservice/gpps.service.message.IMessageSupportService");
	var checkCountButton = $('<button>查询余额</button>');
	$('<div class="row" style="padding-top:10px; padding-bottom:10px;"></div>').append(checkCountButton).appendTo(container);
	$('<div class="row" id="result" style="min-height:200px;"></div>').appendTo(container);
	checkCountButton.click(function(e){
		var res = messageService.queryRest();
		$('#result').html(res);
	});
}

var validatetp = function(container){
	
	var columns = [ {
		"sTitle" : "校验项目",
			"code" : "name"
	}, {
		"sTitle" : "校验说明",
		"code" : "state"
	}, {
		"sTitle" : "执行状态",
		"code" : "phone"
	}, {
		"sTitle" : "执行结果",
		"code" : "requesttime"
	}, {
		"sTitle" : "操作",
		"code" : "requesttime"
	}];
	var aaData = new Array();
	aaData.push(["现金流对帐",
	             "<a href='#'>说明</a>",
		         "执行完毕",
		         "<button>结果</button>",
		         "<button>校验</button>"]);
	
	aaData.push(["账户对帐",
	             "<a href='#'>说明</a>",
		         "执行完毕",
		         "<button>结果</button>",
		         "<button>校验</button>"]);
	
	aaData.push(["融资产品对帐",
	             "<a href='#'>说明</a>",
		         "执行完毕",
		         "<button>结果</button>",
		         "<button>校验</button>"]);

	var mySettings = $.extend({}, defaultSettings_noCallBack, {
		"aoColumns" : columns,
		"aaData" : aaData
	});
	var content = $('<div></div>');
	var table = $('<table class="table table-striped table-hover" style="min-width:300px;"></table>').appendTo(content);
	container.append(content);
	table.dataTable(mySettings);
}


var nav2funtion = {
		'lender-view' : lenderview,
		'borrower-request' : borrowerrequest,
		'tohandle-borrower-request' : borrowerrequest,
		'borrower-all' : borrowerall,
		'borrower-new' : borrowernew,
		'borrower-pass' : borrowerpass,
		'borrower-refuse' : borrowerrefuse,
		'request-all' : requestall,
		'request-tohandle' : requesttohandle,
		'tohandle-request' : requesttohandle,
		'request-pass' : requestpass,
		'request-refuse' : requestrefuse,
		"order-all" : orderall,
		"order-unpublish" : orderunpublish,
		"order-preview" : orderpreview,
		'tohandle-order-preview' : orderpreview,
		"order-financing" : orderfinancing,
		'tohandle-order-financing' : orderfinancing,
		"order-paying" : orderpaying,
		"order-toclose" : ordertoclose,
		'tohandle-order-toclose' : ordertoclose,
		"payback-toaudit" : paybacktoaudit,
		"order-closed" : orderclosed,
		"order-quit" : orderquit,
		"notice-write" : noticewrite,
		"notice-view" : noticeview,
		"news-write" : newswrite,
		"news-view" : newsview,
		"help-write" : helpwrite,
		"help-view" : helpview,
		"help-question" : helpquestion,
		"help-answered" : helpanswered,
		"activity" : activity,
		"validate-tp" : validatetp,
		"message" : message
}