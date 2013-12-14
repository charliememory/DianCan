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
public class Item2Activity extends Activity {  
//	private Resources resources;
	/**
	* android中存储数据的组件,此处存储了商店信息（店名）、服务员信息（员工号）<br>
	*/
	protected SharedPreferences sp;
	/**
	* 列表视图组件，用于显示已点菜单信息
	*/
	protected ListView list;
	protected TextView sum_cost;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item2);
//        resources = getResources();
        Button back_btn = (Button)findViewById(R.id.item2_back_btn);
        Button jiezhang_btn = (Button)findViewById(R.id.submit_btn_jiezhang);
        final ListView list = (ListView)findViewById(R.id.food_list_yidian);
        final TextView sum_cost = (TextView)findViewById(R.id.yidian_sum_text);
        sp = Item2Activity.this.getSharedPreferences("DianCan", Context.MODE_PRIVATE);
        String waiter_num_str = sp.getString("WAITER_NUM", "0");
        
        //设置返回按钮
        back_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
        
        //为同步按钮设置监听器
        jiezhang_btn.setOnClickListener(new View.OnClickListener() {
			
        	@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
			}
		});
        
	}

//	@Override
//	protected void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//	}
}


