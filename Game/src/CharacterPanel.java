import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

public class CharacterPanel extends JPanel implements ListSelectionListener, ActionListener {
    JLabel listText;
    JLabel detailsText;
    JList<Character> characterList;
    JPanel detailsPane;
    JLabel name;
    JLabel exp;
    JLabel level;
    JButton selectButton;
    GameFrame frame;
    Image img = Toolkit.getDefaultToolkit().getImage("src/char_bg.jpg");
    public CharacterPanel(List<Character> characters, GameFrame frame) {
        this.frame = frame;
        setLayout(null);
        name = new JLabel();
        exp = new JLabel();
        level = new JLabel();
        listText = new JLabel("Select a Character:");
        listText.setBounds(70, 50, 150, 80);
        listText.setForeground(Color.WHITE);
        detailsText = new JLabel("Character details:");
        detailsText.setBounds(425, 50 , 150, 80);
        detailsText.setForeground(Color.WHITE);
        characterList = new JList<>(new Vector<>(characters));
        characterList.setCellRenderer( new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (renderer instanceof JLabel && value instanceof Character)
                    ((JLabel) renderer).setText(((Character) value).getClass().getSimpleName());
                return renderer;
            }
        });
        characterList.setBounds(25, 100,200,300);
        characterList.addListSelectionListener(this);
        characterList.setBackground(Color.GRAY);
        detailsPane = new JPanel();
        detailsPane.setLayout(new GridLayout(3,1));
        detailsPane.setBounds(400,100,200,100);
        detailsPane.setBackground(Color.GRAY);
        selectButton = new JButton("Select");
        selectButton.setBounds(70,450,100,50);
        selectButton.addActionListener(this);
        add(new JScrollPane(characterList));
        add(listText);
        add(detailsText);
        add(characterList);
        add(detailsPane);
        add(selectButton);
        detailsPane.setVisible(false);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
        repaint();
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(characterList.isSelectionEmpty()) {
            detailsPane.setVisible(false);
        }
        else{
            int i = characterList.getSelectedIndex();
            Character character = characterList.getModel().getElementAt(i);
            name.setText(" - Name: " + character.name);
            level.setText(" - Level: " + character.level);
            exp.setText(" - Experience: " + character.exp);
            detailsPane.add(name);
            detailsPane.add(level);
            detailsPane.add(exp);
            detailsPane.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == selectButton) {
            int i = characterList.getSelectedIndex();
            if(i >= 0) {
                Character selected_character = characterList.getModel().getElementAt(i);
                GamePanel gamePanel = new GamePanel(selected_character, frame);
                frame.switchPanel( gamePanel, "Game Window");
                gamePanel.showOption();
            }
        }
    }
}