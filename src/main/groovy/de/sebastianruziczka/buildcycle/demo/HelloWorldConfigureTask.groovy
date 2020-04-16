package de.sebastianruziczka.buildcycle.demo

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import de.sebastianruziczka.CobolExtension

class HelloWorldConfigureTask extends DefaultTask{

	private CobolExtension configuration

	public HelloWorldConfigureTask(CobolExtension configuration) {
		this.configuration = configuration
	}
	
	@TaskAction
	public void run() {
		println 'Overwrite configured srcMain settings'
		this.configuration.srcMain = CobolDemoConstants.COBOL_SRC_MODULE_NAME
		println 'Overwriting configured terminal with console terminal'
		this.configuration.terminal = 'current'
		println 'Overwriting configured file format to fixed'
		this.configuration.fileFormat = 'fixed'
	}
}
