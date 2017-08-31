package com.polydes.common.sys;

import java.io.File;

import javax.activation.MimetypesFileTypeMap;

public class Mime
{
	public static MimetypesFileTypeMap typemap = new MimetypesFileTypeMap();
	
	static
	{
		typemap.addMimeTypes("text/plain txt");
		typemap.addMimeTypes("text/xml xml");
		typemap.addMimeTypes("application/json json");
		typemap.addMimeTypes("image/png png");
		typemap.addMimeTypes("audio/mpeg3 mp3");
		typemap.addMimeTypes("audio/ogg ogg");
	}
	
	public static String get(File f)
	{
		return typemap.getContentType(f);
	}
	
	public enum BasicType
	{
		TEXT,
		IMAGE,
		AUDIO,
		BINARY
	}
	
	public static BasicType getType(File f)
	{
		String type = get(f);
		if(type.startsWith("text") || type.equals("application/json"))
		{
			return BasicType.TEXT;
		}
		else if(type.startsWith("image"))
		{
			return BasicType.IMAGE;
		}
		else if(type.startsWith("audio"))
		{
			return BasicType.AUDIO;
		}
		else
		{
			return BasicType.BINARY;
		}
	}
}
