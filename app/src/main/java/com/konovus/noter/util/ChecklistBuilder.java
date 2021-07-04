package com.konovus.noter.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.konovus.noter.R;
import com.konovus.noter.databinding.ChecklistRowBinding;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.databinding.DataBindingUtil;

import static java.lang.Integer.parseInt;

public class ChecklistBuilder {

    private final Context context;
    private final LinearLayout checkListWrapper;
    private int row_index;
    private final List<ChecklistRowBinding> checkBindings = new ArrayList<>();
    private final String color;
    private boolean checkListExists;

    public ChecklistBuilder(Context context, LinearLayout checkListWrapper, String color){
        this.context = context;
        this.checkListWrapper = checkListWrapper;
        this.color = color;
        checkBindings.clear();
    }

    public  void clearChecklist(){
        checkBindings.clear();
        checkListWrapper.removeAllViews();
    }
    public void build(HashMap<String, String> checklist){
        if(checklist != null) {
            checkListExists = true;
            for(int i = 0; i < checklist.entrySet().size(); i ++)
                for (Map.Entry<String, String> entry : checklist.entrySet())
                    if(entry.getKey().contains("" + i))
                        addCheckRow(entry.getKey().contains("true"), entry.getValue());

        } else addCheckRow(false, "");
    }
    public HashMap<String, String> getCheckList(){
        int i = 0;
        HashMap<String, String> checkList = new HashMap<>();
        for(ChecklistRowBinding checklistRowBinding: checkBindings)
            checkList.put("" + i++ + checklistRowBinding.checkBtn.isChecked(), checklistRowBinding.editText.getText().toString());
        return checkList;
    }

    private void addCheckRow(Boolean check, String text) {
        LinearLayout check_row = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.checklist_row, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(checkListWrapper.getLayoutParams());
        layoutParams.setMargins(0, 20, 0, 0);
        checkListWrapper.addView(check_row, row_index++, layoutParams);
        ChecklistRowBinding checkRowBinding = DataBindingUtil.bind(check_row);
        GradientDrawable gradientDrawable = (GradientDrawable) checkRowBinding.checkRowWrapper.getBackground();
        gradientDrawable.setColor(shadeColor(color, 15));
        checkRowBinding.checkBtn.setChecked(check);
        checkRowBinding.editText.setText(text);
        checkBindings.add(checkRowBinding);

        for(ChecklistRowBinding checkBinding: checkBindings){
            setupCheckBtn(checkBinding);
            setupEditText(checkBinding);
            checkBinding.editText.setTag(checkBindings.indexOf(checkBinding));
        }
    }
    private void setupCheckBtn(ChecklistRowBinding checkBinding) {
        checkBinding.checkBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
                checkBinding.editText.setPaintFlags(checkBinding.editText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
             else
                checkBinding.editText.setPaintFlags(checkBinding.editText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

        });
    }


    private void setupEditText(ChecklistRowBinding checkBinding) {
        if(!checkListExists)
            checkBinding.editText.setOnFocusChangeListener((v, hasFocus) -> showKeyboard(v));
        checkBinding.editText.requestFocus();
        checkBinding.editText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE)
                addCheckRow(false, "");
            return true;
        });
        checkBinding.editText.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_DEL && ((EditText) v).getText().toString().trim().isEmpty())
                removeCheckedRow(checkBinding);
            return true;
        });
    }

    private void removeCheckedRow(ChecklistRowBinding checklistRowBinding) {
        checkListWrapper.removeView(checklistRowBinding.checkRowWrapper);
        checkBindings.remove(checklistRowBinding);
        if(checkBindings.size() != 0)
            checkBindings.get(checkBindings.size() - 1).editText.requestFocus();
        row_index--;
    }

    public void changeColor(String color){
        for(ChecklistRowBinding checklistRowBinding : checkBindings){
            GradientDrawable gradientDrawable = (GradientDrawable) checklistRowBinding.checkRowWrapper.getBackground();
            gradientDrawable.setColor(shadeColor(color, 15));
        }
    }

    private void showKeyboard(View view){
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
    }

//    this method can lighten or darken a color by a percentage
    public static int shadeColor(String color, int percent) {

        int R = parseInt(color.substring(1,3),16);
        int G = parseInt(color.substring(3,5),16);
        int B = parseInt(color.substring(5,7),16);

        R = R * (100 + percent) / 100;
        G = G * (100 + percent) / 100;
        B = B * (100 + percent) / 100;

        R = Math.min(R, 255);
        G = Math.min(G, 255);
        B = Math.min(B, 255);

        return Color.rgb(R, G, B);
    }
}
