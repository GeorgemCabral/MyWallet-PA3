package Dir.dev.mywallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import Dir.dev.mywallet.Adapter.HistoryAdapter;
import Dir.dev.mywallet.Adapter.ListAdapterEachMonth;
import Dir.dev.mywallet.Database.DatabaseHandler;
import Dir.dev.mywallet.Model.History;
import Dir.dev.mywallet.Model.User;

import static Dir.dev.mywallet.Internet.isOnline;

public class MainActivity extends AppCompatActivity {

    TextView tvIncome, tvExpense, tvBalance, tvCurrentUser, tvCurrentDate;

    //EACH MONTH
    Spinner spinnerCurrentMonth, spinnerCurrentYear;
    ImageView arrowLeftBtn, arrowRightBtn;

    TextView tvIncomeEachMonth, tvExpenseEachMonth, tvBalanceEachMonth;
    ListView listViewIncomeEachMonth, listViewExpenseEachMonth;

    //montly
    TextView tvAvgIncomePerDay, tvAvgExpensePerDay;
    //annual
    TextView tvAvgIncomePerMonth, tvAvgExpensePerMonth;

    CardView btnIncome, btnExpense, btnHistory, btnBudget, btnReport;

    ImageView btnSetting;

    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;

    DatabaseHandler db;
    User user;

    TextView tvIncomeTotal,tvExpenseTotal,tvTotalBalance;

    Calendar c;

    //MONTHLY
    String currentMonth, currentYear;
    String currentMonthText;

    ArrayList<History> historyListEachMonth;
    ListAdapterEachMonth adapter;

    ArrayAdapter<String> monthAdapter;
    ArrayAdapter<String> yearAdapter;

    //+++++++++++++++++++++++menu bar+++++++++++++++++++++++++++++++++++=
    String status_menu_bar; //"Daily","Monthly","Annual"
    LinearLayout menu_bar_daily, menu_bar_monthly, menu_bar_annual;
    TextView tvMenuDaily, tvMenuMonthly, tvMenuAnnual;

    //cardview permenu dan scrollview permenu
    CardView cardviewTitleDaily, cardviewTitleMonthly, cardviewTitleAnnual;
    ScrollView scrollViewDaily, scrollViewMonthly, scrollViewAnnual;

    //DAILY SPINNER
    //=======current picked spinner=========
    String currentDay_Daily ,currentMonth_Daily, currentYear_Daily;
    String currentMonthText_Daily;
    //=======current picked spinner=========

    //==========dailyTitle============
    Spinner currentDaySpinnerDaily, currentMonthSpinnerDaily, currentAnnualSpinnerDaily;
    ImageView left_arrow_btn_daily, right_arrow_btn_daily;
    //==========dailyTitle============

    //===========dailyDetail=============
    TextView tv_total_income_each_daily, tv_total_expense_each_daily, tv_total_balance_each_daily;
    ListView lv_sub_income_each_daily, lv_sub_expense_each_daily;

    ArrayList<History> historyListEachMonth_Daily;
    ListAdapterEachMonth adapter_Daily;

    ArrayAdapter<String> dayAdapter_Daily;
    ArrayAdapter<String> monthAdapter_Daily;
    ArrayAdapter<String> yearAdapter_Daily;
    //===========dailyDetail=============

    //ANNUAL SPINNER
    //=======current picked spinner=========
    String currentYear_Annual;
    //=======current picked spinner=========

    //==========ANNUAL Title============
    Spinner currentAnnualSpinnerAnnual;
    ImageView left_arrow_btn_annual, right_arrow_btn_annual;
    //==========ANNUAL Title============

    //===========ANNUAL Detail=============
    TextView tv_total_income_annual, tv_total_expense_annual;
    ListView lv_sub_income_annual, lv_sub_expense_annual;

    ArrayList<History> historyList_Annual;
    ListAdapterEachMonth adapter_Annual; //this adapter name is annoying sorry ;", can use for daily,monthly, and annual

    ArrayAdapter<String> yearAdapter_Annual;
    //===========ANNUAL Detail=============

    //+++++++++++++++++++++++menu bar+++++++++++++++++++++++++++++++++++=

    //TOTAL EACHday,month,year
    //Daily
    RelativeLayout relative_layout_total_eachday;
    TextView tv_in_cardview_total_each_day;
    //Monthly
    RelativeLayout relative_layout_total_eachmonth;
    TextView tv_in_cardview_total_each_month;
    //Annual
    RelativeLayout relative_layout_total_annual;
    TextView tv_in_cardview_total_annual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_v2);

        //==============================admob===============================
        MobileAds.initialize(this, "ca-app-pub-9478209802818241~4089310448");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        final LinearLayout admobLayout = findViewById(R.id.layout_admobView);
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
        if(isOnline(MainActivity.this)) {
            admobLayout.setVisibility(View.VISIBLE);
        }
        else {
            admobLayout.setVisibility(View.GONE);
        }
        //==============================admob===============================

        //total eachday
        relative_layout_total_eachday = (RelativeLayout) findViewById(R.id.relative_layout_total_eachday);
        tv_in_cardview_total_each_day = (TextView) findViewById(R.id.tv_in_cardview_total_each_day);
        relative_layout_total_eachday.setBackgroundColor(getResources().getColor(R.color.blue_icon_list));
        //total eachMonth
        relative_layout_total_eachmonth = (RelativeLayout) findViewById(R.id.relative_layout_total_eachmonth);
        tv_in_cardview_total_each_month = (TextView) findViewById(R.id.tv_in_cardview_total_each_month);
        relative_layout_total_eachmonth.setBackgroundColor(getResources().getColor(R.color.blue_icon_list));
        //total annual
        relative_layout_total_annual = (RelativeLayout) findViewById(R.id.relative_layout_total_annual);
        tv_in_cardview_total_annual = (TextView) findViewById(R.id.tv_in_cardview_total_annual);
        relative_layout_total_annual.setBackgroundColor(getResources().getColor(R.color.blue_icon_list));

        //=========menu bar===========
        //init
        status_menu_bar = "Daily"; //set
        menu_bar_daily = (LinearLayout) findViewById(R.id.layout_menu_bar_daily);
        menu_bar_monthly = (LinearLayout) findViewById(R.id.layout_menu_bar_monthly);
        menu_bar_annual = (LinearLayout) findViewById(R.id.layout_menu_bar_annual);

        tvMenuDaily = (TextView) findViewById(R.id.text_menu_daily);
        tvMenuMonthly = (TextView) findViewById(R.id.text_menu_monthly);
        tvMenuAnnual = (TextView) findViewById(R.id.text_menu_annual);

        tvMenuDaily.setTextColor(getResources().getColor(R.color.colorPrimary)); //set
        tvMenuMonthly.setTextColor(getResources().getColor(R.color.bgMainGrey));
        tvMenuAnnual.setTextColor(getResources().getColor(R.color.bgMainGrey));

        //Content per menu bar
        cardviewTitleDaily = (CardView) findViewById(R.id.cardview_title_detail_wallet_daily);
        cardviewTitleMonthly = (CardView) findViewById(R.id.cardview_title_detail_wallet_monthly);
        cardviewTitleAnnual = (CardView) findViewById(R.id.cardview_title_detail_wallet_annual);

        scrollViewDaily = (ScrollView) findViewById(R.id.scrollView_daily);
        scrollViewMonthly = (ScrollView) findViewById(R.id.scrollView_monthly);
        scrollViewAnnual = (ScrollView) findViewById(R.id.scrollView_annual);

        //=============DAILY TITLE SPINNER AND ARROW========
        currentDaySpinnerDaily = (Spinner) findViewById(R.id.currentDaySpinnerDaily);
        currentMonthSpinnerDaily = (Spinner) findViewById(R.id.currentMonthSpinnerDaily);
        currentAnnualSpinnerDaily = (Spinner) findViewById(R.id.currentYearSpinnerDaily);

        left_arrow_btn_daily = (ImageView) findViewById(R.id.left_arrow_btn_daily);
        right_arrow_btn_daily = (ImageView) findViewById(R.id.right_arrow_btn_daily);
        //=============DAILY TITLE SPINNER AND ARROW========

        //=============DAILY Detail each day========
        tv_total_income_each_daily = (TextView) findViewById(R.id.tv_total_income_each_daily);
        tv_total_expense_each_daily = (TextView) findViewById(R.id.tv_total_expense_each_daily);
        tv_total_balance_each_daily  = (TextView) findViewById(R.id.tv_total_balance_each_daily);

        lv_sub_income_each_daily = (ListView) findViewById(R.id.lv_sub_income_each_daily);
        lv_sub_expense_each_daily = (ListView) findViewById(R.id.lv_sub_expense_each_daily);
        //=============DAILY Detail each day========

        //=============ANNUAL TITLE SPINNER AND ARROW========
        currentAnnualSpinnerAnnual = (Spinner) findViewById(R.id.currentYearSpinner_Annual);

        left_arrow_btn_annual = (ImageView) findViewById(R.id.left_arrow_btn_annual);
        right_arrow_btn_annual = (ImageView) findViewById(R.id.right_arrow_btn_annual);
        //=============ANNUAL TITLE SPINNER AND ARROW========

        //=============ANNUAL Detail each year========
        tv_total_income_annual = (TextView) findViewById(R.id.tv_total_income_annual);
        tv_total_expense_annual = (TextView) findViewById(R.id.tv_total_expense_annual);

        lv_sub_income_annual = (ListView) findViewById(R.id.lv_sub_income_annual);
        lv_sub_expense_annual = (ListView) findViewById(R.id.lv_sub_expense_annual);

        tvAvgIncomePerMonth = (TextView) findViewById(R.id.tv_avg_income_permonth);
        tvAvgExpensePerMonth = (TextView) findViewById(R.id.tv_avg_expense_permonth);
        //=============ANNUAL Detail each year========

        menu_bar_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status_menu_bar = "Daily";
                tvMenuDaily.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvMenuMonthly.setTextColor(getResources().getColor(R.color.bgMainGrey));
                tvMenuAnnual.setTextColor(getResources().getColor(R.color.bgMainGrey));

                cardviewTitleDaily.setVisibility(View.VISIBLE);
                cardviewTitleMonthly.setVisibility(View.INVISIBLE);
                cardviewTitleAnnual.setVisibility(View.INVISIBLE);

                scrollViewDaily.setVisibility(View.VISIBLE);
                scrollViewMonthly.setVisibility(View.INVISIBLE);
                scrollViewAnnual.setVisibility(View.INVISIBLE);

                relative_layout_total_eachday.setVisibility(View.VISIBLE);
                relative_layout_total_eachmonth.setVisibility(View.INVISIBLE);
                relative_layout_total_annual.setVisibility(View.INVISIBLE);
            }
        });
        menu_bar_monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status_menu_bar = "Monthly";
                tvMenuDaily.setTextColor(getResources().getColor(R.color.bgMainGrey));
                tvMenuMonthly.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvMenuAnnual.setTextColor(getResources().getColor(R.color.bgMainGrey));

                cardviewTitleDaily.setVisibility(View.INVISIBLE);
                cardviewTitleMonthly.setVisibility(View.VISIBLE);
                cardviewTitleAnnual.setVisibility(View.INVISIBLE);

                scrollViewDaily.setVisibility(View.INVISIBLE);
                scrollViewMonthly.setVisibility(View.VISIBLE);
                scrollViewAnnual.setVisibility(View.INVISIBLE);

                relative_layout_total_eachday.setVisibility(View.INVISIBLE);
                relative_layout_total_eachmonth.setVisibility(View.VISIBLE);
                relative_layout_total_annual.setVisibility(View.INVISIBLE);
            }
        });
        menu_bar_annual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status_menu_bar = "Annual";
                tvMenuDaily.setTextColor(getResources().getColor(R.color.bgMainGrey));
                tvMenuMonthly.setTextColor(getResources().getColor(R.color.bgMainGrey));
                tvMenuAnnual.setTextColor(getResources().getColor(R.color.colorPrimary));

                cardviewTitleDaily.setVisibility(View.INVISIBLE);
                cardviewTitleMonthly.setVisibility(View.INVISIBLE);
                cardviewTitleAnnual.setVisibility(View.VISIBLE);

                scrollViewDaily.setVisibility(View.INVISIBLE);
                scrollViewMonthly.setVisibility(View.INVISIBLE);
                scrollViewAnnual.setVisibility(View.VISIBLE);

                relative_layout_total_eachday.setVisibility(View.INVISIBLE);
                relative_layout_total_eachmonth.setVisibility(View.INVISIBLE);
                relative_layout_total_annual.setVisibility(View.VISIBLE);
            }
        });
        //=========menu bar===========

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        db = new DatabaseHandler(MainActivity.this);

        c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String sDate = sdf.format(c.getTime());

        //set for Annual
        currentYear_Annual = sDate.substring(6,10);
        //set for MONTHLY
        currentMonth = sDate.substring(3,5);
        currentYear = sDate.substring(6,10);
        //set for DAILY
        currentDay_Daily = sDate.substring(0, 2);
        currentMonth_Daily = sDate.substring(3,5);
        currentYear_Daily = sDate.substring(6,10);

        //MONTHLY
        changeCurrentMonthToTextMonth(currentMonth, "monthly");
        changeCurrentMonthToTextMonth(currentMonth_Daily, "daily");
        spinnerCurrentMonth = (Spinner) findViewById(R.id.currentMonthSpinner);
        spinnerCurrentYear = (Spinner) findViewById(R.id.currentYearSpinner);

        //====================ANNUAL SPINNER================================
        //YEAR SPINNER
        Integer pSizeannual = db.getYearHistoryDistinct().size();
        int maxAnnual=0,minAnnual=0, totalYearAnnual=0;
        if(pSizeannual!=0){
            minAnnual=Integer.parseInt(db.getYearHistoryDistinct().get(0));
            maxAnnual=Integer.parseInt(db.getYearHistoryDistinct().get(pSizeannual - 1));
            totalYearAnnual = (maxAnnual-minAnnual)+1;
        }
        String[] itemsYearAnnual = new String[totalYearAnnual];
        if(pSizeannual==0){
            String[] itemsYearInitAnnual = new String[1];
            itemsYearInitAnnual[0] = currentYear_Annual;
            yearAdapter_Annual = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, itemsYearInitAnnual);
        }
        else {
            for (int i = 0; i < totalYearAnnual; i++) {
                itemsYearAnnual[i] = String.valueOf(minAnnual);
                minAnnual++;
            }
            yearAdapter_Annual = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, itemsYearAnnual);
        }

        yearAdapter_Annual.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentAnnualSpinnerAnnual.setAdapter(yearAdapter_Annual);

        currentAnnualSpinnerAnnual.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                //set Current Year from selected Year from spinner
                if (item != null) {
                    currentYear_Annual = item.toString();
                }
                setValueAnnual(currentYear_Annual);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        currentAnnualSpinnerAnnual.setSelection(yearAdapter_Annual.getPosition(currentYear_Annual));
        //====================ANNUAL SPINNER================================

        //====================MONTHLY SPINNER================================
        //MONTH SPINNER
        String[] itemsMonth = {"January","February","March","April","May","June","July","August","September","October","November","December"};

        monthAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, itemsMonth);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrentMonth.setAdapter(monthAdapter);
        spinnerCurrentMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Object item = adapterView.getItemAtPosition(position);

                //set Current Month from selected Month from spinner
                if (item != null) {
                    changeSpinnerTextToCurrentMonth(item.toString(), "monthly");
                }
                changeCurrentMonthToTextMonth(currentMonth, "monthly");
                setValueEachMonth(currentMonth, currentYear);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerCurrentMonth.setSelection(monthAdapter.getPosition(currentMonthText));
        //YEAR SPINNER

        Integer pSize = db.getYearHistoryDistinct().size();
        int max=0,min=0, totalYear=0;
        if(pSize!=0){
            min=Integer.parseInt(db.getYearHistoryDistinct().get(0));
            max=Integer.parseInt(db.getYearHistoryDistinct().get(pSize-1));
            totalYear = (max-min)+1;
        }
        String[] itemsYear = new String[totalYear];
        if(pSize==0){
            String[] itemsYearInit = new String[1];
            itemsYearInit[0] = currentYear;
            yearAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, itemsYearInit);
        }
        else {
            for (int i = 0; i < totalYear; i++) {
                itemsYear[i] = String.valueOf(min);
                min++;
            }
            yearAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, itemsYear);
        }

        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrentYear.setAdapter(yearAdapter);

        spinnerCurrentYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                //set Current Year from selected Year from spinner
                if (item != null) {
                    currentYear = item.toString();
                }
                setValueEachMonth(currentMonth, currentYear);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerCurrentYear.setSelection(yearAdapter.getPosition(currentYear));
        //====================MONTHLY SPINNER================================

        //====================DAILY SPINNER================================
        //Day SPINNER
        Calendar calendardaily = Calendar.getInstance();
        calendardaily.set(Calendar.YEAR, Integer.parseInt(currentYear_Daily));
        calendardaily.set(Calendar.MONTH, Integer.parseInt(currentMonth_Daily)-1);
        int numDaysdaily = calendardaily.getActualMaximum(Calendar.DATE);

        String[] itemsDay_Daily = new String [numDaysdaily];

        for (int i = 0; i < numDaysdaily; i++) {
            if((i+1)<10) itemsDay_Daily [i] = "0"+String.valueOf(i+1);
            else itemsDay_Daily [i] = String.valueOf(i+1);
        }
        dayAdapter_Daily = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, itemsDay_Daily);
        dayAdapter_Daily.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentDaySpinnerDaily.setAdapter(dayAdapter_Daily);

        currentDaySpinnerDaily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                //set Current Month from selected Month from spinner
                if (item != null) {
                    currentDay_Daily = item.toString();
                }
                setValueEachDay(currentDay_Daily,currentMonth_Daily, currentYear_Daily);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        currentDaySpinnerDaily.setSelection(dayAdapter_Daily.getPosition(currentDay_Daily));

        //MONTH SPINNER
        String[] itemsMonth_Daily = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        monthAdapter_Daily = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, itemsMonth_Daily);
        monthAdapter_Daily.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentMonthSpinnerDaily.setAdapter(monthAdapter_Daily);
        currentMonthSpinnerDaily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                //set Current Month from selected Month from spinner
                if (item != null) {
                    changeSpinnerTextToCurrentMonth(item.toString(), "daily");
                }
                changeCurrentMonthToTextMonth(currentMonth_Daily, "daily");
                //checking day
                Calendar calendardaily = Calendar.getInstance();
                calendardaily.set(Calendar.YEAR, Integer.parseInt(currentYear_Daily));
                calendardaily.set(Calendar.MONTH, Integer.parseInt(currentMonth_Daily)-1);
                int numDaysdaily = calendardaily.getActualMaximum(Calendar.DATE);
                String[] itemsDay_Daily = new String [numDaysdaily];

                for (int i = 0; i < numDaysdaily; i++) {
                    if((i+1)<10) itemsDay_Daily [i] = "0"+String.valueOf(i+1);
                    else itemsDay_Daily [i] = String.valueOf(i+1);
                }
                dayAdapter_Daily = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, itemsDay_Daily);
                dayAdapter_Daily.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                currentDaySpinnerDaily.setAdapter(dayAdapter_Daily);

                if(numDaysdaily<Integer.parseInt(currentDay_Daily)){
                    currentDaySpinnerDaily.setSelection(dayAdapter_Daily.getPosition(String.valueOf(numDaysdaily)));
                    currentDay_Daily=String.valueOf(numDaysdaily);
                }else{
                    currentDaySpinnerDaily.setSelection(dayAdapter_Daily.getPosition(currentDay_Daily));
                }
                setValueEachDay(currentDay_Daily,currentMonth_Daily, currentYear_Daily);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        currentMonthSpinnerDaily.setSelection(monthAdapter_Daily.getPosition(currentMonthText_Daily));
        //YEAR SPINNER
        Integer pSizeDaily = db.getYearHistoryDistinct().size();
        int maxDaily=0,minDaily=0, totalYearDaily=0;
        if(pSizeDaily!=0){
            minDaily=Integer.parseInt(db.getYearHistoryDistinct().get(0));
            maxDaily=Integer.parseInt(db.getYearHistoryDistinct().get(pSizeDaily-1));
            totalYearDaily = (maxDaily-minDaily)+1;
        }
        String[] itemsYear_daily = new String[totalYearDaily];
        if(pSizeDaily==0){
            String[] itemsYearInit_daily = new String[1];
            itemsYearInit_daily[0] = currentYear_Daily;
            yearAdapter_Daily = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, itemsYearInit_daily);
        }
        else {
            for (int i = 0; i < totalYearDaily; i++) {
                itemsYear_daily[i] = String.valueOf(minDaily);
                minDaily++;
            }
            yearAdapter_Daily = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, itemsYear_daily);
        }
        yearAdapter_Daily.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currentAnnualSpinnerDaily.setAdapter(yearAdapter_Daily);

        currentAnnualSpinnerDaily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                //set Current Year from selected Year from spinner
                if (item != null) {
                    currentYear_Daily = item.toString();
                }
                //checking day
                Calendar calendardaily = Calendar.getInstance();
                calendardaily.set(Calendar.YEAR, Integer.parseInt(currentYear_Daily));
                calendardaily.set(Calendar.MONTH, Integer.parseInt(currentMonth_Daily)-1);
                int numDaysdaily = calendardaily.getActualMaximum(Calendar.DATE);
                String[] itemsDay_Daily = new String [numDaysdaily];

                for (int i = 0; i < numDaysdaily; i++) {
                    if((i+1)<10) itemsDay_Daily [i] = "0"+String.valueOf(i+1);
                    else itemsDay_Daily [i] = String.valueOf(i+1);
                }
                dayAdapter_Daily = new ArrayAdapter<String>(MainActivity.this, R.layout.spinner_item, itemsDay_Daily);
                dayAdapter_Daily.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                currentDaySpinnerDaily.setAdapter(dayAdapter_Daily);

                if(numDaysdaily<Integer.parseInt(currentDay_Daily)){
                    currentDaySpinnerDaily.setSelection(dayAdapter_Daily.getPosition(String.valueOf(numDaysdaily)));
                    currentDay_Daily=String.valueOf(numDaysdaily);
                }else{
                    currentDaySpinnerDaily.setSelection(dayAdapter_Daily.getPosition(currentDay_Daily));
                }
                setValueEachDay(currentDay_Daily,currentMonth_Daily, currentYear_Daily);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        currentAnnualSpinnerDaily.setSelection(yearAdapter_Daily.getPosition(currentYear_Daily));
        //====================DAILY SPINNER================================

        //++++++++++detail each monthly++++++++++++++++
        tvIncomeEachMonth = (TextView) findViewById(R.id.tv_total_income_each_month);
        tvExpenseEachMonth = (TextView) findViewById(R.id.tv_total_expense_each_month);
        tvBalanceEachMonth = (TextView) findViewById(R.id.tv_total_balance_each_month);

        listViewIncomeEachMonth = (ListView) findViewById(R.id.lv_sub_income_each_month);
        listViewExpenseEachMonth = (ListView) findViewById(R.id.lv_sub_expense_each_month);

        tvAvgIncomePerDay = (TextView) findViewById(R.id.tv_avg_income_perday);
        tvAvgExpensePerDay = (TextView) findViewById(R.id.tv_avg_expense_perday);

        //SET EACH MONTH DETAIL PER CATEGORY
        //init
        setValueEachMonth(currentMonth, currentYear);
        arrowLeftBtn = (ImageView) findViewById(R.id.left_arrow_btn);
        arrowRightBtn = (ImageView) findViewById(R.id.right_arrow_btn);

        //ANNUAL ARROW
        left_arrow_btn_annual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int intCurrentYear=Integer.parseInt(currentYear_Annual);

                intCurrentYear--;
                if(yearAdapter_Annual.getPosition(String.valueOf(intCurrentYear))==-1){
                    intCurrentYear++;
                }
                currentYear_Annual = String.valueOf(intCurrentYear);

                currentAnnualSpinnerAnnual.setSelection(yearAdapter_Annual.getPosition(currentYear_Annual));

                setValueAnnual(currentYear_Annual);
            }
        });

        right_arrow_btn_annual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int intCurrentYear=Integer.parseInt(currentYear_Annual);

                intCurrentYear++;
                if(yearAdapter_Annual.getPosition(String.valueOf(intCurrentYear))==-1){
                    intCurrentYear--;
                }
                currentYear_Annual = String.valueOf(intCurrentYear);

                currentAnnualSpinnerAnnual.setSelection(yearAdapter_Annual.getPosition(currentYear_Annual));

                setValueAnnual(currentYear_Annual);
            }
        });

        //MONTHLY ARROW
        arrowLeftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int intCurrentMonth=Integer.parseInt(currentMonth);
                int intCurrentYear=Integer.parseInt(currentYear);

                intCurrentMonth--;
                if(intCurrentMonth==0){
                    intCurrentMonth=12;
                    intCurrentYear--;
                    if(yearAdapter.getPosition(String.valueOf(intCurrentYear))==-1){
                        intCurrentMonth=1;
                        intCurrentYear++;
                    }
                }

                currentMonth = String.valueOf(intCurrentMonth);
                currentYear = String.valueOf(intCurrentYear);

                if(currentMonth.length()==1) currentMonth = "0"+currentMonth;
                changeCurrentMonthToTextMonth(currentMonth, "monthly");
                spinnerCurrentMonth.setSelection(monthAdapter.getPosition(currentMonthText));


                spinnerCurrentYear.setSelection(yearAdapter.getPosition(currentYear));

                setValueEachMonth(currentMonth, currentYear);
            }
        });

        arrowRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int intCurrentMonth=Integer.parseInt(currentMonth);
                int intCurrentYear=Integer.parseInt(currentYear);

                intCurrentMonth++;
                if(intCurrentMonth==13){
                    intCurrentMonth=1;
                    intCurrentYear++;
                    if(yearAdapter.getPosition(String.valueOf(intCurrentYear))==-1){
                        intCurrentMonth=12;
                        intCurrentYear--;
                    }
                }

                currentMonth = String.valueOf(intCurrentMonth);
                currentYear = String.valueOf(intCurrentYear);

                if(currentMonth.length()==1) currentMonth = "0"+currentMonth;
                changeCurrentMonthToTextMonth(currentMonth, "monthly");
                spinnerCurrentMonth.setSelection(monthAdapter.getPosition(currentMonthText));
                spinnerCurrentYear.setSelection(yearAdapter.getPosition(currentYear));

                setValueEachMonth(currentMonth, currentYear);
            }
        });
        //++++++++++detail each month++++++++++++++++

        left_arrow_btn_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int intCurrentDay=Integer.parseInt(currentDay_Daily);
                int intCurrentMonth=Integer.parseInt(currentMonth_Daily);
                int intCurrentYear=Integer.parseInt(currentYear_Daily);
                //checking day
                Calendar calendardaily = Calendar.getInstance();

                intCurrentDay--;
                if(intCurrentDay==0){
                    intCurrentMonth--;
                    if(intCurrentMonth==0){
                        intCurrentMonth=12;
                        intCurrentYear--;
                        if(yearAdapter_Daily.getPosition(String.valueOf(intCurrentYear))==-1){
                            intCurrentDay=1;
                            intCurrentMonth=1;
                            intCurrentYear++;
                        }
                        else {
                            calendardaily.set(Calendar.YEAR, intCurrentYear);
                            calendardaily.set(Calendar.MONTH, intCurrentMonth-1);
                            int numDaysdaily = calendardaily.getActualMaximum(Calendar.DATE);
                            intCurrentDay = numDaysdaily;
                        }
                    }
                    else {
                        calendardaily.set(Calendar.YEAR, intCurrentYear);
                        calendardaily.set(Calendar.MONTH, intCurrentMonth-1);
                        int numDaysdaily = calendardaily.getActualMaximum(Calendar.DATE);
                        intCurrentDay = numDaysdaily;
                    }
                }

                currentDay_Daily = String.valueOf(intCurrentDay);
                currentMonth_Daily = String.valueOf(intCurrentMonth);
                currentYear_Daily = String.valueOf(intCurrentYear);

                if(currentDay_Daily.length()==1) currentDay_Daily= "0"+currentDay_Daily;
                if(currentMonth_Daily.length()==1) currentMonth_Daily = "0"+currentMonth_Daily;
                changeCurrentMonthToTextMonth(currentMonth_Daily, "daily");

                currentDaySpinnerDaily.setSelection(dayAdapter_Daily.getPosition(currentDay_Daily));
                currentMonthSpinnerDaily.setSelection(monthAdapter_Daily.getPosition(currentMonthText_Daily));
                currentAnnualSpinnerDaily.setSelection(yearAdapter_Daily.getPosition(currentYear_Daily));

                setValueEachDay(currentDay_Daily,currentMonth_Daily, currentYear_Daily);
            }
        });
        right_arrow_btn_daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int intCurrentDay=Integer.parseInt(currentDay_Daily);
                int intCurrentMonth=Integer.parseInt(currentMonth_Daily);
                int intCurrentYear=Integer.parseInt(currentYear_Daily);
                //checking day
                Calendar calendardaily = Calendar.getInstance();
                calendardaily.set(Calendar.YEAR, intCurrentYear);
                calendardaily.set(Calendar.MONTH, intCurrentMonth-1);
                int numDaysdaily = calendardaily.getActualMaximum(Calendar.DATE);

                intCurrentDay++;
                if(intCurrentDay>numDaysdaily){
                    intCurrentMonth++;
                    if(intCurrentMonth==13){
                        intCurrentMonth=1;
                        intCurrentYear++;
                        if(yearAdapter_Daily.getPosition(String.valueOf(intCurrentYear))==-1){
                            intCurrentDay=numDaysdaily;
                            intCurrentMonth=12;
                            intCurrentYear--;
                        }
                        else {
                            calendardaily.set(Calendar.YEAR, intCurrentYear);
                            calendardaily.set(Calendar.MONTH, intCurrentMonth-1);
                            numDaysdaily = calendardaily.getActualMaximum(Calendar.DATE);
                            intCurrentDay = 1;
                        }
                    }
                    else {
                        calendardaily.set(Calendar.YEAR, intCurrentYear);
                        calendardaily.set(Calendar.MONTH, intCurrentMonth-1);
                        numDaysdaily = calendardaily.getActualMaximum(Calendar.DATE);
                        intCurrentDay = 1;
                    }
                }

                currentDay_Daily = String.valueOf(intCurrentDay);
                currentMonth_Daily = String.valueOf(intCurrentMonth);
                currentYear_Daily = String.valueOf(intCurrentYear);

                if(currentDay_Daily.length()==1) currentDay_Daily= "0"+currentDay_Daily;
                if(currentMonth_Daily.length()==1) currentMonth_Daily = "0"+currentMonth_Daily;
                changeCurrentMonthToTextMonth(currentMonth_Daily, "daily");

                currentDaySpinnerDaily.setSelection(dayAdapter_Daily.getPosition(currentDay_Daily));
                currentMonthSpinnerDaily.setSelection(monthAdapter_Daily.getPosition(currentMonthText_Daily));
                currentAnnualSpinnerDaily.setSelection(yearAdapter_Daily.getPosition(currentYear_Daily));

                setValueEachDay(currentDay_Daily,currentMonth_Daily, currentYear_Daily);
            }
        });

        mRelativeLayout = (RelativeLayout) findViewById(R.id.r_layout_menu);

        user = db.getUser();

        tvCurrentUser = (TextView) findViewById(R.id.tv_current_username);
        tvCurrentUser.setText(user.getUsername());

        final Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy");
        String sTodayDate = df.format(c.getTime());
        tvCurrentDate = (TextView) findViewById(R.id.tv_current_date);
        tvCurrentDate.setText(sTodayDate.trim());

        tvIncome = (TextView) findViewById(R.id.tv_income_total);
        tvExpense = (TextView) findViewById(R.id.tv_expense_total);
        tvBalance = (TextView) findViewById(R.id.tv_total);

        btnIncome = (CardView) findViewById(R.id.add_income_btn_cardview);
        btnExpense = (CardView) findViewById(R.id.add_expense_btn_cardview);
        btnHistory = (CardView) findViewById(R.id.view_history_btn_cardview);
        btnBudget = (CardView) findViewById(R.id.view_budget_btn_cardview);
        btnSetting = (ImageView) findViewById(R.id.setting_btn_cardview);

        btnIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addMoney = new Intent( MainActivity.this, AddMoneyActivity.class);
                startActivity(addMoney);
            }
        });

        btnExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent useMoney = new Intent( MainActivity.this, UseMoneyActivity.class);
                startActivity(useMoney);
            }
        });

        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addMoney = new Intent( MainActivity.this, HistoryActivity.class);
                startActivity(addMoney);
            }
        });
        btnBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent budget = new Intent( MainActivity.this, BudgetActivity.class);
                startActivity(budget);
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent setting = new Intent( MainActivity.this, SettingActivity.class);
                startActivity(setting);
            }
        });

        double totalIncome=db.getTotalIncome();
        double totalExpense=db.getTotalExpense();
        double totalBalance=totalIncome-totalExpense;

        DecimalFormat twoDForm = new DecimalFormat("#,##0.00");

        tvIncome.setText( twoDForm.format(totalIncome));
        tvExpense.setText( twoDForm.format(totalExpense));
        tvBalance.setText( twoDForm.format(totalBalance));
    }

    private void changeSpinnerTextToCurrentMonth(String s, String type) {
        if(s.equalsIgnoreCase("January")){
            if(type.equalsIgnoreCase("daily")) currentMonth_Daily = "01";
            else if(type.equalsIgnoreCase("monthly")) currentMonth = "01";
        }
        else if(s.equalsIgnoreCase("February")) {
            if(type.equalsIgnoreCase("daily")) currentMonth_Daily = "02";
            else if(type.equalsIgnoreCase("monthly")) currentMonth = "02";
        }
        else if(s.equalsIgnoreCase("March")) {
            if(type.equalsIgnoreCase("daily")) currentMonth_Daily = "03";
            else if(type.equalsIgnoreCase("monthly")) currentMonth = "03";
        }
        else if(s.equalsIgnoreCase("April")) {
            if(type.equalsIgnoreCase("daily")) currentMonth_Daily = "04";
            else if(type.equalsIgnoreCase("monthly")) currentMonth = "04";
        }
        else if(s.equalsIgnoreCase("May")) {
            if(type.equalsIgnoreCase("daily")) currentMonth_Daily = "05";
            else if(type.equalsIgnoreCase("monthly")) currentMonth = "05";
        }
        else if(s.equalsIgnoreCase("June")) {
            if(type.equalsIgnoreCase("daily")) currentMonth_Daily = "06";
            else if(type.equalsIgnoreCase("monthly")) currentMonth = "06";
        }
        else if(s.equalsIgnoreCase("July")) {
            if(type.equalsIgnoreCase("daily")) currentMonth_Daily = "07";
            else if(type.equalsIgnoreCase("monthly")) currentMonth = "07";
        }
        else if(s.equalsIgnoreCase("August")) {
            if(type.equalsIgnoreCase("daily")) currentMonth_Daily = "08";
            else if(type.equalsIgnoreCase("monthly")) currentMonth = "08";
        }
        else if(s.equalsIgnoreCase("September")) {
            if(type.equalsIgnoreCase("daily")) currentMonth_Daily = "09";
            else if(type.equalsIgnoreCase("monthly")) currentMonth = "09";
        }
        else if(s.equalsIgnoreCase("October")) {
            if(type.equalsIgnoreCase("daily")) currentMonth_Daily = "10";
            else if(type.equalsIgnoreCase("monthly")) currentMonth = "10";
        }
        else if(s.equalsIgnoreCase("November")) {
            if(type.equalsIgnoreCase("daily")) currentMonth_Daily = "11";
            else if(type.equalsIgnoreCase("monthly")) currentMonth = "11";
        }
        else if(s.equalsIgnoreCase("December")) {
            if(type.equalsIgnoreCase("daily")) currentMonth_Daily = "12";
            else if(type.equalsIgnoreCase("monthly")) currentMonth = "12";
        }
    }

    private void changeCurrentMonthToTextMonth(String currentMonth, String type) {
        if(currentMonth.equalsIgnoreCase("01")){
            if(type.equalsIgnoreCase("daily")) currentMonthText_Daily = "January";
            else if(type.equalsIgnoreCase("monthly")) currentMonthText = "January";
        }
        else if(currentMonth.equalsIgnoreCase("02")) {
            if(type.equalsIgnoreCase("daily")) currentMonthText_Daily = "February";
            else if(type.equalsIgnoreCase("monthly")) currentMonthText = "February";
        }
        else if(currentMonth.equalsIgnoreCase("03")) {
            if(type.equalsIgnoreCase("daily")) currentMonthText_Daily = "March";
            else if(type.equalsIgnoreCase("monthly")) currentMonthText = "March";
        }
        else if(currentMonth.equalsIgnoreCase("04")) {
            if(type.equalsIgnoreCase("daily")) currentMonthText_Daily = "April";
            else if(type.equalsIgnoreCase("monthly")) currentMonthText = "April";
        }
        else if(currentMonth.equalsIgnoreCase("05")) {
            if(type.equalsIgnoreCase("daily")) currentMonthText_Daily = "May";
            else if(type.equalsIgnoreCase("monthly")) currentMonthText = "May";
        }
        else if(currentMonth.equalsIgnoreCase("06")) {
            if(type.equalsIgnoreCase("daily")) currentMonthText_Daily = "June";
            else if(type.equalsIgnoreCase("monthly")) currentMonthText = "June";
        }
        else if(currentMonth.equalsIgnoreCase("07")) {
            if(type.equalsIgnoreCase("daily")) currentMonthText_Daily = "July";
            else if(type.equalsIgnoreCase("monthly")) currentMonthText = "July";
        }
        else if(currentMonth.equalsIgnoreCase("08")) {
            if(type.equalsIgnoreCase("daily")) currentMonthText_Daily = "August";
            else if(type.equalsIgnoreCase("monthly")) currentMonthText = "August";
        }
        else if(currentMonth.equalsIgnoreCase("09")) {
            if(type.equalsIgnoreCase("daily")) currentMonthText_Daily = "September";
            else if(type.equalsIgnoreCase("monthly")) currentMonthText = "September";
        }
        else if(currentMonth.equalsIgnoreCase("10")) {
            if(type.equalsIgnoreCase("daily")) currentMonthText_Daily = "October";
            else if(type.equalsIgnoreCase("monthly")) currentMonthText = "October";
        }
        else if(currentMonth.equalsIgnoreCase("11")) {
            if(type.equalsIgnoreCase("daily")) currentMonthText_Daily = "November";
            else if(type.equalsIgnoreCase("monthly")) currentMonthText = "November";
        }
        else if(currentMonth.equalsIgnoreCase("12")) {
            if(type.equalsIgnoreCase("daily")) currentMonthText_Daily = "December";
            else if(type.equalsIgnoreCase("monthly")) currentMonthText = "December";
        }
    }

    private void setValueAnnual(String stringCurrentYear) {
        double totalIncome=db.getTotalIncomeAnnual(stringCurrentYear);
        double totalExpense=db.getTotalExpenseAnnual(stringCurrentYear);
        double totalBalance=totalIncome-totalExpense;

        double avgIncome = totalIncome/12;
        double avgExpense = totalExpense/12;

        DecimalFormat twoDForm = new DecimalFormat("#,##0.00");
        tv_total_income_annual.setText( twoDForm.format(totalIncome));
        tv_total_expense_annual.setText( twoDForm.format(totalExpense));

        if(totalBalance==0.0){
            relative_layout_total_annual.setBackgroundColor(getResources().getColor(R.color.blue_icon_list));
            tv_in_cardview_total_annual.setText(twoDForm.format(totalBalance));
        }
        else if(totalBalance<0.0){
            relative_layout_total_annual.setBackgroundColor(getResources().getColor(R.color.red_icon_expense));
            tv_in_cardview_total_annual.setText(twoDForm.format(totalBalance));
        }
        else {
            relative_layout_total_annual.setBackgroundColor(getResources().getColor(R.color.green_icon_income));
            tv_in_cardview_total_annual.setText("+"+twoDForm.format(totalBalance));
        }

        historyList_Annual = new ArrayList<History>();
        historyList_Annual = db.getAllHistoryIncomePerCategoryAnnual(stringCurrentYear);
        adapter_Annual = new ListAdapterEachMonth(this, historyList_Annual, totalIncome);
        lv_sub_income_annual.setAdapter(adapter_Annual);
        setListViewHeightBasedOnChildren(lv_sub_income_annual);

        historyList_Annual = db.getAllHistoryExpensePerCategoryAnnual(stringCurrentYear);
        adapter_Annual = new ListAdapterEachMonth(this, historyList_Annual, totalExpense);
        lv_sub_expense_annual.setAdapter(adapter_Annual);
        setListViewHeightBasedOnChildren(lv_sub_expense_annual);

        tvAvgIncomePerMonth.setText(twoDForm.format(avgIncome));
        tvAvgExpensePerMonth.setText(twoDForm.format(avgExpense));
    }

    private void setValueEachMonth(String stringCurrentMonth, String stringCurrentYear) {
        double totalIncome=db.getTotalIncomeEachMonth(stringCurrentMonth,stringCurrentYear);
        double totalExpense=db.getTotalExpenseEachMonth(stringCurrentMonth,stringCurrentYear);
        double totalBalance=totalIncome-totalExpense;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(stringCurrentYear));
        calendar.set(Calendar.MONTH, Integer.parseInt(stringCurrentMonth)-1);
        int numDays = calendar.getActualMaximum(Calendar.DATE);

        Log.d("ceknumday",stringCurrentMonth+" "+stringCurrentYear+" "+String.valueOf(numDays));

        double avgIncome = totalIncome/numDays;
        double avgExpense = totalExpense/numDays;

        DecimalFormat twoDForm = new DecimalFormat("#,##0.00");
        tvIncomeEachMonth.setText( twoDForm.format(totalIncome));
        tvExpenseEachMonth.setText( twoDForm.format(totalExpense));
        tvBalanceEachMonth.setText( twoDForm.format(totalBalance));

        //Monthly total
        if(totalBalance==0.0){
            relative_layout_total_eachmonth.setBackgroundColor(getResources().getColor(R.color.blue_icon_list));
            tv_in_cardview_total_each_month.setText(twoDForm.format(totalBalance));
        }
        else if(totalBalance<0.0){
            relative_layout_total_eachmonth.setBackgroundColor(getResources().getColor(R.color.red_icon_expense));
            tv_in_cardview_total_each_month.setText(twoDForm.format(totalBalance));
        }
        else {
            relative_layout_total_eachmonth.setBackgroundColor(getResources().getColor(R.color.green_icon_income));
            tv_in_cardview_total_each_month.setText("+"+twoDForm.format(totalBalance));
        }

        historyListEachMonth = new ArrayList<History>();
        historyListEachMonth = db.getAllHistoryIncomePerCategoryEachMonth(stringCurrentMonth,stringCurrentYear);
        adapter = new ListAdapterEachMonth(this, historyListEachMonth, totalIncome);
        listViewIncomeEachMonth.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listViewIncomeEachMonth);

        historyListEachMonth = db.getAllHistoryExpensePerCategoryEachMonth(stringCurrentMonth,stringCurrentYear);
        adapter = new ListAdapterEachMonth(this, historyListEachMonth, totalExpense);
        listViewExpenseEachMonth.setAdapter(adapter);
        setListViewHeightBasedOnChildren(listViewExpenseEachMonth);

        tvAvgIncomePerDay.setText(twoDForm.format(avgIncome));
        tvAvgExpensePerDay.setText(twoDForm.format(avgExpense));

    }

    private void setValueEachDay(String stringCurrentDay, String stringCurrentMonth, String stringCurrentYear) {
        double totalIncome=db.getTotalIncomeEachDay(stringCurrentDay, stringCurrentMonth,stringCurrentYear);
        double totalExpense=db.getTotalExpenseEachDay(stringCurrentDay, stringCurrentMonth,stringCurrentYear);
        double totalBalance=totalIncome-totalExpense;

        DecimalFormat twoDForm = new DecimalFormat("#,##0.00");
        tv_total_income_each_daily.setText( twoDForm.format(totalIncome));
        tv_total_expense_each_daily.setText( twoDForm.format(totalExpense));
        tv_total_balance_each_daily.setText( twoDForm.format(totalBalance));

        if(totalBalance==0.0){
            relative_layout_total_eachday.setBackgroundColor(getResources().getColor(R.color.blue_icon_list));
            tv_in_cardview_total_each_day.setText(twoDForm.format(totalBalance));
        }
        else if(totalBalance<0.0){
            relative_layout_total_eachday.setBackgroundColor(getResources().getColor(R.color.red_icon_expense));
            tv_in_cardview_total_each_day.setText(twoDForm.format(totalBalance));
        }
        else {
            relative_layout_total_eachday.setBackgroundColor(getResources().getColor(R.color.green_icon_income));
            tv_in_cardview_total_each_day.setText("+"+twoDForm.format(totalBalance));
        }

        historyListEachMonth_Daily = new ArrayList<History>();
        historyListEachMonth_Daily = db.getAllHistoryIncomePerCategoryEachDaily(stringCurrentDay,stringCurrentMonth,stringCurrentYear);
        adapter_Daily = new ListAdapterEachMonth(this, historyListEachMonth_Daily, totalIncome);
        lv_sub_income_each_daily.setAdapter(adapter_Daily);
        setListViewHeightBasedOnChildren(lv_sub_income_each_daily);

        historyListEachMonth_Daily = db.getAllHistoryExpensePerCategoryEachDaily(stringCurrentDay,stringCurrentMonth,stringCurrentYear);
        adapter_Daily = new ListAdapterEachMonth(this, historyListEachMonth_Daily, totalExpense);
        lv_sub_expense_each_daily.setAdapter(adapter_Daily);
        setListViewHeightBasedOnChildren(lv_sub_expense_each_daily);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Log out?");
        alertDialog.setIcon(R.drawable.ic_exit_to_app_black_24dp);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent signIn = new Intent( MainActivity.this, LoginPageActivity.class);
                signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signIn);
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
