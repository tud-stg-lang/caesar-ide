/*
 * Created on 28.07.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.caesarj.ui.editor;

import org.eclipse.jdt.internal.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.RuleBasedScanner;

public class CaesarSourceViewerConfiguration extends JavaSourceViewerConfiguration {

	CaesarTextTools ctt = null;
	
	public CaesarSourceViewerConfiguration(CaesarTextTools textTools, CaesarEditor editor, IPreferenceStore prefStore) 
	{
		super(textTools.getColorManager(), prefStore, editor, IJavaPartitions.JAVA_PARTITIONING);
		this.ctt = textTools;
	}
	
	protected RuleBasedScanner getCodeScanner() {
		return this.ctt.getCodeScanner();
	}
}