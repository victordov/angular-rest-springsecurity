/**
 *
 */
package net.dontdrinkandroot.example.angularrestspringsecurity.configuration;

/**
 * @author Hisham
 *
 */

import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class ApplicationInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext)
            throws ServletException {
        // Load application context
        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(AppConfig.class);
        rootContext.setDisplayName("angular-rest-springsecurity");

        // Add context loader listener
        servletContext.addListener(new ContextLoaderListener(rootContext));
        ServletRegistration.Dynamic dispatcher =
                servletContext.addServlet("RestService", new SpringServlet());
        dispatcher.setInitParameter("com.sun.jersey.config.property.packages", "net.dontdrinkandroot.example.angularrestspringsecurity.rest");
        dispatcher.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/rest/*");

        // Register Spring security filter
        FilterRegistration.Dynamic springSecurityFilterChain =
                servletContext.addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
        springSecurityFilterChain.addMappingForUrlPatterns(null, false, "/*");

    }
}
