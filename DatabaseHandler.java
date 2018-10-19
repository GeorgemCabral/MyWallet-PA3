package Diretory.dev.mywallet.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.security.acl.LastOwnerException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import Diretory.dev.mywallet.Model.Budget;
import Diretory.dev.mywallet.Model.Category;
import Diretory.dev.mywallet.Model.History;
import Diretory.dev.mywallet.Model.User;



public class DatabaseHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 16   ;

    // Database Name
    private static final String DATABASE_NAME = "MyWallet.db";

    //Table Name
    private static final String TABLE_USER = "user";
    private static final String TABLE_CATEGORY = "categories";
    private static final String TABLE_HISTORY = "history";
    private static final String TABLE_BUDGET = "budget";

    //Column Name
    //=============Column Table User=============
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_USERNAME = "user_username";
    private static final String COLUMN_USER_EMAIL = "user_email";
    private static final String COLUMN_USER_PASSWORD = "user_password";

    //=============Column Table Categories=============
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_CATEGORY_NAME = "category_name";
    private static final String COLUMN_CATEGORY_SIGN = "category_sign";

    //=============Column Table History=============
    private static final String COLUMN_HISTORY_ID = "history_id";
    private static final String COLUMN_HISTORY_CATEGORY = "history_category";
    private static final String COLUMN_HISTORY_AMOUNT = "history_amount";
    private static final String COLUMN_HISTORY_SIGN = "history_sign";
    private static final String COLUMN_HISTORY_DETAIL = "history_detail";
    private static final String COLUMN_HISTORY_DATE = "history_date";
    private static final String COLUMN_HISTORY_TIME = "history_time";
    private static final String COLUMN_HISTORY_DATE_TIME = "history_date_time";

    //=============Column Table History=============
    private static final String COLUMN_BUDGET_ID = "budget_id";
    private static final String COLUMN_BUDGET_CATEGORY = "budget_category";
    private static final String COLUMN_BUDGET_AMOUNT = "budget_amount";
    private static final String COLUMN_BUDGET_MONTH = "budget_month";
    private static final String COLUMN_BUDGET_YEAR = "budget_year";

    public DatabaseHandler(Context context) {
        //super(context, name, factory, version);
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("PATH Data: ", String.valueOf(context.getDatabasePath(DATABASE_NAME) ));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_USERNAME + " TEXT," + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CATEGORY_NAME + " TEXT," + COLUMN_CATEGORY_SIGN + " TEXT)";
        db.execSQL(CREATE_CATEGORY_TABLE);

        String CREATE_HISTORY_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + COLUMN_HISTORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HISTORY_CATEGORY + " TEXT," + COLUMN_HISTORY_AMOUNT + " REAL,"
                + COLUMN_HISTORY_DETAIL + " TEXT," + COLUMN_HISTORY_DATE + " TEXT,"
                + COLUMN_HISTORY_TIME + " TEXT," + COLUMN_HISTORY_DATE_TIME + " TEXT,"
                + COLUMN_HISTORY_SIGN + " TEXT)";
        db.execSQL(CREATE_HISTORY_TABLE);

        String CREATE_BUDGET_TABLE = "CREATE TABLE " + TABLE_BUDGET + "("
                + COLUMN_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_BUDGET_CATEGORY + " TEXT," + COLUMN_BUDGET_AMOUNT + " REAL,"
                + COLUMN_BUDGET_MONTH + " TEXT," + COLUMN_BUDGET_YEAR + " TEXT)";
        db.execSQL(CREATE_BUDGET_TABLE);

        //DEFAULT DATA CATEGORIES
        String[] listCategoryExpense = {"Food", "Drinks", "Fun", "Education", "Fuel", "Hotel", "Other", "Personal", "Clothing", "Health", "Transport"};
        //expense
        for(String eachList : listCategoryExpense){
            String insertQuery = "INSERT INTO "+TABLE_CATEGORY+" ( "+COLUMN_CATEGORY_NAME+" , "+COLUMN_CATEGORY_SIGN+") "
                    + " VALUES( '"+eachList+"' ,'-')";
            db.execSQL(insertQuery);
        }

        String[] listCategoryIncome = {"Salary", "Sales"};
        //expense
        for(String eachList : listCategoryIncome){
            String insertQuery = "INSERT INTO "+TABLE_CATEGORY+" ( "+COLUMN_CATEGORY_NAME+" , "+COLUMN_CATEGORY_SIGN+") "
                    + " VALUES( '"+eachList+"' ,'+')";
            db.execSQL(insertQuery);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<16){
            String CREATE_BUDGET_TABLE = "CREATE TABLE " + TABLE_BUDGET + "("
                    + COLUMN_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_BUDGET_CATEGORY + " TEXT," + COLUMN_BUDGET_AMOUNT + " REAL,"
                    + COLUMN_BUDGET_MONTH + " TEXT," + COLUMN_BUDGET_YEAR + " TEXT)";
            db.execSQL(CREATE_BUDGET_TABLE);
        }
    }


    public void addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, user.getUsername());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public void updateUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_USERNAME, user.getUsername());
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        db.update(TABLE_USER, values, COLUMN_USER_ID + " = ?",
                new String[] { String.valueOf(user.getUserId()) });
    }

    public boolean checkUser() {
        boolean check = false;
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_USER;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            check=true;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return check;
    }

    public boolean checkPassword(String username,String password) {
        boolean check = false;
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT user_password FROM " + TABLE_USER+" WHERE user_username = '" + username + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            if(password.equals(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)))){
                check=true;
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return check;
    }

    public User getUser() {
        User user = new User();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_USER;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            user.setUserId(cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID)));
            user.setUsername(cursor.getString(cursor.getColumnIndex(COLUMN_USER_USERNAME)));
            user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_USER_EMAIL)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_USER_PASSWORD)));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return user;
    }

    public ArrayList<String > getCategoriesExpense() {
        ArrayList<String> categories = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT "+COLUMN_CATEGORY_NAME+" FROM " + TABLE_CATEGORY+" WHERE "+COLUMN_CATEGORY_SIGN+" = '-'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                categories.add(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return categories;
    }

    public ArrayList<String > getCategoriesIncome() {
        ArrayList<String> categories = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT "+COLUMN_CATEGORY_NAME+" FROM " + TABLE_CATEGORY+" WHERE "+COLUMN_CATEGORY_SIGN+" = '+'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                categories.add(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return categories;
    }

    public ArrayList<History> getAllHistoryDesc() {
        ArrayList<History> histories = new ArrayList<History>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_HISTORY+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" DESC";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                History history = new History();
                history.setHistoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_HISTORY_ID)));
                history.setHistoryCategory(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_CATEGORY)));
                history.setHistoryAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT)));
                history.setHistorySign(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_SIGN)));
                history.setHistoryDetail(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DETAIL)));
                history.setHistoryDate(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DATE)));
                history.setHistoryTime(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_TIME)));
                history.setHistoryDateTime(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DATE_TIME)));

                histories.add(history);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return histories;
    }
    public ArrayList<History> getAllHistoryAsc() {
        ArrayList<History> histories = new ArrayList<History>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_HISTORY+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" ASC";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                History history = new History();
                history.setHistoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_HISTORY_ID)));
                history.setHistoryCategory(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_CATEGORY)));
                history.setHistoryAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT)));
                history.setHistorySign(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_SIGN)));
                history.setHistoryDetail(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DETAIL)));
                history.setHistoryDate(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DATE)));
                history.setHistoryTime(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_TIME)));
                history.setHistoryDateTime(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DATE_TIME)));

                histories.add(history);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return histories;
    }

    public Double getTotalExpense() {
        String total="";

        double totalAmount=0;
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT "+COLUMN_HISTORY_AMOUNT+" FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_SIGN+" = '-'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                totalAmount+=cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        total = String.valueOf(totalAmount);
        return totalAmount;
    }

    public Double getTotalIncome() {
        String total="";

        double totalAmount=0;
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT "+COLUMN_HISTORY_AMOUNT+" FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_SIGN+" = '+'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                totalAmount+=cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        total = String.valueOf(totalAmount);
        return totalAmount;
    }

    public void addHistory(History history) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_HISTORY_CATEGORY, history.getHistoryCategory());
        values.put(COLUMN_HISTORY_AMOUNT, history.getHistoryAmount());
        values.put(COLUMN_HISTORY_SIGN, history.getHistorySign());
        values.put(COLUMN_HISTORY_DETAIL, history.getHistoryDetail());
        values.put(COLUMN_HISTORY_DATE, history.getHistoryDate());
        values.put(COLUMN_HISTORY_TIME, history.getHistoryTime());
        values.put(COLUMN_HISTORY_DATE_TIME, history.getHistoryDateTime());

        // Inserting Row
        db.insert(TABLE_HISTORY, null, values);
        db.close(); // Closing database connection
    }
    public void deleteHistory(String historyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete Rows
        db.delete(TABLE_HISTORY,COLUMN_HISTORY_ID + " = ?", new String[] {historyId});
        db.close();
    }

    public Double getTotalIncomeAnnual(String year) {
        double totalAmount=0;
        SQLiteDatabase db = this.getWritableDatabase();
        String whereArgs=year;
        String selectQuery = "SELECT "+COLUMN_HISTORY_AMOUNT+" FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '%"+whereArgs+"' AND "+COLUMN_HISTORY_SIGN+" ='+'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                totalAmount+=cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return totalAmount;
    }
    public Double getTotalExpenseAnnual(String year) {
        double totalAmount=0;
        SQLiteDatabase db = this.getWritableDatabase();
        String whereArgs=year;
        String selectQuery = "SELECT "+COLUMN_HISTORY_AMOUNT+" FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '%"+whereArgs+"' AND "+COLUMN_HISTORY_SIGN+" ='-'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                totalAmount+=cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return totalAmount;
    }


    public Double getTotalIncomeEachMonth(String month, String year) {
        double totalAmount=0;
        SQLiteDatabase db = this.getWritableDatabase();
        String whereArgs=month+"/"+year;
        String selectQuery = "SELECT "+COLUMN_HISTORY_AMOUNT+" FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '%"+whereArgs+"' AND "+COLUMN_HISTORY_SIGN+" ='+'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                totalAmount+=cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return totalAmount;
    }

    public Double getTotalExpenseEachMonth(String month, String year) {
        double totalAmount=0;
        SQLiteDatabase db = this.getWritableDatabase();
        String whereArgs=month+"/"+year;
        String selectQuery = "SELECT "+COLUMN_HISTORY_AMOUNT+" FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '%"+whereArgs+"' AND "+COLUMN_HISTORY_SIGN+" ='-'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                totalAmount+=cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return totalAmount;
    }

    //daily
    public Double getTotalIncomeEachDay(String day, String month, String year) {
        double totalAmount=0;
        SQLiteDatabase db = this.getWritableDatabase();
        String whereArgs=day+"/"+month+"/"+year;
        String selectQuery = "SELECT "+COLUMN_HISTORY_AMOUNT+" FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '"+whereArgs+"' AND "+COLUMN_HISTORY_SIGN+" ='+'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                totalAmount+=cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return totalAmount;
    }
    public Double getTotalExpenseEachDay(String day, String month, String year) {
        double totalAmount=0;
        SQLiteDatabase db = this.getWritableDatabase();
        String whereArgs=day+"/"+month+"/"+year;
        String selectQuery = "SELECT "+COLUMN_HISTORY_AMOUNT+" FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '"+whereArgs+"' AND "+COLUMN_HISTORY_SIGN+" ='-'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                totalAmount+=cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return totalAmount;
    }

    public ArrayList<History> getAllHistoryIncomePerCategoryAnnual(String year) {
        ArrayList<History> histories = new ArrayList<History>();
        SQLiteDatabase db = this.getWritableDatabase();

        String whereArgs=year;
        String selectQuery = "SELECT "+COLUMN_HISTORY_CATEGORY+", SUM("+COLUMN_HISTORY_AMOUNT+") as amountPerCategory FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '%"+whereArgs+"' AND "+COLUMN_HISTORY_SIGN+" ='+' GROUP BY "+COLUMN_HISTORY_CATEGORY+" ORDER BY amountPerCategory DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                History history = new History();
                history.setHistoryCategory(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_CATEGORY)));

                Double amount =cursor.getDouble(cursor.getColumnIndex("amountPerCategory"));

                history.setHistoryAmount(amount);
                histories.add(history);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return histories;
    }

    public ArrayList<History> getAllHistoryExpensePerCategoryAnnual(String year) {
        ArrayList<History> histories = new ArrayList<History>();
        SQLiteDatabase db = this.getWritableDatabase();

        String whereArgs=year;
        String selectQuery = "SELECT "+COLUMN_HISTORY_CATEGORY+", SUM("+COLUMN_HISTORY_AMOUNT+") as amountPerCategory FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '%"+whereArgs+"' AND "+COLUMN_HISTORY_SIGN+" ='-' GROUP BY "+COLUMN_HISTORY_CATEGORY+" ORDER BY amountPerCategory DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                History history = new History();
                history.setHistoryCategory(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_CATEGORY)));

                Double amount =cursor.getDouble(cursor.getColumnIndex("amountPerCategory"));

                history.setHistoryAmount(amount);
                histories.add(history);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return histories;
    }

    public ArrayList<History> getAllHistoryIncomePerCategoryEachMonth(String month, String year) {
        ArrayList<History> histories = new ArrayList<History>();
        SQLiteDatabase db = this.getWritableDatabase();

        String whereArgs=month+"/"+year;
        //String selectQuery = "SELECT DISTINCT "+COLUMN_HISTORY_CATEGORY+" FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '%"+whereArgs+"' AND "+COLUMN_HISTORY_SIGN+" ='+' ";
        String selectQuery = "SELECT "+COLUMN_HISTORY_CATEGORY+", SUM("+COLUMN_HISTORY_AMOUNT+") as amountPerCategory FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '%"+whereArgs+"' AND "+COLUMN_HISTORY_SIGN+" ='+' GROUP BY "+COLUMN_HISTORY_CATEGORY+" ORDER BY amountPerCategory DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                History history = new History();
                history.setHistoryCategory(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_CATEGORY)));

                Double amount =cursor.getDouble(cursor.getColumnIndex("amountPerCategory"));

                history.setHistoryAmount(amount);
                //history.setHistoryAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT)));

//                String selectQueryeach = "SELECT "+COLUMN_HISTORY_AMOUNT+" FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_CATEGORY+" ='"+cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_CATEGORY))+"' AND "+COLUMN_HISTORY_SIGN+" ='+' AND "+COLUMN_HISTORY_DATE+" LIKE '%"+whereArgs+"'";
//                Cursor cursorEach = db.rawQuery(selectQueryeach, null);
//                long totalAmount=0;
//                if (cursorEach.moveToFirst()){
//                    do {
//                        totalAmount+=Long.parseLong(cursorEach.getString(cursorEach.getColumnIndex(COLUMN_HISTORY_AMOUNT)));
//                    }while (cursorEach.moveToNext());
//                }
//                history.setHistoryAmount(String.valueOf(totalAmount));
                histories.add(history);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return histories;
    }


    public ArrayList<History> getAllHistoryExpensePerCategoryEachMonth(String month, String year) {
        ArrayList<History> histories = new ArrayList<History>();
        SQLiteDatabase db = this.getWritableDatabase();

        String whereArgs=month+"/"+year;
        //String selectQuery = "SELECT DISTINCT "+COLUMN_HISTORY_CATEGORY+" FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '%"+whereArgs+"' AND "+COLUMN_HISTORY_SIGN+" ='-' ";
        String selectQuery = "SELECT "+COLUMN_HISTORY_CATEGORY+", SUM("+COLUMN_HISTORY_AMOUNT+") as amountPerCategory FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '%"+whereArgs+"' AND "+COLUMN_HISTORY_SIGN+" ='-' GROUP BY "+COLUMN_HISTORY_CATEGORY+" ORDER BY amountPerCategory DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                History history = new History();
                history.setHistoryCategory(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_CATEGORY)));

                Double amount =cursor.getDouble(cursor.getColumnIndex("amountPerCategory"));

                history.setHistoryAmount(amount);

                //history.setHistoryAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT)));
//                String selectQueryeach = "SELECT "+COLUMN_HISTORY_AMOUNT+" FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_CATEGORY+" ='"+cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_CATEGORY))+"' AND "+COLUMN_HISTORY_SIGN+" ='-' AND "+COLUMN_HISTORY_DATE+" LIKE '%"+whereArgs+"'";
//                Cursor cursorEach = db.rawQuery(selectQueryeach, null);
//                long totalAmount=0;
//                if (cursorEach.moveToFirst()){
//                    do {
//                        totalAmount+=Long.parseLong(cursorEach.getString(cursorEach.getColumnIndex(COLUMN_HISTORY_AMOUNT)));
//                    }while (cursorEach.moveToNext());
//                }
//                history.setHistoryAmount(String.valueOf(totalAmount));

                histories.add(history);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return histories;
    }

    //DAILY
    public ArrayList<History> getAllHistoryIncomePerCategoryEachDaily(String day, String month, String year) {
        ArrayList<History> histories = new ArrayList<History>();
        SQLiteDatabase db = this.getWritableDatabase();

        String whereArgs=day+"/"+month+"/"+year;
        String selectQuery = "SELECT "+COLUMN_HISTORY_CATEGORY+", SUM("+COLUMN_HISTORY_AMOUNT+") as amountPerCategory FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '"+whereArgs+"' AND "+COLUMN_HISTORY_SIGN+" ='+' GROUP BY "+COLUMN_HISTORY_CATEGORY+" ORDER BY amountPerCategory DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                History history = new History();
                history.setHistoryCategory(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_CATEGORY)));

                Double amount =cursor.getDouble(cursor.getColumnIndex("amountPerCategory"));

                history.setHistoryAmount(amount);
                histories.add(history);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return histories;
    }

    public ArrayList<History> getAllHistoryExpensePerCategoryEachDaily(String day, String month, String year) {
        ArrayList<History> histories = new ArrayList<History>();
        SQLiteDatabase db = this.getWritableDatabase();

        String whereArgs=day+"/"+month+"/"+year;
        String selectQuery = "SELECT "+COLUMN_HISTORY_CATEGORY+", SUM("+COLUMN_HISTORY_AMOUNT+") as amountPerCategory FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '"+whereArgs+"' AND "+COLUMN_HISTORY_SIGN+" ='-' GROUP BY "+COLUMN_HISTORY_CATEGORY+" ORDER BY amountPerCategory DESC";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                History history = new History();
                history.setHistoryCategory(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_CATEGORY)));

                Double amount =cursor.getDouble(cursor.getColumnIndex("amountPerCategory"));

                history.setHistoryAmount(amount);
                histories.add(history);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return histories;
    }


    public History getHistory(String historyId) {
        History history = new History();
        SQLiteDatabase db = this.getWritableDatabase();

        int id= Integer.parseInt(historyId.trim());
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_ID+" ='"+id+"' ";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            history.setHistoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_HISTORY_ID)));
            history.setHistoryCategory(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_CATEGORY)));
            history.setHistoryAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT)));
            history.setHistorySign(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_SIGN)));
            history.setHistoryDetail(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DETAIL)));
            history.setHistoryDate(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DATE)));
            history.setHistoryTime(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_TIME)));
            history.setHistoryDateTime(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DATE_TIME)));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return history;
    }

    public void updateHistory(History history) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HISTORY_AMOUNT, history.getHistoryAmount());
        values.put(COLUMN_HISTORY_DETAIL, history.getHistoryDetail());
        values.put(COLUMN_HISTORY_CATEGORY, history.getHistoryCategory());
        values.put(COLUMN_HISTORY_SIGN, history.getHistorySign());
        values.put(COLUMN_HISTORY_DATE, history.getHistoryDate());
        values.put(COLUMN_HISTORY_TIME, history.getHistoryTime());
        values.put(COLUMN_HISTORY_DATE_TIME, history.getHistoryDateTime());
        // updating row
        db.update(TABLE_HISTORY, values, COLUMN_HISTORY_ID + " = ?", new String[] { String.valueOf(history.getHistoryId()) });
    }

    public ArrayList<String > getYearHistoryDistinct() {
        ArrayList<String> itemYears = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT DISTINCT SUBSTR("+COLUMN_HISTORY_DATE+", 7, 4) year FROM " + TABLE_HISTORY+ " ORDER BY 1";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                itemYears.add(cursor.getString(cursor.getColumnIndex("year")));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return itemYears;
    }

    //Parameter: (selectedMonth,selectedYear,selectedCategory,sortingStatus,type)
    //ex: (02,2018,Food,Desc,all) all: [income dan expense]
    public ArrayList<History> getAllHistoryFilter(String selectedMonth, String selectedYear, String selectedCategory, String sortingStatus, String statusType) {
        ArrayList<History> histories = new ArrayList<History>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_HISTORY+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" DESC";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                History history = new History();
                history.setHistoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_HISTORY_ID)));
                history.setHistoryCategory(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_CATEGORY)));
                history.setHistoryAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT)));
                history.setHistorySign(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_SIGN)));
                history.setHistoryDetail(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DETAIL)));
                history.setHistoryDate(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DATE)));
                history.setHistoryTime(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_TIME)));
                history.setHistoryDateTime(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DATE_TIME)));

                histories.add(history);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return histories;
    }

    public ArrayList<History> getAllHistoryBasedOnParameter(String currentMonthSelected,String currentYearSelected,String currentCategorySelected,String currentSortingStatus,String currentIncomeBtnStatus,String currentExpenseBtnStatus){
        ArrayList<History> histories = new ArrayList<History>();
        SQLiteDatabase db = this.getWritableDatabase();
        String sign="";
        if(currentIncomeBtnStatus.equalsIgnoreCase("Active")&&currentExpenseBtnStatus.equalsIgnoreCase("NotActive")){
            sign="+";
        }
        else if(currentIncomeBtnStatus.equalsIgnoreCase("NotActive")&&currentExpenseBtnStatus.equalsIgnoreCase("Active")){
            sign="-";
        }
        String whereArgs=currentMonthSelected+"/"+currentYearSelected;
        String selectQuery ="";

        if(currentMonthSelected.equalsIgnoreCase("00")&&
                currentYearSelected.equalsIgnoreCase("All") &&
                currentCategorySelected.equalsIgnoreCase("All")){
            if(currentIncomeBtnStatus.equalsIgnoreCase("Active")&&currentExpenseBtnStatus.equalsIgnoreCase("Active")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else if(currentIncomeBtnStatus.equalsIgnoreCase("NotActive")&&currentExpenseBtnStatus.equalsIgnoreCase("NotActive")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_SIGN+" ='NotAll' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else{
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_SIGN+" ='"+sign+"' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
        }
        else if(!currentMonthSelected.equalsIgnoreCase("00")&&
                !currentYearSelected.equalsIgnoreCase("All") &&
                !currentCategorySelected.equalsIgnoreCase("All")){
            if(currentIncomeBtnStatus.equalsIgnoreCase("Active")&&currentExpenseBtnStatus.equalsIgnoreCase("Active")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '%"+whereArgs+"' AND "+COLUMN_HISTORY_CATEGORY+" ='"+currentCategorySelected+"' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else if(currentIncomeBtnStatus.equalsIgnoreCase("NotActive")&&currentExpenseBtnStatus.equalsIgnoreCase("NotActive")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '%"+whereArgs+"' AND "+COLUMN_HISTORY_CATEGORY+" ='"+currentCategorySelected+"' AND "+COLUMN_HISTORY_SIGN+" ='NotAll' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else{
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DATE+" LIKE '%"+whereArgs+"' AND "+COLUMN_HISTORY_CATEGORY+" ='"+currentCategorySelected+"' AND "+COLUMN_HISTORY_SIGN+" ='"+sign+"' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
        }
        else if(!currentMonthSelected.equalsIgnoreCase("00")&&
                currentYearSelected.equalsIgnoreCase("All") &&
                currentCategorySelected.equalsIgnoreCase("All")){
            if(currentIncomeBtnStatus.equalsIgnoreCase("Active")&&currentExpenseBtnStatus.equalsIgnoreCase("Active")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE SUBSTR("+COLUMN_HISTORY_DATE+", 4, 2) = '"+currentMonthSelected+"' ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else if(currentIncomeBtnStatus.equalsIgnoreCase("NotActive")&&currentExpenseBtnStatus.equalsIgnoreCase("NotActive")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE SUBSTR("+COLUMN_HISTORY_DATE+", 4, 2) = '"+currentMonthSelected+"' AND "+COLUMN_HISTORY_SIGN+" ='NotAll' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else{
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE SUBSTR("+COLUMN_HISTORY_DATE+", 4, 2) = '"+currentMonthSelected+"' AND "+COLUMN_HISTORY_SIGN+" ='"+sign+"' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
        }
        else if(currentMonthSelected.equalsIgnoreCase("00")&&
                !currentYearSelected.equalsIgnoreCase("All") &&
                currentCategorySelected.equalsIgnoreCase("All")){
            if(currentIncomeBtnStatus.equalsIgnoreCase("Active")&&currentExpenseBtnStatus.equalsIgnoreCase("Active")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE SUBSTR("+COLUMN_HISTORY_DATE+", 7, 4) = '"+currentYearSelected+"' ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else if(currentIncomeBtnStatus.equalsIgnoreCase("NotActive")&&currentExpenseBtnStatus.equalsIgnoreCase("NotActive")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE SUBSTR("+COLUMN_HISTORY_DATE+", 7, 4) = '"+currentYearSelected+"' AND "+COLUMN_HISTORY_SIGN+" ='NotAll' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else{
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE SUBSTR("+COLUMN_HISTORY_DATE+", 7, 4) = '"+currentYearSelected+"' AND "+COLUMN_HISTORY_SIGN+" ='"+sign+"' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
        }
        else if(currentMonthSelected.equalsIgnoreCase("00")&&
                currentYearSelected.equalsIgnoreCase("All") &&
                !currentCategorySelected.equalsIgnoreCase("All")){
            if(currentIncomeBtnStatus.equalsIgnoreCase("Active")&&currentExpenseBtnStatus.equalsIgnoreCase("Active")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_CATEGORY+" ='"+currentCategorySelected+"'  ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else if(currentIncomeBtnStatus.equalsIgnoreCase("NotActive")&&currentExpenseBtnStatus.equalsIgnoreCase("NotActive")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_CATEGORY+" ='"+currentCategorySelected+"'  AND "+COLUMN_HISTORY_SIGN+" ='NotAll' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else{
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_CATEGORY+" ='"+currentCategorySelected+"'  AND "+COLUMN_HISTORY_SIGN+" ='"+sign+"' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
        }
        else if(currentMonthSelected.equalsIgnoreCase("00")&&
                !currentYearSelected.equalsIgnoreCase("All") &&
                !currentCategorySelected.equalsIgnoreCase("All")){
            if(currentIncomeBtnStatus.equalsIgnoreCase("Active")&&currentExpenseBtnStatus.equalsIgnoreCase("Active")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_CATEGORY+" ='"+currentCategorySelected+"'  AND  SUBSTR("+COLUMN_HISTORY_DATE+", 7, 4) = '"+currentYearSelected+"' ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else if(currentIncomeBtnStatus.equalsIgnoreCase("NotActive")&&currentExpenseBtnStatus.equalsIgnoreCase("NotActive")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE  "+COLUMN_HISTORY_CATEGORY+" ='"+currentCategorySelected+"'  AND SUBSTR("+COLUMN_HISTORY_DATE+", 7, 4) = '"+currentYearSelected+"' AND "+COLUMN_HISTORY_SIGN+" ='NotAll' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else{
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE  "+COLUMN_HISTORY_CATEGORY+" ='"+currentCategorySelected+"'  AND SUBSTR("+COLUMN_HISTORY_DATE+", 7, 4) = '"+currentYearSelected+"' AND "+COLUMN_HISTORY_SIGN+" ='"+sign+"' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
        }
        else if(!currentMonthSelected.equalsIgnoreCase("00")&&
                currentYearSelected.equalsIgnoreCase("All") &&
                !currentCategorySelected.equalsIgnoreCase("All")){
            if(currentIncomeBtnStatus.equalsIgnoreCase("Active")&&currentExpenseBtnStatus.equalsIgnoreCase("Active")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_CATEGORY+" ='"+currentCategorySelected+"'  AND  SUBSTR("+COLUMN_HISTORY_DATE+", 4, 2) = '"+currentMonthSelected+"' ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else if(currentIncomeBtnStatus.equalsIgnoreCase("NotActive")&&currentExpenseBtnStatus.equalsIgnoreCase("NotActive")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_CATEGORY+" ='"+currentCategorySelected+"'  AND  SUBSTR("+COLUMN_HISTORY_DATE+", 4, 2) = '"+currentMonthSelected+"' AND "+COLUMN_HISTORY_SIGN+" ='NotAll' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else{
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_CATEGORY+" ='"+currentCategorySelected+"'  AND  SUBSTR("+COLUMN_HISTORY_DATE+", 4, 2) = '"+currentMonthSelected+"' AND "+COLUMN_HISTORY_SIGN+" ='"+sign+"' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
        }
        else if(!currentMonthSelected.equalsIgnoreCase("00")&&
                !currentYearSelected.equalsIgnoreCase("All") &&
                currentCategorySelected.equalsIgnoreCase("All")){
            if(currentIncomeBtnStatus.equalsIgnoreCase("Active")&&currentExpenseBtnStatus.equalsIgnoreCase("Active")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE SUBSTR("+COLUMN_HISTORY_DATE+", 7, 4) = '"+currentYearSelected+"' AND SUBSTR("+COLUMN_HISTORY_DATE+", 4, 2) = '"+currentMonthSelected+"' ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else if(currentIncomeBtnStatus.equalsIgnoreCase("NotActive")&&currentExpenseBtnStatus.equalsIgnoreCase("NotActive")){
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE SUBSTR("+COLUMN_HISTORY_DATE+", 7, 4) = '"+currentYearSelected+"' AND SUBSTR("+COLUMN_HISTORY_DATE+", 4, 2) = '"+currentMonthSelected+"' AND "+COLUMN_HISTORY_SIGN+" ='NotAll' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
            else{
                selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE SUBSTR("+COLUMN_HISTORY_DATE+", 7, 4) = '"+currentYearSelected+"' AND  SUBSTR("+COLUMN_HISTORY_DATE+", 4, 2) = '"+currentMonthSelected+"' AND "+COLUMN_HISTORY_SIGN+" ='"+sign+"' "+" ORDER BY "+COLUMN_HISTORY_DATE_TIME +" "+currentSortingStatus;
            }
        }

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                History history = new History();
                history.setHistoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_HISTORY_ID)));
                history.setHistoryCategory(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_CATEGORY)));
                history.setHistoryAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT)));
                history.setHistorySign(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_SIGN)));
                history.setHistoryDetail(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DETAIL)));
                history.setHistoryDate(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DATE)));
                history.setHistoryTime(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_TIME)));
                history.setHistoryDateTime(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DATE_TIME)));

                histories.add(history);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return histories;
    }

    public ArrayList<Category> getCategoriesIncomeSetting() {
        ArrayList<Category> categories = new ArrayList<Category>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY+" WHERE "+COLUMN_CATEGORY_SIGN+" = '+'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                Category category = new Category();
                category.setCategoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                category.setCategoryName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                category.setCategorySign(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_SIGN)));

                categories.add(category);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return categories;
    }
    public ArrayList<Category> getCategoriesExpenseSetting() {
        ArrayList<Category> categories = new ArrayList<Category>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY+" WHERE "+COLUMN_CATEGORY_SIGN+" = '-'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                Category category = new Category();
                category.setCategoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                category.setCategoryName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                category.setCategorySign(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_SIGN)));

                categories.add(category);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return categories;
    }

    public void deleteCategory(String categoryId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORY,COLUMN_CATEGORY_ID + " = ?", new String[] {categoryId});
        db.close();
    }
    public Category getCategory(String categoryId) {
        Category category = new Category();
        SQLiteDatabase db = this.getWritableDatabase();

        int id= Integer.parseInt(categoryId.trim());
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY+" WHERE "+COLUMN_CATEGORY_ID+" ='"+id+"' ";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            category.setCategoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
            category.setCategoryName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
            category.setCategorySign(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_SIGN)));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return category;
    }

    public boolean checkCategory(String categoryName) {
        boolean check = false;
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY+" WHERE "+COLUMN_CATEGORY_NAME+" = '" + categoryName + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount()!=0){
            check=true;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return check;
    }

    public void updateCategory(Category category) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_ID, String.valueOf(category.getCategoryId()));
        values.put(COLUMN_CATEGORY_NAME, category.getCategoryName());
        // updating row
        db.update(TABLE_CATEGORY, values, COLUMN_CATEGORY_ID + " = ?", new String[] { String.valueOf(category.getCategoryId()) });
    }

    public void updateHistoryCauseCategoryEdited(String oldCategory,Category category) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HISTORY_CATEGORY, category.getCategoryName());
        // updating row
        db.update(TABLE_HISTORY, values, COLUMN_HISTORY_CATEGORY + " = ?", new String[] { oldCategory });
    }
    public void addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CATEGORY_NAME, category.getCategoryName());
        values.put(COLUMN_CATEGORY_SIGN, category.getCategorySign());
        // Inserting Row
        db.insert(TABLE_CATEGORY, null, values);
        db.close(); // Closing database connection
    }
    public ArrayList<History> getAllHistoryBasedOnSearch(String searchedDetailHistory){
        ArrayList<History> histories = new ArrayList<History>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_DETAIL+" LIKE '%"+searchedDetailHistory+"%' COLLATE NOCASE ORDER BY "+COLUMN_HISTORY_DATE_TIME +" DESC";;

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            if(!searchedDetailHistory.equalsIgnoreCase("")) {
                do {
                    History history = new History();
                    history.setHistoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_HISTORY_ID)));
                    history.setHistoryCategory(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_CATEGORY)));
                    history.setHistoryAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT)));
                    history.setHistorySign(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_SIGN)));
                    history.setHistoryDetail(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DETAIL)));
                    history.setHistoryDate(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DATE)));
                    history.setHistoryTime(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_TIME)));
                    history.setHistoryDateTime(cursor.getString(cursor.getColumnIndex(COLUMN_HISTORY_DATE_TIME)));

                    histories.add(history);
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return histories;
    }

    public void addBudget(Budget budget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_BUDGET_CATEGORY, budget.getBudgetCategory());
        values.put(COLUMN_BUDGET_AMOUNT, budget.getBudgetAmount());
        values.put(COLUMN_BUDGET_MONTH, budget.getBudgetMonth());
        values.put(COLUMN_BUDGET_YEAR, budget.getBudgetYear());

        db.insert(TABLE_BUDGET, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<Budget> getAllBudget(String month, String year) {
        ArrayList<Budget> budgets = new ArrayList<Budget>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_BUDGET+" WHERE "+COLUMN_BUDGET_MONTH+" ='"+month+"' AND "+COLUMN_BUDGET_YEAR+" ='"+year+"' ORDER BY 2";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                Budget budget = new Budget();
                budget.setBudgetId(cursor.getInt(cursor.getColumnIndex(COLUMN_BUDGET_ID)));
                budget.setBudgetCategory(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_CATEGORY)));
                budget.setBudgetAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_BUDGET_AMOUNT)));
                budget.setBudgetMonth(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_MONTH)));
                budget.setBudgetYear(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_YEAR)));

                budgets.add(budget);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return budgets;
    }

    public Double getTotalExpenseWithParameter(String category, String month, String year) {
        double totalAmount=0;
        SQLiteDatabase db = this.getWritableDatabase();
        String date = year+month;
        String selectQuery = "SELECT "+COLUMN_HISTORY_AMOUNT+" FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_CATEGORY+" = '"+category+"' AND SUBSTR("+COLUMN_HISTORY_DATE_TIME+",1,6) = '"+date+"' ";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                totalAmount+=cursor.getDouble(cursor.getColumnIndex(COLUMN_HISTORY_AMOUNT));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return totalAmount;
    }
    public ArrayList<String > getYearBudgetDistinct() {
        ArrayList<String> itemYears = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT DISTINCT "+COLUMN_BUDGET_YEAR+" FROM " + TABLE_BUDGET+ " ORDER BY 1";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                itemYears.add(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_YEAR)));
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return itemYears;
    }
    public void deleteBudget(String budgetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BUDGET,COLUMN_BUDGET_ID + " = ?", new String[] {budgetId});
        db.close();
    }
    public Budget getBudget(String budgetId) {
        Budget budget = new Budget();
        SQLiteDatabase db = this.getWritableDatabase();

        int id= Integer.parseInt(budgetId.trim());
        String selectQuery = "SELECT * FROM " + TABLE_BUDGET+" WHERE "+COLUMN_BUDGET_ID+" ='"+id+"' ";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            budget.setBudgetId(cursor.getInt(cursor.getColumnIndex(COLUMN_BUDGET_ID)));
            budget.setBudgetCategory(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_CATEGORY)));
            budget.setBudgetAmount(cursor.getDouble(cursor.getColumnIndex(COLUMN_BUDGET_AMOUNT)));
            budget.setBudgetMonth(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_MONTH)));
            budget.setBudgetYear(cursor.getString(cursor.getColumnIndex(COLUMN_BUDGET_YEAR)));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return budget;
    }
    public void updateBudget(Budget budget) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BUDGET_AMOUNT, budget.getBudgetAmount());
        values.put(COLUMN_BUDGET_CATEGORY, budget.getBudgetCategory());
        values.put(COLUMN_BUDGET_MONTH, budget.getBudgetMonth());
        values.put(COLUMN_BUDGET_YEAR, budget.getBudgetYear());
        // updating row
        db.update(TABLE_BUDGET, values, COLUMN_BUDGET_ID + " = ?", new String[] { String.valueOf(budget.getBudgetId()) });
    }
    public boolean checkCategoryBudget(String categoryName, String month, String year) {
        boolean check = false;
        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_BUDGET+" WHERE "+COLUMN_BUDGET_MONTH+" ='"+month+"' AND "+COLUMN_BUDGET_YEAR+" ='"+year+"' AND "+COLUMN_BUDGET_CATEGORY+" ='"+categoryName+"' ";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount()!=0){
            check=true;
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return check;
    }
    public Integer getTotalExpenseHistoryWithParameter(String category, String month, String year) {
        int totalHistory=0;
        SQLiteDatabase db = this.getWritableDatabase();
        String date = year+month;
        String selectQuery = "SELECT COUNT(*) FROM " + TABLE_HISTORY+" WHERE "+COLUMN_HISTORY_CATEGORY+" = '"+category+"' AND SUBSTR("+COLUMN_HISTORY_DATE_TIME+",1,6) = '"+date+"' ";

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            totalHistory=cursor.getInt(0);
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        return totalHistory;
    }
}
