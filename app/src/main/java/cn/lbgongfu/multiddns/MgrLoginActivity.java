package cn.lbgongfu.multiddns;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddns.sdk.MDDNS;

import cn.lbgongfu.multiddns.utils.FetchImgAuthCodeTask;

/**
 * A login screen that offers login via email/password.
 */
public class MgrLoginActivity extends AppCompatActivity {
    private static final String TAG = MgrLoginActivity.class.getName();
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private FetchImgAuthCodeTask mFetchImgAuthCodeTask = null;

    // UI references.
    private EditText mFieldId;
    private EditText mFieldPassword;
    private EditText mFieldAuthCode;
    private ImageView mImgAuthCode;
    private View mProgressView;
    private View mLoginFormView;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mgr_login);
        setupActionBar();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set up the login form.
        mFieldId = (EditText) findViewById(R.id.field_id);
        mFieldId.setText(preferences.getString(getString(R.string.key_id), ""));
        mFieldPassword = (EditText) findViewById(R.id.field_password);
        if (preferences.getBoolean(getString(R.string.key_remember_password), false))
            mFieldPassword.setText(preferences.getString(getString(R.string.key_mgr_password), ""));
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

        Button mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button mBtnRegister = (Button) findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MgrLoginActivity.this, InputEmailOrPhoneNumberActivity.class));
                finish();
            }
        });

        Button mBtnForgetPassword = (Button) findViewById(R.id.btn_forget_password);
        mBtnForgetPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MgrLoginActivity.this, InputOldEmailOrPhoneNumberActivity.class));
                finish();
            }
        });

        mImgAuthCode = (ImageView) findViewById(R.id.img_auth_code);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        fetchImgAuthCode();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void fetchImgAuthCode() {
        if (mFetchImgAuthCodeTask == null || mFetchImgAuthCodeTask.getStatus() == AsyncTask.Status.FINISHED)
        {
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
        mFieldPassword.setError(null);
        mFieldAuthCode.setError(null);

        // Store values at the time of the login attempt.
        String id = mFieldId.getText().toString();
        String password = mFieldPassword.getText().toString();
        String authCode = mFieldAuthCode.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(id))
        {
            mFieldId.setError(getString(R.string.error_field_required));
            focusView = mFieldId;
            cancel = true;
        }

        if (TextUtils.isEmpty(password))
        {
            mFieldPassword.setError(getString(R.string.error_field_required));
            focusView = mFieldPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(authCode))
        {
            mFieldAuthCode.setError(getString(R.string.error_field_required));
            focusView = mFieldAuthCode;
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
            mAuthTask = new UserLoginTask(id, password, authCode);
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
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        private final String mId;
        private final String mPassword;
        private final String mAuthCode;
        private String mErrorMsg = "no error";

        UserLoginTask(String id, String password, String authCode) {
            mId = id;
            mPassword = password;
            mAuthCode = authCode;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String result = MDDNS.MANAGE_LOGIN_SERVER(mId, mPassword, mAuthCode);
            Log.d(TAG, String.format("Id: %s, Password: %s, Code: %s.Result: %s", mId, mPassword, mAuthCode, result));
            if ("ok".equals(result))
            {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(getString(R.string.key_mgr_id), mId);
                editor.putString(getString(R.string.key_mgr_password), mPassword);
                editor.commit();
                return true;
            }
            else
                mErrorMsg = result;
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(MgrLoginActivity.this, UserListActivity.class);
                intent.putExtra(getString(R.string.extra_mgr_id), mId);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MgrLoginActivity.this, getString(R.string.error_login) + "\n" + mErrorMsg, Toast.LENGTH_SHORT).show();
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

