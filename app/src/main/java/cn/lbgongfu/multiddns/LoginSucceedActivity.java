package cn.lbgongfu.multiddns;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.MAPI;
import com.example.MDDNS;

import cn.lbgongfu.multiddns.utils.NotificationHelper;

public class LoginSucceedActivity extends AppCompatActivity {
    public static String CURRENT_USER_ID = null;
    private static String gsLocal_1 = "";
    private static String gsIPAddress = "";
    private TextView mTextId;
    private TextView mTextIp;
    private TextView mTextTime;

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
        mTextId.setText(CURRENT_USER_ID);
        mTextIp = (TextView) findViewById(R.id.text_ip);
        mTextTime = (TextView) findViewById(R.id.text_time);

        MAPI.MessageListener(new MAPI.MessageListener() {

            @Override
            public void putListener(int length, byte[] in_data) {
                final byte[] gpData = new byte[length];
                MAPI.COPY_BUFF8(length, 0, in_data, 0, gpData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (gpData[0]) {
                            case 'A':
                                gsLocal_1 = String.format("下一心跳包%d秒", gpData[1]);
                                break;
                            case 'B':
                                String gsLocal_2 = MAPI.IPADDR_STRING(MAPI.BUFF8_DWORD(1, gpData));
                                if (gsLocal_2 != null && !gsLocal_2.equals(gsIPAddress)) {
                                    gsIPAddress = gsLocal_2;
                                    gsLocal_1 = "IP地址更改为：" + gsLocal_2;
                                    NotificationHelper.showIPChanged(LoginSucceedActivity.this, gsIPAddress, getDefalutIntent(PendingIntent.FLAG_ONE_SHOT));
                                }
                                break;
                            default:
                                break;
                        }
                        mTextTime.setText(gsLocal_1);
                        mTextIp.setText(gsIPAddress);
                    }
                });
            }
        });
        MDDNS.SetHeartBeatStart(1);             //  1=启动DDNS心跳包，0=停止心跳包
    }

    public PendingIntent getDefalutIntent(int flags){
        Intent intent = new Intent(this, LoginSucceedActivity.class);
//        intent.putExtra(Constants.KEY_CURRENT_USER_ID, mFieldId.getText());
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 1, intent, flags);
        return pendingIntent;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.title_exit);
        builder.setMessage(R.string.msg_exit);
        builder.setNegativeButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoginSucceedActivity.super.onBackPressed();
            }
        });
        builder.setPositiveButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CURRENT_USER_ID = null;
                gsIPAddress = null;
                gsLocal_1 = null;
                MDDNS.SetHeartBeatStart(0);
                LoginSucceedActivity.super.onBackPressed();
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_quit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_quit)
        {
            // TODO: 2015/12/26
            MDDNS.SetHeartBeatStart(0);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
