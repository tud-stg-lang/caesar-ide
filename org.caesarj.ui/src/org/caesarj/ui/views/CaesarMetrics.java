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

import java.util.*;
import org.apache.log4j.Logger;
import org.caesarj.ui.editor.CaesarEditor;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.dom.*;



/**
 * The purpose of <code>CaesarMetrics</code> is to demonstrate how to integrate with the
 * JDT model. It defines some basic Java code metrics:
 * 
 * <ul>
 * <li>Number of literals
 * <li>Number of fields
 * <li>Number of methods
 * </ul>
 * 
 * This class will work with the <code>CaesarMetricsView</code> to enable the user
 * to see basic code metrics updated in real-time as the underlying Java model
 * is changed.
 * 
 * @see	org.eclipse.jdt.core.dom.ASTNode
 * @see	org.eclipse.jdt.core.dom.ASTVisitor
 * @see	org.eclipse.jdt.core.ICompilationUnit
 * @see	org.eclipse.jdt.core.IElementChangedListener
 * @see	ICaesarJMetricsListener
 * @see	CaesarMetricsView
 */
public class CaesarMetrics implements IElementChangedListener {
	private static Logger log = Logger.getLogger(CaesarEditor.class);
	private int methodDeclarationCount;
	private int fieldDeclarationCount;
	private int stringLiteralCount;
	private ICompilationUnit cu;
	private List listeners = new ArrayList();
	
	/**
	 * This inner class of <code>CaesarMetrics</code> visits the AST and notifies
	 * its owner <code>CaesarMetrics</code> of the elements that it finds.
	 * 
	 * @see 	CaesarMetrics#processFieldDeclaration(FieldDeclaration)
	 * @see 	CaesarMetrics#processMethodDeclaration(MethodDeclaration)
	 * @see 	CaesarMetrics#processStringLiteral(StringLiteral)
	 */
	private class JavaMetricsAccumulator extends ASTVisitor {
		private CaesarMetrics jm;
		
		/**
		 * Create a new instance, gathering statistics about the given <code>ICompilationUnit</code>
		 * and notifying the given <code>CaesarMetrics</code>.
		 * 
		 * @param newJm	Java metrics owner who will be notified of found metrics.
		 * @param newCu	compilation unit whose metrics will be calculated.
		 */
		public JavaMetricsAccumulator(CaesarMetrics newJm, ICompilationUnit newCu) {
			this.jm = newJm;

			AST.parseCompilationUnit(newCu, false).accept(this);
		}

		public boolean visit(StringLiteral node) {
			return jm.processStringLiteral(node);
		}

		public boolean visit(FieldDeclaration node) {
			log.debug("Node: '"+node.toString()+"'found");
			return jm.processFieldDeclaration(node);
		}

		public boolean visit(MethodDeclaration node) {
			return jm.processMethodDeclaration(node);
		}
	}

	/**
	 * Returns an instance of <code>CaesarMetrics</code>, initialized to zero.
	 */
	public CaesarMetrics() {
		this.reset(null);
	}
	
	/**
	 * Reset and recalculate code metrics for the given <code>ICompilationUnit</code>.
	 * Listeners will be notified of this change.
	 * 
	 * @param	newcu	new compilation unit whose metrics this class represents.
	 * 
	 * @see	ICaesarJMetricsListener
	 */
	public void reset(ICompilationUnit newcu) {
		cu = newcu;

		methodDeclarationCount = 0;
		fieldDeclarationCount = 0;
		stringLiteralCount = 0;

		if (cu != null) {
			new JavaMetricsAccumulator(this, cu);
			
			if (cu.isWorkingCopy())
				cu = (ICompilationUnit) cu.getOriginalElement();
		}
				
		notifyListeners();
	}
	
	/**
	 * Add a change listener.
	 * 
	 * @param	listener	new listener.
	 * 
	 * @see	ICaesarJMetricsListener
	 */
	public void addListener(ICaesarJMetricsListener listener) {
		listeners.add(listener);
		JavaCore.addElementChangedListener(this);
	}
	
	/**
	 * Remove a change listener.
	 * 
	 * @param	listener	old listener.
	 * 
	 * @see	ICaesarJMetricsListener
	 */
	public void removeListener(ICaesarJMetricsListener listener) {
		listeners.remove(listener);
		JavaCore.removeElementChangedListener(this);
	}

	/**
	 * Notifies that one or more attributes of one or more Java elements have changed.
	 * The specific details of the change are described by the given event.
	 * <code>ICaesarJMetricsListener</code> will be notified if this affected the
	 * calculated metrics.
	 *
	 * @param 	event the change event
	 * 
	 * @see	ICaesarJMetricsListener
	 * @see	JavaCore#addElementChangedListener(IElementChangedListener)
	 */
	public void elementChanged(ElementChangedEvent event) {
		if (cu == null)
			return;
			
		ICompilationUnit originalCu;
		ICompilationUnit changedCu = (ICompilationUnit)
			event.getDelta().getElement().getAncestor(IJavaElement.COMPILATION_UNIT);
			
		if (changedCu == null)
			return;
			
		// Test if the changed ICompilationUnit is the same as this view's.
		// Note that notifications may come from working copies, so comparisons
		// of equality must go against the original (i.e., the working copy of a
		// CU isn't equal to the CU).			
		if (changedCu instanceof IWorkingCopy && ((IWorkingCopy) changedCu).isWorkingCopy())
			originalCu = (ICompilationUnit) ((IWorkingCopy) changedCu).getOriginalElement();
		else
			originalCu = changedCu;
			
		if (cu.equals(originalCu)) {
			
			// If the changed element is rooted at our CU, notify listeners
			// that the model has either changed in some way or been invalidated 
			// (IJavaElementDelta.REMOVED).
			if (event.getDelta().getKind() != IJavaElementDelta.REMOVED)
				reset(changedCu);
			else
				reset(null);
		}
	}

	/**
	 * @see ICaesarJMetricsListener
	 */
	private void notifyListeners() {
		for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
			ICaesarJMetricsListener listener = (ICaesarJMetricsListener) iterator.next();
			listener.refresh(this);
		}
	}
	
	/**
	 * See JavaMetricsAccumulator inner class.
	 */
	protected boolean processStringLiteral(StringLiteral node) {
		stringLiteralCount++;
		return false;
	}
	
	/**
	 * See JavaMetricsAccumulator inner class.
	 */
	protected boolean processFieldDeclaration(FieldDeclaration node) {
		fieldDeclarationCount++;
		return false;
	}

	/**
	 * See JavaMetricsAccumulator inner class.
	 */
	protected boolean processMethodDeclaration(MethodDeclaration node) {
		methodDeclarationCount++;
		return true;
	}

	/**
	 * Return basic metrics.
	 * 
	 * @return	The number of methods found in the compilation unit.
	 */
	public int getMethodDeclarationCount() {
		return methodDeclarationCount;
	}

	/**
	 * Return basic metrics.
	 * 
	 * @return	The number of fields found in the compilation unit.
	 */
	public int getFieldDeclarationCount() {
		return fieldDeclarationCount;
	}
	
	/**
	 * Return basic metrics.
	 * 
	 * @return	The number of string literals ("string") found in the compilation unit.
	 */
	public int getStringLiteralCount() {
		return stringLiteralCount;
	}
	
	/**
	 * Return a string representation suitable for display.
	 */
	public String summaryString() {
		StringBuffer sb = new StringBuffer();		
		sb.append(cu.getElementName() + "\n\n");
		sb.append("Hier die Vererbungsinfos.");
		return sb.toString();
	}

	/**
	 * This method returns <code>false</code> in the case where this metrics instance
	 * does not yet have a compilation unit (i.e., everything is zero).
	 * 
	 * @see	CaesarMetrics#summaryString()
	 */
	public boolean hasValidMetrics() {
		return cu != null;
	}
}