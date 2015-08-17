package ru.rian.riamessenger.loaders;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;

import com.activeandroid.Cache;
import com.activeandroid.content.ContentProvider;

import ru.rian.riamessenger.common.DbColumns;
import ru.rian.riamessenger.fragments.BaseTabFragment;
import ru.rian.riamessenger.loaders.base.CursorRiaLoader;
import ru.rian.riamessenger.model.MessageContainer;

/**
 * Created by Roman Lebedenko.
 * fisher3421@gmail.com
 */

public class ChatsListenerLoader extends ChatsBaseLoader {


    //String jid_to_exclude;
    int tabIdloader = -1;
    public ChatsListenerLoader(Context ctx, Bundle args) {
        super(ctx);
      //  jid_to_exclude = args.getString(ChatsFragment.ARG_JID_TO_EXCLUDE);
        tabIdloader = args.getInt(BaseTabFragment.ARG_TAB_ID);
        BaseTabFragment.FragIds fragIds = BaseTabFragment.FragIds.values()[tabIdloader];
    }
}
