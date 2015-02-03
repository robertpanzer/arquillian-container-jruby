package foo.test;

import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class BasicTest {

    @ArquillianResource
    private Ruby rubyInstance;

    @ArquillianResource
    private Deployer deployer;

    @Deployment
    public static JavaArchive deploy() throws Exception {
        return ShrinkWrap.create(JavaArchive.class)
                .addAsResource(
                        Maven.configureResolver()
                                .withRemoteRepo("rubygems", "http://rubygems-proxy.torquebox.org/releases", "default")
                                .resolve("rubygems:asciidoctor:gem:1.5.2")
                                .withTransitivity().asFile()[0]);
    }

    @Test
    public void shouldRenderAsciidocDocument() throws Exception {
        IRubyObject result = rubyInstance.evalScriptlet(
                "require 'asciidoctor'\n" +
                "Asciidoctor.convert '*This* is Asciidoctor.'");
        assertThat(
                (String)JavaEmbedUtils.rubyToJava(rubyInstance, result, String.class),
                containsString("<strong>This</strong> is Asciidoctor."));
    }
}