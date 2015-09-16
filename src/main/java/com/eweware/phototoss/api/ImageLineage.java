package com.eweware.phototoss.api;

import com.eweware.phototoss.core.PhotoRecord;
import com.eweware.phototoss.core.TossRecord;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by davevr on 9/7/15.
 */
public class ImageLineage extends HttpServlet {
    private static final Logger log = Logger.getLogger(ImageLineage.class.getName());
    /*
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


    }
      */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        long userId = Authenticator.CurrentUserId(session);

        if (userId != 0) {
            String imageIdStr = request.getParameter("imageid");
            final long imageId = Long.parseLong(imageIdStr);
            PhotoRecord foundImage = ofy().load().key(Key.create(PhotoRecord.class, imageId)).now();

            if (foundImage != null) {
                // find the tooss
                List<PhotoRecord> photos = GetImageLineage(foundImage);
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                Gson gson = new GsonBuilder().create();
                gson.toJson(photos, out);
                out.flush();
                out.close();
            } else {
                // return not found
                log.warning("image " + imageIdStr + " not found");
                response.setStatus(HttpStatusCodes.STATUS_CODE_NOT_FOUND);
            }

        } else {
            // fail
            log.warning("user authentication failed");
            response.setStatus(HttpStatusCodes.STATUS_CODE_FORBIDDEN);
        }
    }

    public static List<PhotoRecord> GetImageLineage(PhotoRecord foundImage) {
        List<PhotoRecord> photos = new ArrayList<PhotoRecord>();

        while ((foundImage.parentid != null) && (foundImage.parentid > 0L))
        {
            foundImage = getParent(foundImage);
            if (foundImage == null)
                break;
            else
                photos.add(foundImage);
        }

        return photos;

    }

    protected static PhotoRecord getParent(PhotoRecord thePhoto) {
        final long parentId = thePhoto.parentid;
        final PhotoRecord theParent = ofy().load().key(Key.create(PhotoRecord.class, parentId)).now();
        return theParent;
    }
}
