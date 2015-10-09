package com.cainhuang.smackxmpp_demo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.io.IOException;

public class SmackService extends Service {

    public static final String CONNECTED = "com.cainhuang.smackxmpp_demo.smackservice.connected";
    public static final String DISCONNECTED = "com.cainhuang.smackxmpp_demo.smackservice.disconnected";

    private boolean mActive;
    private Thread mThread;
    private Handler mTHandler;
    private SmackConnection connection;

    public static Boolean connected() {
        return SmackConnection.state.equals(SmackConnection.ConnectionState.CONNECTED);
    }

    public SmackService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mActive) {
            mActive = true;

            // Create ConnectionThread Loop
            if (mThread == null || !mThread.isAlive()) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        mTHandler = new Handler();
                        initConnection();
                        Looper.loop();
                    }

                });
                mThread.start();
            }

        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActive = false;
        mTHandler.post(new Runnable() {

            @Override
            public void run() {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
    }

    private void initConnection() {
        if(connection == null) {
            connection = new SmackConnection(this);
        }
        try {
            connection.connect();
        } catch (IOException | SmackException | XMPPException e) {
            e.printStackTrace();
        }
    }

}
