package org.caesarj.ui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.aspectj.asm.LinkNode;
import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.RelationNode;
import org.aspectj.asm.StructureNode;
import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.phylum.JClassImport;
import org.caesarj.compiler.ast.phylum.JPackageImport;
import org.caesarj.ui.CaesarElementImageDescriptor;
import org.caesarj.ui.builder.Builder;
import org.caesarj.ui.marker.AdviceMarker;
import org.caesarj.ui.util.ProjectProperties;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.MarkerUtilities;

/**
 * Adds additional methods needed in NodeEliminator Visitor.
 * 
 * @see isToRemove
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public abstract class CaesarProgramElementNode extends ProgramElementNode {

	public ImageDescriptor PUBLIC, PRIVATE, PROTECTED, DEFAULT;

	protected final static int OVERLAY_ICONS = 0x1;

	protected final static int SMALL_ICONS = 0x2;

	protected final static int LIGHT_TYPE_ICONS = 0x4;

	protected static final Point SMALL_SIZE = new Point(16, 16);

	protected static final Point BIG_SIZE = new Point(22, 16);

	private int modif;

	private ImportCaesarProgramElementNode imports = null;

	private static Logger logger = Logger
			.getLogger(CaesarProgramElementNode.class);

	public CaesarProgramElementNode(String signature, Kind kind,
			List childrenArg) {
		super(signature, kind, childrenArg);
		checkChildren();
	}

	public CaesarProgramElementNode(String signature, Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment,
				childrenArg);
		this.modif = modifiers;
		checkChildren();
	}

	public CaesarProgramElementNode(String signature, Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg,
			JPackageImport[] importedPackages, JClassImport[] importedClasses) {
		super(signature, kind, sourceLocationArg, modifiers, formalComment,
				childrenArg);
		this.modif = modifiers;
		if (this.getProgramElementKind().equals(
				ProgramElementNode.Kind.FILE_JAVA)) {
			this.imports = new ImportCaesarProgramElementNode(kind,
					sourceLocationArg, 0, formalComment, null,
					importedPackages, importedClasses);
		}
		checkChildren();
	}

	public CaesarProgramElementNode(String signature, Kind kind,
			ISourceLocation sourceLocationArg, int modifiers,
			String formalComment, List childrenArg, ProgramElementNode node) {
		this(signature, kind, sourceLocationArg, modifiers, formalComment,
				childrenArg);
		this.setRelations(node.getRelations());
		this.setBytecodeName(node.getBytecodeName());
		this.setBytecodeSignature(node.getBytecodeSignature());
		this.setMessage(node.getMessage());
		this.setImplementor(node.isImplementor());
		this.setRelations(node.getRelations());
		this.setRunnable(node.isRunnable());
		this.setOverrider(node.isOverrider());
		this.setSourceLocation(node.getSourceLocation());
		this.name = node.getName();
	}

	private void checkChildren() {
		Object child[] = this.children.toArray();
		List childrenList = new ArrayList();
		StructureNode node = null;
		for (int i = 0; i < child.length; i++) {
			if (((ProgramElementNode) child[i]).getProgramElementKind().equals(
					ProgramElementNode.Kind.CODE)) {
				ProgramElementNode pNode = (ProgramElementNode) child[i];
				CodeNode cNode = new CodeNode(pNode.getSignature(),
						ProgramElementNode.Kind.CODE,
						pNode.getSourceLocation(), 0, pNode.getFormalComment(),
						pNode.getChildren(), pNode);
				cNode.setParent(this);
				childrenList.add(cNode);
			} else {
				childrenList.add(child[i]);
			}
		}
		this.children = childrenList;
	}

	public String toString() {
		return "[" + getKind() + "] " + getName(); //$NON-NLS-1$//$NON-NLS-2$
	}

	public int getCAModifiers() {
		return this.modif;
	}

	public ImportCaesarProgramElementNode getImports() {
		return this.imports;
	}

	public void addChild(StructureNode sNode) {
		if (((ProgramElementNode) sNode).getProgramElementKind().equals(
				ProgramElementNode.Kind.CODE)) {
			ProgramElementNode pNode = (ProgramElementNode) sNode;
			CodeNode cNode = new CodeNode(pNode.getSignature(),
					ProgramElementNode.Kind.CODE, pNode.getSourceLocation(), 0,
					pNode.getFormalComment(), pNode.getChildren(), pNode);
			super.addChild(cNode);
		} else {
			super.addChild(sNode);
		}
	}

	public Image getImage() {
		ImageDescriptor img;
		switch (this.getCAModifiers() % 8) {
		case 1:
			img = this.PUBLIC;
			break;
		case 2:
			img = this.PRIVATE;
			break;
		case 4:
			img = this.PROTECTED;
			break;
		default:
			img = this.DEFAULT;
		}
		return new CaesarElementImageDescriptor(img, this, BIG_SIZE)
				.createImage();
	}

	public List getRelations() {
		List relations = super.getRelations();
		for (Iterator it = relations.iterator(); it.hasNext();) {
			Object node = it.next();
			if (node instanceof RelationNode) {
				Object[] nodes = ((RelationNode) node).getChildren().toArray();
				HashMap args = new HashMap();
				String messageLocal = ((RelationNode) node).getName()
						.toUpperCase()
						+ ": "; //$NON-NLS-1$
				LinkNode lNode[] = new LinkNode[nodes.length];
				String tempString, className, adviceName;
				for (int i = 0; i < nodes.length; i++) {
					lNode[i] = (LinkNode) nodes[i];
					try {
						tempString = lNode[i].toLongString();
					} catch (Exception e) {
						continue;
					}
					tempString = tempString.substring(tempString
							.lastIndexOf(']') + 1);
					try {
						className = tempString.substring(0, tempString
								.lastIndexOf(':'));
						adviceName = tempString.substring(tempString
								.lastIndexOf(':') + 2, tempString.length() - 1);
						messageLocal += "!" + adviceName + ":" + className + "!  "; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						args.put(AdviceMarker.ID, "AdviceLink"); //$NON-NLS-1$
					} catch (Exception e) {
						messageLocal += "!" + tempString.substring(1, tempString.length() - 1) + "()!  "; //$NON-NLS-1$//$NON-NLS-2$
						args.put(AdviceMarker.ID, "MethodeLink"); //$NON-NLS-1$
					}
				}
				ISourceLocation src = this.getSourceLocation();
				IResource resource = ProjectProperties.findResource(src
						.getSourceFile().getAbsolutePath(), Builder
						.getLastBuildTarget());
				args.put(IMarker.LINE_NUMBER, new Integer(this
						.getSourceLocation().getLine()));
				args.put(IMarker.MESSAGE, messageLocal);
				args.put(AdviceMarker.LINKS, lNode);
				try {
					MarkerUtilities.createMarker(resource, args,
							AdviceMarker.ADVICEMARKER);
				} catch (CoreException e) {
					logger.error("FEHLER BEIM MARKER ERZEUGEN", e); //$NON-NLS-1$
				}
			}
		}
		return relations;
	}

	protected abstract void initImages();

	public abstract String getText(String text);
}