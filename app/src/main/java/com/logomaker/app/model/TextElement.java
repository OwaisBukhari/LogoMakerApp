package com.logomaker.app.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * A text element that can be added to a logo
 */
public class TextElement extends LogoElement {
    
    public static final Parcelable.Creator<TextElement> CREATOR = new Parcelable.Creator<TextElement>() {
        @Override
        public TextElement createFromParcel(Parcel source) {
            return new TextElement(source);
        }

        @Override
        public TextElement[] newArray(int size) {
            return new TextElement[size];
        }
    };
    
    private String text;
    private int textColor;
    private float textSize;
    private Typeface typeface;
    private boolean isBold;
    private boolean isItalic;
    private boolean isUnderlined;
    private Paint textPaint;
    private Rect textBounds;
    
    /**
     * Create a new text element
     */
    public TextElement() {
        super();
        text = "Sample Text";
        textColor = Color.BLACK;
        textSize = 60f;
        typeface = Typeface.DEFAULT;
        isBold = false;
        isItalic = false;
        isUnderlined = false;
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        updateTextPaint();
        textBounds = new Rect();
        measureText();
    }
    
    /**
     * Create a text element from a Parcel
     */
    protected TextElement(Parcel in) {
        super(in);
        text = in.readString();
        textColor = in.readInt();
        textSize = in.readFloat();
        isBold = in.readByte() != 0;
        isItalic = in.readByte() != 0;
        isUnderlined = in.readByte() != 0;
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        updateTextPaint();
        textBounds = new Rect();
        measureText();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(text);
        dest.writeInt(textColor);
        dest.writeFloat(textSize);
        dest.writeByte((byte) (isBold ? 1 : 0));
        dest.writeByte((byte) (isItalic ? 1 : 0));
        dest.writeByte((byte) (isUnderlined ? 1 : 0));
    }
    
    /**
     * Get the type of this element
     * @return Element type
     */
    @Override
    public int getType() {
        return LogoElement.TYPE_TEXT;
    }
    
    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.save();
        canvas.concat(getTransformMatrix());
        
        // Draw text
        canvas.drawText(text, -textBounds.left, -textBounds.top, textPaint);
        
        // Draw underline if needed
        if (isUnderlined) {
            Paint underlinePaint = new Paint(textPaint);
            underlinePaint.setStyle(Paint.Style.STROKE);
            underlinePaint.setStrokeWidth(textSize / 20);
            canvas.drawLine(-textBounds.left, -textBounds.top + textBounds.height() + 5,
                    -textBounds.left + textBounds.width(), -textBounds.top + textBounds.height() + 5,
                    underlinePaint);
        }
        
        canvas.restore();
    }
    
    @Override
    public float getWidth() {
        return textBounds.width();
    }
    
    @Override
    public float getHeight() {
        return textBounds.height();
    }
    
    @Override
    public TextElement clone() {
        TextElement clone = new TextElement();
        clone.text = this.text;
        clone.textColor = this.textColor;
        clone.textSize = this.textSize;
        clone.typeface = this.typeface;
        clone.isBold = this.isBold;
        clone.isItalic = this.isItalic;
        clone.isUnderlined = this.isUnderlined;
        clone.x = this.x;
        clone.y = this.y;
        clone.rotation = this.rotation;
        clone.scale = this.scale;
        clone.opacity = this.opacity;
        clone.locked = this.locked;
        clone.updateTextPaint();
        clone.measureText();
        return clone;
    }
    
    /**
     * Get the text content
     */
    public String getText() {
        return text;
    }
    
    /**
     * Set the text content
     */
    public void setText(String text) {
        this.text = text;
        measureText();
    }
    
    /**
     * Get the text color
     */
    public int getTextColor() {
        return textColor;
    }
    
    /**
     * Set the text color
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        updateTextPaint();
    }
    
    /**
     * Get the text size
     */
    public float getTextSize() {
        return textSize;
    }
    
    /**
     * Set the text size
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        updateTextPaint();
        measureText();
    }
    
    /**
     * Get the typeface
     */
    public Typeface getTypeface() {
        return typeface;
    }
    
    /**
     * Set the typeface
     */
    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
        updateTextPaint();
        measureText();
    }
    
    /**
     * Check if text is bold
     */
    public boolean isBold() {
        return isBold;
    }
    
    /**
     * Set bold style
     */
    public void setBold(boolean bold) {
        isBold = bold;
        updateTextPaint();
        measureText();
    }
    
    /**
     * Check if text is italic
     */
    public boolean isItalic() {
        return isItalic;
    }
    
    /**
     * Set italic style
     */
    public void setItalic(boolean italic) {
        isItalic = italic;
        updateTextPaint();
        measureText();
    }
    
    /**
     * Check if text is underlined
     */
    public boolean isUnderlined() {
        return isUnderlined;
    }
    
    /**
     * Set underlined style
     */
    public void setUnderlined(boolean underlined) {
        isUnderlined = underlined;
    }
    
    /**
     * Update the text paint with current properties
     */
    private void updateTextPaint() {
        textPaint.setColor(textColor);
        textPaint.setAlpha(opacity);
        textPaint.setTextSize(textSize);
        
        int style = Typeface.NORMAL;
        if (isBold && isItalic) {
            style = Typeface.BOLD_ITALIC;
        } else if (isBold) {
            style = Typeface.BOLD;
        } else if (isItalic) {
            style = Typeface.ITALIC;
        }
        
        textPaint.setTypeface(Typeface.create(typeface, style));
    }
    
    /**
     * Measure the text bounds
     */
    private void measureText() {
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
    }
} 