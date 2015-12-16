package cn.lbgongfu.multiddns;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;

public class SelectParseSpanActivity extends AppCompatActivity {
    private NumberPicker mPickerParseSpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_parse_span);

        mPickerParseSpan = (NumberPicker) findViewById(R.id.picker_parse_span);
        mPickerParseSpan.setMinValue(5);
        mPickerParseSpan.setMaxValue(200);
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
            Intent data = new Intent();
            data.putExtra(Constants.KEY_SELECTED_PARSE_SPAN, mPickerParseSpan.getValue());
            setResult(Constants.REQUEST_CODE_SELECT_PARSE_SPAN, data);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
