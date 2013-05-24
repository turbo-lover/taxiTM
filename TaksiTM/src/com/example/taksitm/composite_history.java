package com.example.taksitm;

import android.content.Context;
import android.text.style.LineHeightSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.*;

/**
 * Created by turbo_lover on 24.05.13.
 */
public class composite_history extends RelativeLayout
{

    private TextView header_label;
    private EditText from_txt;
    private LinearLayout lst_view;


    public composite_history(Context context) {
        super(context);
        init_component();
    }

    private void init_component()
    {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.history_composite_element, this);
        header_label = (TextView) findViewById(R.id.label_header);
        from_txt = (EditText) findViewById(R.id.text_from);
        lst_view = (LinearLayout)findViewById(R.id.listView);
    }


    public void add_destination(String str)
    {
        EditText et = new EditText(getContext());
        et.setLayoutParams( new LayoutParams(LayoutParams.MATCH_PARENT,35));
        et.setBackgroundResource(R.drawable.text_input);
        et.setText(str);
        lst_view.addView(et);
    }
    public void add_header(String str)
    {
        //header_label.setText(str);
    }
}
