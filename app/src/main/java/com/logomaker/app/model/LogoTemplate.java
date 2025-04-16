package com.logomaker.app.model;

/**
 * Represents a template for creating logos
 */
public class LogoTemplate {
    
    private String id;
    private String name;
    private String category;
    private String thumbnailResPath;
    private boolean isPremium;
    private Logo logo;
    
    public LogoTemplate(String id, String name, String category, String thumbnailResPath, boolean isPremium) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.thumbnailResPath = thumbnailResPath;
        this.isPremium = isPremium;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getThumbnailResPath() {
        return thumbnailResPath;
    }
    
    public void setThumbnailResPath(String thumbnailResPath) {
        this.thumbnailResPath = thumbnailResPath;
    }
    
    public boolean isPremium() {
        return isPremium;
    }
    
    public void setPremium(boolean premium) {
        isPremium = premium;
    }
    
    public Logo getLogo() {
        return logo;
    }
    
    public void setLogo(Logo logo) {
        this.logo = logo;
    }
    
    /**
     * Create a copy of the template logo to use as a starting point
     */
    public Logo createLogoFromTemplate() {
        // In a real app, we would deserialize the template from storage
        // or create a deep copy of the template logo
        // For this example, we'll just create a new logo
        
        Logo newLogo = new Logo();
        newLogo.setName(name + " Logo");
        
        // Add some default elements based on the template category
        if (category.equals("Business")) {
            // Add business-themed elements
            TextElement companyName = new TextElement();
            companyName.setText("COMPANY");
            companyName.setPosition(new android.graphics.PointF(500, 400));
            newLogo.addElement(companyName);
            
            TextElement tagline = new TextElement();
            tagline.setText("Professional tagline here");
            tagline.setTextSize(40);
            tagline.setPosition(new android.graphics.PointF(500, 500));
            newLogo.addElement(tagline);
            
            ShapeElement shape = new ShapeElement();
            shape.setShapeType(ShapeElement.ShapeType.ROUNDED_RECTANGLE);
            shape.setPosition(new android.graphics.PointF(500, 300));
            newLogo.addElement(shape);
        } else if (category.equals("Technology")) {
            // Add tech-themed elements
            TextElement companyName = new TextElement();
            companyName.setText("TECH");
            companyName.setPosition(new android.graphics.PointF(500, 450));
            newLogo.addElement(companyName);
            
            ShapeElement shape = new ShapeElement();
            shape.setShapeType(ShapeElement.ShapeType.HEXAGON);
            shape.setPosition(new android.graphics.PointF(500, 300));
            newLogo.addElement(shape);
        } else {
            // Generic template
            TextElement text = new TextElement();
            text.setText("YOUR LOGO");
            text.setPosition(new android.graphics.PointF(500, 500));
            newLogo.addElement(text);
        }
        
        return newLogo;
    }
} 