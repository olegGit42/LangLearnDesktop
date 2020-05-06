package com.app.colibri.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.app.colibri.service.MainLocaleManager;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 6583031504249305339L;

	@Autowired
	@Qualifier("mainFrameListener")
	private WindowListener windowListener;

	public MainFrame() {
		super();
	}

	public MainFrame(String title) {
		super(title);
	}

	public void init() {
		MainLocaleManager.addTrackedItem(this, "app_name");
		final String imgPath = "images/colibri_icon.png";
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getClassLoader().getResource(imgPath)));

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.pack();
		this.setSize(new Dimension(850, 730));
		this.setLocationRelativeTo(null);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.addWindowListener(windowListener);
		this.setVisible(true);
	}

	public void setUpScrollPane(JScrollPane upScrollPane) {
		this.add(upScrollPane, BorderLayout.NORTH);
	}

	public void setDownScrollPane(JScrollPane downScrollPane) {
		this.add(downScrollPane, BorderLayout.CENTER);
	}

}
