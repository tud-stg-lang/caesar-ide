package org.caesarj.ui.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.caesarj.kjc.JExpression;
import org.caesarj.kjc.JFieldDeclaration;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JInterfaceDeclaration;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPackageImport;
import org.caesarj.kjc.JPackageName;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;
import org.caesarj.util.InconsistencyException;

/**
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class AsmBuilder extends CaesarVisitor {

    private static final String REGISTRY_CLASS_NAME = "Registry";

    protected StructureModel structureModel = null;
    protected Stack asmStack = new Stack();
    
    protected JClassDeclaration currentClassDeclaration;

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
        if(unit==null)
            return;
            
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

        CaesarProgramElementNode cuNode = new CaesarProgramElementNode(
            new String(file.getName()),
            CaesarProgramElementNode.Kind.FILE_JAVA,
            makeLocation(ref),
            0,
            "",
            new ArrayList()
        );

        if (packageName != null) {
            
            String pkgName = packageName.getName();
            
            pkgName = pkgName.replaceAll("/", ".");
            
            boolean found = false;
            CaesarProgramElementNode pkgNode = null;
            
            for (Iterator it = getStructureModel().getRoot().getChildren().iterator(); it.hasNext(); ) {
                CaesarProgramElementNode currNode = (CaesarProgramElementNode)it.next();
                if (currNode.getName().equals(pkgName)) pkgNode = currNode;
            }
            
            if (pkgNode == null) {
                pkgNode = new CaesarProgramElementNode(
                    pkgName, 
                    CaesarProgramElementNode.Kind.PACKAGE, 
                    new ArrayList()
                );
                getStructureModel().getRoot().addChild(pkgNode);
            }   
            
            // if the node already exists remove before adding
            CaesarProgramElementNode duplicate = null;   
             
            for (Iterator itt = pkgNode.getChildren().iterator(); itt.hasNext(); ) {
                CaesarProgramElementNode child = (CaesarProgramElementNode)itt.next();
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
            CaesarProgramElementNode duplicate = null;    
            
            for (Iterator itt = getStructureModel().getRoot().getChildren().iterator(); itt.hasNext(); ) {
                CaesarProgramElementNode child = (CaesarProgramElementNode)itt.next();
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
        CaesarProgramElementNode peNode = new CaesarProgramElementNode(
            ident,
            CaesarProgramElementNode.Kind.INTERFACE,
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
        
        currentClassDeclaration = self;
        
        CaesarProgramElementNode.Kind kind = 
            CModifier.contains(modifiers, CModifier.ACC_CROSSCUTTING) ?
                CaesarProgramElementNode.Kind.ASPECT
                : CaesarProgramElementNode.Kind.CLASS;
	
        CaesarProgramElementNode peNode = new CaesarProgramElementNode(
            ident,
            kind,
            makeLocation(self.getTokenReference()),
            modifiers,
            "",
            new ArrayList()
        );

        getCurrentStructureNode().addChild(peNode);
        asmStack.push(peNode);
                
        
        // super method ruft nicht die field visitors auf        
        /*
		super.visitClassDeclaration(
			self, modifiers, ident, typeVariables, superClass,
			interfaces, body, methods, decls
        );
        */
                
        for (int i = 0; i < decls.length; i++) {
            decls[i].accept(this);
        }   

        for (int i = 0; i < methods.length; i++) {
            methods[i].accept(this);
        }   
        
        for (int i = 0; i < body.length; i++) {
            body[i].accept(this);
        }
        
        
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
     * TODO untested
	 */
	public void visitConstructorDeclaration(
		JConstructorDeclaration self,
		int modifiers,
		String ident,
		JFormalParameter[] parameters,
		CReferenceType[] exceptions,
		JConstructorBlock body
    ) {
        CaesarProgramElementNode peNode = new CaesarProgramElementNode(
            ident,
            CaesarProgramElementNode.Kind.CONSTRUCTOR,    
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
                
        CaesarProgramElementNode peNode = null;
        
        if(self instanceof AdviceDeclaration) {
            AdviceDeclaration advice = (AdviceDeclaration)self;
            
            CaesarProgramElementNode registryNode =
                findChildByName(getCurrentStructureNode().getChildren(), REGISTRY_CLASS_NAME);
                
            if(registryNode == null) {
                registryNode = new AspectRegistryNode(
                    "Registry",
                    CaesarProgramElementNode.Kind.CLASS,
                    makeLocation(self.getTokenReference()),
                    modifiers,
                    "",
                    new ArrayList()
                );
                
                getCurrentStructureNode().addChild(registryNode);
            }
            
            peNode = new AdviceDeclarationNode(
                advice,
                currentClassDeclaration,
                advice.getKind().getName(),
                CaesarProgramElementNode.Kind.ADVICE,
                makeLocation(self.getTokenReference()),
                modifiers,
                "",
                new ArrayList()
            );
            
            setBytecodeSignature(peNode, ident, parameters, returnType);
            registryNode.addChild(peNode);
        }
        else if(self instanceof PointcutDeclaration) {
            PointcutDeclaration pointcut = (PointcutDeclaration)self;
    
            peNode = new CaesarProgramElementNode(
                ident,
                CaesarProgramElementNode.Kind.POINTCUT,
                makeLocation(self.getTokenReference()),
                modifiers,
                "",
                new ArrayList()
            );
            
            setBytecodeSignature(peNode, ident, parameters, returnType);
            getCurrentStructureNode().addChild(peNode);  
        }
        else {        
            peNode = new CaesarProgramElementNode(
                ident,
                CaesarProgramElementNode.Kind.METHOD,
                makeLocation(self.getTokenReference()),
                modifiers,
                "",
                new ArrayList()
            );            
                                    
            if (ident.equals("main")) {
               peNode.setRunnable(true);
            }
            
            setBytecodeSignature(peNode, ident, parameters, returnType);
            getCurrentStructureNode().addChild(peNode);  
        }   

	}
    
    /**
	 * FIELD
	 */
	public void visitFieldDeclaration(
		JFieldDeclaration self,
		int modifiers,
		CType type,
		String ident,
		JExpression expr
    ) {        
        CaesarProgramElementNode peNode = new CaesarProgramElementNode(
            ident,
            CaesarProgramElementNode.Kind.FIELD,    
            makeLocation(self.getTokenReference()),
            modifiers,
            "",
            new ArrayList()
        );   

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
    
    // TODO NOT WORKING
    private String getFullQualifiedName(CType type) {
        String res;
        
        if(type instanceof CReferenceType)
            res = 'L'+((CReferenceType)type).getQualifiedName()+';'; 
        else
            res = type.getSignature();
        
        return res;
    }
    
    private CaesarProgramElementNode findChildByName(Collection childrenList, String name) {
        for (Iterator it = childrenList.iterator(); it.hasNext();) {
			CaesarProgramElementNode node = (CaesarProgramElementNode) it.next();
            
			if(node.getName().equals(name))
                return node;
		}
        
        return null;
    }
    
    private void setBytecodeSignature(
        CaesarProgramElementNode peNode,
        String ident,
        JFormalParameter[] parameters,
        CType returnType
    ) {
        StringBuffer byteCodeSig = new StringBuffer();
                        
        byteCodeSig.append("(");
        for(int i=0; i<parameters.length; i++) {
            byteCodeSig.append(getFullQualifiedName(parameters[i].getType()));
        }
        byteCodeSig.append(")");
        byteCodeSig.append(returnType.getSignature());

        peNode.setBytecodeName(ident);
        peNode.setBytecodeSignature(byteCodeSig.toString());
    }
}
