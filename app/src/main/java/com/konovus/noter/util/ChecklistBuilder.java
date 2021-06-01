package com.konovus.noter.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.konovus.noter.R;
import com.konovus.noter.databinding.ChecklistRowBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.databinding.DataBindingUtil;

public class ChecklistBuilder {

    private final Context context;
    private final LinearLayout checkListWrapper;
    private int row_index;
    private static final List<ChecklistRowBinding> checkBindings = new ArrayList<>();

    public ChecklistBuilder(Context context, Activity activity){
        this.context = context;
        checkListWrapper = activity.findViewById(R.id.checklist_wrapper);
    }

    public void build(){
        addCheckRow();

    }
    public static LinkedHashMap<Boolean, String> getCheckList(){
        LinkedHashMap<Boolean, String> checkList = new LinkedHashMap<>();
        for(ChecklistRowBinding checklistRowBinding: checkBindings)
            checkList.put(checklistRowBinding.checkBtn.isChecked(), checklistRowBinding.editText.getText().toString());

        return checkList;
    }

    private void addCheckRow() {
        LinearLayout check_row = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.checklist_row, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(checkListWrapper.getLayoutParams());
        layoutParams.setMargins(0, 20, 0, 0);
        checkListWrapper.addView(check_row, row_index++, layoutParams);
        ChecklistRowBinding checkRowBinding = DataBindingUtil.bind(check_row);
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
        checkBinding.editText.setOnFocusChangeListener((v, hasFocus) -> {showKeyboard(true);});
        checkBinding.editText.requestFocus();
        checkBinding.editText.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE)
                addCheckRow();
            return true;
        });
        checkBinding.editText.setOnKeyListener((v, keyCode, event) -> {
            Log.i("NoteR", "setupEditText: ");
            if(event.getAction() == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_DEL && ((EditText) v).getText().toString().trim().isEmpty())
                removeCheckedRow(Integer.parseInt(((EditText) v).getTag().toString()));
            return true;
        });
    }

    private void removeCheckedRow(int pos) {
        checkListWrapper.removeViewAt(pos);
        checkBindings.remove(pos);
        if(checkBindings.size() != 0)
            checkBindings.get(checkBindings.size() - 1).editText.requestFocus();
        row_index--;
    }

    private void showKeyboard(boolean forced){
        InputMethodManager inputMgr = (InputMethodManager) context.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        //if keyboard it's not showing, you can set .SHOW_FORCED

        if(forced) {
            inputMgr.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                    InputMethodManager.RESULT_HIDDEN);
//            inputMgr.showSoftInput(view, InputMethodManager.SHOW_FORCED);

        } else inputMgr.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                InputMethodManager.RESULT_HIDDEN);
    }
}
