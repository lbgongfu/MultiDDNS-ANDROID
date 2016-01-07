package cn.lbgongfu.multiddns;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.lbgongfu.multiddns.dummy.DummyContent;
import cn.lbgongfu.multiddns.models.Domain;

/**
 * A fragment representing a single User detail screen.
 * This fragment is either contained in a {@link UserListActivity}
 * in two-pane mode (on tablets) or a {@link UserDetailActivity}
 * on handsets.
 */
public class UserDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    private Domain mDomain;
    private AsyncTask<Integer, Void, Domain> mFetchDomainTask;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
//            fetchDomain(getArguments().getInt(ARG_ITEM_ID));
            mDomain = UserListFragment.getDomain(getArguments().getInt(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mDomain.getDomain());
            }
        }
    }

    private void fetchDomain(int id) {
        if (mFetchDomainTask == null || mFetchDomainTask.getStatus() == AsyncTask.Status.FINISHED)
        {
            mFetchDomainTask = new AsyncTask<Integer, Void, Domain>()
            {

                @Override
                protected Domain doInBackground(Integer... params) {
                    int id = params[0];
                    return UserListFragment.getDomain(id);
                }

                @Override
                protected void onPostExecute(Domain domain) {
                    mDomain = domain;
                }

                @Override
                protected void onCancelled() {
                    mFetchDomainTask = null;
                }
            }.execute(id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_detail, container, false);
        if (mDomain != null) {
            TextView mTextDomain = (TextView) rootView.findViewById(R.id.text_domain);
            TextView mTextPassword = (TextView) rootView.findViewById(R.id.text_password);
            TextView mTextRegisterDate = (TextView) rootView.findViewById(R.id.text_register_date);
            TextView mTextEffectiveDate = (TextView) rootView.findViewById(R.id.text_effective_date);
            TextView mTextActiveDate = (TextView) rootView.findViewById(R.id.text_active_date);
            TextView mTextIP = (TextView) rootView.findViewById(R.id.text_curr_ip);
            TextView mTextResolveInterval = (TextView) rootView.findViewById(R.id.text_resolve_interval);
            TextView mTextRedirectCheck = (TextView) rootView.findViewById(R.id.text_redirect_check);
            TextView mTextRedirectURL = (TextView) rootView.findViewById(R.id.text_redirect_url);
            TextView mTextReference = (TextView) rootView.findViewById(R.id.text_reference);

            mTextDomain.setText(mDomain.getDomain());
            mTextPassword.setText(mDomain.getPassword());
            mTextRegisterDate.setText(mDomain.getRegisterDate());
            mTextEffectiveDate.setText(mDomain.getEffectiveDate());
            mTextActiveDate.setText(mDomain.getActiveDate());
            mTextIP.setText(mDomain.getIp());
            mTextResolveInterval.setText(mDomain.getResolveInterval());
            mTextRedirectCheck.setText(mDomain.getRedirectCheck());
            mTextRedirectURL.setText(mDomain.getRedirectURL());
            mTextReference.setText(mDomain.getReference());
        }
        return rootView;
    }
}
