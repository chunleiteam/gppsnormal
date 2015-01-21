

function header(title){
	
	var divusercontent = $('<div class="col-md-12" id="usercontent" style="background-color:#999; color:white; min-height:35px; padding-top:5px;"></div>');
	
	
	var divtitle = $('<div style="margin-top:5px; padding: 10px 10px 0px 10px;"></div>');
//	divtitle.append('<font style="font-size:30px; font-weight:bold; margin-left:15px; color:orange; font-family:SimHei" class="text-muted">政采贷</font><font>&nbsp;&nbsp;信用创造价值</font>');
	divtitle.append('<img src="img/logo.png" style="max-width:95%; margin-top:5px;"></img>');
	if(title=='login' || title=='register'){
		if(usertype=='lender')
		{
			window.location.href="myaccount.html";
		}else if(usertype=='borrower'){
			window.location.href="baccount.html";
		}
		else
			{
		divusercontent.css('display', 'none');
			}
	}else if(title=='myaccount'){
		divusercontent.css('display', 'none');
	}else if(title=='loan'){
		if(usertype=='lender'){
			alert('个人用户无法申请融资，请您先退出再申请！');
			window.location.href="myaccount.html";
		}else if(usertype=='borrower'){
			divusercontent.html(greet()+"企业用户"+user.loginId+'&nbsp;&nbsp;|&nbsp;&nbsp;<a href="quit.html" style="color:white;">退出</a>');
		}else{
			divusercontent.html('<a href="login.html" style="color:white;">登录</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href="register.html" style="color:white;">注册</a>');
		}
	}
	else{
	if(usertype=='lender'){
		divusercontent.html(greet()+user.loginId+'&nbsp;&nbsp;<a style="color:white;" href="myaccountdetail.html?fid=mycenter&sid=letter-unread-mycenter" id="inner_letter" target="_blank"><span class="glyphicon glyphicon-envelope" style="margin-left:10px; color=red"></span>&nbsp;'+lettercount+'</a>'+'&nbsp;&nbsp;|&nbsp;&nbsp;<a href="quit.html" style="color:white;">退出</a>');
	}else if(usertype=='borrower'){
		divusercontent.html(greet()+"企业用户"+user.loginId+'&nbsp;&nbsp;|&nbsp;&nbsp;<a href="quit.html" style="color:white;">退出</a>');
	}else{
		divusercontent.html('<a href="login.html" style="color:white;">登录</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href="register.html" style="color:white;">注册</a>');
	}
		}
	
	divusercontent.append('<div style="clear:both;"></div>');
	
	
	var navulstr = '<nav class="navbar navbar-default" role="navigation">';
	navulstr += '<div class="container-fluid">';
	navulstr += '<div class="navbar-header">';
	navulstr += '<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">';
	navulstr += '<span class="sr-only">Toggle navigation</span>';
	navulstr += '<span class="icon-bar"></span>';
	navulstr += '<span class="icon-bar"></span>';
	navulstr += '<span class="icon-bar"></span>';
	navulstr += '</button>';
	navulstr += '<span class="navbar-brand">政采贷</span>';
	navulstr += '</div>';
	navulstr += '<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">';
	navulstr += '<ul class="nav navbar-nav">';
	navulstr += '<li id="index" class="active"><a href="index.html">首页</a></li>';
	navulstr += '<li id="productlist"><a href="productlist.html">我要理财</a></li>';
	navulstr += '<li id="myaccount"><a href="myaccount.html">我的账户</a></li>';
//	navulstr += '<li id="activity"><a href="activity.html">活动中心</a></li>';
	navulstr += '<li id="loan"><a href="loan.html">我要融资</a></li>';
	navulstr += '</ul>';
	navulstr += '<ul class="nav navbar-nav navbar-right">';
//	if(cuser==null && buser==null)
//		{
//			navulstr += '<li><a href="login.html">登录</a></li>';
//			navulstr += '<li><a href="register.html">注册</a></li>';
//		}
	navulstr += '<li id="activity"><a href="activity.html">活动中心</a></li>';
	navulstr += '<li class="dropdown">';
	navulstr += '<a href="#" class="dropdown-toggle" data-toggle="dropdown">帮助<span class="caret"></span></a>';
	navulstr += '<ul class="dropdown-menu" role="menu">';
	navulstr += '<li><a href="helpcenter.html?type=0" target="_blank">新手帮助</a></li>';
	navulstr += '<li><a href="helpcenter.html?type=1" target="_blank">常见问题</a></li>';
	navulstr += '<li><a href="helpcenter.html?type=3" target="_blank">投资融资</a></li>';
	navulstr += '<li class="divider"></li>';
	navulstr += '<li><a href="noticecenter.html?type=0" target="_blank">平台公告</a></li>';
	navulstr += '<li><a href="newscenter.html?type=-1" target="_blank">新闻资讯</a></li>';
	navulstr += '</ul>';
	navulstr += '</li>';
	navulstr += '</ul>';
	navulstr += '</div><!-- /.navbar-collapse -->';
	navulstr += '</div><!-- /.container-fluid -->';
	navulstr += '</nav>';
	
	
	
	var navDiv = $('<div class="col-md-12" style="padding-left:0px; padding-right:0px;"></div>');
	navDiv.append(navulstr);
	
	$('div#header').html('');
	$('div#header').css('background-color', 'white');
	$('#header').append(divusercontent);
	$('#header').append(divtitle);
	$('#header').append(navDiv);
	$('li').removeClass('active');
	$('li[id="'+title+'"]').addClass('active');
}