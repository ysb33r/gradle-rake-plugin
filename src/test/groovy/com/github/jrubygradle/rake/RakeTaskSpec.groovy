package com.github.jrubygradle.rake

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification


/**
 * Created by schalkc on 18/12/14.
 */
class RakeTaskSpec extends Specification {

    static final File TESTFILES = new File("${System.getProperty('TESTFILES') ?: 'src/test/resources/test-files'}")
//    static final File TESTREPO_LOCATION = new File("${System.getProperty('TESTREPO_LOCATION') ?: 'build/tmp/test/repo'}")

    Project project
    RakeFile rakeFile

    void setup() {
        project = ProjectBuilder.builder().withProjectDir(new File('.').absoluteFile).build()

        rakeFile = new RakeFile(project,new File(TESTFILES,'simple/Rakefile'))
        rakeFile.loadTasks()
    }

    def "Must be able to execute a task"() {
        when:
            project.rakeDoc.execute()

        then:
            false
    }

}