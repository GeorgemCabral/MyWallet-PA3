package Diretory.dev.mywallet;

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
import android.text.Editable;
import android.text.TextWatcher;
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

import static Diretory.dev.mywallet.Internet.isOnline;

public class SearchActivity extends AppCompatActivity {
    String searchString="";
    DatabaseHandler db;
    HistoryAdapter adapter;

    //ListView
    ListView listView;
    ArrayList<History> historyList;
    private PopupWindow mPopupWindow;
    private RelativeLayout mRelativeLayout;
    Calendar c;

    EditText et_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
        if(isOnline(SearchActivity.this)) {
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
                Intent main = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(main);
                overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
            }
        });

        et_search = (EditText) findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchString = editable.toString().trim();
                setHistoryListView(searchString);
            }
        });

        mRelativeLayout = (RelativeLayout) findViewById(R.id.r_layout_history_money);
        listView =(ListView) findViewById(R.id.lv_history_wallet);

        setHistoryListView(searchString);
        c = Calendar.getInstance();
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

                        db = new DatabaseHandler(SearchActivity.this);

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

                        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_spinner_dropdown_item, itemsCategories);
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

                                DatabaseHandler db = new DatabaseHandler(SearchActivity.this);

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
                                    Toast.makeText(SearchActivity.this, "Please fill amount of money!!", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    if(sCategory.equalsIgnoreCase("[ Choose ]")){
                                        Toast.makeText(SearchActivity.this, "Please choose category!!", Toast.LENGTH_LONG).show();
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
                                    setHistoryListView(searchString);

                                    DecimalFormat twoDForm = new DecimalFormat("#,##0.00");

//                                    tvTotalIncome.setText( twoDForm.format(db.getTotalIncome()));
//                                    tvTotalExpense.setText( twoDForm.format(db.getTotalExpense()));
//
//                                    Toast.makeText(getBaseContext(), "History edited", Toast.LENGTH_SHORT).show();
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
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchActivity.this);
                        alertDialog.setTitle("Are you sure to delete this record?");
                        alertDialog.setIcon(R.drawable.ic_delete_black_24dp);
                        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DatabaseHandler db = new DatabaseHandler(SearchActivity.this);
                                db.deleteHistory(sHistoryId);

                                setHistoryListView(searchString);
                                DecimalFormat twoDForm = new DecimalFormat("#,##0.00");
//
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
        Intent main = new Intent(SearchActivity.this, MainActivity.class);
        startActivity(main);
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }
    public void hideKeyboardFrom(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private void setHistoryListView(String searchedDetailHistory){
        DatabaseHandler db = new DatabaseHandler(SearchActivity.this);
        historyList = new ArrayList<History>();
        historyList = db.getAllHistoryBasedOnSearch(searchedDetailHistory);
        adapter = new HistoryAdapter(this, historyList);
        listView.setAdapter(adapter);
    }

}
