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
 */
package org.caesarj.ui.util;

import org.caesarj.compiler.KjcClassReader;
import org.caesarj.compiler.export.CClass;
import org.caesarj.compiler.types.KjcSignatureParser;
import org.caesarj.compiler.types.KjcTypeFactory;

/**
 * This is an utility class for manipulating CClass files.
 * 
 * It can be used at once, with static methods, or it can be instantiated
 * and initialized for multiple use (may be faster for several queries).
 * 
 * @author Thiago Tonelli Bartolomei <bart@macacos.org>
 * 
 */
public class CaesarJClassUtil {
	
	/**
	 * The class reader
	 */
	private KjcClassReader classReader = null;
	
	/**
	 * The type factory
	 */
	private KjcTypeFactory factory = null;
	
	/**
	 * Creates and initializes a new instance.
	 * 
	 * @param outputdir
	 */
	public CaesarJClassUtil(String outputdir) {
		classReader = new KjcClassReader(outputdir, outputdir, new KjcSignatureParser());
		factory = new KjcTypeFactory(classReader);
	}
	
	/**
	 * Loads a class from the classname using the pre initialized classReader and Factory
	 * 
	 * @param classname
	 * @return
	 */
	public CClass loadCClass(String classname) {
		return classReader.loadClass(factory, classname);
	}
	
	/**
	 * Reads  the class defined by the outputdir and classname and loads it in a CClass object
	 * 
	 * @param outputdir
	 * @param classname
	 * @return
	 */
	public static CClass loadCClass(String outputdir, String classname) {
		
		// Create the Caesar Class (CCLass) Object using the compiler's class reader
		KjcClassReader classReader = new KjcClassReader(outputdir, outputdir, new KjcSignatureParser());
		KjcTypeFactory factory = new KjcTypeFactory(classReader);
    	return classReader.loadClass(factory, classname);
	}
}
