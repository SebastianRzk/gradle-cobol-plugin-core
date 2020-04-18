package de.sebastianruziczka.buildcycle.compile

import javax.inject.Inject

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.gradle.work.Incremental
import org.gradle.work.InputChanges
import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.compiler.api.CompileJob

class CobolCompileDebugTask extends DefaultTask{

	private CobolExtension configuration
	
	@Input
	boolean tracing = false

    @Incremental
	@PathSensitive(PathSensitivity.ABSOLUTE)
	@InputDirectory
	def File inputDir

	@OutputDirectory
	def File outputDir
	
	public CobolCompileDebugTask() {
		this.configuration = getProject().extensions.findByType(CobolExtension.class)
	}

	@TaskAction
	public void run(InputChanges inputs) {
		logger.info(inputs.incremental
			? 'Compile debug incrementally'
			: 'Compile debug non-incrementally'
		)
		Set<String> done = new HashSet<>()
		String sourceModule = this.configuration.projectFileResolver(this.configuration.srcMainPath).absolutePath
		
		inputs.getFileChanges().each { change ->
			if (change.fileType == FileType.DIRECTORY) return

			println "${change.changeType}: ${change.normalizedPath}"
			def targetFile = outputDir.file(change.normalizedPath).get().asFile
			if (change.changeType == ChangeType.REMOVED) {
				targetFile.delete()
			} else {
				if (change.file.name.endsWith(this.configuration.srcFileType)) {
					def name = change.file.absolutePath.replace(sourceModule, '')
					compileFile(name, change.file.absolutePath)
					done.add(name)
					return
				}
			}
		}
		logger.info('Compiled Files: ' + done)
	}

	void compileFile(String target, String absoluteTargetPath) {
		String modulePath = new File(this.configuration.absoluteBinMainPath(target)).getParent()
		File module = new File(modulePath)
		/**
		 * Create folder in /build/ when needed
		 */
		if (!module.exists()) {
			logger.info('Create folder for compile: ' + modulePath)
			module.mkdirs()
		}
		CompileJob compileJob = this.configuration.compiler.buildDebug(this.configuration)
				.setTargetAndBuild(absoluteTargetPath)
				.setExecutableDestinationPath(this.configuration.absoluteDebugMainPath(target))
				.addAdditionalOption(this.configuration.fileFormat)
		if (this.tracing) {
			compileJob = compileJob.addCodeCoverageOption()
		}
		compileJob
				.execute('COMPILE DEBUG: ' + target)
	}

	private List resolveCompileDependencies(Project project, CobolExtension conf, String mainFile) {
		def list = []
		def tree = project.fileTree(conf.absoluteSrcMainModulePath(mainFile)).include(conf.filetypePattern())
		tree.each { File file ->
			if (!file.absolutePath.equals(conf.absoluteSrcMainPath(mainFile))){
				list << file.absolutePath
			}
		}
		return list
	}
}

class CompileTarget {
	private String name
	private String absolutePath

	public CompileTarget (String name, String absolutePath) {
		this.name = name
		this.absolutePath = absolutePath
	}

	public String name() {
		return this.name
	}

	public String absolutePath () {
		return this.absolutePath
	}

	@Override
	public String toString() {
		return 'COMPILETARGET{' + this.name + ',' + this.absolutePath + '}'
	}
}
