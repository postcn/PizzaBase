import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


public class PizzaBase {
	static String dir;
	static String username;
	static HashMap<String,String> passMap;

	/**
	 * Main method for running the pizza database system
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		dir = System.getProperty("user.dir");
		try {
			ObjectInputStream ostream = new ObjectInputStream(new FileInputStream(dir+"/pass.p"));
			passMap = (HashMap<String,String>) ostream.readObject();
		} catch (FileNotFoundException e) {
			if (JOptionPane.showConfirmDialog(null,"No login file found. Would you like to create a default?", "",JOptionPane.WARNING_MESSAGE) != 0) {
				return;
			}
			passMap = new HashMap<String,String>();
			passMap.put(userHash("Administrator"), userHash("password"));
			savePass();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (login()) {
			new PizzaFrame();
		}	
		else {
			JOptionPane.showMessageDialog(null, "Login failure: number of tries used up. Please restart to try again","",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	static boolean login() {
		JFrame frame = new JFrame();
		try {
			frame.setIconImage(ImageIO.read(PizzaBase.class.getResource("pizzaicon.jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.setTitle("Login to PizzaBase");
		Box center = Box.createVerticalBox();
		Box username = Box.createHorizontalBox();
		Box password = Box.createHorizontalBox();
		JLabel use = new JLabel("Username: ");
		JLabel pass = new JLabel("Password: ");
		JTextField user = new JTextField();
		JTextField passw = new JTextField();
		username.add(use);
		username.add(Box.createGlue());
		username.add(user);
		password.add(pass);
		password.add(Box.createGlue());
		password.add(passw);
		center.add(username);
		center.add(Box.createGlue());
		center.add(password);
		frame.add(center);
		JButton login = new JButton("Login");
		LoginListener list = new LoginListener(2,user,passw);
		login.addActionListener(list);
		frame.add(login, BorderLayout.SOUTH);
		frame.setSize(300,100);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		while(true) {
			if (list.checkLeft() < 0) {
				break;
			}
			if (list.loggedIn()) {
				frame.dispose();
				return true;
			}
		}
		frame.dispose();
		return false;
	}

	private static void savePass() {
		try {
			ObjectOutputStream ostream = new ObjectOutputStream(new FileOutputStream(dir+"/pass.p"));
			ostream.writeObject(passMap);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String userHash(String toHash) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = toHash.getBytes();
			md.update(array, 0, array.length);
			BigInteger i = new BigInteger(1,md.digest());
			return i.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
}

class LoginListener implements ActionListener {
	private int attempts;
	private JTextField username;
	private JTextField password;
	private boolean checker;
	
	LoginListener(int attempts, JTextField username, JTextField password) {
		this.attempts = attempts;
		this.username = username;
		this.password = password;
		this.checker = false;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (attempts > 0) {
			String useHash = PizzaBase.userHash(username.getText());
			String passHash = PizzaBase.userHash(password.getText());
			if (PizzaBase.passMap.get(useHash) != null && PizzaBase.passMap.get(useHash).equals(passHash)) {
				this.checker = true;
			}
			else {
				JOptionPane.showMessageDialog(null, "Login Failure");
				password.setText("");
			}
		}
		attempts--;
	}
	
	public int checkLeft() {
		return this.attempts;
	}
	
	public boolean loggedIn() {
		return this.checker;
	}
}
