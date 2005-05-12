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
 */
package org.caesarj.debug.actions;

import org.caesarj.ui.editor.CaesarEditor;
import org.eclipse.jdt.internal.debug.ui.actions.ManageBreakpointRulerAction;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.AbstractRulerActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Factory for the "ToggleBreakpoint" action for the vertical ruler of the 
 * caesarj-editor.
 *  
 * @author meffert
 */
public class CjBreakpointRulerActionDelegate extends AbstractRulerActionDelegate {

	private IEditorPart fEditorPart;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.AbstractRulerActionDelegate#createAction(org.eclipse.ui.texteditor.ITextEditor, org.eclipse.jface.text.source.IVerticalRulerInfo)
	 */
	protected IAction createAction(ITextEditor editor, IVerticalRulerInfo rulerInfo) {
		// if editor is a caesarj-editor return the caesar breakpoint action,
		// otherwise return the jdt action.
		if(editor instanceof CaesarEditor){
			return new CjBreakpointRulerAction(rulerInfo, editor, fEditorPart);
		}else{
			return new ManageBreakpointRulerAction(rulerInfo, editor);
		}
	}

	/**
	 * @see IEditorActionDelegate#setActiveEditor(bIAction, IEditorPart)
	 */
	public void setActiveEditor(IAction callerAction, IEditorPart targetEditor) {
		fEditorPart = targetEditor;
		super.setActiveEditor(callerAction, targetEditor);
	}
}
