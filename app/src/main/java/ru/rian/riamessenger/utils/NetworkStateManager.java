package ru.rian.riamessenger.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDiskIOException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.activeandroid.ActiveAndroid;

import org.jivesoftware.smack.packet.Presence;

import ru.rian.riamessenger.model.RosterEntryModel;

public class NetworkStateManager {

    public static boolean isNetworkAvailable(Context context) {
        boolean isMobile = false, isWifi = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] infoAvailableNetworks = connectivityManager.getAllNetworkInfo();
        if (infoAvailableNetworks != null) {
            for (NetworkInfo network : infoAvailableNetworks) {
                if (network.getType() == ConnectivityManager.TYPE_WIFI) {
                    if (network.isConnected() && network.isAvailable())
                        isWifi = true;
                }
                if (network.getType() == ConnectivityManager.TYPE_MOBILE) {
                    if (network.isConnected() && network.isAvailable())
                        isMobile = true;
                }
            }
        }
        return isMobile || isWifi;
    }

    public static boolean setCurrentUserPresence(Presence presence, String bareJid) {
        boolean isChanged = true;
        RosterEntryModel rosterEntryModel = DbHelper.getRosterEntryByBareJid(bareJid);
        if (rosterEntryModel == null) {
            rosterEntryModel = new RosterEntryModel();
            rosterEntryModel.bareJid = bareJid;
        } else {
            isChanged = rosterEntryModel.setPresence(presence);
        }
        if (isChanged) {
            try {
                ActiveAndroid.beginTransaction();
                rosterEntryModel.save();
                ActiveAndroid.setTransactionSuccessful();
            } catch (SQLiteDiskIOException e) {
                Log.i("Service", e.getMessage());
            } finally {
                ActiveAndroid.endTransaction();
            }
        }
        return isChanged;
    }
}
