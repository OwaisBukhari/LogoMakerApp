package com.logomaker.app.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

public class ImageElement extends LogoElement {
    
    public static final Parcelable.Creator<ImageElement> CREATOR = new Parcelable.Creator<ImageElement>() {
        @Override
        public ImageElement createFromParcel(Parcel source) {
            return new ImageElement(source);
        }

        @Override
        public ImageElement[] newArray(int size) {
            return new ImageElement[size];
        }
    };
    
    private Bitmap bitmap;
    private String imageUri;
    private float width;
    private float height;
    private boolean hasFilter;
    private int filterColor;
    private float filterAlpha;
    private Paint filterPaint;
    
    public ImageElement() {
        super();
        width = 300;
        height = 300;
        hasFilter = false;
        filterColor = Color.TRANSPARENT;
        filterAlpha = 0.5f;
        setupFilterPaint();
    }
    
    public ImageElement(Bitmap bitmap) {
        this();
        this.bitmap = bitmap;
        if (bitmap != null) {
            this.width = bitmap.getWidth();
            this.height = bitmap.getHeight();
        }
    }
    
    protected ImageElement(Parcel in) {
        super(in);
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
        imageUri = in.readString();
        width = in.readFloat();
        height = in.readFloat();
        hasFilter = in.readByte() != 0;
        filterColor = in.readInt();
        filterAlpha = in.readFloat();
        setupFilterPaint();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(bitmap, flags);
        dest.writeString(imageUri);
        dest.writeFloat(width);
        dest.writeFloat(height);
        dest.writeByte((byte) (hasFilter ? 1 : 0));
        dest.writeInt(filterColor);
        dest.writeFloat(filterAlpha);
    }
    
    private void setupFilterPaint() {
        filterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        filterPaint.setColor(filterColor);
        filterPaint.setAlpha((int) (255 * filterAlpha));
    }
    
    @Override
    public int getType() {
        return LogoElement.TYPE_IMAGE;
    }
    
    @Override
    public float getWidth() {
        return width;
    }
    
    @Override
    public float getHeight() {
        return height;
    }
    
    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (bitmap == null) {
            return;
        }
        
        canvas.save();
        canvas.concat(getTransformMatrix());
        
        // Draw the bitmap centered
        float left = -width / 2;
        float top = -height / 2;
        float right = width / 2;
        float bottom = height / 2;
        RectF destRect = new RectF(left, top, right, bottom);
        
        canvas.drawBitmap(bitmap, null, destRect, null);
        
        // Apply filter if needed
        if (hasFilter) {
            canvas.drawRect(destRect, filterPaint);
        }
        
        // Draw selection highlight if selected
        if (isSelected()) {
            Paint selectionPaint = new Paint();
            selectionPaint.setColor(Color.BLUE);
            selectionPaint.setStyle(Paint.Style.STROKE);
            selectionPaint.setStrokeWidth(2);
            canvas.drawRect(left - 5, top - 5, right + 5, bottom + 5, selectionPaint);
        }
        
        canvas.restore();
    }
    
    public Bitmap getBitmap() {
        return bitmap;
    }
    
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        if (bitmap != null) {
            // Keep aspect ratio when updating bitmap
            float aspectRatio = (float) bitmap.getWidth() / bitmap.getHeight();
            this.height = width / aspectRatio;
        }
    }
    
    public String getImageUri() {
        return imageUri;
    }
    
    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
    
    public void setWidth(float width) {
        this.width = width;
    }
    
    public void setHeight(float height) {
        this.height = height;
    }
    
    public boolean isHasFilter() {
        return hasFilter;
    }
    
    public void setHasFilter(boolean hasFilter) {
        this.hasFilter = hasFilter;
    }
    
    public int getFilterColor() {
        return filterColor;
    }
    
    public void setFilterColor(int filterColor) {
        this.filterColor = filterColor;
        filterPaint.setColor(filterColor);
        filterPaint.setAlpha((int) (255 * filterAlpha));
    }
    
    public float getFilterAlpha() {
        return filterAlpha;
    }
    
    public void setFilterAlpha(float filterAlpha) {
        this.filterAlpha = filterAlpha;
        filterPaint.setAlpha((int) (255 * filterAlpha));
    }
    
    /**
     * Resize the image keeping the aspect ratio
     * @param newWidth The new width
     */
    public void resize(float newWidth) {
        if (bitmap != null) {
            float aspectRatio = (float) bitmap.getWidth() / bitmap.getHeight();
            this.width = newWidth;
            this.height = newWidth / aspectRatio;
        } else {
            this.width = newWidth;
            this.height = newWidth;
        }
    }
} 