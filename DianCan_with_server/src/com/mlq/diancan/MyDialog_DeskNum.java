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
/**
* 继承自Dialog，<br>
* 用于进行客户信息（桌号、人数）修改的窗口，<br>
* 使用SharedPreferences进行信息的存储
* @see android.content.SharedPreferences
*/
public class MyDialog_DeskNum extends Dialog { 
	private Context ctx;
	
	public MyDialog_DeskNum(Context context) {  
		super(context); 
		ctx = context;
	} 
	public MyDialog_DeskNum(Context context,int style) {  
		super(context, style); 
		ctx = context;
	} 
	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.desk_num_dialog);
		final EditText desk_num_ev= (EditText)findViewById(R.id.item1_dialog_desk_num);
		final EditText person_num_ev= (EditText)findViewById(R.id.item1_dialog_person_num);
		Button button_cancel = (Button)findViewById(R.id.item1_dialog_cancel_btn);
		Button button_ok = (Button)findViewById(R.id.item1_dialog_ok_btn);
		button_cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MyDialog_DeskNum.this.cancel(); //调用 dismiss(), 以及 DialogInterface.OnCancelListener
			}
		});
		
		button_ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ctx = MyDialog_DeskNum.this.getContext();       
		        //获取SharedPreferences对象
				SharedPreferences sp = ctx.getSharedPreferences("DianCan", Context.MODE_PRIVATE);
				String desk_num_str = desk_num_ev.getText().toString();
				String person_num_str = person_num_ev.getText().toString();
				if(!desk_num_str.equals("") && !person_num_str.equals("")){
					//存入数据,在/data/data/com.test/shared_prefs目录下生成了一个SP.xml文件
			        Editor editor = sp.edit();
			        editor.putString("DESK_NUM", desk_num_str);
			        editor.putString("PERSON_NUM", person_num_str);
			        editor.commit();
			        MyDialog_DeskNum.this.dismiss();
				}
			}
		});
	}
}