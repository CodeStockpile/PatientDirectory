package com.patient;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;

public class CellValueEditor extends DefaultCellEditor {

	public CellValueEditor() {
		super(new JTextField());
		
	}

}
