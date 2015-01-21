package com.easyservice.support;

import static com.easyservice.support.EasyServiceConstant.ES_PATH_PATTERN;
import static com.easyservice.support.EasyServiceConstant.ES_PROTOCOL_FULLNAME;
import static com.easyservice.support.EasyServiceConstant.PROTOCOL_BINGDING_JSON;
import static com.easyservice.support.EasyServiceConstant.PROTOCOL_BINGDING_REST;
import static com.easyservice.support.EasyServiceConstant.PROTOCOL_ES_WS;
import static com.easyservice.support.EasyServiceConstant.PROTOCOL_TRANSPORT_HTTP;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpParse {

	protected HttpURLConnection realConnection;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected String serviceProtocol;// = PROTOCOL_ES_WS;
	protected String bindingProtocol;// = PROTOCOL_BINGDING_HESSIAN;
	protected String transportProtocol;// = PROTOCOL_ES_WS;
	protected String interfaceName;
	protected Map<String, Object> attributeMap = new HashMap<String, Object>();
	protected ServiceURL requestURL;

	protected Locale locale;

	// sdl Service Description Language
	protected boolean isFetchSdl = false;

	public ServiceURL getRequestURL() {
		return requestURL;
	}

	public HttpParse(HttpServletRequest request, HttpServletResponse response)
			throws MalformedURLException {
		this.request = request;
		this.response = response;
		String pathInfo = request.getServletPath();
		pathInfo=pathInfo.substring(ES_PATH_PATTERN.length());
		int itfend = pathInfo.indexOf('/', 1);
		if (itfend < 0) {
			itfend = pathInfo.length();
		}
		interfaceName = pathInfo.substring(1, itfend);
		String queryString = request.getQueryString();
		if (queryString != null
				&& queryString.startsWith(PROTOCOL_BINGDING_JSON)) {
			isFetchSdl = true;
			serviceProtocol = PROTOCOL_ES_WS;
			bindingProtocol = PROTOCOL_BINGDING_JSON;
			transportProtocol = PROTOCOL_TRANSPORT_HTTP;
		} else {
			String ctxType = getRequestAttribute("Content-Type");
			String ptstr = null;
			if (ctxType == null || !ctxType.startsWith("x-application/es:")) {
				ptstr = request.getParameter(ES_PROTOCOL_FULLNAME);
				if (ptstr != null && ptstr.length() > 0) {
					if (ptstr.startsWith("es:")) {
						ptstr = ptstr.substring(3); // "remove es:"
					}
				} else { // search in attribute
					ptstr = (String) request.getAttribute(ES_PROTOCOL_FULLNAME);
					if (ptstr != null && ptstr.length() > 0) {
						ptstr = ptstr.substring(3); // "remove es:"
					}
				}
			} else {
				ptstr = ctxType.substring("x-application/es:".length());
				int mpos = ptstr.indexOf(';');
				if (mpos > 0) {
					ptstr = ptstr.substring(0, mpos).trim();
				}
			}
			if (ptstr != null && ptstr.length() > 0) {

				String[] pts = ptstr.split("\\-");
				if (pts.length == 1) {
					bindingProtocol = pts[0];
					serviceProtocol = PROTOCOL_ES_WS;
					transportProtocol = PROTOCOL_TRANSPORT_HTTP;
				} else if (pts.length < 3) {
					throw new MalformedURLException("wrong protocol format :"
							+ ptstr);
				} else {
					serviceProtocol = pts[0];
					bindingProtocol = pts[1];
					transportProtocol = pts[2];
				}

			} else {
				// use default value
				bindingProtocol = PROTOCOL_BINGDING_REST;
				serviceProtocol = PROTOCOL_ES_WS;
				transportProtocol = PROTOCOL_TRANSPORT_HTTP;
			}
		}
		StringBuilder transportUrl = new StringBuilder(transportProtocol).append("://").append(request.getHeader("Host")).append(request.getRequestURI().substring(0,request.getRequestURI().indexOf(interfaceName)+interfaceName.length()));
		requestURL = new ServiceURL(serviceProtocol, bindingProtocol,
				transportUrl.toString());
	}
	public <T> void setRequestAttribute(String key, T value) {
		if (request != null){
			request.setAttribute(key, value);
		}else{
			realConnection.setRequestProperty(key, value.toString());
		}
	}
	public <T> T getRequestAttribute(String key) {
		if (request != null) {
			T rt = (T) request.getHeader(key);
			if (rt == null) {
				rt = (T) request.getAttribute(key);
			}
			return rt;
		} else {
			return (T) realConnection.getRequestProperty(key);
		}
	}

	public boolean isFetchSdl() {
		return isFetchSdl;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public String getInterfaceName() {
		return interfaceName;
	}
	public <T> T getAttribute(String key) {
		return (T)attributeMap.get(key);
	}

	public <T> void setAttribute(String key, T value) {
		attributeMap.put(key, value);
	}

	public String getServiceProtocol() {
		return serviceProtocol;
	}

	public String getBindingProtocol() {
		return bindingProtocol;
	}

	public String getTransportProtocol() {
		return transportProtocol;
	}
	
	
}
