package com.eweware.phototoss.api;

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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by ultradad on 1/21/15.
 */
public class GetTossStatus extends HttpServlet {
    private static final Logger log = Logger.getLogger(GetTossStatus.class.getName());


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        final UserRecord curUser = Authenticator.CurrentUser(session);

        if (curUser != null) {
            String tossStr = request.getParameter("toss");
            if (tossStr != null) {
                final long tossId = Long.parseLong(tossStr);

                TossRecord tossRecStatic = ofy().load().key(Key.create(TossRecord.class, (long) tossId)).now();
                if (tossRecStatic != null) {
                    final Date currentTime = new Date();
                    long diffInMillies = currentTime.getTime() - tossRecStatic.shareTime.getTime();
                    long elapsedSec = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                    if (elapsedSec > CatchToss.MAX_TOSS_TIME_IN_SECONDS) {
                        log.log(Level.INFO, "Toss has expired");
                        response.setStatus(400);
                        return;
                    }

                    if (tossRecStatic.ownerId != curUser.id) {
                        log.log(Level.INFO, "Only the toss owner can get the status");
                        response.setStatus(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
                        return;
                    }

                    // return the catches so far...
                    List<PhotoRecord> photos = ofy().load().type(PhotoRecord.class).filter("tossid =", tossId).list();
                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    Gson gson = new GsonBuilder().create();
                    gson.toJson(photos, out);
                    out.flush();
                    out.close();
                    response.setStatus(HttpStatusCodes.STATUS_CODE_OK);

                } else {
                    log.log(Level.WARNING, "Invalid Toss ID");
                    response.setStatus(HttpStatusCodes.STATUS_CODE_NOT_FOUND);
                    return;
                }

            } else {
                log.log(Level.WARNING, "User did not specify a toss id");
                response.setStatus(HttpStatusCodes.STATUS_CODE_NOT_FOUND);
                return;
            }

        }
        else {
            log.log(Level.SEVERE, "unauthorized user calling tossstatus");
            response.setStatus(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
        }

    }
}
