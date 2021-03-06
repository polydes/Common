package com.polydes.common.ext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * <p>This class represents a registry of objects identified by a string key.</p>
 * 
 * <p>Since some keys may not be resolved to an object when the object is needed, this<br/>
 * class also provides a method for delaying the workload related to a certain object.</p>
 * 
 * <p>A registry object can be requested by using a Consumer object which will be called<br/>
 * when the registry object is ready. Until then, a placeholder object will be provided to<br/>
 * the caller.</p>
 * 
 * @author Justin Espedal
 * 
 * @param <T> the type of object this registry holds, implementing the RegistryObject interface
 */
public abstract class ObjectRegistry<T extends RegistryObject>
{
	private HashMap<String, T> map;
	
	public ObjectRegistry()
	{
		map = new HashMap<>();
	}
	
	/**
	 * A method that can be overridden to add behavior when
	 * a placeholder type is being added.
	 */
	protected void preregisterItem(T object)
	{
		map.put(object.getKey(), object);
	}
	
	public void registerItem(T object)
	{
		if(isUnknown(object.getKey()))
			realizeUnknown(object.getKey(), object);
		else if(map.containsKey(object.getKey()))
			throw new IllegalArgumentException("The same key cannot be claimed by two objects");
		else
			map.put(object.getKey(), object);
	}
	
	public void clearRegistry()
	{
		for(T object : new ArrayList<>(map.values()))
			unregisterItem(object);
	}
	
	public void unregisterItem(T object)
	{
		map.remove(object.getKey());
	}
	
	/**
	 * Convenience method for unregisterItem(Object)
	 */
	public void unregisterItem(String key)
	{
		T value = map.get(key);
		if(value != null)
			unregisterItem(value);
	}
	
	public boolean hasItem(String key)
	{
		return map.containsKey(key);
	}
	
	public T getItem(String key)
	{
		if(map.get(key) == null)
			throw new RuntimeException("Null value from " + getClass().getSimpleName() + "!: " + key);
		
		return map.get(key);
	}
	
	public void renameItem(T value, String newName)
	{
		map.put(newName, map.remove(value.getKey()));
		value.setKey(newName);
	}
	
	/**
	 * Convenience method for renameItem(Object, String)
	 */
	public void renameItem(String oldName, String newName)
	{
		T value = map.get(oldName);
		if(value != null)
			renameItem(value, newName);
	}
	
	public Collection<T> values()
	{
		return map.values();
	}
	
	public void dispose()
	{
		for(T t : new ArrayList<>(map.values()))
			unregisterItem(t);
		map.clear();
		unknownValues.clear();
		roRealizers.clear();
		map = null;
		unknownValues = null;
		roRealizers = null;
	}
	
	/*-------------------------------------*\
	 * Unknown Objects
	\*-------------------------------------*/ 
	
	private HashMap<String, T> unknownValues = new HashMap<>();
	private HashMap<String, ArrayList<RORealizer<T>>> roRealizers = new HashMap<>();
	
	public T requestValue(String s, RORealizer<T> tr)
	{
		if(!map.containsKey(s) && !isUnknown(s))
			addUnknown(s);
		
		if(isUnknown(s))
			roRealizers.get(s).add(tr);
		else
			tr.realizeRO(map.get(s));
		
		return map.get(s);
	}
	
	public void addUnknown(String s)
	{
		T newObject = generatePlaceholder(s);
		unknownValues.put(s, newObject);
		roRealizers.put(s, new ArrayList<>());
		preregisterItem(newObject);
	}
	
	/**
	 * <p>Create a placeholder object that can safely be used until
	 * the desired data has been loaded.</>
	 * 
	 * <p>Calling the getKey method on the new object should
	 * return the value of this function's key argument.</p>
	 */
	public abstract T generatePlaceholder(String key);
	
	public boolean isUnknown(String s)
	{
		return unknownValues.containsKey(s);
	}
	
	public void realizeUnknown(String s, T value)
	{
		
		if(!unknownValues.containsKey(s))
			throw new IllegalArgumentException("There is no unknown object \"" + s + "\"");
		unknownValues.remove(s);
		
		for(RORealizer<T> ror : roRealizers.remove(s))
			ror.realizeRO(value);
		
		map.put(value.getKey(), value);
	}
}