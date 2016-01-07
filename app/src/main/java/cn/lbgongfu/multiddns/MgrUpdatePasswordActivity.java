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
import android.view.KeyEvent;
import android.view.Menu;
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
public class MgrUpdatePasswordActivity extends AppCompatActivity {
    private static final String TAG = MgrUpdatePasswordActivity.class.getName();
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UpdatePasswordTask mUpdatePasswordTask = null;

    // UI references.
    private EditText mFieldOldPassword;
    private EditText mFieldNewPassword;
    private EditText mFieldPassword2;
    private EditText mFieldAuthCode;
    private ImageView mImgAuthCode;
    private View mProgressView;
    private View mLoginFormView;
    private FetchImgAuthCodeTask mFetchImgAuthCodeTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mgr_update_password);
        // Set up the login form.
        mFieldOldPassword = (EditText) findViewById(R.id.field_old_password);
        mFieldNewPassword = (EditText) findViewById(R.id.field_new_password);
        mFieldPassword2 = (EditText) findViewById(R.id.field_password_copy);
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
        mImgAuthCode = (ImageView) findViewById(R.id.img_auth_code);

        Button mBtnGetAuthCode = (Button) findViewById(R.id.btn_get_auth_code);
        mBtnGetAuthCode.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchImgAuthCode();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        fetchImgAuthCode();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_complete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_complete)
        {
            attemptLogin();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mUpdatePasswordTask != null) {
            return;
        }

        // Reset errors.
        mFieldOldPassword.setError(null);
        mFieldNewPassword.setError(null);
        mFieldPassword2.setError(null);
        mFieldAuthCode.setError(null);

        // Store values at the time of the login attempt.
        String oldPassword = mFieldOldPassword.getText().toString();
        String newPassword = mFieldNewPassword.getText().toString();
        String password2 = mFieldPassword2.getText().toString();
        String authCode = mFieldAuthCode.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(oldPassword))
        {
            cancel = true;
            mFieldOldPassword.setError(getString(R.string.error_field_required));
            focusView = mFieldOldPassword;
        }

        if (TextUtils.isEmpty(newPassword))
        {
            cancel = true;
            mFieldNewPassword.setError(getString(R.string.error_field_required));
            focusView = mFieldNewPassword;
        }

        if (TextUtils.isEmpty(password2))
        {
            cancel = true;
            mFieldPassword2.setError(getString(R.string.error_field_required));
            focusView = mFieldPassword2;
        }

        if (TextUtils.isEmpty(authCode))
        {
            cancel = true;
            mFieldAuthCode.setError(getString(R.string.error_field_required));
            focusView = mFieldAuthCode;
        }

        if (!cancel && !newPassword.equals(password2))
        {
            cancel = true;
            mFieldPassword2.setError(getString(R.string.error_password_inconformity));
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
            mUpdatePasswordTask = new UpdatePasswordTask(oldPassword, newPassword, authCode);
            mUpdatePasswordTask.execute((Void) null);
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
    public class UpdatePasswordTask extends AsyncTask<Void, Void, Boolean> {

        private final String mOldPassword;
        private final String mNewPassword;
        private final String mAuthCode;
        private String result;

        UpdatePasswordTask(String oldPassword, String newPassword, String authCode) {
            mOldPassword = oldPassword;
            mNewPassword = newPassword;
            mAuthCode = authCode;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d(TAG, String.format("Call method (MDDNS.CHANG_MANAGE_PASSWORD) with parameters (oldPassword=***, newPassword=***, authCode=%s)", mAuthCode));
            result = MDDNS.CHANG_MANAGE_PASSWORD(mOldPassword, mNewPassword, mAuthCode);
            Log.d(TAG, String.format("Call method (MDDNS.CHANG_MANAGE_PASSWORD) return %s)", result));
            return "ok".equals(result);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mUpdatePasswordTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(MgrUpdatePasswordActivity.this, getString(R.string.prompt_update_password_success), Toast.LENGTH_LONG).show();
                startActivity(new Intent(MgrUpdatePasswordActivity.this, MgrLoginActivity.class));
                finish();
            } else {
                Toast.makeText(MgrUpdatePasswordActivity.this, getString(R.string.prompt_update_password_success), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mUpdatePasswordTask = null;
            showProgress(false);
        }
    }
}

