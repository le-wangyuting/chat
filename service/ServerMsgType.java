package service;

import java.lang.reflect.Method;

import common.Message;

public class ServerMsgType {
	ServerHandler server;
	Message msg;


	

	public ServerMsgType(ServerHandler server, Message msg) {
		this.server = server;
		this.msg = msg;
		
	
	}

	public void process() {
		try {
		
			Method method = this.getClass().getDeclaredMethod(msg.getType());
			method.invoke(this);
		} catch (Exception e) {
			System.out.println(e.toString() + " サーバ初期化失敗");
			try {
				Method method = this.getClass().getDeclaredMethod("chat");
				method.invoke(this);
			} catch (Exception e1) {
				System.out.println(e1.toString() + " 処理失敗");
			}
			
		}
	}
	
	

	public void chat() {
		if (msg.getTo().equals("All")) {
			// 全員に送信する
			server.sendAllClient(msg);
		} else {
			// 指定する人に送信
			server.sendToClient(msg);
		}
	}

	public void online() {
		server.sendClientList(msg);
	} 

	public void offline() {
		server.sendAllClient(msg);
	}

	public void login() {
		server.sendToClient(msg);
	}
	

}
