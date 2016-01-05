package utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * 根据提供的参数读取相关的hrtf函数数据
 * 使用方法：构造对象时传入hrtf函数的文件位置
 * 然后调用相关方法获取HRIR对象，hrtf函数信息封装于HRIR对象中
 * 构造好的HRIR_Reader对象会自动保存已经使用过的HRTF函数信息，以备下次使用时调用
 * 
 * @author erhuchen
 *
 */
public class HRIR_Reader {

	private File filePath;
	private HashMap<String, HRIR> hrirs = new HashMap<String, HRIR>();
	
	public HRIR_Reader(File filePath){
		this.filePath = filePath;
	}
	public HRIR_Reader(String path){
		filePath = new File(path);
	}
	
	public HRIR readHrir(int dist,int elev,int azi){
		return this.readHrir(filePath,dist,elev,azi);
	}
	
	public HRIR readHrir(String path,int dist,int elev,int azi){
		filePath = new File(path);
		return this.readHrir(filePath,dist,elev,azi);
	}
	
	public HRIR readHrir(File filePath,int dist,int elev,int azi){
		HRIR hrir = null;
		
		String key = dist+"_"+elev+"_"+azi;
		System.out.println(key);
//		HRIR o = hrirs.get(key);
//		if(null != o)return o;
		
		this.filePath = filePath;
		if(filePath.exists()){
			
			String filename = filePath +"/dist"+dist+"/elev"+elev+"/azi"+azi+"_elev"+elev+"_dist"+dist+".dat";
			File file = new File(filename);
			if(file.exists()){
				hrir = new HRIR();
				hrir.setDistance(dist);
				hrir.setElevation(elev);
				hrir.setAzimuth(azi);
				double[] left = new double[1024];
				double[] right = new double[1024];
				
				try {
					FileInputStream fis = new FileInputStream(file);
					BufferedInputStream bis = new BufferedInputStream(fis);
					byte[] buffer = new byte[8];
					
					for(int i = 0 ;i<1024;i++){
						bis.read(buffer);
						double l = MyUtils.getDouble(buffer);
						left[i] = l;
					}
					for(int i = 0 ;i<1024;i++){
						bis.read(buffer);
						double r = MyUtils.getDouble(buffer);
						right[i] = r;
					}
					bis.close();
					fis.close();
					hrir.setLeft(left);
					hrir.setRight(right);
					
					hrirs.put(key, hrir);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return hrir;
	}
	
	
	public static void main(String args[]){
		String path = AppConfig.RTF_PATH;
		HRIR_Reader hr = new HRIR_Reader(path);
		HRIR h = hr.readHrir(20, 0, 0);
		double[] left = h.getLeft();
		double[] right = h.getRight();
		for(int i = 0;i<1024;i++)
			System.out.println(i+"   "+left[i]);
		System.out.println("--------------");
		for(int i = 0;i<1024;i++)
			System.out.println(i+"   "+right[i]);
	}
}
