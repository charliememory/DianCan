package com.mlq.diancan;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

import com.mlq.diancan.R.drawable;
import com.mlq.diancan.R.id;
import com.mlq.diancan.R.layout;
import com.mlq.diancan.R.string;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
/**
* 继承自第三方android-support-v4.jar包的FragmentActivity，<br>
* 使用此jar包中的Fragment组件来显示不同菜单页面，<br>
* 使用此jar包中的ViewPager组件来实现不同菜单页面滑屏切换效果，<br>
* ViewPager的数据适配器MyFragmentPagerAdapter继承自此jar包中的OnPageChangeListener，<br>
* 适配器的数据源来为ArrayList<Fragment>类型
*/
public class Item1_CaiDanActivity extends FragmentActivity {
	/**
	* android-support-v4.jar包中作为点餐信息的显示组件,<br>
	* 可以实现不同页面滑屏切换效果,<br>
	* 以MyFragmentPagerAdapter作为适配器
	*/
	protected ViewPager mPager;
	/**
	* 由使用TestFragment1类的newInstance方法生成的Fragment组成,<br>
	* 作为ViewPager组件的数据适配器MyFragmentPagerAdapter的数据源
	* @see com.mlq.diancan.TestFragment1
	*/
	protected ArrayList<Fragment> fragmentsList;
	/**
	* 用于白色横线动画的组件
	*/
    protected ImageView ivBottomLine;
	/**
	* 显示菜单页面标题的组件
	*/
    protected TextView tvTab0, tvTab1, tvTab2, tvTab3, tvTab4, tvTab5;
    private int currIndex = 0;
    private int bottomLineWidth;
    private int offset = 0;
    private int position_one;
    private int position_two;
    private int position_three;
    private int position_four;
    private int position_five;
    private Resources resources;
    private final String table_name = Constant.table_caidan;
	/**
	* 操作数据库的对象
	*/
	protected SqliteDbHelper dbHelper;
//	/**
//	* 点餐信息数据库对象
//	*/
//    protected SQLiteDatabase db_DianCan;
//	private ListView list;
    /**
	* 遍历数据库游标对象
	*/
    protected Cursor cursor;

	/**
	* 在第一次载入页面时，进行UI布局的初始化，并检测数据库文件是否已存在
	*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fen_chu);
        
      //*************初始化     list ，包括检测文件和目录是否存在，以及数据库操作   *************
  		File mFile = new File(Constant.DbPath); 
		if(!mFile.exists())
		{ 
			File mDirPath = mFile.getParentFile(); //new File(vFile.getParent()); 
			mDirPath.mkdirs(); 
		}
	}

	/**
	* 载入页面时，依据数据库里的信息进行Fragment 界面的更新
	*/
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		dbHelper = new SqliteDbHelper(Constant.DbPath);
        //初始化 Fragment 界面
        resources = getResources();
        InitWidth();
        InitTextView();
        InitViewPager();
        System.out.println("fragment setup finish");
	}
//*********************   以下为 Fragment 界面需要的函数及类   *******************   
	/**
	* 初始化Fragment的UI信息
	*/
	protected void InitTextView() {
    	tvTab0 = (TextView) findViewById(R.id.tv_tab_0);
        tvTab1 = (TextView) findViewById(R.id.tv_tab_1);
        tvTab2 = (TextView) findViewById(R.id.tv_tab_2);
        tvTab3 = (TextView) findViewById(R.id.tv_tab_3);
        tvTab4 = (TextView) findViewById(R.id.tv_tab_4);
        tvTab5 = (TextView) findViewById(R.id.tv_tab_5);

        tvTab0.setOnClickListener(new MyOnClickListener(0));
        tvTab1.setOnClickListener(new MyOnClickListener(1));
        tvTab2.setOnClickListener(new MyOnClickListener(2));
        tvTab3.setOnClickListener(new MyOnClickListener(3));
        tvTab4.setOnClickListener(new MyOnClickListener(4));
        tvTab5.setOnClickListener(new MyOnClickListener(5));
    }
	/**
	* 根据数据库里的信息 初始化数据源ArrayList<Fragment>的信息及Fragment的标题UI信息,<br>
	* 当数据库中不存在指定的table时，则新建table
	*/
	protected void InitViewPager() {
        mPager = (ViewPager) findViewById(R.id.vPager);
        fragmentsList = new ArrayList<Fragment>();
        LayoutInflater mInflater = getLayoutInflater();
    	ArrayList <HashMap < String , Object >> mAppList = dbHelper.getFoodKinds(table_name);
    	for ( int i= 0; i<mAppList.size() && i<6; i++ ) //最多6个种类
        { 
    		//cursor.getString(1)获取的是菜谱的种类名
//        		fragmentsList.add(TestFragment1.newInstance(Item1_FenChuActivity.this, 
//                		cursor.getString(1), db_CaiDan, db_DianCan));
    		fragmentsList.add(TestFragment1.newInstance(Item1_CaiDanActivity.this, Constant.table_caidan,
    				mAppList.get(i).get("food_kind").toString(), dbHelper));
            switch(i){
            case 0:
            	tvTab0.setText(mAppList.get(i).get("food_kind").toString());
            	break;
            case 1:
            	tvTab1.setText(mAppList.get(i).get("food_kind").toString());
            	break;
            case 2:
            	tvTab2.setText(mAppList.get(i).get("food_kind").toString());
            	break;
            case 3:
            	tvTab3.setText(mAppList.get(i).get("food_kind").toString());
            	break;
            case 4:
            	tvTab4.setText(mAppList.get(i).get("food_kind").toString());
            	break;
            case 5:
            	tvTab5.setText(mAppList.get(i).get("food_kind").toString());
            	break;
            }
        }
//        if(null == cursor){
//        	System.out.println("cursor is null");
//        	fragmentsList.add(TestFragment.newInstance(Item1_FenChuActivity.this, 
//            		"空白", db_CaiDan, db_DianCan));
//        }else{
//        	cursor.moveToFirst();
//        	for ( int i= 0; i<cursor.getCount() && i<4; i++ ) //最多四个种类
//            { 
//        		fragmentsList.add(TestFragment.newInstance(Item1_FenChuActivity.this, 
//                		cursor.getString(1), db_CaiDan, db_DianCan));
//                switch(i){
//	                case 0:
//	                	tvTab1.setText(cursor.getString(1));
//	                	break;
//	                case 1:
//	                	tvTab2.setText(cursor.getString(1));
//	                	break;
//	                case 2:
//	                	tvTab3.setText(cursor.getString(1));
//	                	break;
//	                case 3:
//	                	tvTab4.setText(cursor.getString(1));
//	                	break;
//                }
//                cursor.moveToNext();
//            }
//        }
//        Fragment mianshifragment = TestFragment.newInstance(Item1_FenChuActivity.this, 
//        		"面食", db_CaiDan, db_DianCan);
//        Fragment jiaoziFragment = TestFragment.newInstance(Item1_FenChuActivity.this, 
//        		"饺子", db_CaiDan, db_DianCan);
//        Fragment xiaocaiFragment=TestFragment.newInstance(Item1_FenChuActivity.this, 
//        		"小菜", db_CaiDan, db_DianCan);
//        Fragment yinliaoFragment=TestFragment.newInstance(Item1_FenChuActivity.this, 
//        		"饮料", db_CaiDan, db_DianCan);

//        fragmentsList.add(mianshifragment);
//        fragmentsList.add(jiaoziFragment);
//        fragmentsList.add(xiaocaiFragment);
//        fragmentsList.add(yinliaoFragment);
        
        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList));
        mPager.setCurrentItem(0);
        mPager.setOnPageChangeListener(new MyOnPageChangeListener());
    }
	/**
	* 初始化页面滑动宽度的参数
	*/
    private void InitWidth() {
        ivBottomLine = (ImageView) findViewById(R.id.iv_bottom_line);
        bottomLineWidth = ivBottomLine.getLayoutParams().width;
        System.out.println("cursor imageview width=" + bottomLineWidth);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        offset = (int) ((screenW / 6.0 - bottomLineWidth) / 2);
        Log.i("MainActivity", "offset=" + offset);

        position_one = (int) (screenW / 6.0);
        position_two = position_one * 2;
        position_three = position_one * 3;
        position_four = position_one * 4;
        position_five = position_one * 5;
    }
    /**
	* 继承自View.OnClickListener,<br>
	* 用于响应ViewPager组件click事件的监听器,<br>
	* click后改变组件当前的序号
	*/
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }
    	/**
    	* 改变ViewPager组件当前的序号
    	* @param v 发生click事件的View
    	*/
        @Override
        public void onClick(View v) {
            mPager.setCurrentItem(index);
        }
    };
    
    /**
	* 继承自OnPageChangeListener,<br>
	* ViewPager的数据适配器
	*/
    public class MyOnPageChangeListener implements OnPageChangeListener {
    	/**
    	* 依据滑动到的页面序号进行数据（文本颜色）的变更
    	* @param arg0 滑动到的页面序号
    	*/
        @Override
        public void onPageSelected(int arg0) {
            Animation animation = null;
            switch (arg0) {
            case 0:
                if (currIndex == 1) {
                    animation = new TranslateAnimation(position_one, 0, 0, 0);
                    tvTab1.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(position_two, 0, 0, 0);
                    tvTab2.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 3) {
                    animation = new TranslateAnimation(position_three, 0, 0, 0);
                    tvTab3.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 4) {
                    animation = new TranslateAnimation(position_four, 0, 0, 0);
                    tvTab4.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 5) {
                    animation = new TranslateAnimation(position_five, 0, 0, 0);
                    tvTab5.setTextColor(resources.getColor(R.color.lightwhite));
                }
                tvTab0.setTextColor(resources.getColor(R.color.white));
                break;
            case 1:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, position_one, 0, 0);
                    tvTab0.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(position_two, position_one, 0, 0);
                    tvTab2.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 3) {
                    animation = new TranslateAnimation(position_three, position_one, 0, 0);
                    tvTab3.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 4) {
                    animation = new TranslateAnimation(position_four, position_one, 0, 0);
                    tvTab3.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 5) {
                    animation = new TranslateAnimation(position_five, position_one, 0, 0);
                    tvTab5.setTextColor(resources.getColor(R.color.lightwhite));
                }
                tvTab1.setTextColor(resources.getColor(R.color.white));
                break;
            case 2:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, position_two, 0, 0);
                    tvTab0.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(position_one, position_two, 0, 0);
                    tvTab1.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 3) {
                    animation = new TranslateAnimation(position_three, position_two, 0, 0);
                    tvTab3.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 4) {
                    animation = new TranslateAnimation(position_four, position_two, 0, 0);
                    tvTab4.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 5) {
                    animation = new TranslateAnimation(position_five, position_two, 0, 0);
                    tvTab5.setTextColor(resources.getColor(R.color.lightwhite));
                }
                tvTab2.setTextColor(resources.getColor(R.color.white));
                break;
            case 3:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, position_three, 0, 0);
                    tvTab0.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(position_one, position_three, 0, 0);
                    tvTab1.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(position_two, position_three, 0, 0);
                    tvTab2.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 4) {
                    animation = new TranslateAnimation(position_four, position_three, 0, 0);
                    tvTab2.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 5) {
                    animation = new TranslateAnimation(position_five, position_three, 0, 0);
                    tvTab2.setTextColor(resources.getColor(R.color.lightwhite));
                }
                tvTab3.setTextColor(resources.getColor(R.color.white));
                break;
            case 4:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, position_four, 0, 0);
                    tvTab0.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(position_one, position_four, 0, 0);
                    tvTab1.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(position_two, position_four, 0, 0);
                    tvTab2.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 3) {
                    animation = new TranslateAnimation(position_three, position_four, 0, 0);
                    tvTab3.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 5) {
                    animation = new TranslateAnimation(position_five, position_four, 0, 0);
                    tvTab5.setTextColor(resources.getColor(R.color.lightwhite));
                }
                tvTab4.setTextColor(resources.getColor(R.color.white));
                break;
            case 5:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, position_five, 0, 0);
                    tvTab0.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(position_one, position_five, 0, 0);
                    tvTab1.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(position_two, position_five, 0, 0);
                    tvTab2.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 3) {
                    animation = new TranslateAnimation(position_three, position_five, 0, 0);
                    tvTab3.setTextColor(resources.getColor(R.color.lightwhite));
                } else if (currIndex == 4) {
                    animation = new TranslateAnimation(position_four, position_five, 0, 0);
                    tvTab4.setTextColor(resources.getColor(R.color.lightwhite));
                }
                tvTab5.setTextColor(resources.getColor(R.color.white));
                break;
            }
            currIndex = arg0;
            animation.setFillAfter(true);
            animation.setDuration(300);
            ivBottomLine.startAnimation(animation);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }
//*********************   以下为数据库及 ListView 控件需要的函数及类   ******************* 
//    private void insertData(SQLiteDatabase db, String num, String title, String singer, String info)
//	{
//		//执行插入语句
//		db.execSQL("insert into song values(null , ?, ?, ?, ?)" , new String[]{num, title, singer, info});
//	}

//	@Override
//	public void onDestroy()
//	{
//		super.onDestroy();
//	}
    /**
	* 退出页面时关闭数据库对象
	*/
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//退出程序时关闭SQLiteDatabase
		dbHelper.close();
	}
}
