package ru.tm.taxi;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by turbo_lover on 03.06.13.
 */
public class composite_order extends RelativeLayout implements TextWatcher
{
    Spinner spinner ;
    private AutoCompleteTextView  to;

    private EditText to_number,to_corp;


    public composite_order(Context context)
    {
        super(context);


        init_component();
    }
    public void setSpinner(Spinner parent_spinner)
    {
        spinner = parent_spinner;
    }

    private void init_component()
    {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.order_composit_element, this);

        to = (AutoCompleteTextView) findViewById(R.id.to);
        to_number = (EditText) findViewById(R.id.to_numb);
        to_corp = (EditText) findViewById(R.id.to_corp);

       to.addTextChangedListener(this);
    }

    public String get_to()
    {
        return to.getText().toString();
    }

    public String get_to_number()
    {
        return to_number.getText().toString();
    }

    public String get_to_corp()
    {
        return to_corp.getText().toString();
    }

    public String get_destination(char separator)
    {
       String dest = get_to()+separator+ get_to_number()+separator+get_to_corp();

        return dest;
    }

    public void setTo_number (String str_to_number )
    {
            to_number.setText(str_to_number);
    }

    public void setTo_corp(String str_to_corp)
    {

        to_corp.setText(str_to_corp);
    }

    public void setTo(String str_to)
    {
        to.setText(str_to);

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence s, int i, int i2, int i3)
    {
        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();

        if(Validation.isOnline(getContext()) == false)
        {
            Toast.makeText(getContext(),R.string.dont_have_internet,Toast.LENGTH_SHORT).show();
            return;
        }


        if(s.length() == 2)
        {


            JSONObject jo = new JSONObject();

            try
            {
                jo.put("chars",s.toString());
                jo.put("city_id",spinner.getSelectedItemPosition()+1 );

                worker.execute(jo, "http://taxi-tm.ru/index/android_get_street");
                // TODO вынести все в отдельную функцию
                jo = worker.get();

                JSONArray arr =  jo.getJSONArray("street");

                List<String> list = new ArrayList<String>();

                for(int f = 0; f < arr.length(); f++)
                {
                    JSONObject c = arr.getJSONObject(f);
                    list.add(c.get("title").toString());
                }

                ArrayAdapter<String[]> arrAd = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, list);
                to.setAdapter(arrAd);

                to.showDropDown();


            }
            catch(Exception e)
            {
                Log.d("to",e.getMessage());

            }

        }
    }

    @Override
    public void afterTextChanged(Editable editable)
    {

    }
}

