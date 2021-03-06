package com.polydes.common.ui.darktree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreePath;

import com.polydes.common.nodes.Branch;
import com.polydes.common.res.ResourceLoader;
import com.polydes.common.res.Resources;

public class DTreeUI extends BasicTreeUI implements MouseListener
{
	private static Resources res = ResourceLoader.getResources("com.polydes.common");
	
	public static final Icon iconNoChildren = res.loadIcon("tree/circle.png");
	public static final Icon iconExpanded = res.loadIcon("tree/arrow-down.png");
	public static final Icon iconCollapsed = res.loadIcon("tree/arrow-right.png");

	public static JPanel highlighter = null;
	public static final Color highlightColor = new Color(102, 102, 102);

	private DarkTree<?,?> dtree;

	public DTreeUI(DarkTree<?,?> dtree)
	{
		super();

		this.dtree = dtree;

		if (highlighter == null)
		{
			highlighter = new JPanel();
			highlighter.setBackground(highlightColor);
		}
	}

	private Rectangle viewRect;
	private int treeX;
	private int treeWidth;

	@Override
	public void paint(Graphics g, JComponent c)
	{
		viewRect = dtree.getScroller().getViewport().getViewRect();
		treeX = viewRect.x;
		treeWidth = viewRect.width;

		super.paint(g, c);
	}

	@Override
	protected boolean shouldPaintExpandControl(TreePath path, int row,
			boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf)
	{
		return false;
	}

	private boolean _shouldPaintExpandControl(TreePath path, int row,
			boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf)
	{
		Object value = path.getLastPathComponent();

		if (!(value instanceof Branch))
			return false;
		int depth = path.getPathCount() - 1;
		if ((depth == 0 || (depth == 1 && !isRootVisible()))
				&& !getShowsRootHandles())
			return false;

		return true;
	}

	@Override
	protected void paintExpandControl(Graphics g, Rectangle clipBounds,
			Insets insets, Rectangle bounds, TreePath path, int row,
			boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf)
	{
		return;
	}

	private void _paintExpandControl(Graphics g, Rectangle clipBounds,
			Insets insets, Rectangle bounds, TreePath path, int row,
			boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf)
	{
		Object value = path.getLastPathComponent();
		if (value instanceof Branch)
		{
			Branch<?,?> f = (Branch<?,?>) value;
			
			int middleXOfKnob = bounds.x - getRightChildIndent() + 1;
			int middleYOfKnob = bounds.y + (bounds.height / 2);

			Icon icon;

			if (f.getItems().isEmpty())
				icon = iconNoChildren;
			else if (isExpanded)
				icon = iconExpanded;
			else
				icon = iconCollapsed;

			drawCentered(tree, g, icon, middleXOfKnob, middleYOfKnob);
		}
	}

	@Override
	protected void paintRow(Graphics g, Rectangle clipBounds, Insets insets,
			Rectangle bounds, TreePath path, int row, boolean isExpanded,
			boolean hasBeenExpanded, boolean isLeaf)
	{
		if (tree.isPathSelected(path))
		{
			// paint highlight
			Rectangle b = getHighlightBounds(path);
			rendererPane.paintComponent(g, null, highlighter, b.x, b.y,
					b.width, b.height, false);
		}

		if (_shouldPaintExpandControl(path, row, isExpanded, hasBeenExpanded, isLeaf))
			_paintExpandControl(g, clipBounds, insets, bounds, path, row, isExpanded, hasBeenExpanded, isLeaf);

		// Don't paint the renderer if editing this row.
		if (editingComponent != null && editingPath == path)
			return;
		
		Component component;

		component = currentCellRenderer.getTreeCellRendererComponent(tree,
				path.getLastPathComponent(), tree.isPathSelected(path),
				isExpanded, isLeaf, row, false);

		rendererPane.paintComponent(g, component, tree, bounds.x, bounds.y,
				bounds.width, bounds.height, true);
	}

	private Rectangle getHighlightBounds(TreePath path)
	{
		return new Rectangle(treeX, tree.getPathBounds(path).y, treeWidth, DarkTree.ITEM_HEIGHT);
	}

	@Override
	protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds,
			Insets insets, Rectangle bounds, TreePath path, int row,
			boolean isExpanded, boolean hasBeenExpanded, boolean isLeaf)
	{
		return;
	}

	@Override
	protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds,
			Insets insets, TreePath path)
	{
		return;
	}
	
	@Override
	public void installListeners()
	{
		super.installListeners();
		
		tree.addMouseListener(this);
	}
	
	@Override
	public void uninstallListeners()
	{
		super.uninstallListeners();
		
		tree.removeMouseListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		TreePath path = tree.getClosestPathForLocation(x, y);
		
		if(path == null)
			return;
		
		if(tree.getPathBounds(path).contains(e.getPoint()))
			return;
		
		if(SwingUtilities.isLeftMouseButton(e))
		{
			if(isLocationInExpandControl(path, x, y))
				return;
		}
		
		boolean multiEvent = (isMultiSelectEvent(e) || isToggleSelectionEvent(e));
		
		Rectangle b = tree.getPathBounds(path);
		if(b.y > y || b.y + b.height < y)
		{
			if(multiEvent)
				return;
			else
				tree.setSelectionPath(dtree.getRootPath());
		}
		else
		{
			if(!startEditing(path, e))
				selectPathForEvent(path, e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}
}
