import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TinhCoPhieu {
	public List<CoPhieu> Ds100CoPhieu = new ArrayList<CoPhieu>();
	public List<CoPhieu> Top10CoPhieu;
	public float Phi;
	public int SoLuongCoPhieuDauTu = 5;
	public int ThoiGianGianCach = 30;
	public float TienDauTu;
	public int ChuKi;
	public List<Cum> listCentroid;
	public String ThoiGianBatDau;
	public Float TiLeLoiNhuan = 0.0f;
	public int CumTotNhat;
	public String ThoiGianKetThuc;

	public TinhCoPhieu() {
		Ds100CoPhieu = new ArrayList<CoPhieu>();
		Top10CoPhieu = new ArrayList<CoPhieu>();
	}

	public void layDanhSachCoPhieu() {
		File dir = new File("cophieu68.vn_Choice/");
		String[] children = dir.list();
		if (children == null) {
			System.out.println("Either dir does not exist or is not a directory");
		} else {
			CoPhieu cp;

			for (int i = 0; i < 100; i++) {
				String filename = children[i];
				cp = new CoPhieu();
				cp.FileName = filename;

				Ds100CoPhieu.add(cp);
				// System.out.println(i + " || " + filename);
			}
		}
	}

	public void phanCum() {
		double max = 0;
		for (CoPhieu coPhieu : Ds100CoPhieu) {
			coPhieu.tinhTrungBinh(ThoiGianBatDau);
		}
		// sap xep theo trend 200
		Collections.sort(Ds100CoPhieu, new Comparator<CoPhieu>() {
			@Override
			public int compare(CoPhieu o1, CoPhieu o2) {
				// TODO Auto-generated method stub
				if (o1.Trend200 < o2.Trend200)
					return 1;
				else if (o1.Trend200 > o2.Trend200)
					return -1;
				else
					return 0;
			}
		});

		// chon 10 tam theo 200
		listCentroid = new ArrayList<Cum>();
		for (int i = 0; i < Ds100CoPhieu.size(); i = i + 10) {
			listCentroid.add(new Cum(Ds100CoPhieu.get(i)));
		}

		// kmean cluster
		int change = 1, count = 1;

		while (change != 0) {
			for (Cum centroid : listCentroid) {
				centroid.SoLuongPhanTu = 0;
			}
			change = 0;
			for (CoPhieu coPhieu : Ds100CoPhieu) {
				double min = Double.MAX_VALUE, temp = 0;
				int cum = -1;
				for (Cum centroid : listCentroid) {

					temp = Math.sqrt(Math.pow(coPhieu.Trend200 - centroid.Trend200, 2)
							+ Math.pow(coPhieu.Trend50 - centroid.Trend50, 2)
							+ Math.pow(coPhieu.Momentum125 - centroid.Momentum125, 2)
							+ Math.pow(coPhieu.Momentum20 - centroid.Momentum20, 2));
					if (temp < min && temp != 0) {
						cum = listCentroid.indexOf(centroid);
						min = temp;
					}
				}
				if (cum != coPhieu.Cum)
					change++;
				coPhieu.Cum = cum;
				listCentroid.get(cum).TongTrend200 += coPhieu.Trend200;
				listCentroid.get(cum).TongTrend50 += coPhieu.Trend50;
				listCentroid.get(cum).TongMomentum125 += coPhieu.Momentum125;
				listCentroid.get(cum).TongMomentum20 += coPhieu.Momentum20;
				listCentroid.get(cum).SoLuongPhanTu++;
			}

			// System.out.print("Lan: " + count + " " + change + " ");
			for (Cum centroid : listCentroid) {
				// System.out.print("cum" + listCentroid.indexOf(centroid) + ":
				// " + centroid.SoLuongPhanTu + "||");
				centroid.xacDinhTam();
			}
			// System.out.println("");

			count++;
		}
		// for (Cum cum : listCentroid) {
		//
		// System.out.println("Cum " + listCentroid.indexOf(cum) + ": " +
		// cum.Trend200 + " || " + cum.Trend50 + " || "
		// + cum.Momentum125 + " || " + cum.Momentum20);
		// }

		// int temp=0;
		// CoPhieu cp = new CoPhieu() ;
		// for (int i = 0; i < Ds100CoPhieu.size(); i ++) {
		// max =0;
		// for(int j = i; j < Ds100CoPhieu.size(); j ++){
		// if(Ds100CoPhieu.get(j).Trend200 > max){
		// max = Ds100CoPhieu.get(j).Trend200;
		// cp = Ds100CoPhieu.get(j);
		// }
		// }
		// Ds100CoPhieuDaSapXep.add(cp);
		//
		// }
	}

	public void tinh20ngaytruoc() {
		for (CoPhieu cp : Ds100CoPhieu) {

			cp.GiaNgay20 = cp.GiaTruoc;
			cp.clearData();
			cp.NgayBatDau = ThoiGianBatDau;
			// CSV ghifile = new CSV();
			// ghifile.taoMoiFileLichSu(cp, "LichSu/");

			for (int i = 0; i < ThoiGianGianCach; i++) {
				cp.tinhEMA();

				cp.nextDays();
			}
			cp.LoiNhuan = ((cp.GiaHienTai - cp.GiaNgay20) / cp.GiaNgay20) * 100;
		}
	}

	public void tinh20ngaysau() {
		CSV ghifile = new CSV();
		float tien = ((TienDauTu *(100-Phi))/100) / 10;
		for (int k = 0; k < SoLuongCoPhieuDauTu; k++) {
			CoPhieu cp = Ds100CoPhieu.get(k);

			cp.GiaNgay20 = cp.GiaHienTai;
			cp.SoLuong = 0;
			if (cp.GiaHienTai > cp.Trend200 && cp.GiaHienTai > cp.Trend50) {
				cp.SoLuong = (int) (tien / cp.GiaNgay20);
				ghifile.ghiFileLichSuGiaoDich(cp);
			}
			for (int i = 0; i < ThoiGianGianCach; i++) {
				cp.tinhEMA();
				cp.nextDays();
			}

			cp.LoiNhuan = ((cp.GiaHienTai - cp.GiaNgay20) / cp.GiaNgay20) * 100;
			TienDauTu = TienDauTu - ((cp.SoLuong * cp.GiaNgay20 * ( 100 + Phi))/100);
			cp.TienDauTu = ((cp.SoLuong * cp.GiaNgay20 * ( 100 + Phi))/100);

			System.out.println(cp.FileName + " " + " || " + cp.Trend200 + " || " + cp.Trend50 + " || " + cp.GiaHienTai
					+ " || " + cp.TienDauTu);

		}
		for (int i = SoLuongCoPhieuDauTu; i < Ds100CoPhieu.size(); i++) {
			CoPhieu cp = Ds100CoPhieu.get(i);
			for (int k = 0; k < ThoiGianGianCach; k++) {
				cp.tinhEMA();
				cp.nextDays();
			}
		}

	}

	public void chiaTien() {

	}

	public void tinhLoiNhuan20ngaytruoc() {
		for (CoPhieu coPhieu : Ds100CoPhieu) {
			// docfile.DocfileLichSu(coPhieu, "LichSu/", thoiGianBatDau);
			// System.out.println("Cum: " + coPhieu.Cum + " CP: " +
			// coPhieu.FileName + " || " + "20 ngay: "
			// + coPhieu.GiaNgay20 + " || " + "hien tai: " + coPhieu.GiaHienTai
			// + " || " + " Ty le loi nhuan: "
			// + coPhieu.LoiNhuan);
			listCentroid.get(coPhieu.Cum).LoiNhuan += coPhieu.LoiNhuan;
			listCentroid.get(coPhieu.Cum).DauTu += coPhieu.TienDauTu;
			// listCentroid.get(coPhieu.Cum).TyleLoiNhuan +=
			// round((coPhieu.LoiNhuan / coPhieu.TienDauTu) * 100, 2);
		}

		double bestProfit = 0;

		for (Cum cum : listCentroid) {
			cum.tinhTyLeLoiNhuan();
			cum.TyleLoiNhuan = cum.TyleLoiNhuan / cum.SoLuongPhanTu;
			if (bestProfit < cum.TyleLoiNhuan) {
				bestProfit = cum.TyleLoiNhuan;
				CumTotNhat = listCentroid.indexOf(cum);
			}
			// System.out.println("Cum: " + listCentroid.indexOf(cum) + " || " +
			// " Loi Nhuan: " + cum.LoiNhuan + " || "
			// + " Ty le loi nhuan: " + cum.TyleLoiNhuan);
		}
	}

	public float xacdinhban20ngaysau() {
		CSV csv = new CSV();
		
		for (int i = 0; i < Ds100CoPhieu.size(); i++) {
			CoPhieu coPhieu = Ds100CoPhieu.get(i);
			if (csv.kiemtraCPTon(coPhieu)) {
				if (coPhieu.GiaHienTai < coPhieu.Trend200 && coPhieu.GiaHienTai < coPhieu.Trend50) {
					// System.out.println(" CP: " + coPhieu.FileName + " || " +
					// "
					// Loi Nhuan: " + coPhieu.LoiNhuan + " || "
					// + " Ty le loi nhuan: " + (coPhieu.LoiNhuan /
					// coPhieu.TienDauTu) * 100);
					System.out.println(coPhieu.FileName + " Ban di:  " + (coPhieu.GiaHienTai * coPhieu.SoLuong * ( 100 - Phi))/100);
					TienDauTu += (coPhieu.GiaHienTai * coPhieu.SoLuong * ( 100 - Phi))/100;
					coPhieu.SoLuong = 0;
					csv.ghiFileLichSuGiaoDich(coPhieu);
				} else if (coPhieu.GiaHienTai > coPhieu.Trend200 && coPhieu.GiaHienTai < coPhieu.Trend50) {
					int soluong = coPhieu.SoLuong;
					coPhieu.SoLuong = 0;
					csv.ghiFileLichSuGiaoDich(coPhieu);
					coPhieu.SoLuong = soluong;
					System.out.println(coPhieu.FileName + " Ban di 1/2:  " + (coPhieu.GiaHienTai * (coPhieu.SoLuong / 2)* ( 100 - Phi))/100);
					TienDauTu += (coPhieu.GiaHienTai * (coPhieu.SoLuong / 2)* ( 100 - Phi))/100;
					coPhieu.SoLuong -= coPhieu.SoLuong/2;
					csv.ghiFileLichSuGiaoDich(coPhieu);
				} else {
					System.out.println(coPhieu.FileName + " giu lai");
				}
			}
		}
		System.out.println(String.format("%.2f", TienDauTu));
		System.out.println();
		// Duyet lai Cp cu
		// for (int i = 10; i < Ds100CoPhieu.size(); i++) {
		// CoPhieu cp = Ds100CoPhieu.get(i);
		// if (csv.kiemtraCPTon(cp)) {
		// if (cp.GiaHienTai > cp.Trend200 && cp.GiaHienTai > cp.Trend50)
		// System.out.println(cp.FileName + " giu lai");
		// else {
		// System.out.println(cp.FileName + " Ban di");
		// csv.docfileLichSu(cp);
		// TienDauTu += cp.SoLuong * cp.GiaHienTai;
		// cp.SoLuong = 0;
		// csv.ghiFileLichSuGiaoDich(cp);
		// }
		// }
		// }
		//
		// System.out.println("Tong Tien con lai: " + String.format("%.2f",
		// TienDauTu));
		return TienDauTu;
	}

	public void xepHang() {
		Cum centroid = listCentroid.get(CumTotNhat);
		for (CoPhieu coPhieu : Ds100CoPhieu) {
			coPhieu.KhoanCach = Math.sqrt(
					Math.pow(coPhieu.Trend200 - centroid.Trend200, 2) + Math.pow(coPhieu.Trend50 - centroid.Trend50, 2)
							+ Math.pow(coPhieu.Momentum125 - centroid.Momentum125, 2)
							+ Math.pow(coPhieu.Momentum20 - centroid.Momentum20, 2));
		}

		// sap xep theo gan cum tot nhat
		Collections.sort(Ds100CoPhieu, new Comparator<CoPhieu>() {
			@Override
			public int compare(CoPhieu o1, CoPhieu o2) {
				// TODO Auto-generated method stub
				if (o1.KhoanCach > o2.KhoanCach)
					return 1;
				else if (o1.KhoanCach < o2.KhoanCach)
					return -1;
				else
					return 0;
			}
		});
		System.out.println();
		System.out.println("Cum: " + listCentroid.indexOf(centroid) + " || " + "Tam: " + centroid.Trend200 + " || "
				+ centroid.Trend50 + " || " + centroid.Momentum125 + " || " + centroid.Momentum20);
		System.out.println("Loi nhuan trung bình: " + centroid.LoiNhuan / centroid.SoLuongPhanTu);
		for (int i = 0; i < SoLuongCoPhieuDauTu; i++) {
			System.out.println("CoPhieu: " + Ds100CoPhieu.get(i).FileName + " || " + Ds100CoPhieu.get(i).Cum + " || "
					+ Ds100CoPhieu.get(i).Trend200 + " || " + Ds100CoPhieu.get(i).Trend50 + " || "
					+ Ds100CoPhieu.get(i).Momentum125 + " || " + Ds100CoPhieu.get(i).Momentum20 + " || "
					+ Ds100CoPhieu.get(i).LoiNhuan + " || " + Ds100CoPhieu.get(i).GiaHienTai);
		}
	}

	//@Override
//	public String toString() {
//		for (int i = 0; i < Ds100CoPhieu.size(); i++) {
//			System.out.println(i + " || " + Ds100CoPhieu.get(i).FileName + " ||" + Ds100CoPhieu.get(i).Trend200 + " ||"
//					+ Ds100CoPhieu.get(i).Cum);
//		}
//		return super.toString();
//	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}

	public static void main(String[] args) {
		CSV csv = new CSV();
		float phi = 0.35f;
		csv.clearFileLichSu();
		String thoiGianBatDau = "20130414";
		TinhCoPhieu a = new TinhCoPhieu();
		float tienDauTu = 100000000;
		for (int i = 0; i < 5; i++) {
			System.out.println("Lan " + i + " || " + thoiGianBatDau);
			thoiGianBatDau = CoPhieu.addDaysS(thoiGianBatDau, -20);
			a = new TinhCoPhieu();
			a.Phi = phi;
			a.ThoiGianBatDau = thoiGianBatDau; // 20101022 // 20101201 //
												// 20110110
												// // 20110219 // 20120419 // 20130414
			a.TienDauTu = tienDauTu;
			a.layDanhSachCoPhieu();
			a.phanCum();
			// a.toString();
			a.tinh20ngaytruoc();
			a.tinhLoiNhuan20ngaytruoc();
			a.xepHang();
			a.tinh20ngaysau();
			System.out.println(a.TienDauTu);
			a.xacdinhban20ngaysau();
			System.out.println(a.TienDauTu);
			tienDauTu = a.TienDauTu;
			// tongloi += a.tinhLoiNhuanThat();
			// System.out.println();
			// System.out.println();
			// System.out.println();
			System.out.println();
			thoiGianBatDau = CoPhieu.addDaysS(thoiGianBatDau, 40);
		}

		for (int i = 0; i < a.Ds100CoPhieu.size(); i++) {
			CoPhieu cp = a.Ds100CoPhieu.get(i);
			if (csv.kiemtraCPTon(cp)) {
				System.out.println(cp.FileName + " Ban het: " + (cp.SoLuong * cp.GiaHienTai* ( 100 - phi))/100);
				a.TienDauTu += cp.SoLuong * cp.GiaHienTai;
				cp.SoLuong = 0;
				csv.ghiFileLichSuGiaoDich(cp);
			}
		}
		tienDauTu = a.TienDauTu;
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("Tien con lai: " + String.format("%.2f", tienDauTu));
	}
}
