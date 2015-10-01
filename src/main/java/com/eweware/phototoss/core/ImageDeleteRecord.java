package com.eweware.phototoss.core;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by ultradad on 9/30/15.
 */


@Entity
@Index
public class ImageDeleteRecord {
    @Id
    public Long id;
    public Long userId;
    public Long imageId;

}
