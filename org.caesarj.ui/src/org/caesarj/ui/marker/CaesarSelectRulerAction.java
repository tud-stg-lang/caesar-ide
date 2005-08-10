/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
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
 * $Id: CaesarSelectRulerAction.java,v 1.3 2005-08-10 16:09:33 thiago Exp $
 */

package org.caesarj.ui.marker;

import java.util.ResourceBundle;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.AbstractRulerActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * @author Shadow
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class CaesarSelectRulerAction extends AbstractRulerActionDelegate {

    private static final String BUNDLE_FOR_CONSTRUCTED_KEYS= "org.eclipse.jdt.internal.ui.javaeditor.ConstructedJavaEditorMessages";//$NON-NLS-1$
    private static ResourceBundle fgBundleForConstructedKeys = ResourceBundle.getBundle(BUNDLE_FOR_CONSTRUCTED_KEYS);
    
	protected IAction createAction(ITextEditor editor,
			IVerticalRulerInfo rulerInfo) {
        
        // JavaEditorMessages was made package protected in eclipse 3.1
        // The code to get the bundle was just copied here. Maybe it will
        // be slow, maybe even not work, but we can compile.
		return new CaesarSelectMarkerRulerAction(
                fgBundleForConstructedKeys,
                //JavaEditorMessages.getResourceBundle(),
                "JavaSelectMarkerRulerAction.",
                editor,
				rulerInfo);
	}
}