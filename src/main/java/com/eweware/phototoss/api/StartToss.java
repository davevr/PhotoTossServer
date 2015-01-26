package com.eweware.phototoss.api;

import com.eweware.phototoss.core.*;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by ultradad on 1/21/15.
 */
public class StartToss extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
// ensure user is signed in
        HttpSession session = request.getSession();


        UserRecord curUser = Authenticator.CurrentUser(session);
        long imageId = Long.parseLong(request.getParameter("id"));

        if (curUser != null) {

            TossRecord data = new TossRecord();
            data.ownerid = curUser.id;
            data.ownername = curUser.username;
            data.imageUrl = servingUrl;
            data.caption = request.getParameter("caption");
            data.tags = new ArrayList<String>();
            data.tags.add(request.getParameter("tags"));
            data.created = new Date();

            // save to store
            ofy().save().entity(data).now();


            // write it to the user
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            Gson gson = new GsonBuilder().create();
            gson.toJson(data, out);
            out.flush();
            out.close();
        }
        else {
            response.setStatus(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
        }
    }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
