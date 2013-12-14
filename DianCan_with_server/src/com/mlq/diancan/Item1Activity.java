package com.mlq.diancan;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.mlq.diancan.R;

/**
* 继承自ActivityGroup，<br>
* 使用TabHost组件包含3个Activity（Item1_DeskNumActivity、Item1_FenChuActivity、Item1_YiDianActivity），<br>
* Item1_DeskNumActivity用来修改点餐的客户信息<br>
* Item1_FenChuActivity用来进行点餐<br>
* Item1_YiDianActivity用来进行查看已点菜目，并进行修改和提交
*/
public class Item1Activity extends ActivityGroup {  
	//使用自定义tabhost需要继承继承ActivityGroup，并调用调用setup(activityGroup)
//	public static final int dstPort = 9100;
//	public final String ServerIP = "192.168.1.200";
//	private ListView list;
//	private Button button_update;
//	private Cursor cursor;
//	private byte by[];
//	private String strSend;
//	private byte cut[] = {0x1d,0x56,66,0};
//	private byte fontSize0[] = {0x1d,0x21,0x00};
//	private byte fontSize1[] = {0x1d,0x21,0x11};
//	private byte fontSize2[] = {0x1d,0x21,0x22};
//	private byte fontSize3[] = {0x1d,0x21,0x33};
//	private byte fontSize4[] = {0x1d,0x21,0x44};
//	private byte fontSize5[] = {0x1d,0x21,0x55};
	/**
	* 容纳多个Activity的容器
	*/
	private TabHost mTabHost;
//	private String desk_num_str;
	/**
	* 在第一次载入页面时，进行TabHost组件的初始化
	*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item1);
        //使用自定义的对话框输入桌号
        MyDialog_DeskNum mdialog = new MyDialog_DeskNum(Item1Activity.this, R.style.MyDialogTheme); 
        mdialog.show();
        mdialog.setCancelable(false);
        
        Button button_back = (Button)findViewById(R.id.item1_back_btn);
//        Button button_submit = (Button)findViewById(R.id.item1_submit_btn);
		button_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
//		button_submit.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				handlerSocket.post(socketRunSubmit);
//			}
//		});
		
		//添加自定义的TabHost
		mTabHost = (TabHost)findViewById(R.id.tabhost);
		//使用自定义tabhost需要继承继承ActivityGroup，并调用调用setup(activityGroup)
		mTabHost.setup(this.getLocalActivityManager());
		Intent intent1 = new Intent(this,Item1_DeskNumActivity.class);
		createTab( getString(R.string.desk_num),intent1 );
		Intent intent2 = new Intent(this, Item1_CaiDanActivity.class);
		createTab( getString(R.string.cai_dan),intent2 );
		Intent intent3 = new Intent(this, Item1_YiDianActivity.class);
		createTab( getString(R.string.yi_dian),intent3 );
		Intent intent4 = new Intent(this, Item1_TuiJianActivity.class);
		createTab( getString(R.string.tui_jian),intent4 );
		mTabHost.setCurrentTab(1);
		System.out.println("mTabHost set finish");
		//TCP通信
//		threadSocket = new HandlerThread("socket");
//		threadSocket.start();
//		handlerSocket = new Handler(threadSocket.getLooper());
//		socketRunSubmit =new Runnable(){	
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				try {
//					serverAddress = InetAddress.getByName(ServerIP);//创建一个InetAddree
//				} catch (UnknownHostException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				try {  
//		            Socket s = new Socket(serverAddress, dstPort);  
//		            // outgoing stream redirect to socket  重定向
//		            OutputStream out = s.getOutputStream();  
//		            // 注意第二个参数据为true将会自动flush，否则需要需要手动操作output.flush()  
//		            PrintWriter output = new PrintWriter(out, true);
//		            output.println(new String(fontSize0));
//		            output.println("fontSize0");
//		            output.println(new String(cut));
//		            // read line(s)  
////		            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));  
////		            Toast.makeText(Item1Activity.this, "wait for read", Toast.LENGTH_SHORT).show();
////		            String message = input.readLine();  
////		            Toast.makeText(Item1Activity.this, "read finished", Toast.LENGTH_SHORT).show();
////		            System.out.println("Client receive is: " + message);  
//		            s.close();  
////		            Toast.makeText(Item1Activity.this, getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
//		        } catch (UnknownHostException e) {  
//		        	Toast.makeText(Item1Activity.this, getString(R.string.submit_fail), Toast.LENGTH_SHORT).show();
//		            e.printStackTrace();  
//		        } catch (IOException e) {  
//		        	Toast.makeText(Item1Activity.this, getString(R.string.submit_fail), Toast.LENGTH_SHORT).show();
//		            e.printStackTrace();  
//		        } 
//			}
//		};
	}
	/**
	* 向TabHost组件中添加一个tab
	* @param text tab的名字
	* @param intent tab所对应的Intent
	*/
    protected void createTab(String text ,Intent intent){          
		mTabHost.addTab(mTabHost.newTabSpec(text)
				.setIndicator(createTabView(text))
				.setContent(intent));
	}
	/**
	* 依据text创建tab中用到的Indicator，用TextView组件实现
	* @param text tab名字
	* @return view 创建的Indicator
	*/
    protected View createTabView(String text) {
		View view = LayoutInflater.from(this).inflate(R.layout.tab_indicator, null);
		TextView tv = (TextView) view.findViewById(R.id.text_tab);
		tv.setText(text);
		return view;
	}
}


