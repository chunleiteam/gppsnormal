package com.easyservice.support;

import java.net.MalformedURLException;
import static com.easyservice.support.EasyServiceConstant.*;

public class ServiceURL {
	
	String url;
	String serviceProtocol = EasyServiceConstant.PROTOCOL_ES_WS;
	String bindingProtocol = PROTOCOL_BINGDING_HESSIAN;
	String transportProtocol = PROTOCOL_TRANSPORT_HTTP;
	String transportUrl;
	String interfaceName;
	String host;
	int port;
	boolean methodPathBuildin;
	
	public ServiceURL(String serviceProtocol, String bindingProtocol, String transportUrl, String localInterface) throws MalformedURLException {
		if (localInterface != null){
			this.setInterfaceName(localInterface);
		}
		this.serviceProtocol = serviceProtocol;
		this.bindingProtocol = bindingProtocol;
		this.transportUrl = transportUrl;
		this.transportProtocol = transportUrl.substring(0, transportUrl.indexOf("://"));
		url = "es:" + serviceProtocol+"-" + bindingProtocol + "-" + transportUrl;
		int i = -1;
		int sport = -1;
		if(transportUrl.indexOf("://[")>0){//ipv6 address
			i = url.indexOf("://[")+1;
			sport = url.indexOf("]:",i+3);
			int eport = url.indexOf('/', sport);
			if (eport > 0 && sport > 0){
				try{
					port = Integer.parseInt(url.substring(sport+2, eport));
				}catch (Exception e) {
					throw new MalformedURLException(url +" caused by: " + e.getMessage()); 
				}
			}else{
				port = 80;
			}			
		}else{//ipv4 address or domain
			i = url.indexOf("://");
			sport = url.indexOf(':', i+3);
			int eport = url.indexOf('/', sport);
			if (eport > 0 && sport >0){
				try{
					port = Integer.parseInt(url.substring(sport+1, eport));
				}catch (Exception e) {
					throw new MalformedURLException(url +" caused by: " + e.getMessage()); 
				}
			}else{
				port = 80;
			}
		}
		if (sport > 0){
			host = url.substring(i+3, sport);
		}else{
			host = url.substring(i+3, url.indexOf( '/', i+3));
		}
		if (localInterface == null){
			i =  transportUrl.lastIndexOf('/');
			interfaceName = transportUrl.substring(i+1);
		}
	}
	
	public ServiceURL(String serviceProtocol, String bindingProtocol, String transportUrl) throws MalformedURLException {
		this(serviceProtocol, bindingProtocol, transportUrl, null);
	}

	//es:ws-hassian-http://localhost:80/es/IHello
	public ServiceURL(String url) throws MalformedURLException{
		this.url = url;
		String[] ps =  url.split("\\:");
		if (ps.length < 3){
			throw new MalformedURLException("lack protocol: " + url + " , eg. es:ws-hassian-http://localhost:80/easyservice/edu.caliss.test.IHello");
		}
		if (!ps[0].equals("es")){
			throw new MalformedURLException(url + ": not start with " + "es:");
		}
		String subp = ps[1];
		String[] subs = subp.split("\\-");
		if (subs.length < 3){
			throw new MalformedURLException("lack protocol: " + url + " , eg. es:ws-hassian-http://localhost:80/easyservice/edu.caliss.test.IHello");
		}
		serviceProtocol = subs[0];
		bindingProtocol = subs[1];
		transportProtocol = subs[2];
		int i = url.indexOf("://");
		transportUrl = transportProtocol+ url.substring(i);
		int sport = -1;
		if(url.indexOf("://[")>0){//ipv6
			i = url.indexOf("://[")+1;
			sport = url.indexOf("]:", i+3);
			int eport = url.indexOf('/', sport);
			if (eport > 0 && sport >0){
				try{
					port = Integer.parseInt(url.substring(sport+2, eport));
				}catch (Exception e) {
					throw new MalformedURLException(url +" caused by: " + e.getMessage()); 
				}
				
			}else{
				port = 80;
			}
		}else{//ipv4 or domain
			sport = url.indexOf(':', i+3);
			int eport = url.indexOf('/', sport);
			if (eport > 0 && sport > 0){
				try{
					port = Integer.parseInt(url.substring(sport+1, eport));
				}catch (Exception e) {
					throw new MalformedURLException(url +" caused by: " + e.getMessage()); 
				}
				
			}else{
				port = 80;
			}
		}
		if (sport > 0){
			host = url.substring(i+3, sport);
		}else{
			host = url.substring(i+3, url.indexOf( '/', i+3));
		}
		i =  transportUrl.lastIndexOf('/');
		interfaceName = transportUrl.substring(i+1);
	}
	
	public String getBindingProtocol() {
		return bindingProtocol;
	}

	public void setBindingProtocol(String bindingProtocol) {
		this.bindingProtocol = bindingProtocol;
	}

	public String getServiceProtocol() {
		return serviceProtocol;
	}

	public void setServiceProtocol(String serviceProtocol) {
		this.serviceProtocol = serviceProtocol;
	}

	public String getTransportProtocol() {
		return transportProtocol;
	}

	public void setTransportProtocol(String transportProtocol) {
		this.transportProtocol = transportProtocol;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getTransportUrl() {
		return transportUrl;
	}

	public void setTransportUrl(String transportUrl) {
		this.transportUrl = transportUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isMethodPathBuildin() {
		return methodPathBuildin;
	}

	public void setMethodPathBuildin(boolean methodPathBuildin) {
		this.methodPathBuildin = methodPathBuildin;
	}
	
	@Override
	public String toString() {
		return "[" + serviceProtocol + "," + bindingProtocol + "," + transportProtocol +  "," + host + "," + port + "," + interfaceName + "," + transportUrl + "]";
	}

	/** TEST **/
	public static void main(String[] args) {
		try {//ipv4
			ServiceURL serviceURL1 = new ServiceURL("ws","hessian","http://localhost/easyservice/com.cnebula.common.security.auth.ILoginService",null);
			ServiceURL serviceURL2 = new ServiceURL("es:ws-hessian-http://localhost/easyservice/com.cnebula.common.security.auth.ILoginService");
			ServiceURL serviceURL3 = new ServiceURL("ws","hessian","http://localhost:8686/easyservice/com.cnebula.common.security.auth.ILoginService",null);
			ServiceURL serviceURL4 = new ServiceURL("es:ws-hessian-http://localhost:8686/easyservice/com.cnebula.common.security.auth.ILoginService");
			System.out.println(serviceURL1);
			System.out.println(serviceURL2);
			System.out.println(serviceURL3);
			System.out.println(serviceURL4);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		try {//ipv6
			ServiceURL serviceURL1 = new ServiceURL("ws","hessian","http://[2001:da8:201:1b00:230:48ff:febf:b20c]/easyservice/com.cnebula.common.security.auth.ILoginService",null);
			ServiceURL serviceURL2 = new ServiceURL("es:ws-hessian-http://[2001:da8:201:1b00:230:48ff:febf:b20c]/easyservice/com.cnebula.common.security.auth.ILoginService");
			ServiceURL serviceURL3 = new ServiceURL("ws","hessian","http://[2001:da8:201:1b00:230:48ff:febf:b20c]:8686/easyservice/com.cnebula.common.security.auth.ILoginService",null);
			ServiceURL serviceURL4 = new ServiceURL("es:ws-hessian-http://[2001:da8:201:1b00:230:48ff:febf:b20c]:8686/easyservice/com.cnebula.common.security.auth.ILoginService");
			System.out.println(serviceURL1);
			System.out.println(serviceURL2);
			System.out.println(serviceURL3);
			System.out.println(serviceURL4);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}		
	}
}
