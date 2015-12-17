package cn.lbgongfu.multiddns;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectDomainActivity extends AppCompatActivity {


    public class Domain
    {
        private String domain;
        private boolean redirect;

        public Domain(String domain, boolean redirect)
        {
            this.domain = domain;
            this.redirect = redirect;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public boolean isRedirect() {
            return redirect;
        }

        public void setRedirect(boolean redirect) {
            this.redirect = redirect;
        }

        @Override
        public String toString() {
            return String.format("%s（%s）", domain, redirect ? "支持域名转向" : "不支持域名转向");
        }
    }
    private Domain[] domains = new Domain[]
            {
                new Domain("ip71.cn", true), new Domain("ip72.cn", false), new Domain("ip73.cn", true)
            };
    private ListView mListViewDomain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_domain);

        mListViewDomain = (ListView) findViewById(R.id.listView_domain);
        ArrayAdapter<Domain> data = new ArrayAdapter<Domain>(this, android.R.layout.simple_list_item_1, domains);
        mListViewDomain.setAdapter(data);
        mListViewDomain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra(Constants.KEY_SELECTED_DOMAIN, domains[position].domain);
                setResult(Constants.REQUEST_CODE_SELECT_DOMAIN, data);
                finish();
            }
        });
    }
}
