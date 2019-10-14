package com.polydes.common.data.types.builtin;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.apache.commons.lang3.NotImplementedException;

import com.polydes.common.comp.RenderedPanel;
import com.polydes.common.comp.UpdatingCombo;
import com.polydes.common.data.types.DataEditor;
import com.polydes.common.data.types.DataEditorBuilder;
import com.polydes.common.data.types.DataType;
import com.polydes.common.data.types.EditorProperties;
import com.polydes.common.sw.Resources;
import com.polydes.common.ui.propsheet.PropertiesSheetStyle;

import stencyl.core.lib.AbstractResource;
import stencyl.core.lib.Game;
import stencyl.core.lib.Resource;
import stencyl.core.lib.ResourceType;
import stencyl.core.lib.ResourceTypes;
import stencyl.sw.app.TaskManager;
import stencyl.sw.editors.behavior.SnippetChooser;
import stencyl.sw.editors.scene.dialogs.BackgroundChooser;
import stencyl.sw.editors.snippet.vars.AbstractResourceChooser;
import stencyl.sw.util.comp.GroupButton;

public class StencylResourceType<T extends AbstractResource> extends DataType<T>
{
	ResourceType stencylResourceType;
	
	@SuppressWarnings("unchecked")
	public StencylResourceType(ResourceType stencylResourceType)
	{
		super((Class<T>) stencylResourceType.getResourceClass());
		this.stencylResourceType = stencylResourceType;
	}
	
	public static final String RENDER_PREVIEW = "renderPreview";
	
	@Override
	public DataEditor<T> createEditor(EditorProperties props, PropertiesSheetStyle style)
	{
		if(props.get(RENDER_PREVIEW) == Boolean.TRUE)
			return new RenderedResourceChooser();
		else
			return new DropdownResourceEditor();
	}
	
	@Override
	public DataEditorBuilder createEditorBuilder()
	{
		return new StencylResourceEditorBuilder();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T decode(String s)
	{
		try
		{
			int id = Integer.parseInt(s);
			Resource r = Game.getGame().getResource(id);
			if(r != null && javaType.isAssignableFrom(r.getClass()))
				return (T) r;
			
			return null;
		}
		catch(NumberFormatException ex)
		{
			return null;
		}
	}

	@Override
	public String encode(T r)
	{
		if(r == null)
			return "";
		
		return "" + r.getID();
	}
	
	@Override
	public String toDisplayString(T data)
	{
		return String.valueOf(data);
	}
	
	@Override
	public T copy(T t)
	{
		return t;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<T> getList()
	{
		return (Collection<T>) Game.getGame().getResourcesForResourceType(stencylResourceType);
	}
	
	public class StencylResourceEditorBuilder extends DataEditorBuilder
	{
		public StencylResourceEditorBuilder()
		{
			super(StencylResourceType.this, new EditorProperties());
		}
		
		public StencylResourceEditorBuilder rendered()
		{
			props.put(RENDER_PREVIEW, Boolean.TRUE);
			return this;
		}
	}
	
	public class RenderedResourceChooser extends DataEditor<T>
	{
		final RenderedPanel panel;
		final GroupButton button;
		
		T selected;
		
		public RenderedResourceChooser()
		{
			if(stencylResourceType == ResourceTypes.tileset)
				throw new NotImplementedException("RenderedResourceChooser not implemented for Tileset resources.");
			
			panel = new RenderedPanel(90, 60, 0);
			
			button = new GroupButton(4);
			button.disableEtching();
			button.setText("Choose");
			button.addActionListener(e -> {
				TaskManager.preShowDialog();
		    	T newResource = chooseResource();
		    	TaskManager.postShowDialog();
		    	if(newResource != selected)
		    	{
		    		selected = newResource;
		    		updatePanel();
		    		updated();
		    	}
			});
		}
		
		private void updatePanel()
		{
			panel.setLabel(Resources.getImage(selected));
		}
		
		@Override
		public void set(T t)
		{
			selected = t;
			updatePanel();
		}
		
		@Override
		public T getValue()
		{
			return selected;
		}
		
		@Override
		public JComponent[] getComponents()
		{
			return new JComponent[] {panel, button};
		}
		
		@Override
		public void dispose()
		{
			super.dispose();
			selected = null;
		}
		
		@SuppressWarnings("unchecked")
		private T chooseResource()
		{
			T result = null;
			
			if(stencylResourceType == ResourceTypes.background)
			{
				BackgroundChooser chooser = new BackgroundChooser((Resource) selected);
				result = (T) chooser.getChosenBackground();
				chooser.dispose();
			}
			else if(stencylResourceType == ResourceTypes.snippet)
			{
				//TODO this is actor-only
				SnippetChooser chooser = new SnippetChooser(true);
				result = (T) chooser.getResult();
				chooser.dispose();
			}
			else if(stencylResourceType == ResourceTypes.tileset)
			{
				
			}
			else
			{
				AbstractResourceChooser<T> chooser = new AbstractResourceChooser<T>(stencylResourceType, (Resource) selected);
				result = (T) chooser.getChosenResource();
				chooser.dispose();
			}
			
			
			return result;
		}
	}
	
	public class DropdownResourceEditor extends DataEditor<T>
	{
		final UpdatingCombo<T> editor;
		
		public DropdownResourceEditor()
		{
			editor = new UpdatingCombo<T>(getList(), null);
			editor.setIconProvider(resource -> {
				if(resource == null)
					return null;
				Image resourceImage = Resources.getImage(resource);
				if(resourceImage == null)
					return null;
				return new ImageIcon(resourceImage);
			});
			
			editor.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					updated();
				}
			});
		}
		
		@Override
		public void set(T t)
		{
			editor.setSelectedItem(t);
		}
		
		@Override
		public T getValue()
		{
			return editor.getSelected();
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
			editor.dispose();
		}
	}
}