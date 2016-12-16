package com.cic.datacrawl.ui.panel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.cic.datacrawl.core.entity.DefaultEntity;

public class OutputPanelDemo {

	private static int index = 0;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setLayout(new BorderLayout());

		JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		JButton button = new JButton("Add");
		JButton button2 = new JButton("Add...");
		JButton button1 = new JButton("Add Tab");
		// mainPanel.add(button1, BorderLayout.NORTH);
		// JPanel p = new JPanel();

		JPanel buttonPanel = new JPanel();
		// mainPanel.add(p, BorderLayout.CENTER);
		// mainPanel.add(button, BorderLayout.SOUTH);
		final OutputPanel panel = new OutputPanel();
		// p.setLayout(new BorderLayout());

		final String[] titleName = new String[] { "Entity_1" };
		button1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				titleName[0] = JOptionPane.showInputDialog(panel, "Please input tab name");

			}
		});

		button2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {

				String countStr = JOptionPane.showInputDialog(panel, "Please Input add count:");
				long count = 0;
				try {
					count = Long.parseLong(countStr);
				} catch (Exception e) {
				}
				if (count > 0) {
					ArrayList<DefaultEntity> entityList = new ArrayList<DefaultEntity>();
					for (int i = 0; i < count; ++i) {
						index++;
						DefaultEntity entity = new DefaultEntity(titleName[0]);
						entity.set("column_String", "String_value_" + index);
						entity.set("column_number", index);
						entity.set("column_double", Math.random());
						entity.set("column_boolean", index % 2 == 0);
						entity.set("column_date",
									new Timestamp(System.currentTimeMillis() + (index * 100000)));
						entityList.add(entity);
					}

					DefaultEntity[] entities = new DefaultEntity[entityList.size()];
					entityList.toArray(entities);
					panel.addItem(entities);
				}
			}
		});

		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionevent) {
				DefaultEntity entity = new DefaultEntity(titleName[0]);
				index++;
				entity.set("column_String", "String_value_" + index);
				entity.set("column_number", index);
				entity.set("column_double", Math.random());
				entity.set("column_boolean", index % 2 == 0);
				entity.set("column_date", new Timestamp(System.currentTimeMillis() + (index * 100000)));

				panel.addItem(entity);
			}
		});

		buttonPanel.add(button1);
		buttonPanel.add(button);
		buttonPanel.add(button2);

//		mainPanel.add(buttonPanel, BorderLayout.NORTH);
//		mainPanel.add(panel, BorderLayout.CENTER);

		splitPanel.add(panel);
		splitPanel.add(buttonPanel);
		

//		splitPanel.add(mainPanel);
//		splitPanel.add(new JTextArea());
		splitPanel.setOneTouchExpandable(true);
		frame.add(splitPanel);
		frame.setSize(800, 600);
		splitPanel.setDividerLocation(0.3);
		frame.setVisible(true);

	}
}
