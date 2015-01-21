package com.easyservice.support;

public interface EasyServiceConstant {
	String SESSION_ATTRIBUTENAME_USER="user";
	int USER_ANONYMOUS=-1;
	
	
	String PROTOCOL =  "protocol";
	String PROTOCOL_ES_WS = "ws"; //
	String PROTOCOL_BINGDING_HESSIAN = "hessian";
	String PROTOCOL_BINGDING_REST = "rest";
	String PROTOCOL_BINGDING_JSON = "json"; //
	String PROTOCOL_TRANSPORT_XHTTP = "xhttp";
	String PROTOCOL_TRANSPORT_HTTP = "http"; //
	String PROTOCOL_TRANSPORT_HTTPS = "https";
	String ES_PATH_PATTERN =  "/easyservice";
	String ES_CAPTCHA_PATH_PATTERN =  "/easycaptcha";
	String ES_PROTOCOL_FULLNAME = ".pf"; //
	
	String I18n_httpTransport_listenAt = "httpTransport.listenAt";
	String ES_CAPTCHA_listenAt = "easycaptcha.listenAt";
	
	String CONN_READ_TIMEOUT = "$conn_read_timeout";
	String CONN_CONNECT_TIMEOUT = "$conn_connect_timeout";
}
