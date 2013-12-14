package com.mlq.diancan;

import java.util.ArrayList;
import java.util.HashMap;

import com.mlq.diancan.R.drawable;
import com.mlq.diancan.R.id;
import com.mlq.diancan.R.layout;
import com.mlq.diancan.R.string;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
/**
* 继承自BaseAdapter，<br>
* 利用数据源等信息对UI布局进行初始化，<br>
* 基本和ListButtonAdapter1相同，只是作为Item1_YiDianActivity中的数据适配器，item中的控件有些不同
* @see com.mlq.diancan.ListButtonAdapterA
* @see com.mlq.diancan.Item1_YiDianActivity
*/
public class ListButtonAdapterB extends BaseAdapter { 	
    private ArrayList < HashMap < String , Object > > mAppList; 
    private int layoutResource;
    private LayoutInflater mInflater; 
    private Context ctx; 
    private String table_name;
    private String [ ] keyString; 
    private int [ ] valueViewID; 
    private buttonViewHolder holder;
    private Cursor cursor;
    private SqliteDbHelper dbHelper;  
    private final String ex = new String(" X  ");
    
    private class buttonViewHolder { 
        TextView food_num; 
        TextView food_name;
        TextView food_cost;
        Button button_diancan;
        Button button_cancel; 
    } 
    
    public ListButtonAdapterB( Context c, String mtable_name, SqliteDbHelper mdbHelper, 
    		ArrayList < HashMap < String , Object > > appList,
    		int resource, String [ ] key , int [ ] ID) { 
        ctx = c; 
        table_name = mtable_name;
        mAppList = appList; 
        dbHelper = mdbHelper;
        layoutResource = resource;
        mInflater = ( LayoutInflater) ctx. getSystemService( Context . LAYOUT_INFLATER_SERVICE) ; 
        keyString = new String [ key . length ] ; 
        valueViewID = new int [ ID. length ] ; 
        System . arraycopy ( key , 0, keyString, 0, key.length ) ; 
        System . arraycopy ( ID, 0, valueViewID, 0, ID.length ) ; 
    } 
    
    @Override 
    public int getCount ( ) { 
        return mAppList.size ( ) ; 
    } 

    @Override 
    public Object getItem ( int position ) { 
        return mAppList.get ( position ) ; 
    } 

    @Override 
    public long getItemId( int position ) { 
        return position ; 
    } 

    public void removeItem ( int position ) { 
        mAppList.remove( position ) ; 
        this.notifyDataSetChanged( ) ; 
    } 
    
    @Override 
    public View getView ( int position , View convertView, ViewGroup parent ) { 
        if ( convertView != null ) { 
            holder = ( buttonViewHolder) convertView. getTag ( ) ; 
        } else { 
            convertView = mInflater. inflate ( layoutResource, null ) ; 
            holder = new buttonViewHolder( ) ; 
            holder.food_num = (TextView ) convertView.findViewById( valueViewID[ 0] ) ; 
            holder.food_name = (TextView) convertView.findViewById( valueViewID[ 1] ) ; 
            holder.food_cost = (TextView) convertView.findViewById( valueViewID[ 2] ) ; 
            holder.button_diancan = (Button) convertView.findViewById( valueViewID[ 3] ) ; 
            holder.button_cancel = (Button) convertView.findViewById( valueViewID[ 4] ) ; 
            convertView. setTag( holder) ; 
        } 
        //HashMap	中存的数据类型是	String、Integer，对于	TextView	直接存值，对于	Button	等存	id
        HashMap< String , Object > map = mAppList.get( position ) ; 
        if ( map != null ) { 
        	int _id =  (Integer) map.get ( keyString[ 0] ) ; 
        	String food_name = ( String ) map.get ( keyString[ 1] ) ; 
            String food_cost = ( String ) map.get ( keyString[ 2] ) ;
            String food_count = ( String ) map.get ( keyString[ 3] ) ;
            int mposition = position + 1; //position is indexed from zero
            holder.food_num.setText("" + mposition) ; 
            holder.food_name.setText(food_name) ;
            holder.food_cost.setText(food_cost + ctx.getResources().getString(R.string.money_unit)) ;
            holder.button_diancan.setText(ex + food_count);
            holder.button_diancan.setOnClickListener( new lvButtonListener_diancan(_id, food_name, holder.button_diancan, food_cost) ) ; 
            holder.button_cancel.setOnClickListener( new lvButtonListener_cancel(_id, food_name, holder.button_diancan, position) ) ; 
        }         
        return convertView; 
    } 

    class lvButtonListener_diancan implements OnClickListener { 
        private String mfood_name;
        private String mfood_cost;
        private int m_id;
        private Button mybtn;
    	int food_count = 0;
        lvButtonListener_diancan(int _id, String food_name, Button btn, String food_cost){
        	mfood_name = food_name;
        	mfood_cost = food_cost;
        	m_id = _id;
        	mybtn = btn;
        }
        
        @Override 
        public void onClick( View v) { 
            int vid= v. getId ( ) ; 
            if ( vid == holder.button_diancan. getId ( ) ){
            	food_count = dbHelper.addYiDian(table_name, m_id, mfood_cost, mfood_name);
            	if(-1 == food_count){
            		mybtn.setText(ctx.getResources().getString(R.string.dian_can));
            	}else{
            		mybtn.setText(ex + food_count);
            	}
            }
        } 
    }
    
    class lvButtonListener_cancel implements OnClickListener { 
         private String mfood_name;
         private int m_id;
         private Button mybtn;
         int food_count = 0;
         int position;
        lvButtonListener_cancel(int _id, String food_name, Button btn, int pos){
        	mfood_name = food_name;
        	m_id = _id;
        	mybtn = btn;
        	position = pos;
        }
        
        @Override 
        public void onClick( View v) { 
            int vid= v. getId ( ) ; 
            if ( vid == holder.button_cancel. getId ( ) ){
            	food_count = dbHelper.plusYiDian(table_name, m_id);
            	if(-1 == food_count){
            		removeItem(position);
            	}else{
            		mybtn.setText(ex + food_count);
            	}
            }
        } 
    }
  //***********  对于数据库 SQLite 的操作函数   **********
  	private int addData(SQLiteDatabase db, String food_name, String food_cost)
  	{
  		int food_count = 0;
  		try {
			cursor = db.rawQuery("select food_count from " + table_name
					+ " where food_name = '" + food_name + "'", null);
		}catch(SQLiteException  se){
			System.out.println("no such table");
			//执行DDL创建数据表
			db.execSQL("create table " + table_name + "(_id integer primary key autoincrement,"
					+ "food_name varchar(20)," + "food_cost varchar(20)," + "food_count varchar(10));");
			cursor = db.rawQuery("select food_count from " + table_name
					+ " where food_name = '" + food_name + "'", null);
		}
  		if(cursor.getCount() == 0){
  			db.execSQL("insert into " + table_name + " values(null , ?, ?, ?)" , 
					new String[]{food_name, food_cost, "1"});
  			food_count = 1;
  			System.out.println("insert finish");
  		}
  		else{
  			cursor.moveToFirst();
  			food_count = Integer.parseInt(cursor.getString(0).toString());
  			food_count ++;
  			db.execSQL("update " + table_name + " set food_count = '" + food_count 
  					+ "' where food_name = '" + food_name + "'");
  		}
		return food_count;
  	}
  	private int plusData(SQLiteDatabase db, String food_name)
  	{
  		int food_count = 0;
  		try {
			cursor = db.rawQuery("select food_count from " + table_name
					+ " where food_name = '" + food_name + "'", null);
		}catch(SQLiteException  se){
			System.out.println("no such table");
			//执行DDL创建数据表
			db.execSQL("create table " + table_name + "(_id integer primary key autoincrement,"
					+ "food_name varchar(20)," + "food_cost varchar(20)," + "food_count varchar(10));");
			cursor = db.rawQuery("select food_count from " + table_name
					+ " where food_name = '" + food_name + "'", null);
		}
  		if(0 == cursor.getCount()){
  			//do nothing
  			food_count = -1; //-1 表示没有该项
  		}
  		else{
  			cursor.moveToFirst();
  			food_count = Integer.parseInt(cursor.getString(0).toString());
  			if(food_count > 1){
  				food_count --;
  	  			db.execSQL("update " + table_name + " set food_count = '" + food_count 
  	  					+ "' where food_name = '" + food_name + "'");
  			}else if(1 == food_count){
  				//执行删除语句，  注意单引号
  		  		db.execSQL("delete from " + table_name + " where food_name = '" + food_name + "'");
  		  		food_count = -1; //-1 表示没有该项
  			}
  		}
		return food_count;
  	}
}