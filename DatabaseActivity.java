package Dir.dev.mywallet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.LocaleDisplayNames;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.Calendar;

import static Dir.dev.mywallet.BackUpAndRestore.exportDB;
import static Dir.dev.mywallet.BackUpAndRestore.importDB;
import static Dir.dev.mywallet.BackUpAndRestore.makeFolder;
import static Dir.dev.mywallet.BackUpAndRestore.verifyStoragePermissions;
import static Dir.dev.mywallet.Internet.isOnline;

public class DatabaseActivity extends AppCompatActivity {

    CardView btnBackup;
    ListView listView;
    Switch autoBackup;
    SharedPreferences prefs; // declare the sharedPreference
    boolean value = false; // default value if no value was found
    String key = "key"; // use this key to retrieve the value
    String sharedPrefName = "isMySwitchChecked"; // name of your sharedPreference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
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
        if(isOnline(DatabaseActivity.this)) {
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
                finish();
            }
        });

        prefs = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        value = prefs.getBoolean(key, value); // retrieve the value of your key
        autoBackup = (Switch) findViewById(R.id.autoBackup);
        autoBackup.setChecked(value);

        btnBackup = (CardView) findViewById(R.id.backup_btn_cardview);
        listView = (ListView) findViewById(R.id.lv_backupdatabaseList);
        autoBackup.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    prefs = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
                    prefs.edit().putBoolean(key, true).commit();

                    makeFolder();
                    verifyStoragePermissions(DatabaseActivity.this);
                    setAlarm();
                }
                else {
                    prefs = getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
                    prefs.edit().putBoolean(key, false).commit();
                    cancelAlarm();
                }
            }
        });

        btnBackup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Cek click","cliked");
                makeFolder();
                verifyStoragePermissions(DatabaseActivity.this);
                if(exportDB(getBaseContext())){
                    makeFolder();
                    verifyStoragePermissions(DatabaseActivity.this);
                    File dir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory("MyWallet")));
                    File[] filelist = dir.listFiles();
                    String[] theNamesOfFiles = new String[filelist.length];
                    for (int i = 0; i < theNamesOfFiles.length; i++) {
                        theNamesOfFiles[i] = filelist[i].getName();
                    }

                    String temp="";
                    for(int i=0;i< theNamesOfFiles.length-1;i++){
                        for(int j=i+1;j< theNamesOfFiles.length;j++){
                            if(theNamesOfFiles[i].compareTo(theNamesOfFiles[j])<0){
                                temp = theNamesOfFiles[i];
                                theNamesOfFiles[i]=theNamesOfFiles[j];
                                theNamesOfFiles[j]=temp;
                            }
                        }
                    }

                    ArrayAdapter<String > arrayAdapter =   new ArrayAdapter<String>(DatabaseActivity.this, android.R.layout.simple_list_item_1, theNamesOfFiles);
                    listView.setAdapter(arrayAdapter);

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(DatabaseActivity.this);
                    alertDialog.setTitle("Information");
                    alertDialog.setMessage("The backup was created in:\n/storage/emulated/0/MyWallet");
                    alertDialog.setIcon(R.drawable.ic_info_black_24dp);
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

        makeFolder();
        verifyStoragePermissions(DatabaseActivity.this);
        File dir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory("MyWallet")));
        File[] filelist = dir.listFiles();
        String[] theNamesOfFiles = new String[0];
        if(filelist ==null){
            theNamesOfFiles = new String[0];
        }
        else {
            theNamesOfFiles = new String[filelist.length];
        }
        for (int i = 0; i < theNamesOfFiles.length; i++) {
            theNamesOfFiles[i] = filelist[i].getName();
        }
        String temp="";
        for(int i=0;i< theNamesOfFiles.length-1;i++){
            for(int j=i+1;j< theNamesOfFiles.length;j++){
                if(theNamesOfFiles[i].compareTo(theNamesOfFiles[j])<0){
                    temp = theNamesOfFiles[i];
                    theNamesOfFiles[i]=theNamesOfFiles[j];
                    theNamesOfFiles[j]=temp;
                }
            }
        }

        ArrayAdapter<String > arrayAdapter =   new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, theNamesOfFiles);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Object item = adapterView.getItemAtPosition(i);
                //set Current Year from selected Year from spinner
                if (item != null) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(DatabaseActivity.this);
                    alertDialog.setTitle("Restore Database");
                    alertDialog.setMessage("This action will replace the\ninformation in the current database \nwith the backup.\nAre you sure to continue?");
                    alertDialog.setIcon(R.drawable.ic_storage_black_24dp);
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            makeFolder();
                            verifyStoragePermissions(DatabaseActivity.this);
                            importDB(getBaseContext(), item.toString());
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DatabaseActivity.this);
                            alertDialog.setTitle("Information");
                            alertDialog.setMessage("Backup was restored successfully!");
                            alertDialog.setIcon(R.drawable.ic_info_black_24dp);
                            alertDialog.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            alertDialog.show();
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

            }
        });
    }

    //untuk backup
    public void setAlarm(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,00);
        long startUpTime = calendar.getTimeInMillis();

        Log.d("CEK STARTUP",String.valueOf(startUpTime));
        Log.d("CurrentTimeMillis",String.valueOf(System.currentTimeMillis()));
        Log.d("CEK if",String.valueOf(System.currentTimeMillis() > startUpTime));

        if (System.currentTimeMillis() > startUpTime) {
            startUpTime = startUpTime + 24*60*60*1000;
        }
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,BackUpTask.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC,startUpTime,AlarmManager.INTERVAL_DAY,pendingIntent);
        Log.d("CEK alarm","alarm");
        Log.d("CurrentTIme",String.valueOf(System.currentTimeMillis()));
    }
    //untuk cancelbackup
    public void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this,BackUpTask.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, intent, 0);
        alarmManager.cancel(pendingIntent);
        Log.d("CEK cancelalarm","cancelalarm");
    }
}
