package com.mlq.diancan;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
/**
* 继承自android-support-v4.jar包中的FragmentPagerAdapter类,<br>
* 作为ViewPager组件的数据适配器，使用ArrayList<Fragment> 类型的对象做为数据源
*/
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentsList;

    /**
	* 初始化MyFragmentPagerAdapter
	* @param fm android-support-v4.jar包中一个管理Fragment的类
	*/
    public MyFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    /**
	* 初始化MyFragmentPagerAdapter
	* @param fm android-support-v4.jar包中一个管理Fragment的类
	* @param fragments 适配器的数据源
	*/
    public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.fragmentsList = fragments;
    }
    /**
	* 获取ArrayList<Fragment>数组大小
	* @return ArrayList<Fragment>数组大小
	*/
    @Override
    public int getCount() {
        return fragmentsList.size();
    }
    /**
	* 获取序号对应的Fragment
	* @param arg0 ArrayList<Fragment>数组中的item序号
	* @return 获取序号对应的Fragment
	*/
    @Override
    public Fragment getItem(int arg0) {
        return fragmentsList.get(arg0);
    }
    /**
   	* 获取Fragment在数组中对应的序号
   	* @param object Fragment对象
   	* @return 序号
   	*/
    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

}
