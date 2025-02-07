/* Tab.java

	Purpose:
		
	Description:
		
	History:
		Tue Jul 12 10:43:18     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul;

import java.util.Set;
import java.util.Iterator;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.*;

import org.zkoss.zul.impl.LabelImageElement;

/**
 * A tab.
 * <p>
 * Default {@link #getZclass}: z-tab. (since 3.5.0)
 * 
 * @author tomyeh
 */
public class Tab extends LabelImageElement implements org.zkoss.zul.api.Tab {
	private boolean _selected;
	/** Whether to show a close button. */
	private boolean _closable;

	private boolean _disabled;
	static {
		addClientEvent(Tab.class, Events.ON_CLOSE, 0);
		addClientEvent(Tab.class, Events.ON_SELECT, CE_IMPORTANT);
	}
	public Tab() {}

	public Tab(String label) {
		super(label);
	}

	public Tab(String label, String image) {
		super(label, image);
	}

	//-- super --//
	public void setWidth(String width) {
		Tabbox tb = getTabbox();
		if (tb != null && tb.isVertical())
			throw new UnsupportedOperationException("Set Tabs' width instead");
		super.setWidth(width);
	}
	
	/**
	 * Returns whether this tab is closable. If closable, a button is displayed
	 * and the onClose event is sent if an user clicks the button.
	 * <p>
	 * Default: false.
	 */
	public boolean isClosable() {
		return _closable;
	}

	/**
	 * Sets whether this tab is closable. If closable, a button is displayed and
	 * the onClose event is sent if an user clicks the button.
	 * <p>
	 * Default: false.
	 * <p>
	 * You can intercept the default behavior by either overriding
	 * {@link #onClose}, or listening the onClose event.
	 */
	public void setClosable(boolean closable) {
		if (_closable != closable) {
			_closable = closable;
			smartUpdate("closable", _closable);
		}
	}

	/**
	 * Process the onClose event sent when the close button is pressed.
	 * <p>
	 * Default: invoke {@link #close} to detach itself and the corresponding {@link Tabpanel}.
	 */
	public void onClose() {
		close();
	}

	/** Closes this tab and the linked tabpanel.
	 * This method detaches this component and the linked {@link Tabpanel}).
	 * @since 5.0.0
	 */
	public void close() {
		if (_selected)
			selectNextTab();
		final Tabpanel panel = getLinkedPanel();
		if (panel != null)
			panel.detach();
		detach();
	}

	private void selectNextTab() {
		for (Tab tab = (Tab) getNextSibling(); tab != null; tab = (Tab) tab.getNextSibling())
			if (!tab.isDisabled()) {
				tab.setSelected(true);
				return;
			}
		for (Tab tab = (Tab) getPreviousSibling(); tab != null; tab = (Tab) tab.getPreviousSibling())
			if (!tab.isDisabled()) {
				tab.setSelected(true);
				return;
			}
	}
	
	/**
	 * Returns the tabbox owns this component.
	 */
	public Tabbox getTabbox() {
		final Tabs tabs = (Tabs) getParent();
		return tabs != null ? tabs.getTabbox() : null;
	}
	/**
	 * Returns the tabbox owns this component.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tabbox getTabboxApi() {
		return getTabbox();
	}

	/**
	 * Returns the panel associated with this tab.
	 */
	public Tabpanel getLinkedPanel() {
		final int j = getIndex();
		if (j >= 0) {
			final Tabbox tabbox = getTabbox();
			if (tabbox != null) {
				final Tabpanels tabpanels = tabbox.getTabpanels();
				if (tabpanels != null && tabpanels.getChildren().size() > j)
					return (Tabpanel) tabpanels.getChildren().get(j);
			}
		}
		return null;
	}
	/**
	 * Returns the panel associated with this tab.
	 * @since 3.5.2
	 */
	public org.zkoss.zul.api.Tabpanel getLinkedPanelApi() {
		return getLinkedPanel();
	}

	/**
	 * Returns whether this tab is selected.
	 */
	public boolean isSelected() {
		return _selected;
	}

	/**
	 * Sets whether this tab is selected.
	 */
	public void setSelected(boolean selected) {
		if (_selected != selected) {
			final Tabbox tabbox = (Tabbox) getTabbox();
			if (tabbox != null) {
				// Note: we don't update it here but let its parent does the job
				tabbox.setSelectedTab(this);
			} else {
				_selected = selected;				
				smartUpdate("selected", _selected);
			}
		}
	}

	/**
	 * Returns whether this tab is disabled.
	 * <p>
	 * Default: false.
	 * 
	 * @since 3.0.0
	 */
	public boolean isDisabled() {
		return _disabled;
	}

	/**
	 * Sets whether this tab is disabled. If a tab is disabled, then it cann't
	 * be selected or closed by user, but it still can be controlled by server
	 * side program.
	 * 
	 * @since 3.0.0
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", _disabled);
		}
	}

	/**
	 * Updates _selected directly without updating the client.
	 */
	/* package */void setSelectedDirectly(boolean selected) {
		_selected = selected;
	}

	/**
	 * Returns the index of this panel, or -1 if it doesn't belong to any tabs.
	 */
	public int getIndex() {
		final Tabs tabs = (Tabs) getParent();
		if (tabs == null)
			return -1;
		int j = 0;
		for (Iterator it = tabs.getChildren().iterator();; ++j)
			if (it.next() == this)
				return j;
	}

	// -- super --//
	public String getZclass() {
		if (_zclass != null) return _zclass;
		final Tabbox tabbox = getTabbox();
		final String added = tabbox != null ? tabbox.inAccordionMold() ? "-" + tabbox.getMold() :
			tabbox.isVertical() ? "-ver" : "" : "";
		return "z-tab" + added;
	}

	// -- Component --//
	/**
	 * No child is allowed.
	 */
	protected boolean isChildable() {
		return false;
	}

	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Tabs))
			throw new UiException("Wrong parent: " + parent);
		super.beforeParentChanged(parent);
	}

	// -- ComponentCtrl --//
	/** Processes an AU request.
	 *
	 * <p>Default: in addition to what are handled by {@link LabelImageElement#service},
	 * it also handles onSelect.
	 * @since 5.0.0
	 */
	public void service(org.zkoss.zk.au.AuRequest request, boolean everError) {
		final String cmd = request.getCommand();
		if (cmd.equals(Events.ON_SELECT)) {
			SelectEvent evt = SelectEvent.getSelectEvent(request);
			Set selItems = evt.getSelectedItems();
			if (selItems == null || selItems.size() != 1)
				throw new UiException("Exactly one selected tab is required: " + selItems); // debug purpose
			final Tabbox tabbox = getTabbox();
			if (tabbox != null)
				tabbox.selectTabDirectly((Tab) selItems.iterator().next(), true);

			Events.postEvent(evt);
		} else
			super.service(request, everError);
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
			throws java.io.IOException {
		super.renderProperties(renderer);
		if (_disabled)
			render(renderer, "disabled", _disabled);
		if (_selected)
			render(renderer, "selected", _selected);
		if (_closable)
			render(renderer, "closable", _closable);
	}
}
