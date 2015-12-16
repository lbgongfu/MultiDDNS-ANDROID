package cn.lbgongfu.multiddns;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;

public class SelectRenewTimeoutActivity extends AppCompatActivity {
    private NumberPicker mNumPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_renew_timeout);

        mNumPicker = (NumberPicker) findViewById(R.id.picker_renew_timeout);
        mNumPicker.setMinValue(1);
        mNumPicker.setMaxValue(24);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_select_renew_timeout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_complete)
        {
            Intent data = new Intent();
            data.putExtra(UserRenewActivity.KEY_SELECTED_RENEW_TIMEOUT, mNumPicker.getValue());
            setResult(UserRenewActivity.REQUEST_CODE_SELECT_RENEW_TIMEOUT, data);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
