package utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Transformator implements Runnable {

	public File inputFile;
	public File outputFile;
	public boolean isColocWise = false;
	public int start_dist;
	public int start_elev;
	public int start_azi;
	public int end_dist;
	public int end_elev;
	public int end_azi;
	
	private JTextArea ta;
	private JProgressBar progressBar;
	private JProgressBar progressBar_sub;
	private JTextField lb_output;
	
	
	
	public Transformator(
			 File inputFile,
			 File outputFile,
			 boolean isColocWise ,
			 int start_dist,
			 int start_elev,
			 int start_azi,
			 int end_dist,
			 int end_elev,
			 int end_azi,
			 JTextArea ta,
			 JProgressBar progressBar,
			 JProgressBar progressBar_sub,
			 JTextField lb_output
			){
		this.inputFile =inputFile;
		this.outputFile=outputFile;
		this.isColocWise = isColocWise;
		this.start_dist =start_dist;
		this.start_elev =start_elev;
		this.start_azi=start_azi;
		this.end_dist=end_dist;
		this.end_elev=end_elev;
		this.end_azi=end_azi;
		this.ta = ta;
		this.progressBar = progressBar;
		this.progressBar_sub = progressBar_sub;
		this.lb_output = lb_output;
	}
	
	private ArrayList<Integer> dists;
	private ArrayList<Integer> elevs;
	private ArrayList<Integer> azis;
	
	
	
	private double[] handle(){
		
		if(null == inputFile || null == outputFile){return null;}
		if(!inputFile.exists() || !outputFile.exists()){return null;}
		double[] musicResult = null;
		try {
			
			AudioFileFormat af = AudioSystem.getAudioFileFormat(inputFile);
			System.out.println(af);
			System.out.println(af.getByteLength());
			System.out.println(af.getFrameLength());
			System.out.println(af.getFormat());
			System.out.println(af.getType());
			
			AudioFormat audioFormat = af.getFormat();
			float frameRate = audioFormat.getFrameRate();
			float seconds = af.getFrameLength() / frameRate;
			System.out.println(seconds);
			System.out.println(audioFormat.getFrameSize());
			
			AudioInputStream as = AudioSystem.getAudioInputStream(inputFile);
			
			
			System.out.println(af.getFrameLength()* audioFormat.getFrameSize());
			byte[] music  = new byte[af.getFrameLength()* audioFormat.getFrameSize()];
			
			byte[] buf = new byte[af.getByteLength()];
			int count = 0;
			int index = 0;
			while(( count = as.read(buf) )!= -1){
				System.arraycopy(buf, 0, music, index, count);
				index = count;
			}
			
			
			
			ParameterParser pp = new ParameterParser(isColocWise, start_dist, start_elev, start_azi, end_dist, end_elev, end_azi);
			this.azis = pp.azis;
			this.dists = pp.dists;
			this.elevs = pp.elevs;
			
			
			MyConvolution mc = new MyConvolution( progressBar,progressBar_sub);
			
			
			double[] doubleMusic = MyUtils.mono_16bit_byte2double_array(music, 0, music.length);
			
			//=====>
			long time = System.currentTimeMillis();
			musicResult =  mc.handle(doubleMusic,dists,elevs,azis);
			 time = System.currentTimeMillis() - time;
			 //====>
			 if(null != ta){
				 ta.insert( MyUtils.getLogMeg("处理"+time/1000.0+"s") , 0);
			 }
			
			byte[] byteMusic = MyUtils.mono_16bit_double2byte_array(musicResult, 0, musicResult.length);
			
			audioFormat = new AudioFormat(audioFormat.getSampleRate(), audioFormat.getSampleSizeInBits(), 2, true, false);
			
			System.out.println("save");
			StringBuffer sbs = new StringBuffer();
			sbs.append(start_dist)
			  .append("_").append(start_elev).append("_").append(start_azi)
			  .append("-").append(end_dist).append("_").append(end_elev)
			  .append("_").append(end_azi).append("-").append(inputFile.getName());
			File of = new File(outputFile,sbs.toString());
			if(null != lb_output)
				lb_output.setText(of.getAbsolutePath());
			ByteArrayInputStream bis = new ByteArrayInputStream(byteMusic);
			BufferedInputStream bufferis = new BufferedInputStream(bis);
			AudioInputStream ais = new AudioInputStream(bufferis, audioFormat, byteMusic.length/4);
			AudioSystem.write(ais, Type.WAVE, of);
			ais.close();
			System.out.println("save over"+of.toString());
			if(null !=	ta){
				
				ta.insert(MyUtils.getLogMeg("保存"+of.toString()),0);
			}
			
			/*
			 * 如果需要可以开启播放
			SourceDataLine line = MyAudioClip.getSourceDataLine(audioFormat);
			line.start();
			line.write(byteMusic, 0, byteMusic.length);
			line.drain();
			line.close();
			MyAudioClip.closeSourceDataLine(line);
			*/
			
			
			//---->调试
//			StringBuffer sb = new StringBuffer();
//			for(int i = 0;i<azis.size();i++){
//				sb.append(azis.get(i)).append(",").append(elevs.get(i)).append(",").append(dists.get(i)).append("\n");
//			}
//			System.out.println(sb);
			//----<
			
			
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return musicResult;
	}
	
	
	public void run() {
		handle();
	}
	
	
	public static void main(String args[]){
		boolean isFix = false;
		boolean isColocWise = true;
		int start_dist = 20;
		int end_dist = 20;
		
		int start_azi = 0;
		int end_azi = 360;
		
		int start_elev = 0;
		int end_elev = 0;
		String musicPath = "d:/es01.wav";
		Transformator me = new Transformator(
				new File(musicPath), new File("d:"),isColocWise,
				start_dist,start_elev,start_azi,
				end_dist,end_elev,end_azi,
				null,null,null,null
				);

		me.handle();
	}

}
