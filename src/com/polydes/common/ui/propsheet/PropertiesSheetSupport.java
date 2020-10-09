package com.polydes.common.ui.propsheet;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.reflect.FieldUtils;

import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataType;

import stencyl.sw.util.dg.DialogPanel;

public class PropertiesSheetSupport
{
	private final PropertiesSheetWrapper wrapper;
	private final HashMap<String, FieldInfo> fields;
	private final PropertyChangeSupport pcs;
	private final PropertiesSheetBuilder builder;
	
	private final Object model;
	private Map<String, Object> proxy;
	private boolean declaredFieldsOnly = false;
	
	public PropertiesSheetSupport(DialogPanel panel, Object model)
	{
		this(new DialogPanelWrapper(panel), PropertiesSheetStyle.DARK, model);
	}
	
	public PropertiesSheetSupport(PropertiesSheetWrapper wrapper, PropertiesSheetStyle style, Object model)
	{
		this.wrapper = wrapper;
		fields = new HashMap<>();
		pcs = new PropertyChangeSupport(this);
		builder = new PropertiesSheetBuilder(this, wrapper, style);
		
		this.model = model;
	}
	
	public void run()
	{
		build()
			.field("myInt")._int().min(0).max(10).spinnerEditor().add()
			.field("myBool")._boolean().add()
			.field("myString")._string().expandingEditor().regex("").add()
			.finish();
	}
	
	/**
	 * Write realtime data changes to an intermediate map instead of the data model.<br/>
	 * Use {@code applyChanges()} to save changes when finished.<br/><br/>
	 * 
	 * This should be called before any fields are added.
	 */
	public void useProxy()
	{
		proxy = new HashMap<>();
	}
	
	public void useDeclaredFieldsOnly()
	{
		declaredFieldsOnly = true;
	}
	
	public PropertiesSheetBuilder build()
	{
		return builder.startBuilding();
	}

	public PropertiesSheetBuilder change()
	{
		return builder.startChanging();
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
	public void fieldAdded(final FieldInfo field, DataEditor editor)
	{
		final Object target = proxy != null ? proxy : model;
		
		fields.put(field.varname, field);
		field.oldValue = readField(model, field.varname);
		//TODO: if field.oldValue is a mutable object that's changed in-place,
		//it should be cloned so we can properly restore the old value.
		field.editor = editor;
		editor.setValue(field.oldValue);
		
		if(proxy != null)
			proxy.put(field.varname, field.oldValue);
		
		editor.addListener(() -> writeField(target, field.getVarname(), editor.getValue()));
	}
	
	public FieldInfo getField(String varname)
	{
		return fields.get(varname);
	}
	
	public PropertiesSheetWrapper getWrapper()
	{
		return wrapper;
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void updateField(String varname, Object value)
	{
		((DataEditor) fields.get(varname).editor).setValue(value);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void refreshField(String varname)
	{
		((DataEditor) fields.get(varname).editor).setValue(readField(model, varname));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void changeField(String varname, FieldInfo field, DataEditor editor)
	{
		final Object target = proxy != null ? proxy : model;
		
		fields.remove(varname).editor.dispose();
		fields.put(varname, field);
		field.editor = editor;
		editor.addListener(() -> writeField(target, field.getVarname(), editor.getValue()));
		editor.setValue(readField(target, field.varname));
	}
	
	/**
	 * If useProxy was not called, changes are applied to the model immediately.<br/>
	 * This can be used to undo changes.<br/><br/>
	 * 
	 * It also sends editors back to their initial state.
	 */
	public void revertChanges()
	{
		for(FieldInfo field : fields.values())
			writeField(model, field.varname, field.oldValue);
	}
	
	/**
	 * If useProxy was called, this method returns true if there are unsaved changes.
	 */
	public boolean isDirty()
	{
		for(FieldInfo field : fields.values())
			if(!proxy.get(field.varname).equals(readField(model, field.varname)))
				return true;
		
		return false;
	}
	
	/**
	 * If useProxy was called, this method is used to actually apply the changes when editing is finished.
	 */
	public void applyChanges()
	{
		for(FieldInfo field : fields.values())
			writeField(model, field.varname, proxy.get(field.varname));
	}
	
	public void refreshAllFields()
	{
		for(FieldInfo field : fields.values())
			refreshField(field.varname);
	}
	
	public void dispose()
	{
		for(FieldInfo field : fields.values())
			field.editor.dispose();
		fields.clear();
		wrapper.dispose();
		proxy = null;
	}
	
	public static class FieldInfo
	{
		private String varname;
		private DataType<?> type;
		private String label;
		private String hint;
		private boolean optional;
		
		private Object oldValue;
		private DataEditor<?> editor;
		
		public FieldInfo(String varname, DataType<?> type, String label, String hint, boolean optional)
		{
			this.varname = varname;
			this.type = type;
			this.label = label;
			this.hint = hint;
			this.optional = optional;
		}
		
		public String getVarname()
		{
			return varname;
		}
		
		public DataType<?> getType()
		{
			return type;
		}
		
		public String getHint()
		{
			return hint;
		}
		
		public String getLabel()
		{
			return label;
		}
		
		public boolean isOptional()
		{
			return optional;
		}
		
		public DataEditor<?> getEditor()
		{
			return editor;
		}
	}
	
	/*-------------------------------------*\
	 * Property Change Support
	\*-------------------------------------*/
	
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
	}
	
	public void addPropertyChangeListener(String property, PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(property, listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
	}
	
	/*-------------------------------------*\
	 * Helpers
	\*-------------------------------------*/
	
	@SuppressWarnings("rawtypes")
	public Object readField(Object target, String fieldName)
	{
		if(target instanceof Map)
			return ((Map) target).get(fieldName);
		try
		{
			return declaredFieldsOnly ?
				FieldUtils.readDeclaredField(target, fieldName, true) :
				FieldUtils.readField(target, fieldName, true);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public void writeField(Object target, String fieldName, Object value)
	{
		try
		{
			Object oldValue = readField(target, fieldName);
			if(target instanceof Map)
				((Map) target).put(fieldName, value);
			else if(declaredFieldsOnly)
				FieldUtils.writeDeclaredField(target, fieldName, value, true);
			else
				FieldUtils.writeField(target, fieldName, value, true);
			
			//XXX: this won't work for mutable objects that are changed in-place.
			pcs.firePropertyChange(fieldName, oldValue, value);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
}
