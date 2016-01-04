package cn.lbgongfu.multiddns;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ddns.sdk.MDDNS;

import java.util.ArrayList;
import java.util.List;

import cn.lbgongfu.multiddns.dummy.DummyContent;
import cn.lbgongfu.multiddns.models.Domain;

/**
 * A list fragment representing a list of Users. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link UserDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class UserListFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;
    private AsyncTask<Void, Void, List<Domain>> mFetchUserListTask;
    private List<Domain> mDomains;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(int id);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(int id) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateUserList();
    }

    private void updateUserList() {
        if (mFetchUserListTask == null || mFetchUserListTask.getStatus() == AsyncTask.Status.FINISHED)
        {
            mFetchUserListTask = new AsyncTask<Void, Void, List<Domain>>()
            {

                @Override
                protected List<Domain> doInBackground(Void... params) {
                    ArrayList<Domain> domains = new ArrayList<Domain>();
                    Domain domain = null;
                    main:for (int i = 0; i < 1000; i++)
                    {
                        domain = getDomain(i);
                        if (domain == null) break main;
                        domains.add(domain);
                    }
                    return domains;
                }

                @Override
                protected void onPostExecute(List<Domain> domains) {
                    mDomains = domains;
                    setListAdapter(new ArrayAdapter<>(
                        getActivity(),
                        android.R.layout.simple_list_item_activated_1,
                        android.R.id.text1, domains));
                }

                @Override
                protected void onCancelled() {
                    mFetchUserListTask = null;
                }
            }.execute((Void[]) null);
        }
    }

    @Nullable
    public static Domain getDomain(int id) {
        Domain domain;
        domain = new Domain();
        domain.setId(id);
        for (int j = 0; j < 8; j++)
        {
            String field = MDDNS.GET_USER_ITEM(id, j);
            switch (j)
            {
                case 0:
                    if (TextUtils.isEmpty(field))
                        return null;
                    domain.setDomain(field);
                    break;
                case 1:
                    domain.setRegisterDate(field);
                    break;
                case 2:
                    domain.setEffectiveDate(field);
                    break;
                case 3:
                    domain.setActiveDate(field);
                    break;
                case 4:
                    domain.setIp(field);
                    break;
                case 5:
                    domain.setResolveInterval(field);
                    break;
                case 6:
                    domain.setRedirectCheck(field);
                    break;
                case 7:
                    domain.setRedirectURL(field);
            }
        }
        return domain;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected(mDomains.get(position).getId());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}
