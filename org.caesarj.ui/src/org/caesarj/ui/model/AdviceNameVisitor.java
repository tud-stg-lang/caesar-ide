package org.caesarj.ui.model;

import java.util.Iterator;

import org.aspectj.asm.StructureNode;
import org.aspectj.weaver.NameMangler;
import org.aspectj.weaver.TypeX;
import org.caesarj.compiler.ast.AdviceDeclaration;
import org.caesarj.kjc.JClassDeclaration;

/**
 * Dumps StructureModel using PrintStream
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class AdviceNameVisitor extends AbstractAsmVisitor {

    public void visit(StructureNode node) {       

        if(node instanceof AdviceDeclarationNode) {
            AdviceDeclarationNode adviceNode = (AdviceDeclarationNode)node;
            
            AdviceDeclaration adviceDeclaration = adviceNode.getAdviceDeclaration();
            JClassDeclaration classDeclaration = adviceNode.getClassDeclaration();
            
            String ident =
                NameMangler.adviceName(
                    TypeX.forName(classDeclaration.getCClass().getQualifiedName()),
                    adviceDeclaration.getKind(),
                    adviceDeclaration.getTokenReference().getLine()
                );

            adviceNode.setBytecodeName(ident);
        }

        super.visit(node);
    }    
    
}
