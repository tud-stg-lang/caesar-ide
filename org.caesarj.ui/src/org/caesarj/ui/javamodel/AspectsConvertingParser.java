/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Luzius Meisser - initial implementation
 *******************************************************************************/
package org.caesarj.ui.javamodel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;

/**
 * The purpose of this parser is to convert AspectJ code into similar Java code
 * which allows us to reuse for example for jdt formatting or comment
 * generation.
 * 
 * Depending on the ConversionOptions it gets called with, it does: - replace
 * the keyword "aspect" by "class " - replace all the '.'s in intertype
 * declarations by an '$'s to make them look like an ordinary declarations. -
 * erase the keywords "returning", "throwing", "privileged", "issingleton" e.g.:
 * "after() throwing (Exception e)" -> "after( Exception e)" - erase pointcut
 * designators (includes "percflow" & co.) - add dummy references to all erased
 * class references to end of buffer to make "organize imports" work correctly -
 * add a reference to the target class inside intertype method declarations to
 * simulate the context switch necessary to get proper code completion.
 * (A detailed description of how code completion works in AJDT can be found in
 * bug 74419.)
 * 
 * Restrictions: - class names inside pointcut designators must begin with a
 * capital letter to be recognised as such
 * 
 * 
 * @author Luzius Meisser
 */
public class AspectsConvertingParser implements TerminalTokens {

	public char[] content;

	private Set typeReferences;

	private Set usedIdentifiers;

	//list of replacements
	//by convetion: sorted by posBefore in ascending order
	private ArrayList replacements;

	protected Scanner scanner;

	public AspectsConvertingParser(char[] content) {
		this.content = content;
		this.typeReferences = new HashSet();
		this.usedIdentifiers = new HashSet();
		replacements = new ArrayList(5);
	}

	private static final char[] throwing = "throwing".toCharArray(); //$NON-NLS-1$
	
	private static final char[] ca_cclass = "cclass".toCharArray(); //$NON-NLS-1$
	
	private static final char[] ca_class_ = "class ".toCharArray(); //$NON-NLS-1$
	
	private static final char[] ca_wraps = "wraps".toCharArray(); //$NON-NLS-1$
	
	private static final char[] returning = "returning".toCharArray(); //$NON-NLS-1$

	private static final String thizString = "thiz"; //$NON-NLS-1$

	private boolean insidePointcutDesignator;

	//private boolean insideAspect;

	private boolean insideAspectDeclaration;
	
	public class Replacement {
		//the position in the original char[]
		public int posBefore;

		//the position in the new char[], or -1 if not yet applied
		public int posAfter;

		//the number of chars that get replaced
		public int length;

		//the content to be inserted
		public char[] text;

		//the number of additional chars (lengthAdded == text.length - length)
		public int lengthAdded;

		public Replacement(int pos, int length, char[] text) {
			this.posBefore = pos;
			this.posAfter = -1;
			this.length = length;
			this.text = text;
			lengthAdded = text.length - length;
		}

	}

	int posColon;

	//returns a list of Insertions to let the client now what has been inserted
	//into the buffer so he can translate positions from the old into the new
	//buffer
	public ArrayList convert(ConversionOptions options) {
		boolean insertThisJoinPointReferences = options
				.isThisJoinPointReferencesEnabled();
		boolean addReferencesForOrganizeImports = options
				.isDummyTypeReferencesForOrganizeImportsEnabled();
		boolean isSimulateContextSwitchNecessary = (options.getTargetType() != null);

		scanner = new Scanner();
		scanner.setSource(content);

		insidePointcutDesignator = false;
		//insideAspect = false;
		insideAspectDeclaration = false;
		
		// Bug 93248: Count question marks so as to ignore colons that are part of conditional statements		
		int questionMarkCount = 0; 
		
		// Bug 110751: Ignore colons that are part of enhanced "for" loop in Java 5
		boolean insideFor = false;
		
		replacements.clear();
		typeReferences.clear();
		usedIdentifiers.clear();

		int tok;		
		while (true) {

			try {
				tok = scanner.getNextToken();
			} catch (InvalidInputException e) {
				continue;
			}
			if (tok == TokenNameEOF)
				break;

			switch (tok) {
			case TokenNameIdentifier:
				char[] name = scanner.getCurrentIdentifierSource();
				if (CharOperation.equals(ca_cclass, name)) {
					int pos = scanner.getCurrentTokenStartPosition();
					addReplacement(pos, ca_cclass.length, ca_class_);
				}
				else if (CharOperation.equals(ca_wraps, name)) {
					replaceWrappee();
				}
				else if (CharOperation.equals(throwing, name))
					consumeRetOrThro();
				else if (CharOperation.equals(returning, name))
					consumeRetOrThro();
				else if (insidePointcutDesignator
						&& Character.isUpperCase(name[0])
						&& (content[scanner.getCurrentTokenStartPosition()-1]!='.')) {
					typeReferences.add(new String(name));
				}

				if (isSimulateContextSwitchNecessary) {
					usedIdentifiers.add(new String(name));
				}
				break;
			case TokenNameextends:
				eliminateMultipleInheritance();
				break;				
			case TokenNamefor:
				insideFor=true;
				break;
			case TokenNameRPAREN:
				insideFor=false;
				break;
			case TokenNameCOLON:
				//if (!insideAspect)
				//	break;
				if (insideFor)
					break;
				if (questionMarkCount > 0) {
					questionMarkCount--;
					break;
				}
				startPointcutDesignator();
				break;
				
			case TokenNameQUESTION:
				questionMarkCount++;
				break;
				
			case TokenNameSEMICOLON:
				if (insidePointcutDesignator)
					endPointcutDesignator();
				break;

			case TokenNameLBRACE:
				if (insidePointcutDesignator) {
					endPointcutDesignator();
					//must be start of advice body -> insert tjp reference
					if (insertThisJoinPointReferences
							&& !insideAspectDeclaration)
						addReplacement(
								scanner.getCurrentTokenStartPosition() + 1, 0,
								tjpRefs2);
				}
				insideAspectDeclaration = false;

				break;
			case TokenNameRBRACE:
				if (insidePointcutDesignator) {
					// bug 129367: if we've hit a } here, we must be
					// in the middle of an unterminated pointcut
					endPointcutDesignator();
				}				
				break;
			}
		}

		if (insidePointcutDesignator) {
			// bug 129367: if we've hit the end of the buffer, we must
			// be in the middle of an unterminated pointcut
			endPointcutDesignator();
		}

		if (addReferencesForOrganizeImports)
			addReferences();

		if (isSimulateContextSwitchNecessary)
			simulateContextSwitch(options.getCodeCompletePosition(), options
					.getTargetType());

		applyReplacements();

		//System.out.println(new String(content));
		return replacements;
	}

	/**
	 * Inserts a reference to targetType at the given position. Thanks to this,
	 * we can simulate the context switch necessary in intertype method
	 * declarations.
	 * 
	 * Transformations: - Insertion of local variable 'TargetType thiz' (or, if
	 * thiz is already used, a number is added to thiz to make it unique) at
	 * start of method mody
	 *  - if code completion on code like 'this.methodcall().more...', 'this
	 * gets replaced by thiz
	 *  - if code completion on code like 'methodcall().more...', 'thiz.' gets
	 * added in front.
	 * 
	 * How the correct place for insertion is found: -
	 * 
	 *  
	 */
	private void simulateContextSwitch(int position, char[] targetType) {
		int pos = findInsertionPosition(position - 1) + 1;
		//if code completion on 'this' -> overwrite the this keyword
		int len = 0;
		if ((content[pos] == 't') && (content[pos + 1] == 'h')
				&& (content[pos + 2] == 'i') && (content[pos + 3] == 's')
				&& !Character.isJavaIdentifierPart(content[pos + 4]))
			len = 4;

		String ident = findFreeIdentifier();
		char[] toInsert = (new String(targetType) + ' ' + ident + ';' + ident + '.')
				.toCharArray();
		addReplacement(pos, len, toInsert);

	}
	
	private void eliminateMultipleInheritance() {
		try {
			/* get first parent */
			scanner.getNextToken();
			/* get symbol after parent (can be &) */
			int tok = scanner.getNextToken();
			int start = scanner.getCurrentTokenStartPosition();
			int end = -1;
			List parents = new ArrayList();
			
			if (tok == TokenNameAND) {
				while (tok == TokenNameAND) {
					/* get next parent */
					scanner.getNextToken();
					end = scanner.getCurrentTokenEndPosition();
					char[] parent = scanner.getCurrentIdentifierSource();
					parents.add(String.valueOf(parent));
					/* get symbol after parent (can be &) */
					tok = scanner.getNextToken();
				}
				
				for (int i1 = 0; i1 < parents.size(); i1++) {
					typeReferences.add(parents.get(i1));
				}
				makeBlank(start, end);				
			}
			
			this.scanner.currentPosition = scanner.getCurrentTokenStartPosition();
		}		
		catch (InvalidInputException e) { }
	}
	
	private void makeBlank(int start, int end) {
		char[] blank = new char[end - start + 1];
		for (int i1 = start; i1 <= end; i1++) {
			if (Character.isWhitespace(content[i1])) {
				blank[i1 - start] = content[i1];				
			}
			else {
				blank[i1 - start] = ' ';
			}
		}
		addReplacement(start, end-start+1, blank);
	}
	
	private void replaceWrappee() {
		int pos = scanner.getCurrentTokenStartPosition();
		try {
			scanner.getNextToken();
			char[] wrappeeName = scanner.getCurrentIdentifierSource();			
			int end = findNext('{', pos);
			if (end == -1)
				return;
			String replace = "{ public " + String.valueOf(wrappeeName) + " wrappee;";
			makeBlank(pos, end-1);
			addReplacement(end, 1, replace.toCharArray());
		}		
		catch (InvalidInputException e) { }
	}

	/**
	 * @return An unused identifier
	 */
	private String findFreeIdentifier() {
		int i = 0;
		String ident = thizString + i;
		while (usedIdentifiers.contains(ident)) {
			i++;
			ident = thizString + i;
		}
		return ident;
	}

	/**
	 * @param pos -
	 *            a code position
	 * @return the position that defines the context of the current one at the
	 *         highest level
	 * 
	 * e.g. ' this.doSomthing().get' with pos on the last 't' returns the
	 * position of the char before the first 't'
	 */
	private int findInsertionPosition(int pos) {
		char ch = content[pos];
		int currentPos = pos;

		if (Character.isWhitespace(ch)) {
			currentPos = findPreviousNonSpace(pos);
			if (currentPos == -1)
				return pos;

			ch = content[currentPos];
			if (ch == '.')
				return findInsertionPosition(--currentPos);
			return pos;
		}

		if (Character.isJavaIdentifierPart(ch)) {
			while (Character.isJavaIdentifierPart(ch)) {
				currentPos--;
				ch = content[currentPos];
			}
			return findInsertionPosition(currentPos);
		}

		if (ch == '.') {
			return findInsertionPosition(--pos);
		}

		if (ch == ')') {
			currentPos--;
			int bracketCounter = 1;
			while (currentPos >= 0) {
				ch = content[currentPos];
				if (bracketCounter == 0)
					break;
				if (ch == ')')
					bracketCounter++;
				if (ch == '(') {
					bracketCounter--;
					if (bracketCounter < 0)
						return -1;
				}
				currentPos--;
			}
			return findInsertionPosition(currentPos);
		}

		return pos;
	}

	char[] tjpRefs2 = "org.aspectj.lang.JoinPoint thisJoinPoint; org.aspectj.lang.JoinPoint.StaticPart thisJoinPointStaticPart;" //$NON-NLS-1$
			.toCharArray();

	char[] tjpRefs = "".toCharArray(); //$NON-NLS-1$

	private void applyReplacements() {
		Iterator iter = replacements.listIterator();
		int offset = 0;
		while (iter.hasNext()) {
			Replacement ins = (Replacement) iter.next();
			ins.posAfter = ins.posBefore + offset;
			replace(ins.posAfter, ins.length, ins.text);
			offset += ins.lengthAdded;
		}
	}

	private void replace(int pos, int length, char[] text) {
		if (length != text.length) {
			int toAdd = text.length - length;
			char[] temp = new char[content.length + toAdd];
			System.arraycopy(content, 0, temp, 0, pos);
			System.arraycopy(content, pos, temp, pos + toAdd, content.length
					- pos);
			content = temp;
		}
		System.arraycopy(text, 0, content, pos, text.length);
	}

	private void startPointcutDesignator() {
		if (insidePointcutDesignator)
			return;
		insidePointcutDesignator = true;
		posColon = scanner.getCurrentTokenStartPosition();
	}

	/**
	 *  
	 */
	private void endPointcutDesignator() {
		insidePointcutDesignator = false;
		int posSemi = scanner.getCurrentTokenStartPosition();
		int len = posSemi - posColon;
		char[] empty = new char[len];
		for (int i = 0; i < empty.length; i++) {
			empty[i] = ' ';
		}
		addReplacement(posColon, len, empty);
	}

	public int findPrevious(char ch, int pos) {
		while (pos >= 0) {
			if (content[pos] == ch)
				return pos;
			pos--;
		}
		return -1;
	}

	public int findPrevious(char[] chs, int pos) {
		while (pos >= 0) {
			for (int i = 0; i < chs.length; i++) {
				if (content[pos] == chs[i])
					return pos;
			}
			pos--;
		}
		return -1;
	}

	public int findPreviousSpace(int pos) {
		while (pos >= 0) {
			if (Character.isWhitespace(content[pos]))
				return pos;
			pos--;
		}
		return -1;
	}

	public int findPreviousNonSpace(int pos) {
		while (pos >= 0) {
			if (!Character.isWhitespace(content[pos]))
				return pos;
			pos--;
		}
		return -1;
	}

	public int findNext(char[] chs, int pos) {
		while (pos < content.length) {
			for (int i = 0; i < chs.length; i++) {
				if (content[pos] == chs[i])
					return pos;
			}
			pos++;
		}
		return -1;
	}
	
	public int findNext(char ch, int pos) {
		while (pos < content.length) {
			if (content[pos] == ch)
				return pos;
			pos++;
		}
		return -1;
	}

	char[] endThrow = new char[] { '(', ':' };

	public void consumeRetOrThro() {
		int pos = scanner.getCurrentTokenStartPosition();
		char[] content = scanner.source;

		int end = findNext(endThrow, pos);
		if (end == -1)
			return;

		char[] temp = null;
		if (content[end] == endThrow[0]) {
			pos = findPrevious(')', pos);
			if (pos == -1)
				return;
			int advicebracket = findPrevious('(', pos);
			if (advicebracket == -1)
				return;
			temp = new char[end - pos + 1];
			if (bracketsContainSomething(advicebracket)
					&& bracketsContainSomething(end))
				temp[0] = ',';
			else
				temp[0] = ' ';
			for (int i = 1; i < temp.length; i++) {
				temp[i] = ' ';
			}
		} else {
			temp = new char[end - pos];
			for (int i = 0; i < temp.length; i++) {
				temp[i] = ' ';
			}
		}
		addReplacement(pos, temp.length, temp);
	}

	/**
	 * @param end
	 * @return
	 */
	private boolean bracketsContainSomething(int start) {
		while (++start < content.length) {
			if (content[start] == ')')
				return false;
			if (Character.isJavaIdentifierPart(content[start]))
				return true;
		}
		return false;
	}

	private int findLast(char ch) {
		int pos = content.length;
		while (--pos >= 0) {
			if (content[pos] == ch)
				break;
		}
		return pos;
	}

	//adds references to all used type -> organize imports will work
	public void addReferences() {
		if (typeReferences == null)
			return;

		//char[] decl = new char[] { ' ', 'x', ';' };
		int pos = findLast('}');
		if (pos < 0)
			return;
		StringBuffer temp = new StringBuffer(typeReferences.size() * 10);
		Iterator iter = typeReferences.iterator();
		int varCount=1;
		while (iter.hasNext()) {
			String ref = (String) iter.next();
			temp.append(ref);
			temp.append(" x"); //$NON-NLS-1$
			temp.append(varCount++);
			temp.append(';');
		}
		char[] decls = new char[temp.length()];
		temp.getChars(0, decls.length, decls, 0);
		addReplacement(pos, 0, decls);
	}

	//adds a replacement to list
	//pre: list sorted, post: list sorted
	void addReplacement(int pos, int length, char[] text) {
		int last = replacements.size() - 1;
		while (last >= 0) {
			if (((Replacement) replacements.get(last)).posBefore < pos)
				break;
			last--;
		}
		replacements.add(last + 1, new Replacement(pos, length, text));
	}

	public static boolean conflictsWithAJEdit(int offset, int length,
			ArrayList replacements) {
		Replacement ins;
		for (int i = 0; i < replacements.size(); i++) {
			ins = (Replacement) replacements.get(i);
			if ((offset >= ins.posAfter) && (offset < ins.posAfter + ins.length)) {
				return true;
			}
			if ((offset < ins.posAfter) && (offset + length > ins.posAfter)) {
				return true;
			}
		}
		return false;
	}
	
	//translates a position from after to before changes
	//if the char at that position did not exist before, it returns the
	// position before the inserted area
	public static int translatePositionToBeforeChanges(int posAfter,
			ArrayList replacements) {
		Replacement ins;
		int offset = 0, i;

		for (i = 0; i < replacements.size(); i++) {
			ins = (Replacement) replacements.get(i);
			if (ins.posAfter > posAfter)
				break;
			offset += ins.lengthAdded;
		}
		if (i > 0) {
			ins = (Replacement) replacements.get(i - 1);
			if (ins.posAfter + ins.text.length > posAfter) {
				//diff must be > 0
				int diff = posAfter - ins.posAfter;
				if (diff > ins.length)
					//we are in inserted area -> return pos directly before
					// that area
					offset += diff - ins.length;
			}
		}

		return posAfter - offset;
	}

	//translates a position from before to after changes
	public static int translatePositionToAfterChanges(int posBefore,
			ArrayList replacements) {
		for (int i = 0; i < replacements.size(); i++) {
			Replacement ins = (AspectsConvertingParser.Replacement) replacements
					.get(i);
			if (ins.posAfter <= posBefore)
				posBefore += ins.lengthAdded;
			else
				return posBefore;
		}
		return posBefore;
	}

}