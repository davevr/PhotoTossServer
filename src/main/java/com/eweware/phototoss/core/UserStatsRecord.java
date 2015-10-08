package com.eweware.phototoss.core;

import com.googlecode.objectify.Key;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by ultradad on 10/5/15.
 */

public class UserStatsRecord {
    public Long userid;
    public Integer numtosses;
    public Integer numcatches;
    public Integer numimages;
    public Integer numoriginals;

    public static UserStatsRecord CreateFromUser(long curUserId) {


        UserStatsRecord newRec = new UserStatsRecord();

        newRec.userid = curUserId;
        newRec.numtosses = ofy().load().type(TossRecord.class).filter("ownerId =", curUserId).count();
        newRec.numcatches = ofy().load().type(PhotoRecord.class).filter("tosserid =", curUserId).count();

        newRec.numimages = ofy().load().type(PhotoRecord.class).filter("ownerid =", curUserId).count();
        newRec.numoriginals = ofy().load().type(PhotoRecord.class).filter("ownerid =", curUserId).filter("tossid =", 0).count();

        return newRec;
    }
}
