/*
 * This source file is part of CaesarJ 
 * For the latest info, see http://caesarj.org/
 * 
 * Copyright © 2003-2005 
 * Darmstadt University of Technology, Software Technology Group
 * Also see acknowledgements in readme.txt
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * $Id: CaesarModelTest.java,v 1.8 2005-04-11 09:03:28 thiago Exp $
 */

package org.caesarj.ui.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.aspectj.asm.IHierarchy;

import org.caesarj.compiler.asm.CaesarJElementHierarchy;
import org.caesarj.ui.builder.CaesarAdapter;
import org.aspectj.asm.internal.AspectJElementHierarchy;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.internal.ProgramElement;

/**
 * Test for ASM.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public class CaesarModelTest {
	
    protected static IHierarchy hierarchy = new CaesarJElementHierarchy();
	
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
    	System.out.println("CaesarTest.setupModel() called!");
		String rootLabel = "<root>"; //$NON-NLS-1$
		
		hierarchy.setRoot(
		        new ProgramElement(
		                rootLabel, 
		                IProgramElement.Kind.FILE_JAVA,
		                new ArrayList()));
		hierarchy.setFileMap(new HashMap());
    }
}
