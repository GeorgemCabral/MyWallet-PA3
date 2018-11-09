package Dir.dev.mywallet;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import Dir.dev.mywallet.Database.DatabaseHandler;
import Dir.dev.mywallet.Model.User;
import Dir.dev.mywallet.Validation.InputValidation;

import static Dir.dev.mywallet.Internet.isOnline;

public class SettingActivity extends AppCompatActivity {
    CardView btnEditProfile, btnInfo, btnCategory, btnDatabase;

    DatabaseHandler db;
    User user;
    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;

    private InputValidation inputValidation;

    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //==============================admob===============================
        MobileAds.initialize(this, "ca-app-pub-9478209802818241~4089310448");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        final CardView admobLayout = findViewById(R.id.cardview_admob);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded(){
                admobLayout.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAdFailedToLoad(int errorCode){
                admobLayout.setVisibility(View.GONE);
            }
        });
        if(isOnline(SettingActivity.this)) {
            admobLayout.setVisibility(View.VISIBLE);
        }
        else {
            admobLayout.setVisibility(View.GONE);
        }
        //==============================admob===============================

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ImageView imageViewArroBack = (ImageView) findViewById(R.id.btnBackArrow);
        imageViewArroBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(SettingActivity.this, MainActivity.class);
                startActivity(main);
                overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
            }
        });


        mRelativeLayout = (RelativeLayout) findViewById(R.id.r_layout_setting);

        inputValidation = new InputValidation(SettingActivity.this);

        btnDatabase = (CardView) findViewById(R.id.database_btn_cardview);
        btnDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent databaseIntent = new Intent(SettingActivity.this, DatabaseActivity.class);
                startActivity(databaseIntent);
            }
        });

        btnCategory = (CardView) findViewById(R.id.category_btn_cardview);
        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryIntent = new Intent(SettingActivity.this, CategoryActivity.class);
                startActivity(categoryIntent);
            }
        });


        btnEditProfile = (CardView) findViewById(R.id.edit_profile_btn_cardview);
        btnInfo = (CardView) findViewById(R.id.info_btn_cardview);

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                // Inflate the custom layout/view
                View customView = inflater.inflate(R.layout.popup_view,null);
                // Initialize a new instance of popup window
                mPopupWindow = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

                // Get a reference for the custom view close button
                ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                //TAMBAHAN DIM POPUP WINDOW
                final ViewGroup root = (ViewGroup) getWindow().getDecorView().getRootView();
                // Set a click listener for the popup window close button
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        clearDim(root);
                        mPopupWindow.dismiss();
                    }
                });
                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        clearDim(root);
                    }
                });

                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setFocusable(true);
                //mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                applyDim(root, 0.5f);
                // Finally, show the popup window at the center location of root relative layout
                mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                // Inflate the custom layout/view
                View customView = inflater.inflate(R.layout.popup_view_password,null);

                db = new DatabaseHandler(SettingActivity.this);
                user = db.getUser();
                userId=String.valueOf(user.getUserId());

                // Initialize a new instance of popup window
                mPopupWindow = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

                // Get a reference for the custom view close button
                ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                final EditText etUsername = (EditText) customView.findViewById(R.id.et_username);
                final EditText etPassword = (EditText) customView.findViewById(R.id.et_password);
                final EditText etConfirmPassword = (EditText) customView.findViewById(R.id.et_confirm_password);
                final EditText etEmail = (EditText) customView.findViewById(R.id.et_email);

                final TextInputLayout textInputLayoutUsername = (TextInputLayout) customView.findViewById(R.id.textInputLayoutUsername);
                final TextInputLayout textInputLayoutEmail = (TextInputLayout) customView.findViewById(R.id.textInputLayoutEmail);
                final TextInputLayout textInputLayoutPassword = (TextInputLayout) customView.findViewById(R.id.textInputLayoutPassword);
                final TextInputLayout textInputLayoutConfirmPassword = (TextInputLayout) customView.findViewById(R.id.textInputLayoutConfirmPassword);

                etUsername.setText(user.getUsername());
                etPassword.setText(user.getPassword());
                etConfirmPassword.setText(user.getPassword());
                etEmail.setText(user.getEmail());

                Button btnCancel = (Button) customView.findViewById(R.id.btn_cancel_edit);
                Button btnSave = (Button) customView.findViewById(R.id.btn_save_edit);

                //TAMBAHAN DIM POPUP WINDOW
                final ViewGroup root = (ViewGroup) getWindow().getDecorView().getRootView();

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clearDim(root);
                        mPopupWindow.dismiss();
                    }
                });
                // Set a click listener for the popup window close button
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        clearDim(root);
                        mPopupWindow.dismiss();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String next="No";
                        if(!inputValidation.isInputEditTextFilled(etUsername, textInputLayoutUsername, getString(R.string.error_message)) ||
                                !inputValidation.isInputEditTextFilled(etEmail, textInputLayoutEmail, getString(R.string.error_message))||
                                !inputValidation.isInputEditTextFilled(etPassword, textInputLayoutPassword, getString(R.string.error_message))||
                                !inputValidation.isInputEditTextFilled(etConfirmPassword, textInputLayoutConfirmPassword, getString(R.string.error_message))){
                            next="No";
                            Log.d("CEK valid","kosong");
                        }
                        else {
                            if(!inputValidation.isInputEditTextEmailValid(etEmail, textInputLayoutEmail, getString(R.string.error_email))){
                                next="No";
                                Log.d("CEK valid","emailvalid");
                            }
                            else {
                                if(!inputValidation.checkInputEditTextLengthMinimum(etUsername, textInputLayoutUsername, getString(R.string.error_length_usernameMin))||
                                        !inputValidation.checkInputEditTextLengthMinimum(etPassword, textInputLayoutPassword, getString(R.string.error_length_passwordMin))){
                                    next="No";
                                    Log.d("CEK valid","userminim");
                                }
                                else {
                                    if(!inputValidation.checkInputEditTextLengthMaximum(etUsername, textInputLayoutUsername, getString(R.string.error_length_usernameMax))||
                                            !inputValidation.checkInputEditTextLengthMaximum(etPassword, textInputLayoutPassword, getString(R.string.error_length_passwordMax))){
                                        next="No";
                                        Log.d("CEK valid","usermax");
                                    }
                                    else {
                                        if(!inputValidation.isInputConfirmPasswordTextMatch(etPassword,etConfirmPassword, textInputLayoutConfirmPassword, getString(R.string.error_confirm_password))){
                                            next="No";
                                            Log.d("CEK valid","passwordmatch");
                                        }
                                        else {
                                            next="Yes";
                                        }
                                    }
                                }
                            }
                        }


                        Log.d("CEK NEXT",next);
                        if(next.equalsIgnoreCase("Yes")){
                            User userEdit = new User();
                            userEdit.setUserId(Integer.parseInt(userId));
                            userEdit.setUsername(etUsername.getText().toString().trim());
                            userEdit.setEmail(etEmail.getText().toString().trim());
                            userEdit.setPassword(etPassword.getText().toString().trim());
                            db.updateUser(userEdit);
                            clearDim(root);
                            mPopupWindow.dismiss();
                        }
                    }
                });

                mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        clearDim(root);
                    }
                });

                mPopupWindow.setOutsideTouchable(true);
                mPopupWindow.setFocusable(true);
                //mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                applyDim(root, 0.5f);
                // Finally, show the popup window at the center location of root relative layout
                mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);
            }
        });
    }
    public static void applyDim( ViewGroup parent, float dimAmount){
        Drawable dim = new ColorDrawable(Color.BLACK);
        dim.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        dim.setAlpha((int) (255 * dimAmount));

        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.add(dim);
    }

    public static void clearDim( ViewGroup parent) {
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.clear();
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(SettingActivity.this, MainActivity.class);
        startActivity(main);
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }
}
