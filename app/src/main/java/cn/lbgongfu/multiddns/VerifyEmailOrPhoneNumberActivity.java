package cn.lbgongfu.multiddns;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddns.sdk.MDDNS;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import cn.lbgongfu.multiddns.utils.AuthCodeReceiveWay;
import cn.lbgongfu.multiddns.utils.RegisterSymbol;

public class VerifyEmailOrPhoneNumberActivity extends AppCompatActivity {
    public static final String ACTION_REGISTER = "cn.lbgongfu.multiddns.action.register";
    public static final String ACTION_RETRIEVE_PASSWORD = "cn.lbgongfu.multiddns.actioin.retrieve_password";
    public static final String EXTRA_REGISTER_SYMBOL = "cn.lbgongfu.multiddns.register_symbol";
    public static final String EXTRA_REGISTER_SYMBOL_VALUE = "cn.lbgongfu.multiddns.register_symbol_value";
    public static final String EXTRA_AUTH_CODE_RECEIVE_WAY = "cn.lbgongfu.multiddns.auth_code_receive_way";
    private static final String TAG = VerifyEmailOrPhoneNumberActivity.class.getName();

    private TextView mTextPrompt;
    private EditText mFieldAuthCode;
    private EditText mFieldImgAuthCode;
    private ImageView mImgAuthCode;
    private Button mBtnResend;
    private View mProgressView;
    private View mLoginFormView;
    private View mImgAuthCodeView;

    private String action = ACTION_REGISTER;
    private RegisterSymbol symbol;
    private String symbolValue;
    private AuthCodeReceiveWay receiveWay;

    //验证码有效时间(秒)
    private static int effectiveMillis = 30;
    //计时器
    private int count = effectiveMillis;
    private Timer timer;
    private AsyncTask<Void, Void, Boolean> mFetchAuthCodeTask;
    private AsyncTask<Void, Void, Boolean> mVerifyAuthCodeTask;
    private AsyncTask<Void, Void, Drawable> mFetchImgAuthCodeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email_or_phone_number);

        mTextPrompt = (TextView) findViewById(R.id.text_prompt);
        mFieldAuthCode = (EditText) findViewById(R.id.field_auth_code);
        mFieldImgAuthCode = (EditText) findViewById(R.id.field_img_auth_code);
        mImgAuthCode = (ImageView) findViewById(R.id.img_auth_code);
        mImgAuthCodeView = findViewById(R.id.view_img_auth_code);

        mBtnResend = (Button) findViewById(R.id.btn_resend);
        mBtnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAuthCode();
            }
        });

        Button mBtnNextStep = (Button) findViewById(R.id.btn_next_step);
        mBtnNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptToNextStep();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        Intent intent = getIntent();
        action = intent.getAction();
        symbol = (RegisterSymbol) intent.getSerializableExtra(EXTRA_REGISTER_SYMBOL);
        symbolValue = intent.getStringExtra(EXTRA_REGISTER_SYMBOL_VALUE);
        receiveWay = (AuthCodeReceiveWay) intent.getSerializableExtra(EXTRA_AUTH_CODE_RECEIVE_WAY);

        switch (symbol)
        {
            case EMAIL:
                mTextPrompt.setText("我们已给你的邮箱" + symbolValue + "发送了验证码，请查看你的邮箱，然后将看到的验证码填入下面的输入框中");
                break;
            case PHONE:
                mTextPrompt.setText("我们已给你的手机" + symbolValue + "发送了验证短信，请查看你的短信息，然后将看到的验证码填入下面的输入框中");
                break;
            case KCID:
            case USERNAME:
                mTextPrompt.setText("我们已给你注册时填写的邮箱或手机发送了验证信息，请查看你的邮箱或者是短信息，然后将看到的验证码填入下面的输入框中");
                break;
        }
//        effectiveMillis = Integer.valueOf(MDDNS.GET_MS_VERIFY_VALID_MINUTE()) * 60;
        startTimer();
    }

    @Override
    protected void onDestroy() {
        if (timer != null)
            timer.cancel();
        super.onDestroy();
    }

    private void fetchImgAuthCode() {
        if (mFetchImgAuthCodeTask == null || mFetchImgAuthCodeTask.getStatus() == AsyncTask.Status.FINISHED)
        {
            showProgress(true);
            mFetchImgAuthCodeTask = new AsyncTask<Void, Void, Drawable>()
            {

                @Override
                protected Drawable doInBackground(Void... params) {
                    String filePath = MDDNS.READ_VERIFY_CODE_FILE();
                    if (!new File(filePath).exists())
                    {
                        Log.w(TAG, String.format("File(%s) does not exists.", filePath));
                        return null;
                    }
                    return BitmapDrawable.createFromPath(filePath);
                }

                @Override
                protected void onCancelled() {
                    showProgress(false);
                    mFetchImgAuthCodeTask = null;
                }

                @Override
                protected void onPostExecute(Drawable drawable) {
                    showProgress(false);
                    mFetchImgAuthCodeTask = null;
                    if (drawable != null)
                        mImgAuthCode.setImageDrawable(drawable);
                }
            };
        }
    }

    private void attemptToNextStep() {
        if (mVerifyAuthCodeTask == null || mVerifyAuthCodeTask.getStatus() == AsyncTask.Status.FINISHED)
        {
            final String authCode = mFieldAuthCode.getText().toString();
            mFieldAuthCode.setError(null);
            if (TextUtils.isEmpty(authCode))
            {
                mFieldAuthCode.setError(getString(R.string.error_field_required));
                mFieldAuthCode.requestFocus();
                return;
            }
            showProgress(true);

            mVerifyAuthCodeTask = new AsyncTask<Void, Void, Boolean>()
            {
                private String result;

                @Override
                protected Boolean doInBackground(Void... params) {
                    Log.d(TAG, String.format("Call method(MDDNS.CHECK_MS_VERIFY_CODE) with parameters(authCode=%s)", authCode));
                    result = MDDNS.CHECK_MS_VERIFY_CODE(authCode);
                    Log.d(TAG, String.format("Call method(MDDNS.CHECK_MS_VERIFY_CODE) done and return %s", result));
                    return "ok".equals(result);
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    showProgress(false);
                    mVerifyAuthCodeTask = null;
                    if (success)
                    {
                        if (ACTION_REGISTER.equals(action))
                            startActivity(new Intent(VerifyEmailOrPhoneNumberActivity.this, InputDetailActivity.class));
                        else if (ACTION_RETRIEVE_PASSWORD.equals(action))
                            startActivity(new Intent(VerifyEmailOrPhoneNumberActivity.this, MgrResetPasswordActivity.class));
                        else
                            Log.e(TAG, String.format("no action (%s) defined.", action));
                        finish();
                    }
                    else
                        Toast.makeText(VerifyEmailOrPhoneNumberActivity.this, result, Toast.LENGTH_LONG).show();
                }

                @Override
                protected void onCancelled() {
                    showProgress(false);
                    mVerifyAuthCodeTask = null;
                }
            }.execute((Void[]) null);
        }
    }

    public static void go(Context context, String action, RegisterSymbol registerSymbol, String registerSymbolValue, AuthCodeReceiveWay receiveWay) {
        Intent intent = new Intent(context, VerifyEmailOrPhoneNumberActivity.class);
        intent.setAction(action);
        intent.putExtra(EXTRA_REGISTER_SYMBOL_VALUE, registerSymbolValue);
        intent.putExtra(EXTRA_REGISTER_SYMBOL, registerSymbol);
        intent.putExtra(EXTRA_AUTH_CODE_RECEIVE_WAY, receiveWay);
        context.startActivity(intent);
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

    private void fetchAuthCode() {
        if (mFetchAuthCodeTask == null || mFetchAuthCodeTask.getStatus() == AsyncTask.Status.FINISHED)
        {
            mBtnResend.setEnabled(false);
            final String imgAuthCode = mFieldImgAuthCode.getText().toString();
            mFieldAuthCode.setError(null);
            if (TextUtils.isEmpty(imgAuthCode))
            {
                mImgAuthCodeView.setVisibility(View.VISIBLE);
                mFieldAuthCode.setError(getString(R.string.error_field_required));
                mFieldAuthCode.requestFocus();
                fetchImgAuthCode();
                return;
            }
            mFetchAuthCodeTask = new AsyncTask<Void, Void, Boolean>()
            {
                private String result = "";

                @Override
                protected Boolean doInBackground(Void... params) {
                    if (action == ACTION_REGISTER)
                        result = MDDNS.MAKE_REGISTER_MAIL_SMS_VERIFY(symbolValue, imgAuthCode);
                    else if (ACTION_RETRIEVE_PASSWORD.equals(action))
                        result = MDDNS.MAKE_FORGET_MAIL_SMS_VERIFY(symbol.ordinal(), receiveWay.ordinal(), symbolValue, imgAuthCode);
                    else
                        Log.e(TAG, String.format("no action (%s) defined.", action));
                    return "ok".equals(result);
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    mBtnResend.setEnabled(true);
                    mFetchAuthCodeTask = null;
                    if (success)
                        startTimer();
                }

                @Override
                protected void onCancelled() {
                    mFetchAuthCodeTask = null;
                    mBtnResend.setEnabled(true);
                }
            };
        }
    }

    private void startTimer() {
        mBtnResend.setEnabled(false);
        if (timer == null)
        {
            timer = new Timer();
            count = effectiveMillis;
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    count--;
                    if (count < 0)
                    {
                        count = 0;
                        return;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (count == 0)
                            {
                                mBtnResend.setEnabled(true);
                                mBtnResend.setText(getString(R.string.action_resend));
                                timer.cancel();
                                timer = null;
                            }
                            else
                                mBtnResend.setText(getString(R.string.action_resend) + "(" + count + "秒)");
                        }
                    });
                }
            }, 0, 1000);
        }
    }
}
