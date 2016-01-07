package cn.lbgongfu.multiddns;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ddns.sdk.MDDNS;

public class InputDetailActivity extends AppCompatActivity {
    private static final String TAG = InputDetailActivity.class.getName();
    private EditText mFieldGender;
    private EditText mFieldPassword;
    private EditText mFieldPassword2;
    private View mProgressView;
    private View mLoginFormView;
    private AsyncTask mRegisterTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_detail);

        mFieldGender = (EditText) findViewById(R.id.field_gender);
        mFieldGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(InputDetailActivity.this, SelectGenderActivity.class), 0);
            }
        });
        mFieldPassword = (EditText) findViewById(R.id.field_password);
        mFieldPassword2 = (EditText) findViewById(R.id.field_password_copy);

        Button mBtnComplete = (Button) findViewById(R.id.btn_complete);
        mBtnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptRegister() {
        if (mRegisterTask == null || mRegisterTask.getStatus() == AsyncTask.Status.FINISHED)
        {
            final String password = mFieldPassword.getText().toString();
            final String password2 = mFieldPassword2.getText().toString();
            final String gender = mFieldGender.getText().toString();
            boolean passed = isAllFieldValid(password, password2, gender);
            if (!passed) return;
            showProgress(true);
            mRegisterTask = new AsyncTask<Void, Void, Boolean>()
            {
                public String result;

                @Override
                protected Boolean doInBackground(Void... params) {
                    Log.d(TAG, String.format("Call method(MDDNS.CHECK_MS_VERIFY_CODE) with parameters(password=%s, gender=%s)", password, gender));
                    result = MDDNS.REGISTER_NEW_MANAGE(password, getString(R.string.label_men).equals(gender) ? 1 : 0);
                    Log.d(TAG, String.format("Call method(MDDNS.CHECK_MS_VERIFY_CODE) done and return %s", result));
                    return "ok".equals(result);
                }

                @Override
                protected void onPostExecute(Boolean success) {
                    mRegisterTask = null;
                    showProgress(false);
                    if (success)
                    {
                        Toast.makeText(InputDetailActivity.this, getString(R.string.prompt_register_success), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(InputDetailActivity.this, MgrLoginActivity.class));
                        finish();
                    }
                    else
                        Toast.makeText(InputDetailActivity.this, result, Toast.LENGTH_LONG).show();
                }

                @Override
                protected void onCancelled() {
                    mRegisterTask = null;
                    showProgress(false);
                }
            }.execute((Void[]) null);
        }
    }

    private boolean isAllFieldValid(String password, String password2, String gender) {
        mFieldPassword.setError(null);
        mFieldPassword2.setError(null);
        View focusView = null;
        boolean cancel = false;
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
        if (!password.equals(password2))
        {
            mFieldPassword2.setError(getString(R.string.error_password_inconformity));
            focusView = mFieldPassword2;
            cancel = true;
        }
        return !cancel;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case 0:
                mFieldGender.setText(data.getStringExtra(Constants.KEY_SELECTED_GENDER));
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
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
}
