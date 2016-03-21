package com.rpi.ha.jettysrv;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.rpi.ha.Conf;
import com.rpi.webui.servlet.ApiServlet;
import com.rpi.webui.servlet.ControlServlet;
import com.rpi.webui.servlet.FuncServlet;
import com.rpi.webui.servlet.InfoServlet;
import com.rpi.webui.servlet.LoginServlet;
import com.rpi.webui.servlet.NotifyServlet;
import com.rpi.webui.servlet.RemoServlet;

public class JettyServ {
	
	public static Server server;

	public static void start() throws Exception{
		server = new Server(Conf.api_port);
        
        ServletContextHandler servcontext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servcontext.setContextPath("/api");
        
        servcontext.addServlet(new ServletHolder(new ApiServlet()), "/*");
        servcontext.addServlet(new ServletHolder(new FuncServlet()), "/func");
        servcontext.addServlet(new ServletHolder(new ControlServlet()), "/control");
        servcontext.addServlet(new ServletHolder(new InfoServlet()), "/info");
        servcontext.addServlet(new ServletHolder(new LoginServlet()), "/login");
        servcontext.addServlet(new ServletHolder(new NotifyServlet()), "/notify");
        servcontext.addServlet(new ServletHolder(new RemoServlet()), "/remo");
        server.setHandler(servcontext);
        
        server.start();
        server.join();

        
	}
	
	public static void main(String[] args) throws Exception{
		start();
	}
}
