package org.caesarj.ui.views;

/*
 * "The Java Developer's Guide to Eclipse"
 *   by Shavor, D'Anjou, Fairbrother, Kehn, Kellerman, McCarthy
 * 
 * (C) Copyright International Business Machines Corporation, 2003. 
 * All Rights Reserved.
 * 
 * Code or samples provided herein are provided without warranty of any kind.
 */

/**
 * Notification protocol between <code>JavaModel</code> instances and its interested parties.
 */
public interface ICaesarJMetricsListener {
	/**
	 * Notification of a change in the <code>JavaModel</code> instance.
	 * 
	 * @param	jm	instance that has changed.
	 * 
	 * @see 	CaesarMetrics#addListener(ICaesarJMetricsListener)
	 * @see	CaesarMetrics#removeListener(ICaesarJMetricsListener)
	 */
	void refresh(CaesarMetrics jm);
}
