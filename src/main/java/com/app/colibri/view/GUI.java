package com.app.colibri.view;

import static com.app.colibri.controller.WordController.getBoxInfo;
import static com.app.colibri.service.AppSettings.getLocaledItem;
import static com.app.colibri.service.MainLocaleManager.addTrackedItem;
import static com.app.colibri.view.util.ViewUtil.askCode;
import static com.app.colibri.view.util.ViewUtil.msgBase;
import static com.app.colibri.view.util.ViewUtil.msgErrorCode;
import static com.app.colibri.view.util.ViewUtil.msgInfoCode;
import static com.app.colibri.view.util.ViewUtil.msgWarningCode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.app.colibri.controller.GUIController;
import com.app.colibri.controller.RESTController;
import com.app.colibri.controller.WordController;
import com.app.colibri.model.Word;
import com.app.colibri.model.tablemodel.AllWordsTableModel;
import com.app.colibri.model.tablemodel.RepeateTableModel;
import com.app.colibri.registry.UserDataRegistry;
import com.app.colibri.service.AppRun;
import com.app.colibri.service.AppSettings;
import com.app.colibri.service.MainLocaleManager;
import com.app.colibri.service.NotificationSound;
import com.app.colibri.view.buttons.RefreshButton;
import com.app.colibri.view.chart.BadRememberWordChartFactory;
import com.app.colibri.view.listeners.TextFieldClipboardMouseAdapter;
import com.app.colibri.view.panels.EditPanel;

public class GUI {

	public static final List<Word> repeatedWordList = new ArrayList<Word>();
	public static final Map<String, String> localeSwitchMap;
	public static MainFrame mainFrame = null;
	public static LoginFrame loginFrame = null;

	private JButton bAdd;
	private JTextField newWord;
	private JTextField translate;

	static {
		localeSwitchMap = new HashMap<>();
		localeSwitchMap.put("EN", "RU");
		localeSwitchMap.put("RU", "EN");
	}

	public GUI() {

		// __________________________________________________________________________________________________________________________________
		// --------------------------------------------------[Upper panel]

		// --------[Lang button begin]

		JPanel pnlSwitchLang = new JPanel();
		JButton btnSwithLang = new JButton(WordController.userDataRegistry.getAppLocale());
		addTrackedItem(btnSwithLang, "EN");
		btnSwithLang.setMargin(new Insets(2, 2, 2, 2));
		btnSwithLang.setPreferredSize(new Dimension(34, 25));
		pnlSwitchLang.add(btnSwithLang);
		btnSwithLang.addActionListener(e -> {
			String lang = localeSwitchMap.get(btnSwithLang.getText());
			btnSwithLang.setText(lang);
			AppSettings.changeLocale(lang);
		});

		// --------[Lang button end]

		// --------[Change user button begin]

		final boolean isWebButtonVisible = (AppSettings.appSettings.getUser().getId() != 0);

		JButton bSendToWeb = new JButton();
		addTrackedItem(bSendToWeb, null, "bSendToWebHint");
		bSendToWeb.setMargin(new Insets(2, 2, 2, 2));
		bSendToWeb.setPreferredSize(new Dimension(34, 34));
		bSendToWeb.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/upload25.png")));
		bSendToWeb.setVisible(isWebButtonVisible);
		bSendToWeb.addActionListener(e -> {

			if (!GUIController.canWordAction()) {
				return;
			}

			if (askCode("ask_send_to_web")) {
				if (RESTController.sendUserData()) {
					msgInfoCode("web_send_ok");
				} else {
					msgWarningCode("relogin_web");
				}
			}

		});

		JButton bLoadFromWeb = new JButton();
		addTrackedItem(bLoadFromWeb, null, "bLoadFromWebHint");
		bLoadFromWeb.setMargin(new Insets(2, 2, 2, 2));
		bLoadFromWeb.setPreferredSize(new Dimension(34, 34));
		bLoadFromWeb.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/download25.png")));
		bLoadFromWeb.setVisible(isWebButtonVisible);
		bLoadFromWeb.addActionListener(e -> {

			if (!GUIController.canWordAction()) {
				return;
			}

			if (askCode("ask_load_from_web")) {
				UserDataRegistry webUserDataRegistry = RESTController.getUserData();

				if (webUserDataRegistry != null) {
					WordController.serializeUserDataRegistryWithBackup();
					AppSettings.reloadMainFrame(webUserDataRegistry);
					msgInfoCode("web_load_ok");
					if (GUI.mainFrame != null) {
						GUI.mainFrame.toFront();
					}
				} else {
					msgWarningCode("relogin_web");
				}
			}

		});

		JButton btnChangeUser = new JButton();
		addTrackedItem(btnChangeUser, null, "Change user");
		btnChangeUser.setMargin(new Insets(2, 2, 2, 2));
		btnChangeUser.setPreferredSize(new Dimension(34, 34));
		btnChangeUser.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/change_user30.png")));
		btnChangeUser.addActionListener(e -> {
			LoginFrame.launch(LoginFrame.State.LOGIN);
		});

		JPanel pnlChangeUser = new JPanel();
		pnlChangeUser.add(bSendToWeb);
		pnlChangeUser.add(bLoadFromWeb);
		pnlChangeUser.add(btnChangeUser);

		// --------[Change user button end]

		JPanel inputWordPanel = new JPanel();

		JPanel inputWordWithSave = new JPanel(new BorderLayout());
		inputWordWithSave.add(inputWordPanel, BorderLayout.CENTER);
		inputWordWithSave.add(pnlSwitchLang, BorderLayout.WEST);
		inputWordWithSave.add(pnlChangeUser, BorderLayout.EAST);

		JPanel iWt = new JPanel(new BorderLayout(0, 2));
		inputWordPanel.add(iWt, BorderLayout.WEST);

		JLabel newWordL = new JLabel("New word", SwingConstants.RIGHT);
		addTrackedItem(newWordL, "New word");
		iWt.add(newWordL, BorderLayout.NORTH);

		JLabel translateL = new JLabel("Translate", SwingConstants.RIGHT);
		addTrackedItem(translateL, "Translate");
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
		addTrackedItem(bAdd, null, "bAddTooltipTime");
		bAdd.setToolTipText(getLocaledItem("bAddTooltipTime"));
		bAdd.setPreferredSize(new Dimension(60, 40));
		bAdd.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/time32.png")));
		iWr.add(bAdd);
		bAdd.addActionListener(e -> {
			String newWordStr = newWord.getText().trim();
			String translateStr = translate.getText().trim();

			if (!newWordStr.equals("") && !translateStr.equals("")) {

				if (!GUIController.canWordAction()) {
					return;
				}

				int check = 0;

				for (int i = 0; i < WordController.allWordsList.size(); i++) {
					if (newWordStr.toLowerCase().equals(WordController.allWordsList.get(i).getWord().toLowerCase())) {
						check++;
					}
				}

				if (check == 0) {
					try {
						WordController.createNewWordGUI(newWordStr, translateStr);
						WordController.serializeUserDataRegistry();
					} catch (Exception ex) {
						msgErrorCode("add_error");
					}

					translate.setText("");
					newWord.setText("");
				} else {
					newWord.setText(newWordStr);
					translate.setText(translateStr);
					msgErrorCode("already_added");
				}

			} else {
				newWord.setText(newWordStr);
				translate.setText(translateStr);

				String recurenceTime;
				if (WordController.minRepeatTime == Long.MAX_VALUE) {
					recurenceTime = getLocaledItem("no_repeat_word");
				} else {
					recurenceTime = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(new Date(WordController.minRepeatTime));
				}

				msgBase(recurenceTime, "Repetition time", JOptionPane.INFORMATION_MESSAGE);
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
		addTrackedItem(tableRepeate, null, "Word", "Period", "Box");
		tableRepeate.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// tableRepeate.setRowSorter(new TableRowSorter(modelRepeate));
		// tableRepeate.setAutoCreateRowSorter(true);

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
		addTrackedItem(refreshRepeate, "Refresh");
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
		addTrackedItem(wordRep, "Word");
		wordRep.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		labelsRrepeateLeft.add(getVerticalPadForBoxLayout(3));
		labelsRrepeateLeft.add(wordRep);
		labelsRrepeateLeft.add(getVerticalPadForBoxLayout(5));

		JLabel translateRep = new JLabel("Translate");
		addTrackedItem(translateRep, "Translate");
		translateRep.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		labelsRrepeateLeft.add(translateRep);
		labelsRrepeateLeft.add(getVerticalPadForBoxLayout(2));

		JLabel boxRep = new JLabel("Box");
		addTrackedItem(boxRep, "Box");
		boxRep.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
		labelsRrepeateLeft.add(boxRep);
		labelsRrepeateLeft.add(getVerticalPadForBoxLayout(4));

		JLabel newBoxRep = new JLabel("New box");
		addTrackedItem(newBoxRep, "New box");
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
		addTrackedItem(bShowRep, null, "Show translation");
		bShowRep.setToolTipText("Show translation");
		bShowRep.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/eye25.png")));
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
							} else if (WordController.allWordsList.get(i).getBox() == newBoxText.getItemCount() - 1) {
								newBoxText.setSelectedIndex(newBoxText.getItemCount() - 1);
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
				GUIController.updMinRepeatTime();
			}
		});

		refreshRepeate.doClick();

		JPanel pSaveRepeatButtons = new JPanel();
		pSaveRepeatButtons.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
		labelsRepeateRight.add(pSaveRepeatButtons);

		JButton bSaveRep = new JButton();
		addTrackedItem(bSaveRep, null, "Save new box");
		bSaveRep.setToolTipText("Save new box");
		bSaveRep.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/save21.png")));
		// bSaveRep.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		bSaveRep.setPreferredSize(new Dimension(61, 25));
		pSaveRepeatButtons.add(bSaveRep);
		bSaveRep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (0 <= newBoxText.getSelectedIndex() && newBoxText.getSelectedIndex() <= newBoxText.getItemCount() - 1
						&& !translateRepText.getText().trim().equals("")) {

					if (!GUIController.canWordAction()) {
						return;
					}

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
					WordController.serializeUserDataRegistry();
					GUIController.updMinRepeatTime();
				}
			}
		});

		JPanel pForgotButton = new JPanel();
		pForgotButton.setLayout(new BoxLayout(pForgotButton, BoxLayout.Y_AXIS));
		pForgotButton.add(getVerticalPadForBoxLayout(20));

		JButton bSaveZeroRep = new JButton();
		addTrackedItem(bSaveZeroRep, null, "Forgot");
		bSaveZeroRep.setToolTipText("Forgot");
		bSaveZeroRep.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/forgot51.png")));
		bSaveZeroRep.setPreferredSize(new Dimension(61, 61));
		pForgotButton.add(bSaveZeroRep);
		southRepeatePanel.add(pForgotButton);
		bSaveZeroRep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!translateRepText.getText().trim().equals("")) {

					if (!GUIController.canWordAction()) {
						return;
					}

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
					int selIndex = selectedRows[0]; // tableRepeate.getRowSorter().convertRowIndexToModel(selectedRows[0]);
					TableModel model = tableRepeate.getModel();
					wordRepText.setText(String.valueOf(model.getValueAt(selIndex, 1)));
					translateRepText.setText(null);
					boxValueRep.setText(" ");

					newBoxText.removeAllItems();
					for (int i = 0; i < WordController.repeatPeriodArray.length; i++) {
						newBoxText.addItem(WordController.getBoxInfo(i));
					}

					newBoxText.setSelectedIndex(-1);
				} catch (Exception ex) {
				}
			}
		});

		// ----------[repeate_end]

		// JPanel boxesMain = new JPanel(new BorderLayout());

		// ----------[boxes_begin]

		/*
		 * JPanel boxes = new JPanel(new FlowLayout(JLabel.CENTER, 0, 0));
		 * 
		 * boxesMain.add(boxes, BorderLayout.CENTER);
		 * 
		 * // B0 BoxScrollPane scrollPaneTableB0 = new BoxScrollPane(new
		 * BoxesTableModel(Box.getBox(0), "Box 0")); boxes.add(scrollPaneTableB0);
		 * 
		 * // B1 BoxScrollPane scrollPaneTableB1 = new BoxScrollPane(new
		 * BoxesTableModel(Box.getBox(1), "Box 1")); boxes.add(scrollPaneTableB1);
		 * 
		 * // B2 BoxScrollPane scrollPaneTableB2 = new BoxScrollPane(new
		 * BoxesTableModel(Box.getBox(2), "Box 2")); boxes.add(scrollPaneTableB2);
		 * 
		 * // B3 BoxScrollPane scrollPaneTableB3 = new BoxScrollPane(new
		 * BoxesTableModel(Box.getBox(3), "Box 3")); boxes.add(scrollPaneTableB3);
		 * 
		 * // B4 BoxScrollPane scrollPaneTableB4 = new BoxScrollPane(new
		 * BoxesTableModel(Box.getBox(4), "Box 4")); boxes.add(scrollPaneTableB4);
		 * 
		 * // B5 BoxScrollPane scrollPaneTableB5 = new BoxScrollPane(new
		 * BoxesTableModel(Box.getBox(5), "Box 5")); boxes.add(scrollPaneTableB5);
		 * 
		 * // B6 BoxScrollPane scrollPaneTableB6 = new BoxScrollPane(new
		 * BoxesTableModel(Box.getBox(6), "Box 6")); boxes.add(scrollPaneTableB6);
		 * 
		 * // Refresh List<JTable> tableBoxesList = new ArrayList<>();
		 * tableBoxesList.add(scrollPaneTableB0.getTable());
		 * tableBoxesList.add(scrollPaneTableB1.getTable());
		 * tableBoxesList.add(scrollPaneTableB2.getTable());
		 * tableBoxesList.add(scrollPaneTableB3.getTable());
		 * tableBoxesList.add(scrollPaneTableB4.getTable());
		 * tableBoxesList.add(scrollPaneTableB5.getTable());
		 * tableBoxesList.add(scrollPaneTableB6.getTable());
		 * 
		 * for (int i = 0; i < tableBoxesList.size(); i++) {
		 * addTrackedItem(tableBoxesList.get(i), "Box " + i); }
		 * 
		 * JButton refreshBoxes = new RefreshButton(Box::refreshBox, tableBoxesList);
		 * boxesMain.add(refreshBoxes, BorderLayout.NORTH);
		 */

		// ----------[boxes_end]

		JPanel allWords = new JPanel(new BorderLayout());

		// ----------[allWords_begin]

		TableModel modelAllWords = new AllWordsTableModel(WordController.allWordsList);
		JTable tableAllWords = new JTable(modelAllWords);
		addTrackedItem(tableAllWords, null, "Creation date", "Word", "Translate", "Repeat date", "Box", "Period");
		tableAllWords.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableAllWords.setAutoCreateRowSorter(true);

		JScrollPane scrollPaneTableAllWords = new JScrollPane(tableAllWords);
		allWords.add(scrollPaneTableAllWords, BorderLayout.CENTER);

		tableAllWords.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		tableAllWords.getColumnModel().getColumn(0).setMaxWidth(40);

		tableAllWords.getColumnModel().getColumn(1).setMaxWidth(100);
		tableAllWords.getColumnModel().getColumn(1).setMinWidth(100);

		tableAllWords.getColumnModel().getColumn(2).setMinWidth(200);
		tableAllWords.getColumnModel().getColumn(2).setMaxWidth(200);

		tableAllWords.getColumnModel().getColumn(4).setMaxWidth(100);
		tableAllWords.getColumnModel().getColumn(4).setMinWidth(100);

		tableAllWords.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
		tableAllWords.getColumnModel().getColumn(5).setMaxWidth(45);
		tableAllWords.getColumnModel().getColumn(5).setMinWidth(45);

		tableAllWords.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
		tableAllWords.getColumnModel().getColumn(6).setMaxWidth(60);
		tableAllWords.getColumnModel().getColumn(6).setMinWidth(60);

		JButton refreshAllWords = new RefreshButton(tableAllWords);
		allWords.add(refreshAllWords, BorderLayout.NORTH);

		// ----------[allWords_end]

		// --------------------------------------------------[Panels_end]

		JTabbedPane tabbedPane = new JTabbedPane();
		addTrackedItem(tabbedPane, "Repeat", "Bad remembered words", /* "Boxes", */ "All words", "Editor");
		mainPanel.add(tabbedPane);
		tabbedPane.setPreferredSize(new Dimension(800, 600));
		tabbedPane.add("Repeat", repeat);
		tabbedPane.add("Bad remembered words", BadRememberWordChartFactory.chartPanelWithButton);
		// tabbedPane.add("Boxes", boxesMain);
		tabbedPane.add("All words", allWords);
		tabbedPane.add("Editor", new EditPanel());

		BadRememberWordChartFactory.updateDataset();

		mainFrame = AppRun.appContext.getBean("mainFrame", MainFrame.class);
		mainFrame.setUpScrollPane(new JScrollPane(inputWordWithSave));
		mainFrame.setDownScrollPane(new JScrollPane(mainPanel));

		MainLocaleManager.changeLocaleStatic();

		if (loginFrame != null) {
			mainFrame.setEnabled(false);
			loginFrame.getFrame().toFront();
		}

	}

	public Component getVerticalPadForBoxLayout(final int pad) {
		return javax.swing.Box.createRigidArea(new Dimension(0, pad));
	}

	private void bAddButtonStateChange() {
		if (newWord.getText().trim().equals("") || translate.getText().trim().equals("")) {
			bAdd.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/time32.png")));
			bAdd.setToolTipText(getLocaledItem("bAddTooltipTime"));
			addTrackedItem(bAdd, null, "bAddTooltipTime");
		} else {
			bAdd.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/add32.png")));
			bAdd.setToolTipText(getLocaledItem("bAddTooltipAdd"));
			addTrackedItem(bAdd, null, "bAddTooltipAdd");
		}
	}

}
