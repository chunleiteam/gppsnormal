package com.easyservice.service;

import java.io.IOException;

import com.easyservice.exception.ProtocolParseException;
import com.easyservice.support.HttpParse;
import com.easyservice.support.ServiceRequest;
import com.easyservice.support.ServiceResponse;

public interface IProtocolBinding {
	public ServiceRequest getRequest(HttpParse parse) throws ProtocolParseException;
	public void replyResponse(HttpParse parse, ServiceResponse resp) throws IOException;
	public String getProtocol();
	public Class getInterfaceClass(String name) throws ClassNotFoundException;
}
