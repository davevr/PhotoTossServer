package com.eweware.phototoss.api;

import com.eweware.phototoss.core.ImageStatsRecord;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by ultradad on 10/5/15.
 */
public class ImageStatsServlet extends HttpServlet {

    private static final Logger log = Logger.getLogger(ImageStatsServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        final UserRecord curUser = Authenticator.CurrentUser(session);

        if (curUser != null) {
            String imageIdStr = request.getParameter("id");
            final long imageId = Long.parseLong(imageIdStr);

            ImageStatsRecord newRec = ImageStatsRecord.CreateFromPhoto(imageId);

            if (newRec != null) {
                response.setContentType("application/json");

                PrintWriter out = response.getWriter();
                Gson gson = new GsonBuilder().create();
                gson.toJson(newRec, out);
                out.flush();
                out.close();
            } else {
                // return not found
                log.log(Level.SEVERE, "Requested stats of non-existant image %s", imageIdStr);
                response.setStatus(HttpStatusCodes.STATUS_CODE_NOT_FOUND);
            }
        } else {
            // fail
            log.log(Level.INFO, "Attempt to access photo stats when logged out");
            response.setStatus(HttpStatusCodes.STATUS_CODE_FORBIDDEN);
        }
    }
}
