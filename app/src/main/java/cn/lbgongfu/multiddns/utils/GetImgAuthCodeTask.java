package cn.lbgongfu.multiddns.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by gf on 2015/12/19.
 */
public class GetImgAuthCodeTask extends AsyncTask<Void, Void, Bitmap> {
    private Context mContext;
    private ImageView mImageView;

    public GetImgAuthCodeTask(Context context, ImageView imageView)
    {
        mContext = context;
        if (context == null)
            throw new NullPointerException("Parameter 'context' can not be null!");
        if (imageView == null)
            throw new NullPointerException("Parameter 'imageView' can not be null!");
        mImageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        // TODO: 2015/12/19
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null)
        {
            mImageView.setImageBitmap(bitmap);
        }
        super.onPostExecute(bitmap);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
