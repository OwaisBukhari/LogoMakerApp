package com.logomaker.app.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;

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
    
    @Override
    public ElementType getType() {
        return ElementType.TEXT;
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
    public void draw(Canvas canvas) {
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
        
        // Draw selection highlight if selected
        if (isSelected()) {
            Paint selectionPaint = new Paint();
            selectionPaint.setColor(Color.BLUE);
            selectionPaint.setStyle(Paint.Style.STROKE);
            selectionPaint.setStrokeWidth(2);
            canvas.drawRect(-5, -5, textBounds.width() + 5, textBounds.height() + 5, selectionPaint);
        }
        
        canvas.restore();
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
        measureText();
    }
    
    public int getTextColor() {
        return textColor;
    }
    
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        updateTextPaint();
    }
    
    public float getTextSize() {
        return textSize;
    }
    
    public void setTextSize(float textSize) {
        this.textSize = textSize;
        updateTextPaint();
        measureText();
    }
    
    public Typeface getTypeface() {
        return typeface;
    }
    
    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
        updateTextPaint();
        measureText();
    }
    
    public boolean isBold() {
        return isBold;
    }
    
    public void setBold(boolean bold) {
        isBold = bold;
        updateTextPaint();
        measureText();
    }
    
    public boolean isItalic() {
        return isItalic;
    }
    
    public void setItalic(boolean italic) {
        isItalic = italic;
        updateTextPaint();
        measureText();
    }
    
    public boolean isUnderlined() {
        return isUnderlined;
    }
    
    public void setUnderlined(boolean underlined) {
        isUnderlined = underlined;
    }
    
    private void updateTextPaint() {
        textPaint.setColor(textColor);
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
    
    private void measureText() {
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
    }
} 