package cn.lbgongfu.multiddns;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * A login screen that offers login via email/password.
 */
public class MgrLoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mFieldId;
    private EditText mFieldPassword;
    private EditText mFieldAuthCode;
    private ImageView mImgAuthCode;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mgr_login);
        // Set up the login form.
        mFieldId = (EditText) findViewById(R.id.field_id);
        mFieldPassword = (EditText) findViewById(R.id.field_password);
        mFieldAuthCode = (EditText) findViewById(R.id.field_auth_code);
        mImgAuthCode = (ImageView) findViewById(R.id.img_auth_code);

        Button mBtnGetAuthCode = (Button) findViewById(R.id.btn_get_auth_code);
        mBtnGetAuthCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
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
                MgrRegisterStep1Activity.register = true;
                startActivity(new Intent(MgrLoginActivity.this, MgrRegisterStep1Activity.class));
                finish();
            }
        });

        Button mBtnForgetPassword = (Button) findViewById(R.id.btn_forget_password);
        mBtnForgetPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MgrRegisterStep1Activity.register = false;
                startActivity(new Intent(MgrLoginActivity.this, MgrRegisterStep1Activity.class));
                finish();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
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
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mId;
        private final String mPassword;
        private final String mAuthCode;

        UserLoginTask(String id, String password, String authCode) {
            mId = id;
            mPassword = password;
            mAuthCode = authCode;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
                startActivity(new Intent(MgrLoginActivity.this, UserListActivity.class));
            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
//                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

