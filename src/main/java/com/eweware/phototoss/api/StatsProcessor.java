package com.eweware.phototoss.api;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Dave on 1/28/2015.
 */
public class StatsProcessor extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String imageIdStr = request.getParameter("imageid");
        String tossIdStr = request.getParameter("tossid");
        // Do something with key.
        Queue   taskQueue = QueueFactory.getDefaultQueue();

        try {
            List<TaskHandle> taskList = taskQueue.leaseTasks(2, TimeUnit.SECONDS, 1000 );
            Map<String, Integer> imageIncMap = new HashMap<String, Integer>();
            Map<String, Integer> tossIncMap = new HashMap<String, Integer>();

            for (TaskHandle curTask : taskList) {
                List<Map.Entry<String, String>> params = curTask.extractParams();
                for (Map.Entry<String, String> curParam : params) {
                    if (curParam.getKey().equals("imageid")) {
                        String curId = curParam.getValue();
                        if (imageIncMap.containsKey(curId))
                            imageIncMap.put(curId, imageIncMap.get(curId) + 1);
                        else
                            imageIncMap.put(curId, 1);
                    } else if (curParam.getKey().equals("tossid")) {
                        String curId = curParam.getValue();
                        if (imageIncMap.containsKey(curId))
                            imageIncMap.put(curId, imageIncMap.get(curId) + 1);
                        else
                            imageIncMap.put(curId, 1);
                    }
                }
            }
        }
        catch (Exception exp)
        {
            // to do - figure out what went wrong
            System.out.print(exp.getMessage());
        }

    }
}
