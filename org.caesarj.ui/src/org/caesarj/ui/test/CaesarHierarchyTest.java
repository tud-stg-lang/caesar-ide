/*
 * Created on Sep 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.caesarj.ui.test;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.caesarj.compiler.ClassReader;
import org.caesarj.compiler.KjcClassReader;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.KjcSignatureParser;
import org.caesarj.compiler.types.KjcTypeFactory;
import org.caesarj.compiler.types.TypeFactory;
import org.caesarj.navigator.CaesarByteCodeNavigator;
import org.caesarj.ui.views.CaesarHierarchyView;

/**
 * @author Jochen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CaesarHierarchyTest {
	
	    private ClassReader classReader;
	    private TypeFactory factory;
	    private static Logger log = Logger.getLogger(CaesarHierarchyTest.class);
	        
	    /**
	     * @param extDirs extra directories that should be appended to the classpath
	     */
	    public CaesarHierarchyTest(String extDirs) {
	        classReader = new KjcClassReader(extDirs, extDirs, new KjcSignatureParser());
	        factory = new KjcTypeFactory(classReader, false);
	    }
	    
	    /**
	     * generates the export informations of a class from the classpath
	     */
	    public CClass load(String qn) {
	    	CClass clazz = classReader.loadClass(factory, qn);
	    	log.debug("Class:" + clazz + " \n\tInfo:" + clazz.getAdditionalTypeInformation());
	        return clazz;
	    }
	    
	    /**
	     * This method demonstrates the usage of CaesarByteCodeNavigator
	     */
	    public static void main(String[] args) throws IOException {
	    	CaesarHierarchyTest nav = new CaesarHierarchyTest("c:/test");
	        
	        CClass clazz = nav.load("pricing/Pricing");
	        
	        System.out.println(clazz.getAdditionalTypeInformation());
	    }
	}
