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

package com.google.code.geobeagle.activity.compass;

import com.google.code.geobeagle.R;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CompassFragment extends Fragment {
    static int counter = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("GeoBeagle", "CompassFragment::onCreateView: " + counter);
        View inflatedView = inflater.inflate(R.layout.compass, container, false);
        TextView details = (TextView)inflatedView.findViewById(R.id.gcname);
        Bundle arguments = getArguments();
        details.setText("counter: " + counter++);
        if (arguments != null)
            details.setText(arguments.getString("name"));
        return inflatedView;
    }
}
