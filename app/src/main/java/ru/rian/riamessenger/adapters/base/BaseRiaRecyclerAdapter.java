package ru.rian.riamessenger.adapters.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import lombok.Getter;
import ru.rian.riamessenger.R;
import ru.rian.riamessenger.adapters.list.ContactsAdapter;
import ru.rian.riamessenger.adapters.viewholders.EmptyViewHolder;

/**
 * Created by Roman on 7/2/2015.
 */
public abstract class BaseRiaRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected static final int VIEW_TYPE_EMPTY_ITEM = 0x01;
    protected static final int VIEW_TYPE_HEADER = 0x02;
    protected static final int VIEW_TYPE_CONTENT = 0x03;


    protected List<ContactsAdapter.LineItem> entries = null;

    @Getter
    protected boolean isEmpty = false;
    protected EmptyViewHolder emptyViewHolder;

    public void updateEntries(List<ContactsAdapter.LineItem> entries) {
        if(entries != null) {
            this.entries = entries;
            notifyDataSetChanged();
        }
    }
    @Override
    public int getItemCount() {
        int count = entries == null ? 0 : entries.size();
        if (count == 0) {
            isEmpty = true;
            count = 1;//empty view
        } else {
            isEmpty = false;
        }
        return count;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        RecyclerView.ViewHolder vh = null;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_empty, parent, false);
       // itemView.setLayoutParams(new RecyclerView.LayoutParams(parent.getWidth(), parent.getHeight()));
        vh = new EmptyViewHolder(itemView);
        emptyViewHolder = (EmptyViewHolder) vh;
        return vh;
    }
}
