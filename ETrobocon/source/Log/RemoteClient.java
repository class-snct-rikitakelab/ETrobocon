// PC側の送受信

package jp.etrobo.ev3.pcsample;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import lejos.utility.Delay;

/**
 *
 */
public class RemoteClient extends Frame implements KeyListener {
	private static final long serialVersionUID = 1630664954341554884L;

	public static final int PORT = 7360;

	public static final int CLOSE = 0;
	public static final int START = 71; // 'g'
	public static final int STOP = 83;  // 's'

	Button btnConnect;
	TextField txtIPAddress;
	TextArea messages;

	private Socket socket;
	private DataOutputStream outStream;
	private DataInputStream inputStream;//

	public RemoteClient(String title, String ip) {
		super(title);
		this.setSize(400, 300);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("Ending Warbird Client");
				disconnect();
				System.exit(0);
			}
		});
		buildGUI(ip);
		this.setVisible(true);
		btnConnect.addKeyListener(this);
	}

	public static void main(String args[]) {
		String ip = "10.0.1.1";
		if (args.length > 0) ip = args[0];
		System.out.println("Starting Client...");
		new RemoteClient("LeJOS EV3 Client Sample", ip);
	}

	public void buildGUI(String ip) {
		Panel mainPanel = new Panel(new BorderLayout());
		ControlListener cl = new ControlListener();
		btnConnect = new Button("Connect");
		btnConnect.addActionListener(cl);
		txtIPAddress = new TextField(ip, 16);
		messages = new TextArea("status: DISCONNECTED");
		messages.setEditable(false);
		Panel north = new Panel(new FlowLayout(FlowLayout.LEFT));
		north.add(btnConnect);
		north.add(txtIPAddress);
		Panel center = new Panel(new GridLayout(5, 1));
		center.add(new Label("G to start, S to stop"));
		Panel center4 = new Panel(new FlowLayout(FlowLayout.LEFT));
		center4.add(messages);
		center.add(center4);
		mainPanel.add(north, "North");
		mainPanel.add(center, "Center");
		this.add(mainPanel);
	}

	private void sendCommand(int command) {
		messages.setText("status: SENDING command.");
		try {
			outStream.writeInt(command);
			messages.setText("status: Comand SENT");
		} catch(IOException io) {
			messages.setText("status: ERROR Probrems occurred sending data.");
		}
	}

	private class ControlListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command.equals("Connect")) {
				try {
					socket = new Socket(txtIPAddress.getText(), PORT);
					outStream = new DataOutputStream(socket.getOutputStream());
					inputStream = new DataInputStream(socket.getInputStream());//
					messages.setText("status: CONNECTED");
					btnConnect.setLabel("Disconnect");
				} catch (Exception exc) {
					messages.setText("status: FAILURE Error establishing connection with EV3.");
					System.out.println("Error" + exc);
				}
			} else if (command.equals("Disconnect")) {
				disconnect();
			}
		}
	}

	public void disconnect() {
		try {
			sendCommand(CLOSE);
			socket.close();
			btnConnect.setLabel("Connect");
			messages.setText("status: DISCONNECTED");
		} catch (Exception exc) {
			messages.setText("status: FAILURE Error closing connection with EV3.");
			System.out.println("Error" + exc);
		}
	}

	public void keyPressed(KeyEvent e) {
		sendCommand(e.getKeyCode());
		System.out.println("Pressed " + e.getKeyCode());
		if(e.getKeyCode() == KeyEvent.VK_L){
			receiveLog();
		}
	}


	public void receiveLog(){

		Delay.msDelay(100);//log側が20ごとにコマンド入力の確認しているのでそれより大きく
		try {
			socket = new Socket(txtIPAddress.getText(), PORT);
			// outStream = new DataOutputStream(socket.getOutputStream());
			inputStream = new DataInputStream(socket.getInputStream());
			messages.setText("status: RACEIVE LOG");
			btnConnect.setLabel("Disconnect");
		} catch (Exception exc) {
			messages.setText("status: FAILURE Error establishing connection with EV3.");
			System.out.println("Error" + exc);
		}
		String str;
		File file = new File("log.txt");
		PrintWriter pw;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		} catch (IOException e1) {
			e1.printStackTrace();
			messages.setText("status: log file can't write");
			return;
		}

		try{//読み込めるだけ読み込んでエラーで抜ける
			for(;;){
				str = inputStream.readUTF();
				pw.println(str);
			}
		}
		catch (IOException e){
			e.printStackTrace();
			messages.setText("status: RACEIVE END");
		}
		pw.close();
	}


	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent arg0) {}

}
