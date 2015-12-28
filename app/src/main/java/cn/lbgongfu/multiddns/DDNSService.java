package cn.lbgongfu.multiddns;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.example.MAPI;
import com.example.MDDNS;

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
        MDDNS.SetHeartBeatStart(0);
        super.onDestroy();
    }

    private void MSYS_INITIALIZE(String projectName) {
        String gsLocal_1 = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == true) {   // 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
            gsLocal_1 = Environment.getExternalStorageDirectory().getAbsolutePath();
            gsLocal_1 += "/" + projectName + "/";
            File file = new File(gsLocal_1);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            gsLocal_1 = getApplicationContext().getFilesDir() + "/";
        }
        MAPI.INITIALIZE(gsLocal_1, "114.215.194.168", 1999);
        MDDNS.INITIALIZE(gsLocal_1, android.os.Build.MODEL);

        if (MAPI.RD_MEM_STRING("memory") == null){
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
        MAPI.MessageListener(new MAPI.MessageListener() {
            @Override
            public void putListener(int length, byte[] in_data) {
                final int g4Length = length;
                final byte[] gpData = new byte[length];
                MAPI.COPY_BUFF8(length, 0, in_data, 0, gpData);
                switch (gpData[0]) {
                    case 'A':
                        gsDebugText = String.format("下一心跳包%d秒", gpData[1]);
                        sendDebugText();
                        break;
                    case 'B':
                        String gsLocal_2 = MAPI.IPADDR_STRING(MAPI.BUFF8_DWORD(1, gpData));
                        if (gsLocal_2 != null && !gsLocal_2.equals(simpleBinder.getCurrIP())) {
                            simpleBinder.setCurrIP(gsLocal_2);
                            sendIPChanged(gsLocal_2);
                            NotificationHelper.showIPChanged(DDNSService.this, gsLocal_2);
                            gsDebugText = "IP地址更改为：" + gsLocal_2;
                            sendDebugText();
                        }
                        break;
                    default:
                        MSYS_TEXT_DEBUG(g4Length, gpData);
                        break;
                }
            }
        });
        MDDNS.SetHeartBeatStart(1);
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

    private void MSYS_TEXT_DEBUG(int length, byte[] in_data){
        String gsLocal_1 = null;
        switch (in_data[0]) {
            case 'd':
                gsLocal_1 = String.format("%02X ", in_data[1]);
                break;
            case 'w':
                gsLocal_1 = String.format("%08X ", MAPI.BUFF8_DWORD(1, in_data));
                break;
            case 's':
                gsLocal_1 = String.format("%08X ", MAPI.BUFF8_DWORD(1, in_data));
                int g4Local_1 = (length - 1) / 2;
                char[] gpLocal_2 = new char[g4Local_1];
                int g4Local_2 = 0;
                int g4Local_3 = 1;
                do {
                    gpLocal_2[g4Local_2] = (char) ((in_data[g4Local_3 + 1] << 8) | in_data[g4Local_3]);
                    g4Local_3 += 2;
                } while (++g4Local_2 < g4Local_1);
                gsLocal_1 = new String(gpLocal_2);
                break;
            default:
                gsLocal_1 = null;
                break;
        }
        if (gsLocal_1 != null) {
            if (gsDebugText != null) {
                gsDebugText += gsLocal_1;
            } else {
                gsDebugText = gsLocal_1;
            }
        }
        Log.d(DDNSService.class.getName(), gsDebugText);
    }
}
