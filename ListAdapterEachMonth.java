package Diretory.dev.mywallet.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.xml.transform.dom.DOMLocator;

import Diretory.dev.mywallet.Model.History;
import Diretory.dev.mywallet.R;



public class ListAdapterEachMonth extends BaseAdapter{

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<History> eachlistpercategory;
    private Double total;

    public ListAdapterEachMonth(Context context, ArrayList<History> historyList,Double total) {
        this.context = context;
        this.eachlistpercategory = historyList;
        this.total =total;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return eachlistpercategory.size();
    }

    @Override
    public Object getItem(int i) {
        return eachlistpercategory.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final History lHistory = (History) getItem(i);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.layout_eachmonth, null);
        }
        //Text
        TextView category = (TextView) view.findViewById(R.id.tv_category_each_month);
        TextView persen = (TextView) view.findViewById(R.id.tv_persen_each_month);
        TextView amount = (TextView) view.findViewById(R.id.tv_amount_each_month);

        category.setText(lHistory.getHistoryCategory());

        double persentase=0;
        persentase =((lHistory.getHistoryAmount()/total)*100);

        DecimalFormat twoDFormPersen = new DecimalFormat("0.00");
        if(persentase==100.0){
            persen.setText("(100%)");
        }
        else {
            persen.setText("(" + twoDFormPersen.format(persentase) + "%)");
        }
        DecimalFormat twoDForm = new DecimalFormat("#,##0.00");

        amount.setText( twoDForm.format(lHistory.getHistoryAmount()));
        //amount.setText( fmt.format(Long.parseLong(lHistory.getHistoryAmount())));

        return view;
    }
}
