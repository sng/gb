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

package com.google.code.geobeagle;

import static org.junit.Assert.assertEquals;

import com.google.code.geobeagle.GraphicsGenerator.AttributePainter;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {
        Bitmap.class, Canvas.class, Color.class, GraphicsGenerator.class, Rect.class
})
public class GraphicsGeneratorTest {

    @Test
    public void testCreateIconMapView() throws Exception {
        PowerMock.mockStatic(BitmapFactory.class);
        Resources resources = PowerMock.createMock(Resources.class);
        Bitmap bitmap = PowerMock.createMock(Bitmap.class);
        Bitmap copy = PowerMock.createMock(Bitmap.class);
        Canvas canvas = PowerMock.createMock(Canvas.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        BitmapDrawable bitmapDrawable = PowerMock.createMock(BitmapDrawable.class);
        AttributePainter attributePainter = PowerMock.createMock(AttributePainter.class);
        PowerMock.mockStatic(Color.class);
        
        EasyMock.expect(geocache.getCacheType()).andReturn(CacheType.EARTHCACHE);
        EasyMock
                .expect(
                        BitmapFactory.decodeResource(resources,
                                R.drawable.pin_earth)).andReturn(bitmap);
        EasyMock.expect(bitmap.getHeight()).andReturn(100).anyTimes();
        EasyMock.expect(copy.getWidth()).andReturn(200);
        EasyMock.expect(bitmap.copy(Bitmap.Config.ARGB_8888, true)).andReturn(
                copy);
        EasyMock.expect(copy.getHeight()).andReturn(100);
        PowerMock.expectNew(Canvas.class, copy).andReturn(canvas);
        EasyMock.expect(geocache.getDifficulty()).andReturn(8);
        EasyMock.expect(Color.argb(255, 0x20, 0x20, 0xFF)).andReturn(27);
        attributePainter.drawAttribute(1, 3, 100, 200, canvas, 8, 27);

        EasyMock.expect(geocache.getTerrain()).andReturn(6);
        EasyMock.expect(Color.argb(255, 0xDB, 0xA1, 0x09)).andReturn(99);
        attributePainter.drawAttribute(0, 3, 100, 200, canvas, 6, 99);        
        PowerMock.expectNew(BitmapDrawable.class, copy).andReturn(bitmapDrawable);
        EasyMock.expect(bitmapDrawable.getIntrinsicWidth()).andReturn(110);
        EasyMock.expect(bitmapDrawable.getIntrinsicHeight()).andReturn(220);
        bitmapDrawable.setBounds(-55, -220, 55, 0);
        
        PowerMock.replayAll();
        new GraphicsGenerator(null, null).createIconMapView(
                geocache, null, resources);
        PowerMock.verifyAll();
    }

    @Test
    public void testCreateIconListView() throws Exception {
        PowerMock.mockStatic(BitmapFactory.class);
        Resources resources = PowerMock.createMock(Resources.class);
        Bitmap resourceBitmap = PowerMock.createMock(Bitmap.class);
        Bitmap copy = PowerMock.createMock(Bitmap.class);
        Canvas canvas = PowerMock.createMock(Canvas.class);
        Geocache geocache = PowerMock.createMock(Geocache.class);
        BitmapDrawable bitmapDrawable = PowerMock.createMock(BitmapDrawable.class);
        AttributePainter attributePainter = PowerMock.createMock(AttributePainter.class);
        PowerMock.mockStatic(Color.class);
        PowerMock.mockStatic(Bitmap.class);
        
        EasyMock.expect(geocache.getCacheType()).andReturn(CacheType.EARTHCACHE);
        EasyMock
                .expect(
                        BitmapFactory.decodeResource(resources,
                                R.drawable.cache_earth)).andReturn(resourceBitmap);
        EasyMock.expect(resourceBitmap.getHeight()).andReturn(100).anyTimes();
        EasyMock.expect(resourceBitmap.getWidth()).andReturn(200).anyTimes();
        EasyMock.expect(Bitmap.createBitmap(200, 105, Bitmap.Config.ARGB_8888)).andReturn(copy);
        resourceBitmap.getPixels((int[])EasyMock.anyObject(), EasyMock.eq(0),
                EasyMock.eq(200), EasyMock.eq(0), EasyMock.eq(0), EasyMock
                        .eq(200), EasyMock.eq(100));
        copy.setPixels((int[])EasyMock.anyObject(), EasyMock.eq(0),
                EasyMock.eq(200), EasyMock.eq(0), EasyMock.eq(0), EasyMock
                .eq(200), EasyMock.eq(100));
        EasyMock.expect(copy.getHeight()).andReturn(105);
        EasyMock.expect(copy.getWidth()).andReturn(200);
        
        PowerMock.expectNew(Canvas.class, copy).andReturn(canvas);
        EasyMock.expect(geocache.getDifficulty()).andReturn(8);
        EasyMock.expect(Color.argb(255, 0x20, 0x20, 0xFF)).andReturn(27);
        attributePainter.drawAttribute(1, 0, 105, 200, canvas, 8, 27);

        EasyMock.expect(geocache.getTerrain()).andReturn(6);
        EasyMock.expect(Color.argb(255, 0xDB, 0xA1, 0x09)).andReturn(99);
        attributePainter.drawAttribute(0, 0, 105, 200, canvas, 6, 99);
        PowerMock.expectNew(BitmapDrawable.class, copy).andReturn(bitmapDrawable);
        PowerMock.replayAll();

        assertEquals(bitmapDrawable, new GraphicsGenerator(null,
                null).createIconListView(geocache, null, resources));
        PowerMock.verifyAll();
    }

    @Test
    public void testAttributePainter() {
        Paint tempPaint = PowerMock.createMock(Paint.class);
        Rect tempRect = PowerMock.createMock(Rect.class);
        Canvas canvas = PowerMock.createMock(Canvas.class);

        tempPaint.setColor(27);
        tempRect.set(0, 88, 160, 91);
        canvas.drawRect(tempRect, tempPaint);
        
        tempPaint.setColor(99);
        tempRect.set(0, 92, 120, 95);
        canvas.drawRect(tempRect, tempPaint);
        
        PowerMock.replayAll();
        AttributePainter attributePainter = new AttributePainter(tempPaint, tempRect);
        attributePainter.drawAttribute(1, 4, 100, 200, canvas, 8, 27);
        attributePainter.drawAttribute(0, 4, 100, 200, canvas, 6, 99);
        PowerMock.verifyAll();
    }
    
    @Test
    public void testCreateRating3() throws Exception {
        Drawable unselected = PowerMock.createMock(Drawable.class);
        Drawable halfSelected = PowerMock.createMock(Drawable.class);
        Drawable selected = PowerMock.createMock(Drawable.class);
        Bitmap bitmap = PowerMock.createMock(Bitmap.class);
        Canvas canvas = PowerMock.createMock(Canvas.class);
        BitmapDrawable bitmapDrawable = PowerMock
                .createMock(BitmapDrawable.class);

        EasyMock.expect(unselected.getIntrinsicWidth()).andReturn(10);
        EasyMock.expect(unselected.getIntrinsicHeight()).andReturn(5);
        PowerMock.mockStatic(Bitmap.class);
        EasyMock.expect(Bitmap.createBitmap(50, 16, Bitmap.Config.ARGB_8888))
                .andReturn(bitmap);
        PowerMock.expectNew(Canvas.class, bitmap).andReturn(canvas);

        selected.setBounds(0, 0, 9, 4);
        selected.draw(canvas);
        halfSelected.setBounds(10, 0, 19, 4);
        halfSelected.draw(canvas);
        unselected.setBounds(20, 0, 29, 4);
        unselected.draw(canvas);
        unselected.setBounds(30, 0, 39, 4);
        unselected.draw(canvas);
        unselected.setBounds(40, 0, 49, 4);
        unselected.draw(canvas);

        PowerMock.expectNew(BitmapDrawable.class, bitmap).andReturn(
                bitmapDrawable);

        PowerMock.replayAll();
        new GraphicsGenerator.RatingsGenerator().createRating(unselected,
                halfSelected, selected, 3);
        PowerMock.verifyAll();
    }

    @Test
    public void testCreateRating1() throws Exception {
        Drawable unselected = PowerMock.createMock(Drawable.class);
        Drawable halfSelected = PowerMock.createMock(Drawable.class);
        Drawable selected = PowerMock.createMock(Drawable.class);
        Bitmap bitmap = PowerMock.createMock(Bitmap.class);
        Canvas canvas = PowerMock.createMock(Canvas.class);
        BitmapDrawable bitmapDrawable = PowerMock
                .createMock(BitmapDrawable.class);

        EasyMock.expect(unselected.getIntrinsicWidth()).andReturn(10);
        EasyMock.expect(unselected.getIntrinsicHeight()).andReturn(5);
        PowerMock.mockStatic(Bitmap.class);
        EasyMock.expect(Bitmap.createBitmap(50, 16, Bitmap.Config.ARGB_8888))
                .andReturn(bitmap);
        PowerMock.expectNew(Canvas.class, bitmap).andReturn(canvas);

        halfSelected.setBounds(0, 0, 9, 4);
        halfSelected.draw(canvas);
        unselected.setBounds(10, 0, 19, 4);
        unselected.draw(canvas);
        unselected.setBounds(20, 0, 29, 4);
        unselected.draw(canvas);
        unselected.setBounds(30, 0, 39, 4);
        unselected.draw(canvas);
        unselected.setBounds(40, 0, 49, 4);
        unselected.draw(canvas);

        PowerMock.expectNew(BitmapDrawable.class, bitmap).andReturn(
                bitmapDrawable);

        PowerMock.replayAll();
        new GraphicsGenerator.RatingsGenerator().createRating(unselected,
                halfSelected, selected, 1);
        PowerMock.verifyAll();
    }

    @Test
    public void testGetRatings() {
        Drawable drawable0 = PowerMock.createMock(Drawable.class);
        Drawable drawable1 = PowerMock.createMock(Drawable.class);
        Drawable drawable2 = PowerMock.createMock(Drawable.class);
        Drawable drawables[] = {
                drawable0, drawable1, drawable2
        };
        GraphicsGenerator.RatingsGenerator ratingsGenerator = PowerMock
                .createMock(GraphicsGenerator.RatingsGenerator.class);
        Drawable rating = PowerMock.createMock(Drawable.class);

        EasyMock.expect(
                ratingsGenerator.createRating(drawable0, drawable1, drawable2,
                        1)).andReturn(null);
        EasyMock.expect(
                ratingsGenerator.createRating(drawable0, drawable1, drawable2,
                        2)).andReturn(rating);
        PowerMock.replayAll();
        
        Drawable ratings[] = new GraphicsGenerator(ratingsGenerator,
                null).getRatings(drawables, 2);
        assertEquals(rating, ratings[1]);
        PowerMock.verifyAll();
    }
}
