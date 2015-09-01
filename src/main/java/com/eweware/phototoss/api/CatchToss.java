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
import com.google.appengine.repackaged.com.google.api.client.http.HttpStatusCodes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by ultradad on 1/21/15.
 */
public class CatchToss extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    private static long MAX_TOSS_TIME_IN_SECONDS = 3600; // 120;  todo - should be 120 for prod



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


    protected void doPost(final HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();

        final UserRecord curUser = Authenticator.CurrentUser(session);

        if (curUser != null) {
            String tossStr = request.getParameter("toss");
            String longStr = request.getParameter("long");
            String latStr = request.getParameter("lat");

            Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);

            List<BlobKey> blobKeys = blobs.get("file");

            if (blobKeys == null || blobKeys.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else if ((tossStr == null) || (longStr == null) || (latStr == null)) {
                response.setStatus(400);// error
            }
            else {
                final long tossId = Long.parseLong(tossStr);
                final double longitude = Double.parseDouble(longStr);
                final double latitude = Double.parseDouble(latStr);
                final Date currentTime = new Date();
                TossRecord tossRecStatic = ofy().load().key(Key.create(TossRecord.class, (long) tossId)).now();

                long diffInMillies = currentTime.getTime() - tossRecStatic.shareTime.getTime();
                long elapsedSec = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                if (elapsedSec > MAX_TOSS_TIME_IN_SECONDS) {
                    response.setStatus(400);
                    return;
                }

                final TossRecord tossRec = ofy().load().key(Key.create(TossRecord.class, (long) tossId)).now();
                final PhotoRecord sharedImage = ofy().load().key(Key.create(PhotoRecord.class, tossRec.imageId)).now();
                Key<PhotoRecord> curObj = ofy().load().type(PhotoRecord.class).filter("ownerid =", curUser.id).filter("originid =", sharedImage.originid).keys().first().now();

                if (curObj != null) {
                    response.setStatus(400);
                    PrintWriter out = response.getWriter();
                    out.write("duplicate");
                    out.flush();
                    out.close();
                }


                ImagesService imagesService = ImagesServiceFactory.getImagesService();
                ServingUrlOptions servingOptions = ServingUrlOptions.Builder.withBlobKey(blobKeys.get(0));

                final String servingUrl = imagesService.getServingUrl(servingOptions);

                PhotoRecord data = ofy().transact(new Work<PhotoRecord>() {
                    public PhotoRecord run() {
                        PhotoRecord newImage = new PhotoRecord();
                        newImage.ownername = curUser.username;
                        newImage.ownerid = curUser.id;

                        // copy from source
                        newImage.caption = sharedImage.caption;
                        newImage.created = sharedImage.created;
                        newImage.createdlat = sharedImage.createdlat;
                        newImage.createdlong = sharedImage.createdlong;
                        newImage.imageUrl = sharedImage.imageUrl;
                        newImage.thumbnailurl = sharedImage.thumbnailurl;
                        newImage.tags = sharedImage.tags;

                        // copied on toss
                        if (sharedImage.originid != null)
                            newImage.originid = sharedImage.originid;
                        else
                            newImage.originid = sharedImage.id;

                        newImage.parentid = sharedImage.id;
                        newImage.catchUrl = servingUrl;
                        newImage.receivedcaption = request.getParameter("caption");
                        newImage.receivedlat = latitude;
                        newImage.receivedlong = longitude;
                        newImage.received = new Date();
                        newImage.tosserid = tossRec.ownerId;
                        newImage.tossername = tossRec.ownerName;

                        // updated on tossed image
                        //sharedImage.totalshares++;

                        // update toss record
                       // tossRec.catchCount++;

                        // save to store
                        //ofy().save().entity(tossRec);
                        //ofy().save().entity(sharedImage);
                        ofy().save().entity(newImage);

                        return newImage;
                    }
                });


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
