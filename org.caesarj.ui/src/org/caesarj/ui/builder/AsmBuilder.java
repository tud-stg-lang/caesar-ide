/*
 * Created on 26.10.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.caesarj.ui.builder;

import org.aspectj.asm.StructureModel;
import org.caesarj.compiler.util.CaesarVisitor;
import org.caesarj.kjc.CReferenceType;
import org.caesarj.kjc.CType;
import org.caesarj.kjc.CTypeVariable;
import org.caesarj.kjc.JBlock;
import org.caesarj.kjc.JClassDeclaration;
import org.caesarj.kjc.JCompilationUnit;
import org.caesarj.kjc.JFormalParameter;
import org.caesarj.kjc.JInterfaceDeclaration;
import org.caesarj.kjc.JMethodDeclaration;
import org.caesarj.kjc.JPhylum;
import org.caesarj.kjc.JTypeDeclaration;

/**
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class AsmBuilder extends CaesarVisitor {

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
        // TODO hier geht es los
        
        if(unit!=null) {        
            unit.accept(this);
        }
    }

    
    
    
	public void visitInterfaceDeclaration(
		JInterfaceDeclaration self,
		int modifiers,
		String ident,
		CReferenceType[] interfaces,
		JPhylum[] body,
		JMethodDeclaration[] methods
    ) {	
        
        System.out.println("*** visitInterfaceDeclaration: "+ident);
        	
		super.visitInterfaceDeclaration(
			self,
			modifiers,
			ident,
			interfaces,
			body,
			methods
        );
	}
    
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
        
        System.out.println("*** visitClassDeclaration: "+ident);
        
		super.visitClassDeclaration(
			self,
			modifiers,
			ident,
			typeVariables,
			superClass,
			interfaces,
			body,
			methods,
			decls
        );
	}

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
        
        System.out.println("*** visitMethodDeclaration: "+ident);
        
		super.visitMethodDeclaration(
			self,
			modifiers,
			typeVariables,
			returnType,
			ident,
			parameters,
			exceptions,
			body
        );
	}

	public void visitKopiMethodDeclaration(
		JMethodDeclaration self,
		int modifiers,
		CType returnType,
		String ident,
		JFormalParameter[] parameters,
		CReferenceType[] exceptions,
		JBlock body,
		JBlock ensure,
		JBlock require
    ) {
        
        System.out.println("*** visitKopiMethodDeclaration: "+ident);

		super.visitKopiMethodDeclaration(
			self,
			modifiers,
			returnType,
			ident,
			parameters,
			exceptions,
			body,
			ensure,
			require
        );
	}

}
