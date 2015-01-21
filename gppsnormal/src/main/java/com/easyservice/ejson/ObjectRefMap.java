package com.easyservice.ejson;

import java.util.HashMap;


public class ObjectRefMap {
	
	int index = 1 ; 
	int currentLevel = 0 ;
	int options = 0 ; 
	
	HashMap<Object, Integer> innerMap = new HashMap<Object, Integer>();
	
	public void addObjectRef(Object o){
		if(!innerMap.containsKey(o)){
			innerMap.put(o, index++);
		}
	}
	
	public Integer getRefId(Object o){
		return innerMap.get(o);
	}
	
	
	public boolean containRef(Object o){
		return innerMap.containsKey(o);
	}
	
	public void clear(){
		if(!innerMap.isEmpty()){
			innerMap.clear();
		}
		currentLevel = 0 ;
	}
	
	public boolean isDirty(){
		return (innerMap.size() != 0||currentLevel!=0) ;
	}
	
	public int getCurrentLevel(){
		return currentLevel ;
	}

	public int getOptions() {
		return options;
	}

	public void setOptions(int options) {
		this.options = options;
	}

	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}
	
	public int addCurrentLevel() {
		return ++this.currentLevel ;
	}
	
	public int minusCurrentLevel() {
		return --this.currentLevel ;
	}
	
	
}
