package cn.lbgongfu.multiddns.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.ddns.sdk.MDDNS;

import cn.lbgongfu.multiddns.R;

/**
 * Created by gf on 2015/12/19.
 */
public class FetchImgAuthCodeTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = FetchImgAuthCodeTask.class.getName();
    private Context mContext;
    private ImageView mImageView;
    private String mResult;
    private Drawable mDrawable;

    public FetchImgAuthCodeTask(Context context, ImageView imageView)
    {
        if (context == null)
            throw  new NullPointerException("Parameter 'context' can not be null!");
        mContext = context;
        if (imageView == null)
            throw new NullPointerException("Parameter 'imageView' can not be null!");
        mImageView = imageView;
        execute((Void[]) null);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean success = false;
        try
        {
            Log.d(TAG, "Call method (MDDNS.READ_VERIFY_CODE_FILE) with no parameters");
            mResult = MDDNS.READ_VERIFY_CODE_FILE();
            Log.d(TAG, String.format("Call method (MDDNS.READ_VERIFY_CODE_FILE) return %s", mResult));
            mDrawable = BitmapDrawable.createFromPath(mResult);
            success = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return success;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success)
        {
            mImageView.setImageDrawable(mDrawable);
        }
        else
        {
            Toast.makeText(mContext, TextUtils.isEmpty(mResult) ? mContext.getString(R.string.error_undifined) : mResult, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled() {
        mResult = null;
        mDrawable = null;
    }
}
