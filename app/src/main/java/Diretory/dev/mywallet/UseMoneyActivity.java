package Diretory.dev.mywallet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import Diretory.dev.mywallet.Database.DatabaseHandler;
import Diretory.dev.mywallet.Model.History;

import static henry.dev.mywallet.Internet.isOnline;

public class UseMoneyActivity extends AppCompatActivity {
    EditText amountMoney, detail;
    Spinner category;

    Button btnCalender;
    Button btnTime;
    Button btnSave;

    TextView tv_category;
    Calendar c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_money);

        //==============================admob===============================
        MobileAds.initialize(this, "ca-app-pub-9478209802818241~4089310448");
        final AdView mAdView = (AdView) findViewById(R.id.adView);
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
        if(isOnline(UseMoneyActivity.this)) {
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
                Intent main = new Intent(UseMoneyActivity.this, MainActivity.class);
                startActivity(main);
                overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
            }
        });

        amountMoney = (EditText) findViewById(R.id.amount_of_money_use_money);
        detail = (EditText) findViewById(R.id.detail_use_money);
        category = (Spinner) findViewById(R.id.category_use_money);


        c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

        btnCalender = (Button) findViewById(R.id.datePicker_use_money);
        String sTodayDate = df.format(c.getTime());
        btnCalender.setText(sTodayDate.trim());

        btnTime = (Button) findViewById(R.id.timePicker_use_money);
        String sHour_i, sMinute_i;
        if(String.valueOf(c.get(Calendar.HOUR_OF_DAY)).length()==1){
            sHour_i="0"+String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        }
        else {
            sHour_i=String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        }

        if(String.valueOf(c.get(Calendar.MINUTE)).length()==1){
            sMinute_i="0"+String.valueOf(c.get(Calendar.MINUTE));
        }
        else {
            sMinute_i=String.valueOf(c.get(Calendar.MINUTE));
        }
        btnTime.setText(sHour_i+":"+sMinute_i);

        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(UseMoneyActivity.this);
                new DatePickerDialog(view.getContext(), d, c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(UseMoneyActivity.this);
                new TimePickerDialog(view.getContext(), t, c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE), true).show();
            }
        });


        DatabaseHandler db = new DatabaseHandler(UseMoneyActivity.this);
        final Integer pSize = db.getCategoriesExpense().size();

        String[] itemsCategories = new String[pSize + 1];
        itemsCategories[0] = "[ Choose ]";
        for (int i = 0; i < pSize; i++) {
            itemsCategories[i+1] = db.getCategoriesExpense().get(i);
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(UseMoneyActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsCategories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categoryAdapter);

        category.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    hideKeyboard(UseMoneyActivity.this);
                }
                return false;
            }
        });
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                Object item1 = adapterView.getItemAtPosition(0);

                if (item != null) {
                    if (!item.toString().equalsIgnoreCase("[ Choose ]")){
                        tv_category = (TextView) findViewById(R.id.tv_category);
                        tv_category.setText(item.toString());
                    }else{
                        tv_category = (TextView) findViewById(R.id.tv_category);
                        tv_category.setText(item1.toString());
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSave = (Button) findViewById(R.id.btn_save_use_money);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveHistory();
            }
        });

    }

    private void saveHistory() {
        String sNext="No";

        DatabaseHandler db = new DatabaseHandler(UseMoneyActivity.this);
        Double sAmountMoney ;
        if(amountMoney.getText().toString().equalsIgnoreCase("")){
            sAmountMoney = 0.0;
        }
        else {
            sAmountMoney = Double.parseDouble(amountMoney.getText().toString());
        }


        Log.d("cek sblm roundusemoney",String.valueOf(sAmountMoney));
        sAmountMoney = roundTwoDecimals(sAmountMoney);
        Log.d("cek ssdh roundusemoney",String.valueOf(sAmountMoney));


        String sCategory = ((TextView) findViewById(R.id.tv_category)).getText().toString();
        String sDetail = detail.getText().toString();
        String sDate = btnCalender.getText().toString();
        String sTime = btnTime.getText().toString();

        String sDateEdited = sDate.replace("/","");
        sDateEdited = sDateEdited.substring(4,8)+sDateEdited.substring(2,4)+sDateEdited.substring(0,2);

        String sDateTime = sDateEdited+sTime.replace(":","");
        Log.d("Cek STRING", sDateTime);

        if(sAmountMoney<=0){
            Toast.makeText(UseMoneyActivity.this, "Please fill amount of money!!", Toast.LENGTH_LONG).show();
        }
        else {
            if(sCategory.equalsIgnoreCase("[ Choose ]")){
                Toast.makeText(UseMoneyActivity.this, "Please choose reason of using money!!", Toast.LENGTH_LONG).show();
            }
            else {
                sNext="Yes";
            }
        }

        if(sNext.equalsIgnoreCase("Yes")){
            db.addHistory(new History(sCategory,sAmountMoney,"-",sDetail,sDate,sTime,sDateTime));
            Intent main = new Intent(UseMoneyActivity.this, MainActivity.class);
            startActivity(main);
            overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
        }
    }

    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.parseDouble(twoDForm.format(d));
    }
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, monthOfYear);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String sTodayDate = df.format(c.getTime());
            btnCalender.setText(sTodayDate.trim());
        }
    };

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            String sHour, sMinute;
            if(String.valueOf(c.get(Calendar.HOUR_OF_DAY)).length()==1){
                sHour="0"+String.valueOf(c.get(Calendar.HOUR_OF_DAY));
            }
            else {
                sHour=String.valueOf(c.get(Calendar.HOUR_OF_DAY));
            }

            if(String.valueOf(c.get(Calendar.MINUTE)).length()==1){
                sMinute="0"+String.valueOf(c.get(Calendar.MINUTE));
            }
            else {
                sMinute=String.valueOf(c.get(Calendar.MINUTE));
            }

            btnTime.setText(sHour+":"+sMinute);
        }
    };

    public void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if(view ==null){
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}
