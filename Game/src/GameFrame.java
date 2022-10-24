import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    Game game;
    Account selected_account;

    public GameFrame(Game game) {
        this.game = game;
        centerFrame(640,960);
        switchPanel(new LoginPanel(this),"Login Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
    }

    public void switchPanel(JPanel panel, String title) {
        getContentPane().removeAll();
        setTitle(title);
        getContentPane().add(panel);
        revalidate();
        repaint();
    }

    public void centerFrame(int width,int height){
        setSize(width, height);
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (size.width - GameFrame.this.getWidth()) / 2;
        int y = (size.height - GameFrame.this.getHeight()) / 2;
        setBounds(x,y,getWidth(),getHeight());
    }
}