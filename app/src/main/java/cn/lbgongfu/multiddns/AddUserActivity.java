package cn.lbgongfu.multiddns;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

public class AddUserActivity extends AppCompatActivity {
    private EditText mFieldUsername;
    private EditText mFieldPassword;
    private EditText mFieldDomain;
    private EditText mFieldAuthCode;
    private EditText mFieldParseSpan;
    private EditText mFieldUrl;
    private CheckBox mCheckDomainTurn;
    private ImageView mImgAuthCode;

    private TextInputLayout mLayoutUrl;
    private TextInputLayout mLayoutParseSpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        mFieldUsername = (EditText) findViewById(R.id.field_username);
        mFieldPassword = (EditText) findViewById(R.id.field_password);

        mFieldDomain = (EditText) findViewById(R.id.field_domain);
        mFieldDomain.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    startActivityForResult(new Intent(AddUserActivity.this, SelectDomainActivity.class), Constants.REQUEST_CODE_SELECT_DOMAIN);
            }
        });
        mFieldDomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddUserActivity.this, SelectDomainActivity.class), Constants.REQUEST_CODE_SELECT_DOMAIN);
            }
        });

        mFieldAuthCode = (EditText) findViewById(R.id.field_auth_code);

        mFieldParseSpan = (EditText) findViewById(R.id.field_parse_span);
        mFieldParseSpan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    startActivityForResult(new Intent(AddUserActivity.this, SelectParseSpanActivity.class), Constants.REQUEST_CODE_SELECT_PARSE_SPAN);
            }
        });
        mFieldParseSpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(AddUserActivity.this, SelectParseSpanActivity.class), Constants.REQUEST_CODE_SELECT_PARSE_SPAN);
            }
        });

        mLayoutUrl = (TextInputLayout) findViewById(R.id.layout_url);
        mLayoutParseSpan = (TextInputLayout) findViewById(R.id.layout_parse_span);
        mFieldUrl = (EditText) findViewById(R.id.field_url);

        mCheckDomainTurn = (CheckBox) findViewById(R.id.check_domain_turn);
        mCheckDomainTurn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    mLayoutUrl.setVisibility(View.VISIBLE);
                    mLayoutParseSpan.setVisibility(View.GONE);
                }
                else
                {
                    mLayoutUrl.setVisibility(View.GONE);
                    mLayoutParseSpan.setVisibility(View.VISIBLE);
                }
            }
        });

        mImgAuthCode = (ImageView) findViewById(R.id.img_auth_code);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_complete)
        {
            // TODO: 2015/12/14
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Constants.REQUEST_CODE_SELECT_DOMAIN == requestCode)
        {
            String domain = data.getStringExtra(Constants.KEY_SELECTED_DOMAIN);
            mFieldDomain.setText(domain);
        }
        else if (Constants.REQUEST_CODE_SELECT_PARSE_SPAN == requestCode)
        {
            int span = data.getIntExtra(Constants.KEY_SELECTED_PARSE_SPAN, 5);
            mFieldParseSpan.setText(span + getString(R.string.label_seconds));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
