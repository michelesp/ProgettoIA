package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
	private static final int SIZE_H = 200;

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
		frame.setBounds(100, 100, 450, 250);
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
								loaded = true;
								label2.setVisible(false);
								diagnose();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			}
		});


		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);

		mnNewMenu.add(mntmCaricaCartella);

		panel.add(new JLabel("Per cominciare selezionare una cartella clinica"), BorderLayout.CENTER);
	}

	private void diagnose() {
		progressBar.setVisible(false);
		while(panel.getComponentCount()>0)
			panel.remove(0);
		frame.invalidate();
		frame.repaint();
		frame.pack();
		frame.setSize(SIZE_W, SIZE_H);
		/*panel.setLayout(new GridLayout(3, 1));
		label.clear();
		panel.add(new JLabel("Sepsi: "+core.diagnosticsSepsi()));
		panel.add(new JLabel("Bradicarda: "+core.diagnosticsBradicardia()));
		panel.add(new JLabel("Tachicardia: "+core.diagnosticsTachicardia()));
		*/
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
