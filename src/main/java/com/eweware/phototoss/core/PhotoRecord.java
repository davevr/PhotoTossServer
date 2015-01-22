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
    @Id public Long id;
    public Long originid;
    public Long parentid;
    @Unindex public String caption;
    @Unindex public long totalshares;
    @Unindex public String ownername ;
    public Long ownerid;
    public List<String> tags ;
    @Unindex public String sharedfromname ;
    public String sharedfromid ;
    @Unindex public int myshares ;
    @Unindex public int mysharesessions ;
    public Date received ;
    public Date created;
    public Date lastsharedbyuser ;
    public Date lastshared ;
    public double createdlat;
    public double createdlong ;
    public double receivedlat ;
    public String receivedcaption ;
    public List<String> receivedtags ;
    @Unindex public String imageUrl ;
    @Unindex public String catchUrl;
    @Unindex public String thumbnailUrl;

    public PhotoRecord() {}


}
