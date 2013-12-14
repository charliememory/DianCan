package com.mlq.diancan;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.mlq.diancan.R;
/**
* 继承自Activity，<br>
* 用来进行商店信息（店名）、服务员信息（员工号）的编辑
*/
public class Item3Activity extends Activity {  
//	private Resources resources;
	/**
	* android中存储数据的组件,此处存储了商店信息（店名）、服务员信息（员工号）<br>
	*/
	protected SharedPreferences sp;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item3);
//        resources = getResources();
        final EditText restaurant_name_ev = (EditText)findViewById(R.id.item3_edit1);
        final EditText waiter_num_ev = (EditText)findViewById(R.id.item3_edit2);
        final Button ok_btn = (Button)findViewById(R.id.item3_ok_btn);
        Button back_btn = (Button)findViewById(R.id.item3_back_btn);
        sp = Item3Activity.this.getSharedPreferences("DianCan", Context.MODE_PRIVATE);
        String restaurant_name_str = sp.getString("RESTAURANT_NAME", "XXX店");
        String waiter_num_str = sp.getString("WAITER_NUM", "0");
        waiter_num_ev.setText(waiter_num_str);
        restaurant_name_ev.setText(restaurant_name_str);
        
        //设置返回按钮
        back_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
        
        //为同步按钮设置监听器
        ok_btn.setOnClickListener(new View.OnClickListener() {
			
        	@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
        		String restaurant_name_str = restaurant_name_ev.getText().toString();
        		String waiter_num_str = waiter_num_ev.getText().toString();
        		Editor editor = sp.edit();
		        editor.putString("RESTAURANT_NAME", restaurant_name_str);
		        editor.putString("WAITER_NUM", waiter_num_str);
		        editor.commit();
			}
		});
        
	}

//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//	}
}


