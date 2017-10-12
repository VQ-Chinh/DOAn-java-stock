import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TinhCoPhieu {
	public List<CoPhieu> Ds100CoPhieu = new ArrayList<CoPhieu>();
	public List<CoPhieu> Ds100CoPhieuDaSapXep;
	public List<Cum> listCentroid;
	public Float TiLeLoiNhuan = 0.0f;
	public int CumTotNhat;

	public TinhCoPhieu() {
		Ds100CoPhieu = new ArrayList<CoPhieu>();
		Ds100CoPhieuDaSapXep = new ArrayList<CoPhieu>();
	}

	public void layDanhSachCoPhieu() {
		File dir = new File("cophieu68.vn_Revert/");
		String[] children = dir.list();
		if (children == null) {
			System.out.println("Either dir does not exist or is not a directory");
		} else {
			CoPhieu cp;

			for (int i = 1; i < 101; i++) {
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
			coPhieu.tinhTrungBinh(500);
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

			System.out.print("Lan: " + count + "  " + change + "  ");
			for (Cum centroid : listCentroid) {
				System.out.print("cum" + listCentroid.indexOf(centroid) + ": " + centroid.SoLuongPhanTu + "||");
				centroid.xacDinhTam();
				centroid.SoLuongPhanTu = 0;
			}
			System.out.println("");

			count++;
		}
		for (Cum cum : listCentroid) {
			System.out.println("Cum " + listCentroid.indexOf(cum) + ": " + cum.Trend200 + " || " + cum.Trend50 + " || "
					+ cum.Momentum125 + " || " + cum.Momentum20);
		}

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

	public void tinhMuaBanThu() {

		for (CoPhieu cp : Ds100CoPhieu) {

			double gia = cp.GiaHienTai, longTerm = cp.Trend200, shortTerm = cp.Trend50, giaTruoc = cp.GiaTruoc,loiNhuan = 0;
			
			CSV ghifile = new CSV();
			ghifile.taoMoiFileLichSu(cp);
			for (int i = 0; i < 20; i++) {
				giaTruoc = cp.GiaTruoc;
				cp.tinhEMA();
				gia = cp.GiaHienTai;
				longTerm = cp.Trend200;
				shortTerm = cp.Trend50;
				loiNhuan = (gia - giaTruoc)/gia * 100  ;
				if (gia > longTerm && gia > shortTerm && loiNhuan > TiLeLoiNhuan)
					cp.SoLuong += 100;
				else if (gia > longTerm && gia <= shortTerm && loiNhuan > TiLeLoiNhuan)
					cp.SoLuong += 50;

				else if (gia < longTerm && gia < shortTerm && -loiNhuan > -TiLeLoiNhuan) {
					if (cp.SoLuong >= 100)
						cp.SoLuong -= 100;
					else
						cp.SoLuong = 0;
				} else if (gia > longTerm && gia >= shortTerm && -loiNhuan > -TiLeLoiNhuan) {
					if (cp.SoLuong >= 50)
						cp.SoLuong -= 50;
					else
						cp.SoLuong = 0;
				}
				ghifile.ghiFileLichSU(cp);
			}
		}
	}

	public void tinhLoiNhuanThu() {
		CSV docfile = new CSV();
		for (CoPhieu coPhieu : Ds100CoPhieu) {
			coPhieu.LoiNhuan = docfile.docFileLichSu(coPhieu);
			listCentroid.get(coPhieu.Cum).LoiNhuan += docfile.docFileLichSu(coPhieu);

		}

		double bestProfit = 0;
		for (Cum cum : listCentroid) {
			if (bestProfit < cum.LoiNhuan) {
				bestProfit = cum.LoiNhuan;
				CumTotNhat = listCentroid.indexOf(cum);
			}
			System.out.println("Cum: " + listCentroid.indexOf(cum) + " || " + " Loi Nhuan: " + cum.LoiNhuan);
		}
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
		
		System.out.println("Tam: " + centroid.Trend200 + " || " +  centroid.Trend50 + " || " +  centroid.Momentum125 + " || " +  centroid.Momentum20);
		for (int i = 0; i < 10; i++) {
			System.out.println("CoPhieu: " + Ds100CoPhieu.get(i).FileName+ " || " +  Ds100CoPhieu.get(i).Cum + " || " +  Ds100CoPhieu.get(i).Trend200 + " || " +  Ds100CoPhieu.get(i).Trend50 + " || " +  Ds100CoPhieu.get(i).Momentum125 + " || " +  Ds100CoPhieu.get(i).Momentum20 + " || " +  Ds100CoPhieu.get(i).LoiNhuan + " || " +  Ds100CoPhieu.get(i).KhoanCach);
		}
	}

	@Override
	public String toString() {
		for (int i = 0; i < Ds100CoPhieu.size(); i++) {
			System.out.println(i + " || " + Ds100CoPhieu.get(i).FileName + " ||" + Ds100CoPhieu.get(i).Trend200 + " ||"
					+ Ds100CoPhieu.get(i).Cum);
		}
		return super.toString();
	}

	public static void main(String[] args) {
		TinhCoPhieu a = new TinhCoPhieu();
		a.layDanhSachCoPhieu();
		a.phanCum();
		a.toString();
		a.tinhMuaBanThu();
		a.tinhLoiNhuanThu();
		a.xepHang();
	}
}
