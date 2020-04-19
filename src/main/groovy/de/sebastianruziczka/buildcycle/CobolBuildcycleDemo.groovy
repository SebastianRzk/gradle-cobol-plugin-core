package de.sebastianruziczka.buildcycle

import org.gradle.api.Project
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.buildcycle.demo.CobolDemoConstants
import de.sebastianruziczka.buildcycle.demo.HelloWorldConfigureTask
import de.sebastianruziczka.buildcycle.demo.HelloWorldCopyTask

class CobolBuildcycleDemo {
	void apply (Project project, CobolExtension conf){
		Logger logger = LoggerFactory.getLogger('runCobol')

		
		project.task ('helloWorldCopySource', type: HelloWorldCopyTask) {
			group 'COBOL Demo'
			description 'Copys ' + CobolDemoConstants.COBOL_SRC_FILE_PATH + ' in src/main/cobol'
			helloWorldFilePath = conf.srcMainPath + CobolDemoConstants.COBOL_SRC_FILE_PATH
		}

		project.task ('helloWorldConfigure', type: HelloWorldConfigureTask) {
			group 'COBOL Demo'
			description 'Set configuration for ' + CobolDemoConstants.COBOL_SRC_FILE_PATH
		}
		
		project.task ('helloWorld', dependsOn: ['helloWorldCopySource', 'helloWorldConfigure', 'runDebug']) {
			group 'COBOL Demo'
			description 'Copys ' + CobolDemoConstants.COBOL_SRC_FILE_PATH + ' in src/main/cobol and executes it'
		}
		project.tasks.runDebug.mustRunAfter project.tasks.helloWorldConfigure
		project.tasks.helloWorldConfigure.mustRunAfter project.tasks.helloWorldCopySource
	}
}
