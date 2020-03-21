package com.app.colibri.view.panels;

import static com.app.colibri.controller.WordController.getBoxInfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
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

	/**
	 * Create the panel.
	 */
	public EditPanel() {
		setLayout(new BorderLayout(0, 0));

		JPanel panelCenter = new JPanel();
		add(panelCenter, BorderLayout.CENTER);
		panelCenter.setLayout(null);

		JLabel lblWord = new JLabel("Word");
		lblWord.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblWord.setHorizontalAlignment(SwingConstants.RIGHT);
		lblWord.setBounds(24, 35, 50, 14);
		panelCenter.add(lblWord);

		JLabel lblTranslate = new JLabel("Translate");
		lblTranslate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTranslate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblTranslate.setBounds(24, 60, 50, 14);
		panelCenter.add(lblTranslate);

		tfWord = new JTextField();
		tfWord.setEditable(false);
		tfWord.setBounds(84, 33, 380, 20);
		panelCenter.add(tfWord);
		tfWord.setColumns(10);

		tfTranslate = new JTextField();
		tfTranslate.setEditable(false);
		tfTranslate.setColumns(10);
		tfTranslate.setBounds(84, 58, 380, 20);
		panelCenter.add(tfTranslate);

		JButton btnEdit = new JButton("Edit");
		btnEdit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editState = EditState.EDIT;
				if (isDefinedWord()) {
					if (tfEditWord.getText().trim().equals(tfWord.getText())
							&& tfEditTranslate.getText().trim().equals(tfTranslate.getText())) {
						JOptionPane.showMessageDialog(null, "No changes", "Info", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					for (Word word : WordController.allWordsList) {
						if (tfEditWord.getText().trim().toLowerCase().equals(word.getWord().toLowerCase())
								&& word != editedWord) {
							JOptionPane.showMessageDialog(null, "This word already exists", "Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}

					final int answer = JOptionPane.showConfirmDialog(null, "Do you really want to EDIT the word?", "Question",
							JOptionPane.YES_NO_OPTION);

					if (answer == JOptionPane.YES_OPTION) {
						editedWord.setWord(tfEditWord.getText().trim());
						editedWord.setTranslate(tfEditTranslate.getText().trim());

						JOptionPane.showMessageDialog(null, "Successfully EDITED", "Info", JOptionPane.INFORMATION_MESSAGE);

						int selectedRowIndex = table.getSelectedRows()[0];
						table.getSelectionModel().clearSelection();
						table.changeSelection(selectedRowIndex, 0, false, false);
					}
				}
			}
		});
		btnEdit.setBounds(386, 160, 77, 23);
		panelCenter.add(btnEdit);

		JButton btnDelete = new JButton("Delete");
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editState = EditState.DELETE;
				if (isDefinedWord()) {
					final int answer = JOptionPane.showConfirmDialog(null, "Do you really want to DELETE the word?", "Question",
							JOptionPane.YES_NO_OPTION);

					if (answer == JOptionPane.YES_OPTION) {
						JOptionPane.showMessageDialog(null, "Successfully DELETED", "Info", JOptionPane.INFORMATION_MESSAGE);
						WordController.allWordsList.remove(editedWord);
						searchWords();
					}
				}
			}
		});
		btnDelete.setPreferredSize(new Dimension(51, 23));
		btnDelete.setBounds(386, 5, 77, 23);
		panelCenter.add(btnDelete);

		tfEditWord = new JTextField();
		tfEditWord.setColumns(10);
		tfEditWord.setBounds(84, 187, 380, 20);
		panelCenter.add(tfEditWord);

		tfEditWord.addMouseListener(new TextFieldClipboardMouseAdapter(tfEditWord));

		JLabel lblEditWord = new JLabel("Word");
		lblEditWord.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEditWord.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblEditWord.setBounds(24, 189, 50, 14);
		panelCenter.add(lblEditWord);

		JLabel lblEditTranslate = new JLabel("Translate");
		lblEditTranslate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEditTranslate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblEditTranslate.setBounds(24, 214, 50, 14);
		panelCenter.add(lblEditTranslate);

		tfEditTranslate = new JTextField();
		tfEditTranslate.setColumns(10);
		tfEditTranslate.setBounds(84, 212, 380, 20);
		panelCenter.add(tfEditTranslate);

		tfEditTranslate.addMouseListener(new TextFieldClipboardMouseAdapter(tfEditTranslate));

		JLabel lblActual = new JLabel("Actual");
		lblActual.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblActual.setHorizontalAlignment(SwingConstants.CENTER);
		lblActual.setBounds(238, 11, 46, 14);
		panelCenter.add(lblActual);

		JLabel lblEdit = new JLabel("Edited");
		lblEdit.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblEdit.setHorizontalAlignment(SwingConstants.CENTER);
		lblEdit.setBounds(238, 165, 46, 14);
		panelCenter.add(lblEdit);

		JLabel lblCreationDate = new JLabel("Creation date");
		lblCreationDate.setHorizontalAlignment(SwingConstants.LEFT);
		lblCreationDate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblCreationDate.setBounds(84, 89, 77, 14);
		panelCenter.add(lblCreationDate);

		tfCreationDate = new JTextField();
		tfCreationDate.setEditable(false);
		tfCreationDate.setColumns(10);
		tfCreationDate.setBounds(164, 87, 105, 20);
		panelCenter.add(tfCreationDate);

		JLabel lblRepeateDate = new JLabel("Repeat date");
		lblRepeateDate.setHorizontalAlignment(SwingConstants.LEFT);
		lblRepeateDate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblRepeateDate.setBounds(84, 113, 77, 14);
		panelCenter.add(lblRepeateDate);

		tfRepeateDate = new JTextField();
		tfRepeateDate.setEditable(false);
		tfRepeateDate.setColumns(10);
		tfRepeateDate.setBounds(164, 111, 105, 20);
		panelCenter.add(tfRepeateDate);

		JLabel lblBox = new JLabel("Box");
		lblBox.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBox.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblBox.setBounds(305, 113, 77, 14);
		panelCenter.add(lblBox);

		tfBox = new JTextField();
		tfBox.setEditable(false);
		tfBox.setColumns(10);
		tfBox.setBounds(386, 111, 78, 20);
		panelCenter.add(tfBox);

		JLabel lblRepeateCount = new JLabel("Repeat count");
		lblRepeateCount.setHorizontalAlignment(SwingConstants.RIGHT);
		lblRepeateCount.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblRepeateCount.setBounds(287, 89, 95, 14);
		panelCenter.add(lblRepeateCount);

		tfRepeateCount = new JTextField();
		tfRepeateCount.setEditable(false);
		tfRepeateCount.setColumns(10);
		tfRepeateCount.setBounds(386, 87, 78, 20);
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
		pnlWordsSummary.setBounds(24, 270, 440, 60);
		panelCenter.add(pnlWordsSummary);
		pnlWordsSummary.setLayout(null);

		JLabel lblBoxCount = new JLabel("Box");
		lblBoxCount.setBounds(0, 12, 50, 14);
		pnlWordsSummary.add(lblBoxCount);
		lblBoxCount.setHorizontalAlignment(SwingConstants.RIGHT);
		lblBoxCount.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JLabel lblCount = new JLabel("Count");
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
		lblAll.setBounds(400, 12, 40, 14);
		pnlWordsSummary.add(lblAll);
		lblAll.setHorizontalAlignment(SwingConstants.CENTER);
		lblAll.setFont(new Font("Tahoma", Font.PLAIN, 12));

		lblCountAll = new JLabel("0");
		lblCountAll.setBounds(400, 32, 40, 14);
		pnlWordsSummary.add(lblCountAll);
		lblCountAll.setHorizontalAlignment(SwingConstants.CENTER);
		lblCountAll.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JPanel panelWest = new JPanel();
		panelWest.setName("");
		add(panelWest, BorderLayout.WEST);
		panelWest.setLayout(new BorderLayout(0, 0));

		GUIController.searchWords(searchList, null, false);

		TableModel modelAllWords = new SearchTableModel(searchList);
		table = new JTable();
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
				if (e.getButton() == MouseEvent.BUTTON2) {
					GUIController.addTextFromClipboard(tfFindWord);
					searchWords();
				} else if (e.getButton() == MouseEvent.BUTTON3) {
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
		tbWordTranslate.setPreferredSize(new Dimension(47, 23));
		tbWordTranslate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tbWordTranslate.isSelected()) {
					tbWordTranslate.setText("T");
				} else {
					tbWordTranslate.setText("W");
				}
				searchWords();
			}
		});

		panelFind.add(tbWordTranslate);
		panelFind.add(tfFindWord);
		tfFindWord.setColumns(15);

		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tfFindWord.setText(null);
				searchWords();
			}
		});
		panelFind.add(btnClear);

		showWordsCount();
	}

	private void setWordInEditArea(Word word) {
		this.editedWord = word;

		final String wordStr = word == null ? null : word.getWord();
		final String translateStr = word == null ? null : word.getTranslate();
		final String creationDateStr = word == null ? null : WordController.dateFormat.format(new Date(word.getCreationTime()));
		final String repeateDateStr = word == null ? null
				: WordController.dateFormat.format(new Date(word.getRegTime() + WordController.getTimeDelta(word.getBox())));
		final String boxStr = word == null ? null : getBoxInfo(word);
		final String repeateCountStr = word == null ? null : String.valueOf(word.getRepeateIndicator());
		final String idStr = word == null ? null : String.valueOf(word.getId());

		tfWord.setText(wordStr);
		tfTranslate.setText(translateStr);
		tfEditWord.setText(wordStr);
		tfEditTranslate.setText(translateStr);
		tfCreationDate.setText(creationDateStr);
		tfRepeateDate.setText(repeateDateStr);
		tfBox.setText(boxStr);
		tfRepeateCount.setText(repeateCountStr);
		tfID.setText(idStr);
	}

	private boolean isDefinedWord() {
		boolean isDefined = editedWord == null ? false
				: tfWord.getText().equals(editedWord.getWord()) && tfTranslate.getText().equals(editedWord.getTranslate())
						&& (editState == EditState.DELETE
								|| (!tfEditWord.getText().trim().equals("") && !tfEditTranslate.getText().trim().equals("")));

		if (!isDefined) {
			JOptionPane.showMessageDialog(null, "Undefined word", "Error", JOptionPane.ERROR_MESSAGE);
		}

		editState = EditState.READ;

		return isDefined;
	}

	private void searchWords() {
		GUIController.searchWords(searchList, tfFindWord.getText(), tbWordTranslate.isSelected());
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
