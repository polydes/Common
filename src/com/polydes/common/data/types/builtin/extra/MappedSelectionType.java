package com.polydes.common.data.types.builtin.extra;

import static com.polydes.common.util.Lang.or;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import com.polydes.common.comp.utils.Layout;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.data.types.PropertyKey;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

public class MappedSelectionType<T> extends DataType<T>
{
	private DataType<T> internalType;
	
	public MappedSelectionType(DataType<T> type)
	{
		super(type.javaType, "com.polydes.common.MappedSelection");
		internalType = type;
	}
	
	public static final PropertyKey<Editor>           EDITOR  = new PropertyKey<>("editor");
	public static final PropertyKey<SelectionList<?>> OPTIONS = new PropertyKey<>("options");
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new MappedSelectionEditorBuilder<T>(this);
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	public DataEditor<T> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		SelectionList<T> options = (SelectionList<T>) props.get(OPTIONS);
		
		if(options == null || options.list.isEmpty())
			return new InvalidEditor<T>("The selected source has no items", style);
		
		switch(or(props.get(EDITOR), Editor.Dropdown))
		{
			case RadioButtons:
				return new RadioButtonsSelectionEditor(options, style);
			default:
				return new DropdownSelectionEditor(options);	
		}
	}

	@Override
	public T decode(String s)
	{
		return internalType.decode(s);
	}

	@Override
	public String encode(T s)
	{
		return internalType.encode(s);
	}

	@Override
	public T copy(T t)
	{
		return internalType.copy(t);
	}
	
	public static class MappedSelectionEditorBuilder<T> extends DataEditorBuilder
	{
		private SelectionList<T> options;
		
		public MappedSelectionEditorBuilder(MappedSelectionType<T> parent)
		{
			super(parent, new EditorProperties(){{
				put(EDITOR, Editor.Dropdown);
			}});
		}
		
		public MappedSelectionEditorBuilder<T> radioButtonsEditor()
		{
			props.put(EDITOR, Editor.RadioButtons);
			return this;
		}
		
		public MappedSelectionEditorBuilder<T> source(SelectionList<T> list)
		{
			props.put(OPTIONS, options = list);
			return this;
		}
		
		public MappedSelectionEditorBuilder<T> option(String label, T object)
		{
			if(options == null)
				props.put(OPTIONS, options = new SelectionList<>());
			options.add(label, object);
			return this;
		}
	}
	
	public static enum Editor
	{
		Dropdown,
		RadioButtons/*,
		Grid,
		Cycle;*/
	}
	
	private static final class Selection<T>
	{
		public final String label;
		public final T object;
		
		public Selection(String label, T object)
		{
			this.label = label;
			this.object = object;
		}
		
		@Override
		public String toString()
		{
			return label;
		}
	}
	
	private static final class SelectionList<T>
	{
		private final ArrayList<Selection<T>> list = new ArrayList<>();
		private final HashMap<String, Selection<T>> stringMap = new HashMap<>();
		private final HashMap<T, Selection<T>> objectMap = new HashMap<>();
		
		public void add(String label, T object)
		{
			Selection<T> newSelection = new Selection<>(label, object);
			list.add(newSelection);
			stringMap.put(label, newSelection);
			objectMap.put(object, newSelection);
		}
	}
	
	public static class DropdownSelectionEditor<T> extends DataEditor<Object>
	{
		final SelectionList<T> options;
		final JComboBox<Selection<T>> editor;
		
		@SuppressWarnings("unchecked")
		public DropdownSelectionEditor(SelectionList<T> options)
		{
			this.options = options;
			editor = new JComboBox<Selection<T>>(options.list.toArray(new Selection[0]));
			editor.setBackground(null);
			editor.addActionListener(e -> updated());
		}
		
		@Override
		public void set(Object t)
		{
			editor.setSelectedItem(options.objectMap.get(t));
		}
		
		@Override
		public Object getValue()
		{
			return editor.getItemAt(editor.getSelectedIndex()).object;
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {editor};
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
		}
	}
	
	public static class RadioButtonsSelectionEditor<T> extends DataEditor<Object>
	{
		final SelectionList<T> options;
		final ButtonGroup group;
		final HashMap<String, JRadioButton> bmap;
		final JPanel buttonPanel;
		
		Selection<T> current;
		
		public RadioButtonsSelectionEditor(SelectionList<T> options, PropertiesSheetStyle style)
		{
			this.options = options;
			group = new ButtonGroup();
			ArrayList<JRadioButton> buttons = new ArrayList<JRadioButton>();
			
			bmap = new HashMap<String, JRadioButton>();
			
			JRadioButton b;
			for(final Selection<T> s : options.list)
			{
				buttons.add(b = new JRadioButton(s.label));
				group.add(b);
				bmap.put(s.label, b);
				
				b.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e)
					{
						current = s;
					}
				});
				
				b.setBackground(null);
				b.setForeground(style.labelColor);
			}
			
			buttonPanel = Layout.verticalBox(0, buttons.toArray(new JRadioButton[0]));
		}
		
		@Override
		public void set(Object t)
		{
			current = options.objectMap.get(t);
			if(bmap.containsKey(current.label))
				group.setSelected(bmap.get(current.label).getModel(), true);
		}
		
		@Override
		public Object getValue()
		{
			return current.object;
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {buttonPanel};
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			bmap.clear();
			current = null;
		}
	}
}
