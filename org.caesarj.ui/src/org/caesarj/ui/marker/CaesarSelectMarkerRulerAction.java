package org.caesarj.ui.marker;

import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.IJavaHelpContextIds;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.IJavaAnnotation;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension;
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
			final int opCode = CompilationUnitEditor.CORRECTIONASSIST_PROPOSALS;
			if (operation != null && operation.canDoOperation(opCode)) {
				fTextEditor.selectAndReveal(fPosition.getOffset(), fPosition
						.getLength());
				operation.doOperation(opCode);
				return;
			}
			return;
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
			if (annotation instanceof IJavaAnnotation) {
				IJavaAnnotation javaAnnotation = (IJavaAnnotation) annotation;
				if (!javaAnnotation.isMarkedDeleted()) {
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