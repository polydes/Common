package com.polydes.common.res;

import java.util.HashMap;

public class ResourceLoader
{
	private static HashMap<String, Resources> resourcePacks = new HashMap<>();
	
	public static Resources getResources(String packageName)
	{
		if(!resourcePacks.containsKey(packageName))
			resourcePacks.put(packageName, new Resources(packageName));
		
		return resourcePacks.get(packageName);
	}
}
