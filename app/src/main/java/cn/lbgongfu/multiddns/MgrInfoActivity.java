package cn.lbgongfu.multiddns;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ddns.sdk.MDDNS;

import cn.lbgongfu.multiddns.models.Manager;

public class MgrInfoActivity extends AppCompatActivity {
    private TextView mTextKCID;
    private TextView mTextKB;
    private TextView mTextNickname;
    private TextView mTextEmail;
    private TextView mTextPhoneNumber;

    private AsyncTask mFetchMgrInfoTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mgr_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MgrInfoActivity.this, EditMgrDetailActivity.class));
            }
        });

        mTextKCID = (TextView) findViewById(R.id.text_kcid);
        mTextKB = (TextView) findViewById(R.id.text_kb);
        mTextNickname = (TextView) findViewById(R.id.text_nickname);
        mTextEmail = (TextView) findViewById(R.id.text_email);
        mTextPhoneNumber = (TextView) findViewById(R.id.text_phone_number);
        updateMgrInfo();
    }

    private void updateMgrInfo() {
        if (mFetchMgrInfoTask == null || mFetchMgrInfoTask.getStatus() == AsyncTask.Status.FINISHED)
        {
            mFetchMgrInfoTask = new AsyncTask<Void, Void, Manager>()
            {
                @Override
                protected Manager doInBackground(Void... params) {
                    Manager manager = new Manager();
                    manager.setKcid(MDDNS.GET_MANAGE_INFO(0));
                    manager.setKb(MDDNS.GET_MANAGE_INFO(1));
                    manager.setNickname(MDDNS.GET_MANAGE_INFO(2));
                    manager.setEmail(MDDNS.GET_MANAGE_INFO(3));
                    manager.setPhoneNumber(MDDNS.GET_MANAGE_INFO(4));
                    return manager;
                }

                @Override
                protected void onPostExecute(Manager manager) {
                    if (manager != null)
                    {
                        mTextKCID.setText(manager.getKcid());
                        mTextKB.setText(manager.getKb());
                        mTextNickname.setText(manager.getNickname());
                        mTextEmail.setText(manager.getEmail());
                        mTextPhoneNumber.setText(manager.getPhoneNumber());
                    }
                }

                @Override
                protected void onCancelled() {
                    mFetchMgrInfoTask = null;
                }
            }.execute((Void)null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mgr_info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.item_update_password:
                startActivity(new Intent(this, MgrUpdatePasswordActivity.class));
                break;
            case R.id.item_recharge:
                startActivity(new Intent(this, RechargeActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
