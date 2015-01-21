package com.eweware.phototoss.api;

import com.eweware.phototoss.core.PhotoRecord;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;

import com.eweware.phototoss.core.PhotoRecord;


/**
 * Created by Dave on 1/19/2015.
 */
public class UploadImage extends HttpServlet {
    private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);

        List<BlobKey> blobKeys = blobs.get("file");

        if (blobKeys == null || blobKeys.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } else {
            // get an image server URL
            ImagesService imagesService = ImagesServiceFactory.getImagesService();
            ServingUrlOptions servingOptions = ServingUrlOptions.Builder.withBlobKey(blobKeys.get(0));

            String servingUrl = imagesService.getServingUrl(servingOptions);
            //response.sendRedirect(servingUrl);
            //res.sendRedirect("/serve?blob-key=" + blobKeys.get(0).getKeyString());
            response.setContentType("application/json");
            PhotoRecord data = new PhotoRecord();
            data.imageUrl = servingUrl;
            data.id = "Hello";
            data.caption = request.getParameter("caption");
            data.tags = new ArrayList<String>();
            data.tags.add(request.getParameter("tags"));

            PrintWriter out = response.getWriter();
            Gson gson = new GsonBuilder().create();
            gson.toJson(data, out);
            out.flush();
            out.close();
        }
    }


}
