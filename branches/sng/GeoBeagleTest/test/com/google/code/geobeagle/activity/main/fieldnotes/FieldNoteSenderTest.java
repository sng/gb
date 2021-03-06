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

package com.google.code.geobeagle.activity.main.fieldnotes;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldNoteSender.DialogHelper;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldNoteSender.FieldNoteResources;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldNoteSender.OnClickCancel;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldNoteSender.OnClickOk;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.Editable;
import android.text.InputFilter;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        DateFormat.class, Linkify.class, View.class, FieldNoteSender.class, EditText.class
})
public class FieldNoteSenderTest {
    @Test
    public void testCreateDialog() throws Exception {
        LayoutInflater layoutInflater = PowerMock.createMock(LayoutInflater.class);
        AlertDialog.Builder dialogBuilder = PowerMock.createMock(AlertDialog.Builder.class);
        View fieldNoteDialogView = PowerMock.createMock(View.class);
        DialogHelper dialogHelper = PowerMock.createMock(DialogHelper.class);
        TextView caveat = PowerMock.createMock(TextView.class);
        EditText editText = PowerMock.createMock(EditText.class);
        OnClickOk onClickOk = PowerMock.createMock(OnClickOk.class);
        OnClickCancel onClickCancel = PowerMock.createMock(OnClickCancel.class);
        Dialog dialog = PowerMock.createMock(Dialog.class);
        Context context = PowerMock.createMock(Context.class);

        PowerMock.mockStatic(Linkify.class);
        EasyMock.expect(fieldNoteDialogView.findViewById(R.id.fieldnote_caveat)).andReturn(caveat);
        EasyMock.expect(Linkify.addLinks(caveat, Linkify.WEB_URLS)).andReturn(true);
        EasyMock.expect(layoutInflater.inflate(R.layout.fieldnote, null)).andReturn(
                fieldNoteDialogView);
        FieldNoteResources fieldNoteResources = PowerMock.createMock(FieldNoteResources.class);
        EasyMock.expect(
                dialogHelper.createEditor(fieldNoteDialogView, "GC123 ", fieldNoteResources))
                .andReturn(editText);
        PowerMock.expectNew(OnClickOk.class, fieldNoteResources, editText, "GC123 ", context)
                .andReturn(onClickOk);
        PowerMock.expectNew(OnClickCancel.class).andReturn(onClickCancel);
        EasyMock.expect(
                dialogHelper.createDialog(dialogBuilder, fieldNoteDialogView, onClickOk,
                        onClickCancel)).andReturn(dialog);

        PowerMock.replayAll();
        FieldNoteSender fieldNoteSender = new FieldNoteSender(layoutInflater, dialogBuilder,
                dialogHelper);
        fieldNoteSender.createDialog("GC123", fieldNoteResources, context);
        PowerMock.verifyAll();
    }

    @Test
    public void testCreateEditor() throws Exception {
        View fieldNoteDialogView = PowerMock.createMock(View.class);
        EditText editText = PowerMock.createMock(EditText.class);
        DateFormat dateFormat = PowerMock.createMock(DateFormat.class);
        Date date = PowerMock.createMock(Date.class);
        InputFilter.LengthFilter lengthFilter = PowerMock
                .createMock(InputFilter.LengthFilter.class);
        FieldNoteResources fieldNoteResources = PowerMock.createMock(FieldNoteResources.class);

        EasyMock.expect(fieldNoteDialogView.findViewById(R.id.fieldnote)).andReturn(editText);
        PowerMock.mockStatic(DateFormat.class);
        EasyMock.expect(DateFormat.getTimeInstance(DateFormat.MEDIUM)).andReturn(dateFormat);
        PowerMock.expectNew(Date.class).andReturn(date);
        EasyMock.expect(dateFormat.format(date)).andReturn("12:36 PM");
        EasyMock.expect(fieldNoteResources.getString(R.array.geobeagle_sig)).andReturn("fwgb");
        EasyMock.expect(fieldNoteResources.getString(R.array.default_msg)).andReturn("TFTC! ");
        editText.setText("(12:36 PM/fwgb) TFTC! ");
        EasyMock.expect(fieldNoteResources.getString(R.array.fieldnote_code)).andReturn("GEOC @");
        PowerMock.expectNew(InputFilter.LengthFilter.class, 148).andReturn(lengthFilter);
        editText.setFilters((InputFilter[])EasyMock.anyObject());
        editText.setSelection(16, 22);

        PowerMock.replayAll();
        new DialogHelper().createEditor(fieldNoteDialogView, "GC123 ", fieldNoteResources);
        PowerMock.verifyAll();
    }

    @Test
    public void testDialogHelperCreateDialog() {
        Builder builder = PowerMock.createMock(Builder.class);
        View fieldNoteDialogView = PowerMock.createMock(View.class);
        OnClickOk onClickOk = PowerMock.createMock(OnClickOk.class);
        OnClickCancel onClickCancel = PowerMock.createMock(OnClickCancel.class);
        AlertDialog dialog = PowerMock.createMock(AlertDialog.class);

        EasyMock.expect(builder.setTitle(R.string.field_note_title)).andReturn(builder);
        EasyMock.expect(builder.setView(fieldNoteDialogView)).andReturn(builder);
        EasyMock.expect(builder.setPositiveButton(R.string.send_sms, onClickOk)).andReturn(builder);
        EasyMock.expect(builder.setNegativeButton(R.string.cancel, onClickCancel)).andReturn(
                builder);
        EasyMock.expect(builder.create()).andReturn(dialog);

        PowerMock.replayAll();
        assertEquals(dialog, new DialogHelper().createDialog(builder, fieldNoteDialogView,
                onClickOk, onClickCancel));
        PowerMock.verifyAll();
    }

    @Test
    public void testOnClickCancel() {
        Dialog dialog = PowerMock.createMock(Dialog.class);
        dialog.dismiss();

        PowerMock.replayAll();
        new OnClickCancel().onClick(dialog, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testOnClickOk() throws Exception {
        Dialog dialog = PowerMock.createMock(Dialog.class);
        EditText editText = PowerMock.createMock(EditText.class);
        Editable editable = PowerMock.createMock(Editable.class);
        Intent intent = PowerMock.createMock(Intent.class);
        Context context = PowerMock.createMock(Context.class);
        FieldNoteResources fieldNoteResources = PowerMock.createMock(FieldNoteResources.class);

        dialog.dismiss();
        EasyMock.expect(editText.getText()).andReturn(editable);
        EasyMock.expect(fieldNoteResources.getString(R.array.fieldnote_code)).andReturn("GEOC @");
        PowerMock.expectNew(Intent.class, Intent.ACTION_VIEW).andReturn(intent);
        EasyMock.expect(intent.putExtra("address", "41411")).andReturn(intent);
        EasyMock
                .expect(intent.putExtra((String)EasyMock.anyObject(), (String)EasyMock.anyObject()))
                .andReturn(intent);
        EasyMock.expect(intent.setType("vnd.android-dir/mms-sms")).andReturn(intent);
        context.startActivity(intent);

        PowerMock.replayAll();
        new OnClickOk(fieldNoteResources, editText, editable, context).onClick(dialog, 0);
        PowerMock.verifyAll();
    }

    @Test
    public void testFieldNoteResources() {
        Resources resources = PowerMock.createMock(Resources.class);

        String[] strings = new String[] {
                "dnf", "find"
        };
        EasyMock.expect(resources.getStringArray(17)).andReturn(strings).times(2);

        PowerMock.replayAll();
        assertEquals("find", new FieldNoteResources(resources, R.id.menu_log_find).getString(17));
        assertEquals("dnf", new FieldNoteResources(resources, R.id.menu_log_dnf).getString(17));
        PowerMock.verifyAll();

    }
}
