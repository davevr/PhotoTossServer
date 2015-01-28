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
public class TossRecord {
    @Id
    public Long id;
    public Long ownerId;
    public Long imageId;
    public int gameType;
    public Long catchCount;
    public Date shareTime;
    public double shareLong;
    public double shareLat;
}
