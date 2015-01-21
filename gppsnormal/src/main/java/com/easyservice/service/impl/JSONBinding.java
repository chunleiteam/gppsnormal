package com.easyservice.service.impl;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.zip.Deflater;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.easyservice.HttpTransport;
import com.easyservice.ejson.EJSONDeserializeException;
import com.easyservice.ejson.EJsonConsts;
import com.easyservice.ejson.EJsonException;
import com.easyservice.ejson.EJsonManager;
import com.easyservice.ejson.EJsonReader;
import com.easyservice.ejson.EJsonWriter;
import com.easyservice.exception.ProtocolParseException;
import com.easyservice.service.IProtocolBinding;
import com.easyservice.service.IRemoteServiceRegister;
import com.easyservice.support.ClassInfo;
import com.easyservice.support.EasyServiceConstant;
import com.easyservice.support.GZIPOutputStreamEx;
import com.easyservice.support.HttpParse;
import com.easyservice.support.JSONRequest;
import com.easyservice.support.MangleClassInfo;
import com.easyservice.support.MangleClassInfoPool;
import com.easyservice.support.ServiceRequest;
import com.easyservice.support.ServiceResponse;
import com.easyservice.support.ServiceResponse.ExceptionType;

@Component("JSONBinding")
public class JSONBinding implements IProtocolBinding, BeanPostProcessor {

	static MangleClassInfoPool mangleClassInfoPool = MangleClassInfoPool
			.getDefault();

	static int buf_size = 1024;

	static String PRETTY_FORMAT = ".p"; // true, false, default false;

	EJsonManager eJsonManager = EJsonManager.getDefaultManager();

	protected static final String HEADER_ENCODING = "Content-Encoding";

	static String TARGET_PARAM = ".t";

	static String TENANTID_PARAM = ".tenantId";
	@Autowired
	IRemoteServiceRegister register;
	static Logger log = Logger.getLogger(JSONBinding.class);

	@Override
	public ServiceRequest getRequest(HttpParse httpParse)
			throws ProtocolParseException {
		HttpServletRequest request = httpParse.getRequest();
		String clz = httpParse.getInterfaceName();
		httpParse.setAttribute(PRETTY_FORMAT,
				request.getParameter(PRETTY_FORMAT));
		MangleClassInfo mci = null;// clz2MethodMangleMap.get(clz);
		try {
			mci = mangleClassInfoPool.getMangleClassInfo(clz);
		} catch (ClassNotFoundException e) {
			throw new ProtocolParseException(e,
					ExceptionType.ET_SE_METHOD_NOT_FOUND);
		}
		String charset = request.getCharacterEncoding();
		if (charset == null) {
			charset = "UTF-8";
		}
		String receiveString = request.getParameter("request");
		if (receiveString == null) {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						request.getInputStream(), charset));
				// Read the request
				CharArrayWriter data = new CharArrayWriter();
				char buf[] = new char[buf_size];
				int ret;
				while ((ret = in.read(buf, 0, buf_size)) != -1) {
					data.write(buf, 0, ret);
				}

				receiveString = data.toString();
			} catch (UnsupportedEncodingException e) {
			} catch (IOException e) {
				throw new ProtocolParseException(e, ExceptionType.ET_SE_BADREQ);
			}
		}
		// dump the received string
		if (log.isDebugEnabled()) {
			log.debug("recv: " + receiveString);
			// log.debug("recv: " + prettyPrintJson(receiveString));
		}
		// Process the request
		JSONRequest jsonReq = new JSONRequest();
		Method method = null;
		Class<?>[] paramTypes = null;
		Type[] gparamTypes = null;
		try {
			EJsonReader tokener = new EJsonReader(receiveString);
			tokener.match('{', true);
			String property = tokener.readNextString(':');
			if (!property.equals(EJsonConsts.ID_STRING)
					&& !property.equals(EJsonConsts.REFID_STRING)) {
				throw new EJSONDeserializeException("expect: id, but meet :"
						+ property);
			}
			int jsid = tokener.readNextInt();
			int cur = tokener.readNext();
			if (cur != ',') {
				throw new EJSONDeserializeException("expect: ,, but meet :"
						+ (char) cur);
			}
			while ((property = tokener.readNextString(':')) != null) {
				if (property.equals("id")) {
					jsonReq.setId(tokener.readNextQuoteString());
				} else if (property.equals("method")) {
					jsonReq.setMethod(tokener.readNextQuoteString());
					method = mci.getMethod(jsonReq.getMethod());
					if (method == null) {
						throw new ProtocolParseException(
								new NoSuchMethodException(jsonReq.getMethod()),
								ExceptionType.ET_SE_WRONG_PARAMTYPE);
					}
					paramTypes = method.getParameterTypes();
					gparamTypes = method.getGenericParameterTypes();
					jsonReq.setParams(new Object[paramTypes.length]);
				} else if (property.equals("params")) {
					try {
						int index = 0;
						tokener.match('[', true);
						int next = tokener.LA(1, true);
						if (next == ']') { // empty array
							tokener.match(']', true);
						} else {
							while (true) {
								Class ptype = paramTypes[index];
								Class[] itemTypes = ClassInfo.getItemTypes(
										ptype, gparamTypes[index], null);
								jsonReq.getParams()[index++] = tokener.read(
										ptype, itemTypes);
								next = tokener.LA(1, true);
								if (next == ',') {
									tokener.match(',', true);
									continue;
								} else if (next == ']') {
									tokener.match(']', true);
									break;
								} else {
									throw new EJSONDeserializeException(
											"unexpected array element");
								}
							}
						}
					} catch (Exception e) {
						if (e instanceof EJsonException) {
							throw (EJsonException) e;
						}
						throw new EJSONDeserializeException(e);
					}
				} else if (property.equals("callback")) {
					jsonReq.setCallback(tokener.readNextQuoteString());
				} else {
					throw new EJSONDeserializeException("unknow property: "
							+ property);
				}
				cur = tokener.readNext();
				if (cur == '}') {
					break;
				} else if (cur == ',') {
					continue;
				} else {
					throw new EJSONDeserializeException("unexcepted "
							+ new Character((char) cur));
				}
			}
		} catch (EJsonException e) {
			if (log.isDebugEnabled()) {
				log.error("can't parse call" + receiveString, e);
			}
			throw new ProtocolParseException(e, ExceptionType.ET_SE_BADREQ);
		}
		ServiceRequest req = new ServiceRequest();
		req.setHttpParse(httpParse);
		String encodedMethod;
		String requestId;
		Object[] arguments;
		String callback;
		// Get method name, arguments and request id
		encodedMethod = jsonReq.getMethod();
		arguments = jsonReq.getParams();
		requestId = jsonReq.getId();
		callback = jsonReq.getCallback();
		req.getHttpParse().setAttribute("requestId", requestId);
		req.getHttpParse().setAttribute("callback", callback);

		if (log.isDebugEnabled()) {
			log.debug("call " + encodedMethod + "(" + arguments
					+ ") requestId=" + requestId);
		}

		String className = null;
		String methodName = null;
		int objectID = 0;
		methodName = encodedMethod;

		req.setInterfacClass(mci.getType());
		req.setMethod(method);
		req.setArgs(arguments);
		req.setTarget(request.getParameter(TARGET_PARAM));
		req.setAttribute(TENANTID_PARAM, request.getParameter(TENANTID_PARAM));
		return req;
	}

	@Override
	public void replyResponse(HttpParse httpParse, ServiceResponse resp)
			throws IOException {
		HttpServletResponse sr = httpParse.getResponse();
		sr.setHeader(HEADER_ENCODING, "gzip");
		sr.setCharacterEncoding("utf-8");
		sr.setContentType("text/plain");
		sr.setStatus(200);
		String reqId = httpParse.getAttribute("requestId");
		String callback = httpParse.getAttribute("callback");
		String prettyFormat = httpParse.getAttribute(PRETTY_FORMAT);
		resp.setId(reqId);
		EJsonWriter writer = new EJsonWriter();

		String respstr = writer.write(resp);

		StringBuilder rt = new StringBuilder();
		if (callback != null) {
			rt.append(callback).append("(\"").append(reqId).append("\",");
		}
		if (prettyFormat == null || prettyFormat.equals("false")) {
			rt.append(respstr);
		} else {
			rt.append(respstr);
		}
		if (callback != null) {
			rt.append(");");
		}
		GZIPOutputStreamEx gout = new GZIPOutputStreamEx(sr.getOutputStream(),
				Deflater.BEST_SPEED);
		gout.write(rt.toString().getBytes("utf-8"));
		gout.close();
	}

	@Override
	public String getProtocol() {
		return EasyServiceConstant.PROTOCOL_BINGDING_JSON;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		register.register(getProtocol(), this);
		return bean;
	}

	@Override
	public Class getInterfaceClass(String name) throws ClassNotFoundException {
		MangleClassInfo mci = mangleClassInfoPool.getMangleClassInfo(name);
		return mci.getType();
	}
}
