package com.eweware.phototoss.api;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.repackaged.com.google.api.client.http.HttpStatusCodes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by ultradad on 1/26/15.
 */
public class GetCatchURL extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (Authenticator.getInstance().UserIsLoggedIn(request.getSession())) {
            BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            String theUrl = blobstoreService.createUploadUrl("/api/catch");

            PrintWriter out = response.getWriter();
            out.write(theUrl);
            out.flush();
            out.close();
        } else {
            response.setStatus(HttpStatusCodes.STATUS_CODE_FORBIDDEN);
        }



    }


}
