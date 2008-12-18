
package com.google.code.geobeagle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GeoBeagle extends Activity {
    private static final String sPrefsLocation = "Location";
    private AlertDialog mDlgError;
    private GpsLocationListener mGpsLocationListener;
    private LocationSetter mLocationSetter;
    private LocationViewer mLocationViewer;
    private ErrorDisplayerImpl mErrorDisplayer;

    private AlertDialog createErrorDialog() {
        return new AlertDialog.Builder(this).setNeutralButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int which) {
                    }
                }).create();
    }

    private void getCoordinatesFromIntent(LocationSetter locationSetter, Intent intent,
            AlertDialog alertDialog) {
        try {
            final String query = intent.getData().getQuery();
            final String sanitizedQuery = Util.parseHttpUri(query, new UrlQuerySanitizer(),
                    UrlQuerySanitizer.getAllButNulAndAngleBracketsLegal());
            final String[] latlon = Util.getLatLonFromQuery(sanitizedQuery);
            locationSetter.setLocation(Util.minutesToDegrees(latlon[0]), Util
                    .minutesToDegrees(latlon[1]), latlon[2]);
            // startActivityForResult(new RadarIntentCreator().createIntent(new
            // LatLong(ll)), -1);
        } catch (final Exception e) {
            alertDialog.setMessage("Error: " + e.getMessage());
            alertDialog.show();
            startActivity(new Intent(Intent.ACTION_VIEW, intent.getData()));
        }
    }

    private boolean maybeGetCoordinatesFromIntent() {
        final Intent intent = getIntent();
        final String action = intent.getAction();
        if ((action != null) && action.equals(Intent.ACTION_VIEW)) {
            getCoordinatesFromIntent(mLocationSetter, intent, mDlgError);
            return true;
        }
        return false;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);
            GpsControl gpsControl = new GpsControlImpl(this);
            final EditText txtLocation = (EditText)findViewById(R.id.go_to);
            txtLocation.setOnKeyListener(new LocationOnKeyListener(
                    (Button)findViewById(R.id.cache_page), new TooString(txtLocation)));
            mLocationSetter = new LocationSetterImpl(this, new MockableEditText(txtLocation),
                    gpsControl);
            mDlgError = createErrorDialog();
            mLocationViewer = new LocationViewerImpl(new MockableTextView(
                    (TextView)findViewById(R.id.location_viewer)), new MockableTextView(
                    (TextView)findViewById(R.id.last_updated)), new MockableTextView(
                    (TextView)findViewById(R.id.status)), gpsControl.getLocation());
            mGpsLocationListener = new GpsLocationListener(mLocationViewer, this);
            setOnClickListeners(mLocationSetter);
            final Button btnGoToList = (Button)findViewById(R.id.go_to_list);
            mErrorDisplayer = new ErrorDisplayerImpl(this);
            btnGoToList.setOnClickListener(new DestinationListOnClickListener(mLocationSetter
                    .getDescriptionsAndLocations(), mLocationSetter, new AlertDialog.Builder(this),
                    mErrorDisplayer));
        } catch (final Exception e) {
            ((TextView)findViewById(R.id.debug)).setText(e.toString() + "\n"
                    + Util.getStackTrace(e));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        GpsControlImpl.onPause(this, mGpsLocationListener);
        final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString(sPrefsLocation, mLocationSetter.getLocation().toString());
        editor.commit();
        mLocationSetter.save(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GpsControlImpl.onResume(this, mGpsLocationListener);
        mLocationSetter.load(this);
        if (!maybeGetCoordinatesFromIntent()) {
            mLocationSetter.setLocation(getPreferences(MODE_PRIVATE).getString(sPrefsLocation,
                    getString(R.string.initial_destination)), mErrorDisplayer);
        }
    }

    private void setOnClickListener(final int id, final IntentCreator intentCreator) {
        ((Button)findViewById(id)).setOnClickListener(new OnActivityButtonLinkClickListener(this,
                mDlgError, mLocationSetter, intentCreator));
    }

    private void setOnClickListeners(final LocationSetter controls) {
        setOnClickListener(R.id.radar, new Radar.RadarIntentCreator());
        setOnClickListener(R.id.geocaching_map, new GeocachingMapsIntentCreator());
        setOnClickListener(R.id.nearest_caches, new NearestCachesIntentCreator());
        setOnClickListener(R.id.maps, new MapsIntentCreator());
        setOnClickListener(R.id.cache_page, new CachePageIntentCreator());
    }
}
