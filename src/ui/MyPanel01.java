package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import utils.MyAudioClip;
import utils.MyUtils;
import utils.Transformator;

public class MyPanel01 extends JPanel {

	JFileChooser fileChooser;
	
	JButton btn;
	JTextField textField ;
	JLabel label;
	JButton btn_play;
	JButton btn_stop;
	
	ButtonGroup group1;
	
	JRadioButton rb_clock_wise;
	JRadioButton rb_clock_rev;
	ButtonGroup group2;
	
	JComboBox<Integer> start_dist;
	JComboBox<Integer> start_elev;
	JComboBox<Integer> start_azi;
	
	JComboBox<Integer> end_dist;
	JComboBox<Integer> end_elev;
	JComboBox<Integer> end_azi;
	
	JTextArea txArea;
	JProgressBar progressBar;
	JProgressBar progressBar_sub;
	JTextField tf_outputPath ;
	JButton btn_outputPath;
	JButton btn_comput;
	
	JPanel panel;
	
	JTextField lbOutputFile;
	JButton btn_out_play;
	JButton btn_out_stop;
	JButton btn_out_openFile;
	JProgressBar out_play_progressBar;
	
	Timer timer = null;
	
	AudioInputStream ais = null;
	Clip clip = null;
	
	AudioInputStream ais2 = null;
	Clip clip2 = null;

	
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
	
	public MyPanel01(){
		
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
		//----层面板p2 ，装了移动法则，和起点位置，终点位置
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.Y_AXIS));
		
		
		JPanel p2_1 = new JPanel();
		p2_1.setLayout(new BoxLayout(p2_1, BoxLayout.X_AXIS));
		//->装了起点位置
		JPanel p3_1 = new JPanel();
		p3_1.setLayout(new GridLayout(3, 2,1,1));
		start_dist = new JComboBox<Integer>(new Integer[]{20,30,40,50,75,100,130,160});
		start_elev = new JComboBox<Integer>(new Integer[]{-40,-30,-20,-10,0,10,20,30,40,50,60,70,80,90});
		start_elev.setSelectedIndex(4);
		start_azi = new JComboBox<Integer>();
		fillStartAzi(0);
		start_dist.setPreferredSize(new Dimension(50, 20));
		start_elev.setPreferredSize(new Dimension(50, 20));
		start_azi.setPreferredSize(new Dimension(50, 20));
		p3_1.add(new JLabel("距离"));
		p3_1.add(start_dist);
		p3_1.add(new JLabel("高度角"));
		p3_1.add(start_elev);
		p3_1.add(new JLabel("方位角"));
		p3_1.add(start_azi);
		p3_1.setBorder(new TitledBorder("起点位置"));
		//-:>装了起点位置
		p2_1.add(p3_1);
		p2_1.add(Box.createHorizontalStrut(5));
		//->装了终点位置
		JPanel p3_2 = new JPanel();
		p3_2.setLayout(new GridLayout(3, 2,1,1));
		end_dist = new JComboBox<Integer>(new Integer[]{20,30,40,50,75,100,130,160});
		end_elev = new JComboBox<Integer>(new Integer[]{-40,-30,-20,-10,0,10,20,30,40,50,60,70,80,90});
		end_elev.setSelectedIndex(4);
		end_azi = new JComboBox<Integer>();
		fillEndAzi(0);
		end_dist.setPreferredSize(new Dimension(50, 20));
		end_elev.setPreferredSize(new Dimension(50, 20));
		end_azi.setPreferredSize(new Dimension(50, 20));
		p3_2.add(new JLabel("距离"));
		p3_2.add(end_dist);
		p3_2.add(new JLabel("高度角"));
		p3_2.add(end_elev);
		p3_2.add(new JLabel("方位角"));
		p3_2.add(end_azi);
		p3_2.setBorder(new TitledBorder("终点位置"));
		//-:>装了终点位置
		p2_1.add(p3_2);
		
		//---
		group2 = new ButtonGroup();
		rb_clock_wise = new JRadioButton("顺时针");
		rb_clock_wise.setSelected(true);
		rb_clock_rev = new JRadioButton("逆时针");
		group2.add(rb_clock_wise);
		group2.add(rb_clock_rev);
		JPanel p2_2 = new JPanel();
		p2_2.setBorder(new TitledBorder("移动法则"));
		p2_2.add(rb_clock_wise);
		p2_2.add(rb_clock_rev);
		
		p2.add(p2_2);
		p2.add(p2_1);
		
		//----
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.X_AXIS));
		
		p3.add(p2);
		
		JPanel p3_4 = new JPanel();
		p3_4.setPreferredSize(new Dimension(300,100));
		p3_4.setLayout(new BoxLayout(p3_4, BoxLayout.Y_AXIS));
		p3_4.add(Box.createVerticalStrut(5));
		txArea = new JTextArea();
		
//		txArea.setPreferredSize(new Dimension(150,130));
//		txArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
//		txArea.setLineWrap(false);
		txArea.setEditable(false);
		JScrollPane p3_3 = new JScrollPane(txArea);
		p3_3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		p3_3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		p3_4.add(p3_3);
		
		progressBar = new JProgressBar();
		p3_4.add(progressBar);
		progressBar_sub = new JProgressBar();
		p3_4.add(progressBar_sub);
		p3.add(p3_4);
		
		panel.add(p3);
		panel.add(Box.createVerticalStrut(5));
		//----
		JPanel p4 = new JPanel();
		p4.setLayout(new BoxLayout(p4, BoxLayout.X_AXIS));
		tf_outputPath = new JTextField(20);
		tf_outputPath.setEditable(false);
		p4.add(tf_outputPath);
		btn_outputPath = new JButton("输出目录");
		p4.add(btn_outputPath);
		btn_comput = new JButton("开始计算");
		p4.add(btn_comput);
		
		
		
		panel.add(p4);
		
		//--------
		panel.add(Box.createVerticalStrut(5));
		//--------
		
		JPanel p5 = new JPanel();
		p5.setLayout(new BoxLayout(p5, BoxLayout.X_AXIS));
		p5.add(new JLabel("输入："));
		lbOutputFile = new JTextField();
		lbOutputFile.setEditable(false);
		p5.add(lbOutputFile);
		btn_out_openFile = new JButton("……");
		p5.add(btn_out_openFile);
		btn_out_play = new JButton(">");
		p5.add(btn_out_play);
		btn_out_stop = new JButton("■");
		p5.add(btn_out_stop);
		
		panel.add(p5);
		//-----------------------------------
		out_play_progressBar = new JProgressBar();
		out_play_progressBar.setPreferredSize(new Dimension(300,20));
		out_play_progressBar.setMinimum(0);
		out_play_progressBar.setValue(0);
		panel.add(out_play_progressBar);
		panel.setForeground(Color.ORANGE);
		
		
		//----将gird面板加到最底层面板
		setLayout(new BorderLayout());
		add(panel);
		
		//================================================================
		//================================================================
		//================================================================
		
		btn_out_openFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				initFileChooser("open");
				int x = fileChooser.showOpenDialog(MyPanel01.this);
				if(x == JFileChooser.APPROVE_OPTION){
					
					File file = fileChooser.getSelectedFile();
					lbOutputFile.setText(file.getAbsolutePath());
				}
			}
		});
		//------------------
		btn_out_play.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//------------------
				
				String s = btn_out_play.getText();
				
				if(">".equals(s)){
					
					
					String str = lbOutputFile.getText();
					if(null != str && str.trim().length()>0){
						btn_out_play.setText("||");
						File file = new File(str);
						if(file.exists() && file.isFile()){
							try {
									if(null == ais2 || null == clip2 ){
									ais2 =  AudioSystem.getAudioInputStream(file);
									 clip2 = AudioSystem.getClip();
									 if(null != clip2)
									 clip2.open(ais2);
									 clip2.addLineListener(new LineListener() {
										@Override
										public void update(LineEvent event) {
											if(event.getType() == LineEvent.Type.STOP){
												if(clip2.getFramePosition() == clip2.getFrameLength()){
													btn_out_stop.doClick();
												}
											}
												
										}
									});
									 out_play_progressBar.setMaximum(clip2.getFrameLength());
									 timer = new Timer(20, new ActionListener() {
										@Override
										public void actionPerformed(ActionEvent e) {
											out_play_progressBar.setValue(clip2.getFramePosition());
											out_play_progressBar.setStringPainted(true);
										}
									});
									 timer.start();
								}
								 
								clip2.start();
								
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
					
					btn_out_play.setText(">");
					if(null != clip2)
						clip2.stop();
				}
				//------------------
			}
		});
		//----
		
		btn_out_stop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
					btn_out_play.setText(">");
					if(null !=clip2){
						clip2.stop();
						clip2.close();
						clip2 = null;
					}
					if(null != ais2){
						try {
							ais2.close();
							ais2 = null;
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					if(null != timer){
						timer.stop();
						timer = null;
					}
				}
		});
		
		//----
		btn_comput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				btn_out_stop.doClick();
				
				String musicPath = textField.getText();
				String toPath = tf_outputPath.getText();
				boolean isColocWise = true;
				int start_dist = 20;
				int start_elev = 0;
				int start_azi = 0;
				int end_dist = 20;
				int end_elev = 0;
				int end_azi = 0;
				
				start_dist = (Integer)MyPanel01.this.start_dist.getSelectedItem();
				start_elev = (Integer)MyPanel01.this.start_elev.getSelectedItem();
				start_azi = (Integer)MyPanel01.this.start_azi.getSelectedItem();
				end_dist = (Integer)MyPanel01.this.end_dist.getSelectedItem();
				end_elev = (Integer)MyPanel01.this.end_elev.getSelectedItem();
				end_azi = (Integer)MyPanel01.this.end_azi.getSelectedItem();
				
				if(rb_clock_wise.isSelected()) isColocWise = true;
				else isColocWise = false;
				
				//////////////////////////////////////
				//=====}}>>调用业务逻辑，对音频文件进行3d运算//////
				//////////////////////////////////////
				Transformator t = new Transformator(
				new File(musicPath), new File(toPath),isColocWise,
				start_dist,start_elev,start_azi,
				end_dist,end_elev,end_azi,
				txArea,progressBar,progressBar_sub,lbOutputFile
				);
				
				Thread thread = new Thread(t);
				thread.start();

			}
			
		});
		
		//----
		end_elev.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				Integer x = (Integer)end_elev.getSelectedItem();
				fillEndAzi(x);
			}
		});
		//----
		start_elev.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				Integer x = (Integer)start_elev.getSelectedItem();
				fillStartAzi(x);
			}
		});
		//----
		
		
		btn_outputPath.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				initFileChooser("save");
				int x = fileChooser.showOpenDialog(MyPanel01.this);
				if(x == JFileChooser.APPROVE_OPTION){
					File file = fileChooser.getSelectedFile();
					tf_outputPath.setText(file.getAbsolutePath());
				}
				
			}
		});
		
		//----
		
		btn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				initFileChooser("open");
				int x = fileChooser.showOpenDialog(MyPanel01.this);
				if(x == JFileChooser.APPROVE_OPTION){
					
					File file = fileChooser.getSelectedFile();
					textField.setText(file.getAbsolutePath());
					
					tf_outputPath.setText(file.getParent());
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
					txArea.insert(MyUtils.getLogMeg("定位"+file.getAbsolutePath()), 0);
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
	
	
	private void fillStartAzi(int x){
		
		if(null != start_azi ){
			start_azi.removeAllItems();
			if(x<=50){
				for(int i = 0;i<=360;i+=5){
					start_azi.addItem(i);
				}
			}
			if(x==60){
				for(int i = 0;i<=360;i+=10){
					start_azi.addItem(i);
				}
			}
			if(x==70){
				for(int i = 0;i<=360;i+=15){
					start_azi.addItem(i);
				}
			}
			if(x==80){
				for(int i = 0;i<=360;i+=30){
					start_azi.addItem(i);
				}
			}
			if(x==90){
				for(int i = 0;i<=360;i+=360){
					start_azi.addItem(i);
				}
			}
		}
	}
	private void fillEndAzi(int x){
		if(null != end_azi ){
			end_azi.removeAllItems();
			if(x<=50){
				for(int i = 0;i<=360;i+=5){
					end_azi.addItem(i);
				}
			}
			if(x==60){
				for(int i = 0;i<=360;i+=10){
					end_azi.addItem(i);
				}
			}
			if(x==70){
				for(int i = 0;i<=360;i+=15){
					end_azi.addItem(i);
				}
			}
			if(x==80){
				for(int i = 0;i<=360;i+=30){
					end_azi.addItem(i);
				}
			}
			if(x==90){
				for(int i = 0;i<=360;i+=360){
					end_azi.addItem(i);
				}
			}
		}
	}
	
	public static void main(String args[]){
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(new MyPanel01());
		f.pack();
		f.setVisible(true);
	}
	

}
