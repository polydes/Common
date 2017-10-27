package com.polydes.common.sw;

import java.awt.Image;

import stencyl.core.engine.snippet.ISnippet;
import stencyl.core.lib.AbstractResource;
import stencyl.core.lib.Folder;
import stencyl.core.lib.Resource;
import stencyl.core.lib.scene.SceneModel;
import stencyl.sw.util.Loader;

public class Resources
{
	public static boolean isUnderFolder(AbstractResource r, Folder f)
	{
		//since Stencyl doesn't have nested folders, null folder represents the root folder.
		return f == null || r.getParentFolder() == f;
	}
	
	public static Image getImage(AbstractResource r)
	{
		Image img = null;
		
		if(r instanceof Resource)
		{
			img = ((Resource) r).getThumbnail();
		}
		else if(r instanceof SceneModel)
		{
			SceneModel scene = (SceneModel) r;
			img = scene.getThumbnail();
		}
		else if(r instanceof ISnippet)
		{
			ISnippet snippet = (ISnippet) r;
			img = snippet.getIcon();
		}
		
		if(r != null && img == null)
			img = Loader.loadIcon("res/global/warning.png").getImage();
		
		return img;
	}
}
