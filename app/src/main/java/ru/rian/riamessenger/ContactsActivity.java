package ru.rian.riamessenger;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.karim.MaterialTabs;
import ru.rian.riamessenger.common.TabsRiaBaseActivity;
import ru.rian.riamessenger.fragments.BaseTabFragment;
import ru.rian.riamessenger.prefs.UserAppPreference;
import ru.rian.riamessenger.riaevents.response.XmppErrorEvent;
import ru.rian.riamessenger.utils.LocaleHelper;
import ru.rian.riamessenger.xmpp.SmackRosterManager;


public class ContactsActivity extends TabsRiaBaseActivity {

    //public static final String ARG_USER_ID = "userId";

     final BaseTabFragment.FragIds[] fragmentsIds = {BaseTabFragment.FragIds.ROBOTS_FRAGMENT, BaseTabFragment.FragIds.GROUPS_FRAGMENT, BaseTabFragment.FragIds.CONTACTS_FRAGMENT};
     final String[] fragmentsTags = {BaseTabFragment.ROBOTS_FRAGMENT_TAG, BaseTabFragment.GROUPS_FRAGMENT_TAG, BaseTabFragment.CONTACTS_FRAGMENT_TAG};

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Inject
    UserAppPreference userAppPreference;

    @Bind(R.id.material_tabs)
    MaterialTabs contactsMaterialTabs;

    @Bind(R.id.view_pager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.onCreate(this);

        RiaBaseApplication.component().inject(this);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.chats));
        final int numberOfTabs = fragmentsIds.length;
        SamplePagerAdapter adapter = new SamplePagerAdapter(getSupportFragmentManager(), numberOfTabs);
        viewPager.setAdapter(adapter);
        contactsMaterialTabs.setTypeface(Typeface.create("sans-serif-condensed", Typeface.NORMAL), Typeface.NORMAL);
        contactsMaterialTabs.setViewPager(viewPager);

    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    } */

     void logout() {
         userAppPreference.setLoginStringKey("");
         //userAppPreference.setPassStringKey("");
        /*
        Intent intent = new Intent(this, EnterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_exit:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getIdByTabIndex(int tabIndex) {
        return fragmentsIds[tabIndex].ordinal();
    }

    @Override
    protected String getTagByTabIndex(int tabIndex) {
        return fragmentsTags[tabIndex];
    }

    public void onEvent(final XmppErrorEvent xmppErrorEvent) {
        switch (xmppErrorEvent.state) {
            case EDbUpdating:
            case EDbUpdated:
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(xmppErrorEvent.state == XmppErrorEvent.State.EDbUpdated ? View.GONE : View.VISIBLE);
                    }
                });
                break;
            default:
                super.onEvent(xmppErrorEvent);
                break;
        }
    }

    public class SamplePagerAdapter extends FragmentPagerAdapter {

         final String[] TITLES = {SmackRosterManager.FIRST_SORTED_GROUP, getString(R.string.groups), getString(R.string.contacts)};

         final ArrayList<String> mTitles;

        public SamplePagerAdapter(FragmentManager fm, int numberOfTabs) {
            super(fm);
            mTitles = new ArrayList<>();
            for (int i = 0; i < numberOfTabs; i++) {
                mTitles.add(TITLES[i]);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public int getCount() {
            return mTitles.size();
        }

        @Override
        public Fragment getItem(int tabIndex) {
            String tag = getTagByTabIndex(tabIndex);
            Fragment fragment = null;
            if (tag != null) {
                fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment == null /*|| !fragment.isVisible()*/) {
                    fragment = BaseTabFragment.newInstance(getIdByTabIndex(tabIndex));
                }
            }
            return fragment;
        }
    }

    /*@Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }*/
}
