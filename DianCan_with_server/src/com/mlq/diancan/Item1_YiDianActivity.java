package com.mlq.diancan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.mlq.diancan.R.drawable;
import com.mlq.diancan.R.id;
import com.mlq.diancan.R.layout;
import com.mlq.diancan.R.string;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
/**
* 继承自Activity，<br>
* 用来进行查看已点菜目，并进行修改和提交
*/
public class Item1_YiDianActivity extends Activity {
	private final String table_name = Constant.table_yidian;
    private Resources resources;
    
//    /**
//	* 打印机数据打印端口
//	*/
//	protected static final int dstPort = 9100;
//	/**
//	* 打印机测试打印端口
//	*/
//	protected static final int statusTestPort = 4000;
//	/**
//	* 打印机IP地址
//	*/
//	protected final String ServerIP = "192.168.1.200";
//	/**
//	* 数据库文件路径，存储点餐信息
//	*/
//	protected static final String dbPath_DianCan = Environment.getExternalStorageDirectory().getPath()
//										+ "/DianCan/db/DianCan.db3"; 
	private static final String deleteALL = new String("deleteAll");
	/**
	* 操作数据库的对象
	*/
	protected SqliteDbHelper dbHelper;
	/**
	* 列表视图组件，用于显示已点菜单信息
	*/
	protected ListView list;
	protected TextView sum_cost;
	/**
	* 遍历数据库游标对象
	*/
    protected Cursor cursor;
	/**
	* android提供的Handler线程机制，可自行维护一个threadLooper
	* @see android.os.HandlerThread
	*/
    protected HandlerThread threadSocket;
    /**
	* android提供的Handler类具有两种功能：<br>
	* (1) to schedule messages and runnables to be executed as some point in the future;<br>
	* (2) to enqueue an action to be performed on a different thread than your own.<br> 
	* handlerSocket使用第一种功能，将runnables放入threadSocket生成的threadLooper队列中执行
	* @see android.os.Handler
	*/
    protected Handler handlerSocket;
    /**
	* android提供的Handler类具有两种功能：<br>
	* (1) to schedule messages and runnables to be executed as some point in the future;<br>
	* (2) to enqueue an action to be performed on a different thread than your own.<br> 
	* handlerSocket使用第二种功能，完成不同线程间的通信
	* @see android.os.Handler
	*/
    protected Handler handler;
	/**
	* 继承线程接口，实现向Server端发送点餐信息
	* @see java.lang.Runnable
	*/
	protected Runnable runServerSubmit;
	/**
	* 继承线程接口，实现向打印机提交数据的功能
	* @see java.lang.Runnable
	*/
	protected Runnable runPrintSubmit;
	/**
	* 继承线程接口，实现测试打印机的功能
	* @see java.lang.Runnable
	*/
	protected Runnable runPrintTest;
	private DatagramSocket socket;
	private InetAddress serverAddress;
	private InetAddress printAddress;
	private String restaurant_name_str;
	private String waiter_num_str;
	private String desk_num_str;
	private String person_num_str;
	/**
	* 利用android本身的SharedPreferences类来进行商家信息（店名）、客户信息（台号、人数）的存储
	*/
	protected SharedPreferences sp;
	/**
	* 向Server进行数据提交的UDP-socket对象
	*/
	protected DatagramSocket sServerSubmit;
	private DatagramPacket mDatagramSend;
	private DatagramPacket mDatagramReceive;
	/**
	* 向打印机进行数据提交的TCP-socket对象
	*/
	protected Socket sPrintSubmit;
	/**
	* 对打印机进行测试的TCP-socket对象
	*/
	protected Socket sPrintTest;
	
	/**
	* 在第一次载入页面时，进行UI布局的初始化，商家信息、客户信息载入，<br>
	* 并检测数据库文件是否已存在
	*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item2);
        sp = Item1_YiDianActivity.this.getSharedPreferences("DianCan", Context.MODE_PRIVATE);
        restaurant_name_str = sp.getString("RESTAURANT_NAME", "XXX店");
        waiter_num_str = sp.getString("WAITER_NUM", "0");
        desk_num_str = sp.getString("DESK_NUM", "0");
        person_num_str = sp.getString("PERSON_NUM", "0");
      //*************初始化     list ，包括检测文件和目录是否存在，以及数据库操作   *************
  		File mFile = new File(Constant.DbPath); 
  		System.out.println("make mFile at " + Constant.DbPath);
  		if(!mFile.exists()) 
  		{ 
  			File mDirPath = mFile.getParentFile(); //new File(vFile.getParent()); 
  			mDirPath.mkdirs(); 
  			System.out.println("make mDir /sdcard/DianCan/db ");
  		} 
	}
    /**
	* 载入页面时，进行按钮组件监听器的初始化，<br>
	* 并对ListView组件的更新，<br>
	* 进行提交数据、测试打印机线程的实现
	*/
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		dbHelper = new SqliteDbHelper(Constant.DbPath);
        //初始化 Fragment 界面
        resources = getResources();
        list = (ListView)findViewById(R.id.food_list_yidian);
        sum_cost = (TextView)findViewById(R.id.yidian_sum_text);
		try {
			ArrayList <HashMap <String, Object>> mAppList = dbHelper.selectAll(table_name);
			//更新ListView组件信息
			inflateList(mAppList, list);
		}catch(SQLiteException  se){
			System.out.println("no such table");
			//执行DDL创建数据表
			dbHelper.creatDbTable(table_name);
		}

//  		handler = new Handler(this.getMainLooper()) {
//
//    		@Override
//    		public void handleMessage(Message msg) {
//    			// TODO Auto-generated method stub
//    			super.handleMessage(msg);
//    			System.out.println("Handler receive msg");
//    			if(((String)msg.obj).equals(deleteALL)){
//    				try {
//    					cursor = dbHelper.deleteAllData(table_name);
//    					inflateList(cursor, list);
//    				}catch(SQLiteException  se){
//    					System.out.println("no such table");
//    					//执行DDL创建数据表
//    					dbHelper.creatDbTable(table_name);
//    				}
//    			}
//    		}
//        };
  		final Button submit_btn = (Button)findViewById(R.id.submit_btn_yidian);
  		Button deleteall_btn = (Button)findViewById(R.id.deleteall_btn_yidian);
  		submit_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("-4");
				dbHelper.DB2XML(new File(Constant.DianCanXmlPath), table_name);
				handlerSocket.post(runServerSubmit);
			}
		});
  		deleteall_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					ArrayList <HashMap <String, Object>> mAppList = dbHelper.deleteAllData(table_name);
					//更新ListView组件信息
					inflateList(mAppList, list);
				}catch(SQLiteException  se){
					System.out.println("no such table");
					//执行DDL创建数据表
					dbHelper.creatDbTable(table_name);
				}
			}
		});
  		
  		//TCP通信
		threadSocket = new HandlerThread("socket");
		threadSocket.start();
		handlerSocket = new Handler(threadSocket.getLooper());
		runServerSubmit =new Runnable(){	
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int count = 0;
				TcpSocket socket = new TcpSocket(Constant.ServerIP, Constant.ServerTCPPort);
//				try {
//					sServerSubmit = new DatagramSocket();
//				} catch (SocketException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				try {      //设置receive阻塞时间
//					sServerSubmit.setSoTimeout(3000);
//				} catch (SocketException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				try {
//					serverAddress=InetAddress.getByName(Constant.ServerIP);
//				} catch (UnknownHostException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				//******************** 构建要发送的点餐信息  ****************
//				String dianCan_send = "DianCan";
//				SharedPreferences sp = Item1_YiDianActivity.this.getSharedPreferences("DianCan", Context.MODE_PRIVATE);
//				dianCan_send = dianCan_send + "#" + sp.getString("DESK_NUM", "0");
//				dianCan_send = dianCan_send + "#" + sp.getString("PERSON_NUM", "0");
//	            try {
//	    			dbHelper.selectAll(table_name);
//	    		}catch(SQLiteException  se){
//	    			System.out.println("no such table");
//					dbHelper.creatDbTable(table_name);
//	    		}
//	            cursor.moveToFirst();
//	            String food_name,food_count;
//	    		for ( int i= 0; i<cursor.getCount(); i++ ) 
//	            { 
//	    			food_name = cursor.getString(1);
//	                food_count = cursor.getString(3);
//					dianCan_send = dianCan_send + "#" + food_name + ";" + food_count;
//	                cursor.moveToNext();
//	            }
//	    		cursor.close();  //游标使用完记得关闭，否则会有数据库未关闭的错误
//				byte dataToSend [] = dianCan_send.getBytes();
//				mDatagramSend = new DatagramPacket(dataToSend, dataToSend.length, 
//							serverAddress, Constant.ServerUDPPort);
//				byte dataReceive [] = new byte[256];  //创建一个空的DatagramPacket对象
//				mDatagramReceive = new DatagramPacket(dataReceive,dataReceive.length);
				while(count > Constant.SendTimes) {
					try {
						sServerSubmit.send(mDatagramSend);
						sServerSubmit.receive(mDatagramReceive);
					    String strReceive = new String(mDatagramReceive.getData(),
					    		mDatagramReceive.getOffset(),mDatagramReceive.getLength());
					    if(strReceive.equals("ok")){
					    	submit_btn.setText("已提交");
					    	break;
					    }
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Toast.makeText(Item1_YiDianActivity.this, "请求超时，提交失败", Toast.LENGTH_SHORT).show();
				}
				sServerSubmit.close();
			}
		};
		System.out.println("onResume yidian");
	}
	/**
	* 更新ListView组件,以ListButtonAdapter3类为适配器<br>
	* @param mAppList 数据源
	* @param list 显示数据的ListView组件
	*/
    protected void inflateList(ArrayList <HashMap <String, Object>> mAppList, ListView list)
	{
		// 生成适配器的Item和动态数组对应的元素  
		ListButtonAdapterB adapter = new ListButtonAdapterB( 
				this , 
				Constant.table_yidian,
				dbHelper,
				mAppList, //数据源  
				R.layout.item1_list_item, 
				new String[]{"_id", "food_name", "food_cost", "food_count"} , 
				new int[]{R.id.food_num1, R.id.food_name1, R.id.food_cost1, R.id.food_diancan, 
					R.id.food_cancel} 
        );
		list.setAdapter(adapter);
		// 计算总的价格，并进行显示更新
		int sum = 0;
		for(HashMap <String, Object> map:mAppList){
            String food_cost = ( String ) map.get ("food_cost");
            String food_count = ( String ) map.get ("food_count");
            sum += Integer.valueOf(food_cost) * Integer.valueOf(food_count);
		}
		sum_cost.setText(String.valueOf(sum) + this.getResources().getString(R.string.money_unit));
	}
    /**
	* 退出页面时关闭数据库对象
	*/
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//退出程序时关闭SQLiteDatabase
		dbHelper.close();
		System.out.println("onPause yidian");
	}
}
