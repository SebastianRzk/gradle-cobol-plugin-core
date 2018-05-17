package de.sebastianruziczka.buildcycle

import org.gradle.api.Project
import org.gradle.api.tasks.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import de.sebastianruziczka.CobolExtension
import de.sebastianruziczka.buildcycle.compile.CobolCompileSingleFileTask

class CobolCompile {
	void apply (Project project, CobolExtension conf){
		Logger logger = LoggerFactory.getLogger('compileCobol')

		if (conf.incrementalBuild) {
			project.task ('compileCobolFilesIncremental', type:CobolCompileTask) {
				group 'COBOL'
				description 'Compiles cobol source code'

				onlyIf {
					conf.srcMain != null && !conf.srcMain.equals('')
				}

				outputDir = new File(conf.absoluteBinMainPath())
				inputDir = new File(conf.absoluteSrcMainModulePath())

				configuration = conf
				target = conf.srcMain

				doFirst {
					checkIfMainFileIsSet(logger, conf)
					prepareBinFolder(conf)
				}
			}
			project.task ('createExecutable', type:CobolCompileTask, dependsOn: [
				'compileCobolFilesIncremental'
			]) {
				group 'COBOL'
				description 'Creates an executable from compiled cobol files'

				onlyIf {
					conf.srcMain != null && !conf.srcMain.equals('')
				}

				outputDir = new File(conf.absoluteBinMainPath())
				inputDir = new File(conf.absoluteSrcMainModulePath())

				configuration = conf
				target = conf.srcMain

				doFirst {
					checkIfMainFileIsSet(logger, conf)
					prepareBinFolder(conf)
				}
			}
			project.task ('compileCobol', dependsOn: ['createExecutable']) {
				group 'COBOL'
				description 'Compiles CobolCompileTaskcobol source code and creates executable defined in srcMain. Incremental build enabled.'
			}
		}
		else {
			project.task ('compileCobol', type:CobolCompileTask) {
				group 'COBOL'
				description 'Compiles cobol source code and creates executable defined in srcMain. Incremental build disabled.'

				onlyIf {
					conf.srcMain != null && !conf.srcMain.equals('')
				}

				outputDir = new File(conf.absoluteBinMainPath())
				inputDir = new File(conf.absoluteSrcMainModulePath())

				configuration = conf
				target = conf.srcMain

				doFirst {
					checkIfMainFileIsSet(logger, conf)
					prepareBinFolder(conf)
				}
			}
		}
		project.task ('compileMultiTargetCobol') {
			group 'COBOL'
			description 'Compiles additional executables when defined in multiCompileTargets'
			onlyIf({
				return !conf.multiCompileTargets.isEmpty()
			})
			doFirst {
				prepareBinFolder(conf)
				conf.multiCompileTargets.each{
					CobolCompileSingleFileTask compiler = new CobolCompileSingleFileTask()
					compiler.configuration = conf
					compiler.pr = project
					compiler.target = it
					compiler.compile()
				}
			}
		}
	}


	private checkIfMainFileIsSet(Logger logger, CobolExtension conf) {
		if (conf.srcMain.equals('')) {
			logger.error('No main file configured!')
			logger.error('Please specify main file in your build.gradle')
			print conf.dump()
			throw new Exception('No main file configured!')
		}
	}

	private prepareBinFolder(CobolExtension conf) {
		if (!conf.projectFileResolver(conf.binMainPath).exists()){
			conf.projectFileResolver(conf.binMainPath).mkdirs()
		}
	}
}
