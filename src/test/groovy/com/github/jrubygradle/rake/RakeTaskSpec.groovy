package com.github.jrubygradle.rake

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.LogLevel
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification


/**
 * Created by schalkc on 18/12/14.
 */
class RakeTaskSpec extends Specification {

    static final File TESTFILES = new File("${System.getProperty('TESTFILES') ?: 'src/test/resources/test-files'}")

    Project project
    RakeFile rakeFile
    ByteArrayOutputStream systemOut = new ByteArrayOutputStream()


    void setup() {
        project = ProjectBuilder.builder().withProjectDir(new File('./build/test/RakeExtensionSpec').absoluteFile).build()
        project.apply plugin : 'com.github.jruby-gradle.base'

        rakeFile = new RakeFile(project,new File(TESTFILES,'simple/Rakefile'),output: new PrintStream(systemOut) )
        rakeFile.loadTasks()
    }



    def "Must be able to execute a task"() {

        when:
        project.rakeDoc.execute()

        then:
        systemOut.toString().contains('Someone has to write documentation')
    }

}