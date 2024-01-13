package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class DataTable {

	private String c1;
	private Integer c3;
	private String c2;

	public DataTable(String c1, int c3, String c2) {
		this.c1 = new String(c1);
		this.c2 = new String(c2);
		this.c3 = new Integer(c3);

	}

	public String getC1() {
		return c1;
	}

	public void setC1(String c1) {
		this.c1 = c1;
	}

	public Integer getC3() {
		return c3;
	}

	public void setC3(Integer c3) {
		this.c3 = c3;
	}

	public String getC2() {
		return c2;
	}

	public void setC2(String c2) {
		this.c2 = c2;
	}

}