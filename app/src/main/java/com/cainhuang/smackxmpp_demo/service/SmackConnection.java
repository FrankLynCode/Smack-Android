package com.cainhuang.smackxmpp_demo.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cainhuang.smackxmpp_demo.R;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

public class SmackConnection implements ConnectionListener {

    public static String TAG = "SmackConnection";

    public enum ConnectionState {
        CONNECTED, CONNECTING, RECONNECTING, DISCONNECTED
    }

    public static ConnectionState state = ConnectionState.DISCONNECTED;

    private Integer port;
    private String host;
    private String serviceName;
    private String username;
    private String password;
    private XMPPTCPConnection xmppConnection;
    private Context context;

    public SmackConnection(Context ctx) {
        context = ctx;
        Resources res = ctx.getResources();
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx.getApplicationContext());
        host = res.getString(R.string.smack_host);
        port = Integer.parseInt(res.getString(R.string.smack_port));
        serviceName = ctx.getResources().getString(R.string.smack_serviceName);
        username = sp.getString("username", null);
        password = sp.getString("password", null);
    }

    public void connect() throws IOException, XMPPException, SmackException {
        Log.i(TAG, "connect()");

        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration
                .builder()
                .setHost(host)
                .setPort(port)
                .setServiceName(serviceName)
                .setUsernameAndPassword(username, password)
                .build();


        xmppConnection = new XMPPTCPConnection(config);

        xmppConnection.addConnectionListener(this);

        xmppConnection.connect();
        SmackConnection.state = ConnectionState.CONNECTING;
        xmppConnection.login();
    }

    public void disconnect() {
        Log.i(TAG, "disconnect()");
        if (xmppConnection != null) {
            xmppConnection.disconnect();
        }

        xmppConnection = null;
    }

    @Override
    public void connected(XMPPConnection connection) {
        Log.i(TAG, "connected()");
        SmackConnection.state = ConnectionState.CONNECTED;
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        Log.i(TAG, "authenticated()");
        SmackConnection.state = ConnectionState.CONNECTED;

        Intent intent = new Intent(SmackService.CONNECTED);
        intent.setPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        context.sendBroadcast(intent);
    }

    @Override
    public void connectionClosed() {
        Log.i(TAG, "connectionClosed()");
        SmackConnection.state = ConnectionState.DISCONNECTED;

        Intent intent = new Intent(SmackService.DISCONNECTED);
        intent.setPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        context.sendBroadcast(intent);
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        Log.i(TAG, "connectionClosedOnError()");
        SmackConnection.state = ConnectionState.DISCONNECTED;
    }

    @Override
    public void reconnectingIn(int seconds) {
        Log.i(TAG, "reconnectingIn()");
        SmackConnection.state = ConnectionState.RECONNECTING;
    }

    @Override
    public void reconnectionSuccessful() {
        Log.i(TAG, "reconnectionSuccessful()");
        SmackConnection.state = ConnectionState.CONNECTED;
    }

    @Override
    public void reconnectionFailed(Exception e) {
        Log.i(TAG, "reconnectionFailed()");
        SmackConnection.state = ConnectionState.DISCONNECTED;
    }
}
