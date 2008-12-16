
package com.google.code.geobeagle;

import android.location.Location;
import android.view.View.OnClickListener;

public interface LocationViewer {

    void setLocation(Location location, long time);

    void setLocation(Location location);

    void setStatus(int status);

    String getLocation();

    void setOnClickListener(OnClickListener onClickListener);
}
