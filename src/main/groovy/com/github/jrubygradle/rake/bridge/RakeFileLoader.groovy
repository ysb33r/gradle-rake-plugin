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
package com.github.jrubygradle.rake.bridge

import org.jruby.Ruby
import org.jruby.javasupport.JavaEmbedUtils
import org.jruby.runtime.builtin.IRubyObject

/**
 * Created by schalkc on 17/12/14.
 */
class RakeFileLoader {
    /** Runs some low level Ruby to load a Rakefile
     *
     * @param rakeFile Rakefile to load
     * @return An instance of a {@code GradleRakeWrapper}
     */
    static GradleRakeWrapper load( Ruby rubyRuntime ) {
        assert rubyRuntime != null
        def evaler = JavaEmbedUtils.newRuntimeAdapter()
        def rakeEval = evaler.eval( rubyRuntime,'''
require 'rubygems'
require 'rake'

class GradleRakeWrapper
  def raw_load_tasks
    Rake.application.tap do |application|
      application.init
      application.load_rakefile
    end
  end

  def load_tasks
    raw_load_tasks().tasks
  end
end

GradleRakeWrapper.new
''')

        JavaEmbedUtils.rubyToJava(rubyRuntime, rakeEval as IRubyObject, GradleRakeWrapper.class ) as GradleRakeWrapper
    }

    /** Translates a Rake task name suitable to one usable in Gradle.
     * It applies the {@code prefix} and concatenates a capitalised version of {@code name}.
     * Any incompatible characters in the name will be replaced by appropriate substitution characters.
     *
     * @param prefix A prefix to be used for the task name
     * @param name Rake task name
     * @return Gradle task name
     */
    static String rakeTaskNameToGradle( final String prefix,final String name ) {
        prefix + name.replaceAll(/:/,'-').capitalize()
    }
}
