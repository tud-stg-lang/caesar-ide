package org.caesarj.ui.model;

import java.util.List;

import org.aspectj.asm.ProgramElementNode;
import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.JClassImport;
import org.caesarj.compiler.ast.JPackageImport;

/**
 * Adds additional methods needed in NodeEliminator Visitor.
 * @see isToRemove
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class CaesarProgramElementNode extends ProgramElementNode {
	private int modif;
	private ImportCaesarProgramElementNode imports = null;

	public CaesarProgramElementNode(String signature, Kind kind, List children) {
		super(signature, kind, children);
	}

	public CaesarProgramElementNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children) {
		super(signature, kind, sourceLocation, modifiers, formalComment, children);
		this.modif = modifiers;
	}

	public CaesarProgramElementNode(
		String signature,
		Kind kind,
		ISourceLocation sourceLocation,
		int modifiers,
		String formalComment,
		List children,
		JPackageImport[] importedPackages,
		JClassImport[] importedClasses) {
		super(signature, kind, sourceLocation, modifiers, formalComment, children);
		this.modif = modifiers;
		if (this.getKind().compareTo("java source file") == 0) {
			this.imports =
				new ImportCaesarProgramElementNode(
					signature,
					kind,
					sourceLocation,
					0,
					formalComment,
					null,
					importedPackages,
					importedClasses);
		}
	}

	public String toString() {
		return "[" + getKind() + "] " + getName();
	}

	public int getCAModifiers() {
		return this.modif;
	}
	
	public ImportCaesarProgramElementNode getImports(){
		return this.imports;
	}
}
