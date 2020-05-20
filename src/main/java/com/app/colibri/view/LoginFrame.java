package com.app.colibri.view;

import static com.app.colibri.service.AppSettings.jsonMapper;
import static com.app.colibri.service.MainLocaleManager.addTrackedItem;
import static com.app.colibri.view.util.ViewUtil.askCode;
import static com.app.colibri.view.util.ViewUtil.msgErrorCode;
import static com.app.colibri.view.util.ViewUtil.msgInfoCode;
import static com.app.colibri.view.util.ViewUtil.msgWarningCode;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.app.colibri.controller.GUIController;
import com.app.colibri.controller.RESTController;
import com.app.colibri.controller.WordController;
import com.app.colibri.model.Box;
import com.app.colibri.model.User;
import com.app.colibri.registry.UserDataRegistry;
import com.app.colibri.service.AppRun;
import com.app.colibri.service.AppSettings;
import com.app.colibri.service.MainLocaleManager;
import com.app.colibri.service.crypt.CryptoException;
import com.app.colibri.service.crypt.CryptoUtils;
import com.app.colibri.service.crypt.Password;
import com.app.colibri.view.listeners.TextFieldClipboardMouseAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.Data;

@Data
public class LoginFrame {

	public static enum State {
		LOGIN, REGISTER;
	}

	private char echoChar;
	private String newUsernameBuffer = "";
	private String newUserPasswordBuffer = "";

	private JFrame frame;
	private State state;
	private JTextField tfLogin;
	private JPasswordField pfPassword;
	private JButton btnLogin;
	private JLabel lblRegister;
	private JLabel lblLocale;
	private JCheckBox chbAutoLogIn;
	private JCheckBox chbPwState;
	private JCheckBox chbWeb;

	/**
	 * Launch the application.
	 */
	public static void launch(State state) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame window = new LoginFrame(state);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LoginFrame() {
		initialize();
	}

	/**
	 * @wbp.parser.constructor
	 */
	public LoginFrame(State state) {
		this.state = state;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		GUI.loginFrame = this;

		if (GUI.mainFrame != null) {
			GUI.mainFrame.setEnabled(false);
		}

		frame = new JFrame();
		frame.setResizable(false);
		changeTitle();
		final String imgPath = "images/colibri_icon.png";
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getClassLoader().getResource(imgPath)));

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				GUI.loginFrame = null;
				if (GUI.mainFrame != null) {
					GUI.mainFrame.setEnabled(true);
					GUI.mainFrame.toFront();
				}
			}
		});
		frame.setBounds(100, 100, 300, 170);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JPanel pnlMain = new JPanel();
		frame.getContentPane().add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(null);

		JLabel lblLogin = new JLabel("Username");
		lblLogin.setHorizontalAlignment(SwingConstants.RIGHT);
		addTrackedItem(lblLogin, "Username");
		lblLogin.setBounds(5, 27, 114, 16);
		pnlMain.add(lblLogin);

		tfLogin = new JTextField();
		tfLogin.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {

				if (state == State.REGISTER) {
					final String newUsername = tfLogin.getText();

					if (!checkLoginConstraints(newUsername, 0)) {
						msgWarningCode("username_constraint");
						tfLogin.setText(newUsernameBuffer);
					} else {
						newUsernameBuffer = newUsername;
					}
				}

			}
		});
		tfLogin.addMouseListener(new TextFieldClipboardMouseAdapter(tfLogin, 2));
		tfLogin.setBounds(124, 25, 160, 20);
		pnlMain.add(tfLogin);
		tfLogin.setColumns(10);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				chbPwState.setSelected(!chbPwState.isSelected());
				if (chbPwState.isSelected()) {
					pfPassword.setEchoChar((char) 0);
				} else {
					pfPassword.setEchoChar(echoChar);
				}
			}
		});
		lblPassword.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		addTrackedItem(lblPassword, "Password", "show_hide_password");
		lblPassword.setBounds(42, 52, 77, 16);
		pnlMain.add(lblPassword);

		pfPassword = new JPasswordField();
		pfPassword.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {

				if (state == State.REGISTER) {
					final String newPassword = new String(pfPassword.getPassword());

					if (!checkPassConstraints(newPassword, 0)) {
						msgWarningCode("pass_constraint");
						pfPassword.setText(newUserPasswordBuffer);
					} else {
						newUserPasswordBuffer = newPassword;
					}
				}

			}
		});
		echoChar = pfPassword.getEchoChar();
		pfPassword.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					String clip = GUIController.getFromClipboard();
					if (!clip.equals("")) {
						pfPassword.setText(clip);
					}
				} else if (e.getButton() == MouseEvent.BUTTON2) {
					pfPassword.setText("");
				}
			}
		});
		pfPassword.setBounds(124, 50, 160, 20);
		pnlMain.add(pfPassword);

		lblRegister = new JLabel("Register");
		lblRegister.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblRegister.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				changeState();
			}
		});
		lblRegister.setHorizontalAlignment(SwingConstants.RIGHT);
		addTrackedItem(lblRegister, "Register");
		lblRegister.setBounds(42, 104, 77, 14);
		lblRegister.setForeground(Color.GRAY);
		lblRegister.setFont(new Font("Tahoma", Font.PLAIN, 11));
		pnlMain.add(lblRegister);

		btnLogin = new JButton("Log in");
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				final String newUsername = tfLogin.getText();
				final String newPassword = new String(pfPassword.getPassword());

				File userFile = new File("UserData/" + newUsername + "/User.encrypted");

				if (state == State.LOGIN) {

					User user = AppSettings.appSettings.getUser();

					if (userFile.exists()) {

						try {
							String jsonUser = CryptoUtils.decrypt(AppSettings.KEY, userFile);
							user = jsonMapper.readValue(jsonUser, User.class);
						} catch (IOException | CryptoException ex) {
							ex.printStackTrace();
						}

						if (user.getUserName().equals(newUsername)
								&& Password.checkPassword(newPassword, user.getUserPasswordHash())) {

							if (chbWeb.isSelected()) {

								if (RESTController.checkConnection()) {

									int code = RESTController.login(user, newPassword);

									if (code == 406) {
										msgWarningCode("wrong_web_user_password");
										return;
									} else if (code == 404) {
										msgWarningCode("web_user_not_found");
										return;
									} else if (code == 400) {
										msgErrorCode("web_user_login_fail");
										return;
									}

								} else {
									msgWarningCode("web_connention_error");
									return;
								}

							} else {
								user.setId(0);
								user.setAuthorizationToken(null);
							}

							WordController.serializeUserDataRegistryWithBackup();

							user.setAutoEnter(chbAutoLogIn.isSelected());
							AppSettings.appSettings.setUser(user);

							AppSettings.reloadMainFrame();

							frame.dispose();

						} else {
							msgWarningCode("worong_name_or_pass");
							return;
						}

					} else {
						msgWarningCode("user_not_found");
						return;
					}
					// TODO register
				} else if (state == State.REGISTER) {

					if (userFile.exists()) {
						msgWarningCode("user_exists");
						return;
					} else if (!checkLoginConstraints(newUsername, 4)) {
						msgWarningCode("username_constraint");
						return;
					} else if (!checkPassConstraints(newPassword, 4)) {
						msgWarningCode("pass_constraint");
						return;
					} else {

						User newUser = AppRun.appContext.getBean("user", User.class);
						newUser.setUserName(newUsername);

						if (chbWeb.isSelected()) {
							if (RESTController.checkConnection()) {
								newUser.setUserPasswordHash(Password.hashPassword(newPassword));
								int responseCode = RESTController.register(newUser);

								if (responseCode == 409) {
									msgWarningCode("web_user_exist");
									return;
								} else if (responseCode != 201) {
									msgErrorCode("web_user_reg_fail");
									return;
								}

							} else {
								msgWarningCode("web_connention_error");
								return;
							}
						}

						newUser.setUserPasswordHash(Password.hashPassword(newPassword));

						try {
							File userFileDir = new File("UserData/" + newUsername);
							userFileDir.mkdir();

							String jsonNewUser = jsonMapper.writeValueAsString(newUser);
							CryptoUtils.encrypt(AppSettings.KEY, jsonNewUser, userFile);

							if (GUI.mainFrame != null && WordController.allWordsList != null
									&& WordController.allWordsList.size() > 0) {
								if (askCode("ask_copy_words_to_new_user")) {
									User currentUser = AppSettings.appSettings.getUser();
									AppSettings.appSettings.setUser(newUser);
									WordController.serializeUserDataRegistry();
									AppSettings.appSettings.setUser(currentUser);
								}
							}

							changeState();
							msgInfoCode("Registration success");
						} catch (CryptoException | JsonProcessingException ex) {
							ex.printStackTrace();
							msgErrorCode("Registration error");
						}
					}

				}

			}
		});
		btnLogin.setMargin(new Insets(2, 2, 2, 2));
		addTrackedItem(btnLogin, "Log in");
		btnLogin.setBounds(199, 98, 85, 26);
		pnlMain.add(btnLogin);

		lblLocale = new JLabel("EN");
		lblLocale.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblLocale.setFont(new Font("Tahoma", Font.PLAIN, 12));
		addTrackedItem(lblLocale, "EN");
		lblLocale.setBounds(4, 4, 30, 16);
		pnlMain.add(lblLocale);
		lblLocale.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String lang = GUI.localeSwitchMap.get(lblLocale.getText());
				lblLocale.setText(lang);
				AppSettings.changeLocale(lang);
			}
		});
		lblLocale.setForeground(Color.GRAY);

		chbAutoLogIn = new JCheckBox("Auto log in");
		addTrackedItem(chbAutoLogIn, "Auto log in", "Auto log in");
		chbAutoLogIn.setBounds(199, 71, 85, 24);
		pnlMain.add(chbAutoLogIn);

		chbPwState = new JCheckBox("PWState");
		chbPwState.setVisible(false);
		chbPwState.setBounds(42, 72, 77, 24);
		pnlMain.add(chbPwState);

		chbWeb = new JCheckBox("Web");
		addTrackedItem(chbWeb, "Web", "Web", "chb_web_hint_login", "chb_web_hint_login");
		chbWeb.setBounds(144, 100, 54, 23);
		pnlMain.add(chbWeb);

		MainLocaleManager.changeLocaleStatic();
	}

	private boolean checkLoginConstraints(String login, int min) {
		Pattern p = Pattern.compile("^[0-9a-zA-Zа-яА-ЯёЁ]{" + min + ",60}$");
		return (p.matcher(login).find() && (!login.equalsIgnoreCase("guest")));
	}

	private boolean checkPassConstraints(String pass, int min) {
		Pattern p = Pattern.compile("^[\\S]{" + min + ",72}$");
		return p.matcher(pass).find();
	}

	private void changeTitle() {
		if (state == State.LOGIN) {
			addTrackedItem(frame, "Log in");
		} else {
			addTrackedItem(frame, "Register");
		}

		MainLocaleManager.changeLocaleStatic();
	}

	private void changeState() {

		String btnText;
		String lblText;

		if (state == State.LOGIN) {
			state = State.REGISTER;
			btnText = "Register";
			lblText = "Log in";
			addTrackedItem(chbWeb, "Web", "Web", "chb_web_hint", "chb_web_hint");
		} else {
			state = State.LOGIN;
			btnText = "Log in";
			lblText = "Register";
			addTrackedItem(chbWeb, "Web", "Web", "chb_web_hint_login", "chb_web_hint_login");
		}

		addTrackedItem(btnLogin, btnText);
		addTrackedItem(lblRegister, lblText);
		chbAutoLogIn.setVisible(state == State.LOGIN);

		changeTitle();
	}

	public static void appInit() {
		appInit(WordController.unserializeUserDataRegistry());
	}

	public static void appInit(UserDataRegistry userDataRegistry) {
		WordController.loadUserData(userDataRegistry);
		Box.refreshBox();
		SwingUtilities.invokeLater(GUI::new);
	}

}
