package cn.lbgongfu.multiddns;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class MgrRegisterStep3Activity extends AppCompatActivity {
    public static final String KEY_GENDER = "gender";
    private EditText mFieldGender;
    private EditText mFieldPassword;
    private EditText mFieldPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mgr_register_step3);

        mFieldGender = (EditText) findViewById(R.id.field_gender);
        mFieldGender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    startActivityForResult(new Intent(MgrRegisterStep3Activity.this, SelectGenderActivity.class), 0);
            }
        });
        mFieldGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MgrRegisterStep3Activity.this, SelectGenderActivity.class), 0);
            }
        });
        mFieldPassword = (EditText) findViewById(R.id.field_password);
        mFieldPassword2 = (EditText) findViewById(R.id.field_password_copy);

        Button mBtnComplete = (Button) findViewById(R.id.btn_complete);
        mBtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                startActivity(new Intent(MgrRegisterStep3Activity.this, MgrLoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case 0:
                mFieldGender.setText(data.getStringExtra(KEY_GENDER));
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
