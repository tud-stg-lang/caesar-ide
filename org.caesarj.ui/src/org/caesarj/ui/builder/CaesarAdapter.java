package org.caesarj.ui.builder;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Unknown;
import org.apache.log4j.Logger;
import org.aspectj.asm.StructureModel;
import org.aspectj.asm.StructureModelManager;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.TypeX;
import org.aspectj.weaver.WeaverStateKind;
import org.aspectj.weaver.bcel.UnwovenClassFile;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.Pointcut;
import org.caesarj.compiler.Main;
import org.caesarj.compiler.PositionedError;
import org.caesarj.compiler.aspectj.CaesarBcelWorld;
import org.caesarj.kjc.JCompilationUnit;
import org.caesarj.kjc.KjcEnvironment;
import org.caesarj.ui.model.SignatureResolver;
import org.caesarj.ui.model.AsmBuilder;
import org.caesarj.ui.model.NodeEliminator;
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
        
        return success;
	}
    
	protected JCompilationUnit parseFile(File file, KjcEnvironment env) {
        if(progressMonitor.isCanceled())
            return null;

        JCompilationUnit res;
        progressMonitor.subTask("compiling "+file.getName());
        res = super.parseFile(file, env);
        progressMonitor.worked(1);
        
        AsmBuilder.build(res, StructureModelManager.INSTANCE.getStructureModel());

        return res;
	}

    protected void weaveClasses(UnwovenClassFile[] classFiles) {
        if(progressMonitor.isCanceled())
            return;
        
        progressMonitor.subTask("weaving classes...");
        
        StructureModel model = StructureModelManager.INSTANCE.getStructureModel();
        
        // build model
        /*
        for(Iterator it=compilationUnits.iterator(); it.hasNext(); ) {        
            AsmBuilder.build((JCompilationUnit)it.next(), model);
        }
        */
        

        SignatureResolver adviceNameVisitor = new SignatureResolver();
        adviceNameVisitor.visit(model.getRoot());

        log.debug("--- structure model before weave ---");
        StructureModelDump modelDumpBeforeWeave = new StructureModelDump(System.out);            
        modelDumpBeforeWeave.print("", model.getRoot());
                

        // add model to the world        
        CaesarBcelWorld world = CaesarBcelWorld.getInstance();
        world.setModel(model);

        /*
         * WEAVE
         */
		super.weaveClasses(classFiles);
        
        
        NodeEliminator registryNodeEliminator = new NodeEliminator();
        registryNodeEliminator.visit(model.getRoot());
        registryNodeEliminator.eliminateNodes();
        
        
        log.debug("--- structure model after weave ---");
        StructureModelDump modelDumpAfterWeave = new StructureModelDump(System.out);
        modelDumpAfterWeave.print("", model.getRoot());
                
        progressMonitor.worked(1);
	}

}
