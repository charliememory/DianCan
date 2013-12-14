package com.mlq.diancan;

import java.util.ArrayList;
import java.util.List;

import com.mlq.diancan.R.drawable;
import com.mlq.diancan.R.id;
import com.mlq.diancan.R.layout;
import com.mlq.diancan.R.string;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
/**
* 继承自Dialog，<br>
* 用于进行菜单信息（菜目）增加的窗口，<br>
* 使用SQLiteDatabase进行信息的存储
* @see android.database.sqlite.SQLiteDatabase
*/
public class MyDialog_MenuAdd extends Dialog { 
	private Context ctx;
	private List<String> spinner_list = new ArrayList<String>(); 
	private Resources resources;
//	private ArrayAdapter<String> adapter;
	private TextView table_name_show_tv;
//	private EditText food_kind_edit;
	private EditText food_name_edit;
	private EditText food_cost_edit;
//	private String food_kind;
	private String food_name;
	private String food_cost;
	private SQLiteDatabase db;
	private Handler handerHandler;//用  handler 来解决刷新  ListView 的界面问题
	private Cursor cursor;
	private String table_name;
	private String table_name_show;
    private final String food_kind_table = new String("食物种类");
	
	public MyDialog_MenuAdd(Context context, SQLiteDatabase database, 
			String food_kind, String food_kind_show, Handler handler) {  
		super(context); 
		ctx = context;
		db = database;
		table_name = food_kind;
		table_name_show = food_kind_show;
		handerHandler = handler;
	} 
	public MyDialog_MenuAdd(Context context, int style, SQLiteDatabase database, 
			String food_kind, String food_kind_show, Handler handler) {  
		super(context, style); 
		ctx = context;
		db = database;
		table_name = food_kind;
		table_name_show = food_kind_show;
		handerHandler = handler;
	} 
	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.menu_add_dialog);
		resources = ctx.getResources();
		//第一步：添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项 
//		try {
//			cursor = db.rawQuery("select * from " + food_kind_table, null);
//		}catch(SQLiteException  se){
//			System.out.println("no such table");
//			//执行DDL创建数据表
//			db.execSQL("create table " + food_kind_table + "(_id integer primary key autoincrement,"
//					+ "food_kind_name varchar(10)," + "food_kind_name_show varchar(20);");
//		}
//		cursor.moveToFirst();
//    	for ( int i= 0; i<cursor.getCount(); i++ ) 
//        { 
//    		spinner_list.add(cursor.getString(1));
//            cursor.moveToNext();
//        }
//		spinner_list.add("面食"); 
//		spinner_list.add("饺子"); 
//		spinner_list.add("小菜"); 
//		spinner_list.add("饮料"); 
//		spinner_list.add("自定义");
//        adapter = new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_item, spinner_list);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
//        spinner.setAdapter(adapter);  
//        spinner.setPrompt(resources.getString(R.string.food_kind));  
//        spinner.setSelection( spinner_list.indexOf(table_name) );
//        spinner.setOnItemSelectedListener(new OnItemSelectedListener(){  
//      	  
//            @Override  
//            public void onItemSelected(AdapterView<?> AdapterView, View view, int position,  
//                    long arg3) {  
//                // TODO Auto-generated method stub  
//            	food_kind = AdapterView.getItemAtPosition(position).toString();
//                if( food_kind.equals("自定义") ){
//                	food_kind_custom.setTextColor(resources.getColor(R.color.black));
//                	food_kind_edit.setTextColor(resources.getColor(R.color.black));
//                	food_kind_edit.setEnabled(true);
//                	food_kind_edit.setFocusable(true);
////                	System.out.println("in if, food_kind is: " + food_kind);
//                }else{
//                	food_kind_custom.setTextColor(resources.getColor(R.color.lightgrey));
//                	food_kind_edit.setTextColor(resources.getColor(R.color.lightgrey));
//                	food_kind_edit.setEnabled(false);
////                	food_kind_edit.setFocusable(false);  //为何一设置成 false 就没法再设置成 true ？
////                	System.out.println("in else, food_kind is: " + food_kind);
//                }
//            }  
//          
//            @Override  
//            public void onNothingSelected(AdapterView<?> arg0) {  
//                // TODO Auto-generated method stub  
//                System.out.println("NothingSelected");  
//            }
//        }); 
//        
//        food_kind_custom = (TextView)findViewById(R.id.menu_edit_dialog_text2);
//        food_kind_edit= (EditText)findViewById(R.id.menu_edit_dialog_edit2);
    	table_name_show_tv = (TextView)findViewById(R.id.menu_add_dialog_text2);
    	table_name_show_tv.setText(table_name_show);
		food_name_edit= (EditText)findViewById(R.id.menu_add_dialog_edit3);
		food_cost_edit= (EditText)findViewById(R.id.menu_add_dialog_edit4);
		Button button_cancel = (Button)findViewById(R.id.menu_add_dialog_cancel_btn);
		Button button_ok = (Button)findViewById(R.id.menu_add_dialog_ok_btn);
		button_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MyDialog_MenuAdd.this.cancel(); //调用 dismiss(), 以及 DialogInterface.OnCancelListener
			}
		});
		
		button_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				if( food_kind.equals("自定义") ){
//                	food_kind_custom.setTextColor(resources.getColor(R.color.black));
//                	food_kind = food_kind_edit.getText().toString();
//                }
				food_name = food_name_edit.getText().toString();
				food_cost = food_cost_edit.getText().toString();
				if(!food_name.equals("") && !food_cost.equals("")){
					insertData(db, table_name, food_name, food_cost);
//					System.out.println("insertData food_name:" + food_name + " food_cost:" + food_cost);
					//用  handler 来解决刷新  ListView 的界面问题
					//此处不能用  table_name==food_kind 来进行判断，因为String类没有重载==操作符
//					if(table_name.equals(food_kind)){
						Message msg = handerHandler.obtainMessage();
				        msg.arg1 = 2; //arg1表操作的类型
				        msg.obj = food_name + "," + food_cost;
				        handerHandler.sendMessage(msg);
//					}
			        MyDialog_MenuAdd.this.dismiss();
				}
			}
		});
	}
	//***********  对于数据库 SQLite 的操作函数   **********
	private void insertData(SQLiteDatabase db, String tableName, String food_name, String food_cost)
	{
		//检测该表是否存在
		try {
			db.rawQuery("select * from " + tableName, null);
		}catch(SQLiteException  se){
			System.out.println("no such table");
			//执行DDL创建数据表
			db.execSQL("create table " + tableName + "(_id integer primary key autoincrement,"
					+ "food_name varchar(20)," + "food_cost varchar(10));");
		}
		try {
			//检测“食物种类”表中是否存有该表的表名
			Cursor mcursor = db.rawQuery("select * from " + food_kind_table + 
						" where food_kind_name = '" + tableName + "'", null);
			if(0 == mcursor.getCount()){
				System.out.println("0 == mcursor.getCount()");
	        	db.execSQL("insert into " + food_kind_table + " values(null , ?)" , 
						new String[]{tableName});
	        }
			//执行插入语句
			db.execSQL("insert into " + tableName + " values(null , ?, ?)" , 
						new String[]{food_name, food_cost});
		}catch(SQLiteException  se){
			System.out.println("no such table");
			//执行DDL创建数据表
			db.execSQL("create table " + food_kind_table + "(_id integer primary key autoincrement,"
					+ "food_kind_name varchar(20));");
		}
	}
	private void deleteData(SQLiteDatabase db, String tableName, String food_name)
  	{
  		//执行删除语句，  注意单引号
  		db.execSQL("delete from " + tableName + " where food_name = '" + food_name + "'");
  	}
}