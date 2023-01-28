package com.example.calcdroid.inputs;

import android.content.Context;
import android.text.*;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import androidx.core.content.ContextCompat;
import com.example.calcdroid.R;

import java.util.*;


public class MathKeyboard implements View.OnClickListener, View.OnFocusChangeListener {
    static RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    static {
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    }
    public static Map<String, String> imageSymbols = new HashMap<>();
    static {
        imageSymbols.put("INFINITY","∞");
        imageSymbols.put("LESS","<");
        imageSymbols.put("LESS_OR_EQUAL", "≤");
        imageSymbols.put("MORE",">");
        imageSymbols.put("MORE_OR_EQUAL", "≥");
        imageSymbols.put("SQUARE_ROOT", "√");
        imageSymbols.put("SUM", "Σ");
        imageSymbols.put("DERIVATIVE", "d/dx");
    }
    View keyboardView;
    EditText curEditText;
    ViewGroup currentLayout;
    InputConnection connection;
    LayoutInflater inflater;
    Context context;
    Boolean isSubscript = false, isSuperscript = false;
    ArrayList<Button> buttons = new ArrayList<>();
    Map<EditText, ArrayList<Integer>> superscriptIndices = new HashMap<>(),
        subscriptIndices = new HashMap<>();

    public MathKeyboard(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        keyboardView = inflater.inflate(R.layout.keyboard, null);
        keyboardView.setLayoutParams(params);
        getButtons((ViewGroup) keyboardView);
        listenToButtons();
    }

    @Override
    public void onClick(View v) {
        int cursorPosition = getCursorPosition();
        switch (v.getTag().toString()) {
            case "SWITCH_TO_LETTERS": {
                updateView(R.layout.letter_keyboard);
                break;
            }
            case "SWITCH_TO_FUNCTIONS": {
                updateView(R.layout.func_keyboard);
                break;
            }
            case "SWITCH_TO_MAIN": {
                updateView(R.layout.keyboard);
                break;
            }
            case "CURSOR_LEFT":{
                setCursorPosition(cursorPosition-1);
                break;
            }
            case "CURSOR_RIGHT":{
                setCursorPosition(cursorPosition+1);
                break;
            }
            case "FUNCTION":{
                commitText(((Button)v).getText() + "()", true);
                break;
            }
            case "LETTER": {
                commitText(((Button)v).getText(), false);
                break;
            }
            case "DERIVATIVE":
            case "SQUARE_ROOT":{
                commitText(imageSymbols.get(v.getTag().toString()) + "()", true);
                break;
            }
            case "POWER": {
                switchSuperscript(isSuperscript, (Button)v);
                switchSubscript(true, null);
                break;
            }
            case "SUBSCRIPT": {
                switchSubscript(isSubscript, (Button) v);
                switchSuperscript(true, null);
                break;
            }
            case "MORE":
            case "MORE_OR_EQUAL":
            case "LESS":
            case "LESS_OR_EQUAL": {
                commitText(imageSymbols.get(v.getTag().toString()), false);
                break;
            }
            case "BACKSPACE": {
                if (subscriptIndices.get(curEditText).contains(cursorPosition-1))
                    subscriptIndices.remove(subscriptIndices.get(curEditText).indexOf(cursorPosition-1));
                if (superscriptIndices.get(curEditText).contains(cursorPosition-1))
                    superscriptIndices.remove(superscriptIndices.get(curEditText).indexOf(cursorPosition-1));
                connection.deleteSurroundingText(1,0);
                break;
            }
        }
    }

    private void switchSuperscript(boolean turnOff, Button superscriptButton) {
        if (isSuperscript) {
            isSuperscript = false;
            if (superscriptButton != null)
                superscriptButton.setBackgroundColor(ContextCompat.getColor(context, R.color.light_grey));
        }
        else if (!turnOff) {
            isSuperscript = true;
            if (superscriptButton != null)
                superscriptButton.setBackgroundColor(ContextCompat.getColor(context, R.color.grey));
        }
    }

    private void switchSubscript(boolean turnOff, Button subscriptButton) {
        if (isSubscript) {
            isSubscript = false;
            if (subscriptButton != null)
                subscriptButton.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        }
        else if (!turnOff) {
            isSubscript = true;
            if (subscriptButton != null)
                subscriptButton.setBackgroundColor(ContextCompat.getColor(context, R.color.grey));
        }
    }

    private void commitText(CharSequence text, boolean cursorInBrackets) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        builder.setSpan(new RelativeSizeSpan(isSubscript || isSuperscript? 0.75f:1f),
                0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        int cursorPosition = getCursorPosition();
        if (isSubscript) {
            builder.setSpan(new SubscriptSpan(), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            shiftIndices(cursorPosition, text.length(), subscriptIndices.get(curEditText));
            for (int i = 0; i < text.length(); i++)
                subscriptIndices.get(curEditText).add(i+cursorPosition);
        }
        if (isSuperscript) {
            builder.setSpan(new SuperscriptSpan(), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            shiftIndices(cursorPosition, text.length(), superscriptIndices.get(curEditText));
            for (int i = 0; i < text.length(); i++)
                superscriptIndices.get(curEditText).add(i+cursorPosition);
        }
        connection.commitText(builder, 0);
        setCursorPosition(cursorInBrackets? cursorPosition + text.length() - 1: cursorPosition + text.length());
    }

    private void shiftIndices(int pivot, int offset, ArrayList<Integer> indices) {
        for (int i = 0; i < indices.size(); i++)
            if (indices.get(i) >= pivot)
                indices.set(i, indices.get(i) + offset);
    }
    private int getCursorPosition() {
        ExtractedText extractedText = connection.getExtractedText(new ExtractedTextRequest(), 0);
        return extractedText.startOffset + extractedText.selectionStart;
    }

    private void setCursorPosition(int index) {
        connection.setSelection(index, index);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            connection = v.onCreateInputConnection(new EditorInfo());
            curEditText = (EditText) v;
            ViewGroup curParent = (ViewGroup) v.getParent();
            while (!(curParent instanceof RelativeLayout))
                curParent = (ViewGroup) curParent.getParent();
            moveToLayout(curParent);
            Show();
        }
        else {
            updateView(R.layout.keyboard);
            Hide();
        }

    }

    void moveToLayout(ViewGroup layout) {
        if (currentLayout != null)
            currentLayout.removeView(keyboardView);
        currentLayout = layout;
        currentLayout.addView(keyboardView);
    }

    void Show() {
        keyboardView.setVisibility(View.VISIBLE);
    }

    public void Hide() {
        switchSuperscript(true, getButtonById(R.id.btnPower));
        switchSubscript(true, getButtonById(R.id.btnSubscript));
        keyboardView.setVisibility(View.GONE);
    }

    void updateView(int layoutId) {
        currentLayout.removeView(keyboardView);
        keyboardView = inflater.inflate(layoutId, null);
        keyboardView.setLayoutParams(params);

        buttons.clear();
        getButtons((ViewGroup) keyboardView);
        listenToButtons();

        if (layoutId == R.layout.letter_keyboard && isSubscript)
            getButtonById(R.id.btnSubscript).setBackgroundColor(ContextCompat.getColor(context, R.color.grey));
        else if (layoutId != R.layout.letter_keyboard && isSuperscript)
            getButtonById(R.id.btnPower).setBackgroundColor(ContextCompat.getColor(context, R.color.grey));
        currentLayout.addView(keyboardView);
    }

    void getButtons(ViewGroup view) {
        for (int i = 0; i < view.getChildCount(); i++) {
            View curView = view.getChildAt(i);
            if (curView instanceof ViewGroup)
                getButtons((ViewGroup) curView);
            if (curView instanceof Button)
                buttons.add((Button)curView);
        }
    }

    Button getButtonById(int btnId) {
        for (Button btn: buttons)
            if (btn.getId() == btnId)
                return btn;
        return null;
    }

    void listenToButtons() {
        for (Button btn: buttons)
            btn.setOnClickListener(this);
    }

    public void connectToEditText(EditText t) {
        t.setShowSoftInputOnFocus(false);
        t.setTextIsSelectable(true);
        t.setOnFocusChangeListener(this);
        superscriptIndices.put(t, new ArrayList<>());
        subscriptIndices.put(t, new ArrayList<>());
    }

    public ArrayList<Integer> getSuperscriptIndices(EditText text) {
        return superscriptIndices.get(text);
    }

    public ArrayList<Integer> getSubscriptIndices(EditText text) {
        return subscriptIndices.get(text);
    }
}
