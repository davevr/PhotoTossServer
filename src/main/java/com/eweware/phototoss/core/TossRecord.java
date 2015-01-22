package com.eweware.phototoss.core;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;

/**
 * Created by Dave on 1/21/2015.
 */

@Entity
@Index
public class TossRecord {
    @Id public Long id;
    public Date tossdate;
    public Long imageid;
    public Long originalimageid;
    public Long ownerid;
    public Long catchcount;
    public double tosslong;
    public double tosslat;

}
