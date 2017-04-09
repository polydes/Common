package com.polydes.common.res;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;

public class FilesystemResources extends Resources
{
	private File rootPath;
	
	FilesystemResources(String packageName, File rootPath)
	{
		super(packageName);
		this.rootPath = rootPath;
	}
	
	@Override
	public URL getUrl(String name)
	{
		try
		{
			return new File(rootPath, name).toURI().toURL();
		}
		catch(MalformedURLException e)
		{
			return null;
		}
	}
	
	@Override
	public InputStream getUrlStream(String name)
	{
		try
		{
			return Files.newInputStream(new File(rootPath, name).toPath());
		}
		catch(IOException e)
		{
			return null;
		}
	}
}
