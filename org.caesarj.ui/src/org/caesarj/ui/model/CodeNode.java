package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.JClassImport;
import org.caesarj.compiler.ast.JPackageImport;
import org.caesarj.ui.builder.Builder;
import org.caesarj.ui.util.ProjectProperties;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import org.apache.log4j.Logger;
/**
 * @author Shadow
 *
 * Folgendes auswählen, um die Schablone für den erstellten Typenkommentar zu ändern:
 * Fenster&gt;Benutzervorgaben&gt;Java&gt;Codegenerierung&gt;Code und Kommentare
 */
public class CodeNode extends CaesarProgramElementNode {

	static Logger logger = Logger.getLogger(CodeNode.class);
	/**
	 * @param signature
	 * @param kind
	 * @param children
	 */
	public CodeNode(String signature, Kind kind, List children) {
		super(signature, kind, children);
	}

	/**
	 * @param signature
	 * @param kind
	 * @param sourceLocation
	 * @param modifiers
	 * @param formalComment
	 * @param children
	 */
	public CodeNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment, children);
		IResource resource =
			ProjectProperties.findResource(
				sourceLocation.getSourceFile().getAbsolutePath(),
				Builder.getLastBuildTarget());

		try {
			IMarker marker = resource.createMarker(IMarker.TASK);
			marker.setAttribute(IMarker.LINE_NUMBER, sourceLocation.getLine());
			marker.setAttribute(IMarker.MESSAGE, "DIES IST ein ADVICE TEST");
			marker.setAttribute(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_INFO));
		} catch (CoreException e) {
			logger.error("FEHLER BEIM MARKER ERZEUGEN", e);
		}
	}

	/**
	 * @param signature
	 * @param kind
	 * @param sourceLocation
	 * @param modifiers
	 * @param formalComment
	 * @param children
	 * @param importedPackages
	 * @param importedClasses
	 */
	public CodeNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children,
		JPackageImport[] importedPackages,
		JClassImport[] importedClasses) {
		super(
			signature,
			kind,
			sourceLocation,
			modifiers,
			formalComment,
			children,
			importedPackages,
			importedClasses);

		IResource resource =
			ProjectProperties.findResource(
				sourceLocation.getSourceFile().getAbsolutePath(),
				Builder.getLastBuildTarget());

		try {
			IMarker marker = resource.createMarker(IMarker.TASK);
			marker.setAttribute(IMarker.LINE_NUMBER, sourceLocation.getLine());
			marker.setAttribute(IMarker.MESSAGE, "DIES IST ein ADVICE TEST");
			marker.setAttribute(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_INFO));
		} catch (CoreException e) {
			logger.error("FEHLER BEIM MARKER ERZEUGEN", e);
		}
	}

	/* (Kein Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) throws ClassCastException {
		return super.compareTo(o);
	}

}
