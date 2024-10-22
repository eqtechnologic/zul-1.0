/* Borderlayout.java

	Purpose:
		
	Description:
		
	History:
		Feb 10, 2009 4:17:16 PM , Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.UiException;

/**
 * A border layout is a layout container for arranging and resizing
 * child components to fit in five regions: north, south, east, west, and center.
 * Each region may
 * contain no more than one component, and is identified by a corresponding
 * constant: <code>NORTH</code>, <code>SOUTH</code>, <code>EAST</code>,
 * <code>WEST</code>, and <code>CENTER</code>. When adding a component to
 * a container with a border layout, use one of these five constants, for
 * example:
 * 
 * <pre>
 *  &lt;borderlayout&gt;
 *  &lt;north margins=&quot;1,5,1,1&quot; size=&quot;20%&quot; splittable=&quot;true&quot; collapsible=&quot;true&quot; minsize=&quot;100&quot; maxsize=&quot;400&quot;&gt;
 *  &lt;div&gt;
 *  North
 *  &lt;/div&gt;
 *  &lt;/north&gt;
 *  &lt;west size=&quot;25%&quot; splittable=&quot;true&quot; autoscroll=&quot;true&quot;&gt;
 *  &lt;div&gt;
 *  West
 *  &lt;/div&gt;
 *  &lt;/west&gt;
 *  &lt;center flex=&quot;true&quot;&gt;
 *  &lt;div&gt;
 *  Center
 *  &lt;/div&gt;
 *  &lt;/center&gt;
 *  &lt;east size=&quot;25%&quot; collapsible=&quot;true&quot; onOpen='alert(self.id + &quot; is open :&quot; +event.open)'&gt;
 *  &lt;div&gt;
 *  East
 *  &lt;/div&gt;
 *  &lt;/east&gt;
 *  &lt;south size=&quot;50%&quot; splittable=&quot;true&quot;&gt;
 *  &lt;div&gt;
 *  south
 *  &lt;/div&gt;
 *  &lt;/south&gt;
 *  &lt;/borderlayout&gt;
 * 
 * </pre>
 * 
 * <p>Default {@link #getZclass}: z-borderlayout. (since 3.5.0)
 * 
 * @author jumperchen
 * @since 5.0.0
 */
public class Borderlayout extends HtmlBasedComponent implements org.zkoss.zul.api.Borderlayout {

	/**
	 * The north layout constraint (top of container).
	 */
	public static final String NORTH = "north";

	/**
	 * The south layout constraint (bottom of container).
	 */
	public static final String SOUTH = "south";

	/**
	 * The east layout constraint (right side of container).
	 */
	public static final String EAST = "east";

	/**
	 * The west layout constraint (left side of container).
	 */
	public static final String WEST = "west";

	/**
	 * The center layout constraint (middle of container).
	 */
	public static final String CENTER = "center";

	private transient North _north;

	private transient South _south;

	private transient West _west;

	private transient East _east;

	private transient Center _center;

	public Borderlayout() {
	}

	public North getNorth() {
		return _north;
	}

	public South getSouth() {
		return _south;
	}

	public West getWest() {
		return _west;
	}

	public East getEast() {
		return _east;
	}

	public Center getCenter() {
		return _center;
	}

	public org.zkoss.zul.api.North getNorthApi() {
		return _north;
	}
	public org.zkoss.zul.api.South getSouthApi() {
		return _south;
	}

	public org.zkoss.zul.api.West getWestApi() {
		return _west;
	}

	public org.zkoss.zul.api.East getEastApi() {
		return _east;
	}

	public org.zkoss.zul.api.Center getCenterApi() {
		return _center;
	}
	/**
	 * Re-size this layout component.
	 */
	public void resize() {
		smartUpdate("resize", true);
	}

	public void beforeChildAdded(Component child, Component refChild) {
		if (!(child instanceof LayoutRegion))
			throw new UiException("Unsupported child for Borderlayout: "
					+ child);
		if (child instanceof North) {
			if (_north != null && child != _north)
				throw new UiException("Only one north child is allowed: "
						+ this);
		} else if (child instanceof South) {
			if (_south != null && child != _south)
				throw new UiException("Only one south child is allowed: "
						+ this);
		} else if (child instanceof West) {
			if (_west != null && child != _west)
				throw new UiException("Only one west child is allowed: " + this);
		} else if (child instanceof East) {
			if (_east != null && child != _east)
				throw new UiException("Only one east child is allowed: " + this);
		} else if (child instanceof Center) {
			if (_center != null && child != _center)
				throw new UiException("Only one center child is allowed: "
						+ this);
		}
		super.beforeChildAdded(child, refChild);
	}
	public boolean insertBefore(Component child, Component refChild) {
		if (child instanceof North) {
			if (!super.insertBefore(child, refChild))
				return false;
			_north = (North) child;
		} else if (child instanceof South) {
			if (!super.insertBefore(child, refChild))
				return false;
			_south = (South) child;
		} else if (child instanceof West) {
			if (!super.insertBefore(child, refChild))
				return false;
			_west = (West) child;
		} else if (child instanceof East) {
			if (!super.insertBefore(child, refChild))
				return false;
			_east = (East) child;
		} else if (child instanceof Center) {
			if (!super.insertBefore(child, refChild))
				return false;
			_center = (Center) child;
		} else {
			if (!super.insertBefore(child, refChild))
				return false;
		}
		return true;
	}
	
	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		if (_north == child) _north = null;
		else if (_south == child) _south = null;
		else if (_west == child) _west = null;
		else if (_east == child) _east = null;
		else if (_center == child) _center = null;
	}

	public String getZclass() {
		return _zclass == null ? "z-borderlayout" : _zclass;
	}
	//Cloneable//
	public Object clone() {
		final Borderlayout clone = (Borderlayout) super.clone();
		clone.afterUnmarshal();
		return clone;
	}
	private void afterUnmarshal() {
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof North) {
				_north = (North) child;
			} else if (child instanceof South) {
				_south = (South) child;
			} else if (child instanceof Center) {
				_center = (Center) child;
			} else if (child instanceof West) {
				_west = (West) child;
			} else if (child instanceof East) {
				_east = (East) child;
			}
		}
	}
	//-- Serializable --//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		afterUnmarshal();
	}
}
