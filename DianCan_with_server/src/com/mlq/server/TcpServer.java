package com.mlq.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer {

    /** *//**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        int num = args.length;
        int port = 5456;
        if(num<=0){
            System.out.println("use default server port!");
//            System.exit(0);
        }
        else{
        	port = Integer.parseInt(args[0]);
        }
        System.out.println("server port:"+ port);
        //绑定接受数据端口
        ServerSocket s = new ServerSocket( port);
        while(true){
            Socket socket = s.accept();
            new TcpFileThread(socket).start();
//            new TcpXmlThread(socket).start();
        }
    }

}