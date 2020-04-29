package com.app.colibri.view;

import static com.app.colibri.controller.WordController.getBoxInfo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.app.colibri.controller.AppRun;
import com.app.colibri.controller.GUIController;
import com.app.colibri.controller.NotificationSound;
import com.app.colibri.controller.WordController;
import com.app.colibri.model.Box;
import com.app.colibri.model.Word;
import com.app.colibri.model.tablemodel.AllWordsTableModel;
import com.app.colibri.model.tablemodel.BoxesTableModel;
import com.app.colibri.model.tablemodel.RepeateTableModel;
import com.app.colibri.view.buttons.RefreshButton;
import com.app.colibri.view.chart.BadRememberWordChartFactory;
import com.app.colibri.view.listeners.TextFieldClipboardMouseAdapter;
import com.app.colibri.view.panels.BoxScrollPane;
import com.app.colibri.view.panels.EditPanel;

public class GUI {

	public static final List<Word> repeatedWordList = new ArrayList<Word>();

	private JButton bAdd;
	private JTextField newWord;
	private JTextField translate;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GUI() {

		// __________________________________________________________________________________________________________________________________
		// --------------------------------------------------[Upper panel]

		JPanel inputWordPanel = new JPanel();

		JPanel inputWordWithSave = new JPanel(new BorderLayout());
		inputWordWithSave.add(inputWordPanel, BorderLayout.CENTER);

		JPanel iWt = new JPanel(new BorderLayout(0, 2));
		inputWordPanel.add(iWt, BorderLayout.WEST);

		JLabel newWordL = new JLabel("New word", SwingConstants.RIGHT);
		iWt.add(newWordL, BorderLayout.NORTH);

		JLabel translateL = new JLabel("Translate", SwingConstants.RIGHT);
		iWt.add(translateL, BorderLayout.SOUTH);

		JPanel iWl = new JPanel(new BorderLayout());
		inputWordPanel.add(iWl);

		newWord = new JTextField(40);
		iWl.add(newWord, BorderLayout.NORTH);
		newWord.addMouseListener(new TextFieldClipboardMouseAdapter(newWord));
		newWord.addCaretListener(listener -> bAddButtonStateChange());

		translate = new JTextField(40);
		iWl.add(translate, BorderLayout.SOUTH);
		translate.addMouseListener(new TextFieldClipboardMouseAdapter(translate));
		translate.addCaretListener(listener -> {
			translate.setToolTipText(translate.getText().equals("") ? null : translate.getText());
			bAddButtonStateChange();
		});

		JPanel iWr = new JPanel();
		inputWordPanel.add(iWr);

		bAdd = new JButton();
		bAdd.setPreferredSize(new Dimension(60, 40));
		bAdd.setToolTipText("Show repetition time");
		bAdd.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("time32.png")));
		iWr.add(bAdd);
		bAdd.addActionListener(e -> {
			String newWordStr = newWord.getText().trim();
			String translateStr = translate.getText().trim();

			if (!newWordStr.equals("") && !translateStr.equals("")) {
				int check = 0;

				for (int i = 0; i < WordController.allWordsList.size(); i++) {
					if (newWordStr.toLowerCase().equals(WordController.allWordsList.get(i).getWord().toLowerCase())) {
						check++;
					}
				}

				if (check == 0) {
					try {
						WordController.createNewWordGUI(newWordStr, translateStr);
						WordController.serializeAllWordsMain();
						// JOptionPane.showMessageDialog(null, "Successfully ADDED", "Info",
						// JOptionPane.INFORMATION_MESSAGE);
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(null, "ADDING error!", "Error", JOptionPane.ERROR_MESSAGE);
					}

					translate.setText("");
					newWord.setText("");
				} else {
					newWord.setText(newWord.getText().trim());
					translate.setText(translate.getText().trim());
					JOptionPane.showMessageDialog(null, "This word has already been added!", "Error", JOptionPane.ERROR_MESSAGE);
				}

			} else {
				newWord.setText(newWord.getText().trim());
				translate.setText(translate.getText().trim());

				String recurenceTime;
				if (WordController.minRepeatTime == Long.MAX_VALUE) {
					recurenceTime = "You have no words to repeat";
				} else {
					recurenceTime = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(WordController.minRepeatTime));
				}

				JOptionPane.showMessageDialog(null, recurenceTime, "Repetition time", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		// __________________________________________________________________________________________________________________________________
		// --------------------------------------------------[Lower panel]

		JPanel mainPanel = new JPanel();

		// --------------------------------------------------[Panels_begin]

		// --------------------------------------------------[1]

		JPanel repeat = new JPanel(new BorderLayout());

		// ----------[repeate_begin]

		TableModel modelRepeate = new RepeateTableModel(repeatedWordList);
		JTable tableRepeate = new JTable(modelRepeate);
		tableRepeate.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableRepeate.setRowSorter(new TableRowSorter(modelRepeate));
		tableRepeate.setAutoCreateRowSorter(true);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		tableRepeate.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		tableRepeate.getColumnModel().getColumn(0).setMaxWidth(50);

		tableRepeate.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

		tableRepeate.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		tableRepeate.getColumnModel().getColumn(2).setMaxWidth(60);

		tableRepeate.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
		tableRepeate.getColumnModel().getColumn(3).setMaxWidth(60);

		JScrollPane scrollPaneTableRepeate = new JScrollPane(tableRepeate);
		repeat.add(scrollPaneTableRepeate, BorderLayout.CENTER);

		JButton refreshRepeate = new JButton("Refresh");
		repeat.add(refreshRepeate, BorderLayout.NORTH);

		Timer timer = new Timer(3 * 1000, (e -> {
			if (repeatedWordList.isEmpty()) {
				if (System.currentTimeMillis() >= WordController.minRepeatTime) {
					bAdd.setBackground(Color.GREEN);
					inputWordPanel.setBackground(Color.GREEN);
					// TODO why doesn't produce sound -> Toolkit.getDefaultToolkit().beep();
					NotificationSound.beep();
				}
			}
		}));
		timer.start();

		JPanel southRepeatePanel = new JPanel();
		repeat.add(southRepeatePanel, BorderLayout.SOUTH);

		JPanel labelsRrepeateLeft = new JPanel();
		labelsRrepeateLeft.setLayout(new BoxLayout(labelsRrepeateLeft, BoxLayout.Y_AXIS));
		southRepeatePanel.add(labelsRrepeateLeft);

		JLabel wordRep = new JLabel("Word");
		wordRep.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		labelsRrepeateLeft.add(getVerticalPadForBoxLayout(3));
		labelsRrepeateLeft.add(wordRep);
		labelsRrepeateLeft.add(getVerticalPadForBoxLayout(5));

		JLabel translateRep = new JLabel("Translate");
		translateRep.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		labelsRrepeateLeft.add(translateRep);
		labelsRrepeateLeft.add(getVerticalPadForBoxLayout(2));

		JLabel boxRep = new JLabel("Box");
		boxRep.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		labelsRrepeateLeft.add(boxRep);
		labelsRrepeateLeft.add(getVerticalPadForBoxLayout(4));

		JLabel newBoxRep = new JLabel("New box");
		newBoxRep.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		labelsRrepeateLeft.add(newBoxRep);
		labelsRrepeateLeft.add(getVerticalPadForBoxLayout(6));

		JPanel labelsRrepeateCenter = new JPanel();
		labelsRrepeateCenter.setLayout(new BoxLayout(labelsRrepeateCenter, BoxLayout.Y_AXIS));
		southRepeatePanel.add(labelsRrepeateCenter);

		JTextField wordRepText = new JTextField(40);
		wordRepText.setEditable(false);
		wordRepText.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		labelsRrepeateCenter.add(wordRepText);
		labelsRrepeateCenter.add(getVerticalPadForBoxLayout(1));

		wordRepText.addMouseListener(new TextFieldClipboardMouseAdapter(wordRepText));

		JTextField translateRepText = new JTextField(40);
		translateRepText.setEditable(false);
		translateRepText.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		labelsRrepeateCenter.add(translateRepText);

		JLabel boxValueRep = new JLabel(" ");
		boxValueRep.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		labelsRrepeateCenter.add(boxValueRep);

		JComboBox<String> newBoxText = new JComboBox<String>(WordController.boxPeriod);
		newBoxText.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		labelsRrepeateCenter.add(newBoxText);
		newBoxText.setSelectedIndex(-1);
		newBoxText.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					newBoxText.setSelectedIndex(0);
				}
			}
		});

		JPanel labelsRepeateRight = new JPanel();
		labelsRepeateRight.setLayout(new BoxLayout(labelsRepeateRight, BoxLayout.Y_AXIS));
		southRepeatePanel.add(labelsRepeateRight);

		JPanel pShowRepeatButtons = new JPanel();
		pShowRepeatButtons.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));

		JButton bShowRep = new JButton();
		bShowRep.setToolTipText("Show translation");
		bShowRep.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("eye25.png")));
		// bShowRep.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		bShowRep.setPreferredSize(new Dimension(61, 20));
		labelsRepeateRight.add(getVerticalPadForBoxLayout(20));
		pShowRepeatButtons.add(bShowRep);
		labelsRepeateRight.add(pShowRepeatButtons);
		labelsRepeateRight.add(getVerticalPadForBoxLayout(6));
		bShowRep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (!translateRepText.getText().equals("")) {
					translateRepText.setText(null);
					translateRepText.setToolTipText(null);
					boxValueRep.setText(" ");
					newBoxText.setSelectedIndex(-1);
					return;
				}

				if (!wordRepText.getText().equals("")) {
					String translate = "";
					for (int i = 0; i < WordController.allWordsList.size(); i++) {

						if (wordRepText.getText().trim().toLowerCase()
								.equals(WordController.allWordsList.get(i).getWord().toLowerCase())) {

							wordRepText.setText(WordController.allWordsList.get(i).getWord());

							if (translate.equals("")) {
								translate = WordController.allWordsList.get(i).getTranslate();
							} else {
								translate = translate + "| [copy]: " + WordController.allWordsList.get(i).getTranslate();
							}
							translateRepText.setText(translate);
							translateRepText.setToolTipText(translate);
							boxValueRep.setText(" " + getBoxInfo(WordController.allWordsList.get(i)));
							if (WordController.allWordsList.get(i).getBox() + 1 < newBoxText.getItemCount()) {
								newBoxText.setSelectedIndex(WordController.allWordsList.get(i).getBox() + 1);
							} else if (WordController.allWordsList.get(i).getBox() == 7) {
								newBoxText.setSelectedIndex(7);
							} else {
								newBoxText.setSelectedIndex(-1);
							}
						}
					}
				}
			}
		});

		refreshRepeate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bAdd.setBackground(null);
				inputWordPanel.setBackground(null);
				GUIController.repeateWords(repeatedWordList);
				tableRepeate.repaint();
				tableRepeate.revalidate();
				try {
					tableRepeate.getSelectionModel().clearSelection();
					tableRepeate.changeSelection(0, 1, false, false);
					wordRepText.setText(String.valueOf(tableRepeate.getModel().getValueAt(0, 1)));
				} catch (Exception ex) {
				}
			}
		});

		refreshRepeate.doClick();

		JPanel pSaveRepeatButtons = new JPanel();
		pSaveRepeatButtons.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
		labelsRepeateRight.add(pSaveRepeatButtons);

		JButton bSaveRep = new JButton();
		bSaveRep.setToolTipText("Save new box");
		bSaveRep.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("save21.png")));
		// bSaveRep.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		bSaveRep.setPreferredSize(new Dimension(61, 25));
		pSaveRepeatButtons.add(bSaveRep);
		bSaveRep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (0 <= newBoxText.getSelectedIndex() && newBoxText.getSelectedIndex() <= 7
						&& !translateRepText.getText().trim().equals("")) {
					for (Word word : WordController.allWordsList) {
						if (wordRepText.getText().trim().toLowerCase().equals(word.getWord().toLowerCase())) {
							word.setNewBoxAndUpdDate(newBoxText.getSelectedIndex());
							break;
						}
					}

					wordRepText.setText(null);
					translateRepText.setText(null);
					translateRepText.setToolTipText(null);
					boxValueRep.setText(" ");
					newBoxText.setSelectedIndex(-1);

					refreshRepeate.doClick();
					WordController.serializeAllWordsMain();
					GUIController.updMinRepeatTime();
				}
			}
		});

		JPanel pForgotButton = new JPanel();
		pForgotButton.setLayout(new BoxLayout(pForgotButton, BoxLayout.Y_AXIS));
		pForgotButton.add(getVerticalPadForBoxLayout(20));

		JButton bSaveZeroRep = new JButton();
		bSaveZeroRep.setToolTipText("Forgot");
		bSaveZeroRep.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("forgot51.png")));
		bSaveZeroRep.setPreferredSize(new Dimension(61, 61));
		pForgotButton.add(bSaveZeroRep);
		southRepeatePanel.add(pForgotButton);
		bSaveZeroRep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!translateRepText.getText().trim().equals("")) {
					newBoxText.setSelectedIndex(0);
					bSaveRep.doClick();
				}
			}
		});

		ListSelectionModel selModel = tableRepeate.getSelectionModel();

		selModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int[] selectedRows = tableRepeate.getSelectedRows();
				try {
					int selIndex = tableRepeate.getRowSorter().convertRowIndexToModel(selectedRows[0]);
					TableModel model = tableRepeate.getModel();
					wordRepText.setText(String.valueOf(model.getValueAt(selIndex, 1)));
					translateRepText.setText(null);
					boxValueRep.setText(" ");
					newBoxText.setSelectedIndex(-1);
				} catch (Exception ex) {
				}
			}
		});

		// ----------[repeate_end]

		JPanel boxesMain = new JPanel(new BorderLayout());

		// ----------[boxes_begin]

		JPanel boxes = new JPanel(new FlowLayout(JLabel.CENTER, 0, 0));

		boxesMain.add(boxes, BorderLayout.CENTER);

		// B0
		BoxScrollPane scrollPaneTableB0 = new BoxScrollPane(new BoxesTableModel(Box.getBox(0), "Box 0"));
		boxes.add(scrollPaneTableB0);

		// B1
		BoxScrollPane scrollPaneTableB1 = new BoxScrollPane(new BoxesTableModel(Box.getBox(1), "Box 1"));
		boxes.add(scrollPaneTableB1);

		// B2
		BoxScrollPane scrollPaneTableB2 = new BoxScrollPane(new BoxesTableModel(Box.getBox(2), "Box 2"));
		boxes.add(scrollPaneTableB2);

		// B3
		BoxScrollPane scrollPaneTableB3 = new BoxScrollPane(new BoxesTableModel(Box.getBox(3), "Box 3"));
		boxes.add(scrollPaneTableB3);

		// B4
		BoxScrollPane scrollPaneTableB4 = new BoxScrollPane(new BoxesTableModel(Box.getBox(4), "Box 4"));
		boxes.add(scrollPaneTableB4);

		// B5
		BoxScrollPane scrollPaneTableB5 = new BoxScrollPane(new BoxesTableModel(Box.getBox(5), "Box 5"));
		boxes.add(scrollPaneTableB5);

		// B6
		BoxScrollPane scrollPaneTableB6 = new BoxScrollPane(new BoxesTableModel(Box.getBox(6), "Box 6"));
		boxes.add(scrollPaneTableB6);

		// Refresh
		List<JTable> tableBoxesList = new ArrayList<>();
		tableBoxesList.add(scrollPaneTableB0.getTable());
		tableBoxesList.add(scrollPaneTableB1.getTable());
		tableBoxesList.add(scrollPaneTableB2.getTable());
		tableBoxesList.add(scrollPaneTableB3.getTable());
		tableBoxesList.add(scrollPaneTableB4.getTable());
		tableBoxesList.add(scrollPaneTableB5.getTable());
		tableBoxesList.add(scrollPaneTableB6.getTable());

		JButton refreshBoxes = new RefreshButton(Box::refreshBox, tableBoxesList);
		boxesMain.add(refreshBoxes, BorderLayout.NORTH);

		// ----------[boxes_end]

		JPanel allWords = new JPanel(new BorderLayout());

		// ----------[allWords_begin]

		TableModel modelAllWords = new AllWordsTableModel(WordController.allWordsList);
		JTable tableAllWords = new JTable(modelAllWords);
		tableAllWords.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableAllWords.setAutoCreateRowSorter(true);

		JScrollPane scrollPaneTableAllWords = new JScrollPane(tableAllWords);
		allWords.add(scrollPaneTableAllWords, BorderLayout.CENTER);

		tableAllWords.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		tableAllWords.getColumnModel().getColumn(0).setMaxWidth(30);

		tableAllWords.getColumnModel().getColumn(1).setMaxWidth(100);
		tableAllWords.getColumnModel().getColumn(1).setMinWidth(100);

		tableAllWords.getColumnModel().getColumn(2).setMinWidth(200);
		tableAllWords.getColumnModel().getColumn(2).setMaxWidth(200);

		tableAllWords.getColumnModel().getColumn(4).setMaxWidth(100);
		tableAllWords.getColumnModel().getColumn(4).setMinWidth(100);

		tableAllWords.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
		tableAllWords.getColumnModel().getColumn(5).setMaxWidth(30);
		tableAllWords.getColumnModel().getColumn(5).setMinWidth(30);

		tableAllWords.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
		tableAllWords.getColumnModel().getColumn(6).setMaxWidth(50);
		tableAllWords.getColumnModel().getColumn(6).setMinWidth(50);

		JButton refreshAllWords = new RefreshButton(tableAllWords);
		allWords.add(refreshAllWords, BorderLayout.NORTH);

		// ----------[allWords_end]

		// --------------------------------------------------[Panels_end]

		JTabbedPane tabbedPane = new JTabbedPane();
		mainPanel.add(tabbedPane);
		tabbedPane.setPreferredSize(new Dimension(800, 600));
		tabbedPane.add("Repeat", repeat);
		tabbedPane.add("Bad remembered words", BadRememberWordChartFactory.chartPanelWithButton);
		tabbedPane.add("Boxes", boxesMain);
		tabbedPane.add("All words", allWords);
		tabbedPane.add("Editor", new EditPanel());

		MainFrame mainFrame = AppRun.appContext.getBean("mainFrame", MainFrame.class);
		mainFrame.setUpScrollPane(new JScrollPane(inputWordWithSave));
		mainFrame.setDownScrollPane(new JScrollPane(mainPanel));
	}

	public Component getVerticalPadForBoxLayout(final int pad) {
		return javax.swing.Box.createRigidArea(new Dimension(0, pad));
	}

	private void bAddButtonStateChange() {
		if (newWord.getText().trim().equals("") || translate.getText().trim().equals("")) {
			bAdd.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("time32.png")));
			bAdd.setToolTipText("Show repetition time");
		} else {
			bAdd.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("add32.png")));
			bAdd.setToolTipText("Add new word");
		}
	}

}
