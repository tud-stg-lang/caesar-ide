package org.caesarj.ui.builder;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Unknown;
import org.aspectj.asm.StructureModelManager;
import org.aspectj.weaver.AjAttribute;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.WeaverStateKind;
import org.aspectj.weaver.bcel.UnwovenClassFile;
import org.aspectj.weaver.patterns.PerClause;
import org.aspectj.weaver.patterns.Pointcut;
import org.caesarj.compiler.Main;
import org.caesarj.compiler.PositionedError;
import org.caesarj.kjc.JCompilationUnit;
import org.caesarj.kjc.KjcEnvironment;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * CaesarCompiler
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public final class CaesarAdapter extends Main {
    
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
        this.progressMonitor = progressMonitor;
        
        progressMonitor.beginTask(
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
        
                
        // build aspectj structure model
        AsmBuilder.build(res, StructureModelManager.INSTANCE.getStructureModel());
        
        return res;
	}

	protected void weaveClasses(UnwovenClassFile[] classFiles) {
        /*
        // DEBUG inspect classes
        for(int i=0; i<classFiles.length; i++)
            inspectUnwovenClassFile(classFiles[i]);
        */
        if(progressMonitor.isCanceled())
            return;
        
        progressMonitor.subTask("weaving classes...");
        
		super.weaveClasses(classFiles);
        
        progressMonitor.worked(1);
	}


    /*
     * 
     * DEBUG
     * 
     */
     
    public boolean noWeaveMode() {
        return false;
    }

    private void inspectUnwovenClassFile(UnwovenClassFile file) {
        System.out.println("--------------------------------");
                
        JavaClass jclass = file.getJavaClass();
        
        Attribute attribs[] = jclass.getAttributes();
        System.out.println("*** CLASSNAME: "+jclass.getClassName());
        for(int i=0; i<attribs.length; i++) {            
            showClassAttributes(attribs[i]);
        }
                
    }


    public void showClassAttributes(Attribute a) {
        
        if(a instanceof Unknown) {
            Unknown unknown = (Unknown)a;
            AjAttribute aa = 
                AjAttribute.read(unknown.getName(), unknown.getBytes(), null);
        
            if(aa instanceof AjAttribute.WeaverState) {    
                AjAttribute.WeaverState weaverState = (AjAttribute.WeaverState)aa;
                WeaverStateKind kind = weaverState.reify();                    
                System.out.println("weaver state: "+kind);                    
            }
        
            else if(aa instanceof AjAttribute.PointcutDeclarationAttribute) {
                AjAttribute.PointcutDeclarationAttribute pointcutDeclarationAttribute =
                    (AjAttribute.PointcutDeclarationAttribute)aa;
            
                ResolvedPointcutDefinition rpd = pointcutDeclarationAttribute.reify();
                ShadowMunger munger = rpd.getAssociatedShadowMunger();
                Pointcut pointcut = rpd.getPointcut();
                System.out.println("pointcut: "+rpd);
                System.out.println("\tsignature: "+rpd.getSignature());
                System.out.println("\tExtractableName: "+rpd.getExtractableName());
                                    
                System.out.println("\tshadow munger: "+munger);
                System.out.println("\tstart: "+rpd.getStart());
                System.out.println("\tend: "+rpd.getEnd());                    
                System.out.println("\tdeclaring type: "+rpd.getDeclaringType());                    
                System.out.println("\tsignature: "+rpd);                    
                System.out.println("\tpointcut source location: "+pointcut.getSourceLocation());
            
                                
            }
        
            else if(aa instanceof AjAttribute.Aspect) {
                AjAttribute.Aspect aspect =
                    (AjAttribute.Aspect)aa;
                PerClause perClause = aspect.reify(null);
                                                        
                System.out.println(perClause);
            }
        
            else if(aa instanceof AjAttribute.PrivilegedAttribute) {
                AjAttribute.PrivilegedAttribute privilegedAttribute =
                    (AjAttribute.PrivilegedAttribute)aa;
                                    
                ResolvedMember members[] = privilegedAttribute.getAccessedMembers();
                System.out.println("AjAttribute.PrivilegedAttribute");
                System.out.println("\tresolved memebers:");
                for(int j=0; j<members.length; j++)     
                    System.out.print("\t\t"+members[j].getName()+"; ");     
                System.out.println();
            }
        
            else if(aa instanceof AjAttribute.SourceContextAttribute) {
                AjAttribute.SourceContextAttribute sourceContextAttribute =
                    (AjAttribute.SourceContextAttribute)aa;
            
                System.out.println("AjAttribute.SourceContextAttribute");      
                System.out.print("\tline breaks: ");
                int lineBreakes[] = sourceContextAttribute.getLineBreaks();
                for(int j=0; j<lineBreakes.length; j++)     
                    System.out.print(lineBreakes[j]+"; ");
                
                System.out.println();
                System.out.println("\tsource file name: "+sourceContextAttribute.getSourceFileName());
            }               
            else {                
                System.out.println(aa);
            }
        }            
        else {
            System.out.println(a);
        }
        
    }
}
