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
public class CatchRecord {
    @Id
    public Long id;
    public Date catchdate;
    public Long imageid;
    public Long tossid;
    public Long catchimageid;
    public Long tosserid;
    public double catchlong;
    public double catchlat;
}
