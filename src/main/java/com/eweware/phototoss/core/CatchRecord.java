package com.eweware.phototoss.core;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by Dave on 1/21/2015.
 */

@Entity
@Index
public class CatchRecord {
    @Id
    public Long id;
}
