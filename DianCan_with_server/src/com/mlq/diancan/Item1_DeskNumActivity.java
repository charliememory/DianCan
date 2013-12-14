package com.mlq.diancan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.mlq.diancan.R.drawable;
import com.mlq.diancan.R.id;
import com.mlq.diancan.R.layout;
import com.mlq.diancan.R.string;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

/**
* 继承自Activity，<br>
* 用来查看和修改点餐的客户信息：桌号、人数，<br>
* 客户信息使用SharedPreferences来进行保存，<br>
* 若要进行修改，则使用MyDialog_DeskNum类创建修改窗口
* @see android.content.SharedPreferences <br>
* @see com.mlq.diancan.MyDialog_DeskNum
*/
public class Item1_DeskNumActivity extends Activity {

	/**
	* 在第一次载入页面时，只进行布局的初始化
	*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.desk_num);
		
        System.out.println("DeskNumActivity onCreate");
	}

    /**
    * 载入页面时进行 show_desk_num 的更新<br>
    * 可以保证数据更改后，页面能及时显示
    */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//****************  在 resume 中进行 show_desk_num 的更新，可以防止其显示上一次保存的旧数据      *******************
		System.out.println("DeskNumActivity onResume()");
		SharedPreferences sp = Item1_DeskNumActivity.this.getSharedPreferences("DianCan", Context.MODE_PRIVATE);
		String desk_num_str = sp.getString("DESK_NUM", "0");
		String person_num_str = sp.getString("PERSON_NUM", "0");
        TextView show_desk_num = (TextView)findViewById(R.id.desk_num_text2);
        TextView show_person_num = (TextView)findViewById(R.id.desk_num_text4);
        show_desk_num.setText(desk_num_str);
        show_person_num.setText(person_num_str);
        
        Button button_change = (Button)findViewById(R.id.desk_num_change_btn);
        button_change.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MyDialog_DeskNum mdialog = new MyDialog_DeskNum(Item1_DeskNumActivity.this, R.style.MyDialogTheme); 
		        mdialog.show();
		        mdialog.setCancelable(false);
		        mdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface arg0) {
						// TODO Auto-generated method stub
						SharedPreferences sp = Item1_DeskNumActivity.this.getSharedPreferences("DianCan", Context.MODE_PRIVATE);
						String desk_num_str = sp.getString("DESK_NUM", "0");
						String person_num_str = sp.getString("PERSON_NUM", "0");
				        TextView show_desk_num = (TextView)findViewById(R.id.desk_num_text2);
				        TextView show_person_num = (TextView)findViewById(R.id.desk_num_text4);
				        show_desk_num.setText(desk_num_str);
				        show_person_num.setText(person_num_str);
					}
		        });
			}
		});
	}
}
