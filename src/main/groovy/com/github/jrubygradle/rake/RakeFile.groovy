/*
 * Copyright 2010 the original author or authors.
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

import com.github.jrubygradle.rake.bridge.GradleRakeWrapper
import com.github.jrubygradle.rake.bridge.RakeFileLoader
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.gradle.api.Project
import org.gradle.api.Task
import org.jruby.CompatVersion
import org.jruby.Ruby
import org.jruby.RubyArray
import org.jruby.RubyInstanceConfig
import org.jruby.javasupport.JavaEmbedUtils
import org.jruby.runtime.builtin.IRubyObject
import com.github.jrubygradle.rake.bridge.RakeTask as RubyRakeTask

@CompileStatic
class RakeFile {
    String name = 'rake'
    String group = 'External Rake'

    @PackageScope final File filename
    @PackageScope final Project project
    @PackageScope final RubyInstanceConfig config = new RubyInstanceConfig()
    @PackageScope Ruby rubyRuntime

    private Map<String,RubyRakeTask> rawRakeTasks= [:]

    /** Constructs a Rakefile representation
     *
     * @param rakeFilename
     */
    RakeFile( final Project project, final File rakeFilename ) {
        filename = rakeFilename
        this.project = project
        config = new RubyInstanceConfig()

        // Set compatVersion from `jruby` instead of here
        config.compatVersion = CompatVersion.RUBY2_0
        config.compileMode = RubyInstanceConfig.CompileMode.OFF
        config.argv = ['-f',filename.absolutePath ] as String[]

        config.environment = [ HOME : project.gradle.gradleUserHomeDir.absolutePath ]
        rubyRuntime = JavaEmbedUtils.initialize(Collections.EMPTY_LIST, config)
    }

    /** Loads all of the tasks from the Rakefile and attach them to the Gradle project as {@code RakeTask} tasks.
     *
     * @param project Project to which tasks must be attached.
     * @return
     */
    List<RakeTask> loadTasks() {
        GradleRakeWrapper grw = RakeFileLoader.load(rubyRuntime)

        List<RubyRakeTask> rawTasks = grw.load_tasks ().collect {
            JavaEmbedUtils.rubyToJava(rubyRuntime, it as IRubyObject, RubyRakeTask.class ) as RubyRakeTask
        }

        rawRakeTasks.clear()

        rawTasks.collect { RubyRakeTask it ->
            rawRakeTasks[it.name()] = it
            String taskName = RakeFileLoader.rakeTaskNameToGradle(name,it.name())
            RakeTask task = project.tasks.create(taskName,RakeTask) as RakeTask
            configureTask(it,task)
            task
        }
    }

    /** Invoke the specific task on the Ruby Rake object
     *
     * @param rubyRakeTaskName Name of task to invoke
     */
    def invoke( final String rubyRakeTaskName ) {
        rawRakeTasks[rubyRakeTaskName].invoke(RubyArray.newArray(rubyRuntime, 0 ))
    }

    /** Configures a Gradle RakeTask from a Ruby Rake::Task
     *
     * @param rakeTask An instance of a Rake::Task reflected into a JVM interface
     * @param gradleTask The instance of the Gradle task to be configured
     * @return The Gradle task
     */
    @PackageScope
    @CompileDynamic
    RakeTask configureTask( RubyRakeTask rakeTask, RakeTask gradleTask ) {
        String prefix = name
        gradleTask.rakeTaskName = rakeTask.name()
        gradleTask.rakeFile = this
        gradleTask.configure {
            group = owner.group
        }
//        rakeTask.prerequisite_tasks().each { rakeDepends ->
//            gradleTask.dependsOn RakeFileLoader.rakeTaskNameToGradle(prefix,rakeDepends.name())
//        }
    }


}

