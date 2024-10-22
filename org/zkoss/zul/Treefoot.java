/* Treefoot.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 19 15:36:05     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.impl.XulElement;

/**
 * A row of {@link Treefooter}.
 *
 * <p>Like {@link Treecols}, each tree has at most one {@link Treefoot}.
 * <p>Default {@link #getZclass}: z-treefoot (since 5.0.0)
 * @author tomyeh
 */
public class Treefoot extends XulElement implements org.zkoss.zul.api.Treefoot {
	/** Returns the tree that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 */
	public Tree getTree() {
		return (Tree)getParent();
	}
	/** Returns the tree that it belongs to.
	 * <p>It is the same as {@link #getParent}.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tree getTreeApi() {
		return getTree();
	}
	//-- Component --//
	public String getZclass() {
		return _zclass == null ? "z-treefoot" : _zclass;
	}
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Tree))
			throw new UiException("Wrong parent: "+parent);
		super.beforeParentChanged(parent);
	}
	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof Treefooter))
			throw new UiException("Unsupported child for treefoot: "+child);
		super.beforeChildAdded(child, refChild);
	}
}
