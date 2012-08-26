package com.gustavogenovese.pushNotificationsServer;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class UsersDAO {
	
	private static UsersDAO instance = null;
	private Connection connection;
	
	public synchronized static UsersDAO getInstance(){
		if (instance == null){
			instance = new UsersDAO();
		}
		return instance;
	}

	private static String getUserDataDirectory(){
		String path = System.getProperty("user.home") + 
	    		File.separator + ".androidPushNotificationsServer" +
	    		File.separator;
		File dir = new File(path);
		if (!dir.exists()){
			System.out.println("Data directory not found, creating...");
			dir.mkdirs();
		}
	    return path;
	}
	
	private UsersDAO(){
		try {
			String dbFile = getUserDataDirectory() + "data.db";
			Class.forName("org.sqlite.JDBC");
	        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
	        
	        Statement stat = connection.createStatement();
	        stat.executeUpdate("create table if not exists users " +
	        					"(id varchar(36) not null primary key," +
	        					"username varchar(255) unique not null," +
	        					"password varchar(255) not null," +
	        					"registrationId varchar(2048));");
	        
		}catch(Exception ex){
			System.out.println("Error when creating DAO: " + ex.getMessage());
		}
	}
	
	public void addUser(String username, String password){
		if (username == null || username.trim().length() == 0 ||
			password == null || password.trim().length() == 0){
			return;
		}

		try {
			PreparedStatement prep = connection.prepareStatement("select * from users where username=?");
			prep.setMaxRows(1);
			prep.setString(1, username);
			ResultSet rs = prep.executeQuery();
			boolean exists = rs.next();
			rs.close();
			prep.close();
			
			if (!exists){
				//create user
				PreparedStatement ps = connection.prepareStatement("insert into users (id, username, password) values (?,?,?);");
				ps.setString(1, UUID.randomUUID().toString());
				ps.setString(2, username);
				ps.setString(3, password);
				ps.executeUpdate();
			}

		}catch(SQLException ex){
			System.out.println("Error when adding user: " + ex.getMessage());
		}
	}
	
	public List<User> listUsers(){
		try {
			List<User> users = new LinkedList<User>();
			Statement stat = connection.createStatement();
			ResultSet rs = stat.executeQuery("select * from users;");
	        while (rs.next()) {
	        	User user = new User();
	        	user.setId(rs.getString("id"));
	        	user.setUsername(rs.getString("username"));
	        	user.setPassword(rs.getString("password"));
	        	user.setRegistrationId(rs.getString("registrationId"));
	        	users.add(user);
	        }
	        rs.close();
	        return users;
		}catch(SQLException ex){
			System.out.println("Error when retrieving users: " + ex.getMessage());
			return Collections.emptyList();
		}
		
	}

	public void disconnect(){
		try {
			connection.close();
			instance = null;
		}catch(SQLException ex){
			System.out.println("Error when disconnecting from database: " + ex.getMessage());
		}
	}
}
