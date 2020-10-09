package com.polydes.common.data.types;

import java.util.HashMap;

public class EditorProperties extends HashMap<String, Object>
{
	@SuppressWarnings("unchecked")
	public <T,U extends T> T put(PropertyKey<T> key, U value)
	{
		return (T) put(key.id, value);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(PropertyKey<T> key)
	{
		return (T) get(key.id);
	}

	public void remove(PropertyKey<?> key)
	{
		remove(key.id);
	}
	
	public boolean containsKey(PropertyKey<?> key)
	{
		return containsKey(key.id);
	}
}
