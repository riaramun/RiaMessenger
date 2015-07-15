/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ru.rian.riamessenger.adapters.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.wnafee.vector.MorphButton;

import ru.rian.riamessenger.R;
import ru.rian.riamessenger.adapters.base.AbstractExpandableDataProvider;
import ru.rian.riamessenger.adapters.viewholders.ContactViewHolder;
import ru.rian.riamessenger.compat.MorphButtonCompat;

public class GroupsAdapter
        extends AbstractExpandableItemAdapter<GroupsAdapter.MyGroupViewHolder, ContactViewHolder> {
    private static final String TAG = "GroupsAdapter";

    private AbstractExpandableDataProvider mProvider;

    public static abstract class MyBaseViewHolder extends AbstractExpandableItemViewHolder {
        public RelativeLayout mContainer;
        public TextView mTextView;

        public MyBaseViewHolder(View v) {
            super(v);
            mContainer = (RelativeLayout) v.findViewById(R.id.container);
            mTextView = (TextView) v.findViewById(R.id.contact_name);
        }
    }

    public static class MyGroupViewHolder extends MyBaseViewHolder {
        public MorphButtonCompat mMorphButton;

        public MyGroupViewHolder(View v) {
            super(v);
            mMorphButton = new MorphButtonCompat(v.findViewById(R.id.indicator));
        }
    }

    /*public static class ContactViewHolder extends MyBaseViewHolder {
        public ContactViewHolder(View v) {
            super(v);
        }
    }*/

    public GroupsAdapter(AbstractExpandableDataProvider dataProvider) {
        mProvider = dataProvider;

        // ExpandableItemAdapter requires stable ID, and also
        // have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true);
    }

    public void swapProvider(AbstractExpandableDataProvider dataProvider) {
        mProvider = dataProvider;
    }

    @Override
    public int getGroupCount() {
        return mProvider.getGroupCount();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mProvider.getChildCount(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mProvider.getGroupItem(groupPosition).getGroupId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        AbstractExpandableDataProvider.ChildData childData = mProvider.getChildItem(groupPosition, childPosition);
        if (childData != null)
            return childData.getChildId();
        else
            return 0;
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public MyGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_group_item, parent, false);
        return new MyGroupViewHolder(v);
    }

    @Override
    public ContactViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_item, parent, false);
        return new ContactViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(MyGroupViewHolder holder, int groupPosition, int viewType) {
        // child item
        final AbstractExpandableDataProvider.BaseData item = mProvider.getGroupItem(groupPosition);

        // set text
        holder.mTextView.setText(item.getText());

        // mark as clickable
        holder.itemView.setClickable(true);

        // set background resource (target view ID: container)
        final int expandState = holder.getExpandStateFlags();

        if ((expandState & RecyclerViewExpandableItemManager.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId;
            MorphButton.MorphState indicatorState;

            if ((expandState & RecyclerViewExpandableItemManager.STATE_FLAG_IS_EXPANDED) != 0) {
                bgResId = R.drawable.bg_group_item_expanded_state;
                indicatorState = MorphButton.MorphState.END;
            } else {
                bgResId = R.drawable.bg_group_item_normal_state;
                indicatorState = MorphButton.MorphState.START;
            }

            holder.mContainer.setBackgroundResource(bgResId);

            if (holder.mMorphButton.getState() != indicatorState) {
                holder.mMorphButton.setState(indicatorState, true);
            }
        }
    }

    @Override
    public void onBindChildViewHolder(ContactViewHolder holder, int groupPosition, int childPosition, int viewType) {
        // group item
        final AbstractExpandableDataProvider.ChildData item = mProvider.getChildItem(groupPosition, childPosition);
        if(item != null) {
            // set text
            holder.contactName.setText(item.getText());
            holder.setOnlineStatus(item.getPresence());
        }
        // set background resource (target view ID: container)
        // holder.mContainer.setBackgroundResource(R.drawable.bg_item_normal_state);
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        // check the item is *not* pinned
        if (mProvider.getGroupItem(groupPosition).isPinnedToSwipeLeft()) {
            // return false to raise View.OnClickListener#onClick() event
            return false;
        }

        // check is enabled
        if (!(holder.itemView.isEnabled() && holder.itemView.isClickable())) {
            return false;
        }

        return true;
    }
}