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

package com.google.code.geobeagle.ui;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import android.widget.Spinner;

import junit.framework.TestCase;

public class ContentSelectorTest extends TestCase {

    public void testGetIndex() {
        Spinner spinner = createMock(Spinner.class);
        expect(spinner.getSelectedItemPosition()).andReturn(17);

        replay(spinner);
        ContentSelector contentSelector = new ContentSelector(spinner, null);
        assertEquals(17, contentSelector.getIndex());
        verify(spinner);
    }

}
