/* Row.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:02:43     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;

import org.zkoss.lang.Objects;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zul.impl.LoadStatus;
import org.zkoss.zul.impl.XulElement;

/**
 * A single row in a {@link Rows} element.
 * Each child of the {@link Row} element is placed in each successive cell
 * of the grid. The row with the most child elements determines the number
 * of columns in each row.
 *
 * <p>Default {@link #getZclass}: z-row. (since 3.5.0)
 *
 * @author tomyeh
 */
public class Row extends XulElement implements org.zkoss.zul.api.Row {
	private static final long serialVersionUID = 20091111L;

	private Object _value;
	private String _align, _valign;
	private String _spans;
	private boolean _nowrap;
	/** whether the content of this row is loaded; used if
	 * the grid owning this row is using a list model.
	 */
	private boolean _loaded;
	
	private transient Detail _detail;
	private transient int _index = -1;

	/**
	 * Returns the child detail component.
	 * @since 3.5.0
	 */
	public Detail getDetailChild() {
		return _detail;
	}
	/**
	 * Returns the child detail component.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Detail getDetailChildApi() {
		return getDetailChild();
	}
	/** Returns the grid that contains this row. */
	public Grid getGrid() {
		final Component parent = getParent();
		return parent != null ? (Grid)parent.getParent(): null;
	}

	/** Returns the grid that contains this row. 
	 * @since 3.5.2
	 * */
	public org.zkoss.zul.api.Grid getGridApi() {		
		return getGrid();
	}

	/** Returns the horizontal alignment of the whole row.
	 * <p>Default: null (system default: left unless CSS specified).
	 */
	public String getAlign() {
		return _align;
	}
	/** Sets the horizontal alignment of the whole row.
	 * Allowed values: right, left, center, justify, char
	 */
	public void setAlign(String align) {
		if (!Objects.equals(_align, align)) {
			_align = align;
			smartUpdate("align", _align);
		}
	}
	/** Returns the nowrap.
	 * <p>Default: null (system default: wrap).
	 */
	public boolean isNowrap() {
		return _nowrap;
	}
	/** Sets the nowrap.
	 */
	public void setNowrap(boolean nowrap) {
		if (_nowrap != nowrap) {
			_nowrap = nowrap;
			smartUpdate("nowrap", _nowrap);
		}
	}
	/** Returns the vertical alignment of the whole row.
	 * <p>Default: null (system default: top).
	 */
	public String getValign() {
		return _valign;
	}
	/** Sets the vertical alignment of the whole row.
	 * Allowed values: top, middle, bottom, baseline
	 */
	public void setValign(String valign) {
		if (!Objects.equals(_valign, valign)) {
			_valign = valign;
			smartUpdate("valign", _valign);
		}
	}
	public boolean setVisible(boolean visible) {
		if (isVisible() != visible) {
			final Rows rows = (Rows) getParent();
			if (rows != null) {
				final Group g = rows.getGroup(getIndex());
				if (g == null || g.isOpen())
					rows.addVisibleItemCount(visible ? 1 : -1);
			}
		}
		return super.setVisible(visible);
	}

	/** Returns the value.
	 * <p>Default: null.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 */
	public Object getValue() {
		return _value;
	}
	/** Sets the value.
	 * @param value the value.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 */
	public void setValue(Object value) {
		_value = value;
	}

	/** Returns the spans, which is a list of numbers separated by comma.
	 *
	 * <p>Default: empty.
	 * @deprecated As of release 5.0.0, use {@link Cell} instead.
	 */
	public String getSpans() {
		return _spans;
	}
	/** Sets the spans, which is a list of numbers separated by comma.
	 *
	 * <p>For example, "1,2,3" means the second column will span two columns
	 * and the following column span three columns, while others occupies
	 * one column.
	 * @deprecated As of release 5.0.0, use {@link Cell} instead.
	 */
	public void setSpans(String spans) throws WrongValueException {
		if (!Objects.equals(spans, _spans)) {
			_spans = spans;
			smartUpdate("spans", spans);
		}
	}


	/** Internal Use only. Sets whether the content of this row is loaded; used if
	 * the grid owning this row is using a list model.
	 */
	/*package*/ void setLoaded(boolean loaded) {
		if (loaded != _loaded) {
			_loaded = loaded;

			final Grid grid = getGrid();
			if (grid != null && grid.getModel() != null)
				smartUpdate("_loaded", _loaded);
		}
	}
	/** Internal Use Only. Returns whether the content of this row is loaded; used if
	 * the grid owning this row is using a list model.
	 */
	/*package*/ boolean isLoaded() {
		return _loaded;
	}
	/** Returns the index of the specified row.
	 * The current implementation is stupid, so not public it yet.
	 */
	/*package*/ int getIndex() {
		int j = 0;
		if (_index < 0) {
			for (Iterator it = getParent().getChildren().iterator();
			it.hasNext(); ++j) {
				if (it.next() == this)
					break;
			}
			final Grid grid = getGrid();
			final int offset = grid != null && grid.getModel() != null ? grid.getDataLoader().getOffset() : 0; 
			j += (offset < 0 ? 0 : offset);
		} else {
			j = _index;
		}
		return j;
	}

	public String getZclass() {
		return _zclass == null ? "z-row" : _zclass;
	}
	
	/**
	 * Returns the group that this row belongs to, or null.
	 * @since 3.5.0
	 */
	public Group getGroup() {
		if (this instanceof Group) return (Group)this;
		final Rows rows = (Rows) getParent();
		return (rows != null) ? rows.getGroup(getIndex()) : null;
	}
	/**
	 * Returns the group that this row belongs to, or null.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Group getGroupApi() {
		return getGroup();
	}
	//-- super --//
	/** Returns the style class.
	 * By default, it is the same as grid's stye class, unless
	 * {@link #setSclass} is called with non-empty value.
	 */
	public String getSclass() {
		final String sclass = super.getSclass();
		if (sclass != null) return sclass;

		final Grid grid = getGrid();
		return grid != null ? grid.getSclass(): sclass;
	}
	
	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		
		render(renderer, "align", _align);
		render(renderer, "valign", _valign);
		render(renderer, "nowrap", _nowrap);
		render(renderer, "spans", _spans);
		render(renderer, "_loaded", _loaded);
		if (_index >= 0)
			renderer.render("_index", _index);
	}
	
	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Rows))
			throw new UiException("Unsupported parent for row: "+parent);
		super.beforeParentChanged(parent);
	}
	
	public void beforeChildAdded(Component newChild, Component refChild) {
		if (newChild instanceof Detail) {
			if (_detail != null && _detail != newChild)
				throw new UiException("Only one detail is allowed: "+this);
		}
		super.beforeChildAdded(newChild, refChild);
	}
	public boolean insertBefore(Component newChild, Component refChild) {
		if (newChild instanceof Detail) {
			//move to the first child
			refChild = getChildren().isEmpty() ? null : (Component) getChildren().get(0);
			if (super.insertBefore(newChild, refChild)) {
				_detail = (Detail) newChild;
				return true;
			}
			return false;			
		} else if (refChild != null && refChild == _detail) {
			if (getChildren().size() <= 1) refChild = null;
			else refChild = (Component) getChildren().get(1);
		}
		return super.insertBefore(newChild, refChild);
	}

	public void onChildRemoved(Component child) {
		super.onChildRemoved(child);
		if (_detail == child) _detail = null;
	}

	//Cloneable//
	public Object clone() {
		final Row clone = (Row)super.clone();
		if (_detail != null) clone.afterUnmarshal();
		return clone;
	}
	
	private void afterUnmarshal() {
		for (Iterator it = getChildren().iterator(); it.hasNext();) {
			final Object child = it.next();
			if (child instanceof Detail) {
				_detail = (Detail)child;
				break; //done
			}
		}
	}

	//Serializable//
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		afterUnmarshal();
	}
	
	//-- ComponentCtrl --//
	public Object getExtraCtrl() {
		return new ExtraCtrl();
	}
	/** A utility class to implement {@link #getExtraCtrl}.
	 * It is used only by component developers.
	 */
	protected class ExtraCtrl extends XulElement.ExtraCtrl
	implements LoadStatus {
		//-- LoadStatus --//
		public boolean isLoaded() {
			return Row.this.isLoaded();
		}

		public void setLoaded(boolean loaded) {
			Row.this.setLoaded(loaded);
		}
		
		public void setIndex(int index) {
			_index = index;
		}
	}

}
