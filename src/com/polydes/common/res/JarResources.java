package com.polydes.common.res;

import java.io.InputStream;
import java.net.URL;

public class JarResources extends Resources
{
	private final String rootPath;
	
	JarResources(String packageName, String packageNameAsPath)
	{
		super(packageName);
		rootPath = packageNameAsPath;
	}
	
	@Override
	public URL getUrl(String name)
	{
		return getClass().getResource(rootPath + name);
	}
	
	@Override
	public InputStream getUrlStream(String name)
	{
		return getClass().getResourceAsStream(rootPath + name);
	}
}
