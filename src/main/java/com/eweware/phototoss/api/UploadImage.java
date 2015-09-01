package com.eweware.phototoss.api;

import com.eweware.phototoss.core.PhotoRecord;
import com.eweware.phototoss.core.TossRecord;
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
import com.google.apphosting.api.ApiProxy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.eweware.phototoss.core.PhotoRecord;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import static com.googlecode.objectify.ObjectifyService.ofy;


/**
 * Created by Dave on 1/19/2015.
 */
public class UploadImage extends HttpServlet {
    private static final Logger log = Logger.getLogger(UploadImage.class.getName());
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if (Authenticator.getInstance().UserIsLoggedIn(request.getSession())) {
            BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            String theUrl = blobstoreService.createUploadUrl("/api/image/upload");

            PrintWriter out = response.getWriter();
            out.write(theUrl);
            out.flush();
            out.close();
        } else {
            log.log(Level.WARNING, "Failed to authenticate user");
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
                log.log(Level.WARNING, "Failed to find image data");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
            else {
                ImagesService imagesService = ImagesServiceFactory.getImagesService();
                ServingUrlOptions servingOptions = ServingUrlOptions.Builder.withBlobKey(blobKeys.get(0));
                String servingUrl = imagesService.getServingUrl(servingOptions);
                String thumbStr = request.getParameter("thumbnail");

                if ((thumbStr == null) || (thumbStr.isEmpty())) {
                    // update the main image
                    String longStr = request.getParameter("long");
                    String latStr = request.getParameter("lat");

                    if ((longStr == null) || (latStr == null)) {
                        log.log(Level.WARNING, "no location data in request");
                        response.setStatus(400);
                        return;
                    }
                    double longitude = Double.parseDouble(longStr);
                    double latitude = Double.parseDouble(latStr);


                    // get an image server URL
                    PhotoRecord newRec = saveMainImage(curUser, servingUrl, latitude, longitude);

                    // write it to the user
                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    Gson gson = new GsonBuilder().create();
                    gson.toJson(newRec, out);
                    out.flush();
                    out.close();
                } else {
                    // update the thumbnail only
                    String imageIdStr = request.getParameter("imageid");
                    long imageId = Long.parseLong(imageIdStr);

                    updateImageThumbnail(imageId, servingUrl);

                    PrintWriter out = response.getWriter();
                    out.write(servingUrl);
                    out.flush();
                    out.close();
                }

            }
        }
        else {
            log.log(Level.WARNING, "Failed to authenticate user");
            response.setStatus(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
        }
    }

    private void updateImageThumbnail(long imageId, String servingURL) {
        final PhotoRecord image = ofy().load().key(Key.create(PhotoRecord.class, imageId)).now();
        image.thumbnailurl = servingURL;
        ofy().save().entity(image);
    }


    private PhotoRecord saveMainImage(UserRecord curUser, String servingURL, double latitude, double longitude) {
        PhotoRecord data = new PhotoRecord();
        data.ownerid = curUser.id;
        data.ownername = curUser.username;
        data.imageUrl = servingURL;
        data.thumbnailurl = servingURL;
        data.created = new Date();
        data.createdlong = longitude;
        data.createdlat = latitude;

        // save to store
        ofy().save().entity(data).now();

        return data;
    }


}
