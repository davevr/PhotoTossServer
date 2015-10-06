package com.eweware.phototoss;

import com.eweware.phototoss.core.*;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Created by ultradad on 1/21/15.
 */
public class PhotoTossServerListener implements ServletContextListener {



    public void contextInitialized(ServletContextEvent event) {
        // register our classes
        ObjectifyService.register(PhotoRecord.class);
        ObjectifyService.register(UserRecord.class);
        ObjectifyService.register(TossRecord.class);
        ObjectifyService.register(CatchRecord.class);
        ObjectifyService.register(ImageDeleteRecord.class);

    }



    public void contextDestroyed(ServletContextEvent event) {
        // App Engine does not currently invoke this method.
    }
}
