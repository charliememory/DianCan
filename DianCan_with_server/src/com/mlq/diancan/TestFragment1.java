package com.mlq.diancan;

import java.util.ArrayList;
import java.util.HashMap;

import com.mlq.diancan.R.drawable;
import com.mlq.diancan.R.id;
import com.mlq.diancan.R.layout;
import com.mlq.diancan.R.string;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
/**
* 继承自Fragment，<br>
* 用于根据信息实例化Fragment对象（对应生成Item1_FenChuActivity所需的Fragment）<br>
* 使用SQLiteDatabase进行信息的存储
* @see android.database.sqlite.SQLiteDatabase
* @see com.mlq.diancan.Item1_CaiDanActivity
*/
public class TestFragment1 extends Fragment {
//	private static final String dbPath_CaiDan = Environment.getExternalStorageDirectory().getPath()
//										+ "/DianCan/db/CaiDan.db3"; 
	private static SqliteDbHelper dbHelper;
//	private static final String dbPath_DianCan = Environment.getExternalStorageDirectory().getPath()
//										+ "/DianCan/db/DianCan.db3"; 
//	private static SQLiteDatabase db_DianCan;
    private String hello;// = "hello android";
    private String defaultHello = "default value";
    private static Context ctx;
	private ListView list;
	private ListButtonAdapterA adapter;
	private Cursor cursor;
	private static String tabName = new String("foodKind");
	private static String table_name;
	/**
	* 根据信息实例化Fragment对象
	* @param context caller线程上下文
	* @param title tab标题
	* @param db1 菜单信息数据库对象
	* @param db2 点餐信息数据库对象
	* @return TestFragment1 也即实例化的Fragment对象
	* @see android.os.Bundle
	*/
    static TestFragment1 newInstance(Context context, String mtable_name, String title, SqliteDbHelper mdbHelper) {
        TestFragment1 newFragment = new TestFragment1();
        Bundle bundle = new Bundle();
        bundle.putString(tabName, title);
        newFragment.setArguments(bundle);
        ctx = context;
        table_name = mtable_name;
        dbHelper = mdbHelper;
        newFragment.setArguments(bundle);
        return newFragment;

    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        System.out.println("TestFragment-----onCreate" + args.getString(tabName));
    }
    /**
	* 创建填充好的UI视图
	* @param inflater 布局对象
	* @param container
	* @param savedInstanceState
	* @return view 填充完的UI视图
	* @see android.view.LayoutInflater
	* @see android.view.ViewGroup
	*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        System.out.println("TestFragment-----onCreateView");
        Bundle args = getArguments();
        View view = inflater.inflate(R.layout.fragment, container, false);
        ListView list = (ListView)view.findViewById(R.id.food_list);
//        db = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
		try {
			ArrayList <HashMap < String , Object >> mAppList = dbHelper.selectByFoodKind(table_name, args.getString(tabName));
			inflateList(mAppList, list);
		}catch(SQLiteException  se){
			System.out.println("no such table");
			//执行DDL创建数据表
			dbHelper.creatDbTable(table_name);
		}
        return view;
    }

    
    /**
	* 根据数据源填充UI
	* @param cursor 数据库的游标对象
	* @param list UI视图组件
	* @see com.mlq.diancan.ListButtonAdapterA
	*/
    private void inflateList(ArrayList <HashMap < String , Object >> mAppList, ListView list)
	{
		//填充SimpleCursorAdapter
		/*
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
			MusicList.this , R.layout.music_list_item, cursor 
			, new String[]{"song_title"}
			, new int[]{R.id.song_title});
		*/
		
		// 生成适配器的Item和动态数组对应的元素  
//		Bundle args = getArguments();
		adapter = new ListButtonAdapterA( 
				ctx , 
				Constant.table_yidian,
				dbHelper,
				mAppList, //数据源  
				R.layout.item1_list_item, 
				new String[]{"_id", "food_name", "food_cost"} , 
				new int[]{R.id.food_num1, R.id.food_name1, R.id.food_cost1, R.id.food_diancan, 
					R.id.food_cancel} 
        );
		list.setAdapter(adapter);		
	}
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
        System.out.println("TestFragment-----onDestroy");
    }
}
