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
 * $Id: CaesarSelectMarkerRulerAction.java,v 1.5 2006-10-06 17:05:47 gasiunas Exp $
 */

package org.caesarj.ui.marker;

import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.SelectMarkerRulerAction;

public class CaesarSelectMarkerRulerAction extends SelectMarkerRulerAction {

	private ITextEditor fTextEditor;

	private Position fPosition;

	public CaesarSelectMarkerRulerAction(ResourceBundle bundle, String prefix,
			ITextEditor editor, IVerticalRulerInfo ruler) {
		super(bundle, prefix, editor, ruler);
		fTextEditor = editor;
		//WorkbenchHelp.setHelp(this,
		//	IJavaHelpContextIds.JAVA_SELECT_MARKER_RULER_ACTION);
	}

	public void run() {
		if (fPosition != null) {
			ITextOperationTarget operation = (ITextOperationTarget) fTextEditor
					.getAdapter(ITextOperationTarget.class);
			final int opCode = ISourceViewer.CONTENTASSIST_PROPOSALS;
			if (operation != null && operation.canDoOperation(opCode)) {
				fTextEditor.selectAndReveal(fPosition.getOffset(), fPosition
						.getLength());
				operation.doOperation(opCode);
				return;
			}
		}
		super.run();
	}

	public void update() {
		if (!(fTextEditor instanceof ITextEditorExtension)
				|| ((ITextEditorExtension) fTextEditor).isEditorInputReadOnly()) {
			fPosition = null;
			super.update();
			return;
		}
		fPosition = getJavaAnnotationPosition();
		if (fPosition != null)
			setEnabled(true);
		else
			super.update();
	}

	private Position getJavaAnnotationPosition() {
		AbstractMarkerAnnotationModel model = getAnnotationModel();
		IDocument document = getDocument();
		if (model == null)
			return null;
		ICompilationUnit cu = getCompilationUnit();
		if (cu == null) {
			return null;
		}

		//		boolean hasAssistLightbulb = PreferenceConstants.getPreferenceStore()
		//				.getBoolean(
		//						PreferenceConstants.APPEARANCE_QUICKASSIST_LIGHTBULB);
		Annotation assistAnnotation = null;

		Iterator iter = model.getAnnotationIterator();
		while (iter.hasNext()) {
			Annotation annotation = (Annotation) iter.next();
			if (annotation instanceof MarkerAnnotation) {
				MarkerAnnotation markerAnnotation = (MarkerAnnotation) annotation;
				if (!markerAnnotation.isMarkedDeleted()) {
					Position position = model.getPosition(annotation);
					if (includesRulerLine(position, document))
						return position;
				}
			}
		}
		if (assistAnnotation != null) {
			Position position = model.getPosition(assistAnnotation);
			// no need to check 'JavaCorrectionProcessor.hasAssists': annotation
			// only created when
			// there are assists
			if (includesRulerLine(position, document))
				return position;
		}
		return null;
	}

	private ICompilationUnit getCompilationUnit() {
		IEditorInput input = fTextEditor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			IFile file = ((IFileEditorInput) input).getFile();
			IJavaElement element = JavaCore.create(file);
			if (element instanceof ICompilationUnit)
				return (ICompilationUnit) element;
		}
		return null;
	}
}