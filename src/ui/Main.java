package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

public class Main {

	private JFrame frame ;
	private JPanel panel;
	private JTabbedPane tabPane ;
	private JMenuBar menuBar;
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main m = new Main();
		m.init();

	}
	
	
	private void init(){
		frame = new JFrame("audio Toy");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		panel = new JPanel(new BorderLayout());
//------------------------------------------------------------菜单
		menuBar = new JMenuBar();
		JMenu fileMenu = menuBar.add(new JMenu("File"));
		JMenuItem exitMenuItem = fileMenu.add(new JMenuItem("Exit"));
		exitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menuBar.add(fileMenu);
		
		JMenu optionMenu = menuBar.add(new JMenu("Option"));
		JMenuItem aboutMenuItem = optionMenu.add(new JMenuItem("About"));
		aboutMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "about info");
			}
		});
		menuBar.add(optionMenu);
		
		panel.add(menuBar,BorderLayout.NORTH);
//-------------------------------------------------------------	tab导航	
		
		
		tabPane = new JTabbedPane();
		panel.add(tabPane,BorderLayout.CENTER);
		
//------------------------------------------------------------- 第一个tab
		JPanel p = new JPanel(new BorderLayout());
		EmptyBorder eb = new EmptyBorder(5, 5, 5, 5);
		BevelBorder bb = new BevelBorder(BevelBorder.LOWERED);
		CompoundBorder cb = new CompoundBorder(eb, bb);
		p.setBorder(new CompoundBorder(cb,new EmptyBorder(3, 3, 3, 3)));
		tabPane.add("Transformator",p);
		
		MyPanel01 mypanel01 = new MyPanel01();
		p.add(mypanel01);
//-------------------------------------------------------------	第二个tab
		JPanel p2 = new JPanel(new BorderLayout());
		p2.setBorder(new CompoundBorder(cb,new EmptyBorder(3, 3, 3, 3)));
		tabPane.add("Adjustment",p2);
		
		MyPanel02 mypanel02 = new MyPanel02();
		p2.add(mypanel02);
//-------------------------------------------------------------		
		frame.addWindowStateListener(new WindowStateListener() {
			
			@Override
			public void windowStateChanged(WindowEvent e) {
				frame.pack();
				
			}
		});
//-------------------------------------------------		
		frame.getContentPane().add(panel);
		frame.pack();
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(d.width/2 - frame.getWidth()/2 , d.height/2 - frame.getHeight()/2);
		frame.setVisible(true);
	}

}
