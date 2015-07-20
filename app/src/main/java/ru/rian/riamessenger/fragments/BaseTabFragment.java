/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.rian.riamessenger.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.SearchView;


import de.greenrobot.event.EventBus;
import ru.rian.riamessenger.ContactsActivity;
import ru.rian.riamessenger.common.RiaBaseFragment;
import ru.rian.riamessenger.loaders.ContactsLoader;
import ru.rian.riamessenger.loaders.base.BaseCursorRiaLoader;
import ru.rian.riamessenger.loaders.base.CursorRiaLoader;
import ru.rian.riamessenger.riaevents.response.XmppErrorEvent;
import ru.rian.riamessenger.utils.ScreenUtils;


public abstract class BaseTabFragment extends RiaBaseFragment implements LoaderManager.LoaderCallbacks<CursorRiaLoader.LoaderResult<Cursor>> {


    static public final String CHATS_FRAGMENT_TAG = ChatsFragment.class.getSimpleName();
    static public final String ROOMS_FRAGMENT_TAG = RoomsFragment.class.getSimpleName();
    static public final String ROBOTS_FRAGMENT_TAG = RobotsFragment.class.getSimpleName();
    static public final String GROUPS_FRAGMENT_TAG = GroupsFragment.class.getSimpleName();
    static public final String CONTACTS_FRAGMENT_TAG = ContactsFragment.class.getSimpleName();

    protected int tabId;

    static public final int ROBOTS_FRAGMENT = 0;
    static public final int GROUPS_FRAGMENT = 1;
    static public final int CONTACTS_FRAGMENT = 2;
    static public final int CHATS_FRAGMENT = 3;
    static public final int ROOMS_FRAGMENT = 4;

    public static final String ARG_TAB_ID = "tabId";
    public static final String ARG_TITLE_FILTER = "title_filter";

    public static BaseTabFragment newInstance(int tabId) {
        BaseTabFragment tabFragment = null;
        switch (tabId) {
            case CONTACTS_FRAGMENT:
                tabFragment = new ContactsFragment();
                break;
            case GROUPS_FRAGMENT:
                tabFragment = new GroupsFragment();
                break;
            case ROBOTS_FRAGMENT:
                tabFragment = new RobotsFragment();
                break;
            case CHATS_FRAGMENT:
                tabFragment = new ChatsFragment();
                break;
            case ROOMS_FRAGMENT:
                tabFragment = new RoomsFragment();
                break;
        }
        if (tabFragment != null) {
            Bundle b = new Bundle();
            b.putInt(ARG_TAB_ID, tabId);
            tabFragment.setArguments(b);
        }
        return tabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //RiaApplication.component().inject(this);
        tabId = getArguments().getInt(ARG_TAB_ID);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
        } else {

        }
        getLoaderManager().initLoader(tabId, getBundle(), this);

    }

    protected Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_TAB_ID, tabId);
        return bundle;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /*public void dbRestartLoader() {
        getLoaderManager().restartLoader(tabId, getBundle(), this);
    }*/

    public void onEvent(final XmppErrorEvent xmppErrorEvent) {

        switch (xmppErrorEvent.state) {
            case EDbUpdated:
                isUpdating = false;
                break;
            case EDbUpdating:
                isUpdating = true;
                break;
        }
    }

    Boolean isUpdating = false;

    protected String title_to_search = null;

    protected void setSearchViewListenersAndStyle(SearchView searchView) {

        ScreenUtils.styleSearchView(searchView, title_to_search, getActivity());

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                ScreenUtils.hideKeyboard(getActivity());
                title_to_search = query;
                getLoaderManager().restartLoader(tabId, getBundle(), BaseTabFragment.this);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                title_to_search = newText;
                getLoaderManager().restartLoader(tabId, getBundle(), BaseTabFragment.this);
                return false;
            }
        });
    }

    @Override
    public Loader<CursorRiaLoader.LoaderResult<Cursor>> onCreateLoader(int id, Bundle args) {
        return new ContactsLoader(getActivity(), args);
    }

    @Override
    public void onLoaderReset(Loader<CursorRiaLoader.LoaderResult<Cursor>> loader) {

    }
}
