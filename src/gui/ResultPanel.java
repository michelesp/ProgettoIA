package gui;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import java.awt.Component;
import javax.swing.Box;

public class ResultPanel extends JPanel {
	
	public ResultPanel(boolean sepsi, String text) {
		setLayout(new BorderLayout(0, 0));

		JPanel bottomPanel = new JPanel();
		add(bottomPanel, BorderLayout.SOUTH);

		JLabel sepsiLabel = new JLabel("Sepsi:\t");
		bottomPanel.add(sepsiLabel);

		JLabel sepsiResult = new JLabel(sepsi?"true":"false");
		bottomPanel.add(sepsiResult);

		JTextPane textPane = new JTextPane();
		textPane.setContentType("text/html");
		textPane.setText(text);
		textPane.setEditable(false);
		add(textPane, BorderLayout.CENTER);

		Component horizontalGlue = Box.createHorizontalGlue();
		add(horizontalGlue, BorderLayout.WEST);

		Component horizontalGlue_1 = Box.createHorizontalGlue();
		add(horizontalGlue_1, BorderLayout.EAST);

		Component verticalGlue = Box.createVerticalGlue();
		add(verticalGlue, BorderLayout.NORTH);
	}

}
