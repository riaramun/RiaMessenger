package ru.rian.riamessenger.services;
//TODO: Update this so that UI activities can just get references to the service
// rather than using AIDL
//TODO: Above seems to be done and working, but now need to update methods
// to just return asmack objects such as Roster rather than the simple wrappers

import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import ru.rian.riamessenger.RiaBaseApplication;
import ru.rian.riamessenger.common.RiaConstants;
import ru.rian.riamessenger.common.RiaEventBus;
import ru.rian.riamessenger.prefs.UserAppPreference;
import ru.rian.riamessenger.riaevents.connection.InternetConnEvent;
import ru.rian.riamessenger.riaevents.request.RiaMessageEvent;
import ru.rian.riamessenger.riaevents.request.RiaPresenceEvent;
import ru.rian.riamessenger.riaevents.request.RiaServiceEvent;
import ru.rian.riamessenger.riaevents.response.XmppErrorEvent;
import ru.rian.riamessenger.utils.NetworkStateManager;
import ru.rian.riamessenger.utils.XmppUtils;
import ru.rian.riamessenger.xmpp.SendMsgBroadcastReceiver;
import ru.rian.riamessenger.xmpp.SmackConnectionListener;
import ru.rian.riamessenger.xmpp.SmackMessageManager;
import ru.rian.riamessenger.xmpp.SmackRosterManager;
import ru.rian.riamessenger.xmpp.SmackXmppConnection;


public class RiaXmppService extends Service {
    final SendMsgBroadcastReceiver sendMsgBroadcastReceiver;

    XMPPTCPConnection xmppConnection;
    SmackXmppConnection smackXmppConnection;
    SmackMessageManager xmppMessageManager;
    SmackRosterManager smackRosterManager;

    @Override
    public void onTrimMemory(final int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Presence presence = new Presence(Presence.Type.available);
            presence.setMode(Presence.Mode.away);
            XmppUtils.changeCurrentUserStatus(presence, userAppPreference.getJidStringKey(), xmppConnection);
        }
    }

    public RiaXmppService() {
        super();
        Log.i("RiaService", "RiaService()");
        RiaBaseApplication.component().inject(this);
        sendMsgBroadcastReceiver = new SendMsgBroadcastReceiver(this);
        initSmackModules();
    }

    void initSmackModules() {
        xmppConnection = new XMPPTCPConnection(SmackXmppConnection.getConfig(userAppPreference));
        xmppConnection.addConnectionListener(new SmackConnectionListener(this, userAppPreference, sendMsgBroadcastReceiver));
        xmppConnection.setPacketReplyTimeout(RiaConstants.CONNECTING_TIME_OUT);
        smackRosterManager = new SmackRosterManager(this, userAppPreference, xmppConnection);
        smackXmppConnection = new SmackXmppConnection(xmppConnection, userAppPreference);
        xmppMessageManager = new SmackMessageManager(this, xmppConnection, sendMsgBroadcastReceiver, userAppPreference);
    }

    //XmppServiceComponent xmppServiceComponent;

    // static Context mContext = null;

    /*public static XmppServiceComponent component() {
        return getContext().xmppServiceComponent;
    }*/

    @Inject
    UserAppPreference userAppPreference;


    private Handler connectionHandler = new Handler();
    /*
    This runnable tests are we connected, signed in, and got the roster
    */
    private Runnable connectionRunnable = new Runnable() {
        @Override
        public void run() {
            setConnectingState(false);
            onStartService();
        }
    };

    //OfflineMessageManager offlineMessageManager;

    boolean doLoginAndPassExist() {
        return !TextUtils.isEmpty(userAppPreference.getLoginStringKey()) && !TextUtils.isEmpty(userAppPreference.getPassStringKey());
    }

    public void onEvent(RiaMessageEvent event) {
        if (xmppMessageManager != null) {
            xmppMessageManager.sendMessageToServer(event.getJid(), event.getMessage());
        }
    }

    /*public void onEvent(RiaPresenceEvent event) {
        try {
            smackRosterManager.getRoster().getPresence(JidCreate.bareFrom(event.getJid()));
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
    }*/


    public void onEvent(RiaServiceEvent event) {

        switch (event.getEventId()) {
            case TO_SIGN_IN:
                if (smackXmppConnection.isAuthenticated()) {
                    RiaEventBus.post(XmppErrorEvent.State.EAuthenticated);
                    XmppUtils.changeCurrentUserStatus(new Presence(Presence.Type.available), userAppPreference.getJidStringKey(), xmppConnection);
                } else {
                    onStartService();
                }
                break;
            case TO_SIGN_OUT:
                xmppConnection.disconnect();
                XmppUtils.changeCurrentUserStatus(new Presence(Presence.Type.unavailable), userAppPreference.getJidStringKey(), xmppConnection);
                stopSelf();
                break;
            /*case TO_GET_ROSTER:
                smackRosterManager.tryGetRosterFromServer();
                break;*/
        }
    }



    /*static RiaXmppService getContext() {
        return (RiaXmppService) mContext;
    }*/


    private static final int NOTIFICATION_CONNECTION_STATUS = 1;
    //  private int mClientCount = 0;


    public NotificationManager getNotifyManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void onEvent(final InternetConnEvent internetConnEvent) {
        if (internetConnEvent.isConnectionAvailable()) {
            onStartService();
        } else {
            NetworkStateManager.setCurrentUserPresence(new Presence(Presence.Type.unavailable), userAppPreference.getJidStringKey());
        }
    }

    @Override
    public void onCreate() {
        Log.i("RiaService", "onCreate");
        EventBus.getDefault().register(this);
        /*mContext = this;
        xmppServiceComponent = DaggerXmppServiceComponent.builder()
                .xmppModule(new XmppModule(this, userAppPreference))
                .build();*/
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        Log.i("RiaService", "onStartCommand()");
        onStartService();
        return START_STICKY_COMPATIBILITY;
    }

    /**
     * If the service starts, it means that we don't have yet a connection, roster and so on..
     * The method checks login and password. If login and password exist it starts connecting ,
     * if it is not - it sends auth event to client (probably we don't need it)
     */
    void setConnectingState(boolean isConnecting) {
        this.isConnecting = isConnecting;
        userAppPreference.setConnectingStateKey(isConnecting);
        RiaEventBus.post(isConnecting ? XmppErrorEvent.State.EConnecting : XmppErrorEvent.State.ENotConnecting);
    }

    boolean isConnecting = false;

    void onStartService() {
        if (!isConnecting && doLoginAndPassExist() && NetworkStateManager.isNetworkAvailable(this)) {
            setConnectingState(true);
            bolts.Task.callInBackground(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    smackXmppConnection.tryConnectToServer();
                    smackXmppConnection.tryLoginToServer();
                    connectionHandler.removeCallbacks(connectionRunnable);
                    if (doLoginAndPassExist()) {
                        boolean isOk = smackRosterManager.tryGetRosterFromServer();
                        if (!isOk) {
                            NetworkStateManager.setCurrentUserPresence(new Presence(Presence.Type.unavailable), userAppPreference.getJidStringKey());
                            connectionHandler.postDelayed(connectionRunnable, RiaConstants.GETTING_ROSTER_NEXT_TRY_TIME_OUT);
                        } else {
                            Log.i("RiaService", "everything is ok, we've got roster!!!");
                            setConnectingState(false);
                            if (xmppMessageManager != null) {
                                xmppMessageManager.sendAllNotSentMessages();
                            }
                        }
                    } else {
                        setConnectingState(false);
                        xmppConnection.disconnect();
                        XmppUtils.changeCurrentUserStatus(new Presence(Presence.Type.unavailable), userAppPreference.getJidStringKey(), xmppConnection);
                        stopSelf();
                        //if sign in failed it doesn't sign in second time for some reason
                        //so we try to reinitialise smack modules
                    }
                    return null;
                }
            });
        }
    }


    @Override
    public void onDestroy() {
        Log.i("RiaService", "onDestroy");
        EventBus.getDefault().unregister(this);
        //  mContext = null;
        //userAppPreference.setRiaXmppServiceStartedFlag(false);
        /*synchronized (mActiveNotifications) {
            Iterator<Integer> i = mActiveNotifications.iterator();
            while (i.hasNext()) {
                //It's possible this has already been cleared from the user
                //clicking on the notification, but clearing it here
                //does not hurt anything and is simpler than deleting it
                //out of the Set when it is clicked.
                Integer notificationId = (Integer) i.next();
                getNotifyManager().cancel(notificationId);
            }
            mActiveNotifications.clear();
        }*/
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
