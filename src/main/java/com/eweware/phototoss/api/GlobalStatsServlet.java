package com.eweware.phototoss.api;

import com.eweware.phototoss.core.ImageStatsRecord;
import com.eweware.phototoss.core.PhotoRecord;
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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by ultradad on 10/5/15.
 */
public class GlobalStatsServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(GlobalStatsServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        final UserRecord curUser = Authenticator.CurrentUser(session);

        if (curUser != null) {
            String forceReindex = request.getParameter("force");

            if (forceReindex != null) {
                ForceReindexAll();
            }
            List<PhotoRecord> topImages = ofy().load().type(PhotoRecord.class).order("-totalshares").limit(10).list();

            response.setContentType("application/json");

            PrintWriter out = response.getWriter();
            Gson gson = new GsonBuilder().create();
            gson.toJson(topImages, out);
            out.flush();
            out.close();
        } else {
            // fail
            log.log(Level.INFO, "Attempt to access global stats when logged out");
            response.setStatus(HttpStatusCodes.STATUS_CODE_FORBIDDEN);
        }
    }

    private void ForceReindexAll() {
        List<PhotoRecord> allImages = ofy().load().type(PhotoRecord.class).list();
        Long totalCount = 1L;
        log.log(Level.SEVERE, String.format("Indexing %d images", allImages.size()));
        for (PhotoRecord curImage : allImages) {
            totalCount = 1L;
            totalCount += ofy().load().type(PhotoRecord.class).filter("originid =", curImage.id).count();
            curImage.totalshares = totalCount;
            ofy().save().entity(curImage).now();
        }
    }
}
