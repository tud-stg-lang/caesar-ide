package org.caesarj.ui.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.StructureNode;
import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.JClassImport;
import org.caesarj.compiler.ast.JPackageImport;
import org.caesarj.ui.CaesarElementImageDescriptor;
import org.caesarj.ui.CaesarPluginImages;
import org.caesarj.ui.builder.Builder;
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

	static Logger logger = Logger.getLogger(CaesarProgramElementNode.class);

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
			sNode =
				new CodeNode(
					pNode.getSignature(),
					ProgramElementNode.Kind.CODE,
					pNode.getSourceLocation(),
					0,
					pNode.getFormalComment(),
					pNode.getChildren());
			this.removeChild(sNode);
		}
		super.addChild(sNode);
	}

	public Image getImage() {
		if (this instanceof CodeNode
			|| this.getProgramElementKind().equals(ProgramElementNode.Kind.CODE)) {
			IResource resource =
				ProjectProperties.findResource(
					this.getParent().getSourceLocation().getSourceFile().getAbsolutePath(),
					Builder.getLastBuildTarget());

			HashMap args = new HashMap();
			args.put(IMarker.LINE_NUMBER, new Integer(this.getParent().getSourceLocation().getLine()));
			args.put(IMarker.MESSAGE, "Dies ist ein ADVICE Test");
			args.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_INFO));
			try {//TODO Eigenen Marker
				MarkerUtilities.createMarker(resource, args, IMarker.TASK);//AdviceMarker.ADVICE);
			} catch (CoreException e) {
				logger.error("FEHLER BEIM MARKER ERZEUGEN", e);
			}
			return new CaesarElementImageDescriptor(
				CaesarPluginImages.DESC_CODE,
				null,
				BIG_SIZE,
				false)
				.createImage();
		}

		ImageDescriptor img;
		switch (this.getCAModifiers() % 8) {
			case 1 :
				img = PUBLIC;
				break;
			case 2 :
				img = PRIVATE;
				break;
			case 4 :
				img = PROTECTED;
				break;
			default :
				img = DEFAULT;
		}
		return new CaesarElementImageDescriptor(img, this, BIG_SIZE, false).createImage();
	}

	protected abstract void initImages();
	public abstract String getText(String text);
}
