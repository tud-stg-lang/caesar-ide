package org.caesarj.ui.builder;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.aspectj.bridge.ISourceLocation;
import org.caesarj.ui.CaesarPlugin;
import org.caesarj.ui.editor.CaesarOutlineView;
import org.caesarj.ui.marker.AdviceMarker;
import org.caesarj.ui.util.ProjectProperties;
import org.caesarj.ui.views.CaesarHierarchyView;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

/**
 * Builder is responsible for building a caesar project. Steps done are: 1)
 * collect project properties 2) calling caesar compiler 3) displaying errors 4)
 * refreshing the outlineview
 * 
 * TODO [build] Incremental build? Is there support in caesarj compiler?
 * 
 * @author Ivica Aracic
 */

public class Builder extends IncrementalProjectBuilder {

	private static Logger log = Logger.getLogger(Builder.class);

	/**
	 * The last project we did a build for, needed by content outline view to
	 * decide which updates to accept.
	 */
	private static IProject lastBuiltProject = null;

	private static final Vector allBuildedProjects;

	private ProjectProperties projectProperties;

	private Collection errors = new LinkedList();

	static {
		allBuildedProjects = new Vector();
	}

	/**
	 * What did we last build?
	 */
	public static IProject getLastBuildTarget() {
		return lastBuiltProject;
	}

	/**
	 * @see IncrementalProjectBuilder#build(int, Map, IProgressMonitor) kind is
	 *      one of: FULL_BUILD, INCREMENTAL_BUILD or AUTO_BUILD currently we do
	 *      a full build in every case!
	 */
	protected IProject[] build(int kind, Map args,
			IProgressMonitor progressMonitor) {
		try {
			lastBuiltProject = getProject();
			if (!allBuildedProjects.contains(lastBuiltProject))
				allBuildedProjects.add(lastBuiltProject);
			this.errors.clear();

			this.projectProperties = new ProjectProperties(getProject());

			log
					.debug("Building to '" + this.projectProperties.getOutputPath() + "'"); //$NON-NLS-1$//$NON-NLS-2$
			log.debug("kind: " + kind); //$NON-NLS-1$

			log.debug("----\n" + this.projectProperties.toString() + "----\n"); //$NON-NLS-1$ //$NON-NLS-2$

			CaesarAdapter caesarAdapter = new CaesarAdapter(
					this.projectProperties.getProjectLocation());

			// build
			caesarAdapter.compile(this.projectProperties.getSourceFiles(),
					this.projectProperties.getClassPath(),
					this.projectProperties.getOutputPath(), this.errors,
					progressMonitor);

			// update markers, show errors
			showErrors();

			// update outline view
			Display.getDefault();

			// update has to be executed from Workbenchs Thread

			CaesarPlugin.getDefault().getDisplay().asyncExec(new Runnable() {
				public void run() {
					CaesarOutlineView.updateAll();
					CaesarHierarchyView.updateAll();
				}
			});
		} catch (Throwable t) {
			t.printStackTrace();
		}

		IProject[] requiredResourceDeltasOnNextInvocation = null;
		return requiredResourceDeltasOnNextInvocation;
	}

	// TODO [optimize] make it efficient
	// TODO [feature] warnings are missing
	public void showErrors() {

		try {
			Collection sourceFiles = this.projectProperties.getSourceFiles();
			for (Iterator it = sourceFiles.iterator(); it.hasNext();) {

				String sourcePath = this.projectProperties.getProjectLocation()
						+ it.next().toString();

				IResource resource = ProjectProperties.findResource(sourcePath,
						lastBuiltProject);

				resource.deleteMarkers(IMarker.PROBLEM, true,
						IResource.DEPTH_INFINITE);
				resource.deleteMarkers(AdviceMarker.ADVICEMARKER, true,
						IResource.DEPTH_INFINITE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Iterator it = this.errors.iterator(); it.hasNext();) {
			try {
				PositionedError error = (PositionedError) it.next();
				TokenReference token = error.getTokenReference();

				if (token.getLine() > 0) {
					log.debug("file: " + token.getFile() + ", " + "line: " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							+ token.getLine() + ", " + "path: " //$NON-NLS-1$//$NON-NLS-2$
							+ token.getPath());

					IResource resource = ProjectProperties.findResource(token
							.getPath().getAbsolutePath(), lastBuiltProject);

					IMarker marker = resource.createMarker(IMarker.PROBLEM);
					marker.setAttribute(IMarker.LINE_NUMBER, token.getLine());
					marker.setAttribute(IMarker.MESSAGE, error
							.getFormattedMessage().getMessage());
					marker.setAttribute(IMarker.SEVERITY, new Integer(
							IMarker.SEVERITY_ERROR));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

public static IProject getProjectForSourceLocation(ISourceLocation location) {
		String path = location.getSourceFile().getAbsolutePath();
		Iterator iter = allBuildedProjects.iterator();
		IProject ret = null;
		String projectPath=null;
		while (iter.hasNext()) {
			ret = (IProject) iter.next();
			projectPath = ret.getFullPath().lastSegment();
			if (path.indexOf(projectPath)!= -1)
				return ret;
		}
		return null;
	}}