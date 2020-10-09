package com.polydes.common.nodes;

import com.polydes.common.ui.object.EditableObject;

public class DefaultEditableLeaf extends DefaultViewableLeaf
{
	public DefaultEditableLeaf(String name, EditableObject object)
	{
		super(name, object);
	}
	
	public DefaultEditableLeaf(String name)
	{
		this(name, null);
	}
}