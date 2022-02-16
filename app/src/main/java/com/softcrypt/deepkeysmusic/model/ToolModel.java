package com.softcrypt.deepkeysmusic.model;

import com.softcrypt.deepkeysmusic.adapter.tools.ToolType;

public class ToolModel {
    public String mToolName;
    public int mToolIcon;
    public ToolType mToolType;

    public ToolModel(String toolName, int toolIcon, ToolType toolType) {
        mToolName = toolName;
        mToolIcon = toolIcon;
        mToolType = toolType;
    }
}
