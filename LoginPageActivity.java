package Diretory.dev.mywallet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import Diretory.dev.mywallet.Database.DatabaseHandler;
import Diretory.dev.mywallet.Model.User;
import Diretory.dev.mywallet.Validation.InputValidation;

import static Diretory.dev.mywallet.BackUpAndRestore.importDB;
import static Diretory.dev.mywallet.BackUpAndRestore.makeFolder;
import static Diretory.dev.mywallet.BackUpAndRestore.verifyStoragePermissions;
import static Diretory.dev.mywallet.Internet.isOnline;

public class LoginPageActivity extends AppCompatActivity {
    EditText username,password;
    Button signInBtn;

    DatabaseHandler db;
    InputValidation inputValidation;
    User user;

    TextView recovery_password_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        makeFolder();
        verifyStoragePermissions(LoginPageActivity.this);

        inputValidation = new InputValidation(LoginPageActivity.this);
        db = new DatabaseHandler(LoginPageActivity.this);
        user = db.getUser();

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        signInBtn = (Button) findViewById(R.id.sign_in_button);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyFromDatabase();
            }
        });

        recovery_password_button = (TextView) findViewById(R.id.recovery_password_button);
        recovery_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progress = new ProgressDialog(LoginPageActivity.this);
                final String passwordUser=user.getPassword();
                final String emailUser=user.getEmail();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginPageActivity.this);
                alertDialog.setTitle("Forgot Password");
                alertDialog.setMessage("Do you want to send your password\nto your email?");
                alertDialog.setIcon(R.drawable.ic_lock_black_24dp);
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isOnline(LoginPageActivity.this)) {
                            String version = "";

                            try {
                                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                                version = pInfo.versionName;
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            try {
                                GMailSender sender = new GMailSender(LoginPageActivity.this, "mywallet.official@gmail.com",
                                        "mywalletpassword");
                                sender.sendMail("MyWallet " + version, "Password:" + passwordUser,
                                        emailUser, emailUser);

                                progress.setTitle("Loading");
                                progress.setMessage("Wait while sending...");
                                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                                progress.show();
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginPageActivity.this);
                                alertDialog.setTitle("Mail");
                                alertDialog.setMessage("The password was sent to:\n" + emailUser);
                                alertDialog.setIcon(R.drawable.ic_mail_black_24dp);
                                alertDialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                alertDialog.show();
                            } catch (Exception e) {
                                Log.e("SendMail", e.getMessage(), e);
                            }
                        }
                        else {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginPageActivity.this);
                            alertDialog.setTitle("No internet connection.");
                            alertDialog.setIcon(R.drawable.ic_error_black_24dp);
                            alertDialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            alertDialog.show();
                        }

                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        progress.dismiss();

                    }
                });
                alertDialog.show();
            }
        });
        /*
        registerBtnText = (TextView) findViewById(R.id.register_button_text);
        registerBtnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginPageActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });
        */
    }

    private void verifyFromDatabase() {
        if(db.checkPassword(user.getUsername(), password.getText().toString().trim())){
            Intent signIntent = new Intent(LoginPageActivity.this, MainActivity.class);
            signIntent.putExtra("Username",user.getUsername());
            signIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(signIntent);
        }
        else {
            Toast.makeText(this, "Incorrect password!", Toast.LENGTH_LONG).show();
        }
    }
}
