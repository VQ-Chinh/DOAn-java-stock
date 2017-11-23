import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JSpinner.DateEditor;

import com.congdongjava.CsvReader;

public class CSV {
	private PrintWriter pw;
	private CsvReader Docfile;
	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

	public void revertFile(String fileName) {

		try {

			StringBuilder sb = new StringBuilder();
			Docfile = new CsvReader("cophieu68.vn/" + fileName);

			// Bat dau doc file CSV
			Docfile.readHeaders();

			int i = 0, count = 0, range = Docfile.getHeaderCount();

			// Duyet qua tung ROW - Dong du lieu
			String temp = "";
			while (Docfile.readRecord()) {
				// Lay bang Ten Cot
				temp = "";
				for (i = 0; i < range - 1; i++) {
					temp += Docfile.get(i) + ",";
				}
				temp += Docfile.get(range - 1) + '\n';
				sb.insert(0, temp);
				System.out.println(count++);
			}
			if (count > 750) {
				temp = "";
				for (i = 0; i < range - 1; i++) {
					temp += Docfile.getHeader(i) + ",";
				}
				pw = new PrintWriter(new File("cophieu68.vn_Choice/" + fileName));
				temp += Docfile.getHeader(range - 1) + '\n';
				sb.insert(0, temp);
				pw.write(sb.toString());
				pw.close();
				System.out.println("done!");
			} else {
				Docfile.close();
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

	public void locCoPhieu(String ngay) {
		File dir = new File("cophieu68.vn_Revert/");
		String[] children = dir.list();
		List<CoPhieu> dsCp = new ArrayList<CoPhieu>();
		int num = 0;

		for (int j = 0; j < children.length; j++) {
			String fileName = children[j];
			try {
				Date dateRequi = format.parse(ngay);
				Docfile = new CsvReader("cophieu68.vn_Revert/" + fileName);
				Docfile.readHeaders();
				Docfile.readRecord();
				CoPhieu cp = new CoPhieu();
				cp.NgayBatDau = Docfile.get(1);
				Date pDate, date = new Date();
				pDate = format.parse(Docfile.get(1));
				if (TimeUnit.DAYS.convert(dateRequi.getTime()-pDate.getTime() , TimeUnit.MILLISECONDS) > 200) {
					int ngayChenhLech = 0, count = 0, range = Docfile.getHeaderCount();
					double tien = 0;
					// Duyet qua tung ROW - Dong du lieu
					String temp = "";

					while (Docfile.readRecord()) {
						// Lay bang Ten Cot
						temp = Docfile.get(1);
						date = format.parse(temp);
						ngayChenhLech += TimeUnit.DAYS.convert(date.getTime() - pDate.getTime(), TimeUnit.MILLISECONDS);
						pDate = format.parse(temp);
						count++;
					}
					float chiso = ngayChenhLech * 1f / count;
					if (chiso < 1.49 & TimeUnit.DAYS.convert(date.getTime() - dateRequi.getTime(), TimeUnit.MILLISECONDS) >1000) {
						System.out.println(j + " Ten: " + fileName + ", ngaybatdau: " + cp.NgayBatDau
								+ ", DO on dinh: " + (ngayChenhLech + 0.0) / count);
						num++;
						//revertFile(fileName);
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println(num);
	}

	public void ghiFileLichSU(CoPhieu cp,String path) {
		FileWriter pw;
		try {

			File file = new File(path + cp.FileName);
			String content = "";
			content += cp.NgayBatDau + "," + cp.SoLuong + "," + cp.GiaHienTai + "\n";
			pw = new FileWriter(file.getPath(), true);
			BufferedWriter bw = new BufferedWriter(pw);
			bw.write(content);
			bw.close();
			// System.out.println(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void ghiFileLichSuGiaoDich(CoPhieu cp) {
		FileWriter pw;
		BufferedWriter bw;
		try {

			File file = new File("LichSuGiaoDich/" + cp.FileName);
			String content = "";
			 if (!file.exists()) {
		           file.createNewFile();
		           content = "<ngay>,<sl>,<gia>\n";
		        }
			 content += cp.NgayBatDau + "," + cp.SoLuong + "," + cp.GiaHienTai + "\n";
			pw = new FileWriter(file.getPath(),true);
			 bw = new BufferedWriter(pw);
			bw.write(content);
			bw.close();
			// System.out.println(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
	}

	public void taoMoiFileLichSu(CoPhieu cp, String path) {
		FileWriter pw;
		try {

			File file = new File(path + cp.FileName);
			String content = "<ngay>,<sl>,<gia>\n";
			;

			pw = new FileWriter(file.getPath());
			BufferedWriter bw = new BufferedWriter(pw);
			bw.write(content);
			bw.close();
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

	public void DocfileLichSu(CoPhieu cp,String path, String date) {
		try {
			Docfile = new CsvReader(path + cp.FileName);
			Docfile.readHeaders();

			int slTruoc = 0, count = 0, range = Docfile.getHeaderCount();
			double tien = 0,tienDauTu = 0;
			// Duyet qua tung ROW - Dong du lieu
			String temp = "";
			// while (Docfile.readRecord()) {
			while (Docfile.readRecord() ) {
				 if(Docfile.get(0).equals(date))
					 break;
			}
			
			for (int i = 0; i < 19; i++) {
				
				// Lay bang Ten Cot
				temp = "";
				// for (i = 0; i < range - 1; i++) {
				// temp += Docfile.get(i) + ",";
				// }
				if(Integer.parseInt(Docfile.get(1)) - slTruoc >0)
					tienDauTu += Float.parseFloat(Docfile.get(2)) * (Integer.parseInt(Docfile.get(1)) - slTruoc);
				tien += Float.parseFloat(Docfile.get(2)) * (slTruoc - Integer.parseInt(Docfile.get(1)));
				slTruoc = Integer.parseInt(Docfile.get(1));

				// System.out.println(
				// count++ + " " + Docfile.get(0) + " " + Docfile.get(1) + " " +
				// Docfile.get(2) + " = " + tien);
				Docfile.readRecord();
			}

			tien += Float.parseFloat(Docfile.get(2)) * slTruoc;
			// System.out.println(
			// count++ + " " + Docfile.get(0) + " " + Docfile.get(2) + " = " +
			// tien);
			cp.LoiNhuan = tien;
			cp.TienDauTu = tienDauTu;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void clearFileLichSu(){
		File index = new File("LichSuGiaoDich");
		String[]entries = index.list();
		for(String s: entries){
		    File currentFile = new File(index.getPath(),s);
		    currentFile.delete();
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		// CSV test = new CSV();
		 CoPhieu cp = new CoPhieu();
		 cp.FileName = "excel_bvh.csv";
		// System.out.println(test.DocfileLichSu(cp));

		String sourceDate = "20100104";
		// SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		// Calendar cal = Calendar.getInstance();
		// Date oldDate, myDate;
		// try {
		// myDate = format.parse(sourceDate);
		// cal.setTime(myDate);
		// } catch (ParseException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// oldDate = cal.getTime();
		//
		// System.out.println(oldDate);
		// cal.add(Calendar.DATE, 5); // minus number would decrement the days
		// System.out.println(cal.getTime());
		// System.out.println(
		// "Days: " + TimeUnit.DAYS.convert(oldDate.getTime() -
		// cal.getTime().getTime(), TimeUnit.MILLISECONDS));

		CSV test = new CSV();
		//test.locCoPhieu("20100104");
		// new File("abc").mkdir();
		test.clearFileLichSu();
	}
}
