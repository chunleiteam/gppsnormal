package com.easyservice.ejson;

public abstract class AbstractDeserializer<T> implements IEJsonDeserializer<T> {

	protected IEJsonIOManager manager = EJsonManager.getDefaultManager();
	
	public IEJsonIOManager getManager() {
		return manager;
	}

	public void setManager(IEJsonIOManager manager) {
		this.manager = manager;
	}
	
	public Class loadClass(String type, Class cls) {
		try{
			Class typeCls = Thread.currentThread().getContextClassLoader().loadClass(type);
			if(cls == null || typeCls.isAssignableFrom(cls)){
				return  typeCls; 
			}
			return cls;
		}catch (Exception e) {
			if (e instanceof EJSONDeserializeException){
				throw (EJSONDeserializeException)e;
			}
			throw new EJSONDeserializeException("type no found",e);
		}
	}
	
}
