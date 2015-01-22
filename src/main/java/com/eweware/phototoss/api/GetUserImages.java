package com.eweware.phototoss.api;

import com.eweware.phototoss.core.PhotoRecord;
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
import static com.googlecode.objectify.ObjectifyService.ofy;
/**
 * Created by ultradad on 1/21/15.
 */
public class GetUserImages extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        long userId = Authenticator.CurrentUserId(session);

        if (userId != 0) {

            List < PhotoRecord > photos = ofy().load().type(PhotoRecord.class).filter("ownerid =", userId).list();

            // return them as JSON
            // write it to the user

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            Gson gson = new GsonBuilder().create();
            gson.toJson(photos, out);
            out.flush();
            out.close();

        } else {
            // fail
            response.setStatus(HttpStatusCodes.STATUS_CODE_FORBIDDEN);
        }


    }
}
