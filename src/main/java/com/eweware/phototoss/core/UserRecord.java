package com.eweware.phototoss.core;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Unindex;

import java.util.Date;

/**
 * Created by ultradad on 1/21/15.
 */
@Entity
@Index
public class UserRecord {
    @Id public Long id;

    public String username;
    @Unindex public transient String passwordhash;
    @Unindex public transient String passwordsalt;
    @Unindex public Date creationDate;
    public Date lastActiveDate;
    public Boolean signedOn;
    @Unindex public String imageurl;
}
