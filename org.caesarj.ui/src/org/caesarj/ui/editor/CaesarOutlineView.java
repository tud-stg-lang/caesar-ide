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
import org.caesarj.ui.CaesarElementImageDescriptor;
import org.caesarj.ui.CaesarPlugin;
import org.caesarj.ui.CaesarPluginImages;
import org.caesarj.ui.model.AdviceDeclarationNode;
import org.caesarj.ui.model.CaesarProgramElementNode;
import org.caesarj.ui.model.PackageNode;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
/**
 * Content Outline Page for Caesar Compilation Unit
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */

public class CaesarOutlineView extends ContentOutlinePage {

	private static HashMap categoryMap;
	private String signature;
	private Object imports;
	protected static final Point BIG_SIZE = new Point(22, 16);

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
			try {
				if (element instanceof CaesarProgramElementNode) {
					CaesarProgramElementNode cNode = (CaesarProgramElementNode) element;
					return cNode.getText(super.getText(element));
				} else if (element instanceof LinkNode) {
					LinkNode lNode = (LinkNode) element;
					String returnString = lNode.toString();
					returnString = returnString.substring(returnString.lastIndexOf(']') + 2);
					try {
						String className = returnString.substring(0, returnString.lastIndexOf(':'));
						String advice =
							returnString.substring(
								returnString.lastIndexOf(':') + 2,
								returnString.length());
						return advice + ":" + className;
					} catch (RuntimeException e1) {
						return returnString.substring(0, returnString.length())+"()";
					}
				} else
					return super.getText(element);
			} catch (NullPointerException e) {
				logger.error("Sollte es nicht geben!", e);
			}
			return "ERROR";
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
				ImageDescriptor img;
				if (element instanceof LinkNode) {
					LinkNode lNode = (LinkNode) element;
					return new CaesarElementImageDescriptor(
						CaesarPluginImages.DESC_JOINPOINT,
						null,
						BIG_SIZE)
						.createImage();
				} else if (element instanceof RelationNode) {
					return new CaesarElementImageDescriptor(
						CaesarPluginImages.DESC_ADVICE,
						null,
						BIG_SIZE)
						.createImage();
				} else if (element instanceof CaesarProgramElementNode) {
					CaesarProgramElementNode cNode = (CaesarProgramElementNode) element;
					return cNode.getImage();
				} else
					return super.getImage(element);
			} catch (Exception e) {
				logger.error("Error while loading icon images for caesar outline view.", e);
				return super.getImage(element);
			}
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
				Iterator it = node.getRelations().iterator();
				while(it.hasNext()){
					Object te = it.next();	
					if (!(te instanceof AdviceDeclarationNode))
						vec.add(te);
				}
			}
			StructureNode node = (StructureNode) parentElement;
			for (Iterator it = node.getChildren().iterator(); it.hasNext();)
				vec.add(it.next());
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
	}

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
						IDE.openEditor(CaesarPlugin
								.getDefault()
								.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage(), marker);						
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
