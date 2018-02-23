package com.polydes.common.ui.propsheet;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

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
		if(newField.isOptional())
			panel.addGenericRow(newField.getLabel(), createPanelHider(editor));
		else
			panel.addGenericRow(newField.getLabel(), Layout.horizontalBox(editor.getComponents()));
		
		String hint = newField.getHint();
		if(hint != null && !hint.isEmpty())
			panel.addDescriptionNoSpace(hint);
		fieldsAdded = true;
	}
	
	private JPanel createPanelHider(DataEditor<?> editor)
	{
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setBackground(null);
		
		JCheckBox box = new JCheckBox();
		box.setBackground(null);
		box.setSelected(true);
		
		JPanel panel = Layout.horizontalBox(editor.getComponents());
		
		box.addActionListener(l -> {
			panel.setVisible(box.isSelected());
			if(!box.isSelected())
				editor.setValue(null);
		});
		
		wrapper.add(panel, BorderLayout.CENTER);
		wrapper.add(box, BorderLayout.WEST);
		return wrapper;
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
