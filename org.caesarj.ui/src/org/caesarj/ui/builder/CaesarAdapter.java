package org.caesarj.ui.builder;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.aspectj.asm.StructureModel;
import org.aspectj.asm.StructureModelManager;
import org.caesarj.compiler.KjcEnvironment;
import org.caesarj.compiler.Main;
import org.caesarj.compiler.ast.phylum.JCompilationUnit;
import org.caesarj.ui.model.AsmBuilder;
import org.caesarj.ui.model.StructureModelDump;
import org.caesarj.util.PositionedError;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * This is the main interface to caesar compiler. Responsible for invoking the
 * compiler, displaying progress, building the structure model and collecting
 * compiler errors.
 * 
 * @author Ivica Aracic <ivica.aracic@bytelords.de>
 */
public final class CaesarAdapter extends Main {

	private static Logger log = Logger.getLogger(CaesarAdapter.class);

	public static IProgressMonitor NULL_PROGRESS_MONITOR = new NullProgressMonitor();

	private Collection errors;

	private IProgressMonitor progressMonitor;

	private StructureModel model;

	public CaesarAdapter(String projectLocation) {
		super(projectLocation, null);
	}

	public void inform(PositionedError error) {
		errors.add(error);
	}

	public boolean compile(Collection sourceFiles, String classPath,
			String outputPath, Collection errors) {
		return compile(sourceFiles, classPath, outputPath, errors, null);
	}

	public boolean compile(Collection sourceFiles, String classPath,
			String outputPath, Collection errors,
			IProgressMonitor progressMonitor) {

		model = StructureModelManager.INSTANCE
				.getStructureModel();
		AsmBuilder.preBuild(model);
		boolean success;
		String args[] = new String[sourceFiles.size() + 4];

		int i = 0;

		args[i++] = "-d";
		args[i++] = outputPath;

		args[i++] = "-classpath";
		args[i++] = classPath;

		for (Iterator it = sourceFiles.iterator(); it.hasNext();) {
			args[i++] = it.next().toString();
		}

		this.errors = errors;
		this.progressMonitor = progressMonitor != null ? progressMonitor
				: NULL_PROGRESS_MONITOR;

		this.progressMonitor.beginTask("compiling source files", sourceFiles
				.size() + 1);

		success = run(args);
		AsmBuilder.postBuild(model);
		 _dumpModel("final structure model", model); 
		return success;
	}

	protected JCompilationUnit parseFile(File file, KjcEnvironment env) {
		if (progressMonitor.isCanceled())
			return null;

		JCompilationUnit res;
		progressMonitor.subTask("compiling" + file.getName());
		res = super.parseFile(file, env);
		progressMonitor.worked(1);

		AsmBuilder.build(res, model);

		return res;
	}

	protected void weaveClasses() {
		if (progressMonitor.isCanceled())
			return;

		progressMonitor.subTask("weaving classes...");

		AsmBuilder.preWeave(model);

		//_dumpModel("structure model before weave", model);
		// add model to world and WEAVE
//		CaesarBcelWorld world = CaesarBcelWorld.getInstance();
//		world.setModel(model);
		
		super.weaveClasses();
		
		progressMonitor.worked(1);
	}

	private void _dumpModel(String description, StructureModel model) {

		log.debug("--- " + description + " ---");
		StructureModelDump modelDumpBeforeWeave = new StructureModelDump(
				System.out);
		modelDumpBeforeWeave.print("", model.getRoot());
	}
}