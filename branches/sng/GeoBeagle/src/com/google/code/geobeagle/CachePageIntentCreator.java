package com.google.code.geobeagle;

import android.content.Intent;

public class CachePageIntentCreator implements IntentCreator {
	private final UriParser uriParser;

	public CachePageIntentCreator() {
		uriParser = new UriParserImpl();
	}

	public CachePageIntentCreator(UriParser uriParser) {
		this.uriParser = uriParser;
	}

	public Intent createIntent(Destination destination) {
		return uriParser.createIntent(Intent.ACTION_VIEW, uriParser.parse(String.format(
				"http://coord.info/%1$s", destination.getDescription())));
	}
}
