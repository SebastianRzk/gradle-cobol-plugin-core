package de.sebastianruziczka.buildcycle.demo

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger

import de.sebastianruziczka.CobolExtension

class HelloWorldCopyTask extends DefaultTask{

	@Input
	public String helloWorldFilePath
	
	public String getHelloWorldFilePath() {
		return this.helloWorldFilePath
	}
	
	@OutputFile
	public File getOutputFile() {
		return new File(getHelloWorldFilePath());
	}
	
	@TaskAction
	public void run() {
		println("Copy src file into path")
		this.copy(CobolDemoConstants.COBOL_SRC_FILE_IN_JAR_PATH, getHelloWorldFilePath(), logger)
	}
	
	private void copy(String source, String destination, Logger logger) {
		logger.info('COPY: '+ source + '>>' + destination)
		URLClassLoader urlClassLoader = (URLClassLoader) Thread.currentThread().getContextClassLoader()
		URL sourceFile = urlClassLoader.findResource(source)
		InputStream is = sourceFile.openStream()

		File targetFile = new File(destination)

		if(!targetFile.getParentFile().exists()) {
			targetFile.getParentFile().mkdirs()
		}

		OutputStream outStream = new FileOutputStream(targetFile)

		byte[] buffer = new byte[1024]
		int length
		while ((length = is.read(buffer)) > 0) {
			outStream.write(buffer, 0, length)
		}

		outStream.close()
		is.close()
	}
}
