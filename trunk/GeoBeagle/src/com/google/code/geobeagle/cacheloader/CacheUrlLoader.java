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

package com.google.code.geobeagle.cacheloader;

import com.google.code.geobeagle.cachedetails.FilePathStrategy;
import com.google.code.geobeagle.xmlimport.CacheTagsToUrl;
import com.google.inject.Inject;

public class CacheUrlLoader extends CacheLoader {
    @Inject
    public CacheUrlLoader(FilePathStrategy filePathStrategy,
            DetailsOpener detailsOpener,
            CacheTagsToUrl cacheTagsToUrl) {
        super(filePathStrategy, detailsOpener, cacheTagsToUrl);
    }
}
