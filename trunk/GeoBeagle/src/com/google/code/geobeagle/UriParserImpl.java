package com.google.code.geobeagle;

import android.content.Intent;
import android.net.Uri;

public class UriParserImpl implements UriParser {

	public Uri parse(String uriString) {
		return Uri.parse(uriString);
	}

	public Intent createIntent(String actionView, Uri uri) {
		return new Intent(actionView, uri);
	}

}
