package com.easyservice.xml;

import java.util.Map;

public class MapEntry<K, V> implements Map.Entry<K, V>{
	
	K key;
	V value;
	

	public void setKey(K key){
		this.key = key;
	}
	
	public K getKey() {
		return key;
	}
	
	public V setValue(V value) {
		V old = this.value;
		this.value = value;
		return old;
	}

	public V getValue() {
		return value;
	}

}
