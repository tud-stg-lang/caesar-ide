package org.caesarj.ui.builder;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * ...
 * 
 * @author ivica
 */
public class NullProgressMonitor implements IProgressMonitor {

	public void beginTask(String name, int totalWork) {
	}

	public void done() {
	}

	public void internalWorked(double work) {
	}

	public boolean isCanceled() {
		return false;
	}

	public void setCanceled(boolean value) {
	}

	public void setTaskName(String name) {
	}

	public void subTask(String name) {
	}

	public void worked(int work) {
	}

}
