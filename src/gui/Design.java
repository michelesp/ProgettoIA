<<<<<<< HEAD
package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import core.Core;


public class Design implements Upgradable {

	private static final int SIZE_W = 450;
	private static final int SIZE_H = 450;

	private Core core;

	private JFrame frame;
	private List<JLabel> label;
	static boolean TRANSLATED;
	private boolean loaded;
	private JProgressBar progressBar;
	private JPanel panel;


	/**
	 * Create the application.
	 */
	public Design(Core core) {
		this.core=core;
		initialize();
		frame.setVisible(true);
		//frame.setResizable(false);
		frame.setSize(SIZE_W, SIZE_H);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, SIZE_W, SIZE_H);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		panel = new JPanel();

		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.getContentPane().add(progressBar, BorderLayout.SOUTH);

		loaded = true;
		label = new ArrayList<>();

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("./"));
		JMenuItem mntmCaricaCartella = new JMenuItem("Carica Cartella...");
		mntmCaricaCartella.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!loaded) {
					JOptionPane.showMessageDialog(frame, "Caricamento in corso..", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				int returnVal = fileChooser.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file =fileChooser.getSelectedFile();
					ImageIcon loading = new ImageIcon("./loader.gif");
					JLabel label2 = new JLabel("loading... ", loading, JLabel.CENTER);
					progressBar.setVisible(true);
					while(panel.getComponentCount()>0)
						panel.remove(0);
					panel.add(Box.createVerticalStrut(SIZE_H*2/3), BorderLayout.NORTH);
					panel.add(label2, BorderLayout.CENTER);
					panel.invalidate();
					frame.pack();
					frame.setSize(SIZE_W, SIZE_H);
					frame.repaint();
					System.out.println("File: " + file.getAbsolutePath() + ".");  
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								loaded = false;
								core.execute(file.getAbsolutePath());
								System.out.println("caricamento completo");
								label2.setVisible(false);
								diagnose();
							} catch (Exception e) {
								e.printStackTrace();
								panel.removeAll();
								panel.add(Box.createVerticalStrut(SIZE_H*2/3), BorderLayout.NORTH);
								panel.add(new JLabel("Per cominciare selezionare una cartella clinica"), BorderLayout.CENTER);
								frame.pack();
								frame.setSize(SIZE_W, SIZE_H);
								frame.repaint();
								JOptionPane.showMessageDialog(frame, "File non supportato", "Error", JOptionPane.ERROR_MESSAGE);
							} finally {
								loaded = true;
							}
						}
					}).start();
				}
			}
		});


		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);

		mnNewMenu.add(mntmCaricaCartella);
		
		panel.add(Box.createVerticalStrut(SIZE_H*2/3), BorderLayout.NORTH);
		panel.add(new JLabel("Per cominciare selezionare una cartella clinica"), BorderLayout.CENTER);
		frame.invalidate();
		frame.repaint();
		frame.pack();
		frame.setSize(SIZE_W, SIZE_H);
	}

	private void diagnose() {
		progressBar.setVisible(false);
		while(panel.getComponentCount()>0)
			panel.remove(0);
		frame.invalidate();
		frame.repaint();
		frame.pack();
		frame.setSize(SIZE_W, SIZE_H);
		panel.add(new ResultPanel(core.diagnosticsSepsi(), core.getDiagnosis()));
		panel.invalidate();
		frame.invalidate();
		frame.repaint();
		frame.pack();
		frame.setSize(SIZE_W, SIZE_H);
	}

	@Override
	public void upgradeProgress(int n) {
		progressBar.setValue(n);
		progressBar.invalidate();
		progressBar.repaint();
		frame.invalidate();
		frame.setSize(SIZE_W, SIZE_H);
	}


}
=======
package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import core.Core;


public class Design implements Upgradable {

	private static final int SIZE_W = 450;
	private static final int SIZE_H = 450;

	private Core core;

	private JFrame frame;
	private List<JLabel> label;
	static boolean TRANSLATED;
	private boolean loaded;
	private JProgressBar progressBar;
	private JPanel panel;


	/**
	 * Create the application.
	 */
	public Design(Core core) {
		this.core=core;
		initialize();
		frame.setVisible(true);
		//frame.setResizable(false);
		frame.setSize(SIZE_W, SIZE_H);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, SIZE_W, SIZE_H);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		panel = new JPanel();

		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.getContentPane().add(progressBar, BorderLayout.SOUTH);

		loaded = true;
		label = new ArrayList<>();

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("./"));
		JMenuItem mntmCaricaCartella = new JMenuItem("Carica Cartella...");
		mntmCaricaCartella.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!loaded) {
					JOptionPane.showMessageDialog(frame, "Caricamento in corso..", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				int returnVal = fileChooser.showOpenDialog(frame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file =fileChooser.getSelectedFile();
					ImageIcon loading = new ImageIcon("./loader.gif");
					JLabel label2 = new JLabel("loading... ", loading, JLabel.CENTER);
					progressBar.setVisible(true);
					while(panel.getComponentCount()>0)
						panel.remove(0);
					panel.add(Box.createVerticalStrut(SIZE_H*2/3), BorderLayout.NORTH);
					panel.add(label2, BorderLayout.CENTER);
					panel.invalidate();
					frame.pack();
					frame.setSize(SIZE_W, SIZE_H);
					frame.repaint();
					System.out.println("File: " + file.getAbsolutePath() + ".");  
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								loaded = false;
								core.execute(file.getAbsolutePath());
								System.out.println("caricamento completo");
								label2.setVisible(false);
								diagnose();
							} catch (Exception e) {
								e.printStackTrace();
								panel.removeAll();
								panel.add(Box.createVerticalStrut(SIZE_H*2/3), BorderLayout.NORTH);
								panel.add(new JLabel("Per cominciare selezionare una cartella clinica"), BorderLayout.CENTER);
								frame.pack();
								frame.setSize(SIZE_W, SIZE_H);
								frame.repaint();
								JOptionPane.showMessageDialog(frame, "File non supportato", "Error", JOptionPane.ERROR_MESSAGE);
							} finally {
								loaded = true;
							}
						}
					}).start();
				}
			}
		});


		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);

		mnNewMenu.add(mntmCaricaCartella);
		
		panel.add(Box.createVerticalStrut(SIZE_H*2/3), BorderLayout.NORTH);
		panel.add(new JLabel("Per cominciare selezionare una cartella clinica"), BorderLayout.CENTER);
		frame.invalidate();
		frame.repaint();
		frame.pack();
		frame.setSize(SIZE_W, SIZE_H);
	}

	private void diagnose() {
		progressBar.setVisible(false);
		while(panel.getComponentCount()>0)
			panel.remove(0);
		frame.invalidate();
		frame.repaint();
		frame.pack();
		frame.setSize(SIZE_W, SIZE_H);
		panel.add(new ResultPanel(core.diagnosticsSepsi(), core.getDiagnosis()));
		panel.invalidate();
		frame.invalidate();
		frame.repaint();
		frame.pack();
		frame.setSize(SIZE_W, SIZE_H);
	}

	@Override
	public void upgradeProgress(int n) {
		progressBar.setValue(n);
		progressBar.invalidate();
		progressBar.repaint();
		frame.invalidate();
		frame.setSize(SIZE_W, SIZE_H);
	}


}
>>>>>>> refs/remotes/origin/master1
