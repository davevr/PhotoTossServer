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
import java.util.List;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by davevr on 9/7/15.
 */
public class TossCatches extends HttpServlet {
    private static final Logger log = Logger.getLogger(TossCatches.class.getName());
    /*
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
    */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        long userId = Authenticator.CurrentUserId(session);

        if (userId != 0) {
            String tossIdStr = request.getParameter("tossid");
            final long tossId = Long.parseLong(tossIdStr);
            final TossRecord foundToss = ofy().load().key(Key.create(TossRecord.class, tossId)).now();

            if (foundToss != null) {
                // find the tooss
                List<PhotoRecord> photos = ofy().load().type(PhotoRecord.class).filter("tossid =", tossId).list();
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                Gson gson = new GsonBuilder().create();
                gson.toJson(photos, out);
                out.flush();
                out.close();
            } else {
                // return not found
                log.warning("toss " + tossIdStr + " not found");
                response.setStatus(HttpStatusCodes.STATUS_CODE_NOT_FOUND);
            }

        } else {
            // fail
            log.warning("user authentication failed");
            response.setStatus(HttpStatusCodes.STATUS_CODE_FORBIDDEN);
        }
    }
}
