package utils;

import java.text.SimpleDateFormat;
import java.util.Date;




/**
 * 16bit byte[]e变double[]
 * double[]变16bit byte[]
 * @author erhuchen
 *
 */
public class MyUtils {
	
	public static String getLogMeg(String msg){
		return getCurrentDate()+": "+msg+"\r\n";
	}
	
	public static String getCurrentDate(){
		return getCurrentDate("HH:mm:ss");
	}
	
	public static String getCurrentDate(String format){
		String str = "";
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(now);
		
	}

	/**
	 * 将单声道（双声道同样适用）的16bit 数据（2byte），换为单声道的64bit数据（8byte）
	 * 算法为依次取出两个byte ， 第一个byte为高8位，第二个为低8位，换算为一个short
	 * 然后将该short/32768，得到一个double
	 * @param buffer 待换算的byte数组
	 * @param start  数组的起始下标，最小为0
	 * @param length 需换算的数据的长度，必须为偶数
	 * @return 换算后的double数组，长度为length/2
	 * @author erhuchen 2015-12-17
	 */
	public static double[] mono_16bit_byte2double_array(byte[] buffer,int start, int length){
		double[] result = new double[length/2];
		
		for(int i = start; i< length/2;i++){
			
			byte bh = buffer[2*i];
			byte bl = buffer[2*i+1];
			
			short s = (short)((bl & 0x00FF)<<8 | bh & 0x00FF);
			double d = (double)s / 32768.0 ;
			
			result[i-start] = d ;
		}
		
		return result;
	}
	
	
	/**
	 * 将单声道（双声道同样适用）的64bit 数据（8byte），换为单声道的字节数组（其实为16bit数据）
	 * 算法为依次取出一个double，将其转换为16bit的short -> double * 32767 然后取整
	 * 然后将该short 高8位存为第一个byte， 低8位存为第二个byte
	 * @param buffer 64bit的数据
	 * @param start  数据的起始位置
	 * @param length  数据的长度
	 * @return  转换后的字节数组，长度为   2*length
	 * @author erhuchen 2015-12-17
	 */
	public static byte[] mono_16bit_double2byte_array(double[] buffer,int start, int length){
		byte[] result = new byte[length*2];
		
		for(int i = 0 ;i<length; i++){
			double d = buffer[i];
			
			 
			double dd = d *32768.0 ;
			
			if(dd>32767) {
				dd = 32767;
			}
			if(dd<-32768) {
				dd= -32768;
			}
			
			short s = (short)(dd );
			
			byte bl = (byte)(0x00FF &(s >> 8));
			byte bh = (byte)(0x00FF & s);
			
			result[2*i] = bh;
			result[2*i+1] = bl;
		}
		
		
		return result;
	}
	
	/**
	 * 把每8个byte变为一个double
	 * 用于读取hrtf文件时的变换，读到的是byte[]，利用该函数变为double[]
	 * @param b 长度为8 的byte数组
	 * @return 变成一个double
	 */
	public static double getDouble(byte[] b) { 
	     long l; 
	     l = b[0]; 
	     l &= 0xff; 
	     l |= ((long) b[1] << 8); 
	     l &= 0xffff; 
	     l |= ((long) b[2] << 16); 
	     l &= 0xffffff; 
	     l |= ((long) b[3] << 24); 
	     l &= 0xffffffffl; 
	     l |= ((long) b[4] << 32); 
	     l &= 0xffffffffffl; 
	     l |= ((long) b[5] << 40); 
	     l &= 0xffffffffffffl; 
	     l |= ((long) b[6] << 48); 
	     l &= 0xffffffffffffffl; 
	     l |= ((long) b[7] << 56); 
	     return Double.longBitsToDouble(l); 
	 } 
}
