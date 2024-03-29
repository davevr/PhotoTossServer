package com.eweware.phototoss.api;

import com.eweware.phototoss.core.PhotoRecord;
import com.eweware.phototoss.core.UserRecord;
import com.google.appengine.repackaged.com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.googlecode.objectify.Key;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by ultradad on 2/24/15.
 */
public class ImageAuthorImage extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        final UserRecord curUser = Authenticator.CurrentUser(session);

        if (curUser != null) {
            String imageIdStr = request.getParameter("id");
            final long imageId = Long.parseLong(imageIdStr);
            final PhotoRecord foundImage = ofy().load().key(Key.create(PhotoRecord.class, imageId)).now();
            final UserRecord imageOwner =ofy().load().key(Key.create(UserRecord.class, foundImage.ownerid)).now();

            if (foundImage != null) {
                PrintWriter out = response.getWriter();
                out.write(imageOwner.imageurl);
                out.flush();
                out.close();
            } else {
                // return not found
                response.setStatus(HttpStatusCodes.STATUS_CODE_NOT_FOUND);
            }
        } else {
            // fail
            response.setStatus(HttpStatusCodes.STATUS_CODE_FORBIDDEN);
        }
    }
}
