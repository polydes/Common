package com.polydes.common.comp.utils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import stencyl.sw.util.Worker;

public abstract class DocumentAdapter implements DocumentListener
{
	private boolean noBlank;
	
	public DocumentAdapter(boolean noBlank)
	{
		this.noBlank = noBlank;
	}
	
	protected abstract void update();
	
	private void innerUpdate(DocumentEvent e)
	{
		try
		{
			if(noBlank && e.getDocument().getText(0, e.getDocument().getLength()).equals(""))
				return;
		}
		catch (BadLocationException e1)
		{
			e1.printStackTrace();
		}
		
		update();
	}
	
	@Override public void changedUpdate(DocumentEvent e){innerUpdate(e);}
	@Override public void insertUpdate(DocumentEvent e){innerUpdate(e);}
	@Override public void removeUpdate(DocumentEvent e){innerUpdate(e);}
	
	public static DocumentListener of(Worker listener)
	{
		return new DocumentAdapter(false)
		{
			@Override
			protected void update()
			{
				listener.doWork();
			}
		};
	}
}