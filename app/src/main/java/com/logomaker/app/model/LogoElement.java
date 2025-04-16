package com.logomaker.app.model;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Base class for all elements that can be added to a logo
 */
public abstract class LogoElement implements Parcelable {
    protected String id;
    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected float rotation;
    protected float scale;
    protected int opacity;
    protected boolean locked;
    
    /**
     * Create a new logo element
     */
    public LogoElement() {
        this.id = UUID.randomUUID().toString();
        this.x = 0;
        this.y = 0;
        this.width = 100;
        this.height = 100;
        this.rotation = 0;
        this.scale = 1.0f;
        this.opacity = 255; // Fully opaque
        this.locked = false;
    }
    
    /**
     * Create a logo element from a Parcel
     * @param in Parcel containing element data
     */
    protected LogoElement(Parcel in) {
        id = in.readString();
        x = in.readFloat();
        y = in.readFloat();
        width = in.readFloat();
        height = in.readFloat();
        rotation = in.readFloat();
        scale = in.readFloat();
        opacity = in.readInt();
        locked = in.readByte() != 0;
    }
    
    /**
     * Creator to be implemented by subclasses
     */
    public static final Creator<LogoElement> CREATOR = new Creator<LogoElement>() {
        @Override
        public LogoElement createFromParcel(Parcel in) {
            // This should never be called directly as LogoElement is abstract
            throw new UnsupportedOperationException("Cannot create abstract LogoElement directly");
        }

        @Override
        public LogoElement[] newArray(int size) {
            return new LogoElement[size];
        }
    };
    
    /**
     * Write common element data to a Parcel
     * @param dest Parcel to write to
     * @param flags Flags for writing
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeFloat(width);
        dest.writeFloat(height);
        dest.writeFloat(rotation);
        dest.writeFloat(scale);
        dest.writeInt(opacity);
        dest.writeByte((byte) (locked ? 1 : 0));
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    /**
     * Draw the element on the provided canvas
     * @param canvas Canvas to draw on
     * @param paint Paint to use for drawing
     */
    public abstract void draw(Canvas canvas, Paint paint);
    
    /**
     * Get the transformation matrix for this element
     * @return Matrix containing all transformations
     */
    public Matrix getTransformMatrix() {
        Matrix matrix = new Matrix();
        matrix.postTranslate(-width / 2, -height / 2); // Center the element
        matrix.postScale(scale, scale);
        matrix.postRotate(rotation);
        matrix.postTranslate(x, y);
        return matrix;
    }
    
    /**
     * Get the bounding box of this element
     * @return RectF containing the bounds
     */
    public RectF getBounds() {
        RectF bounds = new RectF(-width / 2, -height / 2, width / 2, height / 2);
        Matrix matrix = getTransformMatrix();
        matrix.mapRect(bounds);
        return bounds;
    }
    
    /**
     * Get the element ID
     * @return Element ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * Get the X position
     * @return X position
     */
    public float getX() {
        return x;
    }
    
    /**
     * Set the X position
     * @param x New X position
     */
    public void setX(float x) {
        this.x = x;
    }
    
    /**
     * Get the Y position
     * @return Y position
     */
    public float getY() {
        return y;
    }
    
    /**
     * Set the Y position
     * @param y New Y position
     */
    public void setY(float y) {
        this.y = y;
    }
    
    /**
     * Set the position
     * @param x New X position
     * @param y New Y position
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Get the width
     * @return Width
     */
    public float getWidth() {
        return width;
    }
    
    /**
     * Set the width
     * @param width New width
     */
    public void setWidth(float width) {
        this.width = width;
    }
    
    /**
     * Get the height
     * @return Height
     */
    public float getHeight() {
        return height;
    }
    
    /**
     * Set the height
     * @param height New height
     */
    public void setHeight(float height) {
        this.height = height;
    }
    
    /**
     * Set the size
     * @param width New width
     * @param height New height
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Get the rotation
     * @return Rotation in degrees
     */
    public float getRotation() {
        return rotation;
    }
    
    /**
     * Set the rotation
     * @param rotation Rotation in degrees
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
    
    /**
     * Get the scale
     * @return Scale factor
     */
    public float getScale() {
        return scale;
    }
    
    /**
     * Set the scale
     * @param scale New scale factor
     */
    public void setScale(float scale) {
        this.scale = scale;
    }
    
    /**
     * Get the opacity
     * @return Opacity (0-255)
     */
    public int getOpacity() {
        return opacity;
    }
    
    /**
     * Set the opacity
     * @param opacity New opacity (0-255)
     */
    public void setOpacity(int opacity) {
        this.opacity = Math.max(0, Math.min(255, opacity));
    }
    
    /**
     * Check if element is locked
     * @return True if locked
     */
    public boolean isLocked() {
        return locked;
    }
    
    /**
     * Set locked state
     * @param locked New locked state
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    /**
     * Clone this element
     * @return A new instance of the element with the same properties
     */
    public abstract LogoElement clone();
} 