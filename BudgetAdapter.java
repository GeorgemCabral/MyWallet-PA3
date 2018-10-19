package Diretory.dev.mywallet.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import Diretory.dev.mywallet.BudgetActivity;
import Diretory.dev.mywallet.Database.DatabaseHandler;
import Diretory.dev.mywallet.Model.Budget;
import Diretory.dev.mywallet.Model.History;
import Diretory.dev.mywallet.R;



public class BudgetAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Budget> budgetList;
    private String currentMonth;
    private String currentYear;

    public BudgetAdapter(Context context, ArrayList<Budget> budgetList,String currentMonth, String currentYear) {
        this.context = context;
        this.budgetList = budgetList;
        this.currentMonth = currentMonth;
        this.currentYear = currentYear;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return budgetList.size();
    }

    @Override
    public Object getItem(int i) {
        return budgetList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(getCount()>0) {
            final Budget lBudget = (Budget) getItem(i);
            if (view == null) {
                LayoutInflater infalInflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = infalInflater.inflate(R.layout.layout_lv_budget, null);
            }
            //Text
            TextView budget_Id = (TextView) view.findViewById(R.id.tv_budget_id);
            TextView budget_category = (TextView) view.findViewById(R.id.budget_category);
            TextView total_history = (TextView) view.findViewById(R.id.total_history);
            TextView budget_result = (TextView) view.findViewById(R.id.budget_result);
            TextView budget_result_status = (TextView) view.findViewById(R.id.budget_result_status);
            LinearLayout percentage_layout = (LinearLayout) view.findViewById(R.id.percentage_layout);
            TextView budget_used = (TextView) view.findViewById(R.id.budget_used);
            TextView budget_amount = (TextView) view.findViewById(R.id.budget_amount);
            TextView tv_percentage = (TextView) view.findViewById(R.id.tv_percentage);

            budget_Id.setText(String.valueOf(lBudget.getBudgetId()));

            budget_category.setText(lBudget.getBudgetCategory());

            DecimalFormat twoDForm = new DecimalFormat("#,##0.00");
            budget_amount.setText( twoDForm.format(lBudget.getBudgetAmount()));

            DatabaseHandler db = new DatabaseHandler(context);
            int totalHistory = db.getTotalExpenseHistoryWithParameter(lBudget.getBudgetCategory(),currentMonth, currentYear);
            total_history.setText("("+String.valueOf(totalHistory)+")");

            Double dBudget_used = db.getTotalExpenseWithParameter(lBudget.getBudgetCategory(),currentMonth, currentYear);
            budget_used.setText(twoDForm.format(dBudget_used));

            float percentage = Float.parseFloat(String.valueOf(dBudget_used/lBudget.getBudgetAmount()));
            DecimalFormat twoDFormPercen = new DecimalFormat("0.00");
            tv_percentage.setText( twoDFormPercen.format(percentage*100)+ "%");

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    percentage_layout.getLayoutParams();
            params.weight = percentage;
            percentage_layout.setLayoutParams(params);
            if(percentage<=0.6){
                percentage_layout.setBackgroundColor(context.getResources().getColor(R.color.green_icon_income));
            }
            else if (percentage >=1){
                percentage_layout.setBackgroundColor(context.getResources().getColor(R.color.red_icon_expense));
            }
            else {
                percentage_layout.setBackgroundColor(context.getResources().getColor(R.color.orange_icon_budget));
            }

            double budgetResult = lBudget.getBudgetAmount() - dBudget_used;
            budget_result.setText(twoDForm.format(budgetResult));

            String status = "";
            if(budgetResult<0){
                status = "over";
                budget_result_status.setTextColor(context.getResources().getColor(R.color.red_icon_expense));
            }
            else {
                status = "saved";
                budget_result_status.setTextColor(context.getResources().getColor(R.color.green_icon_income));
            }
            budget_result_status.setText(status);
        }
        else {
            view = layoutInflater.inflate(R.layout.layout_listview_no_data, null);
        }
        return view;
    }
}
