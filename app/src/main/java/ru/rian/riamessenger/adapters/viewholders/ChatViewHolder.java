package ru.rian.riamessenger.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.devspark.robototextview.widget.RobotoTextView;

import ru.rian.riamessenger.R;

/**
 * Created by Roman on 7/2/2015.
 */

public class ChatViewHolder extends RecyclerView.ViewHolder {

    public final RobotoTextView messageTextView;
    public final RobotoTextView dateTextView;
    public final RobotoTextView contactName;
    public final RobotoTextView onlineStatus;

    public ChatViewHolder(View itemView) {
        super(itemView);
        messageTextView = (RobotoTextView) itemView.findViewById(R.id.message_text);
        dateTextView = (RobotoTextView) itemView.findViewById(R.id.message_created_date);
        contactName = (RobotoTextView) itemView.findViewById(R.id.message_from);
        onlineStatus = (RobotoTextView) itemView.findViewById(R.id.user_online_status);
    }

}
