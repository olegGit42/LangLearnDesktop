package com.app.colibri.view.panels;

import static com.app.colibri.controller.WordController.getBoxInfo;
import static com.app.colibri.service.AppSettings.getLocaledItem;
import static com.app.colibri.service.MainLocaleManager.addTrackedItem;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import com.app.colibri.controller.GUIController;
import com.app.colibri.controller.WordController;
import com.app.colibri.model.Word;
import com.app.colibri.model.tablemodel.SearchTableModel;
import com.app.colibri.view.listeners.TextFieldClipboardMouseAdapter;

@SuppressWarnings("serial")
public class EditPanel extends JPanel {
	public static final int[] wordsCountArray = new int[8];
	public static int wordsCount;

	public static int getWordsCount() {
		return wordsCount;
	}

	private JTable table;
	private JTextField tfFindWord;
	private JTextField tfWord;
	private JTextField tfTranslate;
	private JTextField tfEditWord;
	private JTextField tfEditTranslate;

	private Word editedWord = null;
	private List<Word> searchList = new ArrayList<>();
	private SortedSet<String> actualTagSet = new TreeSet<>();
	private SortedSet<String> editedTagSet = new TreeSet<>();
	private boolean isLockTagPopup = true;

	private EditState editState = EditState.READ;
	private JTextField tfCreationDate;
	private JTextField tfRepeateDate;
	private JTextField tfBox;
	private JTextField tfRepeateCount;
	private JTextField tfID;
	private JToggleButton tbWordTranslate;
	private JLabel lblCountB0;
	private JLabel lblCountB1;
	private JLabel lblCountB2;
	private JLabel lblCountB3;
	private JLabel lblCountB4;
	private JLabel lblCountB5;
	private JLabel lblCountB6;
	private JLabel lblCountB7;
	private JLabel lblCountAll;
	@SuppressWarnings("rawtypes")
	private JComboBox comboBox;
	private JCheckBox cbRepTimeOrder;
	@SuppressWarnings("rawtypes")
	private JComboBox comboBoxEditTag;
	@SuppressWarnings("rawtypes")
	private JComboBox comboBoxTag;
	private JTextField tfFindTag;
	@SuppressWarnings("rawtypes")
	private JComboBox cmbFindTag;
	private JTextField tfTag;
	private JTextField tfNewTag;

	/**
	 * Create the panel.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public EditPanel() {
		setPreferredSize(new Dimension(800, 600));
		setLayout(new BorderLayout(0, 0));

		JPanel panelCenter = new JPanel();
		add(panelCenter, BorderLayout.CENTER);
		panelCenter.setLayout(null);

		JLabel lblWord = new JLabel("Word");
		addTrackedItem(lblWord, "Word");
		lblWord.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblWord.setHorizontalAlignment(SwingConstants.RIGHT);
		lblWord.setBounds(24, 35, 50, 14);
		panelCenter.add(lblWord);

		JLabel lblTranslate = new JLabel("Translate");
		addTrackedItem(lblTranslate, "Translate");
		lblTranslate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTranslate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblTranslate.setBounds(24, 60, 50, 14);
		panelCenter.add(lblTranslate);

		tfWord = new JTextField();
		tfWord.setEditable(false);
		tfWord.setBounds(84, 33, 390, 20);
		panelCenter.add(tfWord);
		tfWord.setColumns(10);

		tfTranslate = new JTextField();
		tfTranslate.setEditable(false);
		tfTranslate.setColumns(10);
		tfTranslate.setBounds(84, 58, 390, 20);
		panelCenter.add(tfTranslate);

		JButton btnEdit = new JButton("Edit");
		addTrackedItem(btnEdit, "Edit");
		btnEdit.setMargin(new Insets(2, 2, 2, 2));
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editState = EditState.EDIT;
				if (isDefinedWord()) {
					if (tfEditWord.getText().trim().equals(tfWord.getText())
							&& tfEditTranslate.getText().trim().equals(tfTranslate.getText())
							&& tfBox.getText().equals(String.valueOf(comboBox.getSelectedItem()))
							&& actualTagSet.equals(editedTagSet)) {
						JOptionPane.showMessageDialog(null, getLocaledItem("No changes"), getLocaledItem("Info"),
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					for (Word word : WordController.allWordsList) {
						if (tfEditWord.getText().trim().toLowerCase().equals(word.getWord().toLowerCase())
								&& word != editedWord) {
							JOptionPane.showMessageDialog(null, getLocaledItem("already_added"), getLocaledItem("Error"),
									JOptionPane.ERROR_MESSAGE);
							return;
						}
					}

					final int answer = JOptionPane.showConfirmDialog(null, getLocaledItem("ask_edit"), getLocaledItem("Question"),
							JOptionPane.YES_NO_OPTION);

					if (answer == JOptionPane.YES_OPTION) {
						editedWord.setWord(tfEditWord.getText().trim());
						editedWord.setTranslate(tfEditTranslate.getText().trim());
						if (!tfBox.getText().equals(String.valueOf(comboBox.getSelectedItem()))) {
							editedWord.setNewBoxAndUpdDate(comboBox.getSelectedIndex());
							GUIController.updMinRepeatTime();
						}
						if (!actualTagSet.equals(editedTagSet)) {
							editedWord.putNewTagSet(editedTagSet);
						}
						WordController.serializeAllWordsMain();

						int selectedRowIndex = table.getSelectedRows()[0];
						table.getSelectionModel().clearSelection();
						table.changeSelection(selectedRowIndex, 0, false, false);
					}
				}
			}
		});
		btnEdit.setBounds(397, 190, 77, 23);
		panelCenter.add(btnEdit);

		JButton btnDelete = new JButton("Delete");
		addTrackedItem(btnDelete, "Delete");
		btnDelete.setMargin(new Insets(2, 2, 2, 2));
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editState = EditState.DELETE;
				if (isDefinedWord()) {
					final int answer = JOptionPane.showConfirmDialog(null, getLocaledItem("ask_del"), getLocaledItem("Question"),
							JOptionPane.YES_NO_OPTION);

					if (answer == JOptionPane.YES_OPTION) {
						WordController.allWordsList.remove(editedWord);
						searchWords();
						GUIController.updMinRepeatTime();
						WordController.serializeAllWordsMain();
					}
				}
			}
		});
		btnDelete.setPreferredSize(new Dimension(51, 23));
		btnDelete.setBounds(397, 5, 77, 23);
		panelCenter.add(btnDelete);

		tfEditWord = new JTextField();
		tfEditWord.setColumns(10);
		tfEditWord.setBounds(84, 217, 390, 20);
		panelCenter.add(tfEditWord);

		tfEditWord.addMouseListener(new TextFieldClipboardMouseAdapter(tfEditWord));

		JLabel lblEditWord = new JLabel("Word");
		addTrackedItem(lblEditWord, "Word");
		lblEditWord.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEditWord.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblEditWord.setBounds(24, 219, 50, 14);
		panelCenter.add(lblEditWord);

		JLabel lblEditTranslate = new JLabel("Translate");
		addTrackedItem(lblEditTranslate, "Translate");
		lblEditTranslate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEditTranslate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblEditTranslate.setBounds(24, 244, 50, 14);
		panelCenter.add(lblEditTranslate);

		tfEditTranslate = new JTextField();
		tfEditTranslate.setColumns(10);
		tfEditTranslate.setBounds(84, 242, 390, 20);
		panelCenter.add(tfEditTranslate);

		tfEditTranslate.addMouseListener(new TextFieldClipboardMouseAdapter(tfEditTranslate));

		JLabel lblActual = new JLabel("Actual");
		addTrackedItem(lblActual, "Actual");
		lblActual.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblActual.setHorizontalAlignment(SwingConstants.CENTER);
		lblActual.setBounds(211, 9, 100, 14);
		panelCenter.add(lblActual);

		JLabel lblEdit = new JLabel("Edited");
		addTrackedItem(lblEdit, "Edited");
		lblEdit.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblEdit.setHorizontalAlignment(SwingConstants.CENTER);
		lblEdit.setBounds(211, 194, 100, 14);
		panelCenter.add(lblEdit);

		JLabel lblCreationDate = new JLabel("Creation date");
		addTrackedItem(lblCreationDate, "Creation date");
		lblCreationDate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCreationDate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCreationDate.setBounds(24, 90, 137, 14);
		panelCenter.add(lblCreationDate);

		tfCreationDate = new JTextField();
		tfCreationDate.setEditable(false);
		tfCreationDate.setColumns(10);
		tfCreationDate.setBounds(164, 87, 105, 20);
		panelCenter.add(tfCreationDate);

		JLabel lblRepeateDate = new JLabel("Repeat date");
		addTrackedItem(lblRepeateDate, "Repeat date");
		lblRepeateDate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRepeateDate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblRepeateDate.setBounds(84, 114, 77, 14);
		panelCenter.add(lblRepeateDate);

		tfRepeateDate = new JTextField();
		tfRepeateDate.setEditable(false);
		tfRepeateDate.setColumns(10);
		tfRepeateDate.setBounds(164, 111, 105, 20);
		panelCenter.add(tfRepeateDate);

		JLabel lblBox = new JLabel("Box");
		addTrackedItem(lblBox, "Box");
		lblBox.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblBox.setBounds(315, 114, 77, 14);
		panelCenter.add(lblBox);

		tfBox = new JTextField();
		tfBox.setEditable(false);
		tfBox.setColumns(10);
		tfBox.setBounds(396, 111, 78, 20);
		panelCenter.add(tfBox);

		JLabel lblRepeateCount = new JLabel("Repeat count");
		addTrackedItem(lblRepeateCount, "Repeat count");
		lblRepeateCount.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRepeateCount.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblRepeateCount.setBounds(297, 90, 95, 14);
		panelCenter.add(lblRepeateCount);

		tfRepeateCount = new JTextField();
		tfRepeateCount.setEditable(false);
		tfRepeateCount.setColumns(10);
		tfRepeateCount.setBounds(396, 87, 78, 20);
		panelCenter.add(tfRepeateCount);

		JLabel lblID = new JLabel("ID");
		lblID.setHorizontalAlignment(SwingConstants.RIGHT);
		lblID.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblID.setBounds(24, 10, 50, 14);
		panelCenter.add(lblID);

		tfID = new JTextField();
		tfID.setEditable(false);
		tfID.setColumns(10);
		tfID.setBounds(84, 8, 50, 20);
		panelCenter.add(tfID);

		JPanel pnlWordsSummary = new JPanel();
		pnlWordsSummary.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		pnlWordsSummary.setBounds(24, 340, 450, 60);
		panelCenter.add(pnlWordsSummary);
		pnlWordsSummary.setLayout(null);

		JLabel lblBoxCount = new JLabel("Box");
		addTrackedItem(lblBoxCount, "Box");
		lblBoxCount.setBounds(0, 12, 50, 14);
		pnlWordsSummary.add(lblBoxCount);
		lblBoxCount.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBoxCount.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JLabel lblCount = new JLabel("Count");
		addTrackedItem(lblCount, "Count");
		lblCount.setBounds(0, 31, 50, 14);
		pnlWordsSummary.add(lblCount);
		lblCount.setHorizontalAlignment(SwingConstants.RIGHT);
		lblCount.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JLabel lbl0 = new JLabel("0");
		lbl0.setBounds(60, 12, 40, 14);
		pnlWordsSummary.add(lbl0);
		lbl0.setHorizontalAlignment(SwingConstants.CENTER);
		lbl0.setFont(new Font("Tahoma", Font.PLAIN, 12));

		lblCountB0 = new JLabel("0");
		lblCountB0.setBounds(60, 32, 40, 14);
		pnlWordsSummary.add(lblCountB0);
		lblCountB0.setHorizontalAlignment(SwingConstants.CENTER);
		lblCountB0.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JLabel lbl3 = new JLabel("3");
		lbl3.setBounds(186, 12, 40, 14);
		pnlWordsSummary.add(lbl3);
		lbl3.setHorizontalAlignment(SwingConstants.CENTER);
		lbl3.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JLabel lbl2 = new JLabel("2");
		lbl2.setBounds(144, 12, 40, 14);
		pnlWordsSummary.add(lbl2);
		lbl2.setHorizontalAlignment(SwingConstants.CENTER);
		lbl2.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JLabel lbl1 = new JLabel("1");
		lbl1.setBounds(101, 12, 40, 14);
		pnlWordsSummary.add(lbl1);
		lbl1.setHorizontalAlignment(SwingConstants.CENTER);
		lbl1.setFont(new Font("Tahoma", Font.PLAIN, 12));

		lblCountB1 = new JLabel("0");
		lblCountB1.setBounds(101, 32, 40, 14);
		pnlWordsSummary.add(lblCountB1);
		lblCountB1.setHorizontalAlignment(SwingConstants.CENTER);
		lblCountB1.setFont(new Font("Tahoma", Font.PLAIN, 12));

		lblCountB2 = new JLabel("0");
		lblCountB2.setBounds(144, 32, 40, 14);
		pnlWordsSummary.add(lblCountB2);
		lblCountB2.setHorizontalAlignment(SwingConstants.CENTER);
		lblCountB2.setFont(new Font("Tahoma", Font.PLAIN, 12));

		lblCountB3 = new JLabel("0");
		lblCountB3.setBounds(186, 32, 40, 14);
		pnlWordsSummary.add(lblCountB3);
		lblCountB3.setHorizontalAlignment(SwingConstants.CENTER);
		lblCountB3.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JLabel lbl4 = new JLabel("4");
		lbl4.setBounds(227, 12, 40, 14);
		pnlWordsSummary.add(lbl4);
		lbl4.setHorizontalAlignment(SwingConstants.CENTER);
		lbl4.setFont(new Font("Tahoma", Font.PLAIN, 12));

		lblCountB4 = new JLabel("0");
		lblCountB4.setBounds(227, 32, 40, 14);
		pnlWordsSummary.add(lblCountB4);
		lblCountB4.setHorizontalAlignment(SwingConstants.CENTER);
		lblCountB4.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JLabel lblArchive = new JLabel("Archive");
		addTrackedItem(lblArchive, "period_box_7");
		lblArchive.setBounds(353, 12, 40, 14);
		pnlWordsSummary.add(lblArchive);
		lblArchive.setHorizontalAlignment(SwingConstants.CENTER);
		lblArchive.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JLabel lbl6 = new JLabel("6");
		lbl6.setBounds(311, 12, 40, 14);
		pnlWordsSummary.add(lbl6);
		lbl6.setHorizontalAlignment(SwingConstants.CENTER);
		lbl6.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JLabel lbl5 = new JLabel("5");
		lbl5.setBounds(268, 12, 40, 14);
		pnlWordsSummary.add(lbl5);
		lbl5.setHorizontalAlignment(SwingConstants.CENTER);
		lbl5.setFont(new Font("Tahoma", Font.PLAIN, 12));

		lblCountB5 = new JLabel("0");
		lblCountB5.setBounds(268, 32, 40, 14);
		pnlWordsSummary.add(lblCountB5);
		lblCountB5.setHorizontalAlignment(SwingConstants.CENTER);
		lblCountB5.setFont(new Font("Tahoma", Font.PLAIN, 12));

		lblCountB6 = new JLabel("0");
		lblCountB6.setBounds(311, 32, 40, 14);
		pnlWordsSummary.add(lblCountB6);
		lblCountB6.setHorizontalAlignment(SwingConstants.CENTER);
		lblCountB6.setFont(new Font("Tahoma", Font.PLAIN, 12));

		lblCountB7 = new JLabel("0");
		lblCountB7.setBounds(353, 32, 40, 14);
		pnlWordsSummary.add(lblCountB7);
		lblCountB7.setHorizontalAlignment(SwingConstants.CENTER);
		lblCountB7.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JLabel lblAll = new JLabel("All");
		addTrackedItem(lblAll, "All");
		lblAll.setBounds(400, 12, 40, 14);
		pnlWordsSummary.add(lblAll);
		lblAll.setHorizontalAlignment(SwingConstants.CENTER);
		lblAll.setFont(new Font("Tahoma", Font.PLAIN, 12));

		lblCountAll = new JLabel("0");
		lblCountAll.setBounds(400, 32, 40, 14);
		pnlWordsSummary.add(lblCountAll);
		lblCountAll.setHorizontalAlignment(SwingConstants.CENTER);
		lblCountAll.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JLabel lblEditBox = new JLabel("Box");
		addTrackedItem(lblEditBox, "Box");
		lblEditBox.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEditBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblEditBox.setBounds(24, 270, 50, 14);
		panelCenter.add(lblEditBox);

		comboBox = new JComboBox(WordController.boxPeriod);
		comboBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					comboBox.setSelectedIndex(0);
				}
			}
		});
		comboBox.setBounds(84, 265, 95, 25);
		panelCenter.add(comboBox);
		comboBox.setSelectedIndex(-1);

		JButton btnBoxPlus = new JButton("");
		btnBoxPlus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				incrementBoxSelection(1);
			}
		});
		btnBoxPlus.setMargin(new Insets(2, 12, 2, 14));
		btnBoxPlus.setIconTextGap(0);
		btnBoxPlus.setBounds(190, 265, 25, 25);
		panelCenter.add(btnBoxPlus);
		btnBoxPlus.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/plus21.png")));

		JButton btnBoxMinus = new JButton("");
		btnBoxMinus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				incrementBoxSelection(-1);
			}
		});
		btnBoxMinus.setMargin(new Insets(2, 12, 2, 14));
		btnBoxMinus.setIconTextGap(0);
		btnBoxMinus.setBounds(220, 265, 25, 25);
		panelCenter.add(btnBoxMinus);
		btnBoxMinus.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/minus21.png")));

		comboBoxTag = new JComboBox();
		comboBoxTag.setBounds(164, 135, 105, 25);
		panelCenter.add(comboBoxTag);

		JLabel lblTags = new JLabel("Tags");
		addTrackedItem(lblTags, "Tags");
		lblTags.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTags.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblTags.setBounds(84, 140, 77, 14);
		panelCenter.add(lblTags);

		comboBoxEditTag = new JComboBox();
		comboBoxEditTag.getEditor().getEditorComponent().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					String clip = GUIController.getFromClipboard();
					if (!clip.equals("")) {
						comboBoxEditTag.setSelectedIndex(-1);
						comboBoxEditTag.setSelectedItem(GUIController.getFromClipboard());
					}
				} else if (e.getButton() == MouseEvent.BUTTON2) {
					comboBoxEditTag.setSelectedIndex(-1);
				}
			}
		});
		comboBoxEditTag.setEditable(true);
		comboBoxEditTag.getEditor().getEditorComponent().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				showTagComboBoxSelectedItem();
			}
		});
		comboBoxEditTag.setBounds(303, 265, 105, 25);
		panelCenter.add(comboBoxEditTag);

		JLabel lblEditTag = new JLabel("Tags");
		addTrackedItem(lblEditTag, "Tags");
		lblEditTag.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEditTag.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblEditTag.setBounds(245, 270, 50, 14);
		panelCenter.add(lblEditTag);

		JButton bAddTag = new JButton("");
		bAddTag.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					isLockTagPopup = false;
					bAddTag.grabFocus();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				comboBoxEditTag.grabFocus();
			}
		});
		bAddTag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String v_newTag = comboBoxEditTag.getSelectedItem() == null ? ""
						: ((String) comboBoxEditTag.getSelectedItem()).trim().toUpperCase();

				if (!(editedTagSet.contains(v_newTag) || v_newTag.equals(""))) {
					editedTagSet.add(v_newTag);
					comboBoxEditTag.addItem(v_newTag);
				}
				comboBoxEditTag.setSelectedItem(v_newTag);
				comboBoxEditTag.showPopup();
			}
		});
		bAddTag.setMargin(new Insets(2, 12, 2, 14));
		bAddTag.setIconTextGap(0);
		bAddTag.setBounds(419, 265, 25, 25);
		panelCenter.add(bAddTag);
		bAddTag.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/plus21.png")));

		JButton bRemoveTag = new JButton("");
		bRemoveTag.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					String v_tag = comboBoxEditTag.getSelectedItem() == null ? ""
							: ((String) comboBoxEditTag.getSelectedItem()).trim().toUpperCase();

					editedTagSet.remove(v_tag);
					comboBoxEditTag.removeItem(v_tag);

				} else if (e.getButton() == MouseEvent.BUTTON3) {
					final int answer = JOptionPane.showConfirmDialog(null, getLocaledItem("ask_rem_wrd_tags"),
							getLocaledItem("Question"), JOptionPane.YES_NO_OPTION);

					if (answer == JOptionPane.YES_OPTION) {
						editedTagSet.clear();
						comboBoxEditTag.removeAllItems();
					}
				}
			}
		});
		bRemoveTag.setMargin(new Insets(2, 12, 2, 14));
		bRemoveTag.setIconTextGap(0);
		bRemoveTag.setBounds(449, 265, 25, 25);
		panelCenter.add(bRemoveTag);
		bRemoveTag.setIcon(new ImageIcon(this.getClass().getClassLoader().getResource("images/minus21.png")));

		JLabel lblTag = new JLabel("Tag");
		addTrackedItem(lblTag, "Tag");
		lblTag.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTag.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblTag.setBounds(24, 418, 50, 14);
		panelCenter.add(lblTag);

		tfTag = new JTextField();
		tfTag.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					String clip = GUIController.getFromClipboard();
					if (!clip.equals("")) {
						tfTag.setText(clip);
					}
				} else if (e.getButton() == MouseEvent.BUTTON2) {
					tfTag.setText("");
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					tfTag.setText((String) cmbFindTag.getSelectedItem());
				}
			}
		});
		tfTag.setEditable(false);
		tfTag.setColumns(10);
		tfTag.setBounds(84, 416, 95, 20);
		panelCenter.add(tfTag);

		JLabel lblNewTag = new JLabel("will replaced by");
		addTrackedItem(lblNewTag, "will_replaced");
		lblNewTag.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewTag.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewTag.setBounds(182, 418, 105, 14);
		panelCenter.add(lblNewTag);

		tfNewTag = new JTextField();
		tfNewTag.addMouseListener(new TextFieldClipboardMouseAdapter(tfNewTag, 2));
		tfNewTag.setColumns(10);
		tfNewTag.setBounds(290, 416, 95, 20);
		panelCenter.add(tfNewTag);

		JButton btnReplaceTag = new JButton("Replace");
		addTrackedItem(btnReplaceTag, "Replace");
		btnReplaceTag.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String v_oldTag = tfTag.getText() == null || tfTag.getText().equals(getLocaledItem("All tags")) ? ""
						: tfTag.getText();
				String v_newTag = tfNewTag.getText() == null ? "" : tfNewTag.getText().toUpperCase().trim();

				if (!WordController.userDataRegistry.getTagRegistry().getTagIdMap().containsKey(v_oldTag)) {
					JOptionPane.showMessageDialog(null,
							v_oldTag.equals("") ? getLocaledItem("choose_tag")
									: getLocaledItem("Tag") + " \"" + v_oldTag + "\" " + getLocaledItem("not found"),
							getLocaledItem("Warning"), JOptionPane.WARNING_MESSAGE);
					return;
				} else if (v_newTag.equals("")) {
					JOptionPane.showMessageDialog(null, getLocaledItem("empty_tag_value"), getLocaledItem("Error"),
							JOptionPane.ERROR_MESSAGE);
					return;
				} else if (WordController.userDataRegistry.getTagRegistry().getTagIdMap().containsKey(v_newTag)) {
					JOptionPane.showMessageDialog(null,
							getLocaledItem("Tag") + " \"" + v_newTag + "\" " + getLocaledItem("already exists"),
							getLocaledItem("Warning"), JOptionPane.WARNING_MESSAGE);
					return;
				}

				final int answer = JOptionPane.showConfirmDialog(null, getLocaledItem("ask_replace_tag"),
						getLocaledItem("Question"), JOptionPane.YES_NO_OPTION);

				if (answer == JOptionPane.YES_OPTION) {
					WordController.userDataRegistry.getTagRegistry().editTag(tfTag.getText(), tfNewTag.getText());
				}
			}
		});
		btnReplaceTag.setMargin(new Insets(2, 7, 2, 7));
		btnReplaceTag.setBounds(397, 412, 77, 26);
		panelCenter.add(btnReplaceTag);

		JButton bRemoveUnusedTags = new JButton("Remove all unused tags");
		addTrackedItem(bRemoveUnusedTags, "rem_unuse_tags");
		bRemoveUnusedTags.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final int answer = JOptionPane.showConfirmDialog(null, getLocaledItem("ask_rem_unuse_tags"),
						getLocaledItem("Question"), JOptionPane.YES_NO_OPTION);

				if (answer == JOptionPane.YES_OPTION) {
					WordController.userDataRegistry.getTagRegistry().removeAllTags();
				}
			}
		});
		bRemoveUnusedTags.setMargin(new Insets(2, 2, 2, 2));
		bRemoveUnusedTags.setBounds(245, 470, 229, 26);
		panelCenter.add(bRemoveUnusedTags);

		JPanel panelWest = new JPanel();
		panelWest.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panelWest.setName("");
		add(panelWest, BorderLayout.WEST);
		panelWest.setLayout(new BorderLayout(0, 0));

		GUIController.searchWords(searchList, null, false, false, getLocaledItem("All tags"));

		TableModel modelAllWords = new SearchTableModel(searchList);
		table = new JTable();
		addTrackedItem(table, "Word", "Translate");
		table.setModel(modelAllWords);
		table.setFont(new Font("Tahoma", Font.PLAIN, 12));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// TODO why bad after DELETION -> table.setAutoCreateRowSorter(true);

		ListSelectionModel selModel = table.getSelectionModel();

		selModel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int[] selectedRows = table.getSelectedRows();
				try {
					int selIndex = selectedRows[0];// table.getRowSorter().convertRowIndexToModel(selectedRows[0]);
					TableModel model = table.getModel();
					setWordInEditArea((Word) model.getValueAt(selIndex, 2));
				} catch (Exception ex) {
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(300, 402));
		panelWest.add(scrollPane, BorderLayout.CENTER);

		JPanel panelFind = new JPanel();
		panelWest.add(panelFind, BorderLayout.NORTH);

		tfFindWord = new JTextField();
		tfFindWord.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					String clip = GUIController.getFromClipboard();
					if (!clip.equals("")) {
						tfFindWord.setText(clip);
					}
					searchWords();
				} else if (e.getButton() == MouseEvent.BUTTON2) {
					if (e.getClickCount() > 1) {
						tfFindWord.setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(System.currentTimeMillis())));
					}
					searchWords();
				}
			}
		});
		tfFindWord.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				searchWords();
			}
		});

		tbWordTranslate = new JToggleButton("W");
		addTrackedItem(tbWordTranslate, "W", "T", "order_by_col_w", "order_by_col_t");
		tbWordTranslate.setToolTipText("order by \"Word\" column");
		tbWordTranslate.setMargin(new Insets(2, 2, 2, 2));
		tbWordTranslate.setPreferredSize(new Dimension(30, 25));
		tbWordTranslate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tbWordTranslate.isSelected()) {
					tbWordTranslate.setText(getLocaledItem("T"));
					tbWordTranslate.setToolTipText(getLocaledItem("order_by_col_t"));
				} else {
					tbWordTranslate.setText(getLocaledItem("W"));
					tbWordTranslate.setToolTipText(getLocaledItem("order_by_col_w"));
				}
				searchWords();
			}
		});

		panelFind.add(tbWordTranslate);
		panelFind.add(tfFindWord);
		tfFindWord.setColumns(17);

		JButton btnClear = new JButton("Clean");
		addTrackedItem(btnClear, "Clean");
		btnClear.setMargin(new Insets(2, 2, 2, 2));
		btnClear.setPreferredSize(new Dimension(60, 25));
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tfFindWord.setText(null);
				searchWords();
			}
		});
		panelFind.add(btnClear);

		JPanel panelFindSouth = new JPanel();
		panelFindSouth.setPreferredSize(new Dimension(10, 67));
		panelWest.add(panelFindSouth, BorderLayout.SOUTH);
		panelFindSouth.setLayout(null);

		cmbFindTag = new JComboBox();
		cmbFindTag.setBounds(144, 4, 150, 25);
		panelFindSouth.add(cmbFindTag);

		tfFindTag = new JTextField();
		tfFindTag.addMouseListener(new TextFieldClipboardMouseAdapter(tfFindTag, 2));
		tfFindTag.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				refreshFindTags(tfFindTag.getText());
			}
		});
		tfFindTag.setColumns(10);
		tfFindTag.setBounds(34, 6, 110, 20);
		panelFindSouth.add(tfFindTag);

		JLabel lblFindTag = new JLabel("Tag");
		addTrackedItem(lblFindTag, "Tag");
		lblFindTag.setHorizontalAlignment(SwingConstants.LEFT);
		lblFindTag.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblFindTag.setBounds(6, 9, 25, 14);
		panelFindSouth.add(lblFindTag);

		cbRepTimeOrder = new JCheckBox("Order by repetition time");
		addTrackedItem(cbRepTimeOrder, "order_rep_time", "order_rep_time", "order_rep_time_hint", "order_rep_time_hint");
		cbRepTimeOrder.setBounds(6, 37, 160, 24);
		panelFindSouth.add(cbRepTimeOrder);

		JButton btnHideTranslateColumn = new JButton("Hide \"Translate\"");
		addTrackedItem(btnHideTranslateColumn, "hide_translate");
		btnHideTranslateColumn.setMargin(new Insets(2, 0, 2, 0));
		btnHideTranslateColumn.setBounds(166, 36, 128, 26);
		panelFindSouth.add(btnHideTranslateColumn);
		btnHideTranslateColumn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (table.getColumnModel().getColumn(1).getWidth() == 0) {
					for (int i = 0; i < 2; i++) {
						table.getColumnModel().getColumn(1).setMinWidth(150);
						table.getColumnModel().getColumn(1).setMaxWidth(150);
						table.getColumnModel().getColumn(1).setWidth(150);
					}

					table.getColumnModel().getColumn(1).setMinWidth(10);
					table.getColumnModel().getColumn(1).setMaxWidth(1000);
					table.getColumnModel().getColumn(1).setPreferredWidth(150);

					if (table.getColumnName(1).startsWith(getLocaledItem("W"))) {
						btnHideTranslateColumn.setText(getLocaledItem("hide_word"));
						addTrackedItem(btnHideTranslateColumn, "hide_word");
					} else {
						btnHideTranslateColumn.setText(getLocaledItem("hide_translate"));
						addTrackedItem(btnHideTranslateColumn, "hide_translate");
					}
				} else {
					table.getColumnModel().getColumn(1).setWidth(0);
					table.getColumnModel().getColumn(1).setMinWidth(0);
					table.getColumnModel().getColumn(1).setMaxWidth(0);

					if (table.getColumnName(1).startsWith(getLocaledItem("W"))) {
						btnHideTranslateColumn.setText(getLocaledItem("show_word"));
						addTrackedItem(btnHideTranslateColumn, "show_word");
					} else {
						btnHideTranslateColumn.setText(getLocaledItem("show_translate"));
						addTrackedItem(btnHideTranslateColumn, "show_translate");
					}
				}
			}
		});
		cbRepTimeOrder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchWords();
			}
		});

		showWordsCount();
		refreshFindTags(null);
	}

	private void setWordInEditArea(Word word) {
		editedWord = word;

		final String wordStr = word == null ? null : word.getWord();
		final String translateStr = word == null ? null : word.getTranslate();
		final String creationDateStr = word == null ? null : WordController.dateFormat.format(new Date(word.getCreationTime()));
		final String repeateDateStr = word == null ? null
				: WordController.dateFormat.format(new Date(word.getRegTime() + WordController.getTimeDelta(word.getBox())));
		final String boxStr = word == null ? null : getBoxInfo(word);
		final String repeateCountStr = word == null ? null : String.valueOf(word.getRepeateIndicator());
		final String idStr = word == null ? null : String.valueOf(word.getId());
		final int box = word == null ? -1 : word.getBox();

		refreshEditorTags(editedWord);
		refreshComboBox();

		tfWord.setText(wordStr);
		tfTranslate.setText(translateStr);
		tfTranslate.setToolTipText(translateStr);
		tfEditWord.setText(wordStr);
		tfEditTranslate.setText(translateStr);
		tfEditTranslate.setToolTipText(translateStr);
		tfCreationDate.setText(creationDateStr);
		tfRepeateDate.setText(repeateDateStr);
		tfBox.setText(boxStr);
		tfRepeateCount.setText(repeateCountStr);
		tfID.setText(idStr);
		comboBox.setSelectedIndex(box);
	}

	@SuppressWarnings("unchecked")
	private void refreshComboBox() {
		comboBox.removeAllItems();
		for (int i = 0; i < WordController.repeatPeriodArray.length; i++) {
			comboBox.addItem(WordController.getBoxInfo(i));
		}
	}

	private boolean isDefinedWord() {
		boolean isDefined = editedWord == null ? false
				: tfWord.getText().equals(editedWord.getWord()) && tfTranslate.getText().equals(editedWord.getTranslate())
						&& comboBox.getSelectedIndex() != -1 && (editState == EditState.DELETE
								|| (!tfEditWord.getText().trim().equals("") && !tfEditTranslate.getText().trim().equals("")));

		if (!isDefined) {
			JOptionPane.showMessageDialog(null, getLocaledItem("Undefined word"), getLocaledItem("Error"),
					JOptionPane.ERROR_MESSAGE);
		}

		editState = EditState.READ;

		return isDefined;
	}

	private void searchWords() {
		int index = cmbFindTag.getSelectedIndex();
		refreshFindTags(tfFindTag.getText());
		cmbFindTag.setSelectedIndex(index);

		GUIController.searchWords(searchList, tfFindWord.getText(), tbWordTranslate.isSelected(), cbRepTimeOrder.isSelected(),
				(String) cmbFindTag.getSelectedItem());
		table.getSelectionModel().clearSelection();
		table.repaint();
		table.revalidate();
		setWordInEditArea(null);
		showWordsCount();
	}

	private void showWordsCount() {
		lblCountB0.setText(String.valueOf(wordsCountArray[0]));
		lblCountB1.setText(String.valueOf(wordsCountArray[1]));
		lblCountB2.setText(String.valueOf(wordsCountArray[2]));
		lblCountB3.setText(String.valueOf(wordsCountArray[3]));
		lblCountB4.setText(String.valueOf(wordsCountArray[4]));
		lblCountB5.setText(String.valueOf(wordsCountArray[5]));
		lblCountB6.setText(String.valueOf(wordsCountArray[6]));
		lblCountB7.setText(String.valueOf(wordsCountArray[7]));
		lblCountAll.setText(String.valueOf(getWordsCount()));
	}

	private void incrementBoxSelection(int delta) {
		try {
			int newIndex = comboBox.getSelectedIndex() + delta;
			if (newIndex != -1) {
				comboBox.setSelectedIndex(newIndex);
			}
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("unchecked")
	private void refreshEditorTags(Word p_word) {
		actualTagSet.clear();
		editedTagSet.clear();
		comboBoxTag.removeAllItems();
		comboBoxEditTag.removeAllItems();

		if (p_word != null) {
			p_word.obtainTagList().forEach(t -> {
				actualTagSet.add(t);
				editedTagSet.add(t);
			});
		}

		actualTagSet.forEach(t -> {
			comboBoxTag.addItem(t);
			comboBoxEditTag.addItem(t);
		});

		try {
			comboBoxTag.setSelectedIndex(0);
			comboBoxEditTag.setSelectedIndex(0);
		} catch (Exception e) {
		}
	}

	@SuppressWarnings("unchecked")
	private void refreshFindTags(String p_tag) {
		List<String> v_allTagList = WordController.userDataRegistry.getTagRegistry().obtainTagList();
		String v_tag = p_tag == null ? "" : p_tag.trim().toUpperCase();

		cmbFindTag.removeAllItems();
		cmbFindTag.addItem(getLocaledItem("All tags"));

		v_allTagList.stream().filter(t -> t.contains(v_tag) || v_tag.equals("")).forEach(cmbFindTag::addItem);

		try {
			cmbFindTag.setSelectedIndex(0);
			if (!v_tag.equals("")) {
				cmbFindTag.setSelectedIndex(1);
			}
		} catch (Exception e) {
		}
	}

	public void showTagComboBoxSelectedItem() {
		String v_newTag = comboBoxEditTag.getSelectedItem() == null ? ""
				: ((String) comboBoxEditTag.getSelectedItem()).trim().toUpperCase();

		comboBoxEditTag.setSelectedItem(v_newTag);

		if (!isLockTagPopup) {
			isLockTagPopup = true;

			if (comboBoxEditTag.getSelectedIndex() != -1) {
				comboBoxEditTag.showPopup();
			}
		}
	}

	public static void incrementWordsCount(Word word) {
		wordsCountArray[word.getBox()]++;
		wordsCount++;
	}

	public static void clearWordsCount() {
		Arrays.fill(wordsCountArray, 0);
		wordsCount = 0;
	}

	private static enum EditState {
		READ, EDIT, DELETE;
	}
}
