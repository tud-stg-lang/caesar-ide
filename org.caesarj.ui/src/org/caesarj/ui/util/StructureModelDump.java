package org.caesarj.ui.util;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import org.aspectj.asm.LinkNode;
import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.RelationNode;
import org.aspectj.asm.StructureModel;
import org.aspectj.asm.StructureNode;
import org.aspectj.bridge.ISourceLocation;

/**
 * Dumps StructureModel using PrintStream
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class StructureModelDump {

    protected StructureModel model;

    public StructureModelDump(StructureModel model) {
        this.model = model;
    }

    public void print() {
        print(System.out);
    }

    public void print(PrintStream out) {
        print("", out, model.getRoot());
    }

    protected void print(String indent, PrintStream out, StructureNode node) {
        out.print(indent);  
        
        printNodeHeader(out, node);
        
        if(node instanceof ProgramElementNode) {
            ProgramElementNode peNode = (ProgramElementNode)node;
            out.print(" '"+peNode.getBytecodeName()+"' '"+peNode.getBytecodeSignature()+"'");
                        
            out.println();
                        
            List relations = peNode.getRelations();
            if(relations.size()>0) {
                for(Iterator it=relations.iterator(); it.hasNext(); ) {               
                    print(indent+"++", out, (StructureNode)it.next());
                }
            }
        }
        else if(node instanceof RelationNode) {
            RelationNode relNode = (RelationNode)node;            
            //out.print(" "+relNode.getRelation().toString());
            out.println();
        }
        else if(node instanceof LinkNode) {
            LinkNode linkNode = (LinkNode)node;
            out.print(" ->> "+linkNode.getProgramElementNode().getBytecodeName());
            out.println();
        }
        else {
            out.println();
        }
        
        for(Iterator it=node.getChildren().iterator(); it.hasNext(); ) {
            print(indent+"..", out, (StructureNode)it.next());
        }
    }
    
    protected void printNodeHeader(PrintStream out, StructureNode node) {
        //out.print(node.getClass().getName());

        out.print("["+node.getKind()+"] "+node.getName());

        ISourceLocation srcLoc = node.getSourceLocation();        
        if(srcLoc != null) {
            out.print("(L "+srcLoc.getLine()+") ");
        }
    }
    
}
