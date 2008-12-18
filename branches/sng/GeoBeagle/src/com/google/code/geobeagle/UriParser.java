
package com.google.code.geobeagle;

import android.content.Intent;
import android.net.Uri;

public interface UriParser {
    Uri parse(String format);

    Intent createIntent(String actionView, Uri parse);
}
