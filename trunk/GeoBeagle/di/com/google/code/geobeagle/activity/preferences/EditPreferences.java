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

package com.google.code.geobeagle.activity.preferences;

import com.google.code.geobeagle.R;
import com.google.inject.Inject;

import roboguice.activity.GuicePreferenceActivity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

public class EditPreferences extends GuicePreferenceActivity {
    static final class FilterSettingsChangeListener implements OnPreferenceChangeListener {
        private final SharedPreferences sharedPreferences;

        @Inject
        FilterSettingsChangeListener(SharedPreferences sharedPreferences) {
            this.sharedPreferences = sharedPreferences;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Editor editor = sharedPreferences.edit();
            editor.putBoolean("filter-dirty", true);
            return true;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        Preference preference = findPreference("filter-found-caches");
        OnPreferenceChangeListener onPreferenceChangeListener = getInjector().getInstance(
                FilterSettingsChangeListener.class);
        preference.setOnPreferenceChangeListener(onPreferenceChangeListener);
    }
}
