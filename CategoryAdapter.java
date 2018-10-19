package Diretory.dev.mywallet.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.icu.util.ULocale;
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

import Diretory.dev.mywallet.Model.Category;
import Diretory.dev.mywallet.Model.History;
import Diretory.dev.mywallet.R;



public class CategoryAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Category> categoryList;

    public CategoryAdapter(Context context, ArrayList<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int i) {
        return categoryList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(getCount()>0) {
            final Category lCategory = (Category) getItem(i);
            if (view == null) {
                LayoutInflater infalInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = infalInflater.inflate(R.layout.layout_each_category, null);
            }
            //Text
            TextView categoryId = (TextView) view.findViewById(R.id.tv_category_id);
            TextView categoryName = (TextView) view.findViewById(R.id.category_name);

            categoryId.setText(String.valueOf(lCategory.getCategoryId()));
            categoryName.setText(lCategory.getCategoryName());
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
