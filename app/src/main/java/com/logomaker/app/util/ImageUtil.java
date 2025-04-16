package com.logomaker.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.logomaker.app.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Utility methods for handling images in the app
 */
public class ImageUtil {

    /**
     * Load an image from a file path into an ImageView
     */
    public static void loadImageFromFile(ImageView imageView, String filePath) {
        Glide.with(imageView.getContext())
                .load(new File(filePath))
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imageView);
    }
    
    /**
     * Load a bitmap directly into an ImageView
     */
    public static void loadBitmapIntoImageView(ImageView imageView, Bitmap bitmap) {
        Glide.with(imageView.getContext())
                .load(bitmap)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(imageView);
    }
    
    /**
     * Save a bitmap to the app's files directory
     * @return The path to the saved file
     */
    public static String saveBitmapToFile(Context context, Bitmap bitmap, String filename) {
        File directory = new File(context.getFilesDir(), "logos");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        File file = new File(directory, filename);
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Export a bitmap to the Pictures/LogoMaker directory
     * @return The path to the exported file
     */
    public static String exportBitmap(Context context, Bitmap bitmap, String filename, 
                                     Bitmap.CompressFormat format, int quality) {
        File directory = new File(context.getExternalFilesDir(null), "LogoMaker");
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        // Add proper extension
        String extension;
        switch (format) {
            case JPEG:
                extension = ".jpg";
                break;
            case PNG:
                extension = ".png";
                break;
            case WEBP:
                extension = ".webp";
                break;
            default:
                extension = ".png";
                break;
        }
        
        if (!filename.endsWith(extension)) {
            filename += extension;
        }
        
        File file = new File(directory, filename);
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(format, quality, out);
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Load a bitmap from a file path
     */
    public static Bitmap loadBitmapFromFile(String filePath) {
        return BitmapFactory.decodeFile(filePath);
    }
    
    /**
     * Resize a bitmap to the specified width and height
     */
    public static Bitmap resizeBitmap(Bitmap source, int targetWidth, int targetHeight) {
        return Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);
    }
    
    /**
     * Resize a bitmap, maintaining aspect ratio
     * @param maxDimension The maximum width or height
     */
    public static Bitmap resizeBitmapKeepAspectRatio(Bitmap source, int maxDimension) {
        int width = source.getWidth();
        int height = source.getHeight();
        
        float ratio = (float) width / height;
        int targetWidth, targetHeight;
        
        if (width > height) {
            targetWidth = maxDimension;
            targetHeight = (int) (maxDimension / ratio);
        } else {
            targetHeight = maxDimension;
            targetWidth = (int) (maxDimension * ratio);
        }
        
        return Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);
    }
} 