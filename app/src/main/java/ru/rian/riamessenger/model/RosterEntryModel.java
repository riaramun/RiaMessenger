package ru.rian.riamessenger.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.jivesoftware.smack.packet.Presence;

import ru.rian.riamessenger.common.DbColumns;

/**
 * Created by Roman on 7/9/2015.
 */

@Table(name = "RosterEntryModels", id = BaseColumns._ID)
public class RosterEntryModel extends Model {

    @Column(name = DbColumns.FromJidCol, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String bareJid;

    @Column(name = "Presence")
    public Integer presence;

    /*@Column(name = DbColumns.ChatRoomModel)
    public ChatRoomModel chatRoomModel;*/

    @Column(name = DbColumns.NameCol)
    public String name;

    @Column(name = DbColumns.RosterGroupModelCol)
    public RosterGroupModel rosterGroupModel;

    // Used to return items from another table based on the foreign key
   /* public List<MessageContainer> items() {
        return getMany(MessageContainer.class, "RosterEntryModel");
    }*/

    // Make sure to have a default constructor for every ActiveAndroid model
    public RosterEntryModel() {
        super();
        presence = UserStatus.USER_STATUS_UNAVAILIBLE.ordinal();
    }

    public boolean setPresence(Presence presence) {
        Integer pres;
        if (presence.isAway())
            pres = UserStatus.USER_STATUS_AWAY.ordinal();
        else if (presence.isAvailable())
            pres = UserStatus.USER_STATUS_AVAILIBLE.ordinal();
        else
            pres = UserStatus.USER_STATUS_UNAVAILIBLE.ordinal();
        if (pres == this.presence) {
            return false;
        }
        this.presence = pres;
        return true;
    }

    public enum UserStatus {
        USER_STATUS_AVAILIBLE,
        USER_STATUS_AWAY,
        USER_STATUS_UNAVAILIBLE
    }
}
