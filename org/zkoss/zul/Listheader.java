/* Listheader.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug  5 13:06:59     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Iterator;
import java.util.List;
import java.util.Comparator;
import java.util.HashMap;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Strings;
import org.zkoss.xml.HTMLs;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.Scopes;

import org.zkoss.zul.impl.GroupsListModel;
import org.zkoss.zul.impl.HeaderElement;

/**
 * The list header which defines the attributes and header of a column
 * of a list box.
 * Its parent must be {@link Listhead}.
 *
 * <p>Difference from XUL:
 * <ol>
 * <li>There is no listcol in ZUL because it is merged into {@link Listheader}.
 * Reason: easier to write Listbox.</li>
 * </ol>
 * <p>Default {@link #getZclass}: z-listheader.(since 5.0.0)
 * @author tomyeh
 */
public class Listheader extends HeaderElement implements org.zkoss.zul.api.Listheader {
    private static final long serialVersionUID = 20080218L;

	private String _sortDir = "natural";
	private transient Comparator _sortAsc, _sortDsc;
	private String _sortAscNm = "none";
	private String _sortDscNm = "none";
	private Object _value;
	private int _maxlength;

	static {
		addClientEvent(Listheader.class, Events.ON_SORT, CE_DUPLICATE_IGNORE);
	}
	
	public Listheader() {
	}
	public Listheader(String label) {
		super(label);
	}
	/* Constructs a list header with label and image.
	 *
	 * @param src the URI of the image. Ignored if null or empty.
	 */
	public Listheader(String label, String src) {
		super(label, src);
	}
	/* Constructs a list header with label, image and width.
	 *
	 * @param src the URI of the image. Ignored if null or empty.
	 * @param width the width of the column. Ignored if null or empty.
	 * @since 3.0.4
	 */
	public Listheader(String label, String src, String width) {
		super(label, src);
		setWidth(width);
	}

	/** Returns the listbox that this belongs to.
	 */
	public Listbox getListbox() {
		final Component comp = getParent();
		return comp != null ? (Listbox)comp.getParent(): null;
	}
	/** Returns the listbox that this belongs to.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Listbox getListboxApi() {
		return getListbox();
	}

	/** Returns the value.
	 * <p>Default: null.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * @since 3.6.0
	 */
	public Object getValue() {
		return _value;
	}
	/** Sets the value.
	 * @param value the value.
	 * <p>Note: the value is application dependent, you can place
	 * whatever value you want.
	 * @since 3.6.0
	 */
	public void setValue(Object value) {
		_value = value;
	}

	/** Returns the sort direction.
	 * <p>Default: "natural".
	 */
	public String getSortDirection() {
		return _sortDir;
	}
	/** Sets the sort direction. This does not sort the data, it only serves
	 * as an indicator as to how the list is sorted.
	 *
	 * <p>If you use {@link #sort(boolean)} to sort list items,
	 * the sort direction is maintained automatically.
	 * If you want to sort it in customized way, you have to set the
	 * sort direction manaully.
	 *
	 * @param sortDir one of "ascending", "descending" and "natural"
	 */
	public void setSortDirection(String sortDir) throws WrongValueException {
		if (sortDir == null || (!"ascending".equals(sortDir)
		&& !"descending".equals(sortDir) && !"natural".equals(sortDir)))
			throw new WrongValueException("Unknown sort direction: "+sortDir);
		if (!Objects.equals(_sortDir, sortDir)) {
			_sortDir = sortDir;
			smartUpdate("sortDirection", _sortDir); //don't use null because sel.js assumes it
		}
	}

	/** Sets the type of the sorter.
	 * You might specify either "auto", "auto(FIELD_NAME1[,FIELD_NAME2] ...)"(since 3.5.3) or "none".
	 * 
	 * <p>If "client" or "client(number)" is specified,
	 * the sort functionality will be done by Javascript at client without notifying
	 * to server, that is, the order of the component in the row is out of sync.
	 * <ul>
	 * <li> "client" : it is treated by a string</li>
	 * <li> "client(number)" : it is treated by a number</li>
	 * </ul>
	 * <p>Note: client sorting cannot work in model case. (since 5.0.0)
	 * 
	 * <p>If "auto" is specified,
	 * {@link #setSortAscending} and/or {@link #setSortDescending} 
	 * are called with {@link ListitemComparator}, if
	 * {@link #getSortDescending} and/or {@link #getSortAscending} are null.
	 * If you assigned a comparator to them, it won't be affected.
	 * The auto created comparator is case-insensitive.
	 *
	 * <p>If "auto(FIELD_NAME1, FIELD_NAME2, ...)" is specified,
	 * {@link #setSortAscending} and/or {@link #setSortDescending} 
	 * are called with {@link FieldComparator}, if
	 * {@link #getSortDescending} and/or {@link #getSortAscending} are null.
	 * If you assigned a comparator to them, it won't be affected.
	 * The auto created comparator is case-insensitive.

	 * <p>If "none" is specified, both {@link #setSortAscending} and
	 * {@link #setSortDescending} are called with null.
	 * Therefore, no more sorting is available to users for this column.
	 */
	public void setSort(String type) {
		if (type == null) return;
		if (type.startsWith("client")) {
			try {
				setSortAscending(type);
				setSortDescending(type);
			} catch (Throwable ex) {
				throw UiException.Aide.wrap(ex); //not possible to throw ClassNotFoundException...
			}
		} else if ("auto".equals(type)) {
			if (getSortAscending() == null)
				setSortAscending(new ListitemComparator(this, true, true));
			if (getSortDescending() == null)
				setSortDescending(new ListitemComparator(this, false, true));
		} else if (!Strings.isBlank(type) && type.startsWith("auto")) {
			final int j = type.indexOf('(');
			final int k = type.lastIndexOf(')');
			if (j >= 0 && k >= 0) {
				final String fieldnames = type.substring(j+1, k);
				if (getSortAscending() == null)
					setSortAscending(new FieldComparator(fieldnames, true));
				if (getSortDescending() == null)
					setSortDescending(new FieldComparator(fieldnames, false));
			} else {
				throw new UiException("Unknown sort type: "+type);
			}
		} else if ("none".equals(type)) {
			setSortAscending((Comparator)null);
			setSortDescending((Comparator)null);
		}
	}

	/** Returns the ascending sorter, or null if not available.
	 */
	public Comparator getSortAscending() {
		return _sortAsc;
	}
	/** Sets the ascending sorter, or null for no sorter for
	 * the ascending order.
	 *
	 * @param sorter the comparator used to sort the ascending order.
	 * If you are using the group feature, you can pass an instance of
	 * {@link GroupComparator} to have a better control.
	 * If an instance of {@link GroupComparator} is passed,
	 * {@link GroupComparator#compareGroup} is used to group elements,
	 * and {@link GroupComparator#compare} is used to sort elements
	 * with a group.
	 * Otherwise, {@link Comparator#compare} is used to group elements
	 * and sort elements within a group.
	 */
	public void setSortAscending(Comparator sorter) {
		if (!Objects.equals(_sortAsc, sorter)) {
			_sortAsc = sorter;
			String nm = _sortAsc == null ? "none" : "fromServer";
			if (!_sortAscNm.equals(nm)) {
				_sortAscNm = nm;
				smartUpdate("sortAscending", _sortAscNm);
			}
		}
	}
	/** Sets the ascending sorter with the class name, or null for
	 * no sorter for the ascending order.
	 */
	public void setSortAscending(String clsnm)
	throws ClassNotFoundException, InstantiationException,
	IllegalAccessException {
		if (!Strings.isBlank(clsnm) && clsnm.startsWith("client") && !_sortAscNm.equals(clsnm)) {
			_sortAscNm = clsnm;
			smartUpdate("sortAscending", clsnm);
		} else
			setSortAscending(toComparator(clsnm));
	}

	/** Returns the descending sorter, or null if not available.
	 */
	public Comparator getSortDescending() {
		return _sortDsc;
	}
	/** Sets the descending sorter, or null for no sorter for the
	 * descending order.
	 *
	 * @param sorter the comparator used to sort the ascending order.
	 * If you are using the group feature, you can pass an instance of
	 * {@link GroupComparator} to have a better control.
	 * If an instance of {@link GroupComparator} is passed,
	 * {@link GroupComparator#compareGroup} is used to group elements,
	 * and {@link GroupComparator#compare} is used to sort elements
	 * with a group.
	 * Otherwise, {@link Comparator#compare} is used to group elements
	 * and sort elements within a group.
	 */
	public void setSortDescending(Comparator sorter) {
		if (!Objects.equals(_sortDsc, sorter)) {
			_sortDsc = sorter;
			String nm = _sortDsc == null ? "none" : "fromServer";
			if (!_sortDscNm.equals(nm)) {
				_sortDscNm = nm;
				smartUpdate("sortDescending", _sortDscNm);
			}
		}
	}
	/** Sets the descending sorter with the class name, or null for
	 * no sorter for the descending order.
	 */
	public void setSortDescending(String clsnm)
	throws ClassNotFoundException, InstantiationException,
	IllegalAccessException {
		if (!Strings.isBlank(clsnm) && clsnm.startsWith("client") && !_sortDscNm.equals(clsnm)) {
			_sortDscNm = clsnm;
			smartUpdate("sortDescending", clsnm);
		} else
			setSortDescending(toComparator(clsnm));
	}

	private Comparator toComparator(String clsnm)
	throws ClassNotFoundException, InstantiationException,
	IllegalAccessException {
		if (clsnm == null || clsnm.length() == 0) return null;

		final Page page = getPage();
		final Class cls = page != null ?
			page.getZScriptClass(clsnm): Classes.forNameByThread(clsnm);
		if (cls == null)
			throw new ClassNotFoundException(clsnm);
		if (!Comparator.class.isAssignableFrom(cls))
			throw new UiException("Comparator must be implemented: "+clsnm);
		return (Comparator)cls.newInstance();
	}

	/** Returns the maximal length of each item's label.
	 * <p>Default: 0 (no limit).
	 */
	public int getMaxlength() {
		return _maxlength;
	}
	/** Sets the maximal length of each item's label.
	 * <p>Default: 0 (no limit).
	 * <p>Notice that maxlength will be applied to this header and all
	 * listcell of the same column.
	 */
	public void setMaxlength(int maxlength) {
		if (maxlength < 0) maxlength = 0;
		if (_maxlength != maxlength) {
			_maxlength = maxlength;
			smartUpdate("maxlength", maxlength);
		}
	}

	/** Returns the column index, starting from 0.
	 */
	public int getColumnIndex() {
		int j = 0;
		for (Iterator it = getParent().getChildren().iterator();
		it.hasNext(); ++j)
			if (it.next() == this)
				break;
		return j;
	}

	/** Sorts the list items based on {@link #getSortAscending}
	 * and {@link #getSortDescending}, if {@link #getSortDirection} doesn't
	 * matches the ascending argument.
	 *
	 * <p>It checks {@link #getSortDirection} to see whether sorting
	 * is required, and update {@link #setSortDirection} after sorted.
	 * For example, if {@link #getSortDirection} returns "ascending" and
	 * the ascending argument is false, nothing happens.
	 * To enforce the sorting, you can invoke {@link #setSortDirection}
	 * with "natural" before invoking this method.
	 * Alternatively, you can invoke {@link #sort(boolean, boolean)} instead.
	 *
	 * <p>It sorts the listitem by use of {@link Components#sort}
	 * data (i.e., {@link Grid#getModel} is null).
	 *
	 * <p>On the other hand, it invokes {@link ListModelExt#sort} to sort
	 * the list item, if live data (i.e., {@link Listbox#getModel} is not null).
	 * In other words, if you use the live data, you have to implement
	 * {@link ListModelExt} to sort the live data explicitly.
	 *
	 * @param ascending whether to use {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @return whether the list items are sorted.
	 * @exception UiException if {@link Listbox#getModel} is not
	 * null but {@link ListModelExt} is not implemented.
	 */
	public boolean sort(boolean ascending) {
		final String dir = getSortDirection();
		if (ascending) {
			if ("ascending".equals(dir)) return false;
		} else {
			if ("descending".equals(dir)) return false;
		}

		final Comparator cmpr = ascending ? _sortAsc: _sortDsc;
		if (cmpr == null) return false;

		final Listbox box = getListbox();
		if (box == null) return false;

		//comparator might be zscript
		Scopes.beforeInterpret(this);
		try {
			final ListModel model = box.getModel();
			boolean isPagingMold = box.inPagingMold();
			int activePg = isPagingMold ? box.getPaginal().getActivePage() : 0;
			if (model != null) { //live data
				if (model instanceof GroupsListModel) {
					((GroupsListModel)model).sort(cmpr, ascending,
						box.getListhead().getChildren().indexOf(this));
				} else {
					if (!(model instanceof ListModelExt))
						throw new UiException("ListModelExt must be implemented in "+model.getClass().getName());
					((ListModelExt)model).sort(cmpr, ascending);
				}
			} else { //not live data
				sort0(box, cmpr);
			}
			if (isPagingMold) box.getPaginal().setActivePage(activePg);
				// Because of maintaining the number of the visible item, we cause
				// the wrong active page when dynamically add/remove the item (i.e. sorting).
				// Therefore, we have to reset the correct active page.
		} finally {
			Scopes.afterInterpret();
		}

		//maintain
		for (Iterator it = box.getListhead().getChildren().iterator();
		it.hasNext();) {
			final Listheader hd = (Listheader)it.next();
			hd.setSortDirection(
				hd != this ? "natural": ascending ? "ascending": "descending");
		}

		// sometimes the items at client side are out of date
		box.invalidate();
		
		return true;
	}
	/** Sorts the items. If with group, each group is sorted independently.
	 */
	private static void sort0(Listbox box, Comparator cmpr) {
		if (box.hasGroup())
			for (Iterator it = box.getGroups().iterator(); it.hasNext();) {
				Listgroup g = (Listgroup)it.next();
				Components.sort(box.getItems(), g.getIndex()+1, g.getIndex()+1 + g.getItemCount(), cmpr);
			}
		else Components.sort(box.getItems(), cmpr);
	}
	/** Sorts the list items based on {@link #getSortAscending}
	 * and {@link #getSortDescending}.
	 *
	 * @param ascending whether to use {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @param force whether to enforce the sorting no matter what the sort
	 * direction ({@link #getSortDirection}) is.
	 * If false, this method is the same as {@link #sort(boolean)}.
	 * @return whether the rows are sorted.
	 */
	public boolean sort(boolean ascending, boolean force) {
		if (force) setSortDirection("natural");
		return sort(ascending);
	}

	//-- event listener --//
	/** It invokes {@link #sort(boolean)} to sort list items and maintain
	 * {@link #getSortDirection}.
	 */
	public void onSort() {
		final String dir = getSortDirection();
		if ("ascending".equals(dir)) sort(false);
		else if ("descending".equals(dir)) sort(true);
		else if (!sort(true)) sort(false);
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-listheader" : _zclass;
	}

	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Listhead))
			throw new UiException("Wrong parent: "+parent);
		super.beforeParentChanged(parent);
	}

	// super
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);
		
		if (!"none".equals(_sortDscNm))
			render(renderer, "sortDescending", _sortDscNm);

		if (!"none".equals(_sortAscNm))
			render(renderer, "sortAscending", _sortAscNm);
		
		if (!"natural".equals(_sortDir))
			render(renderer, "sortDirection", _sortDir);

		if (_maxlength > 0)
			renderer.render("maxlength", _maxlength);

		org.zkoss.zul.impl.Utils.renderCrawlableText(getLabel());
	}
	
	//Cloneable//
	public Object clone() {
		final Listheader clone = (Listheader)super.clone();
		clone.fixClone();
		return clone;
	}
	private void fixClone() {
		if (_sortAsc instanceof ListitemComparator) {
			final ListitemComparator c = (ListitemComparator)_sortAsc;
			if (c.getListheader() == this && c.isAscending())
				_sortAsc =
					new ListitemComparator(this, true, c.shallIgnoreCase());
		}
		if (_sortDsc instanceof ListitemComparator) {
			final ListitemComparator c = (ListitemComparator)_sortDsc;
			if (c.getListheader() == this && !c.isAscending())
				_sortDsc =
					new ListitemComparator(this, false, c.shallIgnoreCase());
		}
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		boolean written = false;
		if (_sortAsc instanceof ListitemComparator) {
			final ListitemComparator c = (ListitemComparator)_sortAsc;
			if (c.getListheader() == this && c.isAscending()) {
				s.writeBoolean(true);
				s.writeBoolean(c.shallIgnoreCase());
				s.writeBoolean(c.byValue());
				written = true;
			}
		}
		if (!written) {
			s.writeBoolean(false);
			s.writeObject(_sortAsc);
		}

		written = false;
		if (_sortDsc instanceof ListitemComparator) {
			final ListitemComparator c = (ListitemComparator)_sortDsc;
			if (c.getListheader() == this && !c.isAscending()) {
				s.writeBoolean(true);
				s.writeBoolean(c.shallIgnoreCase());
				s.writeBoolean(c.byValue());
				written = true;
			}
		}
		if (!written) {
			s.writeBoolean(false);
			s.writeObject(_sortDsc);
		}
	}
	private synchronized void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();

		boolean b = s.readBoolean();
		if (b) {
			final boolean igcs = s.readBoolean();
			final boolean byval = s.readBoolean();
			_sortAsc = new ListitemComparator(this, true, igcs, byval);
		} else {
			//bug #2830325 FieldComparator not castable to ListItemComparator
			_sortAsc = (Comparator)s.readObject();
		}

		b = s.readBoolean();
		if (b) {
			final boolean igcs = s.readBoolean();
			final boolean byval = s.readBoolean();
			_sortDsc = new ListitemComparator(this, false, igcs, byval);
		} else {
			//bug #2830325 FieldComparator not castable to ListItemComparator
			_sortDsc = (Comparator)s.readObject();
		}
	}
}
