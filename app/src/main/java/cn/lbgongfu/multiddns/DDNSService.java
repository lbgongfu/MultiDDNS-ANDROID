package cn.lbgongfu.multiddns;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.ddns.sdk.MAPI;
import com.ddns.sdk.MDDNS;

import java.io.File;

import cn.lbgongfu.multiddns.utils.NotificationHelper;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DDNSService extends Service {
    private static final String TAG = DDNSService.class.getName();

    public class SimpleBinder extends Binder
    {
        private String currUsername;
        private String currIP;
        private boolean login;
        private boolean sendDebugText;

        public String getCurrUsername() {
            return currUsername;
        }

        public void setCurrUsername(String currUsername) {
            this.currUsername = currUsername;
        }

        public String getCurrIP() {
            return currIP;
        }

        public void setCurrIP(String currIP) {
            this.currIP = currIP;
        }

        public boolean isLogin()
        {
            return login;
        }

        public void setLogin(boolean isLogin)
        {
            login = isLogin;
        }

        public boolean isSendDebugText()
        {
            return sendDebugText;
        }

        public void setSendDebugText(boolean isSendDebugText)
        {
            sendDebugText = isSendDebugText;
        }
    }
    private static final String ACTION_HEARTBEAT = "cn.lbgongfu.multiddns.action.heartbeat";
    private static final String EXTRA_USERNAME = "cn.lbgongfu.multiddns.extra.username";

    private String gsDebugText = null;
    private SimpleBinder simpleBinder;

    public DDNSService() {

    }

    public static void startActionHeartbeat(Context context, String username)
    {
        if (TextUtils.isEmpty(username))
            context.startService(new Intent(context, DDNSService.class));
        else
        {
            Intent intent = new Intent(context, DDNSService.class);
            intent.setAction(ACTION_HEARTBEAT);
            intent.putExtra(EXTRA_USERNAME, username);
            context.startService(intent);
        }
        Log.d(DDNSService.class.getName(), "服务已启动");
    }

    public static void stop(Context context)
    {
        context.stopService(new Intent(context, DDNSService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        simpleBinder = new SimpleBinder();
        MSYS_INITIALIZE("MultiDDNS");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return simpleBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_HEARTBEAT.equals(action))
            {
                final String username = intent.getStringExtra(EXTRA_USERNAME);
                handleActionHeartbeat(username);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        MDDNS.SYSTEM_CLOSE();
        super.onDestroy();
        Log.d(TAG, "服务已终止");
    }

    private void MSYS_INITIALIZE(String projectName) {
        String gsLocal_1 = null;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == true) {   // �ж�SDCard�Ƿ���� [��û�����SD��ʱ������ROMҲ��ʶ��Ϊ����sd��]
            gsLocal_1 = Environment.getExternalStorageDirectory().getAbsolutePath();
            gsLocal_1 += "/" + projectName + "/";
            File file = new File(gsLocal_1);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            gsLocal_1 = getApplicationContext().getFilesDir() + "/";
        }
        MDDNS.INITIALIZE(1999, gsLocal_1, android.os.Build.MODEL, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

//MAPI.STRING(gsLocal_1);MAPI.STRING(gsLocal_2);

//MAPI.WR_MEM_STRING("memory", "0");
        if (MAPI.RD_MEM_STRING("memory") != "1"){
            MAPI.WR_MEM_STRING("memory", "1");

            MAPI.WR_MEM_STRING("userName", "1111.ip71.cn");
            MAPI.WR_MEM_STRING("userPSW", "aaaa");
            MAPI.WR_MEM_STRING("manageName", "13423054003@163.com");
            MAPI.WR_MEM_STRING("managePSW", "c27916786");
        }
    }

    private void handleActionHeartbeat(String username) {
        simpleBinder.setCurrUsername(username);
        simpleBinder.setLogin(true);
        MDDNS.MessageListener(new MDDNS.MessageListener() {
            @Override
            public void putListener(int type, String gsLocal_1, String gsLocal_2) {
                switch (type) {
                    case 'A':
                        gsDebugText = "下一次域名解析在" + gsLocal_1 + "秒后";
                        sendDebugText();
                        break;
                    case 'B':
                        if (gsLocal_1 != null && !gsLocal_1.equals(simpleBinder.getCurrIP())) {
                            simpleBinder.setCurrIP(gsLocal_1);
                            sendIPChanged(gsLocal_1);
                            NotificationHelper.showIPChanged(DDNSService.this, gsLocal_1);
                            gsDebugText = "IP地址更改为：" + gsLocal_1;
                            sendDebugText();
                        }
                        break;
                }
            }
        });
        MDDNS.ENABLE_ANALYZE(1);
    }

    private void sendIPChanged(String newIP) {
        Intent intent = new Intent();
        intent.setAction(Constants.ACTION_IP_CHANGED);
        intent.putExtra(Constants.EXTRA_NEW_IP, newIP);
        sendBroadcast(intent);
    }

    private void sendDebugText() {
        if (simpleBinder.isSendDebugText())
        {
            Intent intent = new Intent();
            intent.setAction(Constants.ACTION_SEND_DEBUG_TEXT);
            intent.putExtra(Constants.EXTRA_DEBUG_TEXT, gsDebugText);
            sendBroadcast(intent);
        }
    }
}
