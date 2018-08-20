package com.sakha.s20.ai.ModelGenerator;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefaultDict<K, V> extends HashMap<K, V> {

	Class<V> klass;

	public DefaultDict(Class klass) {
		this.klass = klass;
	}

	@SuppressWarnings("deprecation")
	@Override
	public V get(Object key) {
		V returnValue = super.get(key);
		if (returnValue == null) {
			try {
				returnValue = klass.newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			this.put((K) key, returnValue);
		}
		return returnValue;
	}

	public static void main(String[] args) {
		DefaultDict<Integer, List<Integer>> dict = new DefaultDict<Integer, List<Integer>>(ArrayList.class);
		dict.get("Hello");
		dict.get(1).add(3);
		System.out.println(dict);
	}
}