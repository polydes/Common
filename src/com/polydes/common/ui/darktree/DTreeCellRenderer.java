package com.polydes.common.ui.darktree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import com.polydes.common.nodes.Branch;
import com.polydes.common.nodes.Leaf;
import com.polydes.common.util.IconUtil;

public class DTreeCellRenderer<T extends Leaf<T,U>, U extends Branch<T,U>> extends JPanel implements TreeCellRenderer
{
	private JLabel text;
	
	public static final int ICON_SIZE = 16;
	
	private static final Font branchFont = UIManager.getFont("Label.font").deriveFont(Font.BOLD, 11.0f);
    private static final Font leafFont = UIManager.getFont("Label.font").deriveFont(11.0f);
//	private static final Font leafSelectedFont = leafFont.deriveFont(Font.BOLD);
    
	private int itemHeight = DarkTree.ITEM_HEIGHT;
	
	public DTreeCellRenderer()
	{
		super(new BorderLayout());
		((BorderLayout) getLayout()).setHgap(4);
		setOpaque(true);
		
		text = new JLabel();
		text.setForeground(Color.WHITE);
		
		add(text, BorderLayout.CENTER);
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(super.getPreferredSize().width + 5, itemHeight);
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean isSelected, boolean isExpanded, boolean isLeaf, int row, boolean hasFocus)
	{
		@SuppressWarnings("unchecked")
		T item = (T) value;
		
		text.setText(tree.convertValueToText(value, isSelected, isExpanded, isLeaf, row, hasFocus));
		
		if(item instanceof Branch)
		{
			text.setFont(branchFont);
			text.setIcon(null);
		}
		else
		{
//			if(isSelected)
//				text.setFont(leafSelectedFont);
//			else
				text.setFont(leafFont);
			text.setIcon(IconUtil.getIcon(item.getIcon(), ICON_SIZE));
		}
		
		if(isSelected)
			setBackground(DTreeUI.highlightColor);
		else
			setBackground(null);
		
		return this;
	}
	
//	public void paintChildren(Graphics g)
//	{
//		Graphics2D g2D = (Graphics2D) g;
//		g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//		
//		super.paintChildren(g);
//	}
	
//	public void validate(){}
//	public void invalidate(){}
//	public void revalidate(){}
	@Override public void repaint(long tm, int x, int y, int width, int height) {}
	@Override public void repaint(Rectangle r) {}
	@Override public void repaint(){}
	@Override public void firePropertyChange(String propertyName, Object oldValue, Object newValue){}
	@Override public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}
	@Override public void firePropertyChange(String propertyName, char oldValue, char newValue) {}
	@Override public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
	@Override public void firePropertyChange(String propertyName, int oldValue, int newValue) {}
	@Override public void firePropertyChange(String propertyName, long oldValue, long newValue) {}
	@Override public void firePropertyChange(String propertyName, float oldValue, float newValue) {}
	@Override public void firePropertyChange(String propertyName, double oldValue, double newValue) {}
	@Override public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
	
	@Override
	public boolean isOpaque(){return true;}
}
