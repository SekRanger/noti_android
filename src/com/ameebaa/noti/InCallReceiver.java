package com.ameebaa.noti;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.util.Log;

public class InCallReceiver extends BroadcastReceiver {
	private static String TAG = "noti";
	private boolean isPhoneCalling = false;
    private boolean isRinging = false;
    
    private static String getBroadcast() throws SocketException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements();) {
            NetworkInterface ni = niEnum.nextElement();
            if (!ni.isLoopback()) {
                for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {
                	
                	InetAddress i = interfaceAddress.getBroadcast();
                	if(i==null){
                		continue;
                	}
                	Log.v(TAG, "--" + interfaceAddress.getBroadcast().toString() + "--");
                    return interfaceAddress.getBroadcast().toString().substring(1);
                }
            }
        }
        return null;
    }
    
	@Override
	public void onReceive(Context context, Intent intent) {
		//Log.d(TAG, "InCallReceiver.onReceive" + intent.toString());
		//Set<String> s = intent.getExtras().keySet();
		String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		String state = intent.getStringExtra("state");
		String incoming_number = intent.getStringExtra("incoming_number");
		DatagramSocket socket = null;
		try {
			String data = android.text.TextUtils.join("\u0008", new String[]{android_id , android.os.Build.MODEL , state , incoming_number});
			//Log.v(TAG, data);
			String broadcastAddress = getBroadcast();
			socket = new DatagramSocket(60069);
			Log.v(TAG, broadcastAddress);
			DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(), InetAddress.getByName(broadcastAddress), 60069);
			socket.send(packet);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			try{
				socket.close();
				socket.disconnect();
			}
			catch(Exception e){
			}
		}
	}
}
