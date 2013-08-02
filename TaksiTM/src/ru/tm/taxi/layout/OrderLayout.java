package ru.tm.taxi.layout;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.tm.taxi.*;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static android.widget.Toast.LENGTH_SHORT;
import static ru.tm.taxi.R.layout.order_layout;


public class OrderLayout extends Activity implements TextWatcher
{

    private Spinner city_spinner;
    private LinearLayout lin;
    private AutoCompleteTextView from_auto_compl;
    private EditText ed ;
    String entered_before = "";
    String city_id ="";
    Dictionary _service_numbers;

    //разделение адрессов при передачи к подтверждающей активности
    private final char separator = ' ';


    private JSONObject orderJson;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(order_layout);

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
            return;
        }

        //
        if(previous_activity.equals(HistoryLayout.class.toString()))
        {
            try
            {
                String order_id = intent.getStringExtra("order_id");
                My_AsyncTask_Worker assn_worker = new My_AsyncTask_Worker();

                assn_worker.execute(new JSONObject("{\"order_id\":\""+ order_id +"\"}"),"http://taxi-tm.ru/index/android_get_order");

                JSONObject obj = assn_worker.get().getJSONObject("order");

                String city = obj.getString("city");
                String service = obj.getString("taxi");
                String from = obj.getString("from");
                String from_corp = obj.getString("from_house_corpus");
                String from_house = obj.getString("from_house");
                String to = obj.getString("to");
                String to_corp = obj.getString("to_house_corpus");
                String to_house = obj.getString("to_house");
                String to_2 = obj.getString("to_2");
                String to_corp_2 = obj.getString("to_house_corpus_2");
                String to_house_2 = obj.getString("to_house_2");
                String to_3 = obj.getString("to_3");
                String to_corp_3 = obj.getString("to_house_corpus_3");
                String to_house_3 = obj.getString("to_house_3");

                Spinner spr_service = (Spinner) findViewById(R.id.LayOrder_sp_txt_taxi_serv);
                //EditText et_from        =(EditText)findViewById(R.id);
                EditText et_from_corp   =(EditText)findViewById(R.id.LayOrder_from_corp);
                EditText et_from_house  =(EditText)findViewById(R.id.LayOrder_from_house);

                composite_order co_to = (composite_order) lin.getChildAt(0);

                int id = Integer.parseInt(city);
                city_spinner.setSelection(id-1);

                spr_service.setSelection(Integer.parseInt(service)-1);

                from_auto_compl.setText(from);
                et_from_corp.setText(from_corp);
                et_from_house.setText(from_house);

                co_to.setTo(to);
                co_to.setTo_corp(to_corp);
                co_to.setTo_number(to_house);

                if(to_2.length() !=0)
                {
                    add_adr();
                    composite_order co_to_2= (composite_order) lin.getChildAt(1);
                    co_to_2.setTo(to_2);
                    co_to_2.setTo_corp(to_corp_2);
                    co_to_2.setTo_number(to_house_2);
                }

                if(to_3.length() !=0)
                {
                    add_adr();
                    composite_order co_to_3 = (composite_order) lin.getChildAt(2);
                    co_to_3.setTo(to_3);
                    co_to_3.setTo_corp(to_corp_3);
                    co_to_3.setTo_number(to_house_3);
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
        _service_numbers = new Hashtable();
        ed = (EditText) findViewById(R.id.LayOrder_number);
        ed.addTextChangedListener(new MaskWatcher());
        city_spinner = (Spinner) findViewById(R.id.LayOrder_ed_txt_city);

        orderJson = new JSONObject();
        lin = (LinearLayout) findViewById(R.id.LayOrder_linear_destination);

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

    public void spinner_load_cities()
    {
        if (Validation.isOnline(this) == false)
        {
            Toast.makeText(this, R.string.dont_have_internet, Toast.LENGTH_SHORT).show();

            return;
        }
        Spinner spr = (Spinner) findViewById(R.id.LayOrder_ed_txt_city);

//        if(spr.getCount()==0)
//        {
        try
        {
            // получаем список городов от сервера
            JSONArray jsonArrayCity = getJsonArraCity();


            List<String> list = get_cities(jsonArrayCity);

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            // !  наполняем список городов
            spr.setAdapter(dataAdapter);

            List<String> id_list = new ArrayList<String>();



            for(int i=0; i<jsonArrayCity.length(); i++)
            {
                JSONObject id = jsonArrayCity.getJSONObject(i);
                id_list.add(id.getString("id"));
            }

            spr.setTag(id_list);

        }
        catch (Exception je)
        {
            Log.d("onClick", je.getMessage());
            // je.printStackTrace();
        }
        //}
    }

    public void spinner_load_services()
    {
        if (Validation.isOnline(this) == false)
        {
            //Toast.makeText(this, "Отсутствует подключение к интернету", Toast.LENGTH_SHORT).show();
            return;
        }
        Spinner spr = (Spinner) findViewById(R.id.LayOrder_sp_txt_taxi_serv);

        //epyftv city_id


        try {
            // получаем список сервисов от сервера

            ArrayList<String> tag = (ArrayList<String>) city_spinner.getTag();
            List<String> list = get_service( tag.get(city_spinner.getSelectedItemPosition()));

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            // !  наполняем список городов
            spr.setAdapter(dataAdapter);
        }
        catch (Exception je)
        {
            Log.d("onClick", je.getMessage());
            // je.printStackTrace();
        }

    }

    private List<String> get_service(String city_id)
    {
        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();
        JSONArray ja = new JSONArray();
        List<String> list = new ArrayList<String>();

        try
        {
            worker.execute(new JSONObject().put("city_id", city_id),"http://taxi-tm.ru/index/android_get_taxi_service");
            ja = worker.get().getJSONArray("taxi");

            for (int i = 0; i < ja.length(); i++)
            {
                JSONObject c = ja.getJSONObject(i);
                list.add(c.get("taxi_service").toString());

                ArrayList<String> numberList = new ArrayList<String>();
                String[] phoneNumbers = c.getString("service_phonenumber").split(",");
                for (String number:phoneNumbers)
                {
                    numberList.add(number);
                }
                _service_numbers.put(i,numberList);

            }
        }
        catch (Exception e)
        {
            Log.d("get_city method", e.getMessage());
            return list;
        }
        return list;
    }

    private JSONArray getJsonArraCity()
    {
        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();
        JSONArray ja = new JSONArray();

        try {
            worker.execute(new JSONObject().put("get_city","lol"), "http://taxi-tm.ru/index/android_get_city");
            ja = worker.get().getJSONArray("city");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        catch (ExecutionException e) {
            e.printStackTrace();
        }
        catch (Exception e){

        }

        return ja;

    }


    private List<String> get_cities(JSONArray ja)
    {

        List<String> list= new ArrayList<String>();
        try
        {

            for(int i = 0; i < ja.length(); i++)
            {
                JSONObject c = ja.getJSONObject(i);
                list.add(c.get("title").toString());
            }
        }
        catch(Exception e)
        {
            Log.d("get_city method",e.getMessage());
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
            co.setTag(lin.getChildCount());

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
        co.setTo_number("");
        co.setTo_corp("");


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

        AutoCompleteTextView to = (AutoCompleteTextView) findViewById(R.id.LayOrder_from_txt);
        EditText to_house = (EditText) findViewById(R.id.LayOrder_from_house);
        EditText to_corp = (EditText) findViewById(R.id.LayOrder_from_corp);

       to.setText(pref.get_user_address());

        to_house.setText(pref.get_user_address_house());
        to_corp.setText(pref.get_user_address_corpus());
    }

    public void call_service(View v)
    {
        //CHECKED подгрузить с сервера список номеров

        //CHECKED предоставить возможность выбора номера из списка


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        Spinner servcv = (Spinner) findViewById(R.id.LayOrder_sp_txt_taxi_serv);

        ArrayList<String> numberList = (ArrayList<String>) _service_numbers.get(servcv.getSelectedItemPosition());

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

        Spinner tx_serv = (Spinner) findViewById(R.id.LayOrder_sp_txt_taxi_serv);
        My_Preferences_Worker pw = new My_Preferences_Worker(this);
        EditText ed_comment = (EditText) findViewById(R.id.LayOrder_ed_txt_comment);
        EditText ed_number = (EditText) findViewById(R.id.LayOrder_number);

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
        StringBuilder sb = new StringBuilder();

        for (char c : ed_number.getText().toString().toCharArray())
        {
            if(Character.isDigit(c))
                sb.append(c);
        };

        try
        {
            orderJson.put("destination",getDestination());
            orderJson.put("inception",get_inception());
            orderJson.put("city_id", city_spinner.getSelectedItemPosition()+1);
            orderJson.put("service", taxi_serv);
            orderJson.put("user_id", user_id);
            orderJson.put("comment", comment);


            orderJson.put("number", sb.toString());
        }
        catch(Exception e)
        {
            Log.d("put in json obj",e.getMessage());
        }


        try
        {
//            My_AsyncTask_Worker work = new My_AsyncTask_Worker();
//            Spinner servcv = (Spinner) findViewById(id.LayOrder_sp_txt_taxi_serv);
//
//            JSONObject priceObj = getDestination(true);
//            priceObj.put("city_id", city_spinner.getSelectedItemPosition()+1);
//            priceObj.put("number", sb.toString());
//            priceObj.put("from", from_auto_compl.getText().toString());
//            priceObj.put("service", servcv.getSelectedItemPosition()+1);
//
//            work.execute(priceObj,"http://taxi-tm.ru/index/android_get_price");
//
//            priceObj = work.get();
            String price= "Уточнит оператор";
//            if(priceObj.getString("success").equals("ok"))
//                price = priceObj.getString("price");

            taxi_serv = tx_serv.getSelectedItem().toString();


            Intent i = new Intent(this, ConfirmLayout.class);

            i.putExtra("destination", destination);
            i.putExtra("inception",inception);
            i.putExtra("city",city);
            i.putExtra("service",taxi_serv);

            i.putExtra("comment",comment);
            i.putExtra("number",number);
            i.putExtra("price",price);

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
            JSONObject jsonObject = send_order_to_server();

            try
            {
                if(ParseOrderResponse(jsonObject))
                {
                    finish();
                }

            }
            catch (Exception ex)
            {

            }
        }


    }

    private JSONObject send_order_to_server()
    {
        My_AsyncTask_Worker worker = new My_AsyncTask_Worker();
        JSONObject j = new JSONObject();
        try
        {

            worker.execute(orderJson,"http://taxi-tm.ru/index/android_order");


            j = worker.get();

            return j;
        }
        catch (Exception e)
        {
            Log.d("send_order", e.getMessage().toString());
        }
        return j;
    }

    //было лень думать, быстро допиливал функционал по этому
    // парсим результат заказа, если ok то true, если operator то false и выводит сообщение
    private Boolean ParseOrderResponse(JSONObject j) throws JSONException
    {
        try
        {
            String str = j.get("success").toString();
            if(str.equals("ok"))
            {
                Toast.makeText(this, R.string.congragulation_order, LENGTH_SHORT).show();
                return true;
            }
            if(str.equals("operator"))
            {
                Toast.makeText(this, R.string.operator_off,Toast.LENGTH_LONG).show();
                return false;
            }
        }
        catch (Exception ex)
        {
            Log.d("ParseOrderResponse",ex.getMessage().toString());
        }
        return false;
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

    private JSONObject getDestination(Boolean fake)
    {
        JSONObject obj = new JSONObject();


        try
        {
            int count = lin.getChildCount();

            for (int i = 0; i < count; i++)
            {
                composite_order co = (composite_order) lin.getChildAt(i);

              switch (i)
              {
                  case 0:
                      obj.put("to", co.get_to());
                      break;
                  case 1:
                      obj.put("to_2", co.get_to());
                      break;
                  case 2:
                      obj.put("to_3", co.get_to());
                      break;
              }
            }
        } catch (Exception e) {
            Log.d("get dynamic element", e.getMessage());
        }
        return obj;
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


        if (s.length() == 2 && s.toString().equals(entered_before)==false)
        {

            entered_before = s.toString();
            JSONObject jo = new JSONObject();

            try {

                ArrayList<String> city_id_list= (ArrayList<String>) city_spinner.getTag();


                String city = ""+city_id_list.get( city_spinner.getSelectedItemPosition());

                jo.put("chars", s.toString());
                jo.put("city_id", city);
                city_id= city;

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

                ArrayAdapter<String[]> arrAd = new ArrayAdapter(this, android.R.layout.simple_spinner_item, list);
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
