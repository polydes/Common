package com.polydes.common.sw;

import java.io.IOException;

import org.apache.commons.io.FileUtils;

import stencyl.core.engine.snippet.ISnippet;
import stencyl.core.engine.snippet.ISnippet.SnippetType;
import stencyl.core.lib.Game;
import stencyl.core.lib.game.GameLocations;

public class Snippets
{
	public static ISnippet createNew(Game game, String name, String packageName, String className, String description, String sourceCode)
	{
		try
		{
			int ID = game.getNextSnippetID();
			
			ISnippet s = new ISnippet
			(
				null,
				className, 
				-1, 
				0,
				SnippetType.ARBITRARY,
				-1,
				-1,
				false,
				true,
				packageName
			);
			s.setID(ID);
			s.setName(name);
			s.setDescription(description);
			
			game.getSnippetList().put(ID, s);
			
			game.files.getFile(GameLocations.CODE).mkdirs();
			FileUtils.writeByteArrayToFile(game.files.getFile(GameLocations.CODE, className + ".hx"), sourceCode.getBytes());
			
			return s;
		}

		catch(IOException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
}
