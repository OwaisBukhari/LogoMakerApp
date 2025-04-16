package com.logomaker.app.model;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * A shape element for logos
 */
public class ShapeElement extends LogoElement {
    // Shape types
    public static final int SHAPE_RECTANGLE = 0;
    public static final int SHAPE_ROUNDED_RECTANGLE = 1;
    public static final int SHAPE_CIRCLE = 2;
    public static final int SHAPE_OVAL = 3;
    public static final int SHAPE_TRIANGLE = 4;
    public static final int SHAPE_STAR = 5;
    public static final int SHAPE_POLYGON = 6;
    public static final int SHAPE_CUSTOM = 7;
    
    private int shapeType;
    private float width;
    private float height;
    private int fillColor;
    private int strokeColor;
    private float strokeWidth;
    private float cornerRadius; // For rounded rectangles
    private int sides; // For polygon
    private Path customPath; // For custom shapes
    private boolean hasStroke;
    private boolean hasFill;
    private Paint fillPaint;
    private Paint strokePaint;
    
    /**
     * Create a new shape element
     */
    public ShapeElement() {
        super();
        this.shapeType = SHAPE_RECTANGLE;
        this.fillColor = 0xFF3F51B5; // Indigo
        this.strokeColor = 0xFF000000; // Black
        this.strokeWidth = 2f;
        this.cornerRadius = 8f;
        this.sides = 6; // Hexagon default for polygon
        this.hasStroke = false;
        this.hasFill = true;
        // Default size
        this.width = 100;
        this.height = 100;
        setupPaints();
    }
    
    /**
     * Create a shape element with specific shape type
     * @param shapeType The type of shape
     */
    public ShapeElement(int shapeType) {
        this();
        this.shapeType = shapeType;
    }
    
    /**
     * Create a shape element from a Parcel
     * @param in Parcel containing shape element data
     */
    protected ShapeElement(Parcel in) {
        super(in);
        shapeType = in.readInt();
        width = in.readFloat();
        height = in.readFloat();
        fillColor = in.readInt();
        strokeColor = in.readInt();
        strokeWidth = in.readFloat();
        cornerRadius = in.readFloat();
        sides = in.readInt();
        hasStroke = in.readByte() != 0;
        hasFill = in.readByte() != 0;
        
        if (shapeType == SHAPE_CUSTOM) {
            customPath = new Path();
            customPath.rewind();
            // Read the path data - this is a simplified example and may need enhancement
            // In a real implementation, you might serialize the path data in a custom way
            int pointCount = in.readInt();
            float[] points = new float[pointCount * 2];
            in.readFloatArray(points);
            
            if (pointCount > 0) {
                customPath.moveTo(points[0], points[1]);
                for (int i = 1; i < pointCount; i++) {
                    customPath.lineTo(points[i*2], points[i*2+1]);
                }
                customPath.close();
            }
        }
        
        setupPaints();
    }
    
    public static final Creator<ShapeElement> CREATOR = new Creator<ShapeElement>() {
        @Override
        public ShapeElement createFromParcel(Parcel in) {
            return new ShapeElement(in);
        }

        @Override
        public ShapeElement[] newArray(int size) {
            return new ShapeElement[size];
        }
    };
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(shapeType);
        dest.writeFloat(width);
        dest.writeFloat(height);
        dest.writeInt(fillColor);
        dest.writeInt(strokeColor);
        dest.writeFloat(strokeWidth);
        dest.writeFloat(cornerRadius);
        dest.writeInt(sides);
        dest.writeByte((byte) (hasStroke ? 1 : 0));
        dest.writeByte((byte) (hasFill ? 1 : 0));
        
        if (shapeType == SHAPE_CUSTOM && customPath != null) {
            // This is a simplified example for saving path data
            // In a real implementation, you might need a more robust way to serialize paths
            // Calculate the number of points in the path
            float[] pathPoints = new float[100]; // Assuming maximum 50 points
            int pointCount = getPointsFromPath(customPath, pathPoints);
            
            dest.writeInt(pointCount);
            dest.writeFloatArray(pathPoints);
        }
    }
    
    /**
     * Helper method to extract points from a path
     * This is a simplified implementation and may need enhancement
     */
    private int getPointsFromPath(Path path, float[] points) {
        // In a real implementation, you would extract the actual points
        // For simplicity, this example returns 0 points
        return 0;
    }
    
    private void setupPaints() {
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(fillColor);
        fillPaint.setStyle(Paint.Style.FILL);
        
        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setColor(strokeColor);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeWidth);
    }
    
    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.concat(getTransformMatrix());
        
        // Center the shape
        float centerX = 0;
        float centerY = 0;
        
        switch (shapeType) {
            case SHAPE_RECTANGLE:
                drawRectangle(canvas, centerX, centerY);
                break;
            case SHAPE_CIRCLE:
                drawCircle(canvas, centerX, centerY);
                break;
            case SHAPE_TRIANGLE:
                drawTriangle(canvas, centerX, centerY);
                break;
            case SHAPE_STAR:
                drawStar(canvas, centerX, centerY);
                break;
            case SHAPE_ROUNDED_RECTANGLE:
                drawRoundedRectangle(canvas, centerX, centerY);
                break;
            case SHAPE_POLYGON:
                drawPolygon(canvas, sides, centerX, centerY);
                break;
            case SHAPE_CUSTOM:
                if (customPath != null) {
                    canvas.drawPath(customPath, fillPaint);
                    if (hasStroke) {
                        canvas.drawPath(customPath, strokePaint);
                    }
                }
                break;
        }
        
        // Draw selection highlight if selected
        if (isSelected()) {
            Paint selectionPaint = new Paint();
            selectionPaint.setColor(Color.BLUE);
            selectionPaint.setStyle(Paint.Style.STROKE);
            selectionPaint.setStrokeWidth(2);
            canvas.drawRect(-width / 2 - 5, -height / 2 - 5, width / 2 + 5, height / 2 + 5, selectionPaint);
        }
        
        canvas.restore();
    }
    
    private void drawRectangle(Canvas canvas, float centerX, float centerY) {
        RectF rect = new RectF(centerX - width / 2, centerY - height / 2, centerX + width / 2, centerY + height / 2);
        if (hasFill) {
            canvas.drawRect(rect, fillPaint);
        }
        if (hasStroke) {
            canvas.drawRect(rect, strokePaint);
        }
    }
    
    private void drawCircle(Canvas canvas, float centerX, float centerY) {
        float radius = Math.min(width, height) / 2;
        if (hasFill) {
            canvas.drawCircle(centerX, centerY, radius, fillPaint);
        }
        if (hasStroke) {
            canvas.drawCircle(centerX, centerY, radius, strokePaint);
        }
    }
    
    private void drawTriangle(Canvas canvas, float centerX, float centerY) {
        Path path = new Path();
        path.moveTo(centerX, centerY - height / 2);
        path.lineTo(centerX + width / 2, centerY + height / 2);
        path.lineTo(centerX - width / 2, centerY + height / 2);
        path.close();
        
        if (hasFill) {
            canvas.drawPath(path, fillPaint);
        }
        if (hasStroke) {
            canvas.drawPath(path, strokePaint);
        }
    }
    
    private void drawStar(Canvas canvas, float centerX, float centerY) {
        Path path = new Path();
        float outerRadius = Math.min(width, height) / 2;
        float innerRadius = outerRadius * 0.4f;
        int numPoints = 5;
        float angleStep = (float) (Math.PI / numPoints);
        
        for (int i = 0; i < numPoints * 2; i++) {
            float radius = (i % 2 == 0) ? outerRadius : innerRadius;
            float angle = i * angleStep - (float) Math.PI / 2;
            float x = centerX + (float) (radius * Math.cos(angle));
            float y = centerY + (float) (radius * Math.sin(angle));
            
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        path.close();
        
        if (hasFill) {
            canvas.drawPath(path, fillPaint);
        }
        if (hasStroke) {
            canvas.drawPath(path, strokePaint);
        }
    }
    
    private void drawRoundedRectangle(Canvas canvas, float centerX, float centerY) {
        RectF rect = new RectF(centerX - width / 2, centerY - height / 2, centerX + width / 2, centerY + height / 2);
        float cornerRadius = Math.min(width, height) * 0.2f;
        
        if (hasFill) {
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, fillPaint);
        }
        if (hasStroke) {
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, strokePaint);
        }
    }
    
    private void drawPolygon(Canvas canvas, int sides, float centerX, float centerY) {
        double angle = 2.0 * Math.PI / sides;
        float radius = Math.min(width, height) / 2;
        
        Path path = new Path();
        for (int i = 0; i < sides; i++) {
            double theta = i * angle - Math.PI / 2;
            float x = (float) (radius * Math.cos(theta));
            float y = (float) (radius * Math.sin(theta));
            
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        path.close();
        
        if (hasFill) {
            canvas.drawPath(path, fillPaint);
        }
        if (hasStroke) {
            canvas.drawPath(path, strokePaint);
        }
    }
    
    /**
     * Get the shape type
     * @return Shape type
     */
    public int getShapeType() {
        return shapeType;
    }
    
    /**
     * Set the shape type
     * @param shapeType New shape type
     */
    public void setShapeType(int shapeType) {
        this.shapeType = shapeType;
    }
    
    @Override
    public float getWidth() {
        return width;
    }
    
    @Override
    public float getHeight() {
        return height;
    }
    
    /**
     * Get the fill color
     * @return Fill color
     */
    public int getFillColor() {
        return fillColor;
    }
    
    /**
     * Set the fill color
     * @param fillColor New fill color
     */
    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
        fillPaint.setColor(fillColor);
    }
    
    /**
     * Get the stroke color
     * @return Stroke color
     */
    public int getStrokeColor() {
        return strokeColor;
    }
    
    /**
     * Set the stroke color
     * @param strokeColor New stroke color
     */
    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        strokePaint.setColor(strokeColor);
    }
    
    /**
     * Get the stroke width
     * @return Stroke width
     */
    public float getStrokeWidth() {
        return strokeWidth;
    }
    
    /**
     * Set the stroke width
     * @param strokeWidth New stroke width
     */
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        strokePaint.setStrokeWidth(strokeWidth);
    }
    
    /**
     * Get the corner radius for rounded rectangles
     * @return Corner radius
     */
    public float getCornerRadius() {
        return cornerRadius;
    }
    
    /**
     * Set the corner radius for rounded rectangles
     * @param cornerRadius New corner radius
     */
    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
    }
    
    /**
     * Get the number of sides for polygon
     * @return Number of sides
     */
    public int getSides() {
        return sides;
    }
    
    /**
     * Set the number of sides for polygon
     * @param sides New number of sides
     */
    public void setSides(int sides) {
        if (sides < 3) {
            throw new IllegalArgumentException("A polygon must have at least 3 sides");
        }
        this.sides = sides;
    }
    
    /**
     * Get the custom path
     * @return Custom path
     */
    public Path getCustomPath() {
        return customPath;
    }
    
    /**
     * Set the custom path
     * @param customPath New custom path
     */
    public void setCustomPath(Path customPath) {
        this.customPath = customPath;
    }
    
    /**
     * Check if shape has stroke
     * @return True if has stroke
     */
    public boolean isHasStroke() {
        return hasStroke;
    }
    
    /**
     * Set whether shape has stroke
     * @param hasStroke New has stroke state
     */
    public void setHasStroke(boolean hasStroke) {
        this.hasStroke = hasStroke;
    }
    
    /**
     * Check if shape has fill
     * @return True if has fill
     */
    public boolean isHasFill() {
        return hasFill;
    }
    
    /**
     * Set whether shape has fill
     * @param hasFill New has fill state
     */
    public void setHasFill(boolean hasFill) {
        this.hasFill = hasFill;
    }
    
    @Override
    public ShapeElement clone() {
        ShapeElement clone = new ShapeElement();
        clone.x = this.x;
        clone.y = this.y;
        clone.width = this.width;
        clone.height = this.height;
        clone.rotation = this.rotation;
        clone.scale = this.scale;
        clone.opacity = this.opacity;
        clone.locked = this.locked;
        
        clone.shapeType = this.shapeType;
        clone.fillColor = this.fillColor;
        clone.strokeColor = this.strokeColor;
        clone.strokeWidth = this.strokeWidth;
        clone.cornerRadius = this.cornerRadius;
        clone.sides = this.sides;
        clone.hasStroke = this.hasStroke;
        clone.hasFill = this.hasFill;
        
        if (this.customPath != null) {
            clone.customPath = new Path(this.customPath);
        }
        
        return clone;
    }
} 