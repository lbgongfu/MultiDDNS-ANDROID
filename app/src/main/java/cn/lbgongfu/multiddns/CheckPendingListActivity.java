package cn.lbgongfu.multiddns;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class CheckPendingListActivity extends AppCompatActivity {
    private View overlay;
    private ListView mListCheckPending;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_pending_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        overlay = findViewById(R.id.overlay);
        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlay.setVisibility(View.GONE);
            }
        });

        mListCheckPending = (ListView) findViewById(R.id.list_check_pending);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new String[]{"1", "2"});
        mListCheckPending.setAdapter(adapter);
        mListCheckPending.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListCheckPending.setSelection(position);
                overlay.setVisibility(View.VISIBLE);
            }
        });

        Button mBtnPass = (Button) findViewById(R.id.btn_pass);
        mBtnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2015/12/15  
            }
        });

        Button mBtnNoPass = (Button) findViewById(R.id.btn_no_pass);
        mBtnNoPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2015/12/15
            }
        });
    }

}
