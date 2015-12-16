package cn.lbgongfu.multiddns;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class SelectGenderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_gender);

        Button mBtnMen = (Button) findViewById(R.id.btn_men);
        mBtnMen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(MgrRegisterStep3Activity.KEY_GENDER, getString(R.string.label_men));
                setResult(0, data);
                finish();
            }
        });

        Button mBtnWomen = (Button) findViewById(R.id.btn_women);
        mBtnWomen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra(MgrRegisterStep3Activity.KEY_GENDER, getString(R.string.label_women));
                setResult(0, data);
                finish();
            }
        });
    }

}
