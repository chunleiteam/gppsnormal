

var createProductLine = function(cont, id, title, label1, des1, label2, des2, description, menkan, qixian, rate, style){
	var container = $(cont).addClass('newProjectItem').addClass('newProjectInfoBox');
	var row1 = $('<div></div>').addClass('row').css(
			{
				"padding-bottom" : "20px",
				"padding-left" : "0px",
				"padding-right" : "0px"
			}
			);
	var row1_col1 = $('<div></div>').addClass('col-xs-12 col-sm-12 col-md-2');
	var stateBtnDiv = $('<div></div>').addClass('stateBtn');
	stateBtnDiv.html('<p class="stateBtnProcess"><a title="产品列表" class="productline" href="productlist.html?sid='+id+'" target="_blank"><span class="stateText">'+title+'</span></a></p>');
	stateBtnDiv.appendTo(row1_col1);
	
	
	var row1_col2 = $('<div></div>').addClass('col-xs-12 col-sm-12 col-md-12');
	var BtnListDiv1 = $('<div></div>').addClass('newProjectTitleRightBtnList');
	BtnListDiv1.html('<p class="BtnNormal">'+label1+'</p><div class="promptQX promptQXBox">'+des1+'</div>');
	BtnListDiv1.appendTo(row1_col2);
	
	var BtnListDiv2 = $('<div></div>').addClass('newProjectTitleRightBtnList');
	BtnListDiv2.html('<p class="BtnNormal">'+label2+'</p><div class="promptQX promptQXBox">'+des2+'</div>');
	BtnListDiv2.appendTo(row1_col2);
	
	$('<div class="clear"></div>').appendTo(row1_col2);
	row1.append(row1_col1);	
	row1.append(row1_col2);
	container.append(row1);
	
	
	
	
	var row2 = $('<div></div>').addClass('row');
	
	var row2_col1 = $('<div></div>').addClass('col-xs-3 col-sm-3 col-md-4');
	
	
	var a = $('<a href="intro/intro.html?sid='+id+'" target="_blank" title="产品类型详细介绍"></a>');
	row2_col1.append($('<div class="seeMore"></div>').append(a));
	
	var row2_col2 = $('<div></div>').addClass('col-xs-9 col-sm-9 col-md-8');
	row2_col2.html('<div class="row"><div class="col-xs-12 col-sm-12 col-md-12">'+description+'</div></div>');
	
	var row2_col3 = $('<div></div>').addClass('col-xs-12 col-sm-12 col-md-12');
	row2_col3.html('<p class="itemTextStyle">投标门槛：<span class="orangeStyle">'+menkan+'</span></p>');
	
	var row2_col4 = $('<div></div>').addClass('col-xs-12 col-sm-12 col-md-12');
	row2_col4.html('<p class="itemTextStyle">融资期限：<span class="orangeStyle">'+qixian+'</span></p>');

	var row2_col5 = $('<div></div>').addClass('col-xs-12 col-sm-12 col-md-12');
	row2_col5.html('<div class="itemTextStyle">年化收益：<span class="orangeStyle">'+rate+'</span></div>');
	
	var row2_col6 = $('<div></div>').addClass('col-xs-12 col-sm-12 col-md-12');
	row2_col6.html('<p class="itemTextStyle">还款方式：<span class="orangeStyle">'+style+'</span></p>');
	
	var row2_col7 = $('<div></div>').addClass('col-xs-12 col-sm-12 col-md-12');
	row2_col7.html('<a style="float:right;" href="productlist.html?sid='+id+'">产品列表>>>></a>');
	
	row2.append(row2_col1);
	row2.append(row2_col2);
	row2.append(row2_col3);
	row2.append(row2_col4);
	row2.append(row2_col5);
	row2.append(row2_col6);
	row2.append(row2_col7);
	
	container.append(row2);
	
	return container;
}

var cssStatus = {
		1 : 'online-status', 
		4  : 'done-status',
		2: 'done-status',
		8   : 'done-status'
	}
var buttonStatus = {
		1 : ['btns', '投资'], 
		4  : ['done-success','审核中'],
		2: ['done-success','还款中'],
		8   : ['done-success','还款成功']
}
var dateStr = {
		1 : '截止', 
		4  : '截止',
		2: '计息',
		8   : '关闭'
}


var createProduct2 = function(order){
	var pros = order.products;
	if(pros.size()==1){
		return createSingleProduct2(order);
	}else if(pros.size()>1){
		return createStructureProduct2(order);
	}else
		{
		return null;
		}
}

var createStructureProduct2 = function(order){
	var pros = order.products;
	var state = order.state;
	var maxdays = 0;
	var mindays = 100000;
	
	
	var totalamount = 0;
	var realamount = 0;
	for(var i=0; i<pros.size(); i++){
		var days = parseInt((pros.get(i).incomeEndtime-order.incomeStarttime+23*3600*1000)/(24*3600*1000)+'');
		if(days>maxdays)
			{
			maxdays = days;
			}
		if(days<mindays){
			mindays = days;
		}
		totalamount += parseInt(pros.get(i).expectAmount.value);
		realamount += parseInt(pros.get(i).realAmount.value);
	}
	
	
	var str = '<li class="clearfix">';
	str    += '<div class="product-info-wrap clearfix">';
	str    += '<div class="product-status">';
	str    += '<p class="product-name">';
	str    += '<a class="product-title constructure" href="#" id="'+order.id+'">'+order.title+'[结构化]'+'</a>';
	str    += '</p>';
	str    += '<div class="product-info-wrap clearfix">';
	str    += '<p class="product-info-l">';
	str    += '<span style="margin-right:26px;">投资期限：'+mindays+'-'+maxdays+'天</span>';
	str    += '<span>项目本金：'+totalamount+'元</span>';
	str    += '</p>';
	str    += '<p>';
	str    += '<span class="invest-price">已投金额：'+realamount+'元</span>';
	str    += '</p>';
	str    += '</div>';
	str    += '</div>';
	
	
	var status = '';
	var stateCls = '';
	var stateName = '';
	
	if(state==1){
		status = 'online-status';
		stateName = '投资中';
		stateCls = 'btns';
		str    += '<div class="operate-status '+status+' clearfix">';
		str    += '<div>';
		str    += '<div class="current-amount">';
		str    += '<strong>可投金额</strong>';
		str    += '<span class="cur">'+(totalamount- realamount)+'</span> 元            </div>';
		str    += '<a id='+order.id+' class="stat '+stateCls+'">'+stateName+'</a>';
		str    += '<div style="clear:both;"></div>';
		str    += '</div>';
		str    += '<div class="publish-time" style="margin-top:10px; padding-top:5px;">'+formatDateToDay(order.financingEndtime)+'截止</div>';
	}else if(state==2){
		status = 'done-status';
	stateName = '预发布';
	stateCls = 'btns-green';
		str    += '<div class="operate-status '+status+' clearfix">';
		str    += '<div>';
		str    += '<div class="current-amount">';
		str    += '<strong>可投金额</strong>';
		str    += '<span class="cur">'+(totalamount- realamount)+'</span> 元            </div>';
		str    += '<a id='+order.id+' class="stat '+stateCls+'">'+stateName+'</a>';
		str    += '<div style="clear:both;"></div>';
		str    += '</div>';
		str    += '<div class="publish-time" style="margin-top:10px; padding-top:5px;">'+formatDateToDay(order.financingStarttime)+'开始</div>';	
	}else if(state==4){
		status = 'done-status';
		stateName = '还款中';
		stateCls = 'btns-grey';
		str    += '<div class="operate-status '+status+' clearfix">';
		str    += '<div>';
		str    += '<div class="current-amount">';
		str    += '<strong>可投金额</strong>';
		str    += '<span class="cur">'+(totalamount- realamount)+'</span> 元           </div>';
		str    += '<a id='+order.id+' class="stat '+stateCls+'">'+stateName+'</a>';
		str    += '<div style="clear:both;"></div>';
		str    += '</div>';
		str    += '<div class="publish-time" style="margin-top:10px; padding-top:5px;">'+formatDateToDay(order.incomeStarttime)+'开始还款</div>';	
	}else if(state==8){
		status = 'done-status';
		stateName = '待关闭';
		stateCls = 'btns-grey';
		str    += '<div class="operate-status '+status+' clearfix">';
		str    += '<div>';
		str    += '<div class="current-amount">';
		str    += '<strong>可投金额</strong>';
		str    += '<span class="cur">'+(totalamount- realamount)+'</span> 元           </div>';
		str    += '<a id='+order.id+' class="stat '+stateCls+'">'+stateName+'</a>';
		str    += '<div style="clear:both;"></div>';
		str    += '</div>';
		str    += '<div class="publish-time" style="margin-top:10px; padding-top:5px;">'+formatDateToDay(order.lastModifytime)+'还款完毕</div>';	
	}else if(state==16){
		status = 'done-status';
		stateName = '流标';
		stateCls = 'btns-grey';
		str    += '<div class="operate-status '+status+' clearfix">';
		str    += '<div>';
		str    += '<div class="current-amount">';
		str    += '<strong>流标原因</strong>';
		str    += '融资额度未满  </div>';
		str    += '<a id='+order.id+' class="stat '+stateCls+'">'+stateName+'</a>';
		str    += '<div style="clear:both;"></div>';
		str    += '</div>';
		str    += '<div class="publish-time" style="margin-top:10px; padding-top:5px;">'+formatDateToDay(order.lastModifytime)+'流标</div>';	
	}else if(state==32){
		status = 'done-status';
		stateName = '已关闭';
		stateCls = 'btns-grey';
		str    += '<div class="operate-status '+status+' clearfix">';
		str    += '<div>';
		str    += '<div class="current-amount">';
		str    += '<strong>可投金额</strong>';
		str    += '<span class="cur">'+(totalamount- realamount)+'</span> 元           </div>';
		str    += '<a id='+order.id+' class="stat '+stateCls+'">'+stateName+'</a>';
		str    += '<div style="clear:both;"></div>';
		str    += '</div>';
		str    += '<div class="publish-time" style="margin-top:10px; padding-top:5px;">'+formatDateToDay(order.lastModifytime)+'关闭</div>';	
	}
	str    += '</div>';
	str    += '<div style="clear:both;"></div>';
	str    += '<div class="productinfo" style="width:100%; display:none;">';
	
	for(var i=0; i<pros.size(); i++){
		str    += '<div class="operate-status '+status+' clearfix">';
		str    += '<div>';
		str    += '<div class="current-amount">';
		str    += '<strong>'+seriesname[pros.get(i).productseriesId]+'[level'+pros.get(i).levelToBuy+']'+'</strong>';
		str    += '年华利率<span class="cur">'+parseInt(parseFloat(pros.get(i).rate.value)*100)+"%"+'</span><br>';
		str    +=  '预融资金'+pros.get(i).expectAmount.value+'元<br>';
		str    +=  '已融资金'+pros.get(i).realAmount.value+'元';
		str    +=  '</div>';
		if(state==1)
			{
				if(parseInt(pros.get(i).expectAmount.value)==parseInt(pros.get(i).realAmount.value))
					{
				str    += '<a target="_blank" href="productdetail.html?pid='+pros.get(i).id+'" class="btns">售罄</a>';
					}else{
				str    += '<a target="_blank" href="productdetail.html?pid='+pros.get(i).id+'" class="btns">投资</a>';
					}
			}else if(state==2){
				str    += '<a target="_blank" href="productdetail.html?pid='+pros.get(i).id+'" class="btns-green">预发布</a>';
			}
			else if(state==4){
				if(pros.get(i).state==8)
					str    += '<a target="_blank" href="productdetail.html?pid='+pros.get(i).id+'" class="btns-grey">还款完毕</a>';
				else
				str    += '<a target="_blank" href="productdetail.html?pid='+pros.get(i).id+'" class="btns-grey">还款中</a>';
			}else if(state==8){
				str    += '<a target="_blank" href="productdetail.html?pid='+pros.get(i).id+'" class="btns-grey">还款完毕</a>';
			}else if(state==16){
				str    += '<a target="_blank" href="productdetail.html?pid='+pros.get(i).id+'" class="btns-grey">流标</a>';
			}else if(state==32){
				str    += '<a target="_blank" href="productdetail.html?pid='+pros.get(i).id+'" class="btns-grey">已关闭</a>';
			}
		str    += '<div style="clear:both;"></div>';
		str    += '</div>';
		str    += '</div>';
	}
	
	str    += '</div>';
	str    += '</li>';
	
	return str;
}









	
	
	
	
	




	
	

		
			
			
			
		
	

//首页用
var createSingleProduct = function(pro){
	var order = pro.govermentOrder;
	var state = order.state;
	var days = parseInt((pro.incomeEndtime-order.incomeStarttime+23*3600*1000)/(24*3600*1000)+'');
	
	var str = '<tr class="ui-list-item" id="header" style="padding-left:0px; padding-right:0px;">';
	str += '<td class="text-big">';
	str += '<div style="max-width:200px; overflow:hidden;">';
	str += '<a class="producttitle" href="productdetail.html?pid='+pro.id+'" target="_blank" title="'+order.title+'">'+order.title+'</a>';
	str += '</div>';
	str += '</td>';
	
	var cla = 'A';
	var sname = '';
	if(pro.productseriesId==1){
		cla='A';
		sname='稳健';
	}else if(pro.productseriesId==2){
		cla='C';
		sname='均衡';
	}else if(pro.productseriesId==3){
		cla='D';
		sname='进取';
	}
	str += '<td class="ui-list-field text-center"><a class="ui-creditlevel '+cla+' snow">'+sname+'</a></td>';
	str += '<td class="ui-list-field num text-right pr10"><em class="value">'+parseInt(parseFloat(pro.rate.value)*100)+'</em>%</td>';
	str += '<td class="ui-list-field num text-right pr10"><em class="value">'+parseInt(pro.expectAmount.value)+'</em>元</td>';
	str += '<td class="ui-list-field num text-right pr10"><em class="value">'+days+'</em>天</td>';
	str += '<td class="ui-list-field">';
	
	var percent = parseInt(parseInt(pro.realAmount.value)*100/parseInt(pro.expectAmount.value));
	
	str += '<strong class="ui-progressbar-mid ui-progressbar-mid-'+percent+'"><em>'+percent+'</em>%</strong>';
	str += '</td>';
	str += '<td class="ui-list-field text-right">';
	if(state==1)
		{
		if(parseInt(pro.expectAmount.value)==parseInt(pro.realAmount.value))
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-FIRST_READY" href="productdetail.html?pid='+pro.id+'" target="_blank">';
		else
			str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-OPEN" href="productdetail.html?pid='+pro.id+'" target="_blank">';
		}
	else if(state==2)
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-FIRST_APPLY" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	else if(state==4)
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-IN_PROGRESS" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	else if(state==8)
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-OVER_DUE" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	else if(state==32)
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-CLOSED" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	else if(state==16)
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-BAD_DEBT" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	str += '<span class="OPEN">投标中</span>';
	str += '<span class="READY FIRST_READY">已满标</span>';
	str += '<span class="IN_PROGRESS">还款中</span>';
	str += '<span class="FIRST_APPLY">预发布</span>';
	str += '<span class="OVER_DUE">待关闭</span>';
	str += '<span class="CLOSED">已关闭</span>';
	str += '<span class="BAD_DEBT">流标</span>'
	str += '</a>';
	str += '</td>';
	str += '</tr>';
	
	return str;
}


var createSingleSubProductForFinancing = function(pro){
	var order = pro.govermentOrder;
	var state = order.state;
	var days = parseInt((pro.incomeEndtime-order.incomeStarttime+23*3600*1000)/(24*3600*1000)+'');
	
	var str = '<tr class="ui-list-item" id="header" style="padding-left:0px; padding-right:0px;">';
	
	var cla = 'A';
	var sname = '';
	if(pro.productseriesId==1){
		cla='A';
		sname='稳健';
	}else if(pro.productseriesId==2){
		cla='C';
		sname='均衡';
	}else if(pro.productseriesId==3){
		cla='D';
		sname='进取';
	}
	str += '<td class="ui-list-field text-center"><a class="ui-creditlevel '+cla+' snow">'+sname+'</a></td>';
	str += '<td class="text-right pr10">'+parseInt(parseFloat(pro.rate.value)*100)+'%</td>';
	str += '<td class="text-right pr10">'+parseInt(pro.expectAmount.value)+'元</td>';
	str += '<td class="text-right pr10">'+parseInt(pro.realAmount.value)+'元</td>';
	str += '<td class="text-right pr10">'+days+'天</td>';
	str += '<td class="ui-list-field">';
	
	var percent = parseInt(parseInt(pro.realAmount.value)*100/parseInt(pro.expectAmount.value));
	
	str += '<strong class="ui-progressbar-mid ui-progressbar-mid-'+percent+'"><em>'+percent+'</em>%</strong>';
	str += '</td>';
	str += '<td class="ui-list-field text-right">';
	if(state==1)
		{
		if(parseInt(pro.expectAmount.value)==parseInt(pro.realAmount.value))
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-FIRST_READY" href="productdetail.html?pid='+pro.id+'" target="_blank">';
		else
			str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-OPEN" href="productdetail.html?pid='+pro.id+'" target="_blank">';
		}
	else if(state==2)
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-FIRST_APPLY" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	else if(state==4)
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-IN_PROGRESS" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	else if(state==8)
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-OVER_DUE" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	else if(state==32)
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-CLOSED" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	else if(state==16)
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-BAD_DEBT" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	str += '<span class="OPEN">投标中</span>';
	str += '<span class="READY FIRST_READY">已满标</span>';
	str += '<span class="IN_PROGRESS">还款中</span>';
	str += '<span class="FIRST_APPLY">预发布</span>';
	str += '<span class="OVER_DUE">待关闭</span>';
	str += '<span class="CLOSED">已关闭</span>';
	str += '<span class="BAD_DEBT">流标</span>'
	str += '</a>';
	str += '</td>';
	str += '<td class="text-right pr10">';
	str += '<a href="uploadContract.html?pid='+pro.id+'" target="_blank">合同上载</a>';
	str += '</td>';
	str += '</tr>';
	
	return str;
}

//供融资方在我的账户中浏览用
var createSingleSubProduct = function(pro){
	var order = pro.govermentOrder;
	var state = order.state;
	var days = parseInt((pro.incomeEndtime-order.incomeStarttime+23*3600*1000)/(24*3600*1000)+'');
	
	var str = '<tr class="ui-list-item" id="header" style="padding-left:0px; padding-right:0px;">';
	
	var cla = 'A';
	var sname = '';
	if(pro.productseriesId==1){
		cla='A';
		sname='稳健';
	}else if(pro.productseriesId==2){
		cla='C';
		sname='均衡';
	}else if(pro.productseriesId==3){
		cla='D';
		sname='进取';
	}
	str += '<td class="ui-list-field text-center"><a class="ui-creditlevel '+cla+' snow">'+sname+'</a></td>';
	str += '<td class="text-right pr10">'+parseInt(parseFloat(pro.rate.value)*100)+'%</td>';
	str += '<td class="text-right pr10">'+parseInt(pro.expectAmount.value)+'元</td>';
	str += '<td class="text-right pr10">'+parseInt(pro.realAmount.value)+'元</td>';
	str += '<td class="text-right pr10">'+days+'天</td>';
	str += '<td class="ui-list-field">';
	
	var percent = parseInt(parseInt(pro.realAmount.value)*100/parseInt(pro.expectAmount.value));
	
	str += '<strong class="ui-progressbar-mid ui-progressbar-mid-'+percent+'"><em>'+percent+'</em>%</strong>';
	str += '</td>';
	str += '<td class="ui-list-field text-right">';
	if(state==1)
		{
		if(parseInt(pro.expectAmount.value)==parseInt(pro.realAmount.value))
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-FIRST_READY" href="productdetail.html?pid='+pro.id+'" target="_blank">';
		else
			str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-OPEN" href="productdetail.html?pid='+pro.id+'" target="_blank">';
		}
	else if(state==2)
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-FIRST_APPLY" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	else if(state==4)
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-IN_PROGRESS" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	else if(state==8)
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-OVER_DUE" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	else if(state==32)
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-CLOSED" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	else if(state==16)
		str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-BAD_DEBT" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	str += '<span class="OPEN">投标中</span>';
	str += '<span class="READY FIRST_READY">已满标</span>';
	str += '<span class="IN_PROGRESS">还款中</span>';
	str += '<span class="FIRST_APPLY">预发布</span>';
	str += '<span class="OVER_DUE">待关闭</span>';
	str += '<span class="CLOSED">已关闭</span>';
	str += '<span class="BAD_DEBT">流标</span>'
	str += '</a>';
	str += '</td>';
	str += '</tr>';
	
	return str;
}


//未发布产品
var createUnpublishProduct = function(pro){
	var order = pro.govermentOrder;
	var state = order.state;
	var days = parseInt((pro.incomeEndtime-order.incomeStarttime+23*3600*1000)/(24*3600*1000)+'');
	
	var str = '<tr class="ui-list-item" id="header" style="padding-left:0px; padding-right:0px;">';
	
	var cla = 'A';
	var sname = '';
	if(pro.productseriesId==1){
		cla='A';
		sname='稳健';
	}else if(pro.productseriesId==2){
		cla='C';
		sname='均衡';
	}else if(pro.productseriesId==3){
		cla='D';
		sname='进取';
	}
	str += '<td class="ui-list-field text-center"><a class="ui-creditlevel '+cla+' snow">'+sname+'</a></td>';
	str += '<td class="text-right pr10">'+parseInt(parseFloat(pro.rate.value)*100)+'%</td>';
	str += '<td class="text-right pr10">'+parseInt(pro.expectAmount.value)+'元</td>';
	str += '<td class="text-right pr10">'+parseInt(pro.realAmount.value)+'元</td>';
	str += '<td class="text-right pr10">'+days+'天</td>';
	str += '<td class="ui-list-field">';
	
	var percent = parseInt(parseInt(pro.realAmount.value)*100/parseInt(pro.expectAmount.value));
	
	str += '<strong class="ui-progressbar-mid ui-progressbar-mid-'+percent+'"><em>'+percent+'</em>%</strong>';
	str += '</td>';
	str += '<td class="ui-list-field text-right">';
	str += '<a class="ui-button ui-button-mid ui-button-blue ui-list-invest-button ui-list-invest-button-CLOSED" href="productdetail.html?pid='+pro.id+'" target="_blank">';
	str += '<span class="CLOSED">未发布</span>';
	str += '</a>';
	str += '</td>';
	str += '</tr>';
	
	return str;
}


var createSingleProduct2 = function(order){
	var pro = order.products.get(0);
	var state = order.state;
	var days = parseInt((pro.incomeEndtime-order.incomeStarttime+23*3600*1000)/(24*3600*1000)+'');
	
	
	var str = '<li class="clearfix">';
	str    += '<div class="product-status">';
	str    += '<p class="product-name">';
	str    += '<a target="_blank" class="product-title" href="productdetail.html?pid='+pro.id+'">'+order.title+'['+seriesname[pro.productseriesId]+']'+'[level'+pro.levelToBuy+']'+'</a>';
	str    += '</p>';
	str    += '<div class="product-info-wrap clearfix">';
	str    += '<p class="product-info-l">';
	str    += '<span style="margin-right:26px;">预期年化利率：'+parseInt(parseFloat(pro.rate.value)*100)+"%"+'</span>';
	str    += '<span>投资期限：'+days+'天</span>';
	str    += '</p>';
	str    += '<p>';
	str    += '<span class="invest-price">项目本金：'+pro.expectAmount.value+'元</span>';
	if(state==1 || state==2)
		{
	str    += '<span>剩余可投金额：'+(parseInt(pro.expectAmount.value)- parseInt(pro.realAmount.value))+'元</span>';
		}else{
	str    += '<span>剩余可投金额：0元</span>';
		}
	str    += '</p>';
	str    += '</div>';
	str    += '</div>';
	
	if(state==1){
		str    += '<div class="operate-status online-status clearfix">';
		str    += '<div>';
		str    += '<div class="current-amount">';
		str    += '<strong>投资金额</strong>';
		str    += '<span class="cur">'+pro.minimum+'</span> 元起            </div>';
		if(parseInt(pro.expectAmount.value)==parseInt(pro.realAmount.value))
			str    += '<a target="_blank" href="productdetail.html?pid='+pro.id+'" class="btns">售罄</a>';
		else
			str    += '<a target="_blank" href="productdetail.html?pid='+pro.id+'" class="btns">投资中</a>';
		str    += '<div style="clear:both;"></div>';
		str    += '</div>';
		str    += '<div class="publish-time" style="margin-top:10px; padding-top:5px;">'+formatDateToDay(order.financingEndtime)+'截止</div>';
	}else if(state==2){
		str    += '<div class="operate-status done-status clearfix">';
		str    += '<div>';
		str    += '<div class="current-amount">';
		str    += '<strong>投资金额</strong>';
		str    += '<span class="cur">'+pro.minimum+'</span> 元起            </div>';
		str    += '<a target="_blank" href="productdetail.html?pid='+pro.id+'" class="btns-green">预发布</a>';
		str    += '<div style="clear:both;"></div>';
		str    += '</div>';
		str    += '<div class="publish-time" style="margin-top:10px; padding-top:5px;">'+formatDateToDay(order.financingStarttime)+'开始</div>';	
	}else if(state==4){
		str    += '<div class="operate-status done-status clearfix">';
		str    += '<div>';
		str    += '<div class="current-amount">';
		str    += '<strong>已融金额</strong>';
		str    += '<span class="cur">'+pro.realAmount.value+'</span> 元           </div>';
		str    += '<a target="_blank" href="productdetail.html?pid='+pro.id+'" class="btns-grey">还款中</a>';
		str    += '<div style="clear:both;"></div>';
		str    += '</div>';
		str    += '<div class="publish-time" style="margin-top:10px; padding-top:5px;">'+formatDateToDay(order.incomeStarttime)+'开始还款</div>';	
	}else if(state==8){
		str    += '<div class="operate-status done-status clearfix">';
		str    += '<div>';
		str    += '<div class="current-amount">';
		str    += '<strong>已融金额</strong>';
		str    += '<span class="cur">'+pro.realAmount.value+'</span> 元           </div>';
		str    += '<a target="_blank" href="productdetail.html?pid='+pro.id+'" class="btns-grey">待关闭</a>';
		str    += '<div style="clear:both;"></div>';
		str    += '</div>';
		str    += '<div class="publish-time" style="margin-top:10px; padding-top:5px;">'+formatDateToDay(order.financingEndtime)+'还款完毕</div>';	
	}else if(state==16){
		str    += '<div class="operate-status done-status clearfix">';
		str    += '<div>';
		str    += '<div class="current-amount">';
		str    += '<strong>流标</strong>';
		str    += '</div>';
		str    += '<a target="_blank" href="productdetail.html?pid='+pro.id+'" class="btns-grey">流标</a>';
		str    += '<div style="clear:both;"></div>';
		str    += '</div>';
		str    += '<div class="publish-time" style="margin-top:10px; padding-top:5px;">'+formatDateToDay(order.lastModifytime)+'流标</div>';	
	}else if(state==32){
		str    += '<div class="operate-status done-status clearfix">';
		str    += '<div>';
		str    += '<div class="current-amount">';
		str    += '<strong>已融金额</strong>';
		str    += '<span class="cur">'+pro.realAmount.value+'</span> 元           </div>';
		str    += '<a target="_blank" href="productdetail.html?pid='+pro.id+'" class="btns-grey">已关闭</a>';
		str    += '<div style="clear:both;"></div>';
		str    += '</div>';
		str    += '<div class="publish-time" style="margin-top:10px; padding-top:5px;">'+formatDateToDay(order.lastModifytime)+'关闭</div>';	
	}
	str    += '</div>';
	str    += '<div style="clear:both;"></div>';
	str    += '</li>';
	return str;
}










var createBorrowerNavLevel2 = function(nav){
	var ul = $('<ul class="nav nav-second navbar-nav navbar-right"></ul>');
	if(nav=='mycenter'){
		
		var li2 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="my-score">积分等级</a></li>');
		var li3 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="notice-view">系统公告</a></li>');
		var li5 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="letter-unread-mycenter">站内信</a></li>');
		var li6 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="question-view">我的问题</a></li>');
		
		ul.append(li2);
		ul.append(li3);
		ul.append(li5);
		ul.append(li6);
	}else if(nav=='request'){
		var li1 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="request-all">全部</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="request-tohandle">待处理</a></li>');
		var li3 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="request-pass">审核通过</a></li>');
		var li4 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="request-refuse">拒绝</a></li>');
		ul.append(li1);
		ul.append(li2);
		ul.append(li3);
		ul.append(li4);
	}else if(nav=='payback'){
		var li1 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="payback-all">总览</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="payback-to">待还款</a></li>');
		var li3 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="payback-have">已还款</a></li>');
		
		var li4 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="payback-canpay">可执行还款</a></li>');
		var li5 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="payback-canapply">可申请提前</a></li>');
		var li6 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="payback-toaudit">审核中还款</a></li>');
		ul.append(li1);
		ul.append(li2);
		ul.append(li3);
		ul.append(li6);
		ul.append(li4);
		ul.append(li5);
	}else if(nav=='cash'){
		var li1 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="cash-all">全部</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="cash-recharge">充值</a></li>');
		var li3 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="cash-withdraw">提现</a></li>');
		var li4 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="cash-financing">融资</a></li>');
		var li5 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="cash-payback">还款</a></li>');
		ul.append(li1);
		ul.append(li2);
		ul.append(li3);
		ul.append(li4);
		ul.append(li5);
	}else if(nav=='order'){
		var li1 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="order-all">全部</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="order-preview">预览</a></li>');
		var li3 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="order-financing">融资中</a></li>');
		var li4 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="order-paying">还款中</a></li>');
		var li5 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="order-toclose">待关闭</a></li>');
		var li6 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="order-closed">已关闭</a></li>');
		var li7 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="order-quit">流标</a></li>');
		ul.append(li1);
		ul.append(li2);
		ul.append(li3);
		ul.append(li4);
		ul.append(li5);
		ul.append(li6);
		ul.append(li7);
	}
	
	return ul;
}
var createNavLevel2 = function(nav){
	var ul = $('<ul class="nav nav-second navbar-nav navbar-right"></ul>');
	if(nav=='mycenter'){
		//lettercount = letterDao.countByReceiver(0, 0, cuser.id);
		var li1 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="my-material">个人资料</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="my-score">积分等级</a></li>');
		var li3 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="my-activity">我的活动</a></li>');
		var li4 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="letter-unread-mycenter">站内信('+lettercount+')</a></li>');
		var li5 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="notice-view">系统公告</a></li>');
		var li6 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="question-view">我的问题</a></li>');
		
		
	//	var li4 = $('<li role="presentation" class="dropdown"></li>');
	//	li4.append('<a class="dropdown-toggle" data-toggle="dropdown" data-sk="recommend" href="#">投资推荐<span class="caret"></span></a>');
	//	var ul2 = $('<ul class="dropdown-menu" role="menu"></ul>');
	//	ul2.append('<li><a href="#" data-sk="recommend-wj">稳健型</a></li>');
	//	ul2.append('<li><a href="#" data-sk="recommend-jh">均衡型</a></li>');
	//	ul2.append('<li><a href="#" data-sk="recommend-jq">进取型</a></li>');
	//	li4.append(ul2);
		ul.append(li1);
		ul.append(li2);
		ul.append(li3);
		ul.append(li4);
		ul.append(li5);
		ul.append(li6);
	}else if(nav=='submit'){
		var li1 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="submit-all">全部</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="submit-toafford">待支付</a></li>');
//		var li3 = $('<li role="presentation"><a href="#" data-sk="submit-toaudit">待审核</a></li>');
		var li4 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="submit-payback">还款中</a></li>');
		var li5 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="submit-done">还款完毕</a></li>');
		var li6 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="submit-retreat">已退订</a></li>');
		ul.append(li1);
		ul.append(li2);
//		ul.append(li3);
		ul.append(li4);
		ul.append(li5);
		ul.append(li6);
	}else if(nav=='payback'){
		var li1 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="payback-all">总览</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="payback-to">待回款</a></li>');
		var li3 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="payback-have">已回款</a></li>');
		ul.append(li1);
		ul.append(li2);
		ul.append(li3);
	}else if(nav=='cash'){
		var li1 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="cash-all">全部</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="cash-recharge">充值</a></li>');
		var li3 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="cash-withdraw">提现</a></li>');
		var li4 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="cash-invest">投标</a></li>');
		var li5 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="cash-receive">回款</a></li>');
		ul.append(li1);
		ul.append(li2);
		ul.append(li3);
		ul.append(li4);
		ul.append(li5);
	}else if(nav=='tools'){
		var li1 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="tools-fluxility">流动性分析</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="tools-receive-statistics">回款统计</a></li>');
		var li3 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="tools-analysis">风险分析</a></li>');
		var li4 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="tools-rate">收益率统计</a></li>');
		ul.append(li1);
		ul.append(li2);
		ul.append(li3);
		ul.append(li4);
	}else if(nav='letter'){
		var li1 = $('<li role="presentation" class="active"><a href="javascript:void(0)" data-sk="letter-unread">未读</a></li>');
		var li2 = $('<li role="presentation"><a href="javascript:void(0)" data-sk="letter-readed">已读</a></li>');
		ul.append(li1);
		ul.append(li2);
	}
	
	return ul;
}
