package Dir.dev.mywallet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import Dir.dev.mywallet.Adapter.BudgetAdapter;
import Dir.dev.mywallet.Adapter.ListAdapterEachMonth;
import Dir.dev.mywallet.Database.DatabaseHandler;
import Dir.dev.mywallet.Model.Budget;
import Dir.dev.mywallet.Model.History;

import static Dir.dev.mywallet.Internet.isOnline;

public class BudgetActivity extends AppCompatActivity {
    private ImageView btn_add_budget;
    private Spinner spinnerMonth, spinnerYear;

    DatabaseHandler db;
    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;

    private ListView listViewBudget;

    String currentMonthText;
    String currentMonth, currentYear;

    ArrayList<Budget> budgetList;
    BudgetAdapter adapter;
    Calendar c;

    ArrayAdapter<String> monthAdapter;
    ArrayAdapter<String> yearAdapter;


    TextView rest_budget, status_budget, tv_percentage_total, total_budget_used, total_budget_amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

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
        if(isOnline(BudgetActivity.this)) {
            admobLayout.setVisibility(View.VISIBLE);
        }
        else {
            admobLayout.setVisibility(View.GONE);
        }
        //==============================admob===============================

        ImageView imageViewArroBack = (ImageView) findViewById(R.id.btnBackArrow);
        imageViewArroBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(BudgetActivity.this, MainActivity.class);
                startActivity(main);
                overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
            }
        });

        c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MMyyyy");
        String sDate = sdf.format(c.getTime());

        currentMonth = sDate.substring(0,2);
        currentYear = sDate.substring(2,6);
        changeCurrentMonthToTextMonth(currentMonth);

        db = new DatabaseHandler(BudgetActivity.this);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.r_layout_budget);

        btn_add_budget = (ImageView) findViewById(R.id.btn_add_budget);

        spinnerMonth = (Spinner) findViewById(R.id.spinner_month);
        spinnerYear = (Spinner) findViewById(R.id.spinner_year);

        setSpinnerFilter();

        listViewBudget = (ListView) findViewById(R.id.lv_budget);
        //INIT SET LIST
        setBudgetListView(currentMonthText,currentMonth, currentYear);

        listViewBudget.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {

            }
            public void onSwipeRight() {
                //swipe to month before
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
                changeCurrentMonthToTextMonth(currentMonth);
                spinnerMonth.setSelection(monthAdapter.getPosition(currentMonthText));
                spinnerYear.setSelection(yearAdapter.getPosition(currentYear));

                setBudgetListView(currentMonthText,currentMonth, currentYear);
            }
            public void onSwipeLeft() {
                //swipe to month after
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
                changeCurrentMonthToTextMonth(currentMonth);
                spinnerMonth.setSelection(monthAdapter.getPosition(currentMonthText));
                spinnerYear.setSelection(yearAdapter.getPosition(currentYear));

                setBudgetListView(currentMonthText,currentMonth, currentYear);
            }
            public void onSwipeBottom() {

            }

        });

        listViewBudget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                // Inflate the custom layout/view
                View customView = inflater.inflate(R.layout.popup_list_history,null);
                // Initialize a new instance of popup window
                mPopupWindow = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

                final TextView budgetId =(TextView) view.findViewById(R.id.tv_budget_id);
                final String sBudgetId = budgetId.getText().toString();

                CardView btnEdit = (CardView) customView.findViewById(R.id.edit_cardview_btn);
                CardView btnDelete = (CardView) customView.findViewById(R.id.delete_cardview_btn);

                btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPopupWindow.dismiss();
                        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        // Inflate the custom layout/view
                        final View customView = inflater.inflate(R.layout.popup_edit_budget,null);

                        db = new DatabaseHandler(BudgetActivity.this);

                        // Initialize a new instance of popup window
                        mPopupWindow = new PopupWindow(
                                customView,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );

                        ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                        final EditText amountMoney = (EditText) customView.findViewById(R.id.amount_of_money_budget);
                        final Spinner category = (Spinner) customView.findViewById(R.id.category_money_budget);
                        final TextView tv_category = customView.findViewById(R.id.tv_category);

                        final Spinner month = (Spinner) customView.findViewById(R.id.spinner_month);
                        final TextView tv_month = customView.findViewById(R.id.tv_month);

                        final Spinner year = (Spinner) customView.findViewById(R.id.spinner_year);
                        final TextView tv_year = customView.findViewById(R.id.tv_year);

                        final Button btnCancel = (Button) customView.findViewById(R.id.btn_cancel_edit);
                        final Button btnSave = (Button) customView.findViewById(R.id.btn_save_edit);

                        Budget budgetSelected = db.getBudget(sBudgetId);

                        DecimalFormat twoDForm = new DecimalFormat("#.##");
                        amountMoney.setText(twoDForm.format(budgetSelected.getBudgetAmount()));

                        Integer pSize=0;
                        String [] itemsCategories = new String[pSize + 1];
                        itemsCategories[0] = budgetSelected.getBudgetCategory();

                        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(BudgetActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsCategories);
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
                        category.setSelection(categoryAdapter.getPosition(budgetSelected.getBudgetCategory()));
                        tv_category.setText(budgetSelected.getBudgetCategory());

                        String[] itemsMonth = {budgetSelected.getBudgetMonth()};
                        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(BudgetActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsMonth);
                        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        month.setAdapter(monthAdapter);

                        month.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                                    hideKeyboardFrom(getBaseContext(),view);
                                }
                                return false;
                            }
                        });

                        month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                Object item = adapterView.getItemAtPosition(position);
                                Object item1 = adapterView.getItemAtPosition(0);

                                if (item != null) {
                                    if (!item.toString().equalsIgnoreCase("[ Choose ]")){
                                        tv_month.setText(item.toString());
                                    }else{
                                        tv_month.setText(item1.toString());
                                    }
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        month.setSelection(monthAdapter.getPosition(budgetSelected.getBudgetMonth()));

                        String [] itemsYear = new String[1];
                        itemsYear[0] = budgetSelected.getBudgetYear();

                        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(BudgetActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsYear);
                        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        year.setAdapter(yearAdapter);

                        year.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View view, MotionEvent motionEvent) {
                                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                                    hideKeyboardFrom(getBaseContext(),view);
                                }
                                return false;
                            }
                        });

                        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                Object item = adapterView.getItemAtPosition(position);
                                Object item1 = adapterView.getItemAtPosition(0);

                                if (item != null) {
                                    if (!item.toString().equalsIgnoreCase("[ Choose ]")){
                                        tv_year.setText(item.toString());
                                    }else{
                                        tv_year.setText(item1.toString());
                                    }
                                }
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        year.setSelection(yearAdapter.getPosition(budgetSelected.getBudgetYear()));

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

                                DatabaseHandler db = new DatabaseHandler(BudgetActivity.this);

                                Double sAmountMoney ;
                                if(amountMoney.getText().toString().equalsIgnoreCase("")){
                                    sAmountMoney = 0.0;
                                }
                                else {
                                    sAmountMoney = Double.parseDouble(amountMoney.getText().toString());
                                }

                                sAmountMoney = roundTwoDecimals(sAmountMoney);
                                String sCategory = tv_category.getText().toString();
                                String sMonth = tv_month.getText().toString();
                                String sYear = tv_year.getText().toString();

                                if(sAmountMoney<=0){
                                    Toast.makeText(BudgetActivity.this, "Please fill amount of money!!", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    if(sCategory.equalsIgnoreCase("[ Choose ]")){
                                        Toast.makeText(BudgetActivity.this, "Please choose category!!", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        if(sMonth.equalsIgnoreCase("[ Choose ]")){
                                            Toast.makeText(BudgetActivity.this, "Please choose month!!", Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            if(sYear.equalsIgnoreCase("[ Choose ]")){
                                                Toast.makeText(BudgetActivity.this, "Please choose year!!", Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                sNext="Yes";
                                            }
                                        }
                                    }
                                }

                                if(sNext.equalsIgnoreCase("Yes")){
                                    Budget budget = new Budget();
                                    budget.setBudgetId(Integer.parseInt(sBudgetId));
                                    budget.setBudgetAmount(sAmountMoney);
                                    budget.setBudgetCategory(sCategory);
                                    budget.setBudgetMonth(sMonth);
                                    budget.setBudgetYear(sYear);

                                    db.updateBudget(budget);
                                    setBudgetListView(currentMonthText,currentMonth, currentYear);
                                    Toast.makeText(getBaseContext(), "Budget category "+sCategory+" edited", Toast.LENGTH_SHORT).show();
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
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BudgetActivity.this);
                        alertDialog.setTitle("Are you sure to delete this budget?");
                        alertDialog.setIcon(R.drawable.ic_delete_black_24dp);
                        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseHandler db = new DatabaseHandler(BudgetActivity.this);
                                db.deleteBudget(sBudgetId);
                                setBudgetListView(currentMonthText,currentMonth, currentYear);
                                Toast.makeText(getBaseContext(), "Budget Deleted", Toast.LENGTH_SHORT).show();
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


        btn_add_budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                // Inflate the custom layout/view
                final View customView = inflater.inflate(R.layout.popup_add_budget,null);

                db = new DatabaseHandler(BudgetActivity.this);

                // Initialize a new instance of popup window
                mPopupWindow = new PopupWindow(
                        customView,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

                ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                final EditText amountMoney = (EditText) customView.findViewById(R.id.amount_of_money_budget);
                final Spinner category = (Spinner) customView.findViewById(R.id.category_money_budget);
                final TextView tv_category = customView.findViewById(R.id.tv_category);

                final Spinner month = (Spinner) customView.findViewById(R.id.spinner_month);
                final TextView tv_month = customView.findViewById(R.id.tv_month);

                final Spinner year = (Spinner) customView.findViewById(R.id.spinner_year);
                final TextView tv_year = customView.findViewById(R.id.tv_year);

                final Button btnCancel = (Button) customView.findViewById(R.id.btn_cancel_edit);
                final Button btnSave = (Button) customView.findViewById(R.id.btn_save_edit);

                Integer pSize=0;
                pSize = db.getCategoriesExpense().size();
                String [] itemsCategories = new String[pSize + 1];
                itemsCategories[0] = "[ Choose ]";
                for (int i = 0; i < pSize; i++) {
                    itemsCategories[i+1] = db.getCategoriesExpense().get(i);
                }

                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(BudgetActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsCategories);
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

                String[] itemsMonth = {"[ Choose ]","January","February","March","April","May","June","July","August","September","October","November","December"};
                ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(BudgetActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsMonth);
                monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                month.setAdapter(monthAdapter);

                month.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                            hideKeyboardFrom(getBaseContext(),view);
                        }
                        return false;
                    }
                });

                month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        Object item = adapterView.getItemAtPosition(position);
                        Object item1 = adapterView.getItemAtPosition(0);

                        if (item != null) {
                            if (!item.toString().equalsIgnoreCase("[ Choose ]")){
                                tv_month.setText(item.toString());
                            }else{
                                tv_month.setText(item1.toString());
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                month.setSelection(monthAdapter.getPosition(currentMonthText));

                String [] itemsYear = new String[101];
                itemsYear[0] = "[ Choose ]";
                for (int i = 0; i < 100; i++) {
                    itemsYear[i+1] = String.valueOf(i+2000);
                }

                ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(BudgetActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsYear);
                yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                year.setAdapter(yearAdapter);

                year.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                            hideKeyboardFrom(getBaseContext(),view);
                        }
                        return false;
                    }
                });

                year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                        Object item = adapterView.getItemAtPosition(position);
                        Object item1 = adapterView.getItemAtPosition(0);

                        if (item != null) {
                            if (!item.toString().equalsIgnoreCase("[ Choose ]")){
                                tv_year.setText(item.toString());
                            }else{
                                tv_year.setText(item1.toString());
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                year.setSelection(yearAdapter.getPosition(currentYear));


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

                        DatabaseHandler db = new DatabaseHandler(BudgetActivity.this);

                        Double sAmountMoney ;
                        if(amountMoney.getText().toString().equalsIgnoreCase("")){
                            sAmountMoney = 0.0;
                        }
                        else {
                            sAmountMoney = Double.parseDouble(amountMoney.getText().toString());
                        }

                        sAmountMoney = roundTwoDecimals(sAmountMoney);
                        String sCategory = tv_category.getText().toString();
                        String sMonth = tv_month.getText().toString();
                        String sYear = tv_year.getText().toString();

                        if(sAmountMoney<=0){
                            Toast.makeText(BudgetActivity.this, "Please fill amount of money!!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            if(sCategory.equalsIgnoreCase("[ Choose ]")){
                                Toast.makeText(BudgetActivity.this, "Please choose category!!", Toast.LENGTH_LONG).show();
                            }
                            else {
                                if(sMonth.equalsIgnoreCase("[ Choose ]")){
                                    Toast.makeText(BudgetActivity.this, "Please choose month!!", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    if(sYear.equalsIgnoreCase("[ Choose ]")){
                                        Toast.makeText(BudgetActivity.this, "Please choose year!!", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        if(db.checkCategoryBudget(sCategory,sMonth,sYear)){
                                            Toast.makeText(BudgetActivity.this, "Budget for category "+sCategory+" is already created in this month!!", Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            sNext="Yes";
                                        }
                                    }
                                }
                            }
                        }

                        if(sNext.equalsIgnoreCase("Yes")){
                            Budget budget = new Budget();
                            budget.setBudgetAmount(sAmountMoney);
                            budget.setBudgetCategory(sCategory);
                            budget.setBudgetMonth(sMonth);
                            budget.setBudgetYear(sYear);

                            db.addBudget(budget);
                            setSpinnerFilter();
                            setBudgetListView(currentMonthText,currentMonth, currentYear);

                            Toast.makeText(getBaseContext(), "Budget added", Toast.LENGTH_SHORT).show();
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


    }

    private void setBudgetListView(String currentMonthText,String currentMonth ,String currentYear) {
        budgetList = new ArrayList<Budget>();
        budgetList = db.getAllBudget(currentMonthText,currentYear);
        adapter = new BudgetAdapter(this, budgetList, String.valueOf(currentMonth), String.valueOf(currentYear));//Ex: format bulan tahun 01/2018
        listViewBudget.setAdapter(adapter);

        rest_budget = (TextView) findViewById(R.id.rest_budget);
        status_budget = (TextView) findViewById(R.id.status_budget);
        tv_percentage_total = (TextView) findViewById(R.id.tv_percentage_total);
        total_budget_used = (TextView) findViewById(R.id.total_budget_used);
        total_budget_amount = (TextView) findViewById(R.id.total_budget_amount);
        LinearLayout percentage_layout = (LinearLayout) findViewById(R.id.percentage_layout);

        DecimalFormat twoDForm = new DecimalFormat("#,##0.00");
        Double dtotal_budget_used=0.0, dtotal_budget_amount= 0.0, dtotal_rest_budget;

        for(Budget budgetEach : budgetList){
            dtotal_budget_amount += budgetEach.getBudgetAmount();
            dtotal_budget_used += db.getTotalExpenseWithParameter(budgetEach.getBudgetCategory(),currentMonth, currentYear);
        }

        total_budget_amount.setText( twoDForm.format(dtotal_budget_amount));
        total_budget_used.setText(twoDForm.format(dtotal_budget_used));
        float percentage=0;
        if(budgetList.size()!=0) {
            percentage = Float.parseFloat(String.valueOf(dtotal_budget_used / dtotal_budget_amount));
        }
        DecimalFormat twoDFormPercen = new DecimalFormat("0.00");
        tv_percentage_total.setText( twoDFormPercen.format(percentage*100)+ "%");

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                percentage_layout.getLayoutParams();
        params.weight = percentage;
        percentage_layout.setLayoutParams(params);
        if(percentage<=0.6){
            //percentage_layout.setBackgroundColor(getResources().getColor(R.color.green_icon_income));
            percentage_layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else if (percentage >=1){
            //percentage_layout.setBackgroundColor(getResources().getColor(R.color.red_icon_expense));
            percentage_layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        else {
            //percentage_layout.setBackgroundColor(getResources().getColor(R.color.orange_icon_budget));
            percentage_layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        dtotal_rest_budget = dtotal_budget_amount - dtotal_budget_used;
        rest_budget.setText(twoDForm.format(dtotal_rest_budget));

        String status = "";
        if(dtotal_rest_budget<0){
            status = "over";
            status_budget.setTextColor(getResources().getColor(R.color.red_icon_expense));
            //rest_budget.setTextColor(getResources().getColor(R.color.red_icon_expense));
            rest_budget.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        else {
            status = "saved";
            status_budget.setTextColor(getResources().getColor(R.color.green_icon_income));
            //rest_budget.setTextColor(getResources().getColor(R.color.green_icon_income));
            rest_budget.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        status_budget.setText(status);
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(BudgetActivity.this, MainActivity.class);
        startActivity(main);
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }

    public void hideKeyboardFrom(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    double roundTwoDecimals(double d) {
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
    private void changeSpinnerTextToCurrentMonth(String s) {
        if(s.equalsIgnoreCase("January")){
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
        if(currentMonth.equalsIgnoreCase("01")){
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

    private void setSpinnerFilter(){
        String[] itemsMonth = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        monthAdapter = new ArrayAdapter<String>(BudgetActivity.this, R.layout.spinner_item_orange, itemsMonth);
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
                setBudgetListView(currentMonthText,currentMonth, currentYear);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerMonth.setSelection(monthAdapter.getPosition(currentMonthText));

        //year spinner
        Integer pSize = db.getYearBudgetDistinct().size();
        int max=0,min=0, totalYear=0;
        if(pSize!=0){
            min=Integer.parseInt(db.getYearBudgetDistinct().get(0));
            max=Integer.parseInt(db.getYearBudgetDistinct().get(pSize-1));
            totalYear = (max-min)+1;
        }

        String[] itemsYear = new String[totalYear];
        if(pSize==0){
            String[] itemsYearInit = new String[1];
            itemsYearInit[0] = currentYear;
            yearAdapter = new ArrayAdapter<String>(BudgetActivity.this, R.layout.spinner_item_orange, itemsYearInit);
        }
        else {
            for (int i = 0; i < totalYear; i++) {
                itemsYear[i] = String.valueOf(min);
                min++;
            }
            yearAdapter = new ArrayAdapter<String>(BudgetActivity.this, R.layout.spinner_item_orange, itemsYear);
        }

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
                setBudgetListView(currentMonthText,currentMonth, currentYear);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerYear.setSelection(yearAdapter.getPosition(currentYear));
    }
}
