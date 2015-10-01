package com.eweware.phototoss.api;

import com.eweware.phototoss.core.ImageDeleteRecord;
import com.eweware.phototoss.core.PhotoRecord;
import com.eweware.phototoss.core.TossRecord;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by ultradad on 1/21/15.
 */
public class GetImage extends HttpServlet {
    private static final Logger log = Logger.getLogger(GetImage.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        final UserRecord curUser = Authenticator.CurrentUser(session);

        if (curUser != null) {
            String imageIdStr = request.getParameter("id");
            final long imageId = Long.parseLong(imageIdStr);
            final PhotoRecord foundImage = ofy().load().key(Key.create(PhotoRecord.class, imageId)).now();

            if (foundImage != null) {
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                Gson gson = new GsonBuilder().create();
                gson.toJson(foundImage, out);
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

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        final UserRecord curUser = Authenticator.CurrentUser(session);

        if (curUser != null) {
            String imageIdStr = request.getParameter("id");
            final long imageId = Long.parseLong(imageIdStr);
            Key<PhotoRecord>   theKey = Key.create(PhotoRecord.class, imageId);
            final PhotoRecord foundImage = ofy().load().key(theKey).now();

            if (foundImage != null) {
                if (foundImage.ownerid == curUser.id) {
                    String tossesAlsoStr = request.getParameter("all");
                    DeleteImage(foundImage);
                    if (tossesAlsoStr != null)
                        DeleteChildImages(foundImage);

                    response.setContentType("text/html");
                    PrintWriter out = response.getWriter();
                    out.println("ok");
                    out.flush();
                    out.close();
                } else {
                    log.log(Level.WARNING, "User not authorized to delete image");
                    response.setStatus(HttpStatusCodes.STATUS_CODE_FORBIDDEN);
                    response.setContentType("text/html");
                    PrintWriter out = response.getWriter();
                    out.println("user not authorized to delete image");
                    out.flush();
                    out.close();
                }
            } else {
                // return not found

                response.setStatus(HttpStatusCodes.STATUS_CODE_NOT_FOUND);

            }
        } else {
            // fail
            response.setStatus(HttpStatusCodes.STATUS_CODE_FORBIDDEN);
        }
    }

    protected void DeleteChildImages(PhotoRecord theParent) {
        // delete children
        List<TossRecord> tosses = ofy().load().type(TossRecord.class).filter("imageId =", theParent.id).list();

        if (tosses != null) {
            for (TossRecord curToss : tosses) {
                List<PhotoRecord> photos = ofy().load().type(PhotoRecord.class).filter("tossid =", curToss.id).list();

                if (photos != null) {
                    for (PhotoRecord curImage : photos) {
                        DeleteImage(curImage);
                        DeleteChildImages(curImage);
                    }
                }
            }
        }
    }

    protected void DeleteImage(PhotoRecord theImage) {
        theImage.deleted = true;
        ofy().save().entity(theImage);
    }
}
