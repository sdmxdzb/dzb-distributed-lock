package com.fxiaoke.dzb.distributedlock.util;

/**
 * @author dzb
 * @date 2018年8月21日 下午3:50:10
 * @description  
 * @version 1.0.0
 */
public class PrinterMessage {
	private static int print=0;
	
	public  static void printerMessage(String  message){
		print++;
		System.out.println("打印第"+print+"份文档，内容为:"+message);
	}

}