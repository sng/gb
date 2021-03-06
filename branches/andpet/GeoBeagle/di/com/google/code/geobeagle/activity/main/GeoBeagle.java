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

package com.google.code.geobeagle.activity.main;

import com.google.code.geobeagle.ErrorDisplayer;
import com.google.code.geobeagle.ErrorDisplayerDi;
import com.google.code.geobeagle.GeoFixProvider;
import com.google.code.geobeagle.Geocache;
import com.google.code.geobeagle.GeocacheFactory;
import com.google.code.geobeagle.GraphicsGenerator;
import com.google.code.geobeagle.LocationControlDi;
import com.google.code.geobeagle.R;
import com.google.code.geobeagle.R.id;
import com.google.code.geobeagle.actions.MenuActionSettings;
import com.google.code.geobeagle.actions.CacheActionEdit;
import com.google.code.geobeagle.actions.CacheActionMap;
import com.google.code.geobeagle.actions.CacheActionRadar;
import com.google.code.geobeagle.actions.CacheActionViewUri;
import com.google.code.geobeagle.actions.MenuAction;
import com.google.code.geobeagle.actions.MenuActionCacheList;
import com.google.code.geobeagle.actions.CacheActionGoogleMaps;
import com.google.code.geobeagle.actions.MenuActionFromCacheAction;
import com.google.code.geobeagle.actions.MenuActions;
import com.google.code.geobeagle.activity.ActivityDI;
import com.google.code.geobeagle.activity.ActivitySaver;
import com.google.code.geobeagle.activity.main.fieldnotes.CacheLogger;
import com.google.code.geobeagle.activity.main.DateFormatter;
import com.google.code.geobeagle.activity.main.fieldnotes.DialogHelperCommon;
import com.google.code.geobeagle.activity.main.fieldnotes.DialogHelperFile;
import com.google.code.geobeagle.activity.main.fieldnotes.DialogHelperSms;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteLogger;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteStringsFVsDnf;
import com.google.code.geobeagle.activity.main.fieldnotes.FileLogger;
import com.google.code.geobeagle.activity.main.fieldnotes.SmsLogger;

import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteLogger.OnClickCancel;
import com.google.code.geobeagle.activity.main.fieldnotes.FieldnoteLogger.OnClickOk;import com.google.code.geobeagle.activity.main.GeoBeagleDelegate.LogFindClickListener;
import com.google.code.geobeagle.activity.main.GeoBeagleDelegate.OptionsMenu;
import com.google.code.geobeagle.activity.main.intents.GeocacheToCachePage;
import com.google.code.geobeagle.activity.main.intents.GeocacheToGoogleMap;
import com.google.code.geobeagle.activity.main.intents.IntentFactory;

import com.google.code.geobeagle.activity.main.view.CacheButtonOnClickListener;
import com.google.code.geobeagle.activity.main.view.CacheDetailsOnClickListener;
import com.google.code.geobeagle.activity.main.view.FavoriteView;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer;
import com.google.code.geobeagle.activity.main.view.Misc;
import com.google.code.geobeagle.activity.main.view.WebPageAndDetailsButtonEnabler;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.AttributeViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.UnlabelledAttributeViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.LabelledAttributeViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.NameViewer;
import com.google.code.geobeagle.activity.main.view.GeocacheViewer.ResourceImages;
import com.google.code.geobeagle.database.DbFrontend;
import com.google.code.geobeagle.Toaster;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//TODO: Rename to CompassActivity
/*
 * Main Activity for GeoBeagle.
 */
public class GeoBeagle extends Activity {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
    "yyyy-MM-dd'T'HH:mm'Z'");
    private GeoBeagleDelegate mGeoBeagleDelegate;
    private DbFrontend mDbFrontend;
    private FieldnoteLogger mFieldNoteSender;
    private OptionsMenu mOptionsMenu;
    private static final DateFormat mLocalDateFormat = DateFormat
    .getTimeInstance(DateFormat.MEDIUM);
    private GeocacheFactory mGeocacheFactory;
    
    public Geocache getGeocache() {
        return mGeoBeagleDelegate.getGeocache();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("GeoBeagle", "GeoBeagle.onActivityResult");
        mGeocacheFactory.flushCache();
        if (requestCode == GeoBeagleDelegate.ACTIVITY_REQUEST_TAKE_PICTURE) {
            Log.d("GeoBeagle", "camera intent has returned.");
        } else if (resultCode == 0)
            setIntent(data);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("GeoBeagle", "GeoBeagle onCreate");

        setContentView(R.layout.main);
        final ErrorDisplayer errorDisplayer = ErrorDisplayerDi.createErrorDisplayer(this);
        final WebPageAndDetailsButtonEnabler webPageButtonEnabler = Misc.create(this,
                findViewById(R.id.cache_page), findViewById(R.id.cache_details));

        final GeoFixProvider geoFixProvider = LocationControlDi.create(this);
        mGeocacheFactory = new GeocacheFactory();
        final TextView gcid = (TextView)findViewById(R.id.gcid);
        final GraphicsGenerator graphicsGenerator = new GraphicsGenerator(
                new GraphicsGenerator.RatingsGenerator(), null);
        final Resources resources = this.getResources();
        
        final Drawable[] pawDrawables = {
                resources.getDrawable(R.drawable.paw_unselected_dark),
                resources.getDrawable(R.drawable.paw_half_light),
                resources.getDrawable(R.drawable.paw_selected_light)
        };
        final Drawable[] pawImages = graphicsGenerator.getRatings(pawDrawables, 10);
        final Drawable[] ribbonDrawables = {
                resources.getDrawable(R.drawable.ribbon_unselected_dark),
                resources.getDrawable(R.drawable.ribbon_half_bright),
                resources.getDrawable(R.drawable.ribbon_selected_bright)
        };
        final Drawable[] ribbonImages = graphicsGenerator.getRatings(ribbonDrawables, 10);
        final ImageView difficultyImageView = (ImageView)findViewById(R.id.gc_difficulty);
        final TextView terrainTextView = (TextView)findViewById(R.id.gc_text_terrain);
        final ImageView terrainImageView = (ImageView)findViewById(R.id.gc_terrain);
        final TextView difficultyTextView = (TextView)findViewById(R.id.gc_text_difficulty);
        final ImageView containerImageView = (ImageView)findViewById(R.id.gccontainer);
        final UnlabelledAttributeViewer ribbonImagesOnDifficulty = new UnlabelledAttributeViewer(
                difficultyImageView, ribbonImages);
        final AttributeViewer gcDifficulty = new LabelledAttributeViewer(
                difficultyTextView, ribbonImagesOnDifficulty);
        final UnlabelledAttributeViewer pawImagesOnTerrain = new UnlabelledAttributeViewer(
                terrainImageView, pawImages);
        final AttributeViewer gcTerrain = new LabelledAttributeViewer(terrainTextView,
                pawImagesOnTerrain);
        final ResourceImages containerImagesOnContainer = new ResourceImages(
                containerImageView, GeocacheViewer.CONTAINER_IMAGES);
        
        final NameViewer gcName = new NameViewer(
                ((TextView)findViewById(R.id.gcname)));
        RadarView radar = (RadarView)findViewById(R.id.radarview);
        radar.setUseImperial(false);
        radar.setDistanceView((TextView)findViewById(R.id.radar_distance),
                (TextView)findViewById(R.id.radar_bearing),
                (TextView)findViewById(R.id.radar_accuracy),
                (TextView)findViewById(R.id.radar_lag));
        FavoriteView favorite = (FavoriteView) findViewById(R.id.gcfavorite);
        final GeocacheViewer geocacheViewer = new GeocacheViewer(radar, gcid, gcName,
                (ImageView)findViewById(R.id.gcicon),
                gcDifficulty, gcTerrain, containerImagesOnContainer/*, favorite*/);

        //geoFixProvider.onLocationChanged(null);
        GeoBeagleDelegate.RadarViewRefresher radarViewRefresher = 
            new GeoBeagleDelegate.RadarViewRefresher(radar, geoFixProvider);
        geoFixProvider.addObserver(radarViewRefresher);
        final IntentFactory intentFactory = new IntentFactory(new UriParser());

        final CacheActionViewUri intentStarterViewUri = new CacheActionViewUri(this,
                intentFactory, new GeocacheToGoogleMap(this), resources);
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        final ActivitySaver activitySaver = ActivityDI.createActivitySaver(this);
        mDbFrontend = new DbFrontend(this, mGeocacheFactory);
        final GeocacheFromIntentFactory geocacheFromIntentFactory = new GeocacheFromIntentFactory(
                mGeocacheFactory, mDbFrontend);
        final IncomingIntentHandler incomingIntentHandler = new IncomingIntentHandler(
                mGeocacheFactory, geocacheFromIntentFactory, mDbFrontend);
        Geocache geocache = incomingIntentHandler.maybeGetGeocacheFromIntent(getIntent(), null, mDbFrontend);

        final SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        mGeoBeagleDelegate = new GeoBeagleDelegate(activitySaver,
                this, mGeocacheFactory, geocacheViewer,
                incomingIntentHandler,
                mDbFrontend, radar, defaultSharedPreferences,
                webPageButtonEnabler, geoFixProvider, favorite);

        final MenuAction[] menuActionArray = {
                new MenuActionCacheList(this, resources), 
                new MenuActionFromCacheAction(new CacheActionEdit(this, resources), geocache),
//                new MenuActionLogDnf(this), new MenuActionLogFind(this),
                //new MenuActionSearchOnline(this), 
                new MenuActionSettings(this, resources),
                new MenuActionFromCacheAction(new CacheActionGoogleMaps(intentStarterViewUri, resources), geocache),
                //new MenuActionFromCacheAction(new CacheActionProximity(this, resources), geocache),
        };
        final MenuActions menuActions = new MenuActions(menuActionArray);
        mOptionsMenu = new GeoBeagleDelegate.OptionsMenu(menuActions);
        
        // see http://www.androidguys.com/2008/11/07/rotational-forces-part-two/
        if (getLastNonConfigurationInstance() != null) {
            setIntent((Intent)getLastNonConfigurationInstance());
        }

        final CacheActionMap cacheActionMap = new CacheActionMap(this, resources);
        final CacheButtonOnClickListener mapsButtonOnClickListener = 
            new CacheButtonOnClickListener(cacheActionMap, this, "Map error", errorDisplayer);
        findViewById(id.maps).setOnClickListener(mapsButtonOnClickListener);

        final AlertDialog.Builder cacheDetailsBuilder = new AlertDialog.Builder(this);
        final CacheDetailsOnClickListener cacheDetailsOnClickListener = Misc
                .createCacheDetailsOnClickListener(this, cacheDetailsBuilder, layoutInflater);

        findViewById(R.id.cache_details).setOnClickListener(cacheDetailsOnClickListener);

        final GeocacheToCachePage geocacheToCachePage = new GeocacheToCachePage(getResources());
        final CacheActionViewUri cachePageIntentStarter = new CacheActionViewUri(this,
                intentFactory, geocacheToCachePage, resources);
        final CacheButtonOnClickListener cacheButtonOnClickListener = 
            new CacheButtonOnClickListener(cachePageIntentStarter, this, "", errorDisplayer);
        findViewById(id.cache_page).setOnClickListener(cacheButtonOnClickListener);

        findViewById(id.radarview).setOnClickListener(new CacheButtonOnClickListener(
                new CacheActionRadar(this, resources), this, "Please install the Radar application to use Radar.", 
                errorDisplayer));

        findViewById(id.menu_log_find).setOnClickListener(
                new LogFindClickListener(this, id.menu_log_find));
        findViewById(id.menu_log_dnf).setOnClickListener(
                new LogFindClickListener(this, id.menu_log_dnf));
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        super.onCreateDialog(id);
        final FieldnoteStringsFVsDnf fieldnoteStringsFVsDnf = new FieldnoteStringsFVsDnf(
                getResources());
        final Toaster toaster = new Toaster(this, R.string.error_writing_cache_log,
                Toast.LENGTH_LONG);
        final DateFormatter dateFormatter = new DateFormatter(simpleDateFormat);
        final FileLogger fileLogger = new FileLogger(fieldnoteStringsFVsDnf, dateFormatter, toaster);
        final SmsLogger smsLogger = new SmsLogger(fieldnoteStringsFVsDnf, this);
        final SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        final CacheLogger cacheLogger = new CacheLogger(defaultSharedPreferences, fileLogger,
                smsLogger);
        final OnClickCancel onClickCancel = new OnClickCancel();
        final LayoutInflater layoutInflater = LayoutInflater.from(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View fieldNoteDialogView = layoutInflater.inflate(R.layout.fieldnote, null);
        final TextView fieldnoteCaveat = (TextView)fieldNoteDialogView
                .findViewById(R.id.fieldnote_caveat);

        final CharSequence geocacheId = mGeoBeagleDelegate.getGeocache().getId();
        final boolean fDnf = id == R.id.menu_log_dnf;
        final EditText editText = (EditText)fieldNoteDialogView.findViewById(R.id.fieldnote);
        final DialogHelperCommon dialogHelperCommon = new DialogHelperCommon(
                fieldnoteStringsFVsDnf, editText, fDnf, fieldnoteCaveat);

        final DialogHelperFile dialogHelperFile = new DialogHelperFile(fieldnoteCaveat, this);
        final DialogHelperSms dialogHelperSms = new DialogHelperSms(geocacheId.length(),
                fieldnoteStringsFVsDnf, editText, fDnf, fieldnoteCaveat);

        mFieldNoteSender = new FieldnoteLogger(dialogHelperCommon, dialogHelperFile,
                dialogHelperSms);

        final OnClickOk onClickOk = new OnClickOk(geocacheId, editText, cacheLogger, mDbFrontend, fDnf);
        builder.setTitle(R.string.field_note_title);
        builder.setView(fieldNoteDialogView);
        builder.setNegativeButton(R.string.cancel, onClickCancel);
        builder.setPositiveButton(R.string.log_cache, onClickOk);
        return builder.create();
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onCreateDialog(id);
        final SharedPreferences defaultSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        mFieldNoteSender.onPrepareDialog(dialog, defaultSharedPreferences, mLocalDateFormat
                .format(new Date()));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return mOptionsMenu.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mGeoBeagleDelegate.onKeyDown(keyCode, event))
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mOptionsMenu.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("GeoBeagle", "GeoBeagle onPause");
        mGeoBeagleDelegate.onPause();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mGeoBeagleDelegate.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeoBeagle", "GeoBeagle onResume");
        mGeoBeagleDelegate.onResume();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onRetainNonConfigurationInstance()
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        return getIntent();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mGeoBeagleDelegate.onSaveInstanceState(outState);
    }
}
