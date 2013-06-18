package com.example.taksitm.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

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
import static com.example.taksitm.R.*;

public class OrderLayout extends Activity implements TextWatcher
{

    private Spinner city_spinner;
    private LinearLayout lin;
    private AutoCompleteTextView from_auto_compl;
    private EditText ed ;
    String entered_before = "";
    String city_id ="";

    //разделение адрессов при передачи к подтверждающей активности
    private final char separator = ' ';


    private JSONObject orderJson;
    private ArrayList<String> numberList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.order_layout);

        init_variables();

        load_data_from_server();

        add_adr();

        set_from_pref();


        // узнаем из какой активити мы сюда пришли )
        parse_previous_activity();

    }

    private void parse_previous_activity()
    {

        Intent intent = getIntent();

        String previous_activity = intent.getStringExtra("previous");

        //если из основной но нифига не делаем или если что обрабатываем здесь
        //CHECKED проверка того что пришли со страницы выбора )
        if(previous_activity.equals(ChoiceLayout.class.toString()))
        {
            Toast.makeText(this,"choise = ",LENGTH_SHORT).show();
            return;
        }

        //
        if(previous_activity.equals(HistoryLayout.class.toString()))
        {
            try
            {
                String str_with_json = intent.getStringExtra("json");

                JSONObject json = new JSONObject(str_with_json);

                String city = json.getString("city");
                city_spinner.setSelection(Integer.getInteger(city)-1);

                String[] inception = json.getString("from").split("/");

                from_auto_compl.setText(inception[0]);

                //только если не пробел, это показатель пустоты к сожалению так (
                if(!inception[1].equals(" "))
                {
                     EditText from_house= (EditText) findViewById(id.LayOrder_from_house);
                    from_house.setText( inception[1]);
                }

                if(!inception[2].equals(" "))
                {
                    EditText from_house= (EditText) findViewById(id.LayOrder_from_corp);
                    from_house.setText( inception[2]);
                }

            }
            catch (Exception e)
            {
                Log.d("adding",e.getMessage());
            }

        }



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
        ed = (EditText) findViewById(id.LayOrder_number);
        ed.addTextChangedListener(new MaskWatcher());
        city_spinner = (Spinner) findViewById(id.LayOrder_ed_txt_city);

        orderJson = new JSONObject();
        lin = (LinearLayout) findViewById(id.LayOrder_linear_destination);

        city_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                entered_before = "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                city_spinner.setSelection(0);

            }
        });

        from_auto_compl = (AutoCompleteTextView) findViewById(id.LayOrder_from_txt);

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

    public void spinner_load_cities()
    {
        if (Validation.isOnline(this) == false)
        {
            makeText(this, "Отсутствует подключение к интернету", LENGTH_SHORT).show();
            return;
        }
        Spinner spr = (Spinner) findViewById(id.LayOrder_ed_txt_city);


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

    public void spinner_load_services()
    {
        if (Validation.isOnline(this) == false)
        {
            //Toast.makeText(this, "Отсутствует подключение к интернету", Toast.LENGTH_SHORT).show();
            return;
        }
        Spinner spr = (Spinner) findViewById(id.LayOrder_sp_txt_taxi_serv);

        //epyftv city_id


        try {
            // получаем список городов от сервера
            List<String> list = get_service(city_spinner.getSelectedItemPosition() + 1);

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // !  наполняем список городов
            spr.setAdapter(dataAdapter);
        }
        catch (Exception je)
        {
            Log.d("onClick", je.getMessage());
            // je.printStackTrace();
        }

    }

    private List<String> get_service(int city_id)
    {
        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();
        JSONArray ja = new JSONArray();
        List<String> list = new ArrayList<String>();
        numberList = new ArrayList<String>();
        try
        {
            worker.execute(new JSONObject().put("city_id", city_id),"http://taxi-tm.ru/index/android_get_taxi_service");
            ja = worker.get().getJSONArray("taxi");

            for (int i = 0; i < ja.length(); i++)
            {
                JSONObject c = ja.getJSONObject(i);
                list.add(c.get("taxi_service").toString());


                String[] phoneNumbers = c.getString("service_phonenumber").split(",");
                for (String number:phoneNumbers)
                {
                    numberList.add(number);
                }

            }
        }
        catch (Exception e)
        {
            Log.d("get_city method", e.getMessage());
            return list;
        }
        return list;
    }

    private List<String> get_cities()
    {
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



    private void add_adr()
    {
        if (lin.getChildCount() != 3)
        {
            composite_order co = new composite_order(this);
            co.setSpinner(city_spinner);

            //LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams()
            co.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            try
            {
                lin.addView(co, lin.getChildCount());
            }
            catch (Exception e)
            {
                Log.d("addr_add", e.getMessage());
            }

            return;
        }
    }

    public void add_adr(View v)
    {


        add_adr();


    }

    public void rem_adr(View v)
    {
        if(lin.getChildCount()>1)
        {
            lin.removeViewAt(lin.getChildCount()-1);
        }
    }

    public void by_city(View v)
    {
        My_Preferences_Worker pref = new My_Preferences_Worker(this);
        composite_order co = (composite_order) lin.getChildAt(0);

        co.setTo("По городу");


    }

    public void to_home(View v)
    {
        My_Preferences_Worker pref = new My_Preferences_Worker(this);
       composite_order co = (composite_order) lin.getChildAt(0);

        try
        {
            co.setTo(pref.get_user_address());
            co.setTo_number(pref.get_user_address_house());
            co.setTo_corp(pref.get_user_address_corpus());
        }
        catch (Exception e)
        {
            Log.d("to_home(View v)",e.getMessage());
        }
    }

    public void from_home(View v)
    {
        My_Preferences_Worker pref = new My_Preferences_Worker(this);

        AutoCompleteTextView to = (AutoCompleteTextView) findViewById(id.LayOrder_from_txt);
        EditText to_house = (EditText) findViewById(id.LayOrder_from_house);
        EditText to_corp = (EditText) findViewById(id.LayOrder_from_corp);

       to.setText(pref.get_user_address());

        to_house.setText(pref.get_user_address_house());
        to_corp.setText(pref.get_user_address_corpus());
    }

    public void call_service(View v)
    {
        //CHECKED подгрузить с сервера список номеров

        //CHECKED предоставить возможность выбора номера из списка


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        CharSequence[] charSequences = numberList.toArray(new CharSequence[numberList.size()]);

        AlertDialog.Builder builder;
        builder = alertDialog.setItems(charSequences,new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                try
                {
                    ListView lw = ((AlertDialog)dialogInterface).getListView();
                    String checkedItem = lw.getAdapter().getItem(i).toString();
                    Intent callIntent = new Intent(Intent.ACTION_CALL);


                    callIntent.setData(Uri.parse("tel:" + checkedItem));
                    startActivity(callIntent);
                }
                catch (Exception ex)
                {
                    Log.d("onClick sing line", ex.getMessage());
                }
            }
        }
        );

        builder.show();



    }
    // * * * * * * * * * * Copy-pasted code, but working correctly. Yeah!! * * * * * * * * * * * * * * * * * * * * * * *

    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;

        String LOG_TAG = "LOGGING 123";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            if (TelephonyManager.CALL_STATE_RINGING == state) {
                // phone ringing
                Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
            }

            if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
                // active
                Log.i(LOG_TAG, "OFFHOOK");

                isPhoneCalling = true;
            }

            if (TelephonyManager.CALL_STATE_IDLE == state) {
                // run when class initial and phone call ended,
                // need detect flag from CALL_STATE_OFFHOOK
                Log.i(LOG_TAG, "IDLE");

                if (isPhoneCalling) {

                    Log.i(LOG_TAG, "restart app");

                    // restart app
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(
                                    getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                    isPhoneCalling = false;
                }

            }
        }
    }
    // * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
    public void OrderButton_click(View v)
    {
        //TODO some validation

        Spinner tx_serv = (Spinner) findViewById(id.LayOrder_sp_txt_taxi_serv);
        My_Preferences_Worker pw = new My_Preferences_Worker(this);
        EditText ed_comment = (EditText) findViewById(id.LayOrder_ed_txt_comment);
        EditText ed_number = (EditText) findViewById(id.LayOrder_number);

//        Собираем данные о точках назначения/ начала/ города/ и прочей поебени )
        String destination = getDestination(separator);
        String inception = get_inception(separator);
        String city = city_spinner.getSelectedItem().toString();
        String taxi_serv = ""+tx_serv.getSelectedItemPosition()+1;
        String user_id = pw.get_user_id();
        String comment = ed_comment.getText().toString();
        String number = ed_number.getText().toString();

        if(Validation.isNull(from_auto_compl.getText().toString()))
        {
            Toast.makeText(this,"Заполните поле отправки",LENGTH_SHORT).show();
            return;
        }
        if(Validation.isNull(destination))
        {
            Toast.makeText(this,"Заполните поле конечного адресса",LENGTH_SHORT).show();
            return;
        }
        if(number.length()!=13)
        {
            Toast.makeText(this,"Заполните поле телефонного номера",LENGTH_SHORT).show();
            return;

        }

        try
        {
            orderJson.put("destination",getDestination());
            orderJson.put("inception",get_inception());
            orderJson.put("city_id", city_spinner.getSelectedItemPosition()+1);
            orderJson.put("service", taxi_serv);
            orderJson.put("user_id", user_id);
            orderJson.put("comment", comment);

            StringBuilder sb = new StringBuilder();

            for (char c : ed_number.getText().toString().toCharArray())
            {
                if(Character.isDigit(c))
                    sb.append(c);
            };
            orderJson.put("number", sb.toString());
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
            // CHECKED: handle exception
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
                Toast.makeText(this, string.congragulation_order,LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            Log.d("send_order", e.getMessage().toString());
        }
    }

    private String get_inception(char s)
    {
       EditText home = (EditText) findViewById(id.LayOrder_from_house);
       EditText corp = (EditText) findViewById(id.LayOrder_from_corp);

        String inception =  from_auto_compl.getText().toString()+s+home.getText().toString()+s+corp.getText().toString();
        return inception;
    }
    private JSONArray get_inception()
    {
        EditText home = (EditText) findViewById(id.LayOrder_from_house);
        EditText corp = (EditText) findViewById(id.LayOrder_from_corp);
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
            Toast.makeText(this, string.dont_have_internet, Toast.LENGTH_SHORT).show();
            return;
        }


        if (s.length() == 2 && s.toString().equals(entered_before)==false)
        {

            entered_before = s.toString();
            JSONObject jo = new JSONObject();

            try {
                jo.put("chars", s.toString());
                jo.put("city_id", city_spinner.getSelectedItemPosition() + 1);
                city_id= ""+ city_spinner.getSelectedItemPosition() + 1;

                worker.execute(jo, "http://taxi-tm.ru/index/android_get_street");
                // TODO вынести все в отдельную функцию
                jo = worker.get();

                JSONArray arr = new JSONArray();
                if(jo.getString("status").equals("ok"))
                    arr = jo.getJSONArray("street");

                List<String> list = new ArrayList<String>();

                for (int f = 0; f < arr.length(); f++) {
                    JSONObject c = arr.getJSONObject(f);
                    list.add(c.get("title").toString());
                }

                ArrayAdapter<String[]> arrAd = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, list);
                from_auto_compl.setAdapter(arrAd);

                from_auto_compl.showDropDown();


            }
            catch (Exception e) {
                Log.d("to", e.getMessage());

            }

        }

    }

    @Override
    public void afterTextChanged(Editable editable)
    {

    }
}
