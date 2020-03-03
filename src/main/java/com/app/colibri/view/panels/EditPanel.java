package com.app.colibri.view.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
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
	private JTable table;
	private JTextField tfFindWord;
	private JTextField tfWord;
	private JTextField tfTranslate;
	private JTextField tfEditWord;
	private JTextField tfEditTranslate;

	private Word editedWord = null;
	private List<Word> searchList = new ArrayList<>();

	private EditState editState = EditState.READ;

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
		lblWord.setBounds(28, 35, 46, 14);
		panelCenter.add(lblWord);

		JLabel lblTranslate = new JLabel("Translate");
		lblTranslate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTranslate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblTranslate.setBounds(10, 60, 64, 14);
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
		btnEdit.setBounds(386, 104, 77, 23);
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
		tfEditWord.setBounds(84, 131, 380, 20);
		panelCenter.add(tfEditWord);

		tfEditWord.addMouseListener(new TextFieldClipboardMouseAdapter(tfEditWord));

		JLabel lblEditWord = new JLabel("Word");
		lblEditWord.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEditWord.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblEditWord.setBounds(28, 133, 46, 14);
		panelCenter.add(lblEditWord);

		JLabel lblEditTranslate = new JLabel("Translate");
		lblEditTranslate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEditTranslate.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblEditTranslate.setBounds(10, 158, 64, 14);
		panelCenter.add(lblEditTranslate);

		tfEditTranslate = new JTextField();
		tfEditTranslate.setColumns(10);
		tfEditTranslate.setBounds(84, 156, 380, 20);
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
		lblEdit.setBounds(238, 109, 46, 14);
		panelCenter.add(lblEdit);

		JPanel panelWest = new JPanel();
		panelWest.setName("");
		add(panelWest, BorderLayout.WEST);
		panelWest.setLayout(new BorderLayout(0, 0));

		GUIController.searchWords(searchList, null);

		TableModel modelAllWords1 = new SearchTableModel(searchList);
		table = new JTable();
		table.setModel(modelAllWords1);

//		table = new JTable();
//		table.setModel(new DefaultTableModel(
//				new Object[][] { { null, null }, { null, null }, { null, null }, { null, null }, { null, null }, },
//				new String[] { "New column", "New column" }));

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
		panelFind.add(tfFindWord);
		tfFindWord.setColumns(15);

		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tfFindWord.setText(null);
				GUIController.searchWords(searchList, null);
				table.getSelectionModel().clearSelection();
				table.revalidate();
				setWordInEditArea(null);
			}
		});
		panelFind.add(btnClear);

	}

	private void setWordInEditArea(Word word) {
		this.editedWord = word;

		final String wordStr = word == null ? null : word.getWord();
		final String translateStr = word == null ? null : word.getTranslate();

		tfWord.setText(wordStr);
		tfTranslate.setText(translateStr);
		tfEditWord.setText(wordStr);
		tfEditTranslate.setText(translateStr);
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
		GUIController.searchWords(searchList, tfFindWord.getText());
		table.getSelectionModel().clearSelection();
		table.revalidate();
		setWordInEditArea(null);
	}

	private static enum EditState {
		READ, EDIT, DELETE;
	}
}
