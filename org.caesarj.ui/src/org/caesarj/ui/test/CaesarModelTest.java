package org.caesarj.ui.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.caesarj.ui.builder.CaesarAdapter;

/**
 * Test for ASM.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class CaesarModelTest {

	public static void main(String[] args) {        
        
        Collection errors = new LinkedList();
        
        Collection sourceFiles = new LinkedList();
        
        sourceFiles.add("src/client/Client.java");
        sourceFiles.add("src/pricing/DiscountPricing.java");
        sourceFiles.add("src/pricing/Pricing.java");
        sourceFiles.add("src/pricing/RegularPricing.java");
        sourceFiles.add("src/stockinformationbroker/StockInfo.java");
        sourceFiles.add("src/stockinformationbroker/StockInfoRequest.java");
        sourceFiles.add("src/stockinformationbroker/StockInformationBroker.java");
        sourceFiles.add("src/stockpricing/PerRequestBinding.java");
        sourceFiles.add("src/stockpricing/PerRequestDiscountPricing.java");
        sourceFiles.add("src/stockpricing/PerRequestRegularPricing.java");
        sourceFiles.add("src/stockpricing/PerStockQuoteBinding.java");
        sourceFiles.add("src/stockpricing/PerStockQuoteDiscountPricing.java");
        sourceFiles.add("src/stockpricing/PricingDeployment.java");
        
        CaesarAdapter caesarAdapter = 
            new CaesarAdapter(
                "caesar-test"
            );
        
        setupModel();
        
        // build            
        boolean success =
            caesarAdapter.compile(                
                sourceFiles,
                "caesar-runtime.jar;aspectjrt.jar",
                "bin",
                errors
            );
	}
    
    private static void setupModel() {
        /*
        String rootLabel = "<root>";
        StructureModelManager.INSTANCE.getStructureModel().setRoot(
            new ProgramElementNode(
                rootLabel,
                ProgramElementNode.Kind.FILE_JAVA,
                new ArrayList()
            )
        );
        
        StructureModelManager.INSTANCE.getStructureModel().setFileMap(new HashMap());
        */
    }
}
