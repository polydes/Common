package com.polydes.common.res;

import java.io.File;
import java.util.HashMap;

public class ResourceLoader
{
	private static HashMap<String, Resources> resourcePacks = new HashMap<>();
	
	public static Resources getResources(String packageName)
	{
		if(!resourcePacks.containsKey(packageName))
		{
			String packageNameAsPath = packageName.replaceAll("\\.", "/") + "/";
			String packageNameAsVar = packageName.replaceAll("\\.", "_");
			
			String developmentPathLookup = "STENCYL_EXT_DEV_" + packageNameAsVar;
			
			if(System.getenv(developmentPathLookup) != null)
			{
				String devLocation = System.getenv(developmentPathLookup);
				resourcePacks.put(packageName, new FilesystemResources(packageName, new File(devLocation, "res")));
			}
			else
			{
				resourcePacks.put(packageName, new JarResources(packageName, "/res/" + packageNameAsPath));
			}
		}
		
		return resourcePacks.get(packageName);
	}
}
