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

import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor
import org.gradle.api.Project

/**
 * Created by schalkc on 17/12/14.
 */
@CompileStatic
@TupleConstructor
class RakeExtension {

    /** The project this extension is attached to.
     *
     */
    Project project


    /** Loads the default Rakefile which will be according to the Ruby SourceSet specified
     * in the base plugin.
     *
     * @return List of tasks that were added
     */
    List<RakeTask> loadfile() {
        loadfile 'src/ruby/Rakefile', {}
    }

    /** Loads a specific Rakefile into Gradle task space
     *
     * @param rakeFile Path to Rakefile
     * @return List of tasks that were added
     */
    List<RakeTask> loadfile( final Object rakeFile ) {
        loadfile rakeFile, {}
    }

    /** Loads a specific Rakefile into Gradle task space, with additional
     * configuration via a closure
     *
     * @param rakeFile
     * @param config
     * @return List of tasks that were added
     */
    List<RakeTask> loadfile( final Object rakeFile, @DelegatesTo(RakeFile) Closure config) {

        RakeFile rf = new RakeFile( project, project.file(rakeFile) )

        config.delegate = rf
        config.call()

        rf.loadTasks()
    }

}
