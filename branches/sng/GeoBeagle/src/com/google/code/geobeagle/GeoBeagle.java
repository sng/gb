
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

    private GpsLocationListener gpsLocationListener;

    private LocationSetter locationSetter;

    private LocationViewer locationViewer;

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
            getCoordinatesFromIntent(locationSetter, intent, mDlgError);
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
            locationSetter = new LocationSetterImpl(this, new MockableEditText(txtLocation),
                    gpsControl);
            mDlgError = createErrorDialog();

            locationViewer = new LocationViewerImpl(new MockableButton(
                    (Button)findViewById(R.id.location_viewer_caption)), new MockableTextView(
                    (TextView)findViewById(R.id.location_viewer)), gpsControl.getLocation());
            locationViewer.setOnClickListener(new LocationViewerImpl.LocationViewerOnClickListener(
                    locationViewer, locationSetter));
            gpsLocationListener = new GpsLocationListener(locationViewer, this);

            setOnClickListeners(locationSetter);

            final Button btnGoToList = (Button)findViewById(R.id.go_to_list);
            btnGoToList.setOnClickListener(new DestinationListOnClickListener(locationSetter
                    .getDescriptionsAndLocations(), locationSetter, new AlertDialog.Builder(this)));
        } catch (final Exception e) {
            ((TextView)findViewById(R.id.debug)).setText(e.toString() + "\n"
                    + Util.getStackTrace(e));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        GpsControlImpl.onPause(this, gpsLocationListener);
        final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString(sPrefsLocation, locationSetter.getLocation().toString());
        editor.commit();
        locationSetter.save(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GpsControlImpl.onResume(this, gpsLocationListener);
        locationSetter.load(this);
        if (!maybeGetCoordinatesFromIntent()) {
            locationSetter.setLocation(getPreferences(MODE_PRIVATE).getString(sPrefsLocation,
                    getString(R.string.initial_destination)));
        }
    }

    private void setOnClickListener(final int id, final IntentCreator intentCreator) {
        ((Button)findViewById(id)).setOnClickListener(new OnActivityButtonLinkClickListener(this,
                mDlgError, locationSetter, intentCreator));
    }

    private void setOnClickListeners(final LocationSetter controls) {
        setOnClickListener(R.id.radar, new Radar.RadarIntentCreator());
        setOnClickListener(R.id.geocaching_map, new GeocachingMapsIntentCreator());
        setOnClickListener(R.id.nearest_caches, new NearestCachesIntentCreator());
        setOnClickListener(R.id.maps, new MapsIntentCreator());
        setOnClickListener(R.id.cache_page, new CachePageIntentCreator());
    }

}
