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
 * $Id: CaesarCodeScanner.java,v 1.11 2005-07-28 15:03:47 gasiunas Exp $
 */

package org.caesarj.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.text.AbstractJavaScanner;
import org.eclipse.jdt.internal.ui.text.JavaWhitespaceDetector;
import org.eclipse.jdt.internal.ui.text.JavaWordDetector;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaColorConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * Caesar code scanner responsible for syntax highlighting
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public final class CaesarCodeScanner extends AbstractJavaScanner {

	private static class VersionedWordRule extends WordRule {

		private final String fVersion;

		private final boolean fEnable;

		private String fCurrentVersion;

		public VersionedWordRule(IWordDetector detector, String version,
				boolean enable, String currentVersion) {
			super(detector);

			this.fVersion = version;
			this.fEnable = enable;
			this.fCurrentVersion = currentVersion;
		}

		public void setCurrentVersion(String version) {
			this.fCurrentVersion = version;
		}

		/*
		 * @see IRule#evaluate
		 */

		public IToken evaluate(ICharacterScanner scanner) {
			IToken token = super.evaluate(scanner);

			if (this.fEnable) {
				if (this.fCurrentVersion.equals(this.fVersion)) {
					return token;
				}
				return Token.UNDEFINED;
			} else {
				if (this.fCurrentVersion.equals(this.fVersion)) {
					return Token.UNDEFINED;
				}
				return token;
			}
		}
	}

	private static final String SOURCE_VERSION = "org.eclipse.jdt.core.compiler.source"; //$NON-NLS-1$

	private static String[] fgKeywords = { "abstract", //$NON-NLS-1$
			"break", //$NON-NLS-1$
			"case", //$NON-NLS-1$
			"catch", //$NON-NLS-1$
			"class", //$NON-NLS-1$
			"const", //$NON-NLS-1$
			"continue", //$NON-NLS-1$
			//$NON-NLS-5$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
			"default", "do", //$NON-NLS-2$ //$NON-NLS-1$
			"else", "extends", //$NON-NLS-2$ //$NON-NLS-1$
			"final", "finally", "for", //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
			"goto", //$NON-NLS-1$
			"if", //$NON-NLS-1$
			"implements", //$NON-NLS-1$
			"import", //$NON-NLS-1$
			"instanceof", //$NON-NLS-1$
			"interface", //$NON-NLS-1$
			//$NON-NLS-5$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
			"native", "new", //$NON-NLS-2$ //$NON-NLS-1$
			"package", //$NON-NLS-1$
			"private", //$NON-NLS-1$
			"protected", //$NON-NLS-1$
			"public", //$NON-NLS-1$
			//$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
			"return", //$NON-NLS-1$
			"static", //$NON-NLS-1$
			"super", //$NON-NLS-1$
			"switch", //$NON-NLS-1$
			"synchronized",//$NON-NLS-1$
			//$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
			"this",//$NON-NLS-1$
			"throw",//$NON-NLS-1$
			"throws",//$NON-NLS-1$
			"transient",//$NON-NLS-1$
			"try",//$NON-NLS-1$
			//$NON-NLS-5$ //$NON-NLS-4$ //$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$
			"volatile", //$NON-NLS-1$
			"while" //$NON-NLS-1$
	};

	// AspectJ keywords
	private static String[] ajKeywords = { /* "aspect", */"pointcut",//$NON-NLS-1$
			"privileged",//$NON-NLS-1$
			// Pointcut designators: methods and constructora
			"call", "execution", "initialization", "preinitialization",   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
			// Pointcut designators: exception handlers
			"handler",//$NON-NLS-1$
			// Pointcut designators: fields
			"get", "set",//$NON-NLS-1$ //$NON-NLS-2$
			// Pointcut designators: static initialization
			"staticinitialization",//$NON-NLS-1$
			// Pointcut designators: object
			// (this already a Java keyword)
			"target", "args",//$NON-NLS-1$ //$NON-NLS-2$
			// Pointcut designators: lexical extents
			"within", "withincode",//$NON-NLS-1$ //$NON-NLS-2$
			// Pointcut designators: control flow
			"cflow", "cflowbelow", //$NON-NLS-1$ //$NON-NLS-2$
			// Advice
			"before", "after", "around", "proceed", "throwing", "returning",  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			/* "adviceexecution" , */
			// Declarations
			/*
			 * "declare", "parents" , "warning" , "error", "soft" ,
			 * "precedence",
			 */
			// variables
			"thisJoinPoint", "thisJoinPointStaticPart", //$NON-NLS-1$ //$NON-NLS-2$
			"thisEnclosingJoinPointStaticPart", //$NON-NLS-1$
	// Associations
	/* "issingleton", "perthis", "pertarget", "percflow", "percflowbelow" */};

	// Caesar keywords
	private static String[] caesarKeywords = { "cclass", "wraps", "wrappee",  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
			"deploy", "deployed", "undeploy" }; //$NON-NLS-1$ //$NON-NLS-2$

	private static String[] fgNewKeywords = { "assert" }; //$NON-NLS-1$

	private static String[] fgTypes = { "void", "boolean", "char", "byte",  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"short", "strictfp", "int", "long", "float", "double" };   //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

	//$NON-NLS-1$ //$NON-NLS-5$ //$NON-NLS-7$ //$NON-NLS-6$ //$NON-NLS-8$ //$NON-NLS-9$  //$NON-NLS-10$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-2$

	private static String[] fgConstants = { "false", "null", "true" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	//$NON-NLS-3$ //$NON-NLS-2$ //$NON-NLS-1$

	private static String[] fgTokenProperties = {
			IJavaColorConstants.JAVA_KEYWORD, IJavaColorConstants.JAVA_STRING,
			IJavaColorConstants.JAVA_DEFAULT };

	private VersionedWordRule fVersionedWordRule;

	/**
	 * Creates a Java code scanner
	 */
	public CaesarCodeScanner(IColorManager manager, IPreferenceStore store) {
		super(manager, store);

		initialize();
	}

	/*
	 * @see AbstractJavaScanner#getTokenProperties()
	 */
	protected String[] getTokenProperties() {
		return fgTokenProperties;
	}

	/*
	 * @see AbstractJavaScanner#createRules()
	 */
	protected List createRules() {

		//      System.err.println("AJCodeScanner.createRules() called");
		List rules = new ArrayList();

		// Add rule for strings and character constants.
		Token token = getToken(IJavaColorConstants.JAVA_STRING);
		rules.add(new SingleLineRule("\"", "\"", token, '\\'));  //$NON-NLS-1$//$NON-NLS-2$
		//$NON-NLS-2$ //$NON-NLS-1$
		rules.add(new SingleLineRule("'", "'", token, '\\'));  //$NON-NLS-1$//$NON-NLS-2$
		//$NON-NLS-2$ //$NON-NLS-1$

		// Add generic whitespace rule.
		rules.add(new WhitespaceRule(new JavaWhitespaceDetector()));

		// Add word rule for new keywords, 4077
		Object version = JavaCore.getOptions().get(SOURCE_VERSION);
		if (version instanceof String) {
			this.fVersionedWordRule = new VersionedWordRule(new JavaWordDetector(),
					"1.4", true, (String) version); //$NON-NLS-1$
			//$NON-NLS-1$

			token = getToken(IJavaColorConstants.JAVA_KEYWORD);
			for (int i = 0; i < fgNewKeywords.length; i++)
				this.fVersionedWordRule.addWord(fgNewKeywords[i], token);

			rules.add(this.fVersionedWordRule);
		}

		// Add word rule for keywords, types, and constants.
		token = getToken(IJavaColorConstants.JAVA_DEFAULT);
		WordRule wordRule = new WordRule(new JavaWordDetector(), token);

		token = getToken(IJavaColorConstants.JAVA_KEYWORD);

		for (int i = 0; i < fgKeywords.length; i++)
			wordRule.addWord(fgKeywords[i], token);

		for (int i = 0; i < ajKeywords.length; i++)
			wordRule.addWord(ajKeywords[i], token);

		for (int i = 0; i < caesarKeywords.length; i++)
			wordRule.addWord(caesarKeywords[i], token);

		for (int i = 0; i < fgTypes.length; i++)
			wordRule.addWord(fgTypes[i], token);

		for (int i = 0; i < fgConstants.length; i++)
			wordRule.addWord(fgConstants[i], token);

		rules.add(wordRule);

		setDefaultReturnToken(getToken(IJavaColorConstants.JAVA_DEFAULT));
		return rules;
	}

	/*
	 * @see RuleBasedScanner#setRules(IRule[])
	 */
	public void setRules(IRule[] rules) {
		int i;
		for (i = 0; i < rules.length; i++)
			if (rules[i].equals(this.fVersionedWordRule)) {
				break;
			}

		// not found - invalidate fVersionedWordRule
		if (i == rules.length)
			this.fVersionedWordRule = null;

		super.setRules(rules);
	}

	/*
	 * @see AbstractJavaScanner#affectsBehavior(PropertyChangeEvent)
	 */
	public boolean affectsBehavior(PropertyChangeEvent event) {
		return event.getProperty().equals(SOURCE_VERSION)
				|| super.affectsBehavior(event);
	}

	/*
	 * @see AbstractJavaScanner#adaptToPreferenceChange(PropertyChangeEvent)
	 */
	public void adaptToPreferenceChange(PropertyChangeEvent event) {

		if (event.getProperty().equals(SOURCE_VERSION)) {
			Object value = event.getNewValue();

			if (value instanceof String) {
				String s = (String) value;

				if (this.fVersionedWordRule != null) {
					this.fVersionedWordRule.setCurrentVersion(s);
				}
			}

		} else if (super.affectsBehavior(event)) {
			super.adaptToPreferenceChange(event);
		}
	}

}