package com.gustavogenovese.pushNotificationsServer;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

public class MainWindow implements UserRegisteredListener{
	private static final long serialVersionUID = 1L;
	
	private HttpServer httpServer = null;
	private List<User> users = null;
	private Sender sender;

	private JFrame frmAndroidPushNotifications;
	private final JLabel lblLabel1 = new JLabel("Server status:");
	private JTextField addUserUsername;
	private JTable table;
	private JPasswordField addUserPassword;
	private JButton btnRemoveUser;
	private JButton btnSendToSelected;
	private JTextField textField;
	
	private final static String myApiKey = "AIzaSyCTy1UZ2CF7Mp6k-mP6D2TcWtdVOUBr0Q4";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frmAndroidPushNotifications.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		users = UsersDAO.getInstance().listUsers(false);
		initialize();
		refreshUsersTable();
		ListenersManager.getInstance().addListener(this);
		sender = new Sender(myApiKey);
	}

	/**
	 * Starts an HTTP server to receive the key from the device
	 * 
	 * @return whether the server was successfully started
	 */
	private boolean startServer() {
		URI uri = UriBuilder.fromUri("http://localhost/").port(16000).build();
		System.out.println("Starting Grizzly...");

		if (httpServer == null) {
			ResourceConfig rc = new PackagesResourceConfig(
					"com.gustavogenovese.pushNotificationsServer");
			try {
				httpServer = GrizzlyServerFactory.createHttpServer(uri, rc);
			} catch (IOException ex) {
			}
		} else {
			return true;
		}

		return httpServer != null && httpServer.isStarted();
	}

	/**
	 * Stops the HTTP server
	 * 
	 * @return whether the server was successfully stopped
	 */
	private boolean stopServer() {
		if (httpServer == null) {
			return true;
		}
		httpServer.stop();
		boolean ret = !httpServer.isStarted();
		httpServer = null;
		return ret;
	}

	/**
	 * Reloads the grid with all the registered users
	 */
	private void refreshUsersTable() {
		DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;
			
			boolean[] columnEditables = new boolean[] { false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		model.addColumn("Username");
		model.addColumn("Reg ID");

		for (User user : users) {
			model.addRow(new String[] { user.getUsername(),	user.getRegistrationId() });
		}
		
		table.setModel(model);
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent event) {
				if (!event.getValueIsAdjusting()) {
					int index = table.getSelectionModel().getMinSelectionIndex();
					btnRemoveUser.setEnabled(index>=0);
					
					if (index >= 0){
						String regId = (String) table.getModel().getValueAt(index, 1);
						btnSendToSelected.setEnabled(regId != null && regId.trim().length() > 0);
					} else {
						btnSendToSelected.setEnabled(false);
					}
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAndroidPushNotifications = new JFrame();
		frmAndroidPushNotifications.setResizable(false);
		frmAndroidPushNotifications
				.setTitle("Android push notifications server");
		frmAndroidPushNotifications.setBounds(100, 100, 502, 429);
		frmAndroidPushNotifications
				.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAndroidPushNotifications.getContentPane().setLayout(null);
		lblLabel1.setBounds(12, 0, 101, 30);
		frmAndroidPushNotifications.getContentPane().add(lblLabel1);

		final JLabel lblServerStatus = new JLabel("Stopped");
		lblServerStatus.setBounds(139, 8, 295, 15);
		frmAndroidPushNotifications.getContentPane().add(lblServerStatus);

		final JButton btnStart = new JButton("Start");
		btnStart.setBounds(10, 29, 252, 25);
		frmAndroidPushNotifications.getContentPane().add(btnStart);

		final JButton btnStop = new JButton("Stop");
		btnStop.setEnabled(false);
		btnStop.setBounds(274, 29, 212, 25);
		frmAndroidPushNotifications.getContentPane().add(btnStop);

		/**
		 * Button pressed to start the server
		 */
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (startServer()) {
					btnStop.setEnabled(true);
					btnStart.setEnabled(false);
					lblServerStatus.setText("Started");
				}
			}
		});

		/**
		 * Button pressed to stop the server
		 */
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (stopServer()) {
					btnStop.setEnabled(false);
					btnStart.setEnabled(true);
					lblServerStatus.setText("Stopped");
				}
			}
		});
		JSeparator separator = new JSeparator();
		separator.setBounds(12, 66, 474, 2);
		frmAndroidPushNotifications.getContentPane().add(separator);

		JLabel lblAddUsers = new JLabel("Add users:");
		lblAddUsers.setBounds(12, 80, 117, 15);
		frmAndroidPushNotifications.getContentPane().add(lblAddUsers);

		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(12, 107, 101, 15);
		frmAndroidPushNotifications.getContentPane().add(lblUsername);

		addUserUsername = new JTextField();
		addUserUsername.setBounds(139, 105, 117, 19);
		frmAndroidPushNotifications.getContentPane().add(addUserUsername);
		addUserUsername.setColumns(10);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(274, 107, 70, 15);
		frmAndroidPushNotifications.getContentPane().add(lblPassword);

		addUserPassword = new JPasswordField();
		addUserPassword.setBounds(362, 105, 124, 19);
		frmAndroidPushNotifications.getContentPane().add(addUserPassword);

		JButton btnAddUser = new JButton("Add user");
		btnAddUser.setBounds(12, 134, 474, 25);
		frmAndroidPushNotifications.getContentPane().add(btnAddUser);

		btnAddUser.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				String username = addUserUsername.getText();
				String password = new String(addUserPassword.getPassword());
				UsersDAO.getInstance().addUser(username, password);

				addUserUsername.setText("");
				addUserPassword.setText("");
				// refresh table
				users = UsersDAO.getInstance().listUsers(false);
				refreshUsersTable();
			}
		});

		table = new JTable();
		table.setFillsViewportHeight(true);
		table.setShowVerticalLines(false);
		table.setModel(new DefaultTableModel());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		javax.swing.JScrollPane jScrollPane = new javax.swing.JScrollPane();
		jScrollPane.setBounds(12, 171, 332, 115);
		jScrollPane.setViewportView(table);
		frmAndroidPushNotifications.getContentPane().add(jScrollPane);
		
		btnRemoveUser = new JButton("Remove");
		btnRemoveUser.setBounds(362, 203, 124, 48);
		btnRemoveUser.setEnabled(false);
		frmAndroidPushNotifications.getContentPane().add(btnRemoveUser);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(12, 305, 474, 2);
		frmAndroidPushNotifications.getContentPane().add(separator_1);
		
		JLabel lblTextToSend = new JLabel("Text to send:");
		lblTextToSend.setBounds(12, 319, 115, 15);
		frmAndroidPushNotifications.getContentPane().add(lblTextToSend);
		
		textField = new JTextField();
		textField.setBounds(139, 319, 347, 19);
		frmAndroidPushNotifications.getContentPane().add(textField);
		textField.setColumns(10);
		
		btnSendToSelected = new JButton("Send to selected device");
		btnSendToSelected.setEnabled(false);
		btnSendToSelected.setBounds(12, 346, 250, 25);
		frmAndroidPushNotifications.getContentPane().add(btnSendToSelected);
		btnSendToSelected.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				int index = table.getSelectionModel().getMinSelectionIndex();
				if (index < 0)
					return;
				String regId = (String) table.getModel().getValueAt(index, 1);
				sendMessageToSelectedUser(regId, textField.getText());
			}
		});
		
		JButton btnSendToAll = new JButton("Send to all devices");
		btnSendToAll.setBounds(274, 346, 212, 25);
		frmAndroidPushNotifications.getContentPane().add(btnSendToAll);
		btnSendToAll.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				sendMessageToAll(textField.getText());
			}
		});
		
		btnRemoveUser.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent event) {
				int selIndex = table.getSelectionModel().getMinSelectionIndex();
				if (selIndex < 0){
					return;
				}
				UsersDAO.getInstance().deleteUser(table.getModel().getValueAt(selIndex, 0).toString());
				users = UsersDAO.getInstance().listUsers(false);
				refreshUsersTable();
			}
		});
	}

	@Override
	public void userRegistered(String username, String registrationId) {
		users = UsersDAO.getInstance().listUsers(false);
		refreshUsersTable();
	}
	
	private void sendMessageToSelectedUser(String regId, String text){
		Message message = new Message.Builder()
							.addData("message", text)
							.build();
		try {
			Result result = sender.sendNoRetry(message, regId);

		} catch (IOException e) {
			System.out.println("Error sending message to device: " + e.getMessage());
		}
	}
	
	private void sendMessageToAll(String text){
		List<User> registeredUsers = UsersDAO.getInstance().listUsers(true);
		Message message = new Message.Builder()
			.addData("message", text)
			.build();
		List<String> regIDs = new LinkedList<String>();
		for (User user: registeredUsers){
			regIDs.add(user.getRegistrationId());
		}
		try {
			MulticastResult result = sender.sendNoRetry(message, regIDs);
		} catch (IOException e) {
			System.out.println("Error sending message to all devices: " + e.getMessage());
		}
	}
}
