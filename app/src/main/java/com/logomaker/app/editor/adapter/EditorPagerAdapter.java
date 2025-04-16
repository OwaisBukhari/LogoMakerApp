package com.logomaker.app.editor.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.logomaker.app.editor.LogoEditorActivity;
import com.logomaker.app.editor.fragments.BackgroundEditorFragment;
import com.logomaker.app.editor.fragments.ImageEditorFragment;
import com.logomaker.app.editor.fragments.ShapeEditorFragment;
import com.logomaker.app.editor.fragments.TextEditorFragment;
import com.logomaker.app.editor.views.LogoCanvasView;
import com.logomaker.app.model.Logo;

/**
 * Adapter for the editor view pager
 */
public class EditorPagerAdapter extends FragmentStateAdapter {

    private final LogoEditorActivity activity;
    private final Logo logo;
    private final LogoCanvasView logoCanvas;

    public EditorPagerAdapter(FragmentActivity activity, Logo logo, LogoCanvasView logoCanvas) {
        super(activity);
        this.activity = (LogoEditorActivity) activity;
        this.logo = logo;
        this.logoCanvas = logoCanvas;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return TextEditorFragment.newInstance(logo, logoCanvas);
            case 1:
                return ShapeEditorFragment.newInstance(logo, logoCanvas);
            case 2:
                return ImageEditorFragment.newInstance(logo, logoCanvas);
            case 3:
                return BackgroundEditorFragment.newInstance(logo, logoCanvas);
            default:
                return TextEditorFragment.newInstance(logo, logoCanvas);
        }
    }

    @Override
    public int getItemCount() {
        return 4; // Text, Shape, Image, Background
    }
} 