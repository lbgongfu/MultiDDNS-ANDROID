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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ddns.sdk.MDDNS;

/**
 * A login screen that offers login via email/password.
 */
public class MgrResetPasswordActivity extends AppCompatActivity {

    private static final String TAG = MgrResetPasswordActivity.class.getName();
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mFieldPassword;
    private EditText mFieldPassword2;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mgr_reset_password);
        // Set up the login form.
        mFieldPassword = (EditText) findViewById(R.id.field_password);
        mFieldPassword2 = (EditText) findViewById(R.id.field_password_copy);

        Button mBtnComplete = (Button) findViewById(R.id.btn_complete);
        mBtnComplete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
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

        mFieldPassword.setError(null);
        mFieldPassword2.setError(null);

        // Store values at the time of the login attempt.
        String password = mFieldPassword.getText().toString();
        String password2 = mFieldPassword2.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password))
        {
            mFieldPassword.setError(getString(R.string.error_field_required));
            focusView = mFieldPassword;
            cancel = true;
        }

        if (TextUtils.isEmpty(password2))
        {
            mFieldPassword2.setError(getString(R.string.error_field_required));
            focusView = mFieldPassword2;
            cancel = true;
        }

        if (!cancel && !password.equals(password2))
        {
            mFieldPassword2.setError(getString(R.string.error_password_inconformity));
            cancel = true;
            focusView = mFieldPassword2;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(password);
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

        private final String mPassword;
        private String result;

        UserLoginTask(String password) {
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, String.format("Call method(MDDNS.RESET_MANAGE_PASSWORD) with parameters (password=%s)", mPassword));
            result = MDDNS.RESET_MANAGE_PASSWORD(mPassword);
            Log.d(TAG, String.format("method(MDDNS.RESET_MANAGE_PASSWORD) return %s", result));
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(MgrResetPasswordActivity.this, getString(R.string.prompt_password_reset_success), Toast.LENGTH_LONG).show();
                startActivity(new Intent(MgrResetPasswordActivity.this, MgrLoginActivity.class));
                finish();
            } else {
                Toast.makeText(MgrResetPasswordActivity.this, result, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

