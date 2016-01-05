package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import utils.MyUtils;

public class MyPanel02 extends JPanel{
	
	JFileChooser fileChooser;
	
	JButton btn;
	JTextField textField ;
	JLabel label;
	JButton btn_play;
	JButton btn_stop;
	JPanel panel;
	AudioInputStream ais = null;
	Clip clip = null;
	
	public MyPanel02(){
		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		
		setLayout(new FlowLayout());
		//----最顶上的面板
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.X_AXIS));
		panel.add(p1);
		panel.add(Box.createVerticalStrut(5));
		//----添加文件选择显示结果用的地址框
		textField = new JTextField(20);
		textField.setEditable(false);
		p1.add(textField);
		//----添加文件选择按钮
		btn = new JButton("选择文件");
		p1.add(btn);
		//----添加播放按钮
		btn_play = new JButton(">");
		btn_play.setEnabled(false);
		p1.add(btn_play);
		//----添加停止按钮
		btn_stop = new JButton("■");
		p1.add(btn_stop);
		btn_stop.setEnabled(false);
		//----添加显示文件信息的标签
		JPanel panel_label = new JPanel();
		panel_label.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY) );
		label = new JLabel(" ");
		panel_label.add(label);
		panel.add(panel_label);
		
		
		setLayout(new BorderLayout());
		add(panel);
		
		//----
		
		btn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				initFileChooser("open");
				int x = fileChooser.showOpenDialog(MyPanel02.this);
				if(x == JFileChooser.APPROVE_OPTION){
					
					File file = fileChooser.getSelectedFile();
					textField.setText(file.getAbsolutePath());
					
//					tf_outputPath.setText(file.getParent());
					try {
						AudioInputStream ais =  AudioSystem.getAudioInputStream(file);
						AudioFormat af = ais.getFormat();
						label.setText(af.toString());
						ais.close();
						btn_play.setEnabled(true);
					} catch (UnsupportedAudioFileException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
//					txArea.insert(MyUtils.getLogMeg("定位"+file.getAbsolutePath()), 0);
				}
			}
		});

btn_stop.addActionListener(new ActionListener() {
	
	public void actionPerformed(ActionEvent e) {
		btn_play.setText(">");
		if(null !=clip){
			clip.stop();
			clip.close();
			clip = null;
		}
		if(null != ais){
			try {
				ais.close();
				ais = null;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}
});

btn_play.addActionListener(new ActionListener() {
	
	public void actionPerformed(ActionEvent e) {
		String s = btn_play.getText();
		
		if(">".equals(s)){
			btn_play.setText("||");
			btn_stop.setEnabled(true);
			
			String str = textField.getText();
			if(null != str && str.trim().length()>0){
				File file = new File(str);
				if(file.exists() && file.isFile()){
					try {
							if(null == ais || null == clip ){
							ais =  AudioSystem.getAudioInputStream(file);
							 clip = AudioSystem.getClip();
							 clip.addLineListener(new LineListener() {
									@Override
									public void update(LineEvent event) {
										if(event.getType() == LineEvent.Type.STOP){
											if(clip.getFramePosition() == clip.getFrameLength()){
												btn_stop.doClick();
											}
										}
									}
								});
							 
							 clip.open(ais);
							 
						}
						 
						clip.start();
						
					} catch (UnsupportedAudioFileException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (LineUnavailableException e1) {
						e1.printStackTrace();
					} 
				}
			}
		}else if("||".equals(s)){
			
			btn_play.setText(">");
				clip.stop();
		}
		
	}
});
		
		
	}

	private void initFileChooser(String type){
		if("open".equals(type)){
			fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY  );
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					return "wav";
				}
				
				@Override
				public boolean accept(File f) {
					boolean x = false;
					if(f.exists() && f.isFile()){
						if(f.getName().endsWith(".wav")){
							x = true;
						}
					}
					return x;
				}
			});
		}
		if("save".equals(type)){
			fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY  );
		}
	}
	
	

	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(new MyPanel02());
		f.pack();
		f.setVisible(true);

	}

}
