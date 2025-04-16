package com.logomaker.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.logomaker.app.adapter.RecentDesignAdapter;
import com.logomaker.app.adapter.TemplateAdapter;
import com.logomaker.app.editor.LogoEditorActivity;
import com.logomaker.app.model.Logo;
import com.logomaker.app.model.LogoTemplate;
import com.logomaker.app.templates.TemplateSelectionActivity;
import com.logomaker.app.util.DataManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvRecentDesigns;
    private RecyclerView rvTemplates;
    private RecentDesignAdapter recentDesignAdapter;
    private TemplateAdapter templateAdapter;
    private ChipGroup chipGroupCategories;
    private List<Logo> recentDesigns = new ArrayList<>();
    private List<LogoTemplate> templates = new ArrayList<>();
    private DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        // Initialize views
        rvRecentDesigns = findViewById(R.id.rvRecentDesigns);
        rvTemplates = findViewById(R.id.rvTemplates);
        chipGroupCategories = findViewById(R.id.chipGroupCategories);
        CardView cardCreateNew = findViewById(R.id.cardCreateNew);
        FloatingActionButton fab = findViewById(R.id.fab);
        
        // Initialize data manager
        dataManager = DataManager.getInstance(this);
        
        // Set up recent designs recycler view
        setupRecentDesignsRecyclerView();
        
        // Set up templates recycler view
        setupTemplatesRecyclerView();
        
        // Set up category filter
        setupCategoryFilter();
        
        // Set up click listeners
        cardCreateNew.setOnClickListener(v -> startLogoEditor(null));
        fab.setOnClickListener(v -> startLogoEditor(null));
        
        findViewById(R.id.tvViewAllTemplates).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TemplateSelectionActivity.class);
            startActivity(intent);
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this screen
        loadRecentDesigns();
        loadTemplates();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void setupRecentDesignsRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvRecentDesigns.setLayoutManager(layoutManager);
        
        recentDesignAdapter = new RecentDesignAdapter(recentDesigns);
        recentDesignAdapter.setOnItemClickListener(position -> {
            Logo logo = recentDesigns.get(position);
            startLogoEditor(logo);
        });
        
        rvRecentDesigns.setAdapter(recentDesignAdapter);
        
        // Load recent designs
        loadRecentDesigns();
    }
    
    private void setupTemplatesRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvTemplates.setLayoutManager(layoutManager);
        
        templateAdapter = new TemplateAdapter(templates);
        templateAdapter.setOnItemClickListener(position -> {
            LogoTemplate template = templates.get(position);
            
            if (template.isPremium()) {
                Toast.makeText(MainActivity.this, "Premium templates coming soon!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            Logo logo = template.createLogoFromTemplate();
            startLogoEditor(logo);
        });
        
        rvTemplates.setAdapter(templateAdapter);
        
        // Load templates
        loadTemplates();
    }
    
    private void setupCategoryFilter() {
        chipGroupCategories.setOnCheckedChangeListener((group, checkedId) -> {
            String category = null;
            
            if (checkedId != View.NO_ID) {
                Chip chip = findViewById(checkedId);
                if (chip != null && checkedId != R.id.chipAll) {
                    category = chip.getText().toString();
                }
            }
            
            filterTemplatesByCategory(category);
        });
    }
    
    private void loadRecentDesigns() {
        // In a real app, we would load from storage
        // For this demo, we'll create dummy data
        recentDesigns.clear();
        recentDesigns.addAll(dataManager.getRecentLogos(10));
        
        if (recentDesignAdapter != null) {
            recentDesignAdapter.notifyDataSetChanged();
        }
        
        // Show/hide empty state
        findViewById(R.id.rvRecentDesigns).setVisibility(
                recentDesigns.isEmpty() ? View.GONE : View.VISIBLE);
    }
    
    private void loadTemplates() {
        // In a real app, we would load from storage
        // For this demo, we'll create dummy data
        templates.clear();
        templates.addAll(dataManager.getTemplates());
        
        if (templateAdapter != null) {
            templateAdapter.notifyDataSetChanged();
        }
    }
    
    private void filterTemplatesByCategory(String category) {
        if (category == null) {
            // "All" category selected, show all templates
            templates.clear();
            templates.addAll(dataManager.getTemplates());
        } else {
            // Filter templates by category
            templates.clear();
            templates.addAll(dataManager.getTemplatesByCategory(category));
        }
        
        if (templateAdapter != null) {
            templateAdapter.notifyDataSetChanged();
        }
    }
    
    private void startLogoEditor(Logo logo) {
        Intent intent = new Intent(MainActivity.this, LogoEditorActivity.class);
        
        if (logo != null) {
            // Edit existing logo
            intent.putExtra(LogoEditorActivity.EXTRA_LOGO, logo);
        }
        
        startActivity(intent);
    }
} 