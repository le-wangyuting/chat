package client;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import common.Common;
import common.Config;
import common.Message;

public class ClientMsgType {
	Client client;
	Message msg;
	SimpleDateFormat curtime;	

	public ClientMsgType(Client client, Message msg) {
		this.client = client;
		this.msg = msg;
		this.curtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
	}

	/**
	 * 受信メッセージ処理
	 */
	public void process() {
		try {
			Method method = this.getClass().getDeclaredMethod(msg.getType()); // 指定メソッドを取得する
			method.invoke(this);
			client.ui.setText("Online:" + (client.ui.getClientCount())); // オンライン人数更新
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}

	/**
	 * チャットメッセージ処理
	 */
	public void chat() {
		String toAll = "[public]";
		if (msg.getTo().equals(client.name))
			toAll = "[private]";
		client.ui.append(toAll + msg.getFrom() + ": " + msg.getContent() + "\n");
	}

	/**
	 * ファイル処理
	 */
	public void file() {
		String toAll = "[public]";
		if (msg.getTo().equals(client.name))
			toAll = "[private]";
		client.ui.append(toAll + msg.getFrom() + ": " + msg.getContent() + "\n");
		int confirm = JOptionPane.showConfirmDialog(client.ui,
				  msg.getFrom() + "file:" + msg.getTitle() + "Download?");
		if (confirm != JOptionPane.YES_OPTION)
			return;
		// 保存場所を選択する
		JFileChooser dlg = new JFileChooser();
		dlg.setDialogTitle("名前を付けて保存");
		dlg.setSelectedFile(new File(msg.getTitle()));
		int result = dlg.showSaveDialog(client.ui);
		if (result != JFileChooser.APPROVE_OPTION)
			return;
		File file = dlg.getSelectedFile();
		if (file.exists()) {
			int copy = JOptionPane.showConfirmDialog(null, "上書きしますか", "保存", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (copy == JOptionPane.YES_OPTION) {
				dlg.approveSelection();
			}
		} else {
			dlg.approveSelection();
		}
		if (Common.byte2File(msg.getFile(), file)) {
			JOptionPane.showMessageDialog(client.ui, "保存成功");
		} else {
			JOptionPane.showMessageDialog(client.ui, "保存失敗");
		}
	}

	/**
	 * オンラインアラーム
	 */
	public void online() {
		client.ui.addClient(msg.getFrom());
		if (msg.getContent() != null)
			client.ui.append(msg.getFrom() + msg.getContent() + "\n");
	}

	/**
	 * オフラインアラーム
	 */
	public void offline() {
		client.ui.removeClient(msg.getFrom());
		if (msg.getContent() != null)
			client.ui.append(msg.getFrom() + msg.getContent() + "\n");
	}

	/**
	 * 登録精査失敗
	 */
	public void login() {
		client.ui.setVisible(false);
		JOptionPane.showMessageDialog(client.ui, msg.getContent());
		System.out.println(msg.getContent());
		client.close();
		new Client(Config.IP, Config.PORT);
	}
}
