package com.patient;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

public class PatientTableModel extends AbstractTableModel{
	
	private List<Patient> patientList;
	private String[] columnNames = {"Id","FirstName", "LastName", "Gender", "Address", "hasInsurance"};

	public PatientTableModel() {
		this.patientList = new ArrayList<>();
	}
	
	public void addPatient(Patient patient) {
		patientList.add(patient);
		fireTableDataChanged();
	}
	
	public void setPatient(List<Patient> patient) {
		patientList = new ArrayList<>(patient);
		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {

		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		
		return patientList.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Patient patient = patientList.get(row);
		
		switch(col) {
		case 0:
			return patient.getId();
		case 1:
			return patient.getFirstName();
		case 2:
			return patient.getLastName();
		case 3:
			return patient.getGender();
		case 4:
			return patient.getAddress();
		case 5:
			return patient.getHasInsurance();
		default:
			return null;
		}
	}
	
	public List<Patient> getPatientList() {
		return patientList;
	}

	public void setPatientList(List<Patient> patientList) {
		this.patientList = patientList;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public void setValueAt(Object updatedValue, int selectedRow, int selectedColumn) {
		
		System.out.println("Inside model class");
		Patient patient = patientList.get(selectedRow);
		
		
		switch(selectedColumn) {
		case 0:
			patient.setId(updatedValue.toString());
			break;
		case 1:
			patient.setFirstName(updatedValue.toString());
			break;
		case 2:
			patient.setLastName(updatedValue.toString());
			break;
		case 3:
			patient.setGender(updatedValue.toString());
			break;
		case 4:
			patient.setAddress(updatedValue.toString());
			break;
		case 5:
			patient.setHasInsurance(updatedValue.toString());
			break;
		
		}
		
		fireTableCellUpdated(selectedRow, selectedColumn);
		
		System.out.println(patient);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}
	
	public void removePatient(int row) {
		if(row >= 0 && row < patientList.size()) {
		patientList.remove(row);
		fireTableRowsDeleted(row, row);
		}
	}

	public Patient getPatient(int rowIndex) {
		
		if(rowIndex >0 && rowIndex<patientList.size()) {
			return patientList.get(rowIndex);
		}
		return null;
	}
	
	
	public void clearPatitents() {
		patientList.clear();
		fireTableDataChanged();
	}
}
