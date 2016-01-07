package cn.lbgongfu.multiddns;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ddns.sdk.MDDNS;

import java.io.File;

import cn.lbgongfu.multiddns.utils.AuthCodeReceiveWay;
import cn.lbgongfu.multiddns.utils.FetchImgAuthCodeTask;
import cn.lbgongfu.multiddns.utils.RegisterSymbol;
import cn.lbgongfu.multiddns.utils.VerifyUtil;

/**
 * A login screen that offers login via email/password.
 */
public class InputOldEmailOrPhoneNumberActivity extends AppCompatActivity {

    private static final String TAG = InputOldEmailOrPhoneNumberActivity.class.getName();
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private RadioGroup mRadioGroupSymbols;
    private RadioGroup mRadioGroupReceiveWays;
    private EditText mFieldId;
    private EditText mFieldAuthCode;
    private ImageView mImgAuthCode;
    private View mProgressView;
    private View mLoginFormView;
    private TextInputLayout mLayoutId;

    private RegisterSymbol registerSymbol = RegisterSymbol.EMAIL;
    private AuthCodeReceiveWay receiveWay = AuthCodeReceiveWay.RECEIVE_BY_EMAIL;
    private FetchImgAuthCodeTask mFetchImgAuthCodeTask;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_old_email_or_phone_number);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        registerSymbol = RegisterSymbol.valueOf(preferences.getString(getString(R.string.key_register_symbol), RegisterSymbol.EMAIL.toString()));
        receiveWay = AuthCodeReceiveWay.valueOf(preferences.getString(getString(R.string.key_receive_way), AuthCodeReceiveWay.RECEIVE_BY_EMAIL.toString()));

        // Set up the login form.
        mRadioGroupSymbols = (RadioGroup) findViewById(R.id.radio_group_symbols);
        mRadioGroupSymbols.check(registerSymbol == RegisterSymbol.EMAIL ? R.id.radio_email
                : registerSymbol == RegisterSymbol.PHONE ? R.id.radio_phone
                : registerSymbol == RegisterSymbol.KCID ? R.id.radio_kcid
                : registerSymbol == RegisterSymbol.USERNAME ? R.id.radio_username
                : R.id.radio_email);
        mRadioGroupSymbols.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_email:
                        registerSymbol = RegisterSymbol.EMAIL;
                        mLayoutId.setHint(getString(R.string.prompt_email));
                        break;
                    case R.id.radio_phone:
                        registerSymbol = RegisterSymbol.PHONE;
                        mLayoutId.setHint(getString(R.string.prompt_phone));
                        break;
                    case R.id.radio_kcid:
                        registerSymbol = RegisterSymbol.KCID;
                        mLayoutId.setHint(getString(R.string.prompt_kcid));
                        break;
                    case R.id.radio_username:
                        registerSymbol = RegisterSymbol.USERNAME;
                        mLayoutId.setHint(getString(R.string.prompt_username_short));
                        break;
                }
            }
        });
        mRadioGroupReceiveWays = (RadioGroup) findViewById(R.id.radio_group_receive_ways);
        mRadioGroupReceiveWays.check(receiveWay == AuthCodeReceiveWay.RECEIVE_BY_EMAIL ? R.id.radio_receive_by_email
                : receiveWay == AuthCodeReceiveWay.RECEIVE_BY_PHONE ? R.id.radio_receive_by_phone
                : receiveWay == AuthCodeReceiveWay.RECEIVE_BY_EMAIL_AND_PHONE ? R.id.radio_receive_by_email_and_phone
                : R.id.radio_receive_by_email);
        mRadioGroupReceiveWays.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                receiveWay = checkedId == R.id.radio_receive_by_email ? AuthCodeReceiveWay.RECEIVE_BY_EMAIL
                        : checkedId == R.id.radio_receive_by_phone ? AuthCodeReceiveWay.RECEIVE_BY_PHONE
                        : checkedId == R.id.radio_receive_by_email_and_phone ? AuthCodeReceiveWay.RECEIVE_BY_EMAIL_AND_PHONE
                        : AuthCodeReceiveWay.RECEIVE_BY_EMAIL;
            }
        });
        mFieldId = (EditText) findViewById(R.id.field_id);
        mFieldId.setText(preferences.getString(getString(R.string.key_id), ""));
        mLayoutId = (TextInputLayout) findViewById(R.id.text_input_layout_id);
        mFieldAuthCode = (EditText) findViewById(R.id.field_auth_code);
        mFieldAuthCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.next_step || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        Button mBtnGetAuthCode = (Button) findViewById(R.id.btn_get_auth_code);
        mBtnGetAuthCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchImgAuthCode();
            }
        });

        Button mBtnNextStep = (Button) findViewById(R.id.btn_next_step);
        mBtnNextStep.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mImgAuthCode = (ImageView) findViewById(R.id.img_auth_code);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        fetchImgAuthCode();
    }

    private void fetchImgAuthCode() {
        if (mFetchImgAuthCodeTask == null || mFetchImgAuthCodeTask.getStatus() == AsyncTask.Status.FINISHED) {
            showProgress(true);
            mFetchImgAuthCodeTask = new FetchImgAuthCodeTask(this, mImgAuthCode) {
                @Override
                protected void onCancelled() {
                    super.onCancelled();
                    mFetchImgAuthCodeTask = null;
                    showProgress(false);
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    super.onPostExecute(success);
                    mFetchImgAuthCodeTask = null;
                    showProgress(false);
                }
            };
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mFieldId.setError(null);
        mFieldAuthCode.setError(null);

        // Store values at the time of the login attempt.
        String id = mFieldId.getText().toString();
        String authCode = mFieldAuthCode.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(authCode))
        {
            mFieldAuthCode.setError(getString(R.string.error_field_required));
            focusView = mFieldAuthCode;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(id))
        {
            mFieldId.setError(getString(R.string.error_field_required));
            focusView = mFieldId;
            cancel = true;
        }
        else if (registerSymbol == RegisterSymbol.EMAIL && !VerifyUtil.isEmailValid(id))
        {
            mFieldId.setError(getString(R.string.error_invalid_email));
            focusView = mFieldId;
            cancel = true;
        }
        else if (registerSymbol == RegisterSymbol.PHONE && !VerifyUtil.isPhoneNumberValid(id))
        {
            mFieldId.setError(getString(R.string.error_invalid_phone_number));
            focusView = mFieldId;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(id, authCode);
            mAuthTask.execute((Void) null);
        }
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
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mId;
        private final String mAuthCode;
        private String result = "";

        UserLoginTask(String id, String authCode) {
            mId = id;
            mAuthCode = authCode;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, String.format("Call method(MDDNS.MAKE_FORGET_MAIL_SMS_VERIFY) with parameters (type=%s, smsMail=%s, account=%s, authCode=%s)",
                    registerSymbol.toString(), receiveWay.toString(), mId, mAuthCode));
            result = MDDNS.MAKE_FORGET_MAIL_SMS_VERIFY(registerSymbol.ordinal(), receiveWay.ordinal(), mId, mAuthCode);
            Log.d(TAG, String.format("method(MDDNS.MAKE_FORGET_MAIL_SMS_VERIFY) return %s", result));
            return "ok".equals(result);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.key_id), mId);
                editor.putString(getString(R.string.key_register_symbol), registerSymbol.toString());
                editor.putString(getString(R.string.key_receive_way), receiveWay.toString());
                editor.commit();
                VerifyEmailOrPhoneNumberActivity.go(InputOldEmailOrPhoneNumberActivity.this, VerifyEmailOrPhoneNumberActivity.ACTION_RETRIEVE_PASSWORD,
                        registerSymbol, mId, receiveWay);
                finish();
            } else {
                mFieldAuthCode.requestFocus();
                Toast.makeText(InputOldEmailOrPhoneNumberActivity.this, result, Toast.LENGTH_LONG).show();
                fetchImgAuthCode();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

