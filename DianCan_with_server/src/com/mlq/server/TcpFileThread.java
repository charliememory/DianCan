package com.mlq.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class TcpFileThread extends Thread{
    Socket socket = null;
    public TcpFileThread(Socket socket){
        this.socket = socket;
    }
    public void run(){
        try {
        	String filefrom = "CaiDan.xml";
        	putFiles(socket.getOutputStream(), filefrom, null);
        	filefrom = "CaiDan2.xml";
        	putFiles(socket.getOutputStream(), filefrom, null);
            socket.close();
        }catch(Exception e) {
            e.printStackTrace();
            
        }
    }
    public void putFiles(OutputStream outs,String filefrom,
    		String fileto)throws Exception{
    	//构造应用层传输协议 cmd+filesize+filein
        byte[] cmd = new byte[64];
        byte[] tcmd = "FileUpdate".getBytes();
        for(int i=0;i<tcmd.length;i++) {
            cmd[i] = tcmd[i];
        }
        cmd[tcmd.length] = '#';
        outs.write(cmd,0,cmd.length);
        //文件大小
        File filein = new File(filefrom);
        System.out.println("filein.length:" + filein.length());
        byte[] size = new byte[32];
        byte[] tsize = (""+filein.length()).getBytes();
        for(int i=0;i<tsize.length;i++) {
            size[i] = tsize[i];
        }
        size[tsize.length] = '#';
        outs.write(size,0,size.length);
        //文件内容
        FileInputStream fis = null;
        byte[] buf = new byte[1024*10];
        //char[] bufC = new char[1024*10];
        fis = new FileInputStream(filein);
        int readsize = 0;
        //OutputStream ops = socket.getOutputStream();
        while((readsize = fis.read(buf, 0, buf.length))>0) {
            outs.write(buf,0,readsize);
            outs.flush();
        }
        fis.close();
    }
}