package net.timeboxing.rest.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

public class RestModule extends AbstractModule {

    private static final String INTERNAL_PACKAGE = "net.timeboxing";
    private static final Logger LOG = LoggerFactory.getLogger(RestModule.class);
    private final String packageName;

    public RestModule(String packageName) {
        this.packageName = packageName;
    }

    @Override
    protected void configure() {
        super.configure();

        if (!packageName.startsWith(INTERNAL_PACKAGE)) {
            Reflections internal = new Reflections(INTERNAL_PACKAGE);
            for (Class<?> provider: internal.getTypesAnnotatedWith(Provider.class)) {
                LOG.debug("Binding internal provider {}", provider.getName());
                bind(provider);
            }
        }

        Reflections reflections = new Reflections(packageName);

        for (Class<?> path: reflections.getTypesAnnotatedWith(Path.class)) {
            LOG.debug("Binding path {}", path.getName());
            bind(path).in(Scopes.SINGLETON);
        }

        for (Class<?> provider: reflections.getTypesAnnotatedWith(Provider.class)) {
            LOG.debug("Binding provider {}", provider.getName());
            bind(provider);
        }
    }
}
