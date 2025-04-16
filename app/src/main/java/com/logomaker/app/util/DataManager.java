package com.logomaker.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;

import com.logomaker.app.model.Logo;
import com.logomaker.app.model.LogoTemplate;
import com.logomaker.app.model.ShapeElement;
import com.logomaker.app.model.TextElement;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages app data including saved logos, templates, etc.
 */
public class DataManager {
    
    private static final String PREFS_NAME = "LogoMakerPreferences";
    private static DataManager instance;
    
    private final Context context;
    private final SharedPreferences preferences;
    private final List<Logo> savedLogos = new ArrayList<>();
    private final List<LogoTemplate> templates = new ArrayList<>();
    
    private DataManager(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        
        // Load saved data
        loadSavedLogos();
        loadTemplates();
    }
    
    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }
    
    /**
     * Get a list of recent logos
     * @param limit Maximum number of logos to return
     */
    public List<Logo> getRecentLogos(int limit) {
        // Sort by last modified (newest first)
        Collections.sort(savedLogos, (logo1, logo2) -> 
                Long.compare(logo2.getLastModified(), logo1.getLastModified()));
        
        // Return up to the limit
        return savedLogos.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all available templates
     */
    public List<LogoTemplate> getTemplates() {
        return new ArrayList<>(templates);
    }
    
    /**
     * Get templates for a specific category
     */
    public List<LogoTemplate> getTemplatesByCategory(String category) {
        return templates.stream()
                .filter(template -> template.getCategory().equals(category))
                .collect(Collectors.toList());
    }
    
    /**
     * Save a logo
     */
    public void saveLogo(Logo logo) {
        // Check if this logo already exists
        int existingIndex = -1;
        for (int i = 0; i < savedLogos.size(); i++) {
            if (savedLogos.get(i).getId().equals(logo.getId())) {
                existingIndex = i;
                break;
            }
        }
        
        // Generate a thumbnail for the logo
        Bitmap thumbnail = logo.renderThumbnail(300);
        String thumbnailPath = ImageUtil.saveBitmapToFile(
                context, 
                thumbnail, 
                "thumbnail_" + logo.getId() + ".png");
        logo.setThumbnailPath(thumbnailPath);
        
        if (existingIndex >= 0) {
            // Update existing logo
            savedLogos.set(existingIndex, logo);
        } else {
            // Add new logo
            savedLogos.add(logo);
        }
        
        // TODO: In a real app, we would persist this data to disk
    }
    
    /**
     * Delete a logo
     */
    public void deleteLogo(Logo logo) {
        // Remove from list
        savedLogos.removeIf(l -> l.getId().equals(logo.getId()));
        
        // Delete thumbnail file
        if (logo.getThumbnailPath() != null) {
            File thumbnailFile = new File(logo.getThumbnailPath());
            if (thumbnailFile.exists()) {
                thumbnailFile.delete();
            }
        }
    }
    
    /**
     * Load saved logos from storage (in a real app)
     */
    private void loadSavedLogos() {
        // For this demo, we'll create some sample logos
        for (int i = 1; i <= 5; i++) {
            Logo logo = new Logo();
            logo.setName("Sample Logo " + i);
            logo.setBackgroundColor(Color.WHITE);
            
            // Add a shape
            ShapeElement shape = new ShapeElement();
            shape.setShapeType(i % 2 == 0 ? ShapeElement.ShapeType.CIRCLE : ShapeElement.ShapeType.STAR);
            shape.setPosition(new PointF(500, 300));
            shape.setFillColor(Color.rgb(255, 50 * i, 50 * i));
            logo.addElement(shape);
            
            // Add text
            TextElement text = new TextElement();
            text.setText("LOGO " + i);
            text.setPosition(new PointF(500, 500));
            text.setTextColor(Color.BLACK);
            logo.addElement(text);
            
            // Set creation time staggered in the past
            logo.setLastModified(System.currentTimeMillis() - (i * 60000));
            
            savedLogos.add(logo);
        }
    }
    
    /**
     * Load templates from resources (in a real app)
     */
    private void loadTemplates() {
        // For this demo, we'll create some sample templates
        String[] categories = {"Business", "Technology", "Food", "Fashion", "Sports"};
        String[] names = {"Modern", "Classic", "Bold", "Elegant", "Simple"};
        
        for (int i = 0; i < 10; i++) {
            String id = "template_" + (i + 1);
            String name = names[i % names.length] + " " + categories[i % categories.length];
            String category = categories[i % categories.length];
            String thumbnailResPath = "template_" + (i + 1); // This would be an image in drawable
            boolean isPremium = i % 3 == 0;
            
            LogoTemplate template = new LogoTemplate(id, name, category, thumbnailResPath, isPremium);
            templates.add(template);
        }
    }
} 