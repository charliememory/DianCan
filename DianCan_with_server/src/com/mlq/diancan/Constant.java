package com.mlq.diancan;

import android.os.Environment;

/**
* 用来定义程序中用到的常量
*/
public class Constant {
	/**
	* Server端IP地址
	*/
	public static final String ServerIP = "192.168.1.100";
	/**
	* 打印机IP地址
	*/
	public static final String PrintIP = "192.168.1.200";
	/**
	* Server端UDP点餐端口
	*/
	public static final int ServerUDPPort = 5455;
	/**
	* Server端TCP文件传输端口
	*/
	public static final int ServerTCPPort = 5456;
	/**
	* 打印机数据打印端口
	*/
	public static final int DstPort = 9100;
	/**
	* 打印机测试打印端口
	*/
	public static final int StatusTestPort = 4000;
	/**
	* 更新菜单信息时的slave模式socket端口号
	*/
	public static final int SLAVE_RECEIVE_UDP_PORT = 4446;
	/**
	* 更新菜单信息时的master模式socket端口号
	*/
	public static final int MASTER_SEND_TCP_PORT = 4447;
	/**
	* 更新菜单信息时的master模式下进行广播的IP地址
	*/
	public static final String BROADCAST_IP = "255.255.255.255"; 
	/**
	* 每次通信请求的发送次数上限
	*/
	public static final int SendTimes = 5;
	/**
	* 每次通信请求的发送次数上限
	*/
	public static final int TaskLength = 20;
	/**
	* 数据库文件路径，存储点餐信息
	*/
	public static final String DbPath = Environment.getExternalStorageDirectory().getPath()
										+ "/DianCan/db/DianCan.db3"; 
	/**
	* XML文件路径，存储菜单信息
	*/
	public static final String CaiDan_GuiZe_XmlPath = Environment.getExternalStorageDirectory().getPath()
										+ "/DianCan/db/CaiDan_GuiZe.xml"; 
	/**
	* XML文件路径，存储点餐信息
	*/
	public static final String DianCanXmlPath = Environment.getExternalStorageDirectory().getPath()
										+ "/DianCan/db/DianCan.xml"; 
//	/**
//	* 数据库文件路径，用于保存菜单信息
//	*/
//	public static final String DbPath_CaiDan = Environment.getExternalStorageDirectory().getPath()
//										+ "/DianCan/db/CaiDan.db3";  
	/**
	* 数据库中保存的菜单table名
	*/
	public static final String table_caidan = new String("菜单");
	/**
	* 数据库中保存的已点table名
	*/
	public static final String table_yidian = new String("已点");
	/**
	* 数据库中保存的推荐table名
	*/
	public static final String table_tuijian = new String("推荐");
	/**
	* 数据库中保存推荐规则的table名
	*/
	public static final String table_guize = new String("规则");
	/**
	* 数据库中保存各table更新时间的table名
	*/
	public static final String table_version = new String("版本");
	public static final String empty = new String("空白");
}
