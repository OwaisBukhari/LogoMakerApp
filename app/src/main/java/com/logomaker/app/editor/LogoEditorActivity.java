package com.logomaker.app.editor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.logomaker.app.R;
import com.logomaker.app.editor.adapter.EditorPagerAdapter;
import com.logomaker.app.editor.views.LogoCanvasView;
import com.logomaker.app.export.ExportOptionsActivity;
import com.logomaker.app.model.Logo;
import com.logomaker.app.model.LogoElement;
import com.logomaker.app.model.TextElement;
import com.logomaker.app.util.DataManager;

import java.util.ArrayList;
import java.util.List;

public class LogoEditorActivity extends AppCompatActivity {

    public static final String EXTRA_LOGO = "extra_logo";
    
    private LogoCanvasView logoCanvas;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FloatingActionButton fabExport;
    private ImageButton btnUndo, btnRedo, btnSave;
    
    private Logo logo;
    private DataManager dataManager;
    private UndoRedoManager undoRedoManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo_editor);
        
        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        // Initialize views
        logoCanvas = findViewById(R.id.logoCanvas);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fabExport = findViewById(R.id.fabExport);
        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);
        btnSave = findViewById(R.id.btnSave);
        
        // Get data manager
        dataManager = DataManager.getInstance(this);
        
        // Set up undo/redo manager
        undoRedoManager = new UndoRedoManager();
        
        // Load or create a logo
        if (getIntent().hasExtra(EXTRA_LOGO)) {
            logo = getIntent().getParcelableExtra(EXTRA_LOGO);
        } else {
            logo = new Logo();
        }
        
        // Set up logo canvas
        logoCanvas.setLogo(logo);
        logoCanvas.setOnElementSelectedListener(element -> {
            // Update UI based on selected element
            updateElementPropertiesPanel(element);
        });
        
        // Set up editor tabs
        setupEditorTabs();
        
        // Set up click listeners
        btnUndo.setOnClickListener(v -> performUndo());
        btnRedo.setOnClickListener(v -> performRedo());
        btnSave.setOnClickListener(v -> saveLogo());
        fabExport.setOnClickListener(v -> exportLogo());
        
        // Update UI
        updateUndoRedoButtons();
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Show confirmation dialog if there are unsaved changes
            if (undoRedoManager.hasChanges()) {
                showUnsavedChangesDialog();
                return true;
            } else {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        // Show confirmation dialog if there are unsaved changes
        if (undoRedoManager.hasChanges()) {
            showUnsavedChangesDialog();
        } else {
            super.onBackPressed();
        }
    }
    
    private void setupEditorTabs() {
        EditorPagerAdapter pagerAdapter = new EditorPagerAdapter(this, logo, logoCanvas);
        viewPager.setAdapter(pagerAdapter);
        
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.add_text);
                    break;
                case 1:
                    tab.setText(R.string.add_shape);
                    break;
                case 2:
                    tab.setText(R.string.add_image);
                    break;
                case 3:
                    tab.setText(R.string.background);
                    break;
            }
        }).attach();
    }
    
    private void updateElementPropertiesPanel(LogoElement element) {
        // Show properties for the selected element
        if (element != null) {
            switch (element.getType()) {
                case TEXT:
                    viewPager.setCurrentItem(0);
                    break;
                case SHAPE:
                    viewPager.setCurrentItem(1);
                    break;
                case IMAGE:
                    viewPager.setCurrentItem(2);
                    break;
            }
        }
    }
    
    private void performUndo() {
        if (undoRedoManager.canUndo()) {
            Logo previousState = undoRedoManager.undo();
            logo = previousState;
            logoCanvas.setLogo(logo);
            updateUndoRedoButtons();
        }
    }
    
    private void performRedo() {
        if (undoRedoManager.canRedo()) {
            Logo nextState = undoRedoManager.redo();
            logo = nextState;
            logoCanvas.setLogo(logo);
            updateUndoRedoButtons();
        }
    }
    
    private void updateUndoRedoButtons() {
        btnUndo.setEnabled(undoRedoManager.canUndo());
        btnUndo.setAlpha(undoRedoManager.canUndo() ? 1.0f : 0.5f);
        
        btnRedo.setEnabled(undoRedoManager.canRedo());
        btnRedo.setAlpha(undoRedoManager.canRedo() ? 1.0f : 0.5f);
    }
    
    private void saveLogo() {
        // Update timestamp
        logo.updateLastModified();
        
        // Save logo
        dataManager.saveLogo(logo);
        
        // Clear undo/redo stack
        undoRedoManager.clearHistory();
        
        // Show success message
        Toast.makeText(this, R.string.logo_saved, Toast.LENGTH_SHORT).show();
    }
    
    private void exportLogo() {
        // Save first
        saveLogo();
        
        // Navigate to export options
        Intent intent = new Intent(this, ExportOptionsActivity.class);
        intent.putExtra(ExportOptionsActivity.EXTRA_LOGO_ID, logo.getId());
        startActivity(intent);
    }
    
    private void showUnsavedChangesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Unsaved Changes")
                .setMessage("You have unsaved changes. Do you want to save before exiting?")
                .setPositiveButton("Save", (dialog, which) -> {
                    saveLogo();
                    finish();
                })
                .setNegativeButton("Discard", (dialog, which) -> finish())
                .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
    
    /**
     * Add a new text element to the logo
     */
    public void addTextElement() {
        // Create a new text element
        TextElement textElement = new TextElement();
        textElement.setText("New Text");
        textElement.setPosition(new android.graphics.PointF(logo.getWidth() / 2f, logo.getHeight() / 2f));
        
        // Add to logo
        logo.addElement(textElement);
        
        // Select the new element
        logoCanvas.setSelectedElement(textElement);
        
        // Update canvas
        logoCanvas.invalidate();
        
        // Add to undo/redo stack
        undoRedoManager.addState(logo);
        updateUndoRedoButtons();
    }
    
    /**
     * Manages undo and redo operations
     */
    private static class UndoRedoManager {
        private static final int MAX_HISTORY = 20;
        
        private final List<Logo> undoStack = new ArrayList<>();
        private final List<Logo> redoStack = new ArrayList<>();
        
        public void addState(Logo logo) {
            try {
                // Add deep copy to undo stack
                Logo copy = deepCopyLogo(logo);
                undoStack.add(copy);
                
                // Clear redo stack
                redoStack.clear();
                
                // Limit stack size
                if (undoStack.size() > MAX_HISTORY) {
                    undoStack.remove(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        public Logo undo() {
            if (canUndo()) {
                Logo currentState = undoStack.remove(undoStack.size() - 1);
                redoStack.add(currentState);
                
                if (!undoStack.isEmpty()) {
                    return undoStack.get(undoStack.size() - 1);
                }
            }
            
            // Return a new logo if undo stack is empty
            return new Logo();
        }
        
        public Logo redo() {
            if (canRedo()) {
                Logo nextState = redoStack.remove(redoStack.size() - 1);
                undoStack.add(nextState);
                return nextState;
            }
            
            // Return current state if redo stack is empty
            return undoStack.isEmpty() ? new Logo() : undoStack.get(undoStack.size() - 1);
        }
        
        public boolean canUndo() {
            return undoStack.size() > 1;
        }
        
        public boolean canRedo() {
            return !redoStack.isEmpty();
        }
        
        public boolean hasChanges() {
            return !undoStack.isEmpty();
        }
        
        public void clearHistory() {
            undoStack.clear();
            redoStack.clear();
        }
        
        // Deep copy a logo object using Parcelable
        private Logo deepCopyLogo(Logo original) {
            Parcel parcel = null;
            try {
                parcel = Parcel.obtain();
                original.writeToParcel(parcel, 0);
                parcel.setDataPosition(0);
                return Logo.CREATOR.createFromParcel(parcel);
            } finally {
                if (parcel != null) {
                    parcel.recycle();
                }
            }
        }
    }
} 