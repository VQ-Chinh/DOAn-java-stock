import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

import com.congdongjava.CsvReader;

public class CoPhieu extends ThuocTinh {

	public String Ma;
	public double LoiNhuan;
	public double KhoanCach;
	public int Cum;
	public int SoLuong;
	public int NgayBatDau;
	public String FileName;
	public double GiaTruoc;
	public double GiaHienTai;
	public float Period = 200;
	private double K200 = 2.0 / (200 + 1);
	private double K125 = 2.0 / (125 + 1);
	private double K50 = 2.0 / (50 + 1);
	private double K20 = 2.0 / (20 + 1);

	public CoPhieu() {

	}

	public CoPhieu(CoPhieu cp) {
		this.Trend200 = cp.Trend200;
		this.Trend50 = cp.Trend50;
		this.Momentum125 = cp.Momentum125;
		this.Momentum20 = cp.Momentum20;
	}

	public float tinhTrungBinh(int ngayBatDau) {
		NgayBatDau = ngayBatDau;
		try {
			CsvReader docfile = new CsvReader("cophieu68.vn_Revert/" + FileName);
			docFileTaiViTri(docfile, ngayBatDau - 200);
			// Tinh trung binh ngay truoc
			double EMA200 = 0, EMA50 = 0, EMA125 = 0, EMA20 = 0, temp = 0, price200 = 0, price125 = 0, price50 = 0,
					price20 = 0;

			for (int i = 1; i < 200 + 1; i++) {
				temp = Float.parseFloat(docfile.get(5));
				if (i == 1)
					price200 = temp;
				else if (i == 75)
					price125 = temp;
				else if (i == 150)
					price50 = temp;
				else if (i == 180)
					price20 = temp;
				EMA200 += temp;
				if (200 + 1 - i <= 125)
					EMA125 = EMA125 + temp;
				if (200 + 1 - i <= 50)
					EMA50 = EMA50 + temp;
				if (200 + 1 - i <= 20)
					EMA20 = EMA20 + temp;
				// System.out.println(i + " | " + docfile.get(1) + " | " + temp
				// + " | " + EMA200 + " | " + EMA125 + " | " + EMA50 + " | " +
				// EMA20);
				if ( i==198)
					GiaTruoc = temp;
				docfile.readRecord();
			}
			Trend200 = EMA200 / 200;
			Momentum125 = (temp - price125) / price125 * 100;
			Trend50 = EMA50 / 50;
			Momentum20 = (temp - price20) / price20 * 100;
			// System.out.println(FileName + docfile.get(1) + " | " +
			// docfile.get(5) + " | " + EMA200 );

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public float tinhEMA() {
		double temp;
		CsvReader docfile;
		try {
			docfile = new CsvReader("cophieu68.vn_Revert/" + FileName);
			docFileTaiViTri(docfile, NgayBatDau);
			temp = Float.parseFloat(docfile.get(5));
			GiaTruoc = GiaHienTai;
			GiaHienTai = temp;
			Trend200 = Trend200 + K200 * (temp - Trend200);
			Momentum125 = Momentum125 + K125 * (temp - Momentum125);
			Trend50 = Trend50 + K50 * (temp - Trend50);
			Momentum20 = Momentum20 + K20 * (temp - Momentum20);
			// System.out.println(i + " | " + docfile.get(1) + " | " + temp + "
			// | " + EMA200 + " | " + EMA125 + " | " + EMA50 + " | " + EMA20 );
			docfile.readRecord();
			NgayBatDau++;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
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

	@Override
	public String toString() {

		return FileName + " || " + Trend200 + " || " + Trend50 + " || " + Momentum125 + " || " + Momentum20;
	}

	public static void main(String[] args) {
		CoPhieu a = new CoPhieu();
		a.FileName = "excel_aaa.csv";
		a.tinhTrungBinh(500);
		System.out.println(a.toString());
	}

}
