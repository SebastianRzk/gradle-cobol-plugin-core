package de.sebastianruziczka.buildcycle.compile

import javax.inject.Inject

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

import de.sebastianruziczka.CobolExtension

class CobolCompileExecutableTask extends DefaultTask{

	private CobolExtension configuration
	
	@Input
	String target

	@SkipWhenEmpty
	@InputDirectory
	def File inputDir

	@OutputFile
	def File outputDir
	
	public CobolCompileExecutableTask() {
		this.configuration = getProject().extensions.findByType(CobolExtension.class)
	}

	@TaskAction
	public void run() {
		new CobolCompileExecutableImpl().compile(this.project, this.configuration, this.target)
	}
}
