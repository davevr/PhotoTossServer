package com.eweware.phototoss.api;

import com.eweware.phototoss.core.PhotoRecord;
import com.eweware.phototoss.core.UserRecord;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.*;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by ultradad on 1/28/15.
 */
public class UserImage extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (Authenticator.getInstance().UserIsLoggedIn(request.getSession())) {
            BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            String theUrl = blobstoreService.createUploadUrl("/api/user/image");

            PrintWriter out = response.getWriter();
            out.write(theUrl);
            out.flush();
            out.close();
        } else {
            response.setStatus(HttpStatusCodes.STATUS_CODE_FORBIDDEN);
        }
    }



    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // ensure user is signed in
        HttpSession session = request.getSession();

        UserRecord curUser = Authenticator.CurrentUser(session);

        if (curUser != null) {
            Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);

            List<BlobKey> blobKeys = blobs.get("file");

            if (blobKeys == null || blobKeys.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            else {
                // get an image server URL
                ImagesService imagesService = ImagesServiceFactory.getImagesService();
                ServingUrlOptions servingOptions = ServingUrlOptions.Builder.withBlobKey(blobKeys.get(0));
                String servingUrl = imagesService.getServingUrl(servingOptions);

                 curUser.imageurl = servingUrl;

                // save to store
                ofy().save().entity(curUser).now();

                PrintWriter out = response.getWriter();
                out.write(servingUrl);
                out.flush();
                out.close();
            }
        }
        else {
            response.setStatus(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
        }
    }
}
