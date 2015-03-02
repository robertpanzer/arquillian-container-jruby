package org.arquillian.jruby.resources;

import org.arquillian.jruby.embedded.JRubyScriptExecution;
import org.arquillian.jruby.util.AnnotationUtils;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;
import org.jruby.Ruby;

import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.util.Arrays;

public class RubyResourceProvider extends AbstractResourceProvider {

    @Override
    public boolean canProvide(Class<?> aClass) {
        return aClass == Ruby.class;
    }

    @Override
    public Object lookup(ArquillianResource arquillianResource, Annotation... annotations) {
        Ruby ruby;

        try {
            if (AnnotationUtils.filterAnnotation(annotations, ResourceProvider.MethodInjection.class) != null) {
                ruby = getOrCreateTestMethodScopedScriptingContainer().getProvider().getRuntime();
                rubyScriptExecutionEvent.fire(new JRubyScriptExecution());
            } else if (AnnotationUtils.filterAnnotation(annotations, ResourceProvider.ClassInjection.class) != null) {
                ruby = getOrCreateClassScopedScriptingContainer().getProvider().getRuntime();
            } else {
                throw new IllegalArgumentException("Don't know how to resolve Ruby instance with qualifiers " + Arrays.asList(annotations));
            }
            return ruby;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
