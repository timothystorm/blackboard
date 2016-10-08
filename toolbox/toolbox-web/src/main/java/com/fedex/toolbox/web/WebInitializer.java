package com.fedex.toolbox.web;

import javax.annotation.Priority;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Initializes the web application; replacing web.xml
 *
 * @author Timothy Storm
 */
@Priority(value = 0)
public class WebInitializer implements WebApplicationInitializer {

  @Override
  public void onStartup(ServletContext cntx) throws ServletException {
    // jersey tries to start up before spring - this stops that
    cntx.setInitParameter("contextConfigLocation", "<NONE>");

    // create root context
    AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
    root.register(SpringConfig.class);

    // manage spring life cycle of the root application
    cntx.addListener(new ContextLoaderListener(root));

    // register jersey servlet
    ServletRegistration.Dynamic jersey = cntx.addServlet("JerseyServlet", new ServletContainer());
    jersey.setInitParameter("javax.ws.rs.Application", JerseyConfig.class.getName());
    jersey.setLoadOnStartup(1);
    jersey.addMapping("/*");
  }
}
