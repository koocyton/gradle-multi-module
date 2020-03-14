package com.doopp.gauss.core.server;

import com.doopp.gauss.core.server.configuration.ApplicationConfiguration;
import com.doopp.gauss.core.server.configuration.MyWebMvcConfigurer;
import com.doopp.gauss.core.server.util.ApplicationProperties;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.FileResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainerInitializerInfo;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static io.undertow.Handlers.path;

public class UndertowServer implements InitializingBean, DisposableBean {

    private String webAppName;

    private Resource webAppRoot;

    private String host = "127.0.0.1";

    private int port = 8088;

    // private String sslPort;

    // private Resource jksFile;

    // private String jksPassword;

    // private String jksSecret;

    private Undertow server;

    private DeploymentManager manager;

    @Override
    public void afterPropertiesSet() throws IOException, ServletException {

        manager = Servlets.defaultContainer().addDeployment(this.deploymentInfo());
        manager.deploy();

        // MySocketConnectionCallback connectionCallback = ApplicationContextUtil.getBean(MySocketConnectionCallback.class);
        HttpHandler httpHandler = path()
                .addPrefixPath("/", manager.start());
                // .addPrefixPath("/socket", websocket(connectionCallback));

        //SSLContext sslContext = SSLContext.getInstance("TLS");
        //sslContext.init(getKeyManagers(), null, null);

        server = Undertow.builder()
                // .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                .addHttpListener(port, host)
                // .addHttpsListener(sslPort, host, sslContext)
                .setHandler(httpHandler)
                .build();
        server.start();

        // System.out.print("\n >>> Undertow web server started at http://" + host + ":" + port + " and https://" + host + ":" + sslPort + "\n\n");
        String serverHost = host.equals("127.0.0.1") ? "localhost" : host;
        String serverPort = port==80 ? "" : ":" + port;
        System.out.printf("\n >>> Undertow web server started at http://%s%s\n\n", serverHost, serverPort);
    }

    @Override
    public void destroy() throws Exception {
        server.stop();
        manager.stop();
        manager.undeploy();
        System.console().printf("Undertow web server on port " + port + " stopped");
    }

    // private KeyManager[] getKeyManagers() {
    //     try {
    //         KeyStore keyStore = KeyStore.getInstance("JKS");
    //         keyStore.load(jksFile.getInputStream(), this.jksPassword.toCharArray());
    //         KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
    //         keyManagerFactory.init(keyStore, this.jksSecret.toCharArray());
    //         return keyManagerFactory.getKeyManagers();
    //     }
    //     catch (Exception e) {
    //         throw new RuntimeException(e);
    //     }
    // }

    public void setApplicationProperties(ApplicationProperties properties) {
        this.webAppName = properties.s("server.webAppName");
        this.webAppRoot = properties.r("server.webAppRoot");
        this.host = properties.s("server.host");
        // this.port = properties.i("server.port");
        // this.sslPort = properties.i("server.sslPort");
        // this.jksFile = properties.r("server.jks.file");
        // this.jksPassword = properties.s("server.jks.password");
        // this.jksSecret = properties.s("server.jks.secret");
    }

    public void setPort(int port) {
        this.port = port;
    }

    private DeploymentInfo deploymentInfo () throws IOException {
        // System.out.println(sslPort);
        // web servlet
        // InstanceFactory<? extends ServletContainerInitializer> instanceFactory = new ImmediateInstanceFactory<>(servletContainerInitializer);

        Set<Class<?>> hashSet = new HashSet<>();
        hashSet.add(ApplicationConfiguration.class);
        ServletContainerInitializerInfo sciInfo = new ServletContainerInitializerInfo(WebAppServletContainerInitializer.class, hashSet);
        return Servlets.deployment()
                .addServletContainerInitializers(sciInfo)
                .setResourceManager(new FileResourceManager(webAppRoot.getFile(), 0))
                .setClassLoader(UndertowServer.class.getClassLoader())
                .setContextPath("")
                .setDeploymentName(webAppName);
    }

    private static class WebAppServletContainerInitializer implements ServletContainerInitializer {

        @Override
        public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {

            // set encode
            FilterRegistration.Dynamic encodingFilter = servletContext.addFilter("encoding-filter", CharacterEncodingFilter.class);
            encodingFilter.setInitParameter("encoding", "UTF-8");
            encodingFilter.setInitParameter("forceEncoding", "true");
            encodingFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");

            // root web application context
            AnnotationConfigWebApplicationContext rootWebAppContext = new AnnotationConfigWebApplicationContext();
            rootWebAppContext.register(ApplicationConfiguration.class, MyWebMvcConfigurer.class);
            servletContext.addListener(new ContextLoaderListener(rootWebAppContext));

            // set spring mvc dispatcher
            DispatcherServlet dispatcherServlet = new DispatcherServlet(rootWebAppContext);
            ServletRegistration.Dynamic dispatcher = servletContext.addServlet("mvc-dispatcher", dispatcherServlet);
            dispatcher.setLoadOnStartup(1);
            dispatcher.addMapping("/");

            // session filter
            c.forEach(aClass -> {
                FilterRegistration.Dynamic apiFilter = servletContext.addFilter("apiFilter", aClass);
                apiFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/api/*");
            });
        }
    }
}
