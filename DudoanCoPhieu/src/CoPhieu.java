import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.congdongjava.CsvReader;

public class CoPhieu extends ThuocTinh {

	public String Ma;
	public double LoiNhuan;
	public double KhoanCach;
	public int Cum;
	public int SoLuong;
	public String NgayBatDau;
	public String NgayDanhGia;
	public float ChiSoOnDinh;
	public String FileName;
	public float GiaNgay20;
	public float GiaTruoc;
	public float GiaHienTai;
	public float Period = 200;
	public float TienDauTu = 0;
	private double K200 = 2.0 / (200 + 1);
	private double K125 = 2.0 / (125 + 1);
	private double K50 = 2.0 / (50 + 1);
	private double K20 = 2.0 / (20 + 1);
	public static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

	public CoPhieu() {

	}

	public CoPhieu(CoPhieu cp) {
		this.Trend200 = cp.Trend200;
		this.Trend50 = cp.Trend50;
		this.Momentum125 = cp.Momentum125;
		this.Momentum20 = cp.Momentum20;
	}

	public float tinhTrungBinh(String ngayBatDau) {
		try {
			CsvReader docfile = new CsvReader("cophieu68.vn/" + FileName);
			NgayBatDau = ngayBatDau;
			docFileTaiNgay(docfile, ngayBatDau, 0);
			// Tinh trung binh ngay truoc
			float EMA200 = 0, EMA50 = 0, EMA125 = 0, EMA20 = 0, temp = 0, price200 = 0, price125 = 0, price50 = 0,
					price20 = 0;
			int timeRemain = 0;
			for (int i = 1; i < 200 + 1; i++) {

				temp = Float.parseFloat(docfile.get(5));

				if (i == 125)
					price125 = temp;
				else if (i == 50)
					price50 = temp;
				else if (i == 20)
					price20 = temp;
				EMA200 += temp;
				if (i <= 125)
					EMA125 = EMA125 + temp;
				if (i <= 50)
					EMA50 = EMA50 + temp;
				if (i <= 20)
					EMA20 = EMA20 + temp;
				// System.out.println(i + " | " + docfile.get(1) + " | " + temp
				// + " | " + EMA200 + " | " + EMA125 + " | "
				// + EMA50 + " | " + EMA20 + " | " + timeRemain);
				if (i == 1)
					GiaHienTai = temp;
				if (i == 2)
					GiaTruoc = temp;
				docfile.readRecord();

			}
			Trend200 = EMA200 / 200;
			Momentum125 = (temp - price125) / price125 * 100;
			Trend50 = EMA50 / 50;
			Momentum20 = (temp - price20) / price20 * 100;
			// System.out.println(FileName + docfile.get(1) + " | " +
			// docfile.get(5) + " | " + EMA200);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public boolean tinhEMA() {
		float temp;
		CsvReader docfile;
		boolean key;
		try {
			docfile = new CsvReader("cophieu68.vn_Choice/" + FileName);
			docFileTaiNgay(docfile, NgayBatDau, 1);
			String test = docfile.get(1);
			key = test.equals(NgayBatDau);
			if (key) {
				GiaTruoc = GiaHienTai;
				temp = Float.parseFloat(docfile.get(5));
				GiaHienTai = temp;
				Trend200 = Trend200 + K200 * (temp - Trend200);
				Momentum125 = Momentum125 + K125 * (temp - Momentum125);
				Trend50 = Trend50 + K50 * (temp - Trend50);
				Momentum20 = Momentum20 + K20 * (temp - Momentum20);
				// System.out.println(docfile.get(1) + " | " + temp + " | " +
				// Trend200 + " | " + Momentum125 + " | "
				// + Trend50 + " | " + Momentum20);
			}
			// else
			// System.out.println(NgayBatDau + " ko giao dich ");

			return key;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public void docFileTaiViTri(CsvReader docfile, int vitri) {
		for (int i = 0; i < vitri; i++) {
			try {
				docfile.readRecord();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void docFileTaiNgay(CsvReader docfile, String ngayBatDau, int key) {
		try {
			docfile.readRecord();
			docfile.readRecord();
			String stringDate = docfile.get(1);
			String preDate;
			while (!stringDate.equals(ngayBatDau) && docfile.readRecord()) {

				preDate = stringDate;
				stringDate = docfile.get(1);
				if (Integer.parseInt(stringDate) - Integer.parseInt(ngayBatDau) < 0 && key == 0)
					break;
				if (Integer.parseInt(stringDate) - Integer.parseInt(ngayBatDau) > 0 && key == 1)
					break;
				// System.out.println(docfile.get(1));

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void nextDays(){
		Date time;
		try {
			time = format.parse(NgayBatDau);
			Calendar cal = Calendar.getInstance();
			cal.setTime(time);
			cal.add(Calendar.DATE, 1); // minus number would decrement the
											// days
			NgayBatDau =  format.format(cal.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String addDaysS(String date, int days) {
		Date time;
		try {
			time = format.parse(date);
			Calendar cal = Calendar.getInstance();
			cal.setTime(time);
			cal.add(Calendar.DATE, days); // minus number would decrement the
											// days
			return format.format(cal.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public static String removeDaysS(String date, int days) {
		Date time;
		try {
			time = format.parse(date);
			Calendar cal = Calendar.getInstance();
			cal.setTime(time);
			cal.add(Calendar.DATE, -days); // minus number would decrement the
											// days
			return format.format(cal.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public void clearData() {
		SoLuong = 0;
	}

	@Override
	public String toString() {

		return FileName + " || " + Trend200 + " || " + Trend50 + " || " + Momentum125 + " || " + Momentum20;
	}
	
	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (float) tmp / factor;
	}

	public static void main(String[] args) {
		CoPhieu a = new CoPhieu();
		a.FileName = "excel_^bsesn.csv";
		a.tinhTrungBinh("20101022");
		for (int i = 0; i < 20; i++) {
			boolean key = a.tinhEMA();
		}
		System.out.println(a.toString());
	}

}
