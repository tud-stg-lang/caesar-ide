package org.caesarj.ui.model;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import org.aspectj.asm.LinkNode;
import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.StructureNode;
import org.aspectj.bridge.ISourceLocation;

/**
 * Dumps StructureModel using PrintStream
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class StructureModelDump {

	String indent = ""; //$NON-NLS-1$
	protected PrintStream out;

	public StructureModelDump(PrintStream outArg) {
		this.out = outArg;
	}

	public void print(String indentArg, StructureNode node) {
		this.out.print(indentArg);

		printNodeHeader(this.out, node);

		if (node instanceof ProgramElementNode) {
			ProgramElementNode peNode = (ProgramElementNode) node;
			this.out.print(
				" '" + peNode.getBytecodeName() + "' '" + peNode.getBytecodeSignature() + peNode.getAccessibility()+"'");   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

			this.out.println();

			List relations = peNode.getRelations();
			if (relations.size() > 0) {
				for (Iterator it = relations.iterator(); it.hasNext();) {
					print(indentArg + "++", (StructureNode) it.next()); //$NON-NLS-1$
				}
			}
//		} else if (node instanceof RelationNode) {
//			RelationNode relNode = (RelationNode) node;
//			//out.print(" "+relNode.getRelation().toString());
//			this.out.println();
		} else if (node instanceof LinkNode) {
			LinkNode linkNode = (LinkNode) node;
			this.out.print(" ->> "); //$NON-NLS-1$
			printNodeHeader(this.out, linkNode.getProgramElementNode());
			this.out.println();
		} else {
			this.out.println();
		}

		for (Iterator it = node.getChildren().iterator(); it.hasNext();) {
			print(indentArg + "..", (StructureNode) it.next()); //$NON-NLS-1$
		}
	}

	protected void printNodeHeader(PrintStream outArg, StructureNode node) {
		//out.print(node.getClass().getName());

		outArg.print("[" + node.getKind() + "] " + node.getName()); //$NON-NLS-1$ //$NON-NLS-2$

		ISourceLocation srcLoc = node.getSourceLocation();
		if (srcLoc != null) {
			outArg.print("(L " + srcLoc.getLine() + ") ");  //$NON-NLS-1$//$NON-NLS-2$
		}
	}

}
