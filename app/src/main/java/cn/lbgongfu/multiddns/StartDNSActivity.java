package cn.lbgongfu.multiddns;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ddns.sdk.MDDNS;

import java.util.Timer;
import java.util.TimerTask;

import cn.lbgongfu.multiddns.utils.NotificationHelper;

/**
 * A login screen that offers login via email/password.
 */
public class StartDNSActivity extends AppCompatActivity {

    private static final String TAG = StartDNSActivity.class.getName();
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private StartDNSTask mAuthTask = null;

    // UI references.
    private EditText mFieldId;
    private EditText mFieldPassword;
    private TextView mTextIP;
    private TextView mTextMsg;
    private Button mBtnStartDNS;
    private View mProgressView;
    private View mLoginFormView;
    private View mBillboard;

    private SharedPreferences preferences;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null)
            {
                String action = intent.getAction();
                if (Constants.ACTION_SEND_DEBUG_TEXT.equals(action))
                {
                    mTextMsg.setText(intent.getStringExtra(Constants.EXTRA_DEBUG_TEXT));
                }
                else if (Constants.ACTION_IP_CHANGED.equals(action))
                    mTextIP.setText(intent.getStringExtra(Constants.EXTRA_NEW_IP));
            }
        }
    };
    private DDNSService.SimpleBinder simpleBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            simpleBinder = (DDNSService.SimpleBinder) service;
            if (simpleBinder.isLogin())
            {
                simpleBinder.setSendDebugText(true);
                mTextIP.setText(simpleBinder.getCurrIP());
                mBillboard.setVisibility(View.VISIBLE);
            }
            else
            {
                if (preferences.getBoolean(getString(R.string.key_auto_login), false))
                    attemptStartDNS();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private int backPressedCount;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_dns);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean rememberMe = preferences.getBoolean(getString(R.string.key_remember_password), false);

        mFieldId = (EditText) findViewById(R.id.field_id);
        mFieldId.setText(preferences.getString(getString(R.string.key_user_id), ""));
        mFieldPassword = (EditText) findViewById(R.id.field_password);
        mTextIP = (TextView) findViewById(R.id.text_ip);
        mTextMsg = (TextView) findViewById(R.id.text_msg);
        if (rememberMe)
            mFieldPassword.setText(preferences.getString(getString(R.string.key_user_password), ""));
        mBillboard = findViewById(R.id.layout_msg);

        mBtnStartDNS = (Button) findViewById(R.id.btn_start_dns);
        mBtnStartDNS.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptStartDNS();
            }
        });

        Button mBtnMgrEntry = (Button) findViewById(R.id.btn_mgr_entry);
        mBtnMgrEntry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartDNSActivity.this, MgrLoginActivity.class));
//                DDNSService.stop(LoginActivity.this);
//                finish();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        DDNSService.startActionHeartbeat(this, null);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_SEND_DEBUG_TEXT);
        filter.addAction(Constants.ACTION_IP_CHANGED);
        registerReceiver(receiver, filter);
        bindService(new Intent(this, DDNSService.class), connection, BIND_AUTO_CREATE);
    }

    @Override
    public void onBackPressed() {
        backPressedCount++;
        if (backPressedCount == 2)
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            boolean runInBg = preferences.getBoolean(getString(R.string.key_run_in_background), false);
            if (runInBg)
            {
                StartDNSActivity.super.onBackPressed();
            }
            else
            {
                DDNSService.stop(this);
                StartDNSActivity.super.onBackPressed();
                NotificationHelper.clearIPChanged();
            }
        }
        else
        {
            Toast.makeText(this, getString(R.string.tip_back_pressed), Toast.LENGTH_SHORT).show();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    backPressedCount = 0;
                }
            }, 3000);
        }
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        unregisterReceiver(receiver);
        if (simpleBinder != null)
            simpleBinder.setSendDebugText(false);
        if (timer != null)
            timer.cancel();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptStartDNS() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mFieldId.setError(null);
        mFieldPassword.setError(null);

        // Store values at the time of the login attempt.
        String id = mFieldId.getText().toString();
        String password = mFieldPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password))
        {
            mFieldPassword.setError(getString(R.string.error_field_required));
            focusView = mFieldPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(id)) {
            mFieldId.setError(getString(R.string.error_field_required));
            focusView = mFieldId;
            cancel = true;
        } else if (!isIdValid(id)) {
            mFieldId.setError(getString(R.string.error_invalid_id));
            focusView = mFieldId;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new StartDNSTask(id, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isIdValid(String email) {
        //TODO: Replace this with your own logic
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class StartDNSTask extends AsyncTask<Void, Void, Boolean> {

        private final String mId;
        private final String mPassword;
        private String mErrorMsg;

        StartDNSTask(String id, String password) {
            mId = id;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (simpleBinder != null)
                simpleBinder.setLogin(false);
            String result = MDDNS.DOMAIN_NAME_ANALYZE(mId, mPassword);
            if ("ok".equals(result)) {
                if (simpleBinder != null)
                    simpleBinder.setLogin(true);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.key_user_id), mId);
                editor.putString(getString(R.string.key_user_password), mPassword);
                editor.commit();
                return true;
            }
            else
            {
                mErrorMsg = result;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                DDNSService.startActionHeartbeat(StartDNSActivity.this, mId);
                unbindService(connection);
                bindService(new Intent(StartDNSActivity.this, DDNSService.class), connection, BIND_AUTO_CREATE);
//                gotoSucceed();
//                finish();
            } else {
                mTextIP.setText("");
                mTextMsg.setText("");
                mBillboard.setVisibility(View.GONE);
                if (simpleBinder != null) simpleBinder.clear();
                Toast.makeText(StartDNSActivity.this, getString(R.string.error_login) + "\n" + mErrorMsg, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    /**
     * @deprecated
     */
    private void gotoSucceed() {
        Intent intent = new Intent(StartDNSActivity.this, LoginSucceedActivity.class);
        startActivity(intent);
    }
}

