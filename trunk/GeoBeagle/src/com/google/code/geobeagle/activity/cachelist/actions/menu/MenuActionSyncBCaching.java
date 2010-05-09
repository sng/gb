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

package com.google.code.geobeagle.activity.cachelist.actions.menu;

import com.google.code.geobeagle.R;
import com.google.code.geobeagle.actions.MenuActionBase;
import com.google.code.geobeagle.bcaching.BcachingCommunication;
import com.google.code.geobeagle.cachedetails.WriterWrapper;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Hashtable;

public class MenuActionSyncBCaching extends MenuActionBase {

    private static final class ProgressHandler extends Handler {
        private final ProgressDialog progressDialog;

        ProgressHandler(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
        }

        @Override
        public void handleMessage(Message msg) {
            progressDialog.setTitle("hello  " + msg.getWhen());
            if (msg.what == 0)
                progressDialog.setMax(msg.arg1);
            else if (msg.what == 1)
                progressDialog.setProgress(msg.arg1);
            else if (msg.what == 2)
                progressDialog.dismiss();
            else if (msg.what == 3)
                progressDialog.show();
        }
    }

    static class ImportBcachingWorker extends Thread {

        private final Handler handler;
        private final SharedPreferences sharedPreferences;

        public ImportBcachingWorker(Handler handler, SharedPreferences sharedPreferences) {
            this.handler = handler;
            this.sharedPreferences = sharedPreferences;
        }

        private String readResponse(InputStream in) throws IOException {
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, Charset
                    .forName("UTF-8")), 8192);

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
                result.append('\n');
            }
            return result.toString();
        }

        private void getCacheDetails(BcachingCommunication bcachingCommunication, String csvIds,
                int updatedCaches) throws Exception, IOException {
            Log.d("GeoBeagle", "Getting details: " + updatedCaches);
            Hashtable<String, String> params = new Hashtable<String, String>();
            params.put("a", "detail");
            params.put("desc", "html");
            params.put("ids", csvIds);
            params.put("tbs", "0");
            params.put("wpts", "1");
            params.put("logs", "1");
            params.put("fmt", "gpx");
            params.put("app", "GeoBeagle");

            Log.d("GeoBeagle", "Downloading cache details");
            InputStream is = bcachingCommunication.sendRequest(params);
            DataInputStream dis = new DataInputStream(is);
            String line = null;
            WriterWrapper writerWrapper = new WriterWrapper();
            writerWrapper
                    .open("/sdcard/download/bcaching" + String.valueOf(updatedCaches) + ".gpx");
            while ((line = dis.readLine()) != null) {
                writerWrapper.write(line);
            }
            writerWrapper.close();
        }

        @Override
        public void run() {
            String now = Long.toString(System.currentTimeMillis());
            try {
                String lastUpdate = sharedPreferences.getString("bcaching_lastupdate", "");
                Log.d("GeoBeagle", "lastUpdate");
                BcachingCommunication bcachingCommunication = new BcachingCommunication("fafoofee",
                        "moocow");
                Message.obtain(handler, 3).sendToTarget();

                Hashtable<String, String> params = new Hashtable<String, String>();
                if (!lastUpdate.equals(""))
                    params.put("since", lastUpdate);
                params.put("a", "list");
                params.put("maxcount", "50");
                params.put("found", "0");
                params.put("app", "GeoBeagle");

                int updatedCaches = 0;
                int totalCount = -1; // not initialized

                while (true) {
                    if (updatedCaches > 0)
                        params.put("first", Integer.toString(updatedCaches));

                    String response = readResponse(bcachingCommunication.sendRequest(params));

                    JSONObject obj = new JSONObject(response);
                    JSONArray summary = obj.getJSONArray("data");
                    Log.d("GeoBeagle", summary.toString());

                    int count = summary.length();
                    StringBuilder csvIds = new StringBuilder();
                    for (int i = 0; i < count; i++) {
                        JSONObject cacheObject = summary.getJSONObject(i);
                        int id = cacheObject.getInt("id");
                        if (csvIds.length() > 0) {
                            csvIds.append(',');
                        }
                        csvIds.append(String.valueOf(id));
                    }

                    if (totalCount == -1) {
                        totalCount = obj.getInt("totalCount");
                        Message.obtain(handler, 0, totalCount).sendToTarget();
                        if (totalCount == 0) {
                            break;
                        }
                        Log.d("GeoBeagle", "totalCount = " + totalCount);
                    }

                    updatedCaches += count;
                    if (count < 50 || updatedCaches >= totalCount) {
                        break;
                    }
                    Message.obtain(handler, 1, updatedCaches).sendToTarget();
                    getCacheDetails(bcachingCommunication, csvIds.toString(), updatedCaches);
                }
            } catch (Exception ex) {

            }
            Message.obtain(handler, 2).sendToTarget();
            Log.d("GeoBeagle", "Setting bcaching_lastupdate to " + now);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("bcaching_lastupdate", now);
            editor.commit();
        }
    }

    private Handler handler;
    private final Activity activity;
    private final SharedPreferences sharedPreferences;

    public MenuActionSyncBCaching(Activity activity, SharedPreferences sharedPreferences) {
        super(R.string.menu_sync_bcaching);
        this.activity = activity;
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void act() {
        ProgressDialog myProgressDialog = new ProgressDialog(this.activity);
        myProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        handler = new ProgressHandler(myProgressDialog);
        new ImportBcachingWorker(handler, sharedPreferences).start();
    }
}
