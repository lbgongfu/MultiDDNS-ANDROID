package cn.lbgongfu.multiddns;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class LoginSucceedActivity extends AppCompatActivity {
    private TextView mTextId;
    private TextView mTextIp;
    private TextView mTextTime;
    private DDNSService.SimpleBinder simpleBinder;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null)
            {
                String action = intent.getAction();
                if (Constants.ACTION_SEND_DEBUG_TEXT.equals(action))
                {
                    mTextTime.setText(intent.getStringExtra(Constants.EXTRA_DEBUG_TEXT));
                }
                else if (Constants.ACTION_IP_CHANGED.equals(action))
                    mTextIp.setText(intent.getStringExtra(Constants.EXTRA_NEW_IP));
            }
        }
    };
    private ServiceConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_succeed);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        mTextId = (TextView) findViewById(R.id.text_id);
        mTextIp = (TextView) findViewById(R.id.text_ip);
        mTextTime = (TextView) findViewById(R.id.text_time);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_IP_CHANGED);
        filter.addAction(Constants.ACTION_SEND_DEBUG_TEXT);
        registerReceiver(receiver, filter);

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                simpleBinder = (DDNSService.SimpleBinder) service;
                simpleBinder.setSendDebugText(true);
                mTextId.setText(simpleBinder.getCurrUsername());
                mTextIp.setText(simpleBinder.getCurrIP());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(new Intent(this, DDNSService.class), connection, BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean runInBg = preferences.getBoolean("check_run_background", false);
        if (runInBg)
        {
            LoginSucceedActivity.super.onBackPressed();
        }
        else
        {
            DDNSService.stop(LoginSucceedActivity.this);
            LoginSucceedActivity.super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        unbindService(connection);
        if (simpleBinder != null)
            simpleBinder.setSendDebugText(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_settings)
        {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
