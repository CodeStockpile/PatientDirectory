package com.patient;

import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class DatabaseManager extends JFrame{
	public static final String URL = "jdbc:mysql://localhost:3306/mydatabase";
	public static String USER = "root";
	public static String PASSWORD = "password@123";

	
	public DatabaseManager() throws HeadlessException, SQLException {
		Connection connect = DriverManager.getConnection(URL,USER, PASSWORD);
	}


	

}
