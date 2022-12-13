package service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import common.Config;
import common.Message;

public class Server {

	public Server(int port) {
		try {
			@SuppressWarnings("resource")
			ServerSocket server = new ServerSocket(port);
			while (true) {
				Socket conn = server.accept(); // ソケット立ち上げ
				new Thread(new ServerHandler(conn)).start(); // 双方向通信
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
	

	public static void main(String[] args) {
		new Server(Config.PORT);
	}
}

class ServerHandler implements Runnable {
	private ObjectInputStream in; // 入力
	private ObjectOutputStream out; // 出力
	private String name; // ユーザー名
	private SimpleDateFormat curtime;	
	
	private static ArrayList<ServerHandler> clientList = new ArrayList<ServerHandler>(); // ���û��̷߳����������
	private static HashMap<String, ServerHandler> clientMap = new HashMap<>(); // ���û����������̹߳������ڲ���

	public ServerHandler(Socket socket) {
		try {
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println(e.toString() + " サーバ初期化失敗");
		}
	}

	public void run() {
		if (!loginVerify())
			return; // 登録精査
		try {
			
			addClient(); // クライアントをリストに追加する
			sendMessage(new Message("online", name, "All" ,"入室しました!")); // オンラインアラーム
			while (true) {
				Message rcv = (Message) in.readObject(); // クライアントからのメッセージ
				sendMessage(rcv);
			}
		} catch (Exception e) {
			System.out.println(e.toString() + " クライアント退出しました");
		} finally {
			removeClient();
			sendMessage(new Message("offline", name, "All", "退室しました")); // オフラインアラーム֪
		}
	}



	/**
	 * 登録精査
	 * 
	 * @return
	 */
	private Boolean loginVerify() {
		try {
			Message rcv = (Message) in.readObject();
			System.out.println(rcv.toString());
			this.name = rcv.getFrom();
			if (clientMap.containsKey(name)) {
				// 同じユーザー名入力
				sendToClient(this, new Message("login", null, name, "名前すでに存在します"));
				return false;
			}
		} catch (Exception e) {
			System.out.println(e.toString() + "登録精査失敗");
			return false;
		}
		return true;
	}

	/**
	 * ユーザーをリストに追加する
	 * 
	 * @return
	 */
	private Boolean addClient() {
		if (clientMap.containsKey(name))
			return false;
		clientList.add(this);
		clientMap.put(name, this);
		return true;
	}

	/**
	 * ユーザー削除する
	 * 
	 * @return
	 */
	private Boolean removeClient() {
		clientList.remove(this);
		clientMap.remove(name);
		return true;
	}

	/**
	 * 送信
	 * 
	 * @param msg
	 * @throws IOException
	 */
	public void sendMessage(Message msg) {
		
		new ServerMsgType(this, msg).process();
	}

	/**
	 * 全員に送信する
	 * 
	 * @param msg
	 */
	public void sendAllClient(Message msg) {
		String name = msg.getFrom();
		ServerHandler sh = clientMap.get(name);
		synchronized (clientList) {
			for (ServerHandler client : clientList) {
				if (client != sh) {
					sendToClient(client, msg);
				}
			}
		}
	}

	/**
	 * 指定する人に送信
	 * 
	 * @param msg
	 */
	public void sendToClient(Message msg) {
		String name = msg.getTo();
		ServerHandler client = clientMap.get(name);
		sendToClient(client, msg);
	}

	/**
	 * 指定スレッドに送信
	 * 
	 * @param client
	 * @param msg
	 */
	public void sendToClient(ServerHandler client, Message msg) {
		try {
			client.out.writeObject(msg);
			client.out.flush();
			System.out.println("send to client: " + msg);
		} catch (IOException e) {
			System.out.print(e.toString() + " クライアント送信失敗");
//			client.interrupt();
		}
	}

	/**
	 * オンライン人数更新
	 * 
	 * @param msg
	 */
	public synchronized void sendClientList(Message msg) {
		//新ユーザー名
		ServerHandler newclient = clientMap.get(msg.getFrom());
		for (ServerHandler client : clientList) {
			if (client == newclient)
				continue;
			// 新ユーザーオンライン人数更新
			sendToClient(newclient, new Message(msg.getType(), client.name, newclient.name, null));
			// 全員オンライン人数更新
			sendToClient(client, new Message(msg.getType(), newclient.name, client.name, msg.getContent()));
		}
	}

}
