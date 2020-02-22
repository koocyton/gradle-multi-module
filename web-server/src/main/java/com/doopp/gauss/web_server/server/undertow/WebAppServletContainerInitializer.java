package com.doopp.gauss.web_server.server.undertow;

import com.doopp.gauss.core.server.configuration.ApplicationConfiguration;
import com.doopp.gauss.core.server.configuration.MyWebMvcConfigurer;
import com.doopp.gauss.web_server.server.filter.ApiFilter;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.util.EnumSet;
import java.util.Set;

public class WebAppServletContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {

        // set encode
        FilterRegistration.Dynamic encodingFilter = ctx.addFilter("encoding-filter", CharacterEncodingFilter.class);
        encodingFilter.setInitParameter("encoding", "UTF-8");
        encodingFilter.setInitParameter("forceEncoding", "true");
        encodingFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

        // root web application context
        AnnotationConfigWebApplicationContext rootWebAppContext = new AnnotationConfigWebApplicationContext();
        rootWebAppContext.register(ApplicationConfiguration.class, MyWebMvcConfigurer.class);
        ctx.addListener(new ContextLoaderListener(rootWebAppContext));

        // set spring mvc dispatcher
        DispatcherServlet dispatcherServlet = new DispatcherServlet(rootWebAppContext);
        ServletRegistration.Dynamic dispatcher = ctx.addServlet("mvc-dispatcher", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        // session filter
        FilterRegistration.Dynamic apiFilter = ctx.addFilter("apiFilter", ApiFilter.class);
        apiFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/api/*");
    }

}
