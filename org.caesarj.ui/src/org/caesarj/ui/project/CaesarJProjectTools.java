package org.caesarj.ui.project;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.caesarj.ui.CaesarPlugin;
import org.caesarj.ui.marker.AdviceMarker;
import org.caesarj.ui.util.ProjectProperties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.packageview.PackageExplorerPart;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.progress.UIJob;

public class CaesarJProjectTools {
	
	/**
	 * Adds the CaesarJ Nature to the project
	 * 
	 * @param project
	 * @param prompt whether to prompt the user, or go with the defaults
	 * @throws CoreException
	 */
	public static void addCaesarJNature(final IJavaProject project, final boolean prompt)
			throws CoreException {
		// wrap up the operation so that an autobuild is not triggered in the
		// middle of the conversion
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {
				internal_addCaesarJNature(project, prompt);
			}
		};
		try {
			op.run(null);
		} catch (InvocationTargetException ex) {
		} catch (InterruptedException e) {
		}
	}
	
	private static void internal_addCaesarJNature(IJavaProject javaProject, boolean prompt) throws CoreException {
		//checkOutputFoldersForAJFiles(project);
		
		IProject project = javaProject.getProject();
		
		// add the CaesarJ Nature
		IProjectDescription description = project.getDescription();
		String[] prevNatures = description.getNatureIds();
		String[] newNatures = new String[prevNatures.length + 1];
		System.arraycopy(prevNatures, 0, newNatures, 1, prevNatures.length);
		newNatures[0] = CaesarPlugin.ID_NATURE;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);

		// add runtime libraries to the build path.
		CaesarPlugin caesarPlugin = CaesarPlugin.getDefault();
		
		addClassPath(javaProject, caesarPlugin.getAspectJRuntimeClasspath());
        addClassPath(javaProject, caesarPlugin.getCaesarRuntimeClasspath());	
        /* TODO: remove
        CJCompilationUnitManager.INSTANCE.initCompilationUnits(project);
        */ 
	
		refreshPackageExplorer();
	}
	
	private static Job refreshJob;

	private static int previousExecutionTime;
	
	public static void refreshPackageExplorer() {
		int delay = 5*previousExecutionTime;
		if (delay < 250) {
			delay = 250;
		} else if (delay > 5000) {
			delay = 5000;
		}
		getRefreshPackageExplorerJob().schedule(delay);
	}

	// reuse the same Job to avoid excessive updates
	private static Job getRefreshPackageExplorerJob() {
		if (refreshJob == null) {
			refreshJob = new RefreshPackageExplorerJob();
		}
		return refreshJob;
	}

	private static class RefreshPackageExplorerJob extends UIJob {
		RefreshPackageExplorerJob() {
			super("Refresh package explorer");
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {
			long start = System.currentTimeMillis();
			PackageExplorerPart pep = PackageExplorerPart
					.getFromActivePerspective();
			if (pep != null) {
				pep.getTreeViewer().refresh();
			}
			previousExecutionTime = (int)(System.currentTimeMillis() - start);
			//System.out.println("refresh explorer: elapsed="+previousExecutionTime);
			return Status.OK_STATUS;
		}
	}
    
    private static void addClassPath(IJavaProject javaProject, String classPath) {
        try {
            IClasspathEntry[] originalCP = javaProject.getRawClasspath();            
            IClasspathEntry classpathEntry =
                JavaCore.newVariableEntry(
                    new Path(classPath), // library location
                    null,               // no source
                    null                // no source
                    );
            // Update the raw classpath with the new ajrtCP entry.
            int originalCPLength = originalCP.length;
            IClasspathEntry[] newClasspath =
                new IClasspathEntry[originalCPLength + 1];
            System.arraycopy(originalCP, 0, newClasspath, 0, originalCPLength);
            newClasspath[originalCPLength] = classpathEntry;
            javaProject.setRawClasspath(newClasspath, new NullProgressMonitor());
        }
        catch(Throwable e) {
            e.printStackTrace();
        }
    }
    
    /**
	 * Attempt to update the project's build classpath by removing any occurance
	 * of the AspectJ runtime library.
	 * 
	 * @param project
	 */
	public static void removeFromClassPath(IJavaProject javaProject, String libraryName) {
		try {
			IClasspathEntry[] originalCP = javaProject.getRawClasspath();
			ArrayList tempCP = new ArrayList();

			// Go through each current classpath entry one at a time. If it
			// is not a reference to the aspectjrt.jar then do not add it
			// to the collection of new classpath entries.
			for (int i = 0; i < originalCP.length; i++) {
				IPath path = originalCP[i].getPath();
				if (!path.toOSString().endsWith(libraryName)) {
					tempCP.add(originalCP[i]);
				}
			}// end for

			// Set the classpath with only those elements that survived the
			// above filtration process.
			if (originalCP.length != tempCP.size()) {
				IClasspathEntry[] newCP = (IClasspathEntry[]) tempCP
						.toArray(new IClasspathEntry[tempCP.size()]);
				javaProject.setRawClasspath(newCP, new NullProgressMonitor());
			}// end if at least one classpath element removed
		} catch (JavaModelException e) {
		}
	}
    
    /**
	 * Removes the CaesarJ Nature from an existing CaesarJ project.
	 * 
	 * @param project
	 * @throws CoreException
	 */
	public static void removeCaesarJNature(IJavaProject javaProject)
			throws CoreException {
		
		IProject project = javaProject.getProject();

		deleteAllMarkers(project);
		
		// remove the AspectJ Nature
		IProjectDescription description = project.getDescription();
		String[] prevNatures = description.getNatureIds();
		String[] newNatures = new String[prevNatures.length - 1];
		int newPosition = 0;
		for (int i = 0; i < prevNatures.length; i++) {
			if (!prevNatures[i].equals(CaesarPlugin.ID_NATURE)) {
				// guard against array out of bounds which will occur if we
				// get to here in a project that DOES NOT have the aj nature
				// (should never happen).
				if (newPosition < newNatures.length) {
					newNatures[newPosition++] = prevNatures[i];
				} else {
					// exception... atempt to remove ajnature from a project
					// that
					// doesn't have it. Leave the project natures unchanged.
					newNatures = prevNatures;
					break;
				}// end else
			}// end if
		}// end for
		description.setNatureIds(newNatures);
		project.setDescription(description, null);

		// Update the build classpath to try and remove the aspectjrt.jar
		removeFromClassPath(javaProject, CaesarPlugin.ASPECTJ_RUNTIME_LIB);
		removeFromClassPath(javaProject, CaesarPlugin.CAESAR_RUNTIME_LIB);
        
		//Ensures the project icon refreshes
		refreshPackageExplorer();
	}
	
	private static void deleteAllMarkers(IProject project) {
		try {
			/* delete unpositioned errors */
			project.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			
			ProjectProperties props = ProjectProperties.create(project);
			
			/* delete positioned errors */
			Collection sourceFiles = props.getSourceFiles();
			for (Iterator it = sourceFiles.iterator(); it.hasNext();) {

				String sourcePath = props.getProjectLocation()
						+ it.next().toString();

				IResource resource = ProjectProperties.findResource(sourcePath,
						props.getProject());
				
				if (resource != null) {
					resource.deleteMarkers(IMarker.PROBLEM, true,
							IResource.DEPTH_INFINITE);
					resource.deleteMarkers(AdviceMarker.ADVICEMARKER, true,
							IResource.DEPTH_INFINITE);
				}
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns true if the given project has the CaesarJ nature. Returns
	 * false otherwise, or if the nature could not be determined (e.g. the
	 * project is closed).
	 * @param project
	 * @return
	 */
	public static boolean isCJProject(IProject project) {
		if(project.isOpen()) {			
			try {
				if ((project!=null) && project.hasNature(CaesarPlugin.ID_NATURE)) {
					return true;
				}
			} catch (CoreException e) {	}
		}
		return false;
	}
	
	public static boolean isCJSourceName(String fileName) {
		return (fileName.endsWith(".java") || fileName.endsWith(".cj"));
	}
	
	public static boolean isCJSource(IFile file) {
		if (file == null)
			return false;
		return isCJSourceName(file.getName()) && isCJProject(file.getProject());
	}
}
