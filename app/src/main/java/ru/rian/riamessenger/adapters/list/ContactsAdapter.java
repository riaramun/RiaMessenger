package ru.rian.riamessenger.adapters.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tonicartos.superslim.GridSLM;
import com.tonicartos.superslim.LinearSLM;

import lombok.AllArgsConstructor;
import ru.rian.riamessenger.R;
import ru.rian.riamessenger.adapters.base.BaseRiaRecyclerAdapter;
import ru.rian.riamessenger.adapters.viewholders.ContactViewHolder;
import ru.rian.riamessenger.adapters.viewholders.EmptyViewHolder;

/**
 *
 */
public class ContactsAdapter extends BaseRiaRecyclerAdapter implements BubbleTextGetter {

    private int mHeaderDisplay;

    private boolean mMarginsFixed;

    private final Context mContext;

    @Override
    public String getTextToShowInBubble(final int pos)
    {
        return Character.toString(((LineItem)entries.get(pos)).text.charAt(0));
    }

    public ContactsAdapter(Context context, int headerMode) {
        mContext = context;
        mHeaderDisplay = headerMode;
    }

    public boolean isItemHeader(int position) {
        return ((LineItem) entries.get(position)).isHeader;
    }

    public String itemToString(int position) {
        return ((LineItem) entries.get(position)).text;
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
                ;
                break;
            case VIEW_TYPE_CONTENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item, parent, false);
                viewHolder = new ContactViewHolder(view);
                ;
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
                final LineItem item = (LineItem) entries.get(position);
                contactViewHolder.contactName.setText(item.text);
                // Overrides xml attrs, could use different layouts too.
                if (item.isHeader) {
                    lp.headerDisplay = mHeaderDisplay;
                    if (lp.isHeaderInline() || (mMarginsFixed && !lp.isHeaderOverlay())) {
                        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    } else {
                        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }

                    lp.headerEndMarginIsAuto = !mMarginsFixed;
                    lp.headerStartMarginIsAuto = !mMarginsFixed;
                } else {
                    contactViewHolder.setOnlineStatus(item.presence);
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
            return ((LineItem) entries.get(position)).isHeader ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
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

    private void notifyHeaderChanges() {
        if (entries != null) {
            for (int i = 0; i < entries.size(); i++) {
                LineItem item = (LineItem) entries.get(i);
                if (item.isHeader) {
                    notifyItemChanged(i);
                }
            }
        }
    }

    @AllArgsConstructor
    public static class LineItem {

        public String text;

        public boolean isHeader;

        public int sectionManager;

        public int sectionFirstPosition;

        public int presence;
    }


}
