package org.caesarj.ui.model;

import java.util.Iterator;

import org.aspectj.asm.StructureNode;

/**
 * Generic ASM Visitor
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public abstract class AbstractAsmVisitor {
    public void visit(StructureNode node) {       
        for(Iterator it=node.getChildren().iterator(); it.hasNext(); ) {
            visit((StructureNode)it.next());
        }
    } 
}
