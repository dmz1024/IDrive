package com.ccpress.izijia.yd.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ccpress.izijia.R;

/**
 * Created by dengmingzhi on 16/5/29.
 */
public class TextEditView extends RelativeLayout {
    private EditText et_content;
    private OnTextChangeListener onTextChangeListener;

    public TextEditView(Context context) {
        this(context, null);
    }

    public TextEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextEditView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        View.inflate(getContext(), R.layout.yd_item_text_edit, this);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        et_content = (EditText) findViewById(R.id.et_content);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TextEditView);
        String tv_title_text = typedArray.getString(R.styleable.TextEditView_TextEditView_title);
        typedArray.recycle();
        tv_title.setText(tv_title_text);
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (onTextChangeListener != null) {
                    onTextChangeListener.change(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void setContent(String content) {
        et_content.setText(content);
    }

    public String getContent() {
        return et_content.getText().toString();
    }

    public interface OnTextChangeListener {
        void change(String content);
    }

    public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
        this.onTextChangeListener = onTextChangeListener;
    }
}
