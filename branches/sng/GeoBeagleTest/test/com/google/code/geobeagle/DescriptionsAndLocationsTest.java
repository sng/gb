
package com.google.code.geobeagle;

import junit.framework.TestCase;

public class DescriptionsAndLocationsTest extends TestCase {

    public final void testDescriptionsAndLocations() {
        DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();

        CharSequence[] descriptions = new CharSequence[] {
                "SFO", "OAK"
        };
        final CharSequence[] locations = new CharSequence[] {
                "37 122 etc", "37 122 foo"
        };
        descriptionsAndLocations = new DescriptionsAndLocations();
        for (int ix = 0; ix < descriptions.length; ix++) {
            descriptionsAndLocations.add(descriptions[ix], locations[ix]);
        }
        assertEquals(descriptions[1], descriptionsAndLocations.getPreviousDescriptions().get(1));
        assertEquals(locations[0], descriptionsAndLocations.getPreviousLocations().get(0));
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
        CharSequence[] descriptions = new CharSequence[] {
                "SFO", "OAK"
        };
        final CharSequence[] locations = new CharSequence[] {
                "37 122 etc", "37 122 foo"
        };
        DescriptionsAndLocations descriptionsAndLocations = new DescriptionsAndLocations();
        for (int ix = 0; ix < descriptions.length; ix++) {
            descriptionsAndLocations.add(descriptions[ix], locations[ix]);
        }
        descriptionsAndLocations.clear();

        assertEquals(0, descriptionsAndLocations.getPreviousDescriptions().size());
        assertEquals(0, descriptionsAndLocations.getPreviousLocations().size());
    }
}
