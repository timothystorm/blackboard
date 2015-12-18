package storm.crusade.web;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class StartupContextListener implements ServletContextListener {
    private Timer DAILY_TIMER;

    public StartupContextListener() {
        DAILY_TIMER = new Timer(true);
        DAILY_TIMER.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // what do you want to do every 24 hours?
                App.resetDeveloperKey();
            }
        }, TimeUnit.HOURS.toMillis(24), TimeUnit.HOURS.toMillis(24));
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        try {
            DAILY_TIMER.cancel();
        } finally {
            DAILY_TIMER = null;
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {}
}
