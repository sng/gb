
package com.google.code.geobeagle;

import junit.framework.TestCase;

public class DescriptionsAndLocationsTest extends TestCase {

    public final void testDescriptionsAndLocations() {
        DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();

        descriptionsAndLocations = new DescriptionsAndLocations();
        descriptionsAndLocations.add("SFO", "37 122 etc");
        descriptionsAndLocations.add("OAK", "37 122 foo");
        assertEquals("OAK", descriptionsAndLocations.getPreviousDescriptions().get(1));
        assertEquals("37 122 etc", descriptionsAndLocations.getPreviousLocations().get(0));
    }

    public final void testAdd() {
        DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();
        descriptionsAndLocations.add("new description", "new location");
        assertEquals("new description", descriptionsAndLocations.getPreviousDescriptions().get(0));
    }

    public final void testAddDupe() {
        DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();
        descriptionsAndLocations.add("description1", "new location");
        descriptionsAndLocations.add("description2", "new location");
        descriptionsAndLocations.add("description2", "new location");

        assertEquals(2, descriptionsAndLocations.getPreviousDescriptions().size());
        assertEquals("description1", descriptionsAndLocations.getPreviousDescriptions().get(0));
        assertEquals("description2", descriptionsAndLocations.getPreviousDescriptions().get(1));
    }

    public final void testAddRemoveOldest() {
        DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations(3);
        descriptionsAndLocations.add("description1", "new location");
        descriptionsAndLocations.add("description2", "new location");
        descriptionsAndLocations.add("description3", "new location");
        descriptionsAndLocations.add("description4", "new location");

        assertEquals(3, descriptionsAndLocations.getPreviousDescriptions().size());
        assertEquals("description2", descriptionsAndLocations.getPreviousDescriptions().get(0));
    }

    public final void testClear() {
        DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();
        descriptionsAndLocations.add("SFO", "37 122 etc");
        descriptionsAndLocations.add("OAK", "37 122 foo");
        descriptionsAndLocations.clear();

        assertEquals(0, descriptionsAndLocations.getPreviousDescriptions().size());
        assertEquals(0, descriptionsAndLocations.getPreviousLocations().size());
    }
}
