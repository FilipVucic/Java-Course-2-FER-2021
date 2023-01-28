package hr.fer.oprpp1.listeners;

import hr.fer.oprpp1.Keys;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * {@link ServletContextListener} listening for the app start and writing that time.
 *
 * @author Filip Vucic
 */
@WebListener
public class AppStartListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        servletContextEvent.getServletContext().setAttribute(Keys.KEY_START, System.currentTimeMillis());
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
