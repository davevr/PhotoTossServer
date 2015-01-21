package com.eweware.phototoss.core;

import java.util.Date;
import java.util.List;

/**
 * Created by Dave on 1/19/2015.
 */
public class PhotoRecord {
    public String id;
    public String caption;
    public long totalshares;
    public String ownername ;
    public String ownerid;
    public List<String> tags ;
    public String sharedfromname ;
    public String sharedfromid ;
    public int myshares ;
    public int mysharesessions ;
    public Date received ;
    public Date created;
    public Date lastsharedbyuser ;
    public Date lastshared ;
    public double createdlat;
    public double createdlong ;
    public double receivedlat ;
    public String receivedcaption ;
    public String receivedtags ;
    public String imageUrl ;
    public String catchUrl;


}
