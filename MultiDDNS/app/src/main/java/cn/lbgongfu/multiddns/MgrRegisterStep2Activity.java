package cn.lbgongfu.multiddns;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MgrRegisterStep2Activity extends AppCompatActivity {
    private EditText mFieldAuthCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mgr_register_step2);

        mFieldAuthCode = (EditText) findViewById(R.id.field_auth_code);

        Button mBtnResend = (Button) findViewById(R.id.btn_resend);
        mBtnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });

        Button mBtnNextStep = (Button) findViewById(R.id.btn_next_step);
        mBtnNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //TODO
            if (MgrRegisterStep1Activity.register)
                startActivity(new Intent(MgrRegisterStep2Activity.this, MgrRegisterStep3Activity.class));
            else
                startActivity(new Intent(MgrRegisterStep2Activity.this, MgrResetPasswordActivity.class));
            finish();
            }
        });
    }
}
