/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright � 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: CaesarSourceViewerConfiguration.java,v 1.6 2011-09-16 16:06:24 gasiunas Exp $
 */

package org.caesarj.ui.editor;

import org.eclipse.jdt.ui.text.IJavaPartitions;
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