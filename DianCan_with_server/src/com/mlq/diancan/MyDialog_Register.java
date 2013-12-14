package com.mlq.diancan;

import com.mlq.diancan.R.drawable;
import com.mlq.diancan.R.id;
import com.mlq.diancan.R.layout;
import com.mlq.diancan.R.string;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/**
* 继承自Dialog，<br>
* 用于软件注册的窗口，<br>
* 使用SharedPreferences进行信息的存储
* @see android.content.SharedPreferences
*/
public class MyDialog_Register extends Dialog { 
	private Context ctx;
	private String serial_num_str;
	
	public MyDialog_Register(Context context,String serialNum) {  
		super(context); 
		ctx = context;
		serial_num_str = serialNum;
	} 
	public MyDialog_Register(Context context,int style, String serialNum) {  
		super(context, style); 
		ctx = context;
		serial_num_str = serialNum;
	} 
	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.register_dialog);
		final TextView serial_num_tv = (TextView)findViewById(R.id.register_dialog_text);
		final EditText active_num_ev= (EditText)findViewById(R.id.register_dialog_edit);
		SharedPreferences sp = ctx.getSharedPreferences("DianCan", Context.MODE_PRIVATE);
		serial_num_tv.setText(serial_num_str);
		Button button_cancel = (Button)findViewById(R.id.register_dialog_cancel_btn);
		Button button_ok = (Button)findViewById(R.id.register_dialog_ok_btn);
		button_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MyDialog_Register.this.cancel(); //调用 dismiss(), 以及 DialogInterface.OnCancelListener
			}
		});
		
		button_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ctx = MyDialog_Register.this.getContext();       
		        //获取SharedPreferences对象
				SharedPreferences sp = ctx.getSharedPreferences("DianCan", Context.MODE_PRIVATE);
				String active_num_str = active_num_ev.getText().toString();
				if(!active_num_str.equals("")){
					//存入数据,在/data/data/com.test/shared_prefs目录下生成了一个SP.xml文件
			        Editor editor = sp.edit();
			        editor.putString("ACTIVE_NUM", active_num_str);
			        editor.commit();
			        MyDialog_Register.this.dismiss();
				}
			}
		});
	}
}