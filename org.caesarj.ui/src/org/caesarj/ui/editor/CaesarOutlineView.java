package org.caesarj.ui.editor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.aspectj.asm.LinkNode;
import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.RelationNode;
import org.aspectj.asm.StructureModelManager;
import org.aspectj.asm.StructureNode;
import org.aspectj.bridge.ISourceLocation;
import org.caesarj.compiler.ast.JFormalParameter;
import org.caesarj.compiler.types.CType;
import org.caesarj.ui.CaesarPlugin;
import org.caesarj.ui.CaesarPluginImages;
import org.caesarj.ui.model.AdviceDeclarationNode;
import org.caesarj.ui.model.AspectNode;
import org.caesarj.ui.model.CaesarProgramElementNode;
import org.caesarj.ui.model.ClassNode;
import org.caesarj.ui.model.CodeNode;
import org.caesarj.ui.model.ConstructorDeclarationNode;
import org.caesarj.ui.model.FieldNode;
import org.caesarj.ui.model.ImportCaesarProgramElementNode;
import org.caesarj.ui.model.InterfaceNode;
import org.caesarj.ui.model.MethodDeclarationNode;
import org.caesarj.ui.model.PackageNode;
import org.caesarj.ui.model.PointcutNode;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
/**
 * Content Outline Page for Caesar Compilation Unit
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */

public class CaesarOutlineView extends ContentOutlinePage {

	private static HashMap categoryMap;
	private String signature;
	private static final Point SMALL_SIZE = new Point(16, 16);
	private static final Point BIG_SIZE = new Point(22, 16);
	private Object imports;
	private final static int OVERLAY_ICONS = 0x1;
	private final static int SMALL_ICONS = 0x2;
	private final static int LIGHT_TYPE_ICONS = 0x4;

	static {
		categoryMap = new HashMap();
		categoryMap.put(ProgramElementNode.Kind.PACKAGE, new Integer(0));
		categoryMap.put(ProgramElementNode.Kind.FILE_JAVA, new Integer(1));
		categoryMap.put(ProgramElementNode.Kind.ASPECT, new Integer(5));
		categoryMap.put(ProgramElementNode.Kind.INTERFACE, new Integer(6));
		categoryMap.put(ProgramElementNode.Kind.CLASS, new Integer(7));
		categoryMap.put(ProgramElementNode.Kind.FIELD, new Integer(10));
		categoryMap.put(ProgramElementNode.Kind.CONSTRUCTOR, new Integer(11));
		categoryMap.put(ProgramElementNode.Kind.METHOD, new Integer(12));
		categoryMap.put(ProgramElementNode.Kind.ADVICE, new Integer(13));
		categoryMap.put(ProgramElementNode.Kind.CODE, new Integer(14));
	}

	class LexicalSorter extends ViewerSorter {
		/**
		 * Return a category code for the element. This is used
		 * to sort alphabetically within categories. Categories are:
		 * pointcuts, advice, introductions, declarations, other. i.e.
		 * all pointcuts will be sorted together rather than interleaved
		 * with advice.
		 */
		public int category(Object element) {
			try {
				return (
					(Integer) categoryMap.get(
						((ProgramElementNode) element).getProgramElementKind()))
					.intValue();
			} catch (Exception e) {
				return 999;
			}
		}
	};

	private static Logger logger = Logger.getLogger(CaesarOutlineView.class);

	class CaesarLabelProvider extends LabelProvider {

		public String getText(Object element) {
			String label = "ERROR";
			try {
				//TODO ist wegen parent=null in LinkNode??? Sollte irgendwo beim AST-Tree aufbau behoben werden.
				if (!(element instanceof LinkNode))
					label = super.getText(element);
				if (element instanceof StructureNode) {
					StructureNode node = (StructureNode) element;
					if (element instanceof MethodDeclarationNode) {
						MethodDeclarationNode mNode = (MethodDeclarationNode) element;
						label = label.substring(label.lastIndexOf("]") + 2);
						label += "(";
						JFormalParameter[] para = mNode.getMethodDeclaration().getArgs();
						int paraSize = para.length;
						for (int i = 0; i < paraSize; i++) {
							String arg = para[i].getType().toString();
							label += arg.substring(arg.lastIndexOf('.') + 1);
							if (i < paraSize - 1)
								label += ", ";
						}
						label += ") : ";
						CType type = mNode.getReturnTyp();
						if (type == null) {
							label += "no statement";
						} else if (type.toString().compareTo("") == 0)
							label += "void";
						else
							label
								+= type.toString().substring(type.toString().lastIndexOf('.') + 1);
					} else if (node instanceof ConstructorDeclarationNode) {
						ConstructorDeclarationNode cNode = (ConstructorDeclarationNode) element;
						label = label.substring(label.lastIndexOf("]") + 2);
						label += "(";
						JFormalParameter[] para = cNode.getConstructorDeclaration().getArgs();
						int paraSize = para.length;
						for (int i = 0; i < paraSize; i++) {
							label += para[i];
							if (i < paraSize - 1)
								label += ", ";
						}
						label += ")";
					} else if (node instanceof FieldNode) {
						FieldNode fNode = (FieldNode) element;
						label = label.substring(label.lastIndexOf("]") + 2);
						label += " : " + fNode.getShortType();
					} else if (element instanceof ImportCaesarProgramElementNode) {
						ImportCaesarProgramElementNode iNode =
							(ImportCaesarProgramElementNode) element;
						label = label.substring(label.lastIndexOf("]") + 2);
						label = label.replace('/', '.');
					} else if (element instanceof ClassNode) {
						label = label.substring(label.lastIndexOf("]") + 2);
					} else if (element instanceof AspectNode) {
						label = label.substring(label.lastIndexOf("]") + 2);
					} else if (element instanceof LinkNode) {
						LinkNode lNode = (LinkNode) node;
						return this.getText(lNode.getProgramElementNode());
					} else if (element instanceof AdviceDeclarationNode) {
						label = label.substring(label.lastIndexOf("]") + 2);
					} else if (element instanceof PackageNode) {
						label = label.substring(label.lastIndexOf("]") + 2);
					} else if (element instanceof CodeNode) {
						label = label.substring(label.lastIndexOf("]") + 2);
					}
				}
			} catch (NullPointerException e) {
				logger.error("Sollte es nicht geben!", e);
			}
			return label;
		}

		private String getArgument() {
			if (signature.charAt(0) == '[') {
				signature = signature.substring(1);
				return extractTyp() + "[]";
			} else {
				return extractTyp();
			}
		}

		private String extractTyp() {
			switch (signature.charAt(0)) {
				case 'I' :
					signature = signature.substring(1);
					return "int";
				case 'F' :
					signature = signature.substring(1);
					return "float";
				case 'D' :
					signature = signature.substring(1);
					return "double";
				case 'J' :
					signature = signature.substring(1);
					return "long";
				case 'C' :
					signature = signature.substring(1);
					return "char";
				case 'V' :
					signature = signature.substring(1);
					return "void";
				case 'L' :
					String sig = signature.substring(1, signature.indexOf(';'));
					sig = sig.substring(sig.lastIndexOf('/') + 1);
					signature = signature.substring(signature.indexOf(';') + 1);
					return sig;
				default :
					return "";
			}
		}

		public Image getImage(Object element) {
			try {
				Image image = null;
				//TODO ist wegen parent=null in LinkNode??? Sollte irgendwo beim AST-Tree aufbau behoben werden.
				if (element instanceof LinkNode)
					logger.debug("Element discription: LinkNode");
				else
					logger.debug("Element discription: " + element.toString());
				if (element instanceof StructureNode) {
					StructureNode node = (StructureNode) element;
					ImageDescriptor img;
					logger.debug("Structure Node kind: " + node.getKind());
					if (node instanceof LinkNode) {
						//TODO Linknode setzt den programmnode nicht richtig.
						LinkNode lNode = (LinkNode) node;
						return this.getImage(lNode.getProgramElementNode());
					} else if (node instanceof RelationNode) {
						return new JavaElementImageDescriptor(
							CaesarPluginImages.DESC_ADVICE,
							0,
							BIG_SIZE)
							.createImage();
					} else {
						ProgramElementNode pNode = (ProgramElementNode) node;
						if (pNode instanceof FieldNode) {
							FieldNode fNode = (FieldNode) element;
							int adornmentFlags = computeJavaAdornmentFlags(fNode);
							switch (fNode.getCAModifiers() % 8) {
								case 1 :
									img = CaesarPluginImages.DESC_FIELD_PUBLIC;
									break;
								case 2 :
									img = CaesarPluginImages.DESC_FIELD_PRIVATE;
									break;
								case 4 :
									img = CaesarPluginImages.DESC_FIELD_PROTECTED;
									break;

								default :
									img = CaesarPluginImages.DESC_FIELD_DEFAULT;
							}
							return new JavaElementImageDescriptor(img, adornmentFlags, BIG_SIZE)
								.createImage();
						} else if (pNode instanceof MethodDeclarationNode) {
							MethodDeclarationNode mNode = (MethodDeclarationNode) pNode;
							int adornmentFlags = computeJavaAdornmentFlags(mNode);
							switch (mNode.getCAModifiers() % 8) {
								case 1 :
									img = CaesarPluginImages.DESC_MISC_PUBLIC;
									break;
								case 2 :
									img = CaesarPluginImages.DESC_MISC_PRIVATE;
									break;
								case 4 :
									img = CaesarPluginImages.DESC_MISC_PROTECTED;
									break;
								default :
									img = CaesarPluginImages.DESC_MISC_DEFAULT;
							}
							return new JavaElementImageDescriptor(img, adornmentFlags, BIG_SIZE)
								.createImage();
						} else if (pNode instanceof ConstructorDeclarationNode) {
							ConstructorDeclarationNode cNode = (ConstructorDeclarationNode) element;
							switch (cNode.getCAModifiers() % 8) {
								case 1 :
									img = CaesarPluginImages.DESC_MISC_PUBLIC;
									break;
								case 2 :
									img = CaesarPluginImages.DESC_MISC_PRIVATE;
									break;
								case 4 :
									img = CaesarPluginImages.DESC_MISC_PROTECTED;
									break;
								default :
									img = CaesarPluginImages.DESC_MISC_DEFAULT;
							}
							return new JavaElementImageDescriptor(
								img,
								JavaElementImageDescriptor.CONSTRUCTOR,
								BIG_SIZE)
								.createImage();
						} else if (pNode instanceof ClassNode) {
							ClassNode cNode = (ClassNode) element;
							int adornmentFlags = computeJavaAdornmentFlags(cNode);
							switch (cNode.getCAModifiers() % 8) {
								case 1 :
									img = CaesarPluginImages.DESC_INNERCLASS_PUBLIC;
									break;
								case 2 :
									img = CaesarPluginImages.DESC_INNERCLASS_PRIVATE;
									break;
								case 4 :
									img = CaesarPluginImages.DESC_INNERCLASS_PROTECTED;
									break;
								default :
									img = CaesarPluginImages.DESC_INNERCLASS_DEFAULT;
							}
							return new JavaElementImageDescriptor(img, adornmentFlags, BIG_SIZE)
								.createImage();
						} else if (pNode instanceof InterfaceNode) {
							InterfaceNode iNode = (InterfaceNode) element;
							int adornmentFlags = computeJavaAdornmentFlags(iNode);
							switch (iNode.getCAModifiers() % 8) {
								case 1 :
									img = CaesarPluginImages.DESC_INNERINTERFACE_PUBLIC;
									break;
								case 2 :
									img = CaesarPluginImages.DESC_INNERINTERFACE_PRIVATE;
									break;
								case 4 :
									img = CaesarPluginImages.DESC_INNERINTERFACE_PROTECTED;
									break;
								default :
									img = CaesarPluginImages.DESC_INNERINTERFACE_DEFAULT;
							}
							return new JavaElementImageDescriptor(img, adornmentFlags, BIG_SIZE)
								.createImage();
						} else if (pNode instanceof ImportCaesarProgramElementNode) {
							ImportCaesarProgramElementNode iNode =
								(ImportCaesarProgramElementNode) pNode;
							if (iNode.rootFlag)
								return new JavaElementImageDescriptor(
									CaesarPluginImages.DESC_OUT_IMPORTS,
									0,
									BIG_SIZE)
									.createImage();
							else
								return new JavaElementImageDescriptor(
									CaesarPluginImages.DESC_IMPORTS,
									0,
									BIG_SIZE)
									.createImage();
						} else if (
							pNode instanceof CodeNode
								|| pNode.getProgramElementKind().equals(
									ProgramElementNode.Kind.CODE)) {
							return new JavaElementImageDescriptor(
								CaesarPluginImages.DESC_CODE,
								0,
								BIG_SIZE)
								.createImage();
						} else if (pNode instanceof PackageNode) {
							return new JavaElementImageDescriptor(
								CaesarPluginImages.DESC_OUT_PACKAGE,
								0,
								BIG_SIZE)
								.createImage();
						} else if (pNode instanceof AdviceDeclarationNode) {
							return image =
								new JavaElementImageDescriptor(
									CaesarPluginImages.DESC_JOINPOINT,
									0,
									BIG_SIZE)
									.createImage();
						} else if (pNode instanceof AspectNode) {
							return image =
								new JavaElementImageDescriptor(
									CaesarPluginImages.DESC_ASPECT,
									0,
									BIG_SIZE)
									.createImage();
						} else if (pNode instanceof PointcutNode) {
							return image =
								new JavaElementImageDescriptor(
									CaesarPluginImages.DESC_ERROR,
									0,
									BIG_SIZE)
									.createImage();
						} else
							return new JavaElementImageDescriptor(
								CaesarPluginImages.DESC_ERROR,
								0,
								BIG_SIZE)
								.createImage();
					}
				} else
					return super.getImage(element);
			} catch (Exception e) {
				logger.error("Error while loading icon images for caesar outline view.", e);
				return super.getImage(element);
			}
		}

		private int computeJavaAdornmentFlags(CaesarProgramElementNode node) {
			int flags = 0;
			//String modifiers = node.getModifiers().toString();
			if (node.isImplementor())
				flags |= JavaElementImageDescriptor.IMPLEMENTS;
			if (node.isOverrider())
				flags |= JavaElementImageDescriptor.OVERRIDES;
			if (node.isRunnable())
				flags |= JavaElementImageDescriptor.RUNNABLE;

			int modif = node.getCAModifiers();
			if ((modif / 8) % 2 == 1)
				flags |= JavaElementImageDescriptor.STATIC;
			if ((modif / 16) % 2 == 1)
				flags |= JavaElementImageDescriptor.FINAL;
			if ((modif / 32) % 2 == 1)
				flags |= JavaElementImageDescriptor.SYNCHRONIZED;
			//if((modif/64)%2==1) flags |= JavaElementImageDescriptor.VOLATILE;
			//if((modif/128)%2==1) flags |= JavaElementImageDescriptor.TRANSIENT;
			//if((modif/256)%2==1) flags |= JavaElementImageDescriptor.WARNING;
			//if((modif/512)%2==1) flags |= JavaElementImageDescriptor.ERROR;
			if ((modif / 1024) % 2 == 1)
				flags |= JavaElementImageDescriptor.ABSTRACT;
			return flags;
		}
	}

	/**
	 * uses Structure Model to extract the data for TreeViewer
	 */
	protected class ContentProvider implements ITreeContentProvider {

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			Object[] ret = new Object[3];
			ret[0] = ((PackageNode) ((CaesarProgramElementNode) inputElement).getParent()).clone();
			ret[1] = ((CaesarProgramElementNode) inputElement).getImports();
			CaesarOutlineView.this.imports = ret[1];
			ret[2] = ((CaesarProgramElementNode) inputElement).getChildren().toArray()[0];
			return ret;
		}

		public Object[] getChildren(Object parentElement) {
			Vector vec = new Vector();

			if (parentElement instanceof ProgramElementNode) {
				ProgramElementNode node = (ProgramElementNode) parentElement;
				for (Iterator it = node.getRelations().iterator(); it.hasNext();)
					if (!(it instanceof AdviceDeclarationNode))
						vec.add(it.next());
			}

			{
				StructureNode node = (StructureNode) parentElement;
				for (Iterator it = node.getChildren().iterator(); it.hasNext();)
					vec.add(it.next());
			}

			return vec.toArray();
		}

		public Object getParent(Object element) {
			return ((StructureNode) element).getParent();
		}

		public boolean hasChildren(Object element) {
			if (element instanceof ProgramElementNode) {
				ProgramElementNode node = (ProgramElementNode) element;
				return node.getChildren().size() > 0 || node.getRelations().size() > 0;
			} else {
				StructureNode node = (StructureNode) element;
				return node.getChildren().size() > 0;
			}
		}
	};

	protected CaesarEditor caesarEditor;
	protected boolean enabled;

	/**
	 * Creates a content outline page using the given provider and the given editor.
	 */
	public CaesarOutlineView(CaesarEditor caesarEditor) {
		super();
		allViews.add(this);
		this.caesarEditor = caesarEditor;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer viewer = getTreeViewer();
		viewer.setSorter(new LexicalSorter());
		viewer.setContentProvider(new ContentProvider());
		viewer.setLabelProvider(new CaesarLabelProvider());
		viewer.addSelectionChangedListener(this);

		update();
	}

	public void selectionChanged(SelectionChangedEvent event) {

		super.selectionChanged(event);
		ISelection selection = event.getSelection();
		if (selection.isEmpty()) {
		} else {
			Object item = ((IStructuredSelection) selection).getFirstElement();

			if (item instanceof LinkNode) {
				item = ((LinkNode) item).getProgramElementNode();
			}

			StructureNode selectedNode = (StructureNode) item;
			ISourceLocation sourceLocation = selectedNode.getSourceLocation();

			if (sourceLocation != null) {
				int line = sourceLocation.getLine();
				try {

					IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

					IPath path = new Path(sourceLocation.getSourceFile().getAbsolutePath());
					IResource resource = root.getFileForLocation(path);
					IMarker marker;

					if (resource != null) {
						marker = resource.createMarker(IMarker.MARKER);
						marker.setAttribute(IMarker.LINE_NUMBER, sourceLocation.getLine());
						marker.setAttribute(IMarker.CHAR_START, sourceLocation.getColumn());

						IEditorPart ePart =
							CaesarPlugin
								.getDefault()
								.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage()
								.openEditor(
								marker);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * Updates the outline page.
	 */
	public void update() {

		if (enabled) {
			StructureNode input =
				getInput(StructureModelManager.INSTANCE.getStructureModel().getRoot());

			TreeViewer viewer = getTreeViewer();
			if (viewer != null && input != null) {
				Control control = viewer.getControl();
				if (control != null && !control.isDisposed()) {
					control.setRedraw(false);
					viewer.setInput(input);
					viewer.expandAll();
					viewer.collapseToLevel(this.imports, TreeViewer.ALL_LEVELS);
					control.setRedraw(true);
				}
			}
		}

	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	protected StructureNode getInput(StructureNode node) {
		if (node == null) {
			return null;
		}

		if (node.getName().equals(caesarEditor.getEditorInput().getName())) {
			return node;
		} else {
			StructureNode res = null;
			for (Iterator it = node.getChildren().iterator(); it.hasNext() && res == null;) {
				res = getInput((StructureNode) it.next());
			}

			return res;
		}
	}

	private static List allViews = new LinkedList();

	public static void updateAll() {
		for (Iterator it = allViews.iterator(); it.hasNext();) {
			((CaesarOutlineView) it.next()).update();
		}
	}
}
