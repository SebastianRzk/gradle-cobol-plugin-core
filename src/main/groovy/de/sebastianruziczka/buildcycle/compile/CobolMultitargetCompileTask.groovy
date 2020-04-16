package de.sebastianruziczka.buildcycle.compile

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import de.sebastianruziczka.CobolExtension

class CobolMultitargetCompileTask extends DefaultTask{

	@Input
	List<String> allTargets = new ArrayList<>()

	private CobolExtension configuration;
	
	public CobolMultitargetCompileTask(CobolExtension configuration) {
		this.configuration = configuration
	}

	@TaskAction
	public void run() {
		this.allTargets.each{
			new CobolCompileExecutableImpl().compile(project, this.configuration, it)
		}
	}
}
