package test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine.Info;

import utils.MyUtils;

/**
 * 这是一个混响实验
 * 获取byte数组，写入到声卡中
 * 加入混响效果
 * @author erhuchen  
 *
 */
public class T3 {

	public static void main(String args[]){
		T3 t = new T3();
		t.begin();
	}
	
	
	private void begin(){
		
		try{
			File file = new File("d:/GloryBe 2c.wav");
			InputStream audioSrc = new FileInputStream("D:/work/MATLAB/声学实验/sc01.wav");
			//audioSrc = new FileInputStream(file);
		    InputStream bufferedIn = new BufferedInputStream(audioSrc);
		    
		    
			AudioInputStream ais =  AudioSystem.getAudioInputStream(bufferedIn);
			if(null != ais){
				AudioFormat af = ais.getFormat();
				
				int channel = af.getChannels();
				int frameSize = af.getFrameSize();
				float frameRate = af.getFrameRate();
				int sampleSizeInBits = af.getSampleSizeInBits();
				float sampleRate = af.getSampleRate();
				
				StringBuffer sb = new StringBuffer();
				sb.append("channels: ").append(channel).append("\n")
				  .append("frameSize: ").append(frameSize).append("\n")
				  .append("frameRate: ").append(frameRate).append("\n")
				  .append("sampleSizeInBits: ").append(sampleSizeInBits).append("\n")
				  .append("sampleRate: ").append(sampleRate);
				System.out.println(sb);
				
				
				int differ = 1024;//这个应该为秒计算，将来再改
				int bufferSize = (int)frameRate * frameSize; //每次缓冲一帧
				
				byte[] buffer = new byte[bufferSize];
				byte[] echo = new byte[bufferSize + differ];
				
				double[] d_tail = new double[differ/2]; //由于目前读的是16bit的文件，每2个byte变为一个double
				double[] d_echo = new double[bufferSize];
				
				double gen = 0.5; //回声的音量增益
				////
				
				SourceDataLine line = getLine(af);
				////
				
				System.out.println(line.available()+"/"+line.getBufferSize());
				
				
				line.start();
				int inBytes = 0;
				while((inBytes != -1 ) ){
					inBytes = ais.read(buffer,0,buffer.length);
					if(inBytes >=0){
						
						double[] d_buffer = MyUtils.mono_16bit_byte2double_array(buffer, 0, inBytes);
						
						for(int i = 0 ;i<d_buffer.length;i++){
							d_echo[i] = d_buffer[i] ;
						}

						for(int i = 0 ;i<differ/2;i++){
							d_echo[i] = d_echo[i]  + d_tail[i];
						}
						for(int i = differ/2; i < d_buffer.length;i++){
							d_echo[i] = d_echo[i] + d_buffer[i - differ/2];
						}
						
						
						echo = MyUtils.mono_16bit_double2byte_array(d_echo, 0, inBytes/2);
						
						int outBytes = line.write(echo, 0, inBytes);
						
						
						
						Arrays.fill(d_tail,0);
						Arrays.fill(d_echo, 0);

						if(inBytes > differ){
							System.arraycopy(d_buffer, (inBytes-differ)/2, d_tail, 0, differ/2);
						}else{
							System.arraycopy(d_buffer, 0, d_tail, 0, differ/2);
						}
						
					}
					
				}
				 
				line.drain();
				line.close();
				
			}
			ais.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private SourceDataLine getLine(AudioFormat audioFormat){
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

}
