package com.imsdk.imdeveloper.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.imsdk.imdeveloper.R;
import com.imsdk.imdeveloper.ui.view.sortlistview.ClearEditText;

public class AddClubActivity extends Activity {

    ClearEditText text;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_club);

        text = (ClearEditText)findViewById(R.id.addclub_edittext);
        button = (Button)findViewById(R.id.add_club_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

        ((ImageView) findViewById(R.id.titlebar_logo))
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                         finish();
                    }
                });
    }

    public void add()
    {
//        ClubInformationDao clubInformationDao=new ClubInformationDao(IMMyself.getCustomUserID());
//        JSONObject jsonObject=clubInformationDao.query(text.getText().toString());
//        if()
//        IdNameDao idNameDao=new IdNameDao(IMMyself.getCustomUserID());
    }
}
