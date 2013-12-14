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
* 利用数据源等信息对UI布局进行初始化,<br>
* 作为TestFragment1的数据适配器
* @see com.mlq.diancan.TestFragment1
*/
public class ListButtonAdapter1 extends BaseAdapter { 	
    /**
	* 页面UI布局对象
	*/
    protected LayoutInflater mInflater; 
    private Context ctx; 
    private String table_name;
    private String [ ] keyString; 
    private int [ ] valueViewID; 
    /**
	* 用于完成列表中每一行item的UI布局初始化
	*/
    protected buttonViewHolder holder;
	/**
	* holder的数据源数组
	*/
    protected ArrayList < HashMap < String , Object > > mAppList; 
    private int layoutResource;
    private Cursor cursor;
    private SqliteDbHelper dbHelper;    
    /**
	* 列表中item的布局类，其中包含了3个文本框和2个按钮控件
	*/
    protected class buttonViewHolder { 
        TextView food_num; 
        TextView food_name;
        TextView food_cost;
        Button button_diancan;
        Button button_cancel; 
    } 
    /**
	* 初始化
	* @param c caller线程上下文
	* @param mtable_name 点餐信息的表名
	* @param appList 数据源数组列表
	* @param resource caller线程系统资源对象
	* @param key 用于从数据源appList获取对应数据的key值
	* @param ID 用于寻找填充buttonViewHolder所需的控件
	*/
    public ListButtonAdapter1( Context c, String mtable_name, SqliteDbHelper mdbHelper, 
    		ArrayList < HashMap < String , Object > > appList,
    		int resource, String [ ] key , int [ ] ID) { 
        ctx = c; 
        table_name = mtable_name;
        dbHelper = mdbHelper;
        mAppList = appList; 
        layoutResource = resource;
        keyString = new String [ key . length ] ; 
        valueViewID = new int [ ID. length ] ; 
        System . arraycopy ( key , 0, keyString, 0, key . length ) ; 
        System . arraycopy ( ID, 0, valueViewID, 0, ID. length ) ; 
        mInflater = ( LayoutInflater) ctx. getSystemService( Context . LAYOUT_INFLATER_SERVICE) ; 
    } 
    /**
	* 获取数据源的大小
	* @return 数据源mAppList的大小
	*/
    @Override 
    public int getCount ( ) { 
        return mAppList.size ( ) ; 
    } 
    /**
	* 获取数据源指定序号的item
	* @param position 指定的序号
	* @return 指定序号的item
	*/
    @Override 
    public Object getItem ( int position ) { 
        return mAppList.get ( position ) ; 
    } 
    /**
	* 获取数据源指定Id的item的序号
	* @param position 指定的Id
	* @return 指定Id对应的序号
	*/
    @Override 
    public long getItemId( int position ) { 
        return position ; 
    } 
    /**
	* 删除指定序号的item
	* @param position 指定的序号
	*/
    public void removeItem ( int position ) { 
        mAppList.remove( position ) ; 
        this.notifyDataSetChanged( ) ; 
    } 
    /**
	* 使用数据源进行UI布局进行初始化
	* @param position 指定的数据源序号
	* @param convertView item的UI布局
	* @param parent
	* @return 无
	*/
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
            int mposition = position + 1; //position is indexed from zero
            holder.food_num.setText("" + mposition) ; 
            holder.food_name.setText(food_name) ;
            holder.food_cost.setText(food_cost + ctx.getResources().getString(R.string.money_unit)) ;
            holder.button_diancan.setOnClickListener( new lvButtonListener_diancan(_id, food_name, holder.button_diancan, food_cost) ) ; 
            holder.button_cancel.setOnClickListener( new lvButtonListener_cancel(_id, food_name, holder.button_diancan) ) ; 
        }         
        return convertView; 
    } 
    /**
	* 继承自OnClickListener,<br>
	* 用于监听点餐按钮的click事件
	*/
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
            	dbHelper.addYiDian(table_name, m_id, mfood_cost, mfood_name);
            	dbHelper.updateTuiJian(Constant.table_caidan, Constant.table_yidian, Constant.table_tuijian, Constant.table_guize);
            }
        } 
    }
    /**
	* 继承自OnClickListener,<br>
	* 用于监听取消按钮的click事件
	*/
    class lvButtonListener_cancel implements OnClickListener { 
         private String mfood_name;
         private int m_id;
         private Button mybtn;
         int food_count = 0;
        lvButtonListener_cancel(int _id, String food_name, Button btn){
        	mfood_name = food_name;
        	m_id = _id;
        	mybtn = btn;
        }
        
        @Override 
        public void onClick( View v) { 
            int vid= v. getId ( ) ; 
            if ( vid == holder.button_cancel. getId ( ) ){
            	food_count = dbHelper.plusYiDian(table_name, m_id);
            	dbHelper.updateTuiJian(Constant.table_caidan, Constant.table_yidian, Constant.table_tuijian, Constant.table_guize);
            }
        } 
    }
}