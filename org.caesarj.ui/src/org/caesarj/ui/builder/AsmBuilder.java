package org.caesarj.ui.builder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.StructureModel;
import org.aspectj.asm.StructureNode;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.SourceLocation;
import org.caesarj.compiler.TokenReference;
import org.caesarj.compiler.ast.AdviceDeclaration;
import org.caesarj.compiler.ast.FjClassDeclaration;
import org.caesarj.compiler.ast.PointcutDeclaration;
import org.caesarj.compiler.util.CaesarVisitor;
import org.caesarj.kjc.CModifier;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JClassDeclaration;
import org.caesarj.kjc.JClassImport;
import org.caesarj.kjc.JCompilationUnit;
import org.caesarj.kjc.JConstructorBlock;
import org.caesarj.kjc.JConstructorDeclaration;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JInterfaceDeclaration;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPackageImport;
import org.caesarj.kjc.JPackageName;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;

/**
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class AsmBuilder extends CaesarVisitor {

    protected StructureModel structureModel = null;
    protected Stack asmStack = new Stack();

    public static void build(
        JCompilationUnit unit,
        StructureModel structureModel
    ) {
        new AsmBuilder().internalBuild(unit, structureModel);
    }
    
    private void internalBuild(
        JCompilationUnit unit,
        StructureModel structureModel
    ) {
        setStructureModel(structureModel);
        asmStack.push(structureModel.getRoot());
        
        unit.accept(this);
        
        asmStack.pop();
        setStructureModel(null);
    }
    
    /**
     * CompilationUnit visit method
     */
	public void visitCompilationUnit(
		JCompilationUnit self,
		JPackageName packageName,
		JPackageImport[] importedPackages,
		JClassImport[] importedClasses,
        JTypeDeclaration[] typeDeclarations
    ) {
        
        TokenReference ref = self.getTokenReference();        
        File file = new File(new String(ref.getFile()));

        ProgramElementNode cuNode = new ProgramElementNode(
            new String(file.getName()),
            ProgramElementNode.Kind.FILE_JAVA,
            makeLocation(ref),
            0,
            "",
            new ArrayList()
        );

        if (packageName != null) {
            
            String pkgName = packageName.getName();
            
            boolean found = false;
            ProgramElementNode pkgNode = null;
            
            for (Iterator it = getStructureModel().getRoot().getChildren().iterator(); it.hasNext(); ) {
                ProgramElementNode currNode = (ProgramElementNode)it.next();
                if (currNode.getName().equals(pkgName)) pkgNode = currNode;
            }
            
            if (pkgNode == null) {
                pkgNode = new ProgramElementNode(
                    pkgName, 
                    ProgramElementNode.Kind.PACKAGE, 
                    new ArrayList());
                getStructureModel().getRoot().addChild(pkgNode);
            }   
            
            // if the node already exists remove before adding
            ProgramElementNode duplicate = null;   
             
            for (Iterator itt = pkgNode.getChildren().iterator(); itt.hasNext(); ) {
                ProgramElementNode child = (ProgramElementNode)itt.next();
                if (child.getSourceLocation().getSourceFile().equals(file)) {
                    duplicate = child;
                } 
            }
            if (duplicate != null) {
                pkgNode.removeChild(duplicate);
            }
            
            pkgNode.addChild(cuNode);
        }
        else {
            // if the node already exists remove before adding
            ProgramElementNode duplicate = null;    
            
            for (Iterator itt = getStructureModel().getRoot().getChildren().iterator(); itt.hasNext(); ) {
                ProgramElementNode child = (ProgramElementNode)itt.next();
                if (child.getSourceLocation().getSourceFile().equals(file)) {
                    duplicate = child;
                } 
            }
            
            if (duplicate != null) {
                getStructureModel().getRoot().removeChild(duplicate);
            }

            getCurrentStructureNode().addChild(cuNode);
        }
        
        try {
            //StructureModelManager.INSTANCE.getStructureModel().getFileMap().put(
            getStructureModel().addToFileMap(
                file.getCanonicalPath(),                
                cuNode
            );
        }        
        catch(IOException ioe) {
        }

        asmStack.push(cuNode);       

		super.visitCompilationUnit(
			self,
			packageName,
			importedPackages,
			importedClasses,
			typeDeclarations
        );
        
        asmStack.pop();
	}

    /**
     * Interface visit method
     */  
    public void visitInterfaceDeclaration(
    	JInterfaceDeclaration self,
    	int modifiers,
    	String ident,
    	CReferenceType[] interfaces,
    	JPhylum[] body,
    	JMethodDeclaration[] methods
    ) {	
        ProgramElementNode peNode = new ProgramElementNode(
            ident,
            ProgramElementNode.Kind.INTERFACE,
            makeLocation(self.getTokenReference()),
            modifiers,
            "",
            new ArrayList());

        getCurrentStructureNode().addChild(peNode);
        asmStack.push(peNode);
        	
		super.visitInterfaceDeclaration(
			self, modifiers, ident, interfaces, body, methods
        );
        
        asmStack.pop();
	}
    
    /**
     * crosscutting und normal classes
     */
	public void visitClassDeclaration(
		JClassDeclaration self,
		int modifiers,
		String ident,
		CTypeVariable[] typeVariables,
		String superClass,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods,
		JTypeDeclaration[] decls
    ) {	
        ProgramElementNode.Kind kind = 
            CModifier.contains(modifiers, CModifier.ACC_CROSSCUTTING) ?
                ProgramElementNode.Kind.ASPECT
                : ProgramElementNode.Kind.CLASS;
	
        ProgramElementNode peNode = new ProgramElementNode(
            ident,
            kind,
            makeLocation(self.getTokenReference()),
            modifiers,
            "",
            new ArrayList()
        );

        getCurrentStructureNode().addChild(peNode);
        asmStack.push(peNode);
                
		super.visitClassDeclaration(
			self, modifiers, ident, typeVariables, superClass,
			interfaces, body, methods, decls
        );
        
        
        // get advices and pointcuts visit
        if (self instanceof FjClassDeclaration) {
            FjClassDeclaration clazz = (FjClassDeclaration) self;
            
            AdviceDeclaration[] advices = clazz.getAdvices();
            for (int i = 0; i < advices.length; i++) {
                advices[i].accept(this);
            }
            
            PointcutDeclaration[] pointcuts = clazz.getPointcuts();
            for (int i = 0; i < pointcuts.length; i++) {
                pointcuts[i].accept(this);
            }
        }
        
        asmStack.pop();
	}
    

    /**
	 * ConstructorDeclaration
     * TODO unfinished
	 */
	public void visitConstructorDeclaration(
		JConstructorDeclaration self,
		int modifiers,
		String ident,
		JFormalParameter[] parameters,
		CReferenceType[] exceptions,
		JConstructorBlock body
    ) {
        ProgramElementNode peNode = new ProgramElementNode(
            ident,
            ProgramElementNode.Kind.CONSTRUCTOR,    
            makeLocation(self.getTokenReference()),
            modifiers,
            "",
            new ArrayList()
        );   
        
        getCurrentStructureNode().addChild(peNode);
        
        asmStack.push(peNode);
        
		super.visitConstructorDeclaration(
			self, modifiers, ident, parameters, exceptions, body
        );
        
        asmStack.pop();
	}

    /**
	 * MethodDeclaration
     * TODO unfinished
	 */
	public void visitMethodDeclaration(
		JMethodDeclaration self,
		int modifiers,
		CTypeVariable[] typeVariables,
		CType returnType,
		String ident,
		JFormalParameter[] parameters,
		CReferenceType[] exceptions,
		JBlock body
    ) {
                
        ProgramElementNode peNode;
        
        if(self instanceof AdviceDeclaration) {
            AdviceDeclaration advice = (AdviceDeclaration)self;
            
            peNode = new ProgramElementNode(
                advice.getKind().getName(),
                ProgramElementNode.Kind.ADVICE,
                makeLocation(self.getTokenReference()),
                modifiers,
                "",
                new ArrayList()
            );
        }
        else if(self instanceof PointcutDeclaration) {
            PointcutDeclaration pointcut = (PointcutDeclaration)self;
    
            peNode = new ProgramElementNode(
                pointcut.getIdent(),
                ProgramElementNode.Kind.POINTCUT,
                makeLocation(self.getTokenReference()),
                modifiers,
                "",
                new ArrayList()
            );
        }
        else {        
            peNode = new ProgramElementNode(
                ident,
                ProgramElementNode.Kind.METHOD,
                makeLocation(self.getTokenReference()),
                modifiers,
                "",
                new ArrayList()
            );
        }        
		
        getCurrentStructureNode().addChild(peNode);
        
	}

    

    /*
     *
     * HELPER METHODS
     *  
     */
    private ISourceLocation makeLocation(TokenReference ref) {
        String fileName = new String(ref.getFile());

        return
            new SourceLocation(new File(fileName), ref.getLine());
    }
     
    private StructureNode getCurrentStructureNode() {
        return (StructureNode)asmStack.peek(); 
    }
    
    private void setStructureModel(StructureModel structureModel) {
        this.structureModel = structureModel;
    }
    
    private StructureModel getStructureModel() {
        return structureModel;
    }
    
}
