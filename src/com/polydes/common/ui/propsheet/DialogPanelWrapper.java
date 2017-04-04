package com.polydes.common.ui.propsheet;

import com.polydes.common.comp.utils.Layout;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.ui.propsheet.PropertiesSheetSupport.FieldInfo;

import stencyl.sw.util.dg.DialogPanel;

public class DialogPanelWrapper implements PropertiesSheetWrapper
{
	private DialogPanel panel;
	private boolean fieldsAdded = false;
	
	public DialogPanelWrapper(DialogPanel panel)
	{
		this.panel = panel;
	}
	
	@Override
	public void addField(FieldInfo newField, DataEditor<?> editor)
	{
		panel.addGenericRow(newField.getLabel(), Layout.horizontalBox(editor.getComponents()));
		
		String hint = newField.getHint();
		if(hint != null && !hint.isEmpty())
			panel.addDescriptionRow(hint);
		fieldsAdded = true;
	}
	
	@Override
	public void changeField(String varname, FieldInfo field, DataEditor<?> editor)
	{
		throw new UnsupportedOperationException("DialogPanel doesn't support changing fields.");
	}
	
	@Override
	public void addHeader(String title)
	{
		if(fieldsAdded)
			panel.finishBlock();
		panel.addHeader(title);
		fieldsAdded = false;
	}
	
	@Override
	public void finish()
	{
		panel.finishBlock();
		fieldsAdded = false;
	}
	
	@Override
	public void dispose()
	{
		panel.removeAll();
		panel = null;
	}
}
