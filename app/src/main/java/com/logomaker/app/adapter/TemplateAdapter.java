package com.logomaker.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.logomaker.app.R;
import com.logomaker.app.model.LogoTemplate;
import com.logomaker.app.util.ImageUtil;

import java.util.List;

public class TemplateAdapter extends RecyclerView.Adapter<TemplateAdapter.ViewHolder> {

    private final List<LogoTemplate> templates;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public TemplateAdapter(List<LogoTemplate> templates) {
        this.templates = templates;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_template, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LogoTemplate template = templates.get(position);
        holder.tvTemplateName.setText(template.getName());
        holder.tvTemplateCategory.setText(template.getCategory());
        
        // Show/hide premium chip
        holder.chipPremium.setVisibility(template.isPremium() ? View.VISIBLE : View.GONE);
        
        // Load thumbnail
        if (template.getThumbnailResPath() != null) {
            // For this demo, we'll use a drawable resource
            int resourceId = holder.itemView.getContext().getResources()
                    .getIdentifier(template.getThumbnailResPath(), "drawable", 
                            holder.itemView.getContext().getPackageName());
            
            if (resourceId != 0) {
                holder.ivTemplate.setImageResource(resourceId);
            } else {
                // Use a placeholder
                holder.ivTemplate.setImageResource(R.drawable.template_placeholder);
            }
        } else {
            // Use a placeholder
            holder.ivTemplate.setImageResource(R.drawable.template_placeholder);
        }
        
        // Set up click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return templates.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivTemplate;
        final TextView tvTemplateName;
        final TextView tvTemplateCategory;
        final Chip chipPremium;

        ViewHolder(View itemView) {
            super(itemView);
            ivTemplate = itemView.findViewById(R.id.ivTemplate);
            tvTemplateName = itemView.findViewById(R.id.tvTemplateName);
            tvTemplateCategory = itemView.findViewById(R.id.tvTemplateCategory);
            chipPremium = itemView.findViewById(R.id.chipPremium);
        }
    }
} 