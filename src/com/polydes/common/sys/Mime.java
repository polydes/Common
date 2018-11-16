package com.polydes.common.sys;

import java.io.File;

import org.apache.commons.io.FilenameUtils;

public class Mime
{
	public enum BasicType
	{
		TEXT,
		IMAGE,
		AUDIO,
		BINARY
	}
	
	public static BasicType getType(File f)
	{
		String extension = FilenameUtils.getExtension(f.getName());
		switch(extension)
		{
			case "mp3": case "ogg": return BasicType.AUDIO;
			case "png": return BasicType.IMAGE;
			case "txt": case "json": case "xml": return BasicType.TEXT;
			default: return BasicType.BINARY;
		}
	}
}
