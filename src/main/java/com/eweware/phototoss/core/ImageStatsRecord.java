package com.eweware.phototoss.core;


import com.eweware.phototoss.api.ImageLineage;
import com.googlecode.objectify.Key;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by ultradad on 10/5/15.
 */

public class ImageStatsRecord {
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

            ImageStatsRecord newRec = new ImageStatsRecord();

            newRec.imageid = curPhotoId;
            newRec.numcopies = ofy().load().type(PhotoRecord.class).filter("originid=", foundImage.originid).count();
            newRec.numparents = ImageLineage.GetImageLineage(foundImage).size();
            newRec.numchildren = ofy().load().type(PhotoRecord.class).filter("parentid=", curPhotoId).count();
            newRec.numtosses = ofy().load().type(TossRecord.class).filter("imageId=", curPhotoId).count();
            return newRec;
        }
    }
}