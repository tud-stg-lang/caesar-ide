package org.caesarj.ui.model;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;

import org.aspectj.asm.AsmManager;
import org.aspectj.asm.HierarchyWalker;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.IRelationship;
import org.aspectj.bridge.ISourceLocation;

/**
 * Dumps StructureModel using PrintStream
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class StructureModelDump extends HierarchyWalker {

    String indent = "";
    PrintStream out;
    AsmManager asmManager;

    public StructureModelDump(AsmManager asmManager, PrintStream out) {
        super();
        this.asmManager = asmManager;
        this.out = out;
    }
    
    public void run() {
        IProgramElement root = asmManager.getHierarchy().getRoot();
        this.process(root);
    }

    public void preProcess(IProgramElement node) {
        out.print(indent);  
        
        printNodeHeader(out, node);
        
        out.print(" '"+node.getBytecodeName()+"' '"+node.getBytecodeSignature()+"'");
                    
        out.println();
                    
        // handle relations
        List relations = asmManager.getRelationshipMap().get(node);
        
        if(relations != null) {           
            for(Iterator it = relations.iterator(); it.hasNext();) {
    			IRelationship relation = (IRelationship)it.next();
    			
                out.println(indent+"--> ["+relation.getKind().toString()+"] "+relation.getName());
    		}
        }
    }
    
    public IProgramElement process(IProgramElement node) {
		indent += "...";
        IProgramElement res = super.process(node);
        if(indent.length()>=3) {
            indent = indent.substring(0, indent.length()-3);
        }
        return res;
	}
    
    protected void printNodeHeader(PrintStream out, IProgramElement node) {
        //out.print(node.getClass().getName());

        out.print("["+node.getKind()+"] "+node.getName());

        ISourceLocation srcLoc = node.getSourceLocation();        
        if(srcLoc != null) {
            out.print("(L "+srcLoc.getLine()+") ");
        }
    }
    
}
