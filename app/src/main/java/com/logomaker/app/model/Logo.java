package com.logomaker.app.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a logo with all its elements
 */
public class Logo implements Parcelable {
    private String name;
    private int width;
    private int height;
    private int backgroundColor;
    private final List<LogoElement> elements;
    private String id;
    private long lastModified;
    private String thumbnailPath;

    /**
     * Create a new logo with default values
     */
    public Logo() {
        this.name = "Untitled Logo";
        this.width = 1000;
        this.height = 1000;
        this.backgroundColor = Color.WHITE;
        this.elements = new ArrayList<>();
        this.id = String.valueOf(System.currentTimeMillis());
        this.lastModified = System.currentTimeMillis();
    }

    /**
     * Create a new logo with specified dimensions
     * @param width Logo width
     * @param height Logo height
     */
    public Logo(int width, int height) {
        this();
        this.width = width;
        this.height = height;
    }

    /**
     * Create a logo from a Parcel
     * @param in Parcel containing logo data
     */
    protected Logo(Parcel in) {
        name = in.readString();
        width = in.readInt();
        height = in.readInt();
        backgroundColor = in.readInt();
        elements = new ArrayList<>();
        in.readTypedList(elements, LogoElement.CREATOR);
        id = in.readString();
        lastModified = in.readLong();
        thumbnailPath = in.readString();
    }

    /**
     * Creator for Parcelable implementation
     */
    public static final Creator<Logo> CREATOR = new Creator<Logo>() {
        @Override
        public Logo createFromParcel(Parcel in) {
            return new Logo(in);
        }

        @Override
        public Logo[] newArray(int size) {
            return new Logo[size];
        }
    };

    /**
     * Get the logo name
     * @return Logo name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the logo name
     * @param name New logo name
     */
    public void setName(String name) {
        this.name = name;
        updateLastModified();
    }

    /**
     * Get logo width
     * @return Width in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * Set logo width
     * @param width Width in pixels
     */
    public void setWidth(int width) {
        this.width = width;
        updateLastModified();
    }

    /**
     * Get logo height
     * @return Height in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Set logo height
     * @param height Height in pixels
     */
    public void setHeight(int height) {
        this.height = height;
        updateLastModified();
    }

    /**
     * Get logo background color
     * @return Background color
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Set logo background color
     * @param backgroundColor Background color
     */
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        updateLastModified();
    }

    /**
     * Get all logo elements
     * @return List of logo elements
     */
    public List<LogoElement> getElements() {
        return elements;
    }

    /**
     * Add an element to the logo
     * @param element Element to add
     */
    public void addElement(LogoElement element) {
        elements.add(element);
        updateLastModified();
    }

    /**
     * Remove an element from the logo
     * @param element Element to remove
     * @return True if element was removed, false otherwise
     */
    public boolean removeElement(LogoElement element) {
        boolean result = elements.remove(element);
        if (result) {
            updateLastModified();
        }
        return result;
    }

    /**
     * Move element to the front (top of the stack)
     * @param element Element to move
     */
    public void bringToFront(LogoElement element) {
        if (elements.remove(element)) {
            elements.add(element);
            updateLastModified();
        }
    }

    /**
     * Move element to the back (bottom of the stack)
     * @param element Element to move
     */
    public void sendToBack(LogoElement element) {
        if (elements.remove(element)) {
            elements.add(0, element);
            updateLastModified();
        }
    }

    /**
     * Render the logo to a bitmap
     */
    public Bitmap render() {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        // Draw background
        canvas.drawColor(backgroundColor);
        
        // Draw all elements
        for (LogoElement element : elements) {
            element.draw(canvas);
        }
        
        return bitmap;
    }
    
    /**
     * Render a thumbnail of the logo
     * @param targetWidth The width of the thumbnail
     */
    public Bitmap renderThumbnail(int targetWidth) {
        float scale = (float) targetWidth / width;
        int targetHeight = (int) (height * scale);
        
        Bitmap fullSizeBitmap = render();
        Bitmap thumbnail = Bitmap.createScaledBitmap(fullSizeBitmap, targetWidth, targetHeight, true);
        fullSizeBitmap.recycle();
        
        return thumbnail;
    }

    /**
     * Update the timestamp when the logo was last modified
     */
    public void updateLastModified() {
        lastModified = System.currentTimeMillis();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeInt(backgroundColor);
        dest.writeTypedList(elements);
        dest.writeString(id);
        dest.writeLong(lastModified);
        dest.writeString(thumbnailPath);
    }

    // Getters and setters
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public long getLastModified() {
        return lastModified;
    }
    
    public String getThumbnailPath() {
        return thumbnailPath;
    }
    
    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }
} 