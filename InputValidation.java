package Dir.dev.mywallet.Validation;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;



public class InputValidation {
    private Context context;

    public InputValidation(Context context){
        this.context=context;
    }

    public boolean isInputEditTextFilled(EditText textInputEditText, TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        if(value.isEmpty()){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputEditTextEmailValid(EditText textInputEditText, TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(!value.matches(emailPattern)){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean checkInputEditTextLengthMinimum(EditText textInputEditText, TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        if(value.length()<4){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean checkInputEditTextLengthMaximum(EditText textInputEditText, TextInputLayout textInputLayout, String message){
        String value = textInputEditText.getText().toString().trim();
        if(value.length()>12){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputEditText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    public boolean isInputConfirmPasswordTextMatch(EditText textInputPasswordText,EditText textInputConfirmPasswordText, TextInputLayout textInputLayout, String message){
        String password = textInputPasswordText.getText().toString().trim();
        String confirmPassword = textInputConfirmPasswordText.getText().toString().trim();
        if(!password.equals(confirmPassword)){
            textInputLayout.setError(message);
            hideKeyboardFrom(textInputPasswordText);
            hideKeyboardFrom(textInputConfirmPasswordText);
            return false;
        }
        else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }

    private void hideKeyboardFrom(View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
