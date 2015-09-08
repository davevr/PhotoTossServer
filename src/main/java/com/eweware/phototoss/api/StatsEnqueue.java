package com.eweware.phototoss.api;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Dave on 1/29/2015.
 */
public class StatsEnqueue extends HttpServlet {
    private static final Logger log = Logger.getLogger(StatsEnqueue.class.getName());
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Add the task to the default queue.
        Queue queue = QueueFactory.getDefaultQueue();
        String imageIdStr = request.getParameter("imageid");
        String taskIdStr = request.getParameter("taskid");
        TaskOptions options = TaskOptions.Builder.withUrl("/api/stats/process");
        options.param("imageid", imageIdStr);
        options.param("taskid", taskIdStr);


        queue.add(options);

        response.sendRedirect("/");
    }

}
