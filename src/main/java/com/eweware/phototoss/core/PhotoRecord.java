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
    @Unindex public String ownername ;
    public Long ownerid;

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
    @Unindex public String catchUrl;
    public String receivedcaption ;
    public double receivedlat ;
    public double receivedlong;
    public Date received ;
    public Long tosserid;

    // updated on an image after toss
    public Date lastshared ;
    @Unindex public int tossCount ;
    @Unindex public Long totalshares;


    public PhotoRecord() {}


}
