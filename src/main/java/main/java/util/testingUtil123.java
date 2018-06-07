package main.java.util;

import java.io.IOException;
import java.util.List;

public class testingUtil123 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExcelLibrary sss=new ExcelLibrary();
		ExcelLibrary.getWorkbook("D:\\telmar_automation_projects\\Selenium-Automation-Framework\\TestSuite2.xlsx");
		ExcelAction ma=new ExcelAction();
		try {
			ma.readCapturedObjectProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
