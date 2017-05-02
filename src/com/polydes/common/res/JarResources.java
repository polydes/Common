package com.polydes.common.res;

import java.io.InputStream;
import java.net.URL;

import stencyl.sw.SW;

public class JarResources extends Resources
{
	private final ClassLoader cl;
	private final String rootPath;
	
	JarResources(String packageName, String packageNameAsPath)
	{
		super(packageName);
		
		ClassLoader resourceLoader = SW.get().getExtensionManager().getLoader();
		if(resourceLoader == null)
			resourceLoader = getClass().getClassLoader();
		cl = resourceLoader;
		
		rootPath = packageNameAsPath;
	}
	
	@Override
	public URL getUrl(String name)
	{
		return cl.getResource(rootPath + name);
	}
	
	@Override
	public InputStream getUrlStream(String name)
	{
		return cl.getResourceAsStream(rootPath + name);
	}
}
