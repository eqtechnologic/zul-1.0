/* Treecell.java

	Purpose:

	Description:

	History:
		Wed Jul  6 18:56:30     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.impl.LabelImageElement;

/**
 * A treecell.
 *
 * <p>In XUL, treecell cannot have any child, but ZUL allows it.
 * Thus, you could place any kind of children in it. They will be placed
 * right after the image and label.
 *
 * <p>Default {@link #getZclass}: z-treecell (since 5.0.0)
 * @author tomyeh
 */
public class Treecell extends LabelImageElement implements org.zkoss.zul.api.Treecell {
	private int _span = 1;

	public Treecell() {
	}
	public Treecell(String label) {
		super(label);
	}
	public Treecell(String label, String src) {
		super(label, src);
	}

	/** Return the tree that owns this cell.
	 */
	public Tree getTree() {
		for (Component n = this; (n = n.getParent()) != null;)
			if (n instanceof Tree)
				return (Tree)n;
		return null;
	}
	/** Return the tree that owns this cell.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tree getTreeApi() {
		return getTree();
	}
	/** Returns the tree col associated with this cell, or null if not available.
	 */
	public Treecol getTreecol() {
		final Tree tree = getTree();
		if (tree != null) {
			final Treecols lcs = tree.getTreecols();
			if (lcs != null) {
				final int j = getColumnIndex();
				final List lcschs = lcs.getChildren();
				if (j < lcschs.size())
					return (Treecol)lcschs.get(j);
			}
		}
		return null;
	}
	/** Returns the tree col associated with this cell, or null if not available.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Treecol getTreecolApi() {
		return getTreecol();
	}
	/** Returns the column index of this cell, starting from 0.
	 */
	public int getColumnIndex() {
		int j = 0;
		for (Iterator it = getParent().getChildren().iterator();
		it.hasNext(); ++j)
			if (it.next() == this)
				break;
		return j;
	}

	/** Returns the maximal length for this cell, which is decided by
	 * the corresponding {@link #getTreecol}'s {@link Treecol#getMaxlength}.
	 */
	public int getMaxlength() {
		final Tree tree = getTree();
		if (tree == null)
			return 0;
		final Treecol lc = getTreecol();
		return lc != null ? lc.getMaxlength(): 0;
	}

	/** Returns the level this cell is. The root is level 0.
	 */
	public int getLevel() {
		final Component parent = getParent();
		return parent != null ? ((Treerow)parent).getLevel(): 0;
	}

	/** Returns number of columns to span this cell.
	 * Default: 1.
	 */
	public int getSpan() {
		return _span;
	}
	/** Sets the number of columns to span this cell.
	 * <p>It is the same as the colspan attribute of HTML TD tag.
	 */
	public void setSpan(int span) {
		if (_span != span) {
			_span = span;
			smartUpdate("colspan", _span);
		}
	}

	/** Returns whether an item is the last visible child.
	 */
	public static boolean isLastChild(Treeitem item) {
		final Component parent = item.getParent();
		if (parent == null) return true;
		for (Component n = parent.getLastChild(); n != null; n = n.getPreviousSibling())
			if (n.isVisible()) return  n == item;
		return false;		
	}
	/** Returns an array of Treeitem from the root.
	 */
	private Treeitem[] getTreeitems(Component item) {
		final List pitems = new LinkedList();
		for (;;) {
			final Component tch = item.getParent();
			if (tch == null)
				break;
			item = tch.getParent();
			if (item == null || item instanceof Tree)
				break;
			pitems.add(0, item);
		}
		return (Treeitem[])pitems.toArray(new Treeitem[pitems.size()]);
	}

	private Treeitem getTreeitem() {
		final Component parent = getParent();
		return parent != null ? (Treeitem)parent.getParent(): null;
	}

	public String getZclass() {
		return _zclass == null ? "z-treecell" : _zclass;
	}

	//-- super --//
	/** Returns the width which the same as {@link #getTreecol}'s width.
	 */
	public String getWidth() {
		final Treecol col = getTreecol();
		return col != null ? col.getWidth(): null;
	}
	public void setWidth(String width) {
		throw new UnsupportedOperationException("Set treecol's width instead");
	}

	//-- Component --//
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		
		if (_span > 1)
			renderer.render("colspan", _span);
	}
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Treerow))
			throw new UiException("Wrong parent: "+parent);
		super.beforeParentChanged(parent);
	}
}
