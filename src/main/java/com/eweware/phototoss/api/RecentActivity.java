package com.eweware.phototoss.api;

import com.eweware.phototoss.core.PhotoRecord;
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
 * Created by ultradad on 9/16/15.
 */
public class RecentActivity extends HttpServlet {
    private static final Logger log = Logger.getLogger(RecentActivity.class.getName());

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // no post for this
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        List<PhotoRecord> photoList = ofy().load().type(PhotoRecord.class).order("-created").limit(10).list();
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().create();
        gson.toJson(photoList, out);
        out.flush();
        out.close();

    }
}
