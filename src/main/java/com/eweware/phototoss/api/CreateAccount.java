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
import java.util.Date;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by ultradad on 1/21/15.
 */
public class CreateAccount extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            UserRecord newUser = Authenticator.CreateAndAuthenticateUser(session, username, password);

            if (newUser != null) {
                // we have signed in ok
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
        catch (Exception exp)
        {
            response.setStatus(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
        }

    }
}
