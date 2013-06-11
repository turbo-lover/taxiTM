package com.example.taksitm.layout;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.taksitm.MaskWatcher;
import com.example.taksitm.My_AsyncTask_Worker;
import com.example.taksitm.My_Preferences_Worker;
import com.example.taksitm.R;
import com.example.taksitm.Validation;
import com.example.taksitm.composite_order;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class OrderLayout extends Activity implements TextWatcher
{
    private Spinner spinner;
    private LinearLayout lin;
    private AutoCompleteTextView from_auto_compl;
    private EditText ed ;

    //разделение адрессов при передачи к подтверждающей активности
    private final char separator = ' ';


    private JSONObject orderJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_layout);

        init_variables();

        load_data_from_server();

        add_adr();

        set_from_pref();
    }

    private void set_from_pref()
    {
        My_Preferences_Worker my_pref = new My_Preferences_Worker(this);

        // записываем номер из настроек
       ed.setText(my_pref.get_user_number());
    }

    private void load_data_from_server()
    {
        spinner_load_cities();
        spinner_load_services();
    }


    private void init_variables()
    {
        ed = (EditText) findViewById(R.id.LayOrder_number);
        ed.addTextChangedListener(new MaskWatcher());

        orderJson = new JSONObject();
        lin = (LinearLayout) findViewById(R.id.LayOrder_linear_destination);
        spinner = (Spinner) findViewById(R.id.LayOrder_ed_txt_city);

        from_auto_compl = (AutoCompleteTextView) findViewById(R.id.LayOrder_from_txt);

        from_auto_compl.addTextChangedListener(this);



    }

    private class NetworkStateReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive( Context context, Intent intent )
        {
           if( Validation.isOnline(context))
                load_data_from_server();
        }
    }

    public void spinner_load_cities() {
        if (Validation.isOnline(this) == false) {
            makeText(this, "Отсутствует подключение к интернету", LENGTH_SHORT).show();
            return;
        }
        Spinner spr = (Spinner) findViewById(R.id.LayOrder_ed_txt_city);


        try {
            // получаем список городов от сервера
            List<String> list = get_cities();

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // !  наполняем список городов
            spr.setAdapter(dataAdapter);
        } catch (Exception je) {
            Log.d("onClick", je.getMessage());
            // je.printStackTrace();
        }

    }

    public void spinner_load_services() {
        if (Validation.isOnline(this) == false) {
            //Toast.makeText(this, "Отсутствует подключение к интернету", Toast.LENGTH_SHORT).show();
            return;
        }
        Spinner spr = (Spinner) findViewById(R.id.LayOrder_sp_txt_taxi_serv);

        //epyftv city_id
        Spinner city = (Spinner) findViewById(R.id.LayOrder_ed_txt_city);

        try {
            // получаем список городов от сервера
            List<String> list = get_service(city.getSelectedItemPosition() + 1);

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // !  наполняем список городов
            spr.setAdapter(dataAdapter);
        } catch (Exception je) {
            Log.d("onClick", je.getMessage());
            // je.printStackTrace();
        }

    }

    private List<String> get_service(int city_id) {
        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();
        JSONArray ja = new JSONArray();
        List<String> list = new ArrayList<String>();
        try
        {
            worker.execute(new JSONObject().put("city_id", city_id), "http://taxi-tm.ru/index/android_get_taxi_service");
            ja = worker.get().getJSONArray("taxi");

            for (int i = 0; i < ja.length(); i++) {
                JSONObject c = ja.getJSONObject(i);
                list.add(c.get("taxi_service").toString());

            }
        } catch (Exception e) {
            Log.d("get_city method", e.getMessage());
            return list;
        }
        return list;
    }

    private List<String> get_cities() {
        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();
        JSONArray ja = new JSONArray();
        List<String> list = new ArrayList<String>();
        try {
            worker.execute(new JSONObject().put("get_city", "lol"), "http://taxi-tm.ru/index/android_get_city");
            ja = worker.get().getJSONArray("city");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject c = ja.getJSONObject(i);
                list.add(c.get("title").toString());
            }
        } catch (Exception e) {
            Log.d("get_city method", e.getMessage());
            return list;
        }
        return list;
    }



    private void add_adr() {
        if (lin.getChildCount() != 3) {
            composite_order co = new composite_order(this);
            co.setSpinner(spinner);

            //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams()
            co.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            try {
                lin.addView(co, lin.getChildCount());
            } catch (Exception e) {
                Log.d("addr_add", e.getMessage());
            }

            return;
        }
    }

    public void add_adr(View v) {


        add_adr();


    }

    public void rem_adr(View v)
    {
        if(lin.getChildCount()>1)
        {
            lin.removeViewAt(lin.getChildCount()-1);
        }
    }

    public void by_city()
    {
        My_Preferences_Worker pref = new My_Preferences_Worker(this);
        composite_order co = (composite_order) lin.getChildAt(0);

        co.setTo("По городу");


    }

    public void to_home(View v)
    {
        My_Preferences_Worker pref = new My_Preferences_Worker(this);
       composite_order co = (composite_order) lin.getChildAt(0);

        co.setTo(pref.get_user_address());

        co.setTo_number(pref.get_user_address_house());
        co.setTo_corp(pref.get_user_address_corpus());
    }

    public void from_home(View v)
    {
        My_Preferences_Worker pref = new My_Preferences_Worker(this);

        AutoCompleteTextView to = (AutoCompleteTextView) findViewById(R.id.LayOrder_from_txt);
        EditText to_house = (EditText) findViewById(R.id.LayOrder_from_house);
        EditText to_corp = (EditText) findViewById(R.id.LayOrder_from_corp);

       to.setText(pref.get_user_address());

        to_house.setText(pref.get_user_address_house());
        to_corp.setText(pref.get_user_address_corpus());
    }


    public void OrderButton_click(View v)
    {
        //TODO some validation

        Spinner tx_serv = (Spinner) findViewById(R.id.LayOrder_sp_txt_taxi_serv);
        My_Preferences_Worker pw = new My_Preferences_Worker(this);
        EditText ed_comment = (EditText) findViewById(R.id.LayOrder_ed_txt_comment);
        EditText ed_number = (EditText) findViewById(R.id.LayOrder_number);

//        Собираем данные о точках назначения/ начала/ города/ и прочей поебени )
        String destination = getDestination(separator);
        String inception = get_inception(separator);
        String city = spinner.getSelectedItem().toString();
        String taxi_serv = ""+tx_serv.getSelectedItemPosition()+1;
        String user_id = pw.get_user_id();
        String comment = ed_comment.getText().toString();
        String number = ed_number.getText().toString();

        try
        {
            orderJson.put("destination",getDestination());
            orderJson.put("inception",get_inception());
            orderJson.put("city_id",spinner.getSelectedItemPosition()+1);
            orderJson.put("service", taxi_serv);
            orderJson.put("user_id", user_id);
            orderJson.put("comment", comment);
            orderJson.put("number", number);
        }
        catch(Exception e)
        {
            Log.d("put in json obj",e.getMessage());
        }


        try
        {
            taxi_serv = tx_serv.getSelectedItem().toString();

            Intent i = new Intent(this, ConfirmLayout.class);

            i.putExtra("destination", destination);
            i.putExtra("inception",inception);
            i.putExtra("city",city);
            i.putExtra("service",taxi_serv);

            i.putExtra("comment",comment);
            i.putExtra("number",number);

            startActivityForResult(i, 0);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
/////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==0)
        {
            finish();
            return;
        }
        if(resultCode == 2)
        {
            send_order_to_server();
            finish();
        }


    }

    private void send_order_to_server()
    {
        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();

        try
        {

            worker.execute(orderJson,"http://taxi-tm.ru/index/android_order");

            JSONObject j = worker.get();

            String str = j.get("success").toString();
            if(str.equals("ok"))
            {
                Toast.makeText(this,R.string.congragulation_order,LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.d("send_order", e.getMessage().toString());
        }
    }

    private String get_inception(char s)
    {
       EditText home = (EditText) findViewById(R.id.LayOrder_from_house);
       EditText corp = (EditText) findViewById(R.id.LayOrder_from_corp);

        String inception =  from_auto_compl.getText().toString()+s+home.getText().toString()+s+corp.getText().toString();
        return inception;
    }
    private JSONArray get_inception()
    {
        EditText home = (EditText) findViewById(R.id.LayOrder_from_house);
        EditText corp = (EditText) findViewById(R.id.LayOrder_from_corp);
        JSONObject ja = new JSONObject();
        JSONArray js = new JSONArray();
        try
        {
            ja.put("from",from_auto_compl.getText().toString());
            ja.put("from_house",home.getText().toString());
            ja.put("from_corpus",corp.getText().toString());

            js.put(ja);

        }
        catch(Exception e)
        {
            Log.d("inception",e.getMessage());
        }


        return js;
    }

    private String getDestination(char c)
    {
       StringBuilder destination = new StringBuilder();

        try
        {
            int count = lin.getChildCount();

            for (int i = 0; i < count; i++) {
                composite_order co = (composite_order) lin.getChildAt(i);

                destination.append(co.get_destination(c));
                destination.append("\n");
            }
        } catch (Exception e) {
            Log.d("get dynamyk element", e.getMessage());
        }
        return destination.toString();
    }


    private JSONArray getDestination()
    {
       JSONArray destination = new JSONArray();


        try
        {
            int count = lin.getChildCount();

            for (int i = 0; i < count; i++)
            {
                composite_order co = (composite_order) lin.getChildAt(i);

                JSONObject obj = new JSONObject();
                obj.put("to", co.get_to());
                obj.put("to_house", co.get_to_number());
                obj.put("to_corpus", co.get_to_corp());

                destination.put(obj);
            }
        } catch (Exception e) {
            Log.d("get dynamic element", e.getMessage());
        }
        return destination;
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence s, int i, int i2, int i3) {
        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();

        if (Validation.isOnline(this) == false) {
            Toast.makeText(this, R.string.dont_have_internet, Toast.LENGTH_SHORT).show();
            return;
        }


        if (s.length() == 2) {


            JSONObject jo = new JSONObject();

            try {
                jo.put("chars", s.toString());
                jo.put("city_id", spinner.getSelectedItemPosition() + 1);

                worker.execute(jo, "http://taxi-tm.ru/index/android_get_street");
                // TODO вынести все в отдельную функцию
                jo = worker.get();

                JSONArray arr = jo.getJSONArray("street");

                List<String> list = new ArrayList<String>();

                for (int f = 0; f < arr.length(); f++) {
                    JSONObject c = arr.getJSONObject(f);
                    list.add(c.get("title").toString());
                }

                ArrayAdapter<String[]> arrAd = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, list);
                from_auto_compl.setAdapter(arrAd);

                from_auto_compl.showDropDown();


            } catch (Exception e) {
                Log.d("to", e.getMessage());

            }

        }

    }

    @Override
    public void afterTextChanged(Editable editable)
    {

    }
}
