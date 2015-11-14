package com.polydes.common.data.types.hidden;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;

import com.polydes.common.collections.CollectionPredicate;
import com.polydes.common.comp.UpdatingCombo;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.Types;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

@SuppressWarnings("rawtypes")
public class DataTypeType extends DataType<DataType>
{
	public DataTypeType()
	{
		super(DataType.class);
	}

	@Override
	public DataEditor<DataType> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		return new DataTypeEditor();
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new DataEditorBuilder(this, new EditorProperties());
	}

	@Override
	public DataType decode(String s)
	{
		return Types.get().getItem(s);
	}

	@Override
	public String toDisplayString(DataType data)
	{
		return data.getId();
	}

	@Override
	public String encode(DataType data)
	{
		return data.getId();
	}
	
	@Override
	public DataType copy(DataType t)
	{
		return t;
	}
	
	public static class DataTypeEditor extends DataEditor<DataType>
	{
		UpdatingCombo<DataType<?>> typeChooser;
		
		public DataTypeEditor()
		{
			typeChooser = new UpdatingCombo<DataType<?>>(Types.get().values(), null);
			
			typeChooser.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
		}
		
		public DataTypeEditor(CollectionPredicate<DataType<?>> filter)
		{
			typeChooser = new UpdatingCombo<DataType<?>>(Types.get().values(), filter);
			
			typeChooser.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
		}
		
//		public DataTypeEditor(final EditorProperties props)
//		{
//			CollectionPredicate<DataType<?>> filter = new CollectionPredicate<DataType<?>>()
//			{
//				@Override
//				public boolean test(DataType<?> t)
//				{
//					return !e.excludedTypes.contains(t);
//				};
//			};
//			
//			typeChooser = new UpdatingCombo<DataType<?>>(Types.get().values(), filter);
//			
//			typeChooser.addActionListener(new ActionListener()
//			{
//				@Override
//				public void actionPerformed(ActionEvent e)
//				{
//					updated();
//				}
//			});
//			
//			typeChooser.setMaximumRowCount(18);
//		}
		
		public void setFilter(CollectionPredicate<DataType<?>> filter)
		{
			typeChooser.setFilter(filter);
		}
		
		@Override
		public void set(DataType t)
		{
			typeChooser.setSelectedItem(t);
		}
		
		@Override
		public DataType getValue()
		{
			return typeChooser.getSelected();
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {typeChooser};
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			typeChooser.dispose();
			typeChooser = null;
		}
	}
}