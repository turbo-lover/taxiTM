package ru.tm.taxi;


import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.ParseException;

/**
 * Created by Turbo on 08.06.13.
 */
public class MaskWatcher  implements TextWatcher
{
    private EditText phone;
    private String mask = "(___)___-____";
    String mResult = "";

    public MaskWatcher()
    {
        initialize_component();
    }

    public MaskWatcher(String string_of_mask)
    {
        initialize_component();
        mask = string_of_mask;
    }

    private void initialize_component()
    {
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void afterTextChanged(Editable s) {

        //s.insert(1,"7");
        String value = s.toString();



        if(value.equals(mResult))
            return;

        try {

            // prepare the formatter
            MaskFormatter formatter = new MaskFormatter(mask);
            formatter.setValueContainsLiteralCharacters(false);
            formatter.setPlaceholderCharacter((char)1);

            // get a string with applied mask and placeholder chars
            value = formatter.valueToString(value);

            try{

                // find first placeholder
                value = value.substring(0, value.indexOf((char)1));

                //process a mask char
                while (value.charAt(value.length()-1) == mask.charAt(value.length()-1))
                {
                    value = value.substring(0, value.length() - 1);
                }

            }
            catch(Exception e){}




            mResult = value;


            s.replace(0, s.length(), mResult);


        } catch (ParseException e) {

            //the entered value does not match a mask
            int offset = e.getErrorOffset();
            value = removeCharAt(value, offset);
            s.replace(0, s.length(), value);

        }
    }

    public static String removeCharAt(String s, int pos) {

        StringBuffer buffer = new StringBuffer(s.length() - 1);
        buffer.append(s.substring(0, pos)).append(s.substring(pos + 1));
        return buffer.toString();

    }
}
