package org.caesarj.ui.model;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.StructureNode;
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
		checkChildren();
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
		checkChildren();
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
		if (this.getProgramElementKind().equals(ProgramElementNode.Kind.FILE_JAVA)) {
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
		checkChildren();
	}

	private void checkChildren() {
		Object child[] = this.children.toArray();
		List childrenList = new ArrayList();
		StructureNode node = null;
		for (int i = 0; i < child.length; i++) {
			if (((ProgramElementNode) child[i])
				.getProgramElementKind()
				.equals(ProgramElementNode.Kind.CODE)) {
				node =
					new CodeNode(
						((ProgramElementNode) child[i]).getSignature(),
						ProgramElementNode.Kind.CODE,
						((ProgramElementNode) child[i]).getSourceLocation(),
						0,
						((ProgramElementNode) child[i]).getFormalComment(),
						((ProgramElementNode) child[i]).getChildren());
				node.setParent(this);
			}
			childrenList.add(node);
		}
		this.children = childrenList;
	}

	public String toString() {
		return "[" + getKind() + "] " + getName();
	}

	public int getCAModifiers() {
		return this.modif;
	}

	public ImportCaesarProgramElementNode getImports() {
		return this.imports;
	}

	public void addChild(StructureNode sNode) {
		if (((ProgramElementNode) sNode)
			.getProgramElementKind()
			.equals(ProgramElementNode.Kind.CODE)) {
			ProgramElementNode pNode = (ProgramElementNode) sNode;
			CodeNode cNode =
				new CodeNode(
					pNode.getSignature(),
					ProgramElementNode.Kind.CODE,
					pNode.getSourceLocation(),
					0,
					pNode.getFormalComment(),
					pNode.getChildren());
			cNode.setParent(this);
			this.removeChild(sNode);
			super.addChild(cNode);
		} else
			super.addChild(sNode);
	}
}
