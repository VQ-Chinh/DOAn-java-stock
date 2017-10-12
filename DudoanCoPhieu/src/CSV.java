import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.congdongjava.CsvReader;

public class CSV {
	public void revertFile(String fileName) {
		PrintWriter pw;

		try {

			StringBuilder sb = new StringBuilder();
			CsvReader docfile = new CsvReader("cophieu68.vn/" + fileName);
			// Bat dau doc file CSV
			docfile.readHeaders();

			int i = 0, count = 0, range = docfile.getHeaderCount();

			// Duyet qua tung ROW - Dong du lieu
			String temp = "";
			while (docfile.readRecord()) {
				// Lay bang Ten Cot
				temp = "";
				for (i = 0; i < range - 1; i++) {
					temp += docfile.get(i) + ",";
				}
				temp += docfile.get(range - 1) + '\n';
				sb.insert(0, temp);
				System.out.println(count++);
			}
			if (count > 750) {
				temp = "";
				for (i = 0; i < range - 1; i++) {
					temp += docfile.getHeader(i) + ",";
				}
				pw = new PrintWriter(new File("cophieu68.vn_Revert/" + fileName));
				temp += docfile.getHeader(range - 1) + '\n';
				sb.insert(0, temp);
				pw.write(sb.toString());
				pw.close();
				System.out.println("done!");
			} else {
				docfile.close();
			}
		} catch (Exception ex) {
			Logger.getLogger(testDocCSV.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void revertAll() {
		File dir = new File("cophieu68.vn/");
		String[] children = dir.list();
		if (children == null) {
			System.out.println("Either dir does not exist or is not a directory");
		} else {
			for (int i = 0; i < children.length; i++) {
				String fileName = children[i];
				revertFile(fileName);
			}
		}
	}

	public void ghiFileLichSU(CoPhieu cp) {
		FileWriter fw;
		try {

			File file = new File("LichSu/" + cp.FileName);
			String content = "";
			content += cp.NgayBatDau + "," + cp.SoLuong + "," + cp.GiaHienTai + "\n";
			fw = new FileWriter(file.getPath(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		//	System.out.println(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void taoMoiFileLichSu(CoPhieu cp){
		FileWriter fw;
		try {

			File file = new File("LichSu/" + cp.FileName);
			String content = "<ngay>,<sl>,<gia>\n";;
			
			fw = new FileWriter(file.getPath());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double docFileLichSu(CoPhieu cp) {
		try {
			CsvReader docfile = new CsvReader("LichSu/" + cp.FileName);
			docfile.readHeaders();

			int slTruoc = 0, count = 0, range = docfile.getHeaderCount();
			double tien = 0;
			// Duyet qua tung ROW - Dong du lieu
			String temp = "";
			// while (docfile.readRecord()) {
			for (int i = 0; i < 19; i++) {
				docfile.readRecord();
				// Lay bang Ten Cot
				temp = "";
				// for (i = 0; i < range - 1; i++) {
				// temp += docfile.get(i) + ",";
				// }

				tien += Float.parseFloat(docfile.get(2)) * (slTruoc - Integer.parseInt(docfile.get(1)));
				slTruoc = Integer.parseInt(docfile.get(1));

//				System.out.println(
//						count++ + " " + docfile.get(0) + " " + docfile.get(1) + " " + docfile.get(2) + " = " + tien);
			}
			
			docfile.readRecord();
			tien += Float.parseFloat(docfile.get(2)) * slTruoc;
//			System.out.println(
//					count++ + " " + docfile.get(0) +  " " + docfile.get(2) + " = " + tien);
			return tien;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	

	public static void main(String[] args) throws FileNotFoundException {
		CSV test = new CSV();
		CoPhieu cp = new CoPhieu();
		cp.FileName = "excel_bvh.csv";
		System.out.println(test.docFileLichSu(cp));

	}
}
