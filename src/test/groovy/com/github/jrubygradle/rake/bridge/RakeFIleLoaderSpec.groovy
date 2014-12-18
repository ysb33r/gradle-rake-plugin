package com.github.jrubygradle.rake.bridge

import com.github.jrubygradle.rake.RakeFile
import org.jruby.CompatVersion
import org.jruby.Ruby
import org.jruby.RubyInstanceConfig
import org.jruby.javasupport.JavaEmbedUtils
import spock.lang.Specification


/**
 * Created by schalkc on 17/12/14.
 */
class RakeFileLoaderSpec extends Specification {

    static final File TESTFILES = new File("${System.getProperty('TESTFILES') ?: 'src/test/resources/test-files'}")

    def "Load a Rake Wrapper"() {
        given:
            def rakefile = new File(TESTFILES,'simple/Rakefile')
            assert rakefile.exists()
            def config = new RubyInstanceConfig()
            config.compatVersion = CompatVersion.RUBY2_0
            config.compileMode = RubyInstanceConfig.CompileMode.OFF
            config.argv = ['-f',rakefile.absolutePath ] as String[]
            config.environment = [:]
            def rf = JavaEmbedUtils.initialize(Collections.EMPTY_LIST, config)

        when:
            GradleRakeWrapper grw = RakeFileLoader.load(rf)

        then:
            grw != null

        when:
            def tasks = grw.load_tasks()

        then:
            tasks.size() == 4
    }
}