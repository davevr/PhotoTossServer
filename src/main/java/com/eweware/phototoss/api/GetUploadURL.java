package com.eweware.phototoss.api;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.repackaged.com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Dave on 1/19/2015.
 */
public class GetUploadURL extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (Authenticator.getInstance().UserIsLoggedIn(request.getSession())) {
            BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            String theUrl = blobstoreService.createUploadUrl("/api/uploadImage");

            PrintWriter out = response.getWriter();
            out.write(theUrl);
            out.flush();
            out.close();
        } else {
            response.setStatus(HttpStatusCodes.STATUS_CODE_FORBIDDEN);
        }



    }
}
