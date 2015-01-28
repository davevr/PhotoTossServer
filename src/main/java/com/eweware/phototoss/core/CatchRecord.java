package com.eweware.phototoss.core;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

/**
 * Created by ultradad on 1/27/15.
 */

@Entity
@Index
public class CatchRecord {
    @Id
    public Long id;
    public Date catchTime;
    public Long tossId;
    public Long catcherId;
    public Long tosserId;

}
