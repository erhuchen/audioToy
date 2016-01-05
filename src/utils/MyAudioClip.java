package utils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine.Info;

/**
 * 封装我的声卡
 *目前只能开启输出line和关闭输出line
 * @author erhuchen
 *
 */
public class MyAudioClip {
	
	
	
	
	
	public static SourceDataLine getSourceDataLine(AudioFormat audioFormat){
		SourceDataLine line = null;
	
		DataLine.Info info = new Info(SourceDataLine.class, audioFormat);
		
		
		try{
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return line;
	}
	
	public static void closeSourceDataLine(SourceDataLine line){
		try{
			line.drain();
			line.close();
			
		}catch(Exception exp){
			exp.printStackTrace();
		}
	}
}
