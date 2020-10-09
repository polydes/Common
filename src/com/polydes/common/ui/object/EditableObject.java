package com.polydes.common.ui.object;

import javax.swing.JPanel;

public abstract class EditableObject implements ViewableObject
{
	public static JPanel BLANK_EDITOR = new JPanel();
	
	public abstract JPanel getEditor();
	public abstract void disposeEditor();
	public abstract void revertChanges();
	
	@Override
	public final JPanel getView()
	{
		return getEditor();
	}
	
	@Override
	public final void disposeView()
	{
		disposeEditor();
	}
}
