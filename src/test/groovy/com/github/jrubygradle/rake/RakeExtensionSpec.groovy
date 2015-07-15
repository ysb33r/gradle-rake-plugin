/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jrubygradle.rake

import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification


/**
 * Created by schalkc on 17/12/14.
 */
class RakeExtensionSpec extends Specification {

    static final File TESTFILES = new File( "${System.getProperty('TESTFILES') ?: 'src/test/resources/test-files'}")
    static final File TESTDIR = new File(System.getProperty('TESTROOT') ?: './build/test','RakeExtensionSpec').absoluteFile
//    static final File TESTREPO_LOCATION = new File("${System.getProperty('TESTREPO_LOCATION') ?: 'build/tmp/test/repo'}")

    Project project

    void setup() {

        if(TESTDIR.exists()) {
            TESTDIR.deleteDir()
        }
        TESTDIR.mkdirs()

        project = ProjectBuilder.builder().withProjectDir(TESTDIR).build()
        project.apply plugin : 'com.github.jruby-gradle.base'

    }

    def "Must be able to create an extension"() {
        given:
        def ext = new RakeExtension(project)

        expect:
        ext.project == project
    }

    def "Must be able to load a Rakefile"() {
        given:
        FileUtils.copyDirectory( new File(TESTFILES,'simple'),new File(TESTDIR,'simple') )
        def ext = new RakeExtension(project)
        def tasks = ext.loadfile("${TESTDIR}/simple/Rakefile")

        expect:
        tasks.size() == 4
        tasks.find { it.name == 'rakeDoc'}

    }
}