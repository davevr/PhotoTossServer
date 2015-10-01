package com.eweware.phototoss.core;

import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;

import java.util.Date;
import java.util.List;

/**
 * Created by Dave on 1/19/2015.
 */
@Entity
@Index
public class PhotoRecord {
    // unique for each photo
    @Id public Long id;
    @Unindex public String ownername ; // original owner who took the picture
    public Long ownerid; // id of the person who took the picture

    // copied from source image
    @Unindex public String caption;
    public Date created;
    public double createdlat;
    public double createdlong ;
    @Unindex public String imageUrl ;
    @Unindex public String thumbnailurl;
    public List<String> tags ;

    // completed on a toss on the new image
    public Long originid;   // original image
    public Long parentid;   // most recent image
    @Unindex public String catchUrl;  // the url of the image taken when this image was caught
    public String receivedcaption ; // the caption set when this image was tossed
    public double receivedlat ;  // the lat this image was caught at
    public double receivedlong;  // the long this image was caught at
    public Date received ;  // the date this image was caught
    public Long tosserid;  // the id of the person who tossed this image
    public String tossername; // the name of the person who tossed this image
    public Long tossid; // the id of the toss record - used to track tosses up the chain

    // updated on an image after toss
    public Date lastshared ;
    @Unindex public int tossCount ;
    @Unindex public Long totalshares;
    public Boolean deleted;

    public PhotoRecord() {
        deleted = false;
    }


}
