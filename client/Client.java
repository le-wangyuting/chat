package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import common.Common;
import common.Config;
import common.Message;

public class Client extends Thread {
	public ClientUI ui; // 表示画面
	public String name; // ユーザー名
	private Socket csocket; // クライアントソケット
	private ObjectInputStream in; // 入力
	private ObjectOutputStream out; // 出力

	public static void main(String[] args) {
		new Client(Config.IP, Config.PORT);
	}

	public Client(String server, int port) {
		try {
			csocket = new Socket(server, port);
			out = new ObjectOutputStream(csocket.getOutputStream());
			in = new ObjectInputStream(csocket.getInputStream());
		} catch (Exception e) {
			System.out.println(e.toString() + " client error");
			this.close();
			System.exit(0);
		}
		this.start();
	}

	public void run() {
		try {
			// ユーザー名を取得する
			while (name == null || name.equals(""))
				name = JOptionPane.showInputDialog("input name").trim();
			// 初回実行、送信
			send(new Message("login", name, null, null));
			// クライアント画面
			ui = new ClientUI(this, name + " のクライアント");
			// メッセージスクリプト
			while (true) {
				Message rcv = (Message) in.readObject(); //クライアントからのメッセージ
				System.out.println(rcv.toString());
				new ClientMsgType(this, rcv).process();
			}
		} catch (Exception e) {
			System.out.println(e.toString() + " 接続中止");
			close();
		}
	}

	/**
	 * クライアントからサーバーに送信
	 * 
	 * @param msg
	 *            メッセージ対象
	 */
	public void send(Message msg) {
		try {
			out.writeObject(msg);
			out.flush();
		} catch (Exception e) {
			System.out.println(e.toString() + " メッセージ変換エラー");
		}
	}

	/**
	 * メモリ解放
	 */
	public void close() {
		try {
			if (ui != null)
				ui.dispose();
			if (out != null)
				out.close();
			if (in != null)
				in.close();
			if (csocket != null)
				csocket.close();
		} catch (IOException e) {
			System.out.println(e.toString() + "メモリ解放失敗");
		}
	}
}

/**
 * クライアント画面
 * 
 *
 */
@SuppressWarnings("serial")
class ClientUI extends Frame {
	JTextArea msgArea; // メッセージ表示画面
	JTextField msgField; // 入力画面
	JComboBox<String> clientList; // オンラインユーザーリスト
	JComboBox<String> msgType;	// テキスト、ファイル
	JButton btn; // 送信
	JLabel cntLabel; // オンライン人数
	JScrollPane textAreaScrollPane;
	JPanel textFieldPanel = new JPanel();
	Client client;
	JButton clearBtn;

	public ClientUI(Client client, String winname) {
		super(winname); 
		setSize(600, 400);
		this.client = client;
	
		// テキスト表示画面
		msgArea = new JTextArea(400, 400);
		msgArea.setEditable(false);
		msgArea.setFont(new Font("ＭＳ ゴシック", Font.BOLD, 16));
		msgArea.setLineWrap(true);
		textAreaScrollPane = new JScrollPane(msgArea);
		add(textAreaScrollPane, BorderLayout.CENTER);
		// テキスト入力画面
		textFieldPanel.setLayout(new FlowLayout(0, 10, 10));
		add(textFieldPanel, BorderLayout.SOUTH);

		clientList = new JComboBox<String>(); // オンラインユーザーリスト
		clientList.addItem("All");
		textFieldPanel.add(clientList);
		
		msgType = new JComboBox<String>();	// メッセージタイプ
		msgType.addItem("chat");
		msgType.addItem("file");
		textFieldPanel.add(msgType);

		msgField = new JTextField(20); // テキスト
		textFieldPanel.add(msgField);

		btn = new JButton("send"); // 送信ボタン
		btn.setMnemonic(KeyEvent.VK_ENTER);
		clearBtn = new JButton("クリア");
		textFieldPanel.add(btn);
		textFieldPanel.add(clearBtn);

		cntLabel = new JLabel("Online:1"); // オンライン人数
		textFieldPanel.add(cntLabel);
		
		ImageIcon icon = new ImageIcon("C:\\workspace\\Test\\client\\icon.jpg");
	    setIconImage(icon.getImage());
		// 送信ボタンイベント
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String type = (String) msgType.getSelectedItem();
				String title = "";
				String content = getText().trim();
				byte[] fbyte = null;
				if (content.equals("")) {
					JOptionPane.showMessageDialog(client.ui, "please input text");
					return;
				}
				if (type.equals("file")) {
					// ファイル取得する
					JFileChooser dlg = new JFileChooser();
					dlg.setDialogTitle("file open");
					int result = dlg.showOpenDialog(client.ui);
					if (result == JFileChooser.APPROVE_OPTION) {
						File file = dlg.getSelectedFile();
						fbyte = Common.file2Byte(file);
						title = file.getName();
					} else {
						return;
					}
					append("TO " + getName() + ": " + title + "\n");
				}
				append("TO " + getName() + ": " + content + "\n");
				client.send(new Message(type, client.name, getName(), title, content, fbyte));
				clear();
			}
		});
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(client.ui, "exit?")) {
					client.close();
					System.exit(0);
				}
			}
		});
		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Clear the area
				msgArea.setText("");
			}
 
		});
		setVisible(true);
	}

	/**
	 * 入力欄のテキスト取得する
	 * 
	 * @return
	 */
	public String getText() {
		return msgField.getText().trim();
	}
	
	/**
	 * オンライン人数更新
	 * @param txt
	 */
	public void setText(String txt) {
		cntLabel.setText(txt);
	}

	/**         
	 * 指定ユーザー名取得する
	 */
	public String getName() {
		return (String) clientList.getSelectedItem();
	}

	/**
	 * オンライン人数取得する
	 * 
	 * @return
	 */
	public int getClientCount() {
		return clientList.getItemCount();
	}

	/**
	 * オンラインユーザーを追加する
	 * 
	 * @param name
	 *            
	 */
	public void addClient(String name) {
		clientList.addItem(name);
	}

	/**
	 * ユーザーを削除する
	 * 
	 * @param name
	 *            
	 */
	public void removeClient(String name) {
		clientList.removeItem(name);
	}

	/**
	 * 入力欄をクリアする
	 */
	public void clear() {
		msgField.setText(" ");
	}

	/**
	 *テキストを表示画面に追加する
	 * 
	 * @param txt
	 *           
	 */
	public void append(String txt) {
		msgArea.append(txt);
	}
	  
}
