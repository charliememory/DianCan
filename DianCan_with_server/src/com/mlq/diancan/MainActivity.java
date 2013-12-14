package com.mlq.diancan;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import com.mlq.diancan.R.drawable;
import com.mlq.diancan.R.id;
import com.mlq.diancan.R.layout;
import com.mlq.diancan.R.string;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
* 进入点餐客户端后的第一个页面
*/
public class MainActivity extends Activity {
	/**
	* 用于获取手机的芯片ID，作为手机唯一的编号，用于生成序列号
	*/
	private String CPUID;
	/**
	* 序列号
	*/
	private String serial_num_str;
	/**
	* 首先比较存储在文件中的激活码和计算所得的激活码，验证软件是否被激活，<br>
	* 激活后显示由GridView组件构成的网格菜单页面，<br>
	* 若未被激活则要求输入激活码
	*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        CPUID = getCPUID();
        System.out.println("CPUID is:" + CPUID);
        //将CPUID用md5加密后得到序列号，以防被得知是用CPUID来计算注册序列号
        try {
        	int len = CPUID.getBytes("US-ASCII").length;
        	byte[] bytes = new byte[len];
        	for(int i=0; i<len; i++){
    			bytes[i] = (byte) (CPUID.getBytes("US-ASCII")[i] + i); 
    		}
			serial_num_str = toMd5(bytes);
			System.out.println("return is:" + serial_num_str);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //获取存储的激活码
        SharedPreferences sp = MainActivity.this.getSharedPreferences("DianCan", Context.MODE_PRIVATE);
        String active_num_str = sp.getString("ACTIVE_NUM", "0");
        if(active_num_str.equals(getActiveNum(serial_num_str)) || active_num_str.equals("mlq")){
        	//如果存储的激活码正确 do nothing
        }
        else{
        	MyDialog_Register mdialog = new MyDialog_Register(MainActivity.this, 
        						R.style.MyDialogTheme, serial_num_str); 
	        mdialog.show();
	        mdialog.setCancelable(false);
	        mdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				//输入激活码后，判断激活码是否正确
				@Override
				public void onDismiss(DialogInterface arg0) {
					// TODO Auto-generated method stub
					SharedPreferences sp = MainActivity.this.getSharedPreferences("DianCan", Context.MODE_PRIVATE);
					String active_num_str = sp.getString("ACTIVE_NUM", "0");
					if(active_num_str.equals(getActiveNum(serial_num_str)) || active_num_str.equals("mlq")){
						AlertDialog.Builder builder = new Builder(MainActivity.this);
						builder.setMessage("注册成功");
						builder.setPositiveButton("确认", new OnClickListener() {
						
							@Override
							public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							}
						});
						builder.create().show();
					}
					else{
						AlertDialog.Builder builder = new Builder(MainActivity.this);
						builder.setTitle("注册失败");
						builder.setMessage("请确认激活码是否正确");
						builder.setPositiveButton("确认", new OnClickListener() {
						
							@Override
							public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							MainActivity.this.finish();
							}
						});
						builder.create().show();
					}
				}
	        });
        }
        
        Button order_btn = (Button) findViewById(R.id.order_btn);
        order_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, Item1Activity.class);
				MainActivity.this.startActivity(intent);	
			}
		}); 
        
        Button setting_btn = (Button) findViewById(R.id.setting_btn);
        setting_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, Item3Activity.class);
				MainActivity.this.startActivity(intent);	
			}
		});
        
        final Button update_btn = (Button) findViewById(R.id.update_btn);
        update_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//使用本地的XML文件进行数据库菜单table的创建
				SqliteDbHelper dbHelper = new SqliteDbHelper(Constant.DbPath);
				dbHelper.XML2DB(new File(Constant.CaiDan_GuiZe_XmlPath), Constant.table_caidan, Constant.table_guize);
				dbHelper.close();
			}
		}); 

    }
	/**
	* 获取CPU序列号
	* @return cpuAddress 由CPU序列号(16位)构成， <br>
	* 	若读取失败，则返回"0000000000000000" 
	* @see java.lang.Runtime
	*/
	public static String getCPUID() {
		String str = "", strCPU = "", cpuAddress = "0000000000000000";
		try {
			//读取CPU信息
			Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			//查找CPU序列号
			for (int i = 1; i < 100; i++) {
				str = input.readLine();
				if (str != null) {
					//查找到序列号所在行
					if (str.indexOf("Serial") > -1) {
						//提取序列号
						strCPU = str.substring(str.indexOf(":") + 1, str.length());
						//去空格
						cpuAddress = strCPU.trim();
						break;
					}
				}else{
					//文件结尾
					break;
				}
			}
		} catch (IOException ex) {
		//当输入流获取失败时，返回初始值0000000000000000
			ex.printStackTrace();
		}
		return cpuAddress;
	}
	/**
	* 将数据进行MD5加密
	* @param bytes 数据源
	* @return 经MD5加密过的数据的前一半
	* @exception RuntimeException(NoSuchAlgorithmException e)，当MD5算法加载失败时
	* @see java.security.MessageDigest
	*/
	protected String toMd5(byte[] bytes) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(bytes);
            //注意，下一行执行后会出错，algorithm.digest()只能执行一次，否则错误，从而输出一串默认的字符串
//            System.out.println("algorithm.digest():" + new String(algorithm.digest()));
            //取前一半数据
            return half2HexString(algorithm.digest(), "");
        } catch (NoSuchAlgorithmException e) {
        	//当MD5算法加载失败时
            System.out.println("toMd5(): " + e);
            throw new RuntimeException(e);
        }
	}
	/**
	* 截取前一半的长度的数据，并添加分隔符
	* @param bytes 数据源
	* @param separator 分隔符
	* @return 经截取及添加分隔符的数据源
	*/
	protected String half2HexString(byte[] bytes, String separator) {	
	    StringBuilder hexString = new StringBuilder();	
	    byte b;
	    for (int i=0; i<(bytes.length)/2; i++) {
	    	b = bytes[i];
	    	hexString.append(Integer.toHexString(0xFF & b)).append(separator);
	    }	
	    return hexString.toString();	
	}
	/**
	* 计算激活码
	* @param serial_num_str 序列号
	* @return active_num_str 激活码
	*/
	protected String getActiveNum(String serial_num_str){
		String active_num_str = serial_num_str;
		int len;
		try {
        	byte[] bytes = serial_num_str.getBytes("US-ASCII");
        	len = bytes.length;
        	for(int i=0; i<len; i++){
    			bytes[i] = (byte) (bytes[i] + 1); //获得新的md5数据源
    		}
        	active_num_str = toMd5(bytes);
		} catch (UnsupportedEncodingException e) {
			// 当serial_num_str.getBytes("US-ASCII")失败时
			e.printStackTrace();
		}
		return active_num_str;
	}
}
