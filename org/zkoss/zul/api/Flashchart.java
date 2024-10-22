/* Flashchart.java

	Purpose:
		
	Description:
		
	History:
		Nov 26, 2009 12:19:18 AM , Created by joy

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.api;

import org.zkoss.zul.ChartModel;

/**
 * A generic flashchart component.
 * 
 * @author Joy Lo
 * @since 5.0.0
 */
public interface Flashchart extends Flash {
	/**
	 * Sets the type of chart
	 * <p>Default: "pie"
	 * <p>Types: pie, line, bar, column
	 */
	public void setType(String type);
	/**
	 * Returns the type of chart
	 */
	public String getType();
	/**
	 * Sets the model of chart.
	 * <p>Only implement models which matched the allowed types
	 * @param model
	 * @see #setType(String)
	 */
	public void setModel(ChartModel model);
	/**
	 * Returns the model of chart
	 */
	public ChartModel getModel();	
	/**
	 * Sets X-Axis name of chart. If doesn't set this attribute, then default will shows Series 2.
	 * <p>Default: Series 2
	 * <p>Only used for StackColumnChart and it only works when the chart initial.
	 */
	public void setXaxis(String xAxis);
	/**
	 * Returns the name of X-Axis
	 */
	public String getXaxis();
	/**
	 * Sets Y-Axis name of chart. If doesn't set this attribute, then default will shows Series 1.
	 * <p>Default: Series 1
	 * <p>Only used for StackColumnChart and it only works when the chart initial.
	 */
	public void setYaxis(String yAxis);
	/**
	 * Returns the name of Y-Axis
	 */
	public String getYaxis();
	/**
	 * Sets the content style of flashchart.
	 * <p>Default format: "Category-Attribute=Value", ex."legend-display=right"
	 */
	public void setChartStyle(String chartStyle);
	/**
	 * Returns the content style.
	 */
	public String getChartStyle();
}
