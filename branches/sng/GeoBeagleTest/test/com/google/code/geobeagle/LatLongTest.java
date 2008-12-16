
package com.google.code.geobeagle;

import junit.framework.TestCase;

public class LatLongTest extends TestCase {
    public void testLatLong() {
        Destination ll = new Destination("37 00.0 122 00.0");
        assertEquals(37.0, ll.getLatitude());
        assertEquals(122.0, ll.getLongitude());

        Destination ll2 = new Destination("37 00.0\t122 00.0");
        assertEquals(37.0, ll2.getLatitude());
        assertEquals(122.0, ll2.getLongitude());

        Destination ll3 = new Destination("37 03.0 122 00.0");
        assertEquals(37.05, ll3.getLatitude());
        assertEquals(122.0, ll3.getLongitude());

        Destination ll4 = new Destination(" \t 37 03.0 122 00.0  ");
        assertEquals(37.05, ll4.getLatitude());
        assertEquals(122.0, ll4.getLongitude());
        assertEquals("", ll4.getDescription());
    }

    public void testDescription() {
        Destination ll5 = new Destination(" \t 37 03.0 122 00.0 # Description ");
        assertEquals(37.05, ll5.getLatitude());
        assertEquals(122.0, ll5.getLongitude());
        assertEquals("Description", ll5.getDescription());

    }
}
