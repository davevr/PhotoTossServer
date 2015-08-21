package com.eweware.phototoss.api;

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
 * Created by ultradad on 8/21/15.
 */
public class FBLogin extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String fbId = request.getParameter("id");
        String authToken = request.getParameter("token");

        if (Authenticator.AuthenticateFBUser(session, fbId, authToken)) {
            // yeah!
            long userId = Authenticator.CurrentUserId(session);
            UserRecord newUser = ofy().load().key(Key.create(UserRecord.class, userId)).now();

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            Gson gson = new GsonBuilder().create();
            gson.toJson(newUser, out);
            out.flush();
            out.close();

        } else {
            // no luck
            response.setStatus(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
        }
    }

}
