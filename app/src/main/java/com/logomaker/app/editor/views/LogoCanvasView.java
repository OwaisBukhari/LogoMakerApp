package com.logomaker.app.editor.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.logomaker.app.model.ImageElement;
import com.logomaker.app.model.Logo;
import com.logomaker.app.model.LogoElement;
import com.logomaker.app.model.ShapeElement;
import com.logomaker.app.model.TextElement;

/**
 * Canvas view for rendering and editing the logo
 */
public class LogoCanvasView extends View {

    private Logo logo;
    private LogoElement selectedElement;
    private float lastTouchX, lastTouchY;
    private boolean isDragging = false;
    
    // Selection indicator
    private final Paint selectionPaint;
    private final RectF selectionRect = new RectF();
    
    // Listener for element selection
    private OnElementSelectedListener elementSelectedListener;

    public LogoCanvasView(Context context) {
        this(context, null);
    }

    public LogoCanvasView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LogoCanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        
        // Initialize selection paint
        selectionPaint = new Paint();
        selectionPaint.setColor(Color.BLUE);
        selectionPaint.setStyle(Paint.Style.STROKE);
        selectionPaint.setStrokeWidth(4);
    }

    /**
     * Set the logo to be displayed on the canvas
     * @param logo The logo to display
     */
    public void setLogo(Logo logo) {
        this.logo = logo;
        invalidate();
    }

    /**
     * Get the currently displayed logo
     * @return The logo
     */
    public Logo getLogo() {
        return logo;
    }

    /**
     * Set the selected element
     * @param element The element to select
     */
    public void setSelectedElement(LogoElement element) {
        this.selectedElement = element;
        
        // Notify listener
        if (elementSelectedListener != null) {
            elementSelectedListener.onElementSelected(element);
        }
        
        invalidate();
    }

    /**
     * Get the currently selected element
     * @return The selected element
     */
    public LogoElement getSelectedElement() {
        return selectedElement;
    }

    /**
     * Set the element selection listener
     * @param listener The listener
     */
    public void setOnElementSelectedListener(OnElementSelectedListener listener) {
        this.elementSelectedListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (logo == null) return;
        
        // Draw logo background
        canvas.drawColor(logo.getBackgroundColor());
        
        // Draw all logo elements
        for (LogoElement element : logo.getElements()) {
            element.render(canvas);
            
            // Draw selection indicator for selected element
            if (element == selectedElement) {
                calculateSelectionRect(element);
                canvas.drawRect(selectionRect, selectionPaint);
            }
        }
    }

    /**
     * Calculate the selection rectangle for the given element
     * @param element The element
     */
    private void calculateSelectionRect(LogoElement element) {
        float padding = 10;
        selectionRect.set(
            element.getX() - padding,
            element.getY() - padding,
            element.getX() + element.getWidth() + padding,
            element.getY() + element.getHeight() + padding
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Check if user tapped on an element
                LogoElement tappedElement = findElementAt(x, y);
                setSelectedElement(tappedElement);
                
                if (tappedElement != null) {
                    isDragging = true;
                    lastTouchX = x;
                    lastTouchY = y;
                }
                return true;
                
            case MotionEvent.ACTION_MOVE:
                if (isDragging && selectedElement != null) {
                    // Calculate movement delta
                    float deltaX = x - lastTouchX;
                    float deltaY = y - lastTouchY;
                    
                    // Move the selected element
                    float newX = selectedElement.getX() + deltaX;
                    float newY = selectedElement.getY() + deltaY;
                    
                    // Ensure element stays within canvas bounds
                    newX = Math.max(0, Math.min(newX, getWidth() - selectedElement.getWidth()));
                    newY = Math.max(0, Math.min(newY, getHeight() - selectedElement.getHeight()));
                    
                    selectedElement.setX(newX);
                    selectedElement.setY(newY);
                    
                    // Update last touch position
                    lastTouchX = x;
                    lastTouchY = y;
                    
                    invalidate();
                }
                return true;
                
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                return true;
        }
        
        return super.onTouchEvent(event);
    }

    /**
     * Find the element at the given coordinates
     * @param x X coordinate
     * @param y Y coordinate
     * @return The element at the given coordinates, or null if no element was found
     */
    private LogoElement findElementAt(float x, float y) {
        if (logo == null) return null;
        
        // Iterate through elements in reverse order (top-most elements first)
        for (int i = logo.getElements().size() - 1; i >= 0; i--) {
            LogoElement element = logo.getElements().get(i);
            
            // Check if coordinates are within element bounds
            if (x >= element.getX() && x <= element.getX() + element.getWidth() &&
                y >= element.getY() && y <= element.getY() + element.getHeight()) {
                return element;
            }
        }
        
        return null;
    }

    /**
     * Refresh the canvas
     */
    public void refreshCanvas() {
        invalidate();
    }

    /**
     * Interface for element selection events
     */
    public interface OnElementSelectedListener {
        void onElementSelected(LogoElement element);
    }
} 