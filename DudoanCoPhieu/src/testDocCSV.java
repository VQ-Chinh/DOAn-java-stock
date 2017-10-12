import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.congdongjava.CsvReader;

public class testDocCSV {
	public static void docfile() {
		try {
			CsvReader docfile = new CsvReader("cophieu68.vn/excel_^aex.csv");
			// Bat dau doc file CSV
			docfile.readHeaders();
			// Duyet qua tung ROW - Dong du lieu
			while (docfile.readRecord()) {
				// Lay bang Ten Cot
				String temp = "";
				temp += docfile.get("Ma co phieu") + " | ";
				// Lay bang So tu tu cua cot bat dau tu 0
				temp += docfile.get(0) + " | ";
				temp += docfile.get(1) + " | ";
				temp += docfile.get(2) + " | ";
				temp += docfile.get(3) + " | ";
				System.out.println(temp);
			}
		} catch (Exception ex) {
			Logger.getLogger(testDocCSV.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void getheadernames(String link) {
		try {
			// Lay ten cac truong du lieu
			CsvReader docfile = new CsvReader(link);
			// Bat dau doc file CSV
			docfile.readHeaders();
			for (int i = 0; i < docfile.getHeaderCount(); i++) {
				System.out.println(docfile.getHeader(i));
			}
		} catch (Exception ex) {
			Logger.getLogger(testDocCSV.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void docCacFile(){
		 File dir = new File("cophieu68.vn/");
	      String[] children = dir.list();
	      if (children == null) {
	         System.out.println( "Either dir does not exist or is not a directory");
	      }
		  else {
	         for (int i=0; i< children.length; i++) {
	            String filename = children[i];
	            System.out.println(filename);
	         }
	      }
	 }

	public static void main(String[] args) {
		testDocCSV a= new testDocCSV();
		a.getheadernames("cophieu68.vn/excel_^aex.csv");
		a.docCacFile();
		
	}
}
