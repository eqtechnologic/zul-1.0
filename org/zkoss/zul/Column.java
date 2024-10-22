/* Column.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 25 16:02:36     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import java.util.Collections;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Comparator;
import java.util.List;

import org.zkoss.lang.Objects;
import org.zkoss.lang.Classes;
import org.zkoss.lang.Strings;
import org.zkoss.mesg.Messages;
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
import org.zkoss.zul.mesg.MZul;

/**
 * A single column in a {@link Columns} element.
 * Each child of the {@link Column} element is placed in each successive
 * cell of the grid.
 * The column with the most child elements determines the number of rows
 * in each column.
 *
 * <p>The use of column is mainly to define attributes for each cell
 * in the grid.
 * 
 * <p>Default {@link #getZclass}: z-column. (since 3.5.0)
 *
 * @author tomyeh
 */
public class Column extends HeaderElement implements org.zkoss.zul.api.Column{
	private String _sortDir = "natural";
	private transient Comparator _sortAsc, _sortDsc;
	private String _sortAscNm = "none";
	private String _sortDscNm = "none";
	private Object _value;

	static {
		addClientEvent(Column.class, Events.ON_SORT, CE_DUPLICATE_IGNORE);
		addClientEvent(Column.class, Events.ON_GROUP, CE_DUPLICATE_IGNORE);
	}
	
	public Column() {
	}
	public Column(String label) {
		super(label);
	}
	/* Constructs a grid header with label and image.
	 *
	 * @param lable the label. No label if null or empty.
	 * @param src the URI of the image, or null to ignore.
	 */
	public Column(String label, String src) {
		super(label, src);
	}
	/* Constructs a grid header with label, image and width.
	 *
	 * @param lable the label. No label if null or empty.
	 * @param src the URI of the image. Ignored if null or empty.
	 * @param width the width of the column. Ignored if null or empty.
	 * @since 3.0.4
	 */
	public Column(String label, String src, String width) {
		super(label, src);
		setWidth(width);
	}

	/** Returns the grid that contains this column. */
	public Grid getGrid() {
		final Component parent = getParent();
		return parent != null ? (Grid)parent.getParent(): null;
	}
	/** Returns the grid that contains this column.
	 * @since 3.5.2 
	 * */
	public org.zkoss.zul.api.Grid getGridApi() {		
		return getGrid();
	}
	
	/** Returns the sort direction.
	 * <p>Default: "natural".
	 */
	public String getSortDirection() {
		return _sortDir;
	}
	/** Sets the sort direction. This does not sort the data, it only serves
	 * as an indicator as to how the grid is sorted.
	 *
	 * <p>If you use {@link #sort(boolean)} to sort rows ({@link Row}),
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
			smartUpdate("sortDirection", _sortDir);
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
	 * are called with {@link RowComparator}, if
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
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 * @since 3.5.3
	 */
	public void setSort(String type) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (type == null) return;
		if (type.startsWith("client")) {
			setSortAscending(type);
			setSortDescending(type);
		} else if ("auto".equals(type)) {
			if (getSortAscending() == null)
				setSortAscending(new RowComparator(this, true, false, false));
			if (getSortDescending() == null)
				setSortDescending(new RowComparator(this, false, false, false));
		} else if (type.startsWith("auto")) {
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

	/** Sorts the rows ({@link Row}) based on {@link #getSortAscending}
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
	 * <p>It sorts the rows by use of {@link Components#sort}, if not live
	 * data (i.e., {@link Grid#getModel} is null).
	 *
	 * <p>On the other hand, it invokes {@link ListModelExt#sort} to sort
	 * the rows, if live data (i.e., {@link Grid#getModel} is not null).
	 * In other words, if you use the live data, you have to implement
	 * {@link ListModelExt} to sort the live data explicitly.
	 *
	 * @param ascending whether to use {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @return whether the rows are sorted.
	 * @exception UiException if {@link Grid#getModel} is not
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

		final Grid grid = getGrid();
		if (grid == null) return false;
		final Rows rows = grid.getRows();
		if (rows == null) return false;

		//comparator might be zscript
		Scopes.beforeInterpret(this);
		try {
			final ListModel model = grid.getModel();
			boolean isPagingMold = grid.inPagingMold();
			int activePg = isPagingMold ? grid.getPaginal().getActivePage() : 0;
			if (model != null) { //live data
				if (model instanceof GroupsListModel) {
					((GroupsListModel)model).sort(cmpr, ascending,
						grid.getColumns().getChildren().indexOf(this));
				} else {
					if (!(model instanceof ListModelExt))
						throw new UiException("ListModelExt must be implemented in "+model.getClass());
					((ListModelExt)model).sort(cmpr, ascending);
					//CONSIDER: provide index for sort
				}
			} else { //not live data
				sort0(grid, cmpr);
			}
			if (isPagingMold) grid.getPaginal().setActivePage(activePg);
				// Because of maintaining the number of the visible item, we cause
				// the wrong active page when dynamically add/remove the item (i.e. sorting).
				// Therefore, we have to reset the correct active page.
		} finally {
			Scopes.afterInterpret();
		}
		fixDirection(grid, ascending);

		// sometimes the items at client side are out of date
		grid.getRows().invalidate();
		return true;
	}
	/** Sorts the rows. If with group, each group is sorted independently.
	 */
	private static void sort0(Grid grid, Comparator cmpr) {
		final Rows rows = grid.getRows();
		if (rows.hasGroup())
			for (Iterator it = rows.getGroups().iterator(); it.hasNext();) {
				Group g = (Group)it.next();
				int index = g.getIndex() + 1;
				Components.sort(rows.getChildren(), index, index + g.getItemCount(), cmpr);
			}
		else Components.sort(rows.getChildren(), cmpr);
	}
	
	private void fixDirection(Grid grid, boolean ascending) {
		//maintain
		for (Iterator it = grid.getColumns().getChildren().iterator();
		it.hasNext();) {
			final Column hd = (Column)it.next();
			hd.setSortDirection(
				hd != this ? "natural": ascending ? "ascending": "descending");
		}
	}
	/** Sorts the rows ({@link Row}) based on {@link #getSortAscending}
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
	/**
	 * Groups and sorts the rows ({@link Row}) based on
	 * {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * 
	 * @param ascending whether to use {@link #getSortAscending}.
	 * If the corresponding comparator is not set, it returns false
	 * and does nothing.
	 * @return whether the rows are grouped.
	 * @since 3.5.0
	 */
	public boolean group(boolean ascending) {
		final String dir = getSortDirection();		
		if (ascending) {
			if ("ascending".equals(dir)) return false;
		} else {
			if ("descending".equals(dir)) return false;
		}
		final Comparator cmpr = ascending ? _sortAsc: _sortDsc;
		if (cmpr == null) return false;
		
		final Grid grid = getGrid();
		if (grid == null) return false;
		
		//comparator might be zscript
		Scopes.beforeInterpret(this);
		try {
			final ListModel model = grid.getModel();
			int index = grid.getColumns().getChildren().indexOf(this);
			if (model != null) { //live data
				if (!(model instanceof GroupsListModel))
					throw new UiException("GroupsModel must be implemented in "+model.getClass().getName());
				((GroupsListModel)model).group(cmpr, ascending, index);
			} else { // not live data
				final Rows rows = grid.getRows();
				if (rows == null) return false;//Avoid grid with null group		
				if (rows.hasGroup()) {
					final List groups = new ArrayList(rows.getGroups());
					for (Iterator it = groups.iterator(); it.hasNext();)
						((Group)it.next()).detach(); // Groupfoot is removed automatically, if any.
				}
				
				Comparator cmprx;
				if(cmpr instanceof GroupComparator){
					cmprx = new Comparator(){
						public int compare(Object o1, Object o2) {
							return ((GroupComparator)cmpr).compareGroup(o1, o2);
						}
					};
				}else{
					cmprx = cmpr;
				}

				final List children = new LinkedList(rows.getChildren());
				rows.getChildren().clear();
				Collections.sort(children, cmprx);

				Row previous = null;
				for (Iterator it = children.iterator(); it.hasNext();) {
					final Row row = (Row) it.next();
					it.remove();
					if (previous == null || cmprx.compare(previous, row) != 0) {
						//new group
						final List cells = row.getChildren();
						if (cells.size() < index)
							throw new IndexOutOfBoundsException(
									"Index: "+index+" but size: "+ cells.size());
						Group group;
						Component cell = (Component)cells.get(index);
						if (cell instanceof Label) {
							String val = ((Label)cell).getValue();
							group = new Group(val);
						} else {
							Component cc = cell.getFirstChild();
							if (cc instanceof Label) {
								String val = ((Label)cc).getValue();
								group = new Group(val);
							} else {
								group = new Group(Messages.get(MZul.GRID_OTHER));
							}
						}
						rows.appendChild(group);
					}
					rows.appendChild(row);
					previous = row;
				}

				if (cmprx != cmpr)
					sort0(grid, cmpr); //need to sort each group
			}
		} finally {
			Scopes.afterInterpret();
		}

		fixDirection(grid, ascending);
	
		// sometimes the items at client side are out of date
		grid.getRows().invalidate();
		return true;
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

		org.zkoss.zul.impl.Utils.renderCrawlableText(getLabel());
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
	
	/** It invokes {@link #group(boolean)} to group list items and maintain
	 * {@link #getSortDirection}.
	 */
	public void onGroup() {
		final String dir = getSortDirection();
		if ("ascending".equals(dir)) group(false);
		else if ("descending".equals(dir)) group(true);
		else if (!group(true)) group(false);
	}

	public String getZclass() {
		return _zclass == null ? "z-column" : _zclass;
	}

	//-- Component --//
	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Columns))
			throw new UiException("Unsupported parent for column: "+parent);
		super.beforeParentChanged(parent);
	}
	

	//Cloneable//
	public Object clone() {
		final Column clone = (Column)super.clone();
		clone.fixClone();
		return clone;
	}
	private void fixClone() {
		if (_sortAsc instanceof RowComparator) {
			final RowComparator c = (RowComparator)_sortAsc;
			if (c.getColumn() == this && c.isAscending())
				_sortAsc =
					new RowComparator(this, true, c.shallIgnoreCase(), false);
		}
		if (_sortDsc instanceof RowComparator) {
			final RowComparator c = (RowComparator)_sortDsc;
			if (c.getColumn() == this && !c.isAscending())
				_sortDsc =
					new RowComparator(this, false, c.shallIgnoreCase(), false);
		}
	}

	//Serializable//
	//NOTE: they must be declared as private
	private synchronized void writeObject(java.io.ObjectOutputStream s)
	throws java.io.IOException {
		s.defaultWriteObject();

		boolean written = false;
		if (_sortAsc instanceof RowComparator) {
			final RowComparator c = (RowComparator)_sortAsc;
			if (c.getColumn() == this && c.isAscending()) {
				s.writeBoolean(true);
				s.writeBoolean(c.shallIgnoreCase());
				written = true;
			}
		}
		if (!written) {
			s.writeBoolean(false);
			s.writeObject(_sortAsc);
		}

		written = false;
		if (_sortDsc instanceof RowComparator) {
			final RowComparator c = (RowComparator)_sortDsc;
			if (c.getColumn() == this && !c.isAscending()) {
				s.writeBoolean(true);
				s.writeBoolean(c.shallIgnoreCase());
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
			_sortAsc = new RowComparator(this, true, igcs, false);
		} else {
			//bug #2830325 FieldComparator not castable to ListItemComparator
			_sortAsc = (Comparator)s.readObject();
		}

		b = s.readBoolean();
		if (b) {
			final boolean igcs = s.readBoolean();
			_sortDsc = new RowComparator(this, false, igcs, false);
		} else {
			//bug #2830325 FieldComparator not castable to ListItemComparator
			_sortDsc = (Comparator)s.readObject();
		}
	}
}
