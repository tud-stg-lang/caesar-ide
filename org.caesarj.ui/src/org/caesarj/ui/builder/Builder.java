package org.caesarj.ui.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.StructureModelManager;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.TokenReference;
import org.caesarj.ui.util.ProjectProperties;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Builder
 * 
 * TODO [build] incremental build?
 * 
 * @author Ivica Aracic
 */
public class Builder extends IncrementalProjectBuilder {

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
    public static IProject getLastBuildTarget( ) {
        return lastBuiltProject;
    }

    /**
     * @see IncrementalProjectBuilder#build(int, Map, IProgressMonitor)
     * kind is one of: FULL_BUILD, INCREMENTAL_BUILD or AUTO_BUILD
     * currently we do a full build in every case!
     */
    protected IProject[] build (
        int kind,
        Map args,
        IProgressMonitor progressMonitor
    ) throws CoreException {
        try {
            setupModel();
            
			lastBuiltProject = getProject();
            errors.clear();
			
            System.out.println("Ceaser Builder");
            
            projectProperties =
                new ProjectProperties(getProject());
            
            System.out.println(projectProperties.toString());
            
            CaesarAdapter caesarAdapter = 
                new CaesarAdapter(
                    projectProperties.getProjectLocation()
                );
            
            // build            
            boolean success =
                caesarAdapter.compile(                
                    projectProperties.getSourceFiles(),
                    projectProperties.getClassPath(),
                    projectProperties.getOutputPath(),
                    errors,
                    progressMonitor
                );
			        
            // update markers, show errors
            showErrors();
        }
        catch (Throwable t) {           	
        	t.printStackTrace();                     
        }

        IProject[] requiredResourceDeltasOnNextInvocation = null;
        return requiredResourceDeltasOnNextInvocation;
    }
    
    private void setupModel() {
        String rootLabel = "<root>";
        StructureModelManager.INSTANCE.getStructureModel().setRoot(
            new ProgramElementNode(
                rootLabel,
                ProgramElementNode.Kind.FILE_JAVA,
                new ArrayList()
            )
        );
            
        StructureModelManager.INSTANCE.getStructureModel().setFileMap(new HashMap());
    }
    
    // TODO [optimize] make it efficient
    // TODO [feature] warnings are missing
    public void showErrors() {
        
        try {
            Collection sourceFiles = projectProperties.getSourceFiles();
            for(Iterator it=sourceFiles.iterator(); it.hasNext(); ) {

                String sourcePath = projectProperties.getProjectLocation()+it.next().toString(); 

                IResource resource =
                    ProjectProperties.findResource(
                        sourcePath,
                        lastBuiltProject
                    );                    
        
                resource.deleteMarkers(
                    IMarker.PROBLEM,
                    true,
                    IResource.DEPTH_INFINITE
                );
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        
        for(Iterator it=errors.iterator(); it.hasNext(); ) {
            try {
                PositionedError error = (PositionedError)it.next();
                TokenReference token = error.getTokenReference();
                            
                if(token.getLine() > 0) {
                    System.out.println(
                        "file: "+token.getFile()+"\n"+
                        "line: "+token.getLine()+"\n"+
                        "path: "+token.getPath()
                    );
                    
                    IResource resource =
                        ProjectProperties.findResource(
                            token.getPath().getAbsolutePath(),
                            lastBuiltProject
                        );                    

                    IMarker marker = resource.createMarker(IMarker.PROBLEM);
                    marker.setAttribute(IMarker.LINE_NUMBER, token.getLine());
                    marker.setAttribute(IMarker.MESSAGE, error.getFormattedMessage().getMessage());                    
                    marker.setAttribute(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
                }
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }      
    
}
