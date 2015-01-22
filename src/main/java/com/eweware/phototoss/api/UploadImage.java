package com.eweware.phototoss.api;

import com.eweware.phototoss.core.PhotoRecord;
import com.eweware.phototoss.core.UserRecord;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.appengine.repackaged.com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;

import com.eweware.phototoss.core.PhotoRecord;
import com.googlecode.objectify.ObjectifyService;
import static com.googlecode.objectify.ObjectifyService.ofy;


/**
 * Created by Dave on 1/19/2015.
 */
public class UploadImage extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

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


                PhotoRecord data = new PhotoRecord();
                data.ownerid = curUser.id;
                data.ownername = curUser.username;
                data.imageUrl = servingUrl;
                data.caption = request.getParameter("caption");
                data.tags = new ArrayList<String>();
                data.tags.add(request.getParameter("tags"));
                data.created = new Date();

                // save to store
                ofy().save().entity(data).now();


                // write it to the user
                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                Gson gson = new GsonBuilder().create();
                gson.toJson(data, out);
                out.flush();
                out.close();
            }
        }
        else {
            response.setStatus(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
        }
    }


}
