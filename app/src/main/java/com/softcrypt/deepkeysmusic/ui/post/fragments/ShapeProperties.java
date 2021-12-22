package com.softcrypt.deepkeysmusic.ui.post.fragments;

import ja.burhanrashid52.photoeditor.shape.ShapeType;

public interface ShapeProperties {
    void onColorChanged(int colorCode);
    void onOpacityChanged(int opacity);
    void onShapeSizeChanged(int shapeSize);
    void onShapePicked(ShapeType shapeType);
}
