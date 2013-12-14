package com.mlq.diancan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

public class SqliteDbHelper {

    private Cursor cursor;
    private static SQLiteDatabase db;
    
	public SqliteDbHelper(String DbPath) {
		// TODO Auto-generated constructor stub
		db = SQLiteDatabase.openOrCreateDatabase(DbPath, null);
	}
	/**
	* 在数据库中根据table_name创建相应的table，<br>
	* 在table中加入了冗余的food_kind_no以及_id字段是为了方便推荐模型的映射(food_kind_no--food_kind)(_id--food_name)
	* @param table_name 操作的table名
	*/
	public void creatDbTable(String table_name)
  	{
		if(table_name.equals(Constant.table_caidan)){
			// 菜单table中的 _id 字段要从1自增，得自己写程序完成，autoincrement不一定从1开始
			db.execSQL("create table " + table_name + "(_id integer primary key,"
					 + "food_kind varchar(20)," + "food_cost varchar(10)," + "food_name varchar(20));");
		}
		else if(table_name.equals(Constant.table_tuijian)){ 
			// 推荐table中的 _id 字段是依据菜单table 中的 _id 定
			db.execSQL("create table " + table_name + "(_id integer primary key,"
					 + "food_cost varchar(10)," + "food_name varchar(20));");
		}
		else if(table_name.equals(Constant.table_yidian)){ 
			// 已点table中的 _id 字段是依据菜单table 中的 _id 定
			db.execSQL("create table " + table_name + "(_id integer primary key,"
					 + "food_cost varchar(20)," + "food_count varchar(10)," + "food_name varchar(20));");
		}
		else if(table_name.equals(Constant.table_guize)){
			db.execSQL("create table " + table_name + "(_id integer primary key autoincrement,"
					 + "order_set varchar(200)," + "recommand_set varchar(200)," + "conf float(20));");
		}
		else if(table_name.equals(Constant.table_version)){
			db.execSQL("create table " + table_name + "(_id integer primary key autoincrement,"
					 + "table_name varchar(20)," + "time_stamp varchar(20));");
		}
		else{
			System.err.println("creat table failed, table_name error");
		}
  	}
	/**
	* 根据table_name检测相应的table在数据库中是否存在
	* @param table_name 操作的table名
	* @return table存在则返回true，否则返回false
	*/
	public boolean tableExist(String table_name)
  	{
		try {
			cursor = db.rawQuery("select * from " + table_name, null);
			return true;
		}catch(SQLiteException  se){
			System.out.println("no such table");
			return false;
		}
  	}
	/**
	* 显示数据库table中的菜目的种类（不重复），<br>
	* @param table_name 操作的table名
	* @return cursor 数据库信息的游标
	*/
	public ArrayList <HashMap <String, Object>> getFoodKinds(String table_name)
  	{
  		try {
			cursor = db.rawQuery("select * from " + table_name + 
					" where _id in(Select min(_id) FROM " + table_name + " group by food_kind)", null);
		}catch(SQLiteException  se){
			System.out.println("no such table");
			//执行DDL创建数据表
			creatDbTable(table_name);
			cursor = db.rawQuery("select * from " + table_name, null);
		}
  		//数据格式转换为java通用格式，便于功能层与数据层分离
  		ArrayList <HashMap <String, Object>> mAppList = new ArrayList <HashMap <String, Object>>();
		cursor.moveToFirst();
		if(cursor.getCount() > 0){
			for ( int i= 0; i<cursor.getCount(); i++ ) 
	        { 
	            HashMap <String, Object> map = new HashMap <String, Object> ( ); 
//	            map.put( "food_kind_no" , cursor.getString(1)) ;
	            map.put( "food_kind" , cursor.getString(1));
	            mAppList.add( map ) ;
	            cursor.moveToNext();
	        }
		}
		else{  //如果table中无数据，则显示“空白”
			HashMap <String, Object> map = new HashMap <String, Object> ( ); 
//            map.put( "food_kind_no" , "0") ;
            map.put( "food_kind" , Constant.empty);
            mAppList.add( map ) ;
		}
    	cursor.close();  //游标使用完记得关闭，否则会有数据库未关闭的错误
		return mAppList;
  	}
	/**
	* 显示数据库table中的菜目的种类（不重复），<br>
	* @param table_name 操作的table名
	* @return cursor 数据库信息的游标
	*/
	public ArrayList <HashMap <String, Object>> selectByFoodKind(String table_name, String food_kind)
  	{
  		try {
  			cursor = db.rawQuery("select * from " + table_name
					+ " where food_kind = '" + food_kind + "'", null);
		}catch(SQLiteException  se){
			System.out.println("no such table");
			//执行DDL创建数据表
			creatDbTable(table_name);
			cursor = db.rawQuery("select * from " + table_name, null);
		}
  		//数据格式转换为java通用格式，便于功能层与数据层分离
  		ArrayList <HashMap <String, Object>> mAppList = new ArrayList <HashMap <String, Object>>();
		cursor.moveToFirst();
		for ( int i= 0; i<cursor.getCount(); i++ ) 
        { 
            HashMap <String, Object> map = new HashMap <String, Object> ( ); 
			map.put("_id", cursor.getInt(0));
            map.put( "food_cost" , cursor.getString(2));
            map.put( "food_name" , cursor.getString(3));
//            System.out.println("map.put finish");
            mAppList.add( map ) ;
//            System.out.println("mAppList.add finish");
            cursor.moveToNext();
        }
    	cursor.close();  //游标使用完记得关闭，否则会有数据库未关闭的错误
		return mAppList;
  	}
	/**
	* 显示数据库table中的菜目的种类（不重复），<br>
	* @param table_name 操作的table名
	* @return cursor 数据库信息的游标
	*/
	public HashMap <String, Object> selectById(String table_name, int _id)
  	{
  		try {
  			cursor = db.rawQuery("select * from " + table_name + " where _id = " + _id, null);
		}catch(SQLiteException  se){
			System.out.println("no such table");
			//执行DDL创建数据表
			creatDbTable(table_name);
			cursor = db.rawQuery("select * from " + table_name, null);
		}
  		//数据格式转换为java通用格式，便于功能层与数据层分离
//  		ArrayList <HashMap <String, Object>> mAppList = new ArrayList <HashMap <String, Object>>();
		cursor.moveToFirst();
//		for ( int i= 0; i<cursor.getCount(); i++ ) 
//        { 
        HashMap <String, Object> map = new HashMap <String, Object> ( ); 
		map.put("_id", cursor.getInt(0));
        map.put( "food_cost" , cursor.getString(2));
        map.put( "food_name" , cursor.getString(3));
//            System.out.println("map.put finish");
//            mAppList.add( map ) ;
////            System.out.println("mAppList.add finish");
//            cursor.moveToNext();
//        }
    	cursor.close();  //游标使用完记得关闭，否则会有数据库未关闭的错误
		return map;
  	}
	/**
	* 在数据库中根据table_name选择相应的table，<br>
	* @param table_name 操作的table名
	* @return cursor 数据库信息的游标
	*/
	public ArrayList <HashMap <String, Object>> selectAll(String table_name)
  	{
  		try {
			cursor = db.rawQuery("select * from " + table_name, null);
		}catch(SQLiteException  se){
			System.out.println("no such table");
			//执行DDL创建数据表
			creatDbTable(table_name);
			cursor = db.rawQuery("select * from " + table_name, null);
		}
  		//数据格式转换为java通用格式，便于功能层与数据层分离
  		ArrayList <HashMap <String, Object>> mAppList = new ArrayList <HashMap <String, Object>>();
		cursor.moveToFirst();
		for ( int i= 0; i<cursor.getCount(); i++ ) 
        { 
            HashMap <String, Object> map = new HashMap <String, Object> ( ); 
            if(table_name.equals(Constant.table_caidan)){
    			map.put("_id", cursor.getInt(0));
                map.put( "food_kind" , cursor.getString(1));
                map.put( "food_cost" , cursor.getString(2));
                map.put( "food_name" , cursor.getString(3));
    		}
    		else if(table_name.equals(Constant.table_tuijian)){
//    			map.put( "food_kind_no" , cursor.getString(1));
    			map.put("_id", cursor.getInt(0));
                map.put( "food_cost" , cursor.getString(1));
                map.put( "food_name" , cursor.getString(2));
    		}
    		else if(table_name.equals(Constant.table_yidian)){
    			map.put("_id", cursor.getInt(0));
    			map.put( "food_cost" , cursor.getString(1));
                map.put( "food_count" , cursor.getString(2));
                map.put( "food_name" , cursor.getString(3));
    		}
    		else if(table_name.equals(Constant.table_guize)){
    			map.put( "order_set" , cursor.getString(1));
                map.put( "recommand_set" , cursor.getString(2));
                map.put( "conf" , cursor.getFloat(3));
    		}
    		else if(table_name.equals(Constant.table_version)){
    			map.put( "table_name" , cursor.getString(1));
                map.put( "time_stamp" , cursor.getString(2));
    		}
    		else{
    			System.err.println("creat table failed, table_name error");
    		}
            mAppList.add( map ) ;
//            System.out.println("mAppList.add finish");
            cursor.moveToNext();
        }
    	cursor.close();  //游标使用完记得关闭，否则会有数据库未关闭的错误
		return mAppList;
  	}
	/**
	* 向数据库中添加数据（已点信息），<br>
	* 若未点过该菜，则进行插入操作，<br>
	* 否则只更新food_count字段,<br>
	* 用 _id 字段来进行table的操作可加快速度，因为table在 creat 时在_id 字段上建立了索引
	* @param table_name 操作的table名
	* @param food_cost 点餐信息的价格字段
	* @param food_name 点餐信息的菜名字段
	* @return food_count 点餐信息的已点数目字段，-1表示无该项
	*/
	public int addYiDian(String table_name, int _id, String food_cost, String food_name)
  	{
  		int food_count = 0;
  		try {
			cursor = db.rawQuery("select food_count from " + table_name
					+ " where _id = " + _id, null);
		}catch(SQLiteException  se){
			System.out.println("no such table");
			//执行DDL创建数据表
			creatDbTable(table_name);
			cursor = db.rawQuery("select food_count from " + table_name
					+ " where _id = " + _id, null);
		}
  		if(cursor.getCount() == 0){
  			db.execSQL("insert into " + table_name + " values(? , ?, ?, ?)" , 
					new Object[]{_id, food_cost, "1", food_name});
  			food_count = 1;
  			System.out.println("insert finish");
  		}
  		else{
  			cursor.moveToFirst();
  			food_count = Integer.parseInt(cursor.getString(0).toString());
  			food_count ++;
  			db.execSQL("update " + table_name + " set food_count = '" + food_count 
  					+ "' where _id = " + _id);
  		}
    	cursor.close();  //游标使用完记得关闭，否则会有数据库未关闭的错误
		return food_count;
  	}
	/**
	* 从数据库中减去数据（已点信息），<br>
	* 若点过该菜份数>1，则进行更新操作，food_count字段减一,<br>
	* 若点过该菜份数=1，则进行删除操作,<br>
	* 若未点过该菜，则不进行操作,<br>
	* 用 _id 字段来进行table的操作可加快速度，因为table在 creat 时在_id 字段上建立了索引
	* @param table_name 操作的table名
	* @param _id 点餐信息的 _id 字段，表示食物标号信息
	* @return food_count 点餐信息的已点数目字段，-1表示无该项
	*/
	public int plusYiDian(String table_name, int _id)
  	{
  		int food_count = 0;
  		try {
			cursor = db.rawQuery("select food_count from " + table_name
					+ " where _id = " + _id, null);
		}catch(SQLiteException  se){
			System.out.println("no such table");
			//执行DDL创建数据表
			creatDbTable(table_name);
			cursor = db.rawQuery("select food_count from " + table_name
					+ " where _id = " + _id, null);
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
  	  					+ "' where _id = " + _id);
  			}else if(1 == food_count){
  				//执行删除语句，  注意单引号
  		  		db.execSQL("delete from " + table_name + " where _id = " + _id);
  		  		food_count = -1; //-1 表示没有该项
  			}
  		}
    	cursor.close();  //游标使用完记得关闭，否则会有数据库未关闭的错误
		return food_count;
  	}

	/**
	* 在不删除表的情况下删除所有的行，<br>
	* @param table_caidan 菜单table名
	* @param table_yidian 已点table名
	* @param table_tuijian 推荐table名
	* @param table_guize 规则table名
	*/
	public void updateTuiJian(String table_caidan, String table_yidian, String table_tuijian, String table_guize)
	{
		// 清空推荐table
		deleteAllData(table_tuijian);
		// 已点菜目的序号构成的字符串
		String order_set = "";
		// 符合规则的推荐菜目信息的列表
		ArrayList <HashMap <String , Object>> recommandList = new ArrayList <HashMap <String, Object>>();
		// 已点菜目信息列表
		ArrayList <HashMap <String , Object>> yidianList = selectAll(table_yidian);
		for(HashMap <String, Object> map:yidianList){
			int _id = (Integer) map.get("_id");
			order_set = order_set + _id;
			int lastIndex = yidianList.size()-1;
			if(!map.equals(yidianList.get(lastIndex))){
				order_set = order_set + "_";
			}
		}
		System.out.println("order_set:" + order_set);
		// 对比规则表中的信息，筛选出符合规则的推荐菜目信息
		ArrayList <HashMap <String, Object>> guizeList = selectAll(table_guize);
		for(HashMap <String, Object> map:guizeList){
			String t_order_set = (String) map.get("order_set");
			if(t_order_set.equals(order_set)){
				// 需要对map进行clone，因为java中传参是对象的引用，需要避免map的改动对recommandList的影响
				HashMap <String, Object> tmap = (HashMap<String, Object>) map.clone();
				// 删去tmap中的"order_set"键值对，降低空间冗余度以减少存储及排序复杂度
				tmap.remove("order_set");
				recommandList.add(tmap); 
			}
		}
		// 对符合规则的推荐菜目信息按置信度conf进行"降序"排列
		Collections.sort(recommandList, new Comparator<HashMap <String , Object>>() {
			
			@Override
			public int compare(HashMap<String, Object> arg0,
					HashMap<String, Object> arg1) {
				// TODO Auto-generated method stub
				Float conf0 = (Float) arg0.get("conf");
				Float conf1 = (Float) arg1.get("conf");
				return conf1.compareTo(conf0);
			}

        });
		// 将置信度最高的 N 个推荐菜目的 _id 不重复地放入结果列表
		ArrayList <Integer> resultList = new ArrayList <Integer>();
		for(HashMap <String , Object> map:recommandList){
			String recommand_set = (String) map.get("recommand_set");
			String[] array = recommand_set.split("_");
			for (String str : array) {
				Integer _id = Integer.valueOf(str);
				if(!resultList.contains(_id)){
					resultList.add(_id);
				}
			}
		}
		// 清空推荐table中的信息
		// 根据结果列表中的 _id 信息从菜单table里查找菜目信息，并插入推荐table
		for(Integer _id:resultList){
			HashMap <String, Object> map = selectById(table_caidan, _id);
			String food_cost = (String) map.get("food_cost");
			String food_name = (String) map.get("food_name");
			try {
				db.execSQL("insert into " + table_tuijian + " values(?, ?, ?)" , 
						new Object[]{(int)_id, food_cost, food_name});
			}catch(SQLiteException  se){
				System.out.println("no such table");
				//执行DDL创建数据表
				creatDbTable(table_tuijian);
				db.execSQL("insert into " + table_tuijian + " values(?, ?, ?)" , 
						new Object[]{(int)_id, food_cost, food_name});
			}
		}
		
//		try {
//			db.execSQL("insert into " + table_name + " values(?, ?, ?)" , 
//					Object[]{_id, food_cost, food_name});
//		}catch(SQLiteException  se){
//			creatDbTable(table_name);
//			db.execSQL("insert into " + table_name + " values(?, ?, ?)" , 
//					Object[]{_id, food_cost, food_name});
//		}		
	}
	/**
	* 向数据库中添加数据（table版本信息，即从 1970-1-1-0:00:00 GMT 到现在的毫秒数），<br>
	* 若未添加过版本信息，则进行插入操作，否则进行更新操作
	* 否则只更新food_count字段
	* @param table_name 操作的table名
	* @param version_table_name 要添加版本信息所对应的table名
	*/
	public void addVersion(String table_name, String version_table_name)
  	{
		long time_stamp = System.currentTimeMillis();
  		try {
			cursor = db.rawQuery("select time_stamp from " + table_name
					+ " where table_name = '" + version_table_name + "'", null);
		}catch(SQLiteException  se){
			System.out.println("no such table");
			//执行DDL创建数据表
			creatDbTable(table_name);
			cursor = db.rawQuery("select time_stamp from " + table_name
					+ " where table_name = '" + version_table_name + "'", null);
		}
  		if(cursor.getCount() == 0){
  			db.execSQL("insert into " + table_name + " values(null , ?, ?)" , 
					new String[]{version_table_name, String.valueOf(time_stamp)});
  			System.out.println("insert finish");
  		}
  		else{
  			cursor.moveToFirst();
  			db.execSQL("update " + table_name + " set time_stamp = '" + String.valueOf(time_stamp) 
  					+ "' where table_name = '" + version_table_name + "'");
  		}
    	cursor.close();  //游标使用完记得关闭，否则会有数据库未关闭的错误
  	}
	/**
	* 在不删除表的情况下删除所有的行，<br>
	* @param table_name 操作的table名
	* @return cursor 数据库数据游标
	*/
	public ArrayList <HashMap <String, Object>> deleteAllData(String table_name)
	{
		try {
			db.execSQL("delete from " + table_name);
		}catch(SQLiteException  se){
			creatDbTable(table_name);
		}
		//数据格式转换为java通用格式，便于功能层与数据层分离
  		ArrayList <HashMap <String, Object>> mAppList = new ArrayList <HashMap <String, Object>>();
		return mAppList;
		
	}
	/**
	* 利用XML格式的菜单文件更新数据库菜谱table，<br>
	* @param file XML文件名
	* @param table_caidan 菜单table名
	* @param table_guize 规则table名
	*/
	public int XML2DB(File file, String table_caidan, String table_guize) {
		// TODO Auto-generated constructor stub
		int result = 0;
		try {
			deleteAllData(table_caidan);
		}catch(SQLiteException  se){
			creatDbTable(table_caidan);
		}
		try {
			deleteAllData(table_guize);
		}catch(SQLiteException  se){
			creatDbTable(table_guize);
		}
		try { 
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			Element tables = document.getDocumentElement();
			NodeList nl1 = tables.getElementsByTagName("table");
			System.out.println("table: " + nl1.getLength());
			for(int i = 0; i < nl1.getLength(); i++){
				Element ele1 = (Element)nl1.item(i);
				String name = ele1.getAttribute("name");
				if(name.equals(Constant.table_caidan)){
					int _id = 0;
					result = result|0x1;
					NodeList nl2 = ele1.getElementsByTagName("food_kind");
					System.out.println("food_kind: " + nl2.getLength());
					for(int j = 0; j < nl2.getLength(); j++){
						Element ele2 = (Element)nl2.item(j);
						String food_kind = ele2.getAttribute("name");
						NodeList nl3 = ele2.getElementsByTagName("food");
						System.out.println("food: " + nl3.getLength());
						for(int k = 0; k < nl3.getLength(); k++){
							Element ele3 = (Element)nl3.item(k);
							String food_cost = ele3.getAttribute("food_cost");
							String food_name = ele3.getAttribute("food_name");
							_id ++;
				  			db.execSQL("insert into " + table_caidan + " values(?, ?, ?, ?)" , 
									new Object[]{_id, food_kind, food_cost, food_name});
							
						}
					}
					addVersion(Constant.table_version, Constant.table_caidan);
				}
				else if(name.equals(Constant.table_guize)){
					result = result|0x2;
					NodeList nl2 = tables.getElementsByTagName("rule");
					System.out.println("in table_guize nl2.getLength(): " + nl2.getLength());
					for(int j = 0; j < nl2.getLength(); j++){
						Element ele2 = (Element)nl2.item(j);
//						String _id = ele2.getAttribute("_id");
//						String food_kind_no = ele2.getAttribute("food_kind_no");
						String order_set = ele2.getAttribute("order_set");
						String recommand_set = ele2.getAttribute("recommand_set");
						float conf = Float.valueOf(ele2.getAttribute("conf"));
			  			db.execSQL("insert into " + table_guize + " values(null, ?, ?, ?)" , 
								new Object[]{order_set, recommand_set, conf});
						
					}
					addVersion(Constant.table_version, Constant.table_guize);
				}
			}
		} catch (ParserConfigurationException e) { 
			System.out.println(e.getMessage()); 
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	/**
	* 将数据库中的点餐信息转换为XML格式文件<br>
	* @param table_name 操作的数据库table名
	* @return success 是否更新成功
	*/
	public boolean DB2XML(File file, String table_name) {
		// TODO Auto-generated constructor stub
		boolean success = false;
        try {
        	//生成DOM模型树
    		Document document;
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
    		DocumentBuilder builder;
    		builder = factory.newDocumentBuilder();
    		document = builder.parse(file);
    		Element root = document.createElement("root");
    		document.appendChild(root);
            Element table = document.createElement("table");
            table.setAttribute("name", "已点");
            // 从数据库中获取已点信息
            ArrayList<HashMap <String, Object>> list = selectAll(table_name);
            for(HashMap <String, Object> map:list){
            	Element food = document.createElement("food");
            	String mfood_cost = (String) map.get("food_cost");
            	String mfood_count = (String) map.get("food_count");
            	String mfood_name = (String) map.get("food_name");
            	food.setAttribute("food_cost", mfood_cost);
            	food.setAttribute("food_count", mfood_count);
            	food.setAttribute("food_name", mfood_name);
            	table.appendChild(food);
            }
            root.appendChild(table);
            //转换为XML文件
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(document);
            transformer.setOutputProperty(OutputKeys.ENCODING, "utf8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            PrintWriter pw = new PrintWriter(new FileOutputStream(file));
            StreamResult result = new StreamResult(pw);
            transformer.transform(source, result);
            System.out.println("生成XML文件成功!");
        } catch (TransformerConfigurationException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (TransformerException e) {
            System.out.println(e.getMessage());
        } catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}
	/**
	* 退出页面时关闭数据库对象
	*/
	public void close() {
		// TODO Auto-generated method stub
		//退出程序时关闭SQLiteDatabase
		if (cursor!= null && !cursor.isClosed())
		{
			cursor.close();
		}
		if (db != null && db.isOpen())
		{
			db.close();
		}
	}
}
