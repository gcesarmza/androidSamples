package com.gustavogenovese.pushNotificationsServer;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import javax.swing.table.DefaultTableModel;

public class MainWindow {

	private HttpServer httpServer = null;
	private List<User> users = null;

	private JFrame frmAndroidPushNotifications;
	private final JLabel lblLabel1 = new JLabel("Server status:");
	private JTextField addUserUsername;
	private JTable table;
	private JPasswordField addUserPassword;

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
		users = UsersDAO.getInstance().listUsers();
		initialize();
		refreshUsersTable();
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
	private void refreshUsersTable(){
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("ID");
		model.addColumn("Username");
		model.addColumn("Reg ID");
		for (User user: users){
			model.addRow(new String[] {
				user.getId(), user.getUsername(), user.getRegistrationId()
			});
		}
		table.setModel(model);
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAndroidPushNotifications = new JFrame();
		frmAndroidPushNotifications.setResizable(false);
		frmAndroidPushNotifications
				.setTitle("Android push notifications server");
		frmAndroidPushNotifications.setBounds(100, 100, 450, 419);
		frmAndroidPushNotifications
				.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAndroidPushNotifications.getContentPane().setLayout(null);
		lblLabel1.setBounds(12, 0, 101, 30);
		frmAndroidPushNotifications.getContentPane().add(lblLabel1);

		final JLabel lblServerStatus = new JLabel("Stopped");
		lblServerStatus.setBounds(139, 8, 295, 15);
		frmAndroidPushNotifications.getContentPane().add(lblServerStatus);

		final JButton btnStart = new JButton("Start");
		btnStart.setBounds(10, 29, 117, 25);
		frmAndroidPushNotifications.getContentPane().add(btnStart);

		final JButton btnStop = new JButton("Stop");
		btnStop.setEnabled(false);
		btnStop.setBounds(139, 29, 117, 25);
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
		separator.setBounds(12, 66, 422, 2);
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
		lblPassword.setBounds(12, 134, 70, 15);
		frmAndroidPushNotifications.getContentPane().add(lblPassword);

		addUserPassword = new JPasswordField();
		addUserPassword.setBounds(139, 132, 117, 19);
		frmAndroidPushNotifications.getContentPane().add(addUserPassword);

		JButton btnAddUser = new JButton("Add user");
		btnAddUser.setBounds(12, 161, 117, 25);
		frmAndroidPushNotifications.getContentPane().add(btnAddUser);

		btnAddUser.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				String username = addUserUsername.getText();
				String password = new String(addUserPassword.getPassword());
				UsersDAO.getInstance().addUser(username, password);

				addUserUsername.setText("");
				addUserPassword.setText("");
				// refresh table
				users = UsersDAO.getInstance().listUsers();
				refreshUsersTable();
			}
		});

		table = new JTable();
		table.setRowSelectionAllowed(false);
		table.setShowVerticalLines(false);
		table.setModel(new DefaultTableModel());
		table.setBounds(12, 198, 422, 182);
		frmAndroidPushNotifications.getContentPane().add(table);
	}
}
