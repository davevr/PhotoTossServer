package com.eweware.phototoss.core;

import com.googlecode.objectify.annotation.Entity;

/**
 * Created by ultradad on 10/14/15.
 */

public class BarcodeLocation {
    public BarcodePoint topleft;
    public BarcodePoint topright;
    public BarcodePoint bottomleft;
    public BarcodePoint bottomright;

    public BarcodeLocation() {
        topleft = new BarcodePoint();
        topright = new BarcodePoint();
        bottomleft = new BarcodePoint();
        bottomright = new BarcodePoint();

    }
}


