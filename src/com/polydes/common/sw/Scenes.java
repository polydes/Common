package com.polydes.common.sw;

import stencyl.core.engine.snippet.ISnippet;
import stencyl.core.engine.snippet.SnippetInstance;
import stencyl.core.lib.scene.SceneModel;
import stencyl.sw.SW;

public class Scenes
{
//	private static final Logger log = Logger.getLogger(Scenes.class);
	
	public static SceneModel ensureLoaded(SceneModel model)
	{
		if(!model.hasLoaded())
			model = model.headerLoad();
		
		return model;
	}
	
	public static boolean hasSnippet(SceneModel model, ISnippet s)
	{
		for(SnippetInstance si : model.getSnippets().values())
			if(si.getSnippet().getID() == s.getID())
				return true;
		
		return false;
	}
	
	public static enum Xml
	{
		//TODO [polydes]: maybe reimplement
	}
	
	public static void rewriteXml(SceneModel model, Xml xml)
	{
		//TODO [polydes]: maybe reimplement
	}
	
	public static void addSnippet(SceneModel model, ISnippet s)
	{
		model.getSnippets().put(s.getID(), new SnippetInstance(s));
	}
	
	public static boolean isNewScene(SceneModel model)
	{
		return SW.get().getWorkspace().isResourceOpen(model) && SW.get().getWorkspace().getDocumentForResource(model).isNew();
	}
}
