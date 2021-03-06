/*
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.google.code.geobeagle.activity.cachelist.presenter;

import static org.junit.Assert.*;

import com.google.code.geobeagle.formatting.DistanceFormatterImperial;

import org.junit.Test;

import java.util.Locale;

public class DistanceFormatterImperialTest {
    @Test
    public void testFormatNegativeDistance() {
        assertEquals("", new DistanceFormatterImperial().formatDistance(-1));
    }

    @Test
    public void testMiles() {
        Locale.setDefault(Locale.GERMANY);
        assertEquals("1,00mi", new DistanceFormatterImperial().formatDistance(1609.344f));
    }

    @Test
    public void testFeet() {
        Locale.setDefault(Locale.GERMANY);
        assertEquals("2ft", new DistanceFormatterImperial().formatDistance(0.914f));
        assertEquals("3ft", new DistanceFormatterImperial().formatDistance(0.915f));
        assertEquals("3ft", new DistanceFormatterImperial().formatDistance(0.916f));
    }
}
