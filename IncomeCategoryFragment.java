package Diretory.dev.mywallet.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import Diretory.dev.mywallet.Adapter.CategoryAdapter;
import Diretory.dev.mywallet.Database.DatabaseHandler;
import Diretory.dev.mywallet.HistoryActivity;
import Diretory.dev.mywallet.Model.Category;
import Diretory.dev.mywallet.Model.History;
import Diretory.dev.mywallet.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;



public class IncomeCategoryFragment extends Fragment {

    DatabaseHandler controller;

    ArrayList<Category> categories;
    View rootView;
    ListView listView;

    private PopupWindow mPopupWindow;

    LinearLayout linearLayout;

    ImageView btnAddCategory;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        controller = new DatabaseHandler(this.getActivity());
        categories = new ArrayList<Category>();
        categories = controller.getCategoriesIncomeSetting();

            rootView= inflater.inflate(R.layout.fragment_income_category,null);
            linearLayout = (LinearLayout) rootView.findViewById(R.id.r_layout_category);
            listView =(ListView) rootView.findViewById(R.id.lv_income_category);
            setValueCategory();

            btnAddCategory = rootView.findViewById(R.id.btn_add_category);
            btnAddCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                    // Inflate the custom layout/view
                    final View customView = inflater.inflate(R.layout.popup_add_category,null);//can use as add too

                    controller = new DatabaseHandler(getActivity());

                    // Initialize a new instance of popup window
                    mPopupWindow = new PopupWindow(
                            customView,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );

                    ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                    final EditText categoryEdited = (EditText) customView.findViewById(R.id.category_name_edittext);

                    final Button btnCancel = (Button) customView.findViewById(R.id.btn_cancel_edit);
                    final Button btnSave = (Button) customView.findViewById(R.id.btn_save_edit);
                    //TAMBAHAN DIM POPUP WINDOW
                    final ViewGroup root = (ViewGroup) getActivity().getWindow().getDecorView().getRootView();

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

                            DatabaseHandler db = new DatabaseHandler(getActivity());

                            String sCategoryName = categoryEdited.getText().toString();

                            if(sCategoryName.length()<=0){
                                Toast.makeText(getActivity(), "Category's name must be fill!!", Toast.LENGTH_LONG).show();
                            }
                            else {
                                if(cekCategoryName(sCategoryName)){
                                    Toast.makeText(getActivity(), "Category's name is already listed!!", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    sNext="Yes";
                                }
                            }

                            if(sNext.equalsIgnoreCase("Yes")){
                                Category categoryEdited = new Category();
                                categoryEdited.setCategoryName(sCategoryName);
                                categoryEdited.setCategorySign("+");

                                db.addCategory(categoryEdited);

                                setValueCategory();
                                Toast.makeText(getActivity(), "Category added", Toast.LENGTH_SHORT).show();
                                mPopupWindow.dismiss();
                            }
                        }
                    });
                    //TAMBAHAN DIM POPUP WINDOW
                    final ViewGroup root2 = (ViewGroup) getActivity().getWindow().getDecorView().getRootView();
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
                    mPopupWindow.showAtLocation(linearLayout, Gravity.CENTER,0,0);
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                    // Inflate the custom layout/view
                    View customView = inflater.inflate(R.layout.popup_list_history,null);
                    // Initialize a new instance of popup window
                    mPopupWindow = new PopupWindow(
                            customView,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    final TextView categoryId = (TextView) view.findViewById(R.id.tv_category_id);
                    final String sCategoryId = categoryId.getText().toString();

                    CardView btnEdit = (CardView) customView.findViewById(R.id.edit_cardview_btn);
                    CardView btnDelete = (CardView) customView.findViewById(R.id.delete_cardview_btn);

                    btnEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mPopupWindow.dismiss();
                            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                            // Inflate the custom layout/view
                            final View customView = inflater.inflate(R.layout.popup_edit_category,null);

                            controller = new DatabaseHandler(getActivity());

                            // Initialize a new instance of popup window
                            mPopupWindow = new PopupWindow(
                                    customView,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT
                            );

                            ImageButton closeButton = (ImageButton) customView.findViewById(R.id.ib_close);
                            final EditText categoryEdited = (EditText) customView.findViewById(R.id.category_name_edittext);

                            final Button btnCancel = (Button) customView.findViewById(R.id.btn_cancel_edit);
                            final Button btnSave = (Button) customView.findViewById(R.id.btn_save_edit);

                            final Category categorySelected = controller.getCategory(sCategoryId);
                            categoryEdited.setText(categorySelected.getCategoryName());
                            //TAMBAHAN DIM POPUP WINDOW
                            final ViewGroup root = (ViewGroup) getActivity().getWindow().getDecorView().getRootView();

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

                                    DatabaseHandler db = new DatabaseHandler(getActivity());

                                    String sCategoryName = categoryEdited.getText().toString();

                                    if(sCategoryName.length()<=0){
                                        Toast.makeText(getActivity(), "Category's name must be fill!!", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        if(cekCategoryName(sCategoryName)){
                                            Toast.makeText(getActivity(), "Category's name is already listed!!", Toast.LENGTH_LONG).show();
                                        }
                                        else {
                                            sNext="Yes";
                                        }
                                    }

                                    if(sNext.equalsIgnoreCase("Yes")){
                                        Category categoryEdited = new Category();
                                        categoryEdited.setCategoryId(Integer.parseInt(sCategoryId));
                                        categoryEdited.setCategoryName(sCategoryName);
                                        //update category db
                                        //update history category db
                                        db.updateCategory(categoryEdited);
                                        db.updateHistoryCauseCategoryEdited(categorySelected.getCategoryName(),categoryEdited);
                                        setValueCategory();
                                        Toast.makeText(getActivity(), "Category edited", Toast.LENGTH_SHORT).show();
                                        mPopupWindow.dismiss();
                                    }
                                }
                            });
                            //TAMBAHAN DIM POPUP WINDOW
                            final ViewGroup root2 = (ViewGroup) getActivity().getWindow().getDecorView().getRootView();
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
                            mPopupWindow.showAtLocation(linearLayout, Gravity.CENTER,0,0);
                        }
                    });

                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                            alertDialog.setTitle("Are you sure to delete this record?");
                            alertDialog.setIcon(R.drawable.ic_delete_black_24dp);
                            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseHandler db = new DatabaseHandler(getActivity());

                                    db.deleteCategory(sCategoryId);
                                    setValueCategory();

                                    Toast.makeText(getActivity(), "Category Deleted", Toast.LENGTH_SHORT).show();
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
                    final ViewGroup root = (ViewGroup) getActivity().getWindow().getDecorView().getRootView();
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
                    // Finally, show the popup window at the center location of root linear layout
                    mPopupWindow.showAtLocation(linearLayout, Gravity.CENTER,0,0);
                }
            });

        return rootView;
    }

    private boolean cekCategoryName(String sCategoryName) {
        boolean flag=false;
        controller = new DatabaseHandler(getActivity());
        flag = controller.checkCategory(sCategoryName);

        return flag;
    }

    private void setValueCategory() {
        categories = new ArrayList<Category>();
        categories = controller.getCategoriesIncomeSetting();
        CategoryAdapter categoryAdapter = new CategoryAdapter(this.getActivity(), categories);

        listView.setAdapter(categoryAdapter);
    }

    @Override
    public String toString() {
        String title= "INCOME";
        return title;
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
}

