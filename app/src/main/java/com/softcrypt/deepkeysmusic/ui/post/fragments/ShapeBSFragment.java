package com.softcrypt.deepkeysmusic.ui.post.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.softcrypt.deepkeysmusic.R;
import com.softcrypt.deepkeysmusic.adapter.ColorPickerAdapter;

import java.util.Objects;

import ja.burhanrashid52.photoeditor.shape.ShapeType;

public class ShapeBSFragment extends BottomSheetDialogFragment implements SeekBar.OnSeekBarChangeListener {

    public ShapeBSFragment() {
        // Required empty public constructor
    }

    private ShapeProperties mProperties;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_button_shape_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rvColor = view.findViewById(R.id.shapeColors);
        SeekBar sbOpacity = view.findViewById(R.id.shapeOpacity);
        SeekBar sbBrushSize = view.findViewById(R.id.shapeSize);
        RadioGroup shapeGroup = view.findViewById(R.id.shapeRadioGroup);

        // shape picker
        shapeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.lineRadioButton) {
                mProperties.onShapePicked(ShapeType.LINE);
            } else if (checkedId == R.id.ovalRadioButton) {
                mProperties.onShapePicked(ShapeType.OVAL);
            } else if (checkedId == R.id.rectRadioButton) {
                mProperties.onShapePicked(ShapeType.RECTANGLE);
            } else {
                mProperties.onShapePicked(ShapeType.BRUSH);
            }
        });

        sbOpacity.setOnSeekBarChangeListener(this);
        sbBrushSize.setOnSeekBarChangeListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rvColor.setLayoutManager(layoutManager);
        rvColor.setHasFixedSize(true);
        ColorPickerAdapter colorPickerAdapter = new ColorPickerAdapter(requireActivity());
        colorPickerAdapter.setOnColorPickerClickListener(colorCode -> {
            if (mProperties != null) {
                dismiss();
                mProperties.onColorChanged(colorCode);
            }
        });
        rvColor.setAdapter(colorPickerAdapter);
    }

    public void setPropertiesChangeListener(ShapeProperties properties) {
        mProperties = properties;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.shapeOpacity:
                if (mProperties != null) {
                    mProperties.onOpacityChanged(progress);
                }
                break;
            case R.id.shapeSize:
                if (mProperties != null) {
                    mProperties.onShapeSizeChanged(progress);
                }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}