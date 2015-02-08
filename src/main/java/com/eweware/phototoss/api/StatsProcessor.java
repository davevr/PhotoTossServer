package com.eweware.phototoss.api;

import com.eweware.phototoss.core.PhotoRecord;
import com.eweware.phototoss.core.TossRecord;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by Dave on 1/28/2015.
 */
public class StatsProcessor extends HttpServlet {

    // questionable procedure here...
    Map<String, Integer> imageIncMap = new HashMap<String, Integer>();
    Map<String, Integer> tossIncMap = new HashMap<String, Integer>();
    Date lastWriteTIme = new Date();
    static int MAX_TIME_BETWEEN_FLUSHES = 120;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String imageIdStr = request.getParameter("imageid");

        if (imageIdStr != null) {
            if (imageIncMap.containsKey(imageIdStr))
                imageIncMap.put(imageIdStr, imageIncMap.get(imageIdStr) + 1);
            else
                imageIncMap.put(imageIdStr, 1);
        }

        String tossIdStr = request.getParameter("tossid");
        if (tossIdStr != null) {
            if (tossIncMap.containsKey(tossIdStr))
                tossIncMap.put(tossIdStr, tossIncMap.get(tossIdStr) + 1);
            else
                tossIncMap.put(tossIdStr, 1);
        }

        // write out hte map - maybe
        Date curTime = new Date();
        long diffInMillies = curTime.getTime() - lastWriteTIme.getTime();
        long elapsedSec = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        if (elapsedSec > MAX_TIME_BETWEEN_FLUSHES) {
            // flush the queue
            for (Map.Entry<String, Integer> entry : imageIncMap.entrySet()) {
                final long imageId = Long.parseLong(entry.getKey());
                final int newShares = entry.getValue();

                ofy().transact(new VoidWork() {
                    public void vrun() {
                        PhotoRecord sharedImage = ofy().load().key(Key.create(PhotoRecord.class, imageId)).now();
                        if (sharedImage.totalshares == null)
                            sharedImage.totalshares = (long)newShares;
                        else
                            sharedImage.totalshares += newShares;
                        ofy().save().entity(sharedImage);
                    }
                });
            }

            for (Map.Entry<String, Integer> entry : tossIncMap.entrySet()) {
                final long tossId = Long.parseLong(entry.getKey());
                final int newShares = entry.getValue();

                ofy().transact(new VoidWork() {
                    public void vrun() {
                        TossRecord theToss = ofy().load().key(Key.create(TossRecord.class, tossId)).now();
                        if (theToss.catchCount == null)
                            theToss.catchCount = (long)newShares;
                        else
                            theToss.catchCount += newShares;
                        ofy().save().entity(theToss);
                    }
                });
            }


        }


    }
}
