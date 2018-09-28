package Diretory.dev.mywallet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

import Diretory.dev.mywallet.Database.DatabaseHandler;
import Diretory.dev.mywallet.Model.User;
import Diretory.dev.mywallet.Validation.InputValidation;

import static Diretory.dev.mywallet.BackUpAndRestore.makeFolder;
import static Diretory.dev.mywallet.BackUpAndRestore.verifyStoragePermissions;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout textInputLayoutUsername;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;

    private InputValidation inputValidation;

    EditText username, email, password, confirmPassword;
    Button registerBtn;

    private DatabaseHandler db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        makeFolder();
        verifyStoragePermissions(RegisterActivity.this);

        db = new DatabaseHandler(RegisterActivity.this);
        if(db.checkUser()){
            Intent signIn = new Intent(RegisterActivity.this, LoginPageActivity.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);
        }

        user = new User();
        db = new DatabaseHandler(RegisterActivity.this);

        username = (EditText)findViewById(R.id.username);
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        confirmPassword = (EditText)findViewById(R.id.confirm_password);

        textInputLayoutUsername = (TextInputLayout) findViewById(R.id.textInputLayoutUsername);
        textInputLayoutEmail = (TextInputLayout) findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = (TextInputLayout) findViewById(R.id.textInputLayoutPassword);
        textInputLayoutConfirmPassword = (TextInputLayout) findViewById(R.id.textInputLayoutConfirmPassword);

        inputValidation = new InputValidation(RegisterActivity.this);

        registerBtn = (Button) findViewById(R.id.register_button);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForm();
            }
        });
    }

    private void checkForm() {
        if(!inputValidation.isInputEditTextFilled(username, textInputLayoutUsername, getString(R.string.error_message))){
            return;
        }
        if(!inputValidation.isInputEditTextFilled(email, textInputLayoutEmail, getString(R.string.error_message))){
            return;
        }
        if(!inputValidation.isInputEditTextFilled(password, textInputLayoutPassword, getString(R.string.error_message))){
            return;
        }
        if(!inputValidation.isInputEditTextFilled(confirmPassword, textInputLayoutConfirmPassword, getString(R.string.error_message))){
            return;
        }

        if(!inputValidation.isInputEditTextEmailValid(email, textInputLayoutEmail, getString(R.string.error_email))){
            return;
        }

        if(!inputValidation.checkInputEditTextLengthMinimum(username, textInputLayoutUsername, getString(R.string.error_length_usernameMin))){
            return;
        }
        if(!inputValidation.checkInputEditTextLengthMinimum(password, textInputLayoutPassword, getString(R.string.error_length_passwordMin))){
            return;
        }

        if(!inputValidation.checkInputEditTextLengthMaximum(username, textInputLayoutUsername, getString(R.string.error_length_usernameMax))){
            return;
        }
        if(!inputValidation.checkInputEditTextLengthMaximum(password, textInputLayoutPassword, getString(R.string.error_length_passwordMax))){
            return;
        }

        if(!inputValidation.isInputConfirmPasswordTextMatch(password,confirmPassword, textInputLayoutConfirmPassword, getString(R.string.error_confirm_password))){
            return;
        }

        user.setUsername(username.getText().toString().trim());
        user.setEmail(email.getText().toString().trim());
        user.setPassword(password.getText().toString().trim());
        db.addUser(user);
        Intent signIn = new Intent( RegisterActivity.this, LoginPageActivity.class);
        signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signIn);
    }
}
