package Diretory.dev.mywallet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import java.util.ArrayList;
import java.util.Calendar;

import Diretory.dev.mywallet.Adapter.HistoryAdapter;
import Diretory.dev.mywallet.Database.DatabaseHandler;
import Diretory.dev.mywallet.Model.History;
import Diretory.dev.mywallet.Model.User;

import static Diretory.dev.mywallet.BackUpAndRestore.importDB;
import static Diretory.dev.mywallet.BackUpAndRestore.makeFolder;
import static Diretory.dev.mywallet.BackUpAndRestore.verifyStoragePermissions;
import static Diretory.dev.mywallet.Internet.isOnline;

public class HistoryActivity extends AppCompatActivity {

    DatabaseHandler db;
    HistoryAdapter adapter;
    //ListView
    ListView listView;
    ArrayList<History> historyList;

    TextView sortingStatus, incomeBtnStatus, expenseBtnStatus;

    ImageView sortingBtn, incomeBtn, expenseBtn, searchBtn;

    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;

    Calendar c;

    Spinner spinnerMonth, spinnerYear, spinnerCategory;
    ArrayAdapter<String> monthAdapter;
    ArrayAdapter<String> yearAdapter;
    ArrayAdapter<String> categoryAdapter;

    //FILTER PARAMETER
     String currentMonth="00", currentYear= "All";
     String currentMonthText;
     String currentCategory= "All";
     String currentIncomeBtn="Active", currentExpenseBtn="Active";
     String currentSortingStatus="Desc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

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
        if(isOnline(HistoryActivity.this)) {
            admobLayout.setVisibility(View.VISIBLE);
        }
        else {
            admobLayout.setVisibility(View.GONE);
        }
        //==============================admob===============================

        searchBtn = (ImageView) findViewById(R.id.search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(HistoryActivity.this, SearchActivity.class);
                startActivity(searchIntent);
            }
        });

        ImageView imageViewArroBack = (ImageView) findViewById(R.id.btnBackArrow);
        imageViewArroBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(main);
                overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
            }
        });


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        c = Calendar.getInstance();

        db = new DatabaseHandler(HistoryActivity.this);

        spinnerMonth = (Spinner) findViewById(R.id.spinner_filter_month_history);
        spinnerYear = (Spinner) findViewById(R.id.spinner_filter_year_history);
        spinnerCategory = (Spinner) findViewById(R.id.spinner_filter_category_history);

        mRelativeLayout = (RelativeLayout) findViewById(R.id.r_layout_history_money);

//        tvTotalIncome = (TextView) findViewById(R.id.tv_total_income_history);
//        tvTotalExpense = (TextView) findViewById(R.id.tv_total_expense_history);

        DecimalFormat twoDForm = new DecimalFormat("#,##0.00");

//        tvTotalIncome.setText( twoDForm.format(db.getTotalIncome()));
//        tvTotalExpense.setText( twoDForm.format(db.getTotalExpense()));

        sortingStatus = (TextView) findViewById(R.id.sorting_status);
        sortingStatus.setText("Desc");

        incomeBtnStatus = (TextView) findViewById(R.id.income_button_status);
        incomeBtnStatus.setText("Selected");
        expenseBtnStatus = (TextView) findViewById(R.id.expense_button_status);
        expenseBtnStatus.setText("Selected");

        incomeBtn = (ImageView) findViewById(R.id.income_button);
        expenseBtn = (ImageView) findViewById(R.id.expense_button);
        //currentIncomeBtn, currentExpenseBtn
        incomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] itemsCategory = new String[1];

                Integer pSizeIncomeCategories = db.getCategoriesIncome().size();
                Integer pSizeExpenseCategories = db.getCategoriesExpense().size();

                if(currentIncomeBtn.equalsIgnoreCase("Active")){
                    currentIncomeBtn="NotActive";
                    ImageViewCompat.setImageTintList(incomeBtn, ColorStateList.valueOf(ContextCompat.getColor(HistoryActivity.this,R.color.bgMainGrey)));
                    //expenseCategory
                    if(currentExpenseBtn.equalsIgnoreCase("Active")) {
                        spinnerCategory.setVisibility(View.VISIBLE);
                        currentCategory="All";
                        itemsCategory = new String[pSizeExpenseCategories + 1];
                        itemsCategory[0] = "All";
                        for (int i = 0; i < pSizeExpenseCategories; i++) {
                            itemsCategory[i+1] = db.getCategoriesExpense().get(i);
                        }
                    }
                    else {
                        spinnerCategory.setVisibility(View.INVISIBLE);
                        currentCategory="All";
                        itemsCategory = new String[1];
                        itemsCategory[0] = currentCategory;
                    }
                }
                else if(currentIncomeBtn.equalsIgnoreCase("NotActive")){
                    currentIncomeBtn="Active";
                    ImageViewCompat.setImageTintList(incomeBtn, ColorStateList.valueOf(ContextCompat.getColor(HistoryActivity.this,R.color.green_icon_income)));
                    if(currentExpenseBtn.equalsIgnoreCase("Active")) {
                        spinnerCategory.setVisibility(View.INVISIBLE);
                        currentCategory="All";
                        itemsCategory = new String[1];
                        itemsCategory[0] = currentCategory;
                    }
                    //incomeCategory
                    else {
                        spinnerCategory.setVisibility(View.VISIBLE);
                        currentCategory="All";
                        itemsCategory = new String[pSizeIncomeCategories + 1];
                        itemsCategory[0] = "All";
                        for (int i = 0; i < pSizeIncomeCategories; i++) {
                            itemsCategory[i+1] = db.getCategoriesIncome().get(i);
                        }
                    }
                }

                categoryAdapter = new ArrayAdapter<String>(HistoryActivity.this, R.layout.spinner_item_blackfont, itemsCategory);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(categoryAdapter);
                spinnerCategory.setSelection(categoryAdapter.getPosition(currentCategory));

                setHistoryListView(currentMonth,currentYear,currentCategory,currentSortingStatus,currentIncomeBtn,currentExpenseBtn);
            }
        });

        expenseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] itemsCategory = new String[1];

                Integer pSizeIncomeCategories = db.getCategoriesIncome().size();
                Integer pSizeExpenseCategories = db.getCategoriesExpense().size();

                if(currentExpenseBtn.equalsIgnoreCase("Active")){
                    currentExpenseBtn="NotActive";
                    ImageViewCompat.setImageTintList(expenseBtn, ColorStateList.valueOf(ContextCompat.getColor(HistoryActivity.this,R.color.bgMainGrey)));
                    //incomeCategory
                    if(currentIncomeBtn.equalsIgnoreCase("Active")) {
                        spinnerCategory.setVisibility(View.VISIBLE);
                        currentCategory="All";
                        itemsCategory = new String[pSizeIncomeCategories + 1];
                        itemsCategory[0] = "All";
                        for (int i = 0; i < pSizeIncomeCategories; i++) {
                            itemsCategory[i+1] = db.getCategoriesIncome().get(i);
                        }
                    }
                    else {
                        spinnerCategory.setVisibility(View.INVISIBLE);
                        currentCategory="All";
                        itemsCategory = new String[1];
                        itemsCategory[0] = currentCategory;
                    }
                }
                else if(currentExpenseBtn.equalsIgnoreCase("NotActive")){
                    currentExpenseBtn="Active";
                    ImageViewCompat.setImageTintList(expenseBtn, ColorStateList.valueOf(ContextCompat.getColor(HistoryActivity.this,R.color.red_icon_expense)));
                    if(currentIncomeBtn.equalsIgnoreCase("Active")) {
                        spinnerCategory.setVisibility(View.INVISIBLE);
                        currentCategory="All";
                        itemsCategory = new String[1];
                        itemsCategory[0] = currentCategory;
                    }
                    //expenseCategory
                    else {
                        spinnerCategory.setVisibility(View.VISIBLE);
                        currentCategory="All";
                        itemsCategory = new String[pSizeExpenseCategories + 1];
                        itemsCategory[0] = "All";
                        for (int i = 0; i < pSizeExpenseCategories; i++) {
                            itemsCategory[i+1] = db.getCategoriesExpense().get(i);
                        }
                    }
                }

                categoryAdapter = new ArrayAdapter<String>(HistoryActivity.this, R.layout.spinner_item_blackfont, itemsCategory);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(categoryAdapter);
                spinnerCategory.setSelection(categoryAdapter.getPosition(currentCategory));

                setHistoryListView(currentMonth,currentYear,currentCategory,currentSortingStatus,currentIncomeBtn,currentExpenseBtn);
            }
        });


        changeCurrentMonthToTextMonth(currentMonth);

        //MONTH SPINNER
        String[] itemsMonth = {"All","January","February","March","April","May","June","July","August","September","October","November","December"};

        monthAdapter = new ArrayAdapter<String>(HistoryActivity.this, R.layout.spinner_item_blackfont, itemsMonth);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item != null) {
                    changeSpinnerTextToCurrentMonth(item.toString());
                }
                changeCurrentMonthToTextMonth(currentMonth);
                setHistoryListView(currentMonth,currentYear,currentCategory,currentSortingStatus,currentIncomeBtn,currentExpenseBtn);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerMonth.setSelection(monthAdapter.getPosition(currentMonthText));

        //all
        Integer pSize = db.getYearHistoryDistinct().size();
        String[] itemsYear = new String[pSize+1];
        itemsYear[0] = currentYear;
        for (int i = 0; i < pSize; i++) {
            itemsYear[i+1] = db.getYearHistoryDistinct().get(i);
        }
        yearAdapter = new ArrayAdapter<String>(HistoryActivity.this, R.layout.spinner_item_blackfont, itemsYear);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                //set Current Year from selected Year from spinner
                if (item != null) {
                    currentYear = item.toString();
                }
                setHistoryListView(currentMonth,currentYear,currentCategory,currentSortingStatus,currentIncomeBtn,currentExpenseBtn);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerYear.setSelection(yearAdapter.getPosition(currentYear));


        String[] itemsCategory = new String[1];
        itemsCategory[0] = currentCategory;

        categoryAdapter = new ArrayAdapter<String>(HistoryActivity.this, R.layout.spinner_item_blackfont, itemsCategory);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item != null) {
                    currentCategory = item.toString();
                }
                setHistoryListView(currentMonth,currentYear,currentCategory,currentSortingStatus,currentIncomeBtn,currentExpenseBtn);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerCategory.setSelection(categoryAdapter.getPosition(currentCategory));

        spinnerCategory.setVisibility(View.INVISIBLE);

        listView =(ListView) findViewById(R.id.lv_history_wallet);

        setHistoryListView(currentMonth,currentYear,currentCategory,currentSortingStatus,currentIncomeBtn,currentExpenseBtn);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, final long l) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                // Inflate the custom layout/view
                View customView = inflater.inflate(R.layout.popup_list_history,null);
                // Initialize a new instance of popup window
                mPopupWindow = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

                final TextView historyId =(TextView) view.findViewById(R.id.tv_history_id);
                final String sHistoryId = historyId.getText().toString();

                CardView btnEdit = (CardView) customView.findViewById(R.id.edit_cardview_btn);
                CardView btnDelete = (CardView) customView.findViewById(R.id.delete_cardview_btn);

                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        // Inflate the custom layout/view
                        final View customView = inflater.inflate(R.layout.popup_edit_history,null);

                        db = new DatabaseHandler(HistoryActivity.this);

                        // Initialize a new instance of popup window
                        mPopupWindow = new PopupWindow(
                                customView,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );

                        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                        final EditText amountMoney = (EditText) customView.findViewById(R.id.amount_of_money_edit_money);
                        final EditText detail = (EditText) customView.findViewById(R.id.detail_edit_money);

                        final Spinner category = (Spinner) customView.findViewById(R.id.category_edit_money);
                        final TextView tv_category = customView.findViewById(R.id.tv_category);

                        final Button btnCalender = (Button) customView.findViewById(R.id.datePicker_edit_money);
                        final Button btnTime = (Button) customView.findViewById(R.id.timePicker_edit_money);

                        final Button btnCancel = (Button) customView.findViewById(R.id.btn_cancel_edit);
                        final Button btnSave = (Button) customView.findViewById(R.id.btn_save_edit);

                        History historySelected = db.getHistory(sHistoryId);
                        final String signSelected = historySelected.getHistorySign();

                        DecimalFormat twoDForm = new DecimalFormat("#.##");

                        amountMoney.setText(twoDForm.format(historySelected.getHistoryAmount()));
                        detail.setText(historySelected.getHistoryDetail());

                        Integer pSize=0;
                        String[] itemsCategories = new String[pSize + 1];
                        if(historySelected.getHistorySign().equalsIgnoreCase("+")){
                            pSize = db.getCategoriesIncome().size();
                            itemsCategories = new String[pSize + 1];
                            itemsCategories[0] = "[ Choose ]";
                            for (int i = 0; i < pSize; i++) {
                                itemsCategories[i+1] = db.getCategoriesIncome().get(i);
                            }
                        }
                        else if(historySelected.getHistorySign().equalsIgnoreCase("-")){
                            pSize = db.getCategoriesExpense().size();
                            itemsCategories = new String[pSize + 1];
                            itemsCategories[0] = "[ Choose ]";
                            for (int i = 0; i < pSize; i++) {
                                itemsCategories[i+1] = db.getCategoriesExpense().get(i);
                            }
                        }

                        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(HistoryActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsCategories);
                        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        category.setAdapter(categoryAdapter);

                        category.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                                    hideKeyboardFrom(getBaseContext(),view);
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
                                        tv_category.setText(item.toString());
                                    }else{
                                        tv_category.setText(item1.toString());
                                    }
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        category.setSelection(categoryAdapter.getPosition(historySelected.getHistoryCategory()));
                        tv_category.setText(historySelected.getHistoryCategory());

                        btnCalender.setText(historySelected.getHistoryDate());
                        btnTime.setText(historySelected.getHistoryTime());

                        final DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
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
                        final TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
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
                        btnCalender.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                hideKeyboardFrom(getBaseContext(),view);
                                new DatePickerDialog(view.getContext(), d, c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                            }
                        });

                        btnTime.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                hideKeyboardFrom(getBaseContext(),view);
                                new TimePickerDialog(view.getContext(), t, c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE), true).show();
                            }
                        });

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
                                String sNext="No";

                                DatabaseHandler db = new DatabaseHandler(HistoryActivity.this);

                                Double sAmountMoney ;
                                if(amountMoney.getText().toString().equalsIgnoreCase("")){
                                    sAmountMoney = 0.0;
                                }
                                else {
                                    sAmountMoney = Double.parseDouble(amountMoney.getText().toString());
                                }

                                Log.d("cek sblm roundeditmoney",String.valueOf(sAmountMoney));
                                sAmountMoney = roundTwoDecimals(sAmountMoney);
                                Log.d("cek ssdh roundeditmoney",String.valueOf(sAmountMoney));


                                String sCategory = tv_category.getText().toString();
                                String sDetail = detail.getText().toString();
                                String sDate = btnCalender.getText().toString();
                                String sTime = btnTime.getText().toString();

                                String sDateEdited = sDate.replace("/","");
                                sDateEdited = sDateEdited.substring(4,8)+sDateEdited.substring(2,4)+sDateEdited.substring(0,2);

                                String sDateTime = sDateEdited+sTime.replace(":","");
                                Log.d("Cek STRING", sDateTime);

                                if(sAmountMoney<=0){
                                    Toast.makeText(HistoryActivity.this, "Please fill amount of money!!", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    if(sCategory.equalsIgnoreCase("[ Choose ]")){
                                        Toast.makeText(HistoryActivity.this, "Please choose category!!", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        sNext="Yes";
                                    }
                                }

                                if(sNext.equalsIgnoreCase("Yes")){
                                    History historyedit = new History();
                                    historyedit.setHistoryId(Integer.parseInt(sHistoryId));
                                    historyedit.setHistoryAmount(sAmountMoney);
                                    historyedit.setHistoryCategory(sCategory);
                                    historyedit.setHistoryDetail(sDetail);
                                    historyedit.setHistorySign(signSelected);
                                    historyedit.setHistoryDate(sDate);
                                    historyedit.setHistoryTime(sTime);
                                    historyedit.setHistoryDateTime(sDateTime);

                                    db.updateHistory(historyedit);
                                    setHistoryListView(currentMonth,currentYear,currentCategory,currentSortingStatus,currentIncomeBtn,currentExpenseBtn);

                                    DecimalFormat twoDForm = new DecimalFormat("#,##0.00");

//                                    tvTotalIncome.setText( twoDForm.format(db.getTotalIncome()));
//                                    tvTotalExpense.setText( twoDForm.format(db.getTotalExpense()));
//
                                    Toast.makeText(getBaseContext(), "History edited", Toast.LENGTH_SHORT).show();
                                    mPopupWindow.dismiss();
                                }
                            }
                        });
                        //TAMBAHAN DIM POPUP WINDOW
                        final ViewGroup root2 = (ViewGroup) getWindow().getDecorView().getRootView();
                        // Set a click listener for the popup window close button

                        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                clearDim(root2);
                            }
                        });

                        mPopupWindow.setOutsideTouchable(true);
                        mPopupWindow.setFocusable(true);
                        //mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        applyDim(root2, 0.5f);
                        // Finally, show the popup window at the center location of root relative layout
                        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);
                    }
                });

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HistoryActivity.this);
                        alertDialog.setTitle("Are you sure to delete this record?");
                        alertDialog.setIcon(R.drawable.ic_delete_black_24dp);
                        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseHandler db = new DatabaseHandler(HistoryActivity.this);
                                db.deleteHistory(sHistoryId);

                                setHistoryListView(currentMonth,currentYear,currentCategory,currentSortingStatus,currentIncomeBtn,currentExpenseBtn);
                                DecimalFormat twoDForm = new DecimalFormat("#,##0.00");
//
//                                tvTotalIncome.setText( twoDForm.format(db.getTotalIncome()));
//                                tvTotalExpense.setText( twoDForm.format(db.getTotalExpense()));

                                Toast.makeText(getBaseContext(), "History Deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
                        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        alertDialog.show();
                        mPopupWindow.dismiss();
                    }
                });

                //TAMBAHAN DIM POPUP WINDOW
                final ViewGroup root = (ViewGroup) getWindow().getDecorView().getRootView();
                // Set a click listener for the popup window close button

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

        sortingBtn = (ImageView) findViewById(R.id.sorting_button);
        sortingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentSortingStatus.equalsIgnoreCase("Desc")){
                    currentSortingStatus="Asc";
                    sortingBtn.setScaleY(-1);
                }
                else if (currentSortingStatus.equalsIgnoreCase("Asc")){
                    currentSortingStatus="Desc";
                    sortingBtn.setScaleY(1);
                }
                setHistoryListView(currentMonth,currentYear,currentCategory,currentSortingStatus,currentIncomeBtn,currentExpenseBtn);
            }
        });
    }

    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.parseDouble(twoDForm.format(d));
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
        Intent main = new Intent(HistoryActivity.this, MainActivity.class);
        startActivity(main);
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }

    private void changeSpinnerTextToCurrentMonth(String s) {
        if(s.equalsIgnoreCase("All")){
            currentMonth = "00";
        }
        else if(s.equalsIgnoreCase("January")){
            currentMonth = "01";
        }
        else if(s.equalsIgnoreCase("February")) {
            currentMonth = "02";
        }
        else if(s.equalsIgnoreCase("March")) {
            currentMonth = "03";
        }
        else if(s.equalsIgnoreCase("April")) {
            currentMonth = "04";
        }
        else if(s.equalsIgnoreCase("May")) {
            currentMonth = "05";
        }
        else if(s.equalsIgnoreCase("June")) {
            currentMonth = "06";
        }
        else if(s.equalsIgnoreCase("July")) {
            currentMonth = "07";
        }
        else if(s.equalsIgnoreCase("August")) {
            currentMonth = "08";
        }
        else if(s.equalsIgnoreCase("September")) {
            currentMonth = "09";
        }
        else if(s.equalsIgnoreCase("October")) {
            currentMonth = "10";
        }
        else if(s.equalsIgnoreCase("November")) {
            currentMonth = "11";
        }
        else if(s.equalsIgnoreCase("December")) {
            currentMonth = "12";
        }
    }

    private void changeCurrentMonthToTextMonth(String currentMonth) {
        if(currentMonth.equalsIgnoreCase("00")){
            currentMonthText = "All";
        }
        else if(currentMonth.equalsIgnoreCase("01")){
            currentMonthText = "January";
        }
        else if(currentMonth.equalsIgnoreCase("02")) {
            currentMonthText = "February";
        }
        else if(currentMonth.equalsIgnoreCase("03")) {
            currentMonthText = "March";
        }
        else if(currentMonth.equalsIgnoreCase("04")) {
            currentMonthText = "April";
        }
        else if(currentMonth.equalsIgnoreCase("05")) {
            currentMonthText = "May";
        }
        else if(currentMonth.equalsIgnoreCase("06")) {
            currentMonthText = "June";
        }
        else if(currentMonth.equalsIgnoreCase("07")) {
            currentMonthText = "July";
        }
        else if(currentMonth.equalsIgnoreCase("08")) {
            currentMonthText = "August";
        }
        else if(currentMonth.equalsIgnoreCase("09")) {
            currentMonthText = "September";
        }
        else if(currentMonth.equalsIgnoreCase("10")) {
            currentMonthText = "October";
        }
        else if(currentMonth.equalsIgnoreCase("11")) {
            currentMonthText = "November";
        }
        else if(currentMonth.equalsIgnoreCase("12")) {
            currentMonthText = "December";
        }
    }

    private void setHistoryListView(String currentMonthSelected, String currentYearSelected, String currentCategorySelected,String currentSortingStatus, String currentIncomeBtnStatus, String currentExpenseBtnStatus){
        DatabaseHandler db = new DatabaseHandler(HistoryActivity.this);
        historyList = new ArrayList<History>();
        if(
                (currentIncomeBtnStatus.equalsIgnoreCase("NotActive") && currentExpenseBtnStatus.equalsIgnoreCase("NotActive"))
                        ||
                (currentIncomeBtnStatus.equalsIgnoreCase("Active") && currentExpenseBtnStatus.equalsIgnoreCase("Active"))
                ){
            currentCategorySelected="All";
        }
        historyList = db.getAllHistoryBasedOnParameter(currentMonthSelected, currentYearSelected, currentCategorySelected, currentSortingStatus, currentIncomeBtnStatus, currentExpenseBtnStatus);
        adapter = new HistoryAdapter(this, historyList);
        listView.setAdapter(adapter);
    }
    public void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if(view ==null){
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
    public void hideKeyboardFrom(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}
