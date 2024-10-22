/* Comboitem.java

	Purpose:
		
	Description:
		
	History:
		Thu Dec 15 17:33:35     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.lang.Objects;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.impl.LabelImageElement;

/**
 * An item of a combo box.
 *
 * <p>Non-XUL extension. Refer to {@link Combobox}.
 * 
 * <p>Default {@link #getZclass}: z-comboitem. (since 5.0.0)
 *
 * @author tomyeh
 * @see Combobox
 */
public class Comboitem extends LabelImageElement implements org.zkoss.zul.api.Comboitem {
	private String _desc = "";
	private Object _value;
	private String _content = "";
	private boolean _disabled;

	public Comboitem() {
	}
	public Comboitem(String label) {
		super(label);
	}
	public Comboitem(String label, String image) {
		super(label, image);
	}
	
	/**
	 * Sets whether it is disabled.
	 * @since 3.0.1
	 */
	public void setDisabled(boolean disabled) {
		if (_disabled != disabled) {
			_disabled = disabled;
			smartUpdate("disabled", disabled);
		}
	}
	
	/** Returns whether it is disabled.
	 * <p>Default: false.
	 * @since 3.0.1
	 */
	public boolean isDisabled() {
		return _disabled;
	}
	
	/** Returns the description (never null).
	 * The description is used to provide extra information such that
	 * users is easy to make a selection.
	 * <p>Default: "".
	 * <p>Deriving class can override it to return whatever it wants
	 * other than null.
	 */
	public String getDescription() {
		return _desc;
	}
	/** Sets the description.
	 */
	public void setDescription(String desc) {
		if (desc == null) desc = "";
		if (!_desc.equals(desc)) {
			_desc = desc;
			smartUpdate("description", getDescription()); //alow overriding
		}
	}

	/** Returns the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 *
	 * @see #getDescription
	 * @since 3.0.0
	 */
	public String getContent() {
		return _content;
	}
	/** Sets the embedded content (i.e., HTML tags) that is
	 * shown as part of the description.
	 *
	 * <p>It is useful to show the description in more versatile way.
	 *
	 * <p>Default: empty ("").
	 *
	 * <p>Deriving class can override it to return whatever it wants
	 * other than null.
	 *
	 * <h3>Security Note</h3>
	 * <p>Unlike other methods, the content assigned to this method
	 * is generated directly to the browser without escaping.
	 * Thus, it is better not to have something input by the user to avoid
	 * any <a href="http://books.zkoss.org/wiki/ZK_Developer%27s_Guide/Advanced_ZK/Security_Tip/Cross-site_scripting#The_content_Property_of_html_and_comboitem">XSS</a>
	 * attach.
	 * @see #setDescription
	 * @since 3.0.0
	 */
	public void setContent(String content) {
		if (content == null) content = "";
		if (!Objects.equals(_content, content)) {
			_content = content;
			smartUpdate("content", getContent()); //allow overriding getContent()
		}
	}

	/** Returns the value associated with this combo item.
	 * The value is application dependent. It can be anything.
	 *
	 * <p>It is usually used with {@link Combobox#getSelectedItem}.
	 * For example,
	 * <code>combobox.getSelectedItem().getValue()</code>
	 *
	 * @see Combobox#getSelectedItem
	 * @see #setValue
	 * @since 2.4.0
	 */
	public Object getValue() {
		return _value;
	}
	/** Associate the value with this combo item.
	 * The value is application dependent. It can be anything.
	 *
	 * @see Combobox#getSelectedItem
	 * @see #getValue
	 * @since 2.4.0
	 */
	public void setValue(Object value) {
		_value = value;
	}

	//-- super --//
	public String getZclass() {
		return _zclass == null ? "z-comboitem" : _zclass;
	}	
	public void setLabel(String label) {
		final String old = getLabel();
		if (!Objects.equals(old, label)) {
			final Combobox cb = (Combobox)getParent();
			final boolean reIndex = cb != null && cb.getSelectedItemDirectly() == this;

			super.setLabel(label);
			
			if (reIndex) {
				final Constraint constr = cb.getConstraint();
				if (constr != null && constr instanceof SimpleConstraint 
						&& (((SimpleConstraint)constr).getFlags() & SimpleConstraint.STRICT) != 0) {
					cb.setValue(label);
				} else {
					cb.reIndexRequired();
				}
			}
		}
	}
	protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer)
	throws java.io.IOException {
		super.renderProperties(renderer);

		render(renderer, "disabled", _disabled);
		render(renderer, "description", getDescription()); //allow overriding getDescription()
		render(renderer, "content", getContent()); //allow overriding getContent()
	}

	public void beforeParentChanged(Component parent) {
		if (parent != null && !(parent instanceof Combobox))
			throw new UiException("Comboitem's parent must be Combobox");		
		super.beforeParentChanged(parent);
	}

	/** No child is allowed. */
	protected boolean isChildable() {
		return false;
	}
}
