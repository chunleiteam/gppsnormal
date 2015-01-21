package com.easyservice.service;

public interface IRemoteServiceRegister {
	public void register(String protocol, IProtocolBinding binding);
	
	public void unregister(String protocol, IProtocolBinding binding);

	public IProtocolBinding lookupBinding(String protocol);
}
