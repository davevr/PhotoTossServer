package com.eweware.phototoss.api;

import com.eweware.phototoss.core.BarcodeLocation;
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
    public static long MAX_TOSS_TIME_IN_SECONDS = 3600; // 120;  todo - should be 120 for prod



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
            final String tossStr = request.getParameter("toss");
            final String longStr = request.getParameter("long");
            final String latStr = request.getParameter("lat");

            final Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);

            final List<BlobKey> blobKeys = blobs.get("file");

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
                final TossRecord tossRec = ofy().load().key(Key.create(TossRecord.class, (long) tossId)).now();


                long diffInMillies = currentTime.getTime() - tossRec.shareTime.getTime();
                long elapsedSec = TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                if (elapsedSec > MAX_TOSS_TIME_IN_SECONDS) {
                    response.setContentType("text/html");
                    response.setStatus(400);
                    PrintWriter out = response.getWriter();
                    out.write("expired_toss");
                    out.flush();
                    out.close();
                    return;
                }


                final PhotoRecord sharedImage = ofy().load().key(Key.create(PhotoRecord.class, tossRec.imageId)).now();
                Key<PhotoRecord> curObj = ofy().load().type(PhotoRecord.class).filter("ownerid =", curUser.id).filter("originid =", sharedImage.originid).keys().first().now();

                if (curObj != null) {
                    response.setContentType("text/html");
                    response.setStatus(400);
                    PrintWriter out = response.getWriter();
                    out.write("duplicate_image");
                    out.flush();
                    out.close();
                } else {
                    ImagesService imagesService = ImagesServiceFactory.getImagesService();
                    ServingUrlOptions servingOptions = ServingUrlOptions.Builder.withBlobKey(blobKeys.get(0));

                    final String servingUrl = imagesService.getServingUrl(servingOptions);
                    final String tlxStr = request.getParameter("tlx");
                    final String tlyStr = request.getParameter("tly");
                    final String trxStr = request.getParameter("trx");
                    final String tryStr = request.getParameter("try");
                    final String blxStr = request.getParameter("blx");
                    final String blyStr = request.getParameter("bly");
                    final String brxStr = request.getParameter("brx");
                    final String bryStr = request.getParameter("bry");

                    PhotoRecord data = ofy().transact(new Work<PhotoRecord>() {
                        public PhotoRecord run() {
                            PhotoRecord newImage = new PhotoRecord();
                            newImage.ownername = curUser.username;
                            newImage.ownerid = curUser.id;

                            // maybe set coords
                            if ((tlxStr != null) && (tlyStr != null) && (trxStr != null) && (tryStr != null) &&
                                    (blxStr != null) && (blyStr != null) && (brxStr != null) && (bryStr != null)) {

                                // set coord
                                final float tlx = Float.parseFloat(tlxStr);
                                final float tly = Float.parseFloat(tlyStr);
                                final float trx = Float.parseFloat(trxStr);
                                final float _try = Float.parseFloat(tryStr);
                                final float blx = Float.parseFloat(blxStr);
                                final float bly = Float.parseFloat(blyStr);
                                final float brx = Float.parseFloat(brxStr);
                                final float bry = Float.parseFloat(bryStr);
                                newImage.barcodelocation = new BarcodeLocation();
                                newImage.barcodelocation.topleft.x = tlx;
                                newImage.barcodelocation.topleft.y = tly;
                                newImage.barcodelocation.topright.x = trx;
                                newImage.barcodelocation.topright.y = _try;

                                newImage.barcodelocation.bottomleft.x = blx;
                                newImage.barcodelocation.bottomleft.y = bly;
                                newImage.barcodelocation.bottomright.x = brx;
                                newImage.barcodelocation.bottomright.y = bry;
                            }

                            // copy from source
                            newImage.created = sharedImage.created;
                            newImage.createdlat = sharedImage.createdlat;
                            newImage.createdlong = sharedImage.createdlong;
                            newImage.imageUrl = sharedImage.imageUrl;
                            newImage.thumbnailurl = sharedImage.thumbnailurl;

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
                            newImage.tossid = tossRec.id;

                            ofy().save().entity(newImage);

                            return newImage;
                        }
                    });

                    // update stats on original image
                    ofy().transact(new VoidWork() {
                        public void vrun() {
                            final PhotoRecord originalImage = ofy().load().key(Key.create(PhotoRecord.class, sharedImage.originid)).now();
                            if (originalImage.totalshares == null)
                                originalImage.totalshares = 1L;
                            else
                                originalImage.totalshares++;
                            ofy().save().entity(originalImage);
                        }
                    });



                    NotifyParentsOfToss(sharedImage);
                    // write it to the user
                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    Gson gson = new GsonBuilder().create();
                    gson.toJson(data, out);
                    out.flush();
                    out.close();
                }

            }
        }
        else {
            response.setStatus(HttpStatusCodes.STATUS_CODE_UNAUTHORIZED);
        }
    }

    private void NotifyParentsOfToss(PhotoRecord thePhoto) throws IOException {
        List<PhotoRecord> parentImages = ImageLineage.GetImageLineage(thePhoto);

        if (parentImages != null && parentImages.size() > 0) {
            Set<String> keyMap = new HashSet<String>();

            for (PhotoRecord curParent : parentImages) {
                keyMap.add("user_" + curParent.ownerid.toString());
            }

            String imageId = thePhoto.originid.toString();
            String imageUrl = thePhoto.imageUrl + "=s256-c";
            String titleStr = "Your photo was tossed!";
            String contentStr = "Check the map for details...";

            NotifyTest.SendNotification(titleStr, contentStr, imageId, imageUrl, keyMap);
        }
    }


}
