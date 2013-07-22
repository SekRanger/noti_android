package com.ameebaa.noti;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog.Calls;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	private static String TAG = "noti";
    
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button b = (Button)findViewById(R.id.button1);
        b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String android_id = Secure.getString(MainActivity.this.getContentResolver(), Secure.ANDROID_ID);
				String state = "RINGING";
				String incoming_number = "0894857223";
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
		});
    }
}
