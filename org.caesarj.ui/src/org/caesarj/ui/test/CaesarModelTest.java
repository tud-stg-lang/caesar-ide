package org.caesarj.ui.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.aspectj.asm.ProgramElementNode;
import org.aspectj.asm.StructureModelManager;
import org.caesarj.ui.builder.CaesarAdapter;

/**
 * ...
 * 
 * @author ivica
 */
public class CaesarModelTest {

	public static void main(String[] args) {        
        
        Collection errors = new LinkedList();
        
        Collection sourceFiles = new LinkedList();
        
        sourceFiles.add("src/test/Test.java");
        
        CaesarAdapter caesarAdapter = 
            new CaesarAdapter(
                "caesar-test"
            );
        
        setupModel();
        
        // build            
        boolean success =
            caesarAdapter.compile(                
                sourceFiles,
                "caesar-runtime.jar",
                "bin",
                errors
            );
	}
    
    private static void setupModel() {
        String rootLabel = "<root>";
        StructureModelManager.INSTANCE.getStructureModel().setRoot(
            new ProgramElementNode(
                rootLabel,
                ProgramElementNode.Kind.FILE_JAVA,
                new ArrayList()
            )
        );
        
        StructureModelManager.INSTANCE.getStructureModel().setFileMap(new HashMap());
    }
}
