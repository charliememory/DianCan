package com.mlq.diancan;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class TcpSocket  {	
	
	private static int FrameHeadLen = 64;
	private static int FileNameLen = 256;
	private static int SizeLen = 32;
	private Socket msocket = null;
	private InputStream ins = null;
	private OutputStream outs = null;
	
	public TcpSocket(String serverIP, int severPort){
		while(null == msocket){
			try {
				InetAddress addr = InetAddress.getByName(serverIP);
		        msocket = new Socket(addr, severPort);
	//	        msocket.
		        outs = msocket.getOutputStream();
		        ins = msocket.getInputStream();
			} catch (Exception e) {
				// do nothing
			}
		}
	}
	
	public TcpSocket(Socket socket) throws IOException{
		msocket = socket;
        outs = msocket.getOutputStream();
        ins = msocket.getInputStream();
	}
	
	public void close(){
		try {
			if(null != msocket){
				msocket.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//从输入流中获取以'#'结束的信息
	public void sendTaskName(String task) throws IOException{		
		byte[] content = creamtInfoBytes(task, Constant.TaskLength);
		outs.write(content,0,content.length);
        outs.flush();
	}
	
	//从输入流中获取以'#'结束的信息
	public String getFrameInfo(int length) throws IOException{
		byte[] info = new byte[length];
		int b = 0;
        while(b < info.length) {
            b += ins.read(info, b, info.length-b);
        }
        int ends = 0;
        for(int i = 0; i < info.length; i++) {
            if(info[i]=='#') {
                ends = i;
                break;
            }
        }
        return new String(info,0,ends,"UTF-8");
	}
	
	//将信息以'#'结束的方式创建字节数组，以写入tcp流
	public byte[] creamtInfoBytes(String info, int length) throws IOException{
		byte[] frame = new byte[length];
        byte[] temp = info.getBytes();
        for(int i=0;i<temp.length;i++) {
        	frame[i] = temp[i];
        }
        frame[temp.length] = '#';
        return frame;
	}
	
//*************************  file 相关  ************************
    public void sendFileFrame(String filefrom, String fileto)throws Exception{
    	//构造应用层传输协议 head + fileName + fileSize + fileContent
    	//****** head ******
        byte[] head = creamtInfoBytes("FileUpdate", FrameHeadLen);
        outs.write(head,0,head.length);
        //****** fileName ******
        byte[] file = creamtInfoBytes("fileto", FileNameLen);
        outs.write(file,0,file.length);
        //****** fileSize ******
        File filein = new File(filefrom);
        System.out.println("filein.length:" + filein.length());
        byte[] size = creamtInfoBytes(""+filein.length(), SizeLen); 
        outs.write(size,0,size.length);
        //fileContent
        FileInputStream fis = null;
        byte[] buf = new byte[1024*10];
        fis = new FileInputStream(filein);
        int readsize = 0;
        while((readsize = fis.read(buf, 0, buf.length))>0) {
            outs.write(buf,0,readsize);
            outs.flush();
        }
        fis.close();
    }
    
    public void receiveFile(String fileDirPath)throws Exception{
    	try {
            while(true)
            {
                String filename = getFrameInfo(FileNameLen);
                filename = fileDirPath + filename;
                File fileout = new File(filename);
                File fileParent = new File(fileout.getParent());
                fileParent.mkdirs();//保证文件夹存在
                if(fileout.isFile()) {
                    throw new Exception("file exists"+fileout.getAbsolutePath());
                }
                FileOutputStream fos = new FileOutputStream(fileout);                
                //****** fileSize ******
                String filesizes = getFrameInfo(SizeLen);
                System.out.println("filesize:"+filesizes);
                int ta = Integer.parseInt(filesizes);                
                //****** fileContent ******
                byte[] buf = new byte[1024*10];
                while(true) {
                    if(ta==0) {
                        break;
                    }
                    int len = ta;
                    if(len>buf.length) {
                        len = buf.length;
                    }
                    int rlen = ins.read(buf, 0, len);
                    ta -= rlen;
                    if(rlen>0) {
                        fos.write(buf,0,rlen);
                        fos.flush();
                    }
                    else {
                    	System.out.println("ta:" + ta);
                        break;
                    }
                }
                fos.close();
                break;
            }
        }catch(Exception e) {
            e.printStackTrace();            
        }
    }
    
//***************************************************************************
	
}