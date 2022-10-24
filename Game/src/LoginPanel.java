import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class LoginPanel extends JPanel implements ActionListener {
    JLabel emailLabel;
    JLabel passwordLabel;
    JLabel testingLabel;
    JTextField textField;
    JPasswordField passwordField;
    JButton loginButton;
    JCheckBox showPassword;
    GameFrame frame;
    Image img = Toolkit.getDefaultToolkit().getImage("src/cata.png");
    public LoginPanel(GameFrame frame) {
        this.frame = frame;
        setLayout(null);
        emailLabel = new JLabel("EMAIL:");
        emailLabel.setBounds(175, 300, 100, 30);
        emailLabel.setForeground(Color.WHITE);
        passwordLabel = new JLabel("PASSWORD:");
        passwordLabel.setBounds(175, 450, 100, 30);
        passwordLabel.setForeground(Color.WHITE);
        textField = new JTextField();
        textField.setBounds(250, 300, 150, 30);
        passwordField = new JPasswordField();
        passwordField.setBounds(250, 450, 150, 30);
        showPassword = new JCheckBox("Show Password");
        showPassword.setBounds(250, 480, 150, 30);
        showPassword.setBackground(Color.GRAY);
        loginButton = new JButton("LOGIN");
        loginButton.setBounds(250, 550, 100, 30);
        testingLabel = new JLabel("To skip this stage, pick a random account by typing Testing in both fields.");
        testingLabel.setForeground(Color.RED);
        testingLabel.setBounds(120, 700, 450, 30);
        add(emailLabel);
        add(passwordLabel);
        add(textField);
        add(passwordField);
        add(showPassword);
        add(loginButton);
        add(testingLabel);
        loginButton.addActionListener(this);
        showPassword.addActionListener(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String userText;
            String pwdText;
            userText = textField.getText();
            pwdText = passwordField.getText();
            boolean account_matched = false;
            Account selected_account = new Account();
            for (Account account : frame.game.accounts) {
                if (userText.equals(account.info.getCredentials().getEmail())
                        && pwdText.equals(account.info.getCredentials().getPassword())) {
                    account_matched = true;
                    selected_account = account;
                    break;
                }
            }
            if(account_matched || (userText.equalsIgnoreCase("Testing")
                    && pwdText.equalsIgnoreCase("Testing"))) {
                if(userText.equalsIgnoreCase("Testing")){
                    Random rand = new Random();
                    selected_account = frame.game.accounts.get(rand.nextInt(frame.game.accounts.size()));
                }
                JOptionPane.showMessageDialog(this, "Login Successful!\n" +
                        "Welcome " + selected_account.info.getName() + "!\n" + "You have "
                        + selected_account.maps_completed + " maps completed!");
                frame.selected_account = selected_account;
                frame.switchPanel(new CharacterPanel(selected_account.chars,frame),"Character Selection");
            }
            else
                JOptionPane.showMessageDialog(this, "Invalid Username or Password");
        }

        if (e.getSource() == showPassword) {
            if (showPassword.isSelected())
                passwordField.setEchoChar((char)0);
            else
                passwordField.setEchoChar('•');
        }
    }
}