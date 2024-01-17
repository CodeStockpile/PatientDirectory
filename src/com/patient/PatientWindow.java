package com.patient;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EventObject;

public class PatientWindow extends JFrame {

	private PatientTableModel patientModel;
	private JTextField idField;
	private JTextField firstNameField;
	private JTextField lastNameField;
	private JComboBox genderComboBox;
	private JCheckBox insuranceCheckBox;
	private JTextArea addressField;
	
	private DatabaseManager dbm;

	private JTable tableModel = new JTable(patientModel);

	public PatientWindow() {
		super("Patient Information");
		setSize(600, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		setLocationRelativeTo(null);

		// initialize patient model
		patientModel = new PatientTableModel();

		// initialize all the panels
		JPanel inputPanel = createInputPanel();
		tableModel = createTable();
		JScrollPane tableScrollPane = new JScrollPane(tableModel);
		tableScrollPane.setBorder(BorderFactory.createTitledBorder("Patient Information "));
		JPanel btnPanel = submitButtonPanel();
		


		// Continuously listen to the cell data and update the database on change
		patientModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();

				// Only update the database if the change occurred in a valid cell
				if (row != TableModelEvent.HEADER_ROW && column != TableModelEvent.ALL_COLUMNS) {
					updatePatientInDatabase(row, column);
				}
			}
		});
		
		
		// --------------- DELETE BUTTON --------------------------------------
		
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedPatients();
            }
        });
        
        //---------------- REFRESH BUTTON -----------------------
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				patientModel.clearPatitents();
				createTable();
			}
        	
        });
        
        btnPanel.add(refreshButton);
        btnPanel.add(deleteButton);
		add(inputPanel);
		add(btnPanel);
		add(tableScrollPane);
		setVisible(true);
		
		firstNameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(!Character.isLetter(e.getKeyChar())) {e.consume();}
			}
		});
	}

	private JPanel createInputPanel() {
		JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
		inputPanel.setBorder(BorderFactory.createTitledBorder("Enter the Patient data"));

		idField = new JTextField();
		firstNameField = new JTextField();
		lastNameField = new JTextField();
		String[] genderOptions = { "Male", "Female", "Other" };
		genderComboBox = new JComboBox<>(genderOptions);
		insuranceCheckBox = new JCheckBox();
		addressField = new JTextArea();

// submit button code....

		inputPanel.add(new JLabel("ID:"));
		inputPanel.add(idField);
		inputPanel.add(new JLabel("First Name:"));
		inputPanel.add(firstNameField);
		inputPanel.add(new JLabel("Last Name:"));
		inputPanel.add(lastNameField);
		inputPanel.add(new JLabel("Gender:"));
		inputPanel.add(genderComboBox);
		inputPanel.add(new JLabel("Address:"));
		inputPanel.add(addressField);
		inputPanel.add(new JLabel("Has Insurance:"));
		inputPanel.add(insuranceCheckBox);
		
//		firstNameField.addKeyListener(new KeyAdapter() {
//			@Override
//			public void keyTyped(KeyEvent e) {
//				if(!Character.isLetter(e.getKeyChar())) {e.consume();}
//			}
//		});

		return inputPanel;
	}

	// ----------- SUBMIT BUTTON PANEL -------------------------------------------
	private JPanel submitButtonPanel() {

		JPanel submitButtonPanel = new JPanel();
		JButton submitButton = new JButton("Submit");

		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addPatient();
				}
		});

		submitButtonPanel.add(submitButton);
		return submitButtonPanel;
	}

	
	private void clearInputFields(JTextField idField, JTextField firstNameField, JTextField lastNameField,
			JComboBox<String> genderComboBox, JCheckBox insuranceCheckBox, JTextArea addressField) {
		idField.setText("");
		firstNameField.setText("");
		lastNameField.setText("");
		genderComboBox.setSelectedIndex(0);
		insuranceCheckBox.setSelected(false);
		addressField.setText("");
	}

	private JTable createTable() {

		String QUERY = "SELECT id, firstName, lastName, gender, address, hasInsurance FROM patient";
		try {
			Connection connection = DriverManager.getConnection(dbm.URL, dbm.USER, dbm.PASSWORD);
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);
			while (rs.next()) {
				String id = rs.getString("id");
				String firstName = rs.getString("firstName");
				String lastName = rs.getString("lastName");
				String gender = rs.getString("gender");
				String hasInsurance = rs.getString("hasInsurance");
				String address = rs.getString("address");
//
//				String hasInsuranceString;
//				if (hasInsurance == true) {
//					hasInsuranceString = "Yes";
//				} else {
//					hasInsuranceString = "No";
//				}

				Patient patient = new Patient(id, firstName, lastName, gender, address, hasInsurance);
				patientModel.addPatient(patient);
			}

		} catch (SQLException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error while loading data", "Warning", JOptionPane.ERROR_MESSAGE);
		}

		JTable table = new JTable(patientModel);

		// set cell editor to allow editing in the table
		table.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()));
		
		TableColumn genderColumn = table.getColumnModel().getColumn(3);
		
		String[] genderOptions = {"Male","Female","Other"};
		JComboBox<String> genderEditor = new JComboBox<String>(genderOptions);
		genderColumn.setCellEditor(new DefaultCellEditor(genderEditor));
		
		TableColumn hasInsuranceColumn = table.getColumnModel().getColumn(5);
		
		String[] insuranceOptions = {"Yes", "No"};
		JComboBox<String> insuranceEditor = new JComboBox<String>(insuranceOptions);
		hasInsuranceColumn.setCellEditor(new DefaultCellEditor(insuranceEditor));
		
		// disable editing of the id column
		TableColumn idColumn = table.getColumnModel().getColumn(0);
		idColumn.setCellEditor(new DefaultCellEditor(new JTextField()) {
			public boolean isCellEditable(EventObject anEvent) {
				return false;
			}
		});

		return table;
	}
	
	// ----------- ADD PATIENT TO LOCAL TABLE ---------------b
	
	public void addPatient() {
		if (areAllFieldsFilled()) {
			String id = idField.getText();
			String firstName = firstNameField.getText();
			String lastName = lastNameField.getText();
			String gender = (String) genderComboBox.getSelectedItem();
			boolean hasInsurance = insuranceCheckBox.isSelected();
			String address = addressField.getText();

			String hasInsuranceString;
			if (hasInsurance == true) {
				hasInsuranceString = "Yes";
			} else {
				hasInsuranceString = "No";
			}

			Patient patient = new Patient(id, firstName, lastName, gender, address, hasInsuranceString);
			patientModel.addPatient(patient);
			addPatientToDatabase(patient);

			clearInputFields(idField, firstNameField, lastNameField, genderComboBox, insuranceCheckBox,
					addressField);
		} else {
			// Display warning in UI
			JOptionPane.showMessageDialog(PatientWindow.this, "Please enter data in all fields.", "Warning",
					JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private boolean areAllFieldsFilled() {
		return !idField.getText().isEmpty() && !firstNameField.getText().isEmpty()
				&& !lastNameField.getText().isEmpty() && genderComboBox.getSelectedItem() != null
				&& !addressField.getText().isEmpty();
	}
	

	// ----------- ADD PATIENT TO THE DATABASE ---------------------------------
	public void addPatientToDatabase(Patient patient) {
		
		String id = patient.getId();
		String firstName = patient.getFirstName();
		String lastName = patient.getLastName();
		String gender = patient.getGender();
		String address = patient.getAddress();
		String hasInsurance = patient.getHasInsurance();

		try (Connection connection = DriverManager.getConnection(dbm.URL, dbm.USER, dbm.PASSWORD)) {
			String sql = "INSERT INTO patient(id, firstName, lastName, gender, address, hasInsurance) VALUES (?,?,?,?,?,?)";
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setString(1, id);
				preparedStatement.setString(2, firstName);
				preparedStatement.setString(3, lastName);
				preparedStatement.setString(4, gender);
				preparedStatement.setString(5, address);
				preparedStatement.setString(6, hasInsurance);
				preparedStatement.executeUpdate();

			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error while saving data", "Warning", JOptionPane.ERROR_MESSAGE);
		}

	}

	// ------------ UPDATE DATABASE ON DATA CHANGE --------------------
	private void updatePatientInDatabase(int row, int column) {
        String id = (String) patientModel.getValueAt(row, 0); // Assuming id is in the first column
        String columnName = patientModel.getColumnName(column);
        Object updatedValue = patientModel.getValueAt(row, column);

        try (Connection connection = DriverManager.getConnection(dbm.URL, dbm.USER, dbm.PASSWORD)) {
            String sql = "UPDATE patient SET " + columnName + " = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setObject(1, updatedValue);
                preparedStatement.setString(2, id);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while updating data", "Warning", JOptionPane.ERROR_MESSAGE);
        }
    }
	
	
	
	// -------------- DELETE OPERATION ----------------------------
	
    private void deleteSelectedPatients() {
        int[] selectedRows = tableModel.getSelectedRows();

        if (selectedRows.length > 0) {
            int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected records?",
                    "Confirmation", JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                for (int i = selectedRows.length - 1; i >= 0; i--) {	
                    deletePatientFromDatabase(selectedRows[i]);
                  
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select at least one record to delete.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void deletePatientFromDatabase(int rowIndex) {
    	
    	String patientId = (String) patientModel.getValueAt(rowIndex, 0);
    	
        try (Connection connection = DriverManager.getConnection(dbm.URL, dbm.USER, dbm.PASSWORD)) {
            String sql = "DELETE FROM patient WHERE id=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, patientId);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
        	System.out.println("Error in the method !");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while deleting data", "Warning", JOptionPane.ERROR_MESSAGE);
        }
        
        patientModel.removePatient(rowIndex);
    }
    
}