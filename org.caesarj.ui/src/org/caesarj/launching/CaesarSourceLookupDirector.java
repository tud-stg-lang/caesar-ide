package org.caesarj.launching;

import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;
import org.eclipse.jdt.internal.launching.JavaSourceLookupDirector;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaSourceLookupParticipant;

/**
 * Caesar source lookup director.
 * 
 * @author meffert
 */
public class CaesarSourceLookupDirector extends JavaSourceLookupDirector {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.core.sourcelookup.ISourceLookupDirector#initializeParticipants()
	 */
	public void initializeParticipants() {
		addParticipants(new ISourceLookupParticipant[] {new JavaSourceLookupParticipant(), new CaesarSourceLookupParticipant()});
	}
}
