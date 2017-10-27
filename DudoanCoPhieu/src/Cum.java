
public class Cum extends ThuocTinh {
	
	public int ID;
	public int SoLuongPhanTu;
	public double TongTrend200;
	public double TongTrend50;
	public double TongMomentum125;
	public double TongMomentum20;
	public double LoiNhuan;
	public double DauTu;
	public double TyleLoiNhuan = 0;
	public Cum(CoPhieu cp){
		this.Trend200 = cp.Trend200;
		this.Trend50 = cp.Trend50;
		this.Momentum125 = cp.Momentum125;
		this.Momentum20 = cp.Momentum20;
	}
	
	public void xacDinhTam(){
		Trend200 = TongTrend200 / SoLuongPhanTu;
		Trend50 = TongTrend50 / SoLuongPhanTu;
		Momentum125 = TongMomentum125 / SoLuongPhanTu;
		Momentum20 = Momentum20 / SoLuongPhanTu;
		TongTrend200 = 0;
		TongTrend50 = 0;
		TongMomentum125 = 0;
		TongMomentum20 = 0;
	}
}
