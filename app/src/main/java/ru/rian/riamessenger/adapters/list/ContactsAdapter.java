package ru.rian.riamessenger.adapters.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import java.util.HashSet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.rian.riamessenger.R;
import ru.rian.riamessenger.adapters.base.BaseRiaRecyclerAdapter;
import ru.rian.riamessenger.adapters.viewholders.ContactViewHolder;
import ru.rian.riamessenger.adapters.viewholders.EmptyViewHolder;
import ru.rian.riamessenger.listeners.ContactsListClickListener;
import ru.rian.riamessenger.model.RosterEntryModel;
import ru.rian.riamessenger.utils.DbHelper;
import ru.rian.riamessenger.utils.NetworkStateManager;
import ru.rian.riamessenger.utils.RiaTextUtils;
import ru.rian.riamessenger.utils.ViewUtils;

/**
 *
 */
public class ContactsAdapter extends BaseRiaRecyclerAdapter implements RosterEntryIdGetter, BubbleTextGetter {

    public enum ListItemMode {
        EPresence,
        ECheckNox
    }

    @Getter
    HashSet<String> selectedUsersJidMap = new HashSet<String>();

    final ListItemMode listItemMode;

    int mHeaderDisplay;

    boolean mMarginsFixed;

    final Context mContext;

    final ContactsListClickListener contactsListClickListener;

    @Override
    public String getTextToShowInBubble(final int pos) {
        String retStr = "";
        if (entries != null && entries.size() > 0) {
            retStr = Character.toString(entries.get(pos).text.charAt(0));
        }
        return retStr;
    }

    public ContactsAdapter(ListItemMode listItemMode, Context context, int headerMode, ContactsListClickListener contactsListClickListener) {
        this.listItemMode = listItemMode;
        mContext = context;
        mHeaderDisplay = headerMode;
        this.contactsListClickListener = contactsListClickListener;
    }

    public boolean isItemHeader(int position) {
        return entries.get(position).isHeader;
    }


    public String itemToString(int position) {
        return entries.get(position).text;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_item, parent, false);
                viewHolder = new ContactViewHolder(view);

                break;
            case VIEW_TYPE_CONTENT:
                int resId = -1;
                if (listItemMode == ListItemMode.EPresence) {
                    resId = R.layout.list_item_contact_with_presence;
                } else {
                    resId = R.layout.list_item_contact_with_checkbox;
                }
                view = LayoutInflater.from(parent.getContext())
                        .inflate(resId, parent, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RecyclerView recyclerView = (RecyclerView) v.getParent();
                        int pos = recyclerView.getChildAdapterPosition(v);
                        String jid = getUser(pos);
                        if (selectedUsersJidMap.contains(jid)) {
                            selectedUsersJidMap.remove(jid);

                        } else {

                            selectedUsersJidMap.add(jid);
                        }
                        contactsListClickListener.onClick(ContactsAdapter.this, v);
                        ContactsAdapter.this.notifyItemChanged(pos);
                    }
                });
                viewHolder = new ContactViewHolder(view);
                break;
            case VIEW_TYPE_EMPTY_ITEM:
                viewHolder = super.onCreateViewHolder(parent, viewType);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final View itemView = holder.itemView;
        final GridSLM.LayoutParams lp = GridSLM.LayoutParams.from(itemView.getLayoutParams());

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_EMPTY_ITEM:
                emptyViewHolder = (EmptyViewHolder) holder;
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.setFirstPosition(0);
                lp.setSlm(LinearSLM.ID);
                break;
            case VIEW_TYPE_CONTENT:
            case VIEW_TYPE_HEADER:
                ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
                final LineItem item = entries.get(position);

                // Overrides xml attrs, could use different layouts too.
                if (item.isHeader) {
                    contactViewHolder.contactName.setText(item.text);
                    lp.headerDisplay = mHeaderDisplay;
                    if (lp.isHeaderInline() || (mMarginsFixed && !lp.isHeaderOverlay())) {
                        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    } else {
                        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }

                    lp.headerEndMarginIsAuto = !mMarginsFixed;
                    lp.headerStartMarginIsAuto = !mMarginsFixed;
                } else {
                    contactViewHolder.contactName.setText(RiaTextUtils.capFirst(item.text));
                    if (listItemMode == ListItemMode.EPresence) {
                        if (NetworkStateManager.isNetworkAvailable(mContext)) {
                            ViewUtils.setOnlineStatus(contactViewHolder.onlineStatus, item.presence);
                        } else {
                            ViewUtils.setOnlineStatus(contactViewHolder.onlineStatus, RosterEntryModel.UserStatus.USER_STATUS_UNAVAILIBLE.ordinal());
                        }
                    } else {
                        String jid = getUser(position);
                        contactViewHolder.contactSelected.setChecked(selectedUsersJidMap.contains(jid));
                    }
                }
                lp.setSlm(LinearSLM.ID);
                lp.setColumnWidth(mContext.getResources().getDimensionPixelSize(R.dimen.grid_column_width));
                lp.setFirstPosition(item.sectionFirstPosition);
                break;
        }
        itemView.setLayoutParams(lp);
    }

    @Override
    public int getItemViewType(int position) {
        if (entries != null && entries.size() > 0) {
            return entries.get(position).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
        } else {
            return VIEW_TYPE_EMPTY_ITEM;
        }
    }


    public void setHeaderDisplay(int headerDisplay) {
        mHeaderDisplay = headerDisplay;
        notifyHeaderChanges();
    }

    public void setMarginsFixed(boolean marginsFixed) {
        mMarginsFixed = marginsFixed;
        notifyHeaderChanges();
    }

    void notifyHeaderChanges() {
        if (entries != null) {
            for (int i = 0; i < entries.size(); i++) {
                LineItem item = entries.get(i);
                if (item.isHeader) {
                    notifyItemChanged(i);
                }
            }
        }
    }

    @Override
    public String getUser(int index) {
        String jid = null;
        if (entries != null && entries.size() > index) {
            long id = entries.get(index).modelId;
            RosterEntryModel rosterEntryModel = DbHelper.getRosterEntryById(id);
            if (rosterEntryModel != null) {
                jid = rosterEntryModel.bareJid;
            }
        }
        return jid;
    }

    @AllArgsConstructor
    public static class LineItem {

        public String text;

        public boolean isHeader;

        public int sectionManager;

        public int sectionFirstPosition;

        public int presence;

        public Long modelId;
    }


}
