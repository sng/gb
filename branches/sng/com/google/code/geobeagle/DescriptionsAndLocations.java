package com.google.code.geobeagle;

import java.util.ArrayList;
import java.util.List;

public class DescriptionsAndLocations {
	private List<CharSequence> previousDescriptions;
	private List<CharSequence> previousLocations;
	private int maxSize;

	public DescriptionsAndLocations() {
		create(25);
	}
	
	public DescriptionsAndLocations(int maxSize) {
		create(maxSize);
	}

	private void create(int maxSize) {
		previousDescriptions = new ArrayList<CharSequence>();
		previousLocations = new ArrayList<CharSequence>();
		this.maxSize = maxSize;
	}

	public void add(CharSequence description, CharSequence location) {
		final int ix = previousDescriptions.indexOf(description);
		if (ix >= 0) {
			remove(ix);
		}
		
		previousDescriptions.add(description);
		previousLocations.add(location);
		if (previousDescriptions.size() > maxSize) {
			remove(0);
		}
	}

	public void clear() {
		previousDescriptions.clear();
		previousLocations.clear();
	}

	public List<CharSequence> getPreviousDescriptions() {
		return previousDescriptions;
	}

	public List<CharSequence> getPreviousLocations() {
		return previousLocations;
	}

	private void remove(int i) {
		previousLocations.remove(i);
		previousDescriptions.remove(i);		
	}
}