package com.yespustak.yespustakapp.utils;

import android.util.Log;
import android.widget.EditText;

import com.yespustak.yespustakapp.R;

public class Validator {

    //input type constants
    public static final int MOBILE_NO = 1;
    public static final int EMAIL = 2;
    public static final int PASSWORD = 3;
    public static final int NAME = 4;
    public static final int DOB = 5;
    public static final int WHATSAPP_NO = 6;
    public static final int USERNAME = 7;

    //TODO: later make it single function
    public static boolean validate(EditText inputField, int inputType) {
        return validate(inputField, inputType, true);
    }

    public static boolean validate(EditText inputField, int inputType, boolean animateError) {
        String input = inputField.getText().toString().trim();
        String errorMsg = null;
        String title;
        switch (inputType) {
            case NAME:
                title = utils.getStringResource(R.string.title_name);
                if (input.isEmpty())
                    errorMsg = utils.getStringResource(R.string.msg_field_required, title);
                else if (input.length() < 3)
                    errorMsg = utils.getStringResource(R.string.msg_field_min_limit, title, 3);
                else if (input.length() > 60)
                    errorMsg = utils.getStringResource(R.string.msg_field_max_limit_60, title);
                break;

            case EMAIL:
                title = utils.getStringResource(R.string.title_email);
                if (input.isEmpty())
                    errorMsg = utils.getStringResource(R.string.msg_field_required, title);
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches())
                    errorMsg = utils.getStringResource(R.string.msg_field_invalid, title);
                break;

            case MOBILE_NO:
                title = utils.getStringResource(R.string.title_mobile_number);
                if (input.isEmpty())
                    errorMsg = utils.getStringResource(R.string.msg_field_required, title);
                else if (input.length() != 10)
                    errorMsg = utils.getStringResource(R.string.msg_field_invalid, title);
                break;

            case DOB:
                title = utils.getStringResource(R.string.title_date_of_birth);
                if (input.isEmpty())
                    errorMsg = utils.getStringResource(R.string.msg_field_required, title);
                break;

            case PASSWORD:
                title = utils.getStringResource(R.string.title_password);
                if (input.isEmpty())
                    errorMsg = utils.getStringResource(R.string.msg_field_required, title);
                else if (input.length() < 6)
                    errorMsg = utils.getStringResource(R.string.msg_field_min_limit, title, 6);
                else if (input.length() > 10)
                    errorMsg = utils.getStringResource(R.string.msg_field_max_limit_10, title);
                break;
            case WHATSAPP_NO:
                title = utils.getStringResource(R.string.title_whatsapp_number);
                if (!input.isEmpty() && input.length() != 10)
                    errorMsg = utils.getStringResource(R.string.msg_field_invalid, title);

            case USERNAME:
                title = utils.getStringResource(R.string.title_username);
                if (!input.isEmpty() && input.length() < 3)
                    errorMsg = utils.getStringResource(R.string.msg_field_invalid, title);

            default:
                Log.e("Validator", "Invalid type");
        }

        utils.setError(inputField, errorMsg, animateError);
        return errorMsg == null;
    }

    public static boolean validateConfirmPassword(EditText etPassword, EditText etConfirmPassword, boolean animateError) {
        String errorMsg = null;
        if (!etPassword.getText().toString().trim().equals(etConfirmPassword.getText().toString().trim())) {
            errorMsg = utils.getStringResource(R.string.text_pass_not_match);
        }

        utils.setError(etConfirmPassword, errorMsg, animateError);
        return errorMsg == null;
    }
}
