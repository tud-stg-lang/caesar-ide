package org.caesarj.ui.builder;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.aspectj.asm.AsmManager;
import org.aspectj.asm.IHierarchy;
import org.aspectj.asm.IProgramElement;
import org.aspectj.weaver.bcel.UnwovenClassFile;
import org.caesarj.compiler.Main;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.aspectj.CaesarBcelWorld;
import org.caesarj.kjc.JCompilationUnit;
import org.caesarj.kjc.KjcEnvironment;
import org.caesarj.ui.model.AsmBuilder;
import org.caesarj.ui.model.StructureModelDump;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This is the main interface to caesar compiler.
 * Responsible for invoking the compiler, displaying progress,
 * building the structure model and collecting compiler errors.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public final class CaesarAdapter extends Main {
    
    private static Logger log = Logger.getLogger(CaesarAdapter.class);
    
    public static IProgressMonitor NULL_PROGRESS_MONITOR = new NullProgressMonitor();
    
    private Collection       errors;
    private IProgressMonitor progressMonitor;                          
            
    public CaesarAdapter(String projectLocation) {
        super(projectLocation, null);
    }
            
    public void inform(PositionedError error) {
        errors.add(error);
    }

    public boolean compile(        
        Collection sourceFiles,
        String classPath,
        String outputPath,
        Collection errors        
    ) {
        return
            compile(sourceFiles, classPath, outputPath, errors, null);
    }
            
	public boolean compile(        
        Collection sourceFiles,
        String classPath,
        String outputPath,
        Collection errors,
        IProgressMonitor progressMonitor
    ) {		
        
        IHierarchy model = AsmManager.getDefault().getHierarchy();
        
        AsmBuilder.preBuild(model);
        
		boolean success;
		String args[] = new String[sourceFiles.size()+4];
				
		int i = 0;
		
		args[i++] = "-d";
		args[i++] = outputPath;
		
		args[i++] = "-classpath";		
        args[i++] = classPath;
				
		for(Iterator it=sourceFiles.iterator(); it.hasNext(); ) {
			args[i++] = it.next().toString();
		}
		        
        this.errors = errors;
        this.progressMonitor =
            progressMonitor!=null ?
            progressMonitor :
            NULL_PROGRESS_MONITOR;               
        
        this.progressMonitor.beginTask(
            "compiling source files", sourceFiles.size()+1
        );
        
		success = run(args);
        
        AsmBuilder.postBuild(model);        
                
        _dumpModel("final structure model");
        
        return success;
	}
    
	protected JCompilationUnit parseFile(File file, KjcEnvironment env) {
        if(progressMonitor.isCanceled())
            return null;

        JCompilationUnit res;
        progressMonitor.subTask("compiling "+file.getName());
        res = super.parseFile(file, env);
        progressMonitor.worked(1);
        
        IHierarchy model = AsmManager.getDefault().getHierarchy();
        AsmBuilder.build(res, model);

        return res;
	}

    protected void weaveClasses() {
        if(progressMonitor.isCanceled())
            return;
        
        progressMonitor.subTask("weaving classes...");
        
        IHierarchy model = AsmManager.getDefault().getHierarchy();

        AsmBuilder.preWeave(model);
        
        _dumpModel("structure model before weave");

        // add model to world and WEAVE      
        CaesarBcelWorld world = CaesarBcelWorld.getInstance();
        world.setModel(model);
		super.weaveClasses();        
                              
        progressMonitor.worked(1);
	}


    private void _dumpModel(String description) {
        log.debug("--- "+description+" ---");
        IProgramElement root = 
            AsmManager.getDefault().getHierarchy().getRoot();
        
        StructureModelDump modelDumpBeforeWeave = new StructureModelDump(AsmManager.getDefault(), System.out);
        modelDumpBeforeWeave.run();            
    }
}
