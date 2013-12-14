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
* 用于进行菜单信息（菜目）删除的窗口，<br>
* 使用SQLiteDatabase进行信息的存储
* @see android.database.sqlite.SQLiteDatabase
*/
public class MyDialog_MenuDelete extends Dialog { 
	private Context ctx;
	private List<String> spanner_list = new ArrayList<String>(); 
	private Resources resources;
	private TextView food_kind_text;
	private TextView food_name_text;
	private TextView food_cost_text;
	private String food_kind_old;
	private String food_name_old;
	private String food_cost_old;
	private SQLiteDatabase db;
	private Cursor cursor;
	private Handler handerHandler;//用  handler 来解决刷新  ListView 的界面问题
	private int position;
	
	public MyDialog_MenuDelete(Context context, SQLiteDatabase database, 
			String food_kind, String food_name, String food_cost, Handler handler, int pos) {  
		super(context); 
		ctx = context;
		db = database;
		food_kind_old = food_kind;
		food_name_old = food_name;
		food_cost_old = food_cost;
		handerHandler = handler;
		position = pos;
	} 
	public MyDialog_MenuDelete(Context context, int style, SQLiteDatabase database, 
			String food_kind, String food_name, String food_cost, Handler handler, int pos) {  
		super(context, style); 
		ctx = context;
		db = database;
		food_kind_old = food_kind;
		food_name_old = food_name;
		food_cost_old = food_cost;
		handerHandler = handler;
		position = pos;
	} 
	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.menu_delete_dialog);
		resources = ctx.getResources();
		
        
        food_kind_text= (TextView)findViewById(R.id.menu_delete_dialog_text1_2);
		food_name_text= (TextView)findViewById(R.id.menu_delete_dialog_text2_2);
		food_cost_text= (TextView)findViewById(R.id.menu_delete_dialog_text3_2);

        food_kind_text.setText(food_kind_old);
        food_name_text.setText(food_name_old);
        food_cost_text.setText(food_cost_old);
        
		Button button_cancel = (Button)findViewById(R.id.menu_delete_dialog_cancel_btn);
		Button button_ok = (Button)findViewById(R.id.menu_delete_dialog_ok_btn);
		button_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MyDialog_MenuDelete.this.cancel(); //调用 dismiss(), 以及 DialogInterface.OnCancelListener
			}
		});
		
		button_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				deleteData(db, food_kind_old, food_name_old);
				//用  handler 来解决刷新  ListView 的界面问题
				Message msg = handerHandler.obtainMessage();
		        msg.arg1 = 0;
		        msg.arg2 = position;
		        handerHandler.sendMessage(msg);
		        
		        MyDialog_MenuDelete.this.dismiss();
			}
		});
	}
	//***********  对于数据库 SQLite 的操作函数   **********
	private void insertData(SQLiteDatabase db, String tableName, String food_name, String food_cost)
	{
		//执行插入语句
		db.execSQL("insert into " + tableName + " values(null , ?, ?)" , 
					new String[]{food_name, food_cost});
	}
	private void deleteData(SQLiteDatabase db, String tableName, String food_name)
  	{
  		//执行删除语句，  注意单引号
  		db.execSQL("delete from " + tableName + " where food_name = '" + food_name + "'");
  	}
}