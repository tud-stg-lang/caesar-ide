package org.caesarj.ui.marker;

import org.eclipse.jdt.internal.ui.javaeditor.JavaEditorMessages;
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

	protected IAction createAction(ITextEditor editor,
			IVerticalRulerInfo rulerInfo) {
		return new CaesarSelectMarkerRulerAction(JavaEditorMessages
				.getResourceBundle(), "JavaSelectMarkerRulerAction.", editor,
				rulerInfo);
	}
}