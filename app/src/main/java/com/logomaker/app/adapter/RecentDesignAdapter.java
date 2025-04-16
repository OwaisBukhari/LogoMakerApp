package com.logomaker.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.logomaker.app.R;
import com.logomaker.app.model.Logo;
import com.logomaker.app.util.ImageUtil;

import java.util.List;

public class RecentDesignAdapter extends RecyclerView.Adapter<RecentDesignAdapter.ViewHolder> {

    private final List<Logo> recentDesigns;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public RecentDesignAdapter(List<Logo> recentDesigns) {
        this.recentDesigns = recentDesigns;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Logo logo = recentDesigns.get(position);
        holder.tvDesignName.setText(logo.getName());
        
        // Load thumbnail
        if (logo.getThumbnailPath() != null) {
            ImageUtil.loadImageFromFile(holder.ivDesign, logo.getThumbnailPath());
        } else {
            // If no thumbnail exists, render one
            ImageUtil.loadBitmapIntoImageView(holder.ivDesign, logo.renderThumbnail(300));
        }
        
        // Set up click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });
        
        holder.ivMore.setOnClickListener(v -> {
            // Show options menu (share, delete, etc.)
            // This would be implemented in a real app
        });
    }

    @Override
    public int getItemCount() {
        return recentDesigns.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivDesign;
        final TextView tvDesignName;
        final ImageView ivMore;

        ViewHolder(View itemView) {
            super(itemView);
            ivDesign = itemView.findViewById(R.id.ivDesign);
            tvDesignName = itemView.findViewById(R.id.tvDesignName);
            ivMore = itemView.findViewById(R.id.ivMore);
        }
    }
} 