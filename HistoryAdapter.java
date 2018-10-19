package Diretory.dev.mywallet.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import Diretory.dev.mywallet.Model.History;
import Diretory.dev.mywallet.R;



public class HistoryAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<History> historyList;

    public HistoryAdapter(Context context, ArrayList<History> historyList) {
        this.context = context;
        this.historyList = historyList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int i) {
        return historyList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(getCount()>0) {
            final History lHistory = (History) getItem(i);
            if (view == null) {
                LayoutInflater infalInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = infalInflater.inflate(R.layout.layout_lv_history_each_wallet, null);
            }
            //Text
            TextView historyId = (TextView) view.findViewById(R.id.tv_history_id);
            TextView datetime = (TextView) view.findViewById(R.id.datetime_history);
            TextView category = (TextView) view.findViewById(R.id.tv_category_history);
            TextView detail = (TextView) view.findViewById(R.id.tv_detail_history);
            TextView amount = (TextView) view.findViewById(R.id.tv_amount_history);

            ImageView signImage = (ImageView) view.findViewById(R.id.imageview_sign_history);

            String month, day, year, dateEdited="";
            year = lHistory.getHistoryDate().substring(6,10);
            day = lHistory.getHistoryDate().substring(0,2);
            month =lHistory.getHistoryDate().substring(3,5);

            if(month.equalsIgnoreCase("01")) month = "January";
            else if(month.equalsIgnoreCase("02")) month = "February";
            else if(month.equalsIgnoreCase("03")) month = "March";
            else if(month.equalsIgnoreCase("04")) month = "April";
            else if(month.equalsIgnoreCase("05")) month = "May";
            else if(month.equalsIgnoreCase("06")) month = "June";
            else if(month.equalsIgnoreCase("07")) month = "July";
            else if(month.equalsIgnoreCase("08")) month = "August";
            else if(month.equalsIgnoreCase("09")) month = "September";
            else if(month.equalsIgnoreCase("10")) month = "October";
            else if(month.equalsIgnoreCase("11")) month = "November";
            else if(month.equalsIgnoreCase("12")) month = "Desember";

            if(day.substring(0,1).equalsIgnoreCase("0")) day = day.substring(1);

            dateEdited = month+" "+day+", "+year;

            historyId.setText(String.valueOf(lHistory.getHistoryId()));
            datetime.setText(dateEdited+", "+lHistory.getHistoryTime());
            category.setText(lHistory.getHistoryCategory());
            detail.setText(lHistory.getHistoryDetail());

//            DecimalFormat fmt = new DecimalFormat("#,###");

            DecimalFormat twoDForm = new DecimalFormat("#,##0.00");
            amount.setText( twoDForm.format(lHistory.getHistoryAmount()));
            //amount.setText( fmt.format(Long.parseLong(lHistory.getHistoryAmount())));

            Drawable removeDrawable = context.getResources().getDrawable(R.drawable.ic_remove_circle_black_24dp);
            Drawable addDrawable = context.getResources().getDrawable(R.drawable.ic_add_circle_black_24dp);

            if(lHistory.getHistorySign().equalsIgnoreCase("-")){
                signImage.setImageDrawable(removeDrawable);
                ImageViewCompat.setImageTintList(signImage, ColorStateList.valueOf(ContextCompat.getColor
                        (context, R.color.red_icon_expense)));
            }
            else if(lHistory.getHistorySign().equalsIgnoreCase("+")){
                signImage.setImageDrawable(addDrawable);
                ImageViewCompat.setImageTintList(signImage, ColorStateList.valueOf(ContextCompat.getColor
                        (context, R.color.green_icon_income)));
            }
        }
        else {
            if (view == null) {
                LayoutInflater infalInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = infalInflater.inflate(R.layout.layout_listview_no_data, null);
            }
        }
        return view;
    }
}
