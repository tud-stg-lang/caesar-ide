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
 * $Id: CaesarHierarchyTest.java,v 1.6 2005-01-24 16:57:22 aracic Exp $
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

/**
 * @author Jochen
 *
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
	        factory = new KjcTypeFactory(classReader);
	    }
	    
	    /**
	     * generates the export informations of a class from the classpath
	     */
	    public CClass load(String qn) {
	    	String internalName = qn.replace('.', '/');
	    	CClass clazz = classReader.loadClass(factory, internalName);
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
