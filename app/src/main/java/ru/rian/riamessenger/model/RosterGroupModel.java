package ru.rian.riamessenger.model;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by Roman on 7/9/2015.
 */

@Table(name = "RosterGroupModels", id = BaseColumns._ID)
public class RosterGroupModel extends Model {

    // This is a regular field
    @Column(name = "Name", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String name;

    // Make sure to have a default constructor for every ActiveAndroid model
    public RosterGroupModel(){
        super();
    }

    public List<RosterEntryModel> items() {
        return getMany(RosterEntryModel.class, "RosterGroupModel");
    }
}
