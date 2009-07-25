
package com.google.code.geobeagle;

import com.google.code.geobeagle.ErrorDisplayer.DisplayErrorRunnable;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface.OnClickListener;

@PrepareForTest( {
        Activity.class, DisplayErrorRunnable.class, ErrorDisplayer.class
})
@RunWith(PowerMockRunner.class)
public class ErrorDisplayerTest {

    @Test
    public void displayErrorWithArgs() throws Exception {
        Activity activity = PowerMock.createMock(Activity.class);
        Builder alertDialogBuilder = PowerMock.createMock(Builder.class);
        PowerMock.expectNew(Builder.class, activity).andReturn(alertDialogBuilder);
        EasyMock.expect(activity.getText(17)).andReturn("hello, %1$s");
        EasyMock.expect(alertDialogBuilder.setMessage("hello, world"))
                .andReturn(alertDialogBuilder);
        OnClickListener onClickListener = PowerMock.createMock(OnClickListener.class);
        DisplayErrorRunnable displayErrorRunnable = PowerMock
                .createMock(DisplayErrorRunnable.class);
        EasyMock.expect(alertDialogBuilder.setNeutralButton("Ok", onClickListener)).andReturn(
                alertDialogBuilder);
        AlertDialog alertDialog = PowerMock.createMock(AlertDialog.class);
        EasyMock.expect(alertDialogBuilder.create()).andReturn(alertDialog);

        PowerMock.expectNew(DisplayErrorRunnable.class, alertDialog)
                .andReturn(displayErrorRunnable);
        activity.runOnUiThread(displayErrorRunnable);

        PowerMock.replayAll();
        new ErrorDisplayer(activity, onClickListener).displayError(17, "world");
        PowerMock.verifyAll();
    }

    @Test
    public void displayErrorRunnable() {
        AlertDialog alertDialog = PowerMock.createMock(AlertDialog.class);
        alertDialog.show();

        PowerMock.replayAll();
        new DisplayErrorRunnable(alertDialog).run();
        PowerMock.verifyAll();
    }
}
