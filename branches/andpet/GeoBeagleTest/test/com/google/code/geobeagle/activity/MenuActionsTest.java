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

package com.google.code.geobeagle.activity;

import static org.junit.Assert.*;

import com.google.code.geobeagle.actions.MenuAction;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.cachelist.actions.MenuActionSyncGpx;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

@PrepareForTest( {
    Log.class
})
@RunWith(PowerMockRunner.class)
public class MenuActionsTest {
    @Before
    public void ignoreLogging() {
        PowerMock.mockStatic(Log.class);
        EasyMock.expect(Log.w((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(0).anyTimes();
    }

    @Test
    public void testAct() {
        MenuActionSyncGpx menuActionSyncGpx = PowerMock.createMock(MenuActionSyncGpx.class);
        //Resources resources = PowerMock.createMock(Resources.class);

        //EasyMock.expect(menuActionSyncGpx.getId()).andReturn(R.string.menu_sync).times(2);
        menuActionSyncGpx.act();

        PowerMock.replayAll();
        final MenuActions menuActions = new MenuActions();
        menuActions.add(menuActionSyncGpx);

        assertTrue(menuActions.act(0));
        assertFalse(menuActions.act(1));
        PowerMock.verifyAll();
    }

    @Test
    public void testArrayCtor() {
        MenuAction menuAction = PowerMock.createMock(MenuAction.class);
        MenuAction[] menuActionsArray = {
            menuAction
        };

        //EasyMock.expect(menuAction.getId()).andReturn(1);
        menuAction.act();

        PowerMock.replayAll();
        MenuActions menuActions = new MenuActions(menuActionsArray);
        assertTrue(menuActions.act(0));
        PowerMock.verifyAll();
    }

    @Test
    public void testCreateOptionsEmptyMenu() {
        MenuAction[] menuActionsArray = {};
        Menu menu = PowerMock.createMock(Menu.class);

        PowerMock.replayAll();
        MenuActions menuActions = new MenuActions(menuActionsArray);
        assertFalse(menuActions.onCreateOptionsMenu(menu));
        PowerMock.verifyAll();
    }

    @Test
    public void testCreateOptionsMenu() {
        MenuAction menuAction = PowerMock.createMock(MenuAction.class);
        EasyMock.expect(menuAction.getLabel()).andReturn("menuitem");
        MenuAction[] menuActionsArray = {
            menuAction
        };
        MenuItem menuItem = PowerMock.createMock(MenuItem.class);
        Menu menu = PowerMock.createMock(Menu.class);

        menu.clear();
        //EasyMock.expect(menuAction.getId()).andReturn(1);
        EasyMock.expect(menu.add(0, 0, 0, "menuitem")).andReturn(menuItem);

        PowerMock.replayAll();
        MenuActions menuActions = new MenuActions(menuActionsArray);
        assertTrue(menuActions.onCreateOptionsMenu(menu));
        PowerMock.verifyAll();

    }

}
