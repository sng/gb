package com.google.code.geobeagle;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class GraphicsGenerator {
    private final Paint mTempPaint;
    private final Rect mTempRect;
    private final RatingsGenerator mRatingsGenerator;

    public GraphicsGenerator(RatingsGenerator ratingsGenerator, Paint paint, Rect rect) {
        mTempPaint = paint;
        mTempRect = rect;
        mRatingsGenerator = ratingsGenerator;
    }

    public static class RatingsGenerator {
        public Drawable createRating(Drawable unselected, Drawable halfSelected, Drawable selected,
                int rating) {
            int width = unselected.getIntrinsicWidth();
            int height = unselected.getIntrinsicHeight();
            Bitmap bitmap = Bitmap.createBitmap(5 * width, 16, Bitmap.Config.ARGB_8888);

            Canvas c = new Canvas(bitmap);
            int i = 0;
            while (i < rating / 2) {
                draw(width, height, c, i++, selected);
            }
            if (rating % 2 == 1) {
                draw(width, height, c, i++, halfSelected);
            }
            while (i < 5) {
                draw(width, height, c, i++, unselected);
            }
            return new BitmapDrawable(bitmap);
        }

        private void draw(int width, int height, Canvas c, int i, Drawable drawable) {
            drawable.setBounds(width * i, 0, width * (i + 1) - 1, height - 1);
            drawable.draw(c);
        }
    }

    public Drawable[] getRatings(Drawable drawables[], int maxRatings) {
        Drawable[] ratings = new Drawable[maxRatings];
        for (int i = 1; i <= maxRatings; i++) {
            ratings[i - 1] = mRatingsGenerator.createRating(drawables[0], drawables[1],
                    drawables[2], i);
        }
        return ratings;
    }

    private Drawable createOverlay(Geocache geocache, int thickness, int bottom, 
            int backdropId, Drawable overlayIcon, Resources resources) {
        Bitmap bitmap = BitmapFactory.decodeResource(resources, backdropId);
        int imageHeight = bitmap.getHeight();
        int imageWidth = bitmap.getWidth();
        
        Bitmap copy;
        if (bottom >= 0) {
            copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        } else {
            copy = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight()-bottom, 
                    Bitmap.Config.ARGB_8888);
            int[] pixels = new int[imageWidth*imageHeight];
            bitmap.getPixels(pixels, 0, imageWidth, 0, 0, imageWidth, imageHeight);
            copy.setPixels(pixels, 0, imageWidth, 0, 0, imageWidth, imageHeight);
            imageHeight = copy.getHeight();
            bottom = 0;
        }

        Canvas canvas = new Canvas(copy);

        //mTempPaint.setColor(Color.BLUE);
        //mTempPaint.setARGB(255, 0x20, 0x20, 0xFF);  //light blue
        mTempPaint.setColor(Color.YELLOW);
        int diffWidth = (int)(imageWidth * (geocache.getDifficulty()/10.0));
        mTempRect.set(0, imageHeight-bottom-2*thickness-2, 
                diffWidth, imageHeight-bottom-thickness-2);
        canvas.drawRect(mTempRect, mTempPaint);
        
        mTempPaint.setARGB(255, 0xDB, 0xA1, 0x09);  //a lighter brown
        //mTempPaint.setARGB(255, 139, 94, 23);  //same color as paws
        int terrWidth = (int)(imageWidth * (geocache.getTerrain()/10.0));
        //Draw on right side:
        //mTempRect.set(imageWidth-terrWidth, imageHeight-bottom-thickness-1,
        //        imageWidth-1, imageHeight-bottom-1);
        //Draw on left side:
        mTempRect.set(0, imageHeight-bottom-thickness-1,
                terrWidth, imageHeight-bottom-1);
        canvas.drawRect(mTempRect, mTempPaint);
        
        if (overlayIcon != null) {
            overlayIcon.setBounds(imageWidth-1-overlayIcon.getIntrinsicWidth(),
                    0, imageWidth-1, overlayIcon.getIntrinsicHeight()-1);
            overlayIcon.draw(canvas);
        }
            
        return new BitmapDrawable(copy);
    }
    
    /** Creates the icon to use in the list view */
    public Drawable createIcon(Geocache geocache, Drawable overlayIcon, 
            Resources resources) {
        return createOverlay(geocache, 3, -5, geocache.getCacheType().icon(), 
                overlayIcon, resources);
    }
    
    /** Creates the icon to use in the map view */
    public Drawable createIconMap(Geocache geocache, Drawable overlayIcon, 
            Resources resources) {
        Drawable iconMap = createOverlay(geocache, 3, 3, 
                geocache.getCacheType().iconMap(), overlayIcon, resources);
        int width = iconMap.getIntrinsicWidth();
        int height = iconMap.getIntrinsicHeight();
        iconMap.setBounds(-width/2, -height, width/2, 0);
        return iconMap;
    }

    /** Returns a new Drawable that is 'top' over 'bottom'. 
     * Top is assumed to be smaller and is centered over bottom. */
    public Drawable superimpose(Drawable top, Drawable bottom) {
        int width = bottom.getIntrinsicWidth();
        int height = bottom.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        bottom.setBounds(0, 0, width-1, height-1);
        int topWidth = top.getIntrinsicWidth();
        int topHeight = top.getIntrinsicHeight();
        top.setBounds(width/2 - topWidth/2, height/2 - topHeight/2, 
                width/2 - topWidth/2 + topWidth, height/2 - topHeight/2 + topHeight);
        bottom.draw(c);
        top.draw(c);
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        bd.setBounds(-width/2, -height, width/2, 0);  //Necessary!
        return bd;
    }
    
}
