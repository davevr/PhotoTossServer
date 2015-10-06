package com.eweware.phototoss.core;


import com.eweware.phototoss.api.ImageLineage;
import com.googlecode.objectify.Key;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by ultradad on 10/5/15.
 */

public class ImageStatsRecord {
    private static final Logger log = Logger.getLogger(ImageStatsRecord.class.getName());
    public Long imageid;
    public Integer numcopies;
    public Integer numtosses;
    public Integer numparents;
    public Integer numchildren;

    public static ImageStatsRecord CreateFromPhoto(long curPhotoId) {
        final PhotoRecord foundImage = ofy().load().key(Key.create(PhotoRecord.class, curPhotoId)).now();

        if (foundImage == null)
            return null;
        else {
            Long originId = foundImage.originid;
            if (originId == null)
                originId = foundImage.id;

            ImageStatsRecord newRec = new ImageStatsRecord();

            newRec.imageid = curPhotoId;
            newRec.numcopies = ofy().load().type(PhotoRecord.class).filter("originid =", originId).count() + 1;
            newRec.numparents = ImageLineage.GetImageLineage(foundImage).size();
            newRec.numchildren = ofy().load().type(PhotoRecord.class).filter("parentid =", curPhotoId).count();
            newRec.numtosses = ofy().load().type(TossRecord.class).filter("imageId =", curPhotoId).count();
            return newRec;
        }
    }
}