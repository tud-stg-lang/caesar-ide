package org.caesarj.ui.builder;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.caesarj.ui.CaesarPlugin;
import org.caesarj.ui.editor.CaesarOutlineView;
import org.caesarj.ui.util.ProjectProperties;
import org.caesarj.util.PositionedError;
import org.caesarj.util.TokenReference;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;

/**
 * Builder is responsible for building a caesar project.
 * Steps done are:
 * 1) collect project properties
 * 2) calling caesar compiler
 * 3) displaying errors
 * 4) refreshing the outlineview
 * 
 * TODO [build] Incremental build? Is there support in caesarj compiler?
 * 
 * @author Ivica Aracic
 */
public class Builder extends IncrementalProjectBuilder {

	private static Logger log = Logger.getLogger(Builder.class);

	/**
	 * The last project we did a build for, needed by content outline
	 * view to decide which updates to accept.
	 */
	private static IProject lastBuiltProject = null;

	/** The progress monitor used for this build */
	private IProgressMonitor monitor;

	private ProjectProperties projectProperties;
	private Collection errors = new LinkedList();

	/**
	 * Constructor
	 */
	public Builder() {
	}

	/**
	 * What did we last build?
	 */
	public static IProject getLastBuildTarget() {
		return lastBuiltProject;
	}

	/**
	 * @see IncrementalProjectBuilder#build(int, Map, IProgressMonitor)
	 * kind is one of: FULL_BUILD, INCREMENTAL_BUILD or AUTO_BUILD
	 * currently we do a full build in every case!
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor progressMonitor)
		throws CoreException {
		try {
			lastBuiltProject = getProject();
			errors.clear();

			log.debug("kind: " + kind);

			projectProperties = new ProjectProperties(getProject());

			log.debug("----\n" + projectProperties.toString() + "----\n");

			CaesarAdapter caesarAdapter = new CaesarAdapter(projectProperties.getProjectLocation());

			// build            
			boolean success =
				caesarAdapter.compile(
					projectProperties.getSourceFiles(),
					projectProperties.getClassPath(),
					projectProperties.getOutputPath(),
					errors,
					progressMonitor);

			// update markers, show errors
			showErrors();

			// update outline view         
			Display display = Display.getDefault();

			// update has to be executed from Workbenchs Thread

			CaesarPlugin.getDefault().getDisplay().asyncExec(new Runnable() {
				public void run() {
					CaesarOutlineView.updateAll();
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
			Collection sourceFiles = projectProperties.getSourceFiles();
			for (Iterator it = sourceFiles.iterator(); it.hasNext();) {

				String sourcePath = projectProperties.getProjectLocation() + it.next().toString();

				IResource resource = ProjectProperties.findResource(sourcePath, lastBuiltProject);

				resource.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
				//resource.deleteMarkers(IMarker.TASK, true, IResource.DEPTH_INFINITE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Iterator it = errors.iterator(); it.hasNext();) {
			try {
				PositionedError error = (PositionedError) it.next();
				TokenReference token = error.getTokenReference();

				if (token.getLine() > 0) {
					log.debug(
						"file: "
							+ token.getFile()
							+ ", "
							+ "line: "
							+ token.getLine()
							+ ", "
							+ "path: "
							+ token.getPath());

					IResource resource =
						ProjectProperties.findResource(
							token.getPath().getAbsolutePath(),
							lastBuiltProject);

					IMarker marker = resource.createMarker(IMarker.PROBLEM);
					marker.setAttribute(IMarker.LINE_NUMBER, token.getLine());
					marker.setAttribute(IMarker.MESSAGE, error.getFormattedMessage().getMessage());
					marker.setAttribute(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
