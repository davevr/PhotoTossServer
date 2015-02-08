package com.eweware.phototoss.api;

import com.eweware.phototoss.core.UserRecord;
import com.google.appengine.repackaged.com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import com.eweware.phototoss.core.*;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by ultradad on 1/21/15.
 */
public class StartToss extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String imageIdStr = request.getParameter("image");
        String gameTypeStr = request.getParameter("game");
        String shareLatStr = request.getParameter("lat");
        String shareLongStr = request.getParameter("long");
        int gameType = 0;
        double longitude = 0.0;
        double latitude = 0.0;

        long userId = Authenticator.CurrentUserId(session);

        if (userId != 0) {
            try {
                // all parameters are required
                if ((imageIdStr == null) ||
                        (gameTypeStr == null) ||
                        (shareLatStr == null) ||
                        (shareLongStr == null)) {
                    response.setStatus(400);

                } else {
                    // good to go
                    final long imageId = Long.parseLong(imageIdStr);
                    gameType = Integer.parseInt(gameTypeStr);
                    latitude = Double.parseDouble(shareLatStr);
                    longitude = Double.parseDouble(shareLongStr);

                    TossRecord newToss = new TossRecord();

                    newToss.catchCount = 0L;
                    newToss.gameType = gameType;
                    newToss.imageId = imageId;
                    newToss.ownerId = userId;
                    newToss.shareLat = latitude;
                    newToss.shareLong = longitude;
                    newToss.shareTime = new Date();

                    ofy().save().entity(newToss).now();

                    ofy().transact(new VoidWork() {
                        public void vrun() {
                            PhotoRecord sharedImage = ofy().load().key(Key.create(PhotoRecord.class, imageId)).now();
                            sharedImage.tossCount++;
                            sharedImage.lastshared = new Date();
                            if (sharedImage.originid == null)
                                sharedImage.originid = sharedImage.id;
                            ofy().save().entity(sharedImage);
                        }
                    });

                    // we have signed in ok
                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    Gson gson = new GsonBuilder().create();
                    gson.toJson(newToss, out);
                    out.flush();
                    out.close();
                }

            }
            catch (Exception exp)
            {
                response.setStatus(500);
            }
        } else {
            // fail
            response.setStatus(HttpStatusCodes.STATUS_CODE_FORBIDDEN);
        }
    }


}
