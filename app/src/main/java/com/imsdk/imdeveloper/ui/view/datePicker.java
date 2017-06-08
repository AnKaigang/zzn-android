package com.imsdk.imdeveloper.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.activity.AddClubList;

import java.util.Calendar;

/**
 * Created by Administrator on 2015/12/19.
 */
public class datePicker extends AlertDialog{

    AddClubList activity = null;
    LinearLayout dateTimeLayout;
    DatePicker datePic;

    public datePicker(Context context) {
        super(context);
        activity= (AddClubList) context;
        dateTimeLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.date_picker, null);
        datePic= (DatePicker) dateTimeLayout.findViewById(R.id.datepicker);
        Calendar calendar = Calendar.getInstance();
        datePic.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        activity.mDateEdit.setText(year + monthOfYear + dayOfMonth);
                    }
                });
    }

    protected datePicker(Context context, int theme) {
        super(context, theme);
    }

    protected datePicker(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_picker);
    }
}
