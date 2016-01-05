package utils;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JProgressBar;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;


/**
 * 卷积分的封装
 * @author erhuchen
 *
 */
public class MyConvolution {
	
	String path = AppConfig.RTF_PATH;
	HRIR_Reader hr = new HRIR_Reader(path);
	JProgressBar progressBar;
	JProgressBar progressBar_sub;
	
	public MyConvolution(){}
	public MyConvolution(JProgressBar progressBar, JProgressBar progressBar_sub){
		this.progressBar = progressBar;
		this.progressBar_sub = progressBar_sub;
	}
	
	/**
	 * 该方法根据一组坐标位置对music进行3d处理
	 * music为单声道文件（必须）
	 * @param music
	 * @param dists
	 * @param elevs
	 * @param azis
	 * @return
	 */
	public double[] handle(double[] music,ArrayList<Integer> dists,ArrayList<Integer> elevs, ArrayList<Integer> azis){
		
		int partCount = azis.size();		//整个音频分为多少段
		System.out.println("-->"+partCount);
		if(null !=progressBar){
			progressBar.setMaximum(partCount);
			progressBar.setMinimum(-1);
			progressBar.setValue(-1);
		}
		
		int musicLength = music.length;		//音频的长度
		
		int musicPartLength = musicLength / partCount; //分割音频后，每段的长度
		int last_musicPartLength = musicPartLength + musicLength % partCount; //分割音频后，最后一段的长度

		//--》注意，固定长度的音频有partCount-1段，最后一段的长度不确定
		
		double[] music_left  = new double[musicLength+1024];
		double[] music_right = new double[musicLength+1024];
		for(int i = 0;i< partCount;i++){
			
			int dist = dists.get(i);
			int elev = elevs.get(i);
			int azi = azis.get(i);
			
			
			HRIR h = hr.readHrir(dist, elev, azi);/////////////////////////////////////////////////////////
			double[] left = h.getLeft();
			double[] right = h.getRight();
			
			if(i == partCount-1){ //最后一段
				double[] music_temp = new double[last_musicPartLength];
				System.arraycopy(music, i*musicPartLength, music_temp, 0, last_musicPartLength);
				double[] result = handle(music_temp, left);
				for(int j = 0;j<result.length;j++){
					music_left[i*musicPartLength+j] += result[j]; 
				}
				//---------->
				double[] music_temp2 = new double[last_musicPartLength];
				System.arraycopy(music, i*musicPartLength, music_temp2, 0, last_musicPartLength);
				double[] result2 = handle(music_temp2, right);
				for(int j = 0;j<result.length;j++){
					music_right[i*musicPartLength+j] += result2[j]; 
				}
				
			}else{//不是最后一段
				double music_temp[] = new double[musicPartLength];
				System.arraycopy(music, i*musicPartLength, music_temp, 0, musicPartLength);
				double[] result = handle(music_temp, left);
				for(int j = 0;j<result.length;j++){
					music_left[i*musicPartLength+j] += result[j]; 
				}
				//---------->
				double[] music_temp2 = new double[musicPartLength];
				System.arraycopy(music, i*musicPartLength, music_temp2, 0, musicPartLength);
				double[] result2 = handle(music_temp2, right);
				for(int j = 0;j<result.length;j++){
					music_right[i*musicPartLength+j] += result2[j]; 
				}
			}
			
			if(null !=progressBar)
				progressBar.setValue(i);
			
		}
		
		if(null !=progressBar)
			progressBar.setValue(partCount);
		
		double[] result = new double[2*(musicLength+1024)];
		for(int i = 0 ;i< musicLength+1024;i++){
			result[i*2] = music_left[i];
			result[i*2+1] = music_right[i];
		}
		
		return result;
//		int part = azis.size(); //整个音频分为多少段
//		int frameCount = af.getFrameLength(); //整个音频有多少帧
//		int framePerPart = frameCount / part;//每一小段音频有多少帧
//		int bytesPerPart = framePerPart * audioFormat.getFrameSize(); //每一小段音频有多少字节Byte
//		int lastFramePartLength = frameCount /part + frameCount % part; //最后一段音频有多少帧
//		int bytesLastPart = lastFramePartLength * audioFormat.getFrameSize();//最后一段音频多少字节Byte
//		
//		double[] musicResult_temp = new double[af.getFrameLength()* audioFormat.getFrameSize() + 2048 ];
//		
//		for(int i = 0 ;i<part-1 ;i++){
//			double[] frgement =  new double[bytesPerPart];
//			System.arraycopy(doubleMusic, i*bytesPerPart, frgement, 0, bytesPerPart);
//			musicResult = mc.handle(frgement, left_hrtf, right_hrtf)
//		}
		
	}
	
	/**
	 * 一个有问题的方法
	 * 将music使用left_hrtf和right_hrtf分别卷积分，最后拼接为双声道music返回
	 * 最后music的长度应该为  2*music +2046 个 double
	 * @param music
	 * @param left_hrtf
	 * @param right_hrtf
	 * @return
	 */
	public double[] handle(double[] music, double[] left_hrtf, double[] right_hrtf){
		int length = music.length;
		int partCount = 0 ;
		if(length%1024==0){
			partCount = length / 1024;
		}else{
			partCount = length / 1024 + 1;
		}
		double[] leftResult = new double[1024*partCount+1024];
		double[] rightResult = new double[1024*partCount+1024];
		
		for(int i = 0 ;i<partCount; i++){
			
			double[] part = new double[1024]; 
			
			if(i <= partCount-2 ) 
				System.arraycopy(music, i*1024, part, 0, 1024);
			else
				System.arraycopy(music, i*1024, part, 0,  length-1024*(partCount-1));
			double[] resultPart = convert(part,left_hrtf);
			for(int j = 0 ;j<resultPart.length;j++){
				leftResult[i*1024+j] += resultPart[j];
			}
			resultPart = convert(part, right_hrtf);
			for(int j = 0 ;j<resultPart.length;j++){
				rightResult[i*1024+j] += resultPart[j];
			}
			//---->debug
//			System.out.println(i);
			//----:>
		}
		double[] musicResult = new double[length*2+2046];
		for(int i = 0 ;i<length+1023;i++){
			musicResult[2*i] = leftResult[i];
			musicResult[2*i+1] = rightResult[i];
		}
		return musicResult;
	}
	
	
	/**
	 * 不拼接为双声道 ，将music与hrtf进行卷积分，得到的结果长度为 music+1024 的长度
	 * @param music
	 * @param left_hrtf
	 * @param right_hrtf
	 * @return
	 */
	private double[] handle(double[] music, double[] hrtf){
		int length = music.length;
		int partCount = 0 ;
		if(length%1024==0){
			partCount = length / 1024;
		}else{
			partCount = length / 1024 + 1;
		}
		double[] leftResult = new double[1024*partCount+1024];
		
		if(null != progressBar_sub){
			progressBar_sub.setMaximum(partCount);
			progressBar_sub.setMinimum(0);
			progressBar_sub.setValue(0);
		}
		
		for(int i = 0 ;i<partCount; i++){
			
			double[] part = new double[1024]; 
			
			if(i <= partCount-2 ) 
				System.arraycopy(music, i*1024, part, 0, 1024);
			else
				System.arraycopy(music, i*1024, part, 0,  length-1024*(partCount-1));
			double[] resultPart = convert(part,hrtf);
			for(int j = 0 ;j<resultPart.length;j++){
				leftResult[i*1024+j] += resultPart[j];
			}
			
			if(null != progressBar_sub)
				progressBar_sub.setValue(i);
			//---->debug
//			System.out.println(i);
			//----:>
		}
		if(null != progressBar_sub)
			progressBar_sub.setValue(partCount);
		
		double[] r = new double[length+1024];
		System.arraycopy(leftResult, 0, r, 0, r.length);
		
		return r;
	}
	
	
	/**
	 * 将double[]变为Complex[],保持数组长度不变
	 * @param value
	 * @return
	 */
	private Complex[] toComplex(double[] value){
		int count = value.length;
		Complex[] result = new Complex[count];
		for(int i = 0 ;i< count;i++){
			double d = value[i];
			Complex c = new Complex(d, 0);
			result[i] = c;
		}
		return result;
	}
	
	/**
	 * 将Complex[]变为double[]，保持数组长度不变
	 * @param value
	 * @return
	 */
	private double[] toDouble(Complex[] value){
		int count = value.length;
		double[] result = new double[count];
		for(int i = 0 ;i<count;i++){
			double d = value[i].getReal();
			result[i] = d;
		}
		return result;
	}
	
	
	
	
	
	
	/**
	 * 将 value和hrtf进行卷积分运算，返回卷积后的结果
	 * 结果长度为 value + hrtf 的长度和
	 * @param value
	 * @param hrtf
	 * @return
	 */
	private double[] convert(double[] value , double[] hrtf){

		double[] ext_value = new double[value.length+hrtf.length];
		double[] ext_hrtf = new double[value.length+hrtf.length];
		System.arraycopy(value, 0, ext_value, 0, value.length);
		System.arraycopy(hrtf, 0, ext_hrtf, 0, 1024);
		
		FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
		Complex[] c_value = fft.transform(ext_value, TransformType.FORWARD);
		Complex[] c_hrtf = fft.transform(ext_hrtf, TransformType.FORWARD);
		int N = value.length + hrtf.length;
		
		Complex[] c = new Complex[N];
		for(int i = 0; i<N; i++){
			c[i]  = c_value[i].multiply(c_hrtf[i]);
		}
		return toDouble(fft.transform(c, TransformType.INVERSE));
	}
}
