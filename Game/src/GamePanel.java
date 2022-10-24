import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    GameFrame frame;
    JTable gameTable;
    JPanel midPanel;

    JLabel healthLabel;
    JLabel manaLabel;
    JLabel goldLabel;
    JLabel capacityLabel;
    JPanel topPanel;

    JLabel storyLabel;
    JPanel bottomPanel;

    JLabel choiceLabel;
    JPanel rightPanel;

    JLabel positionLabel;
    JPanel leftPanel;

    Grid map;
    boolean character_is_busy;

    JButton yes;
    JButton no;
    JButton selectBasicButton;
    JButton selectAbilityButton;
    JButton selectPotionButton;

    int earnedExperience;
    int killedEnemies;
    int earnedGold;
    public GamePanel(Character current_character,GameFrame frame){
        this.frame = frame;
        frame.centerFrame(800,800);
        setLayout(new BorderLayout(0,150));
        map = Grid.grid_gen(5, 5, current_character);
        //map = Grid.rand_grid_gen(current_character);
        gameTable = new JTable(new CellTableModel(map));
        gameTable.setDefaultRenderer(Cell.class,new CellRenderer());
        gameTable.setRowHeight(60);
        for (int i = 0; i < map.height; i++) {
            gameTable.getColumnModel().getColumn(i).setPreferredWidth(60);
        }
        gameTable.setTableHeader(null);
        gameTable.setCellSelectionEnabled(false);
        gameTable.setFocusable(false);
        gameTable.setDefaultEditor(Cell.class,null);
        healthLabel = new JLabel("HP: " + current_character.currentHealth);
        manaLabel = new JLabel("MP: " + current_character.currentMana);
        goldLabel = new JLabel("Gold: " + current_character.inventory.gold);
        capacityLabel = new JLabel("Capacity: " + current_character.inventory.returnQuantity());
        topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.add(healthLabel);
        topPanel.add(manaLabel);
        topPanel.add(goldLabel);
        topPanel.add(capacityLabel);
        storyLabel = new JLabel(frame.game.showStory(map.current_cell));
        bottomPanel = new JPanel();
        bottomPanel.add(storyLabel);
        choiceLabel = new JLabel("<html>Choose where to go:<br>Use the arrow keys ↑ ↓ → ←<br>to move.<html>");
        rightPanel = new JPanel();
        rightPanel.add(choiceLabel);
        positionLabel = new JLabel("Your current cell is: " + map.player.x + ", " + map.player.y);
        leftPanel = new JPanel();
        leftPanel.add(positionLabel);
        midPanel = new JPanel();
        midPanel.add(gameTable);
        topPanel.setPreferredSize(new Dimension(800,50));
        rightPanel.setPreferredSize(new Dimension(190,300));
        midPanel.setPreferredSize(new Dimension(400,400));
        leftPanel.setPreferredSize(new Dimension(190,300));
        bottomPanel.setPreferredSize(new Dimension(800,50));
        add(topPanel, BorderLayout.NORTH);
        add(midPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(rightPanel, BorderLayout.EAST);
        add(leftPanel, BorderLayout.WEST);
        character_is_busy = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(yes)){
            Shop shop = (Shop) map.current_cell.cellType;
            rightPanel.removeAll();
            rightPanel.revalidate();
            rightPanel.repaint();
            rightPanel.setLayout(new GridLayout(shop.potionList.size()+1,2));
            for(Potion potion : shop.potionList){
                JLabel potionLabel = new JLabel("<html><u>"+potion.getClass().getSimpleName()+ "</u><br>Price: "
                        + potion.getPrice() + "<br>RegenValue: " + potion.getRegen() + "<br>Weight: "
                        + potion.getWeight() + "</html>");
                rightPanel.add(potionLabel);
                JButton buyButton = new JButton("Buy");
                rightPanel.add(buyButton);
                buyButton.addActionListener(f -> {
                    if(map.player.inventory.returnQuantity() < potion.getWeight())
                        JOptionPane.showMessageDialog(this, "You don't have enough space!");
                    else {
                        int var = map.player.inventory.gold;
                        shop.potionList.remove(potion);
                        map.player.buyPotion(potion);
                        if (var == map.player.inventory.gold) {
                            shop.potionList.add(potion);
                            JOptionPane.showMessageDialog(this, "You don't have enough gold!");
                        } else {
                            rightPanel.remove(potionLabel);
                            rightPanel.remove(buyButton);
                        }
                    }
                    rightPanel.revalidate();
                    rightPanel.repaint();
                    goldLabel.setText("Gold: " + map.player.inventory.gold);
                    capacityLabel.setText("Capacity: " + map.player.inventory.returnQuantity());
                    if (shop.potionList.size() == 0) {
                        character_is_busy = false;
                        rightPanel.removeAll();
                        rightPanel.revalidate();
                        rightPanel.repaint();
                        choiceLabel.setText("<html>Choose where to go:" +
                                "<br>Use the arrow keys ↑ ↓ → ←" +
                                "<br>to move.<html>");
                        rightPanel.add(choiceLabel);
                        JOptionPane.showMessageDialog(this, "The shop is empty!");
                    }
                });
            }
            JButton exit = new JButton("Exit");
            exit.addActionListener(f -> {
                character_is_busy = false;
                rightPanel.removeAll();
                rightPanel.revalidate();
                rightPanel.repaint();
                choiceLabel.setText("<html>Choose where to go:<br>Use the arrow keys ↑ ↓ → ←<br>to move.<html>");
                rightPanel.add(choiceLabel);
            });
            rightPanel.add(exit);
            rightPanel.add(new Panel());
        }

        if(e.getSource().equals(no)){
            character_is_busy = false;
            rightPanel.removeAll();
            rightPanel.revalidate();
            rightPanel.repaint();
            choiceLabel.setText("<html>Choose where to go:<br>Use the arrow keys ↑ ↓ → ←<br>to move.<html>");
            rightPanel.add(choiceLabel);
        }

        if(e.getSource().equals(selectBasicButton)){
            Enemy enemy = (Enemy) map.current_cell.cellType;
            int before = enemy.currentHealth;
            int damage  =  map.player.getDamage();
            enemy.receiveDamage(damage);
            if(enemy.currentHealth == before){
                JOptionPane.showMessageDialog(this, "The enemy dodged your attack!");
            }
            else {
                if((map.player instanceof Warrior && (before - enemy.currentHealth) == map.player.strength*2)
                || (map.player instanceof Rogue && (before - enemy.currentHealth) == map.player.dexterity*2)
                || (map.player instanceof Mage && (before - enemy.currentHealth) == map.player.charisma*2))
                    JOptionPane.showMessageDialog(this, "Normal hit on enemy for "
                            + damage + " HP!");
                else
                    JOptionPane.showMessageDialog(this, "Critical hit on enemy for "
                            + damage + " HP!");
            }
            if(enemy.currentHealth <= 0){
                JOptionPane.showMessageDialog(this, "You killed the enemy!\nLeaving combat...");
                map.player.gainExperience(enemy.maxHealth/3);
                map.player.inventory.gold += enemy.maxHealth/4;
                goldLabel.setText("Gold: " + map.player.inventory.gold);
                killedEnemies++;
                earnedGold += enemy.maxHealth/4;
                earnedExperience += enemy.maxHealth/3;
                character_is_busy = false;
                rightPanel.removeAll();
                rightPanel.revalidate();
                rightPanel.repaint();
                choiceLabel.setText("<html>Choose where to go:<br>Use the arrow keys ↑ ↓ → ←<br>to move.<html>");
                rightPanel.add(choiceLabel);
            }
            else
                takeEnemyTurn();
        }

        if(e.getSource().equals(selectAbilityButton)){
            Enemy enemy = (Enemy) map.current_cell.cellType;
            if (map.player.abilities.size() != 0) {
                rightPanel.removeAll();
                rightPanel.revalidate();
                rightPanel.repaint();
                rightPanel.setLayout(new GridLayout(map.player.abilities.size()+1, 2));
                for (Spell spell : map.player.abilities) {
                    JLabel spellLabel = new JLabel("<html><u>" + spell.getClass().getSimpleName()
                            + "</u><br>Mana Cost: " + spell.manaCost + "<br> Damage: " + spell.damage +  "</html>");
                    rightPanel.add(spellLabel);
                    JButton useButton = new JButton("Use");
                    useButton.addActionListener(f -> {
                        if(map.player.currentMana >= spell.manaCost) {
                            int before = enemy.currentHealth;
                            map.player.useAbility(spell, enemy);
                            if (enemy.currentHealth == before) {
                                if ((enemy.earthProtection && spell instanceof Earth)
                                        || (enemy.fireProtection && spell instanceof Fire)
                                        || (enemy.iceProtection && spell instanceof Ice))
                                    JOptionPane.showMessageDialog(this, "The enemy has "
                                            + spell.getClass().getSimpleName() + " resistance!");
                                else
                                    JOptionPane.showMessageDialog(this,
                                            "The enemy dodged your attack!");
                            } else {
                                JOptionPane.showMessageDialog(this,
                                        spell.getClass().getSimpleName()+ " spell cast hit for "
                                                + spell.damage + " HP!");
                            }
                            manaLabel.setText("MP: " + map.player.currentMana);
                            if(enemy.currentHealth <= 0){
                                JOptionPane.showMessageDialog(this
                                        , "You killed the enemy!\nLeaving combat...");
                                map.player.gainExperience(enemy.maxHealth/3);
                                map.player.inventory.gold += enemy.maxHealth/4;
                                goldLabel.setText("Gold: " + map.player.inventory.gold);
                                killedEnemies++;
                                earnedGold += enemy.maxHealth/4;
                                earnedExperience += enemy.maxHealth/3;
                                character_is_busy = false;
                                rightPanel.removeAll();
                                rightPanel.revalidate();
                                rightPanel.repaint();
                                choiceLabel.setText("<html>Choose where to go:" +
                                        "<br>Use the arrow keys ↑ ↓ → ←" +
                                        "<br>to move.<html>");
                                rightPanel.add(choiceLabel);
                            }
                            else
                                takeEnemyTurn();
                        }
                        else
                            JOptionPane.showMessageDialog(this, "Not enough mana!");
                    });
                    rightPanel.add(useButton);
                }
                JButton backButton = new JButton("Back");
                backButton.addActionListener(f->takePlayerTurn());
                rightPanel.add(backButton);
                rightPanel.add(new JPanel());
            } else
                JOptionPane.showMessageDialog(this, "Out of spells!");
        }

        if(e.getSource().equals(selectPotionButton)){
            if (map.player.inventory.potions.size() != 0) {
                rightPanel.removeAll();
                rightPanel.revalidate();
                rightPanel.repaint();
                rightPanel.setLayout(new GridLayout(map.player.inventory.potions.size()+1, 2));
                for (Potion potion : map.player.inventory.potions) {
                    JLabel potionLabel = new JLabel("<html><u>" + potion.getClass().getSimpleName()
                            + "</u><br>RegenValue: " + potion.getRegen() + "</html>");
                    rightPanel.add(potionLabel);
                    JButton useButton = new JButton("Use");
                    useButton.addActionListener(f -> {
                        potion.usePotion(map.player);
                        JOptionPane.showMessageDialog(this, "Regenerated " + potion.getRegen() +
                                " HP!");
                        healthLabel.setText("HP: " + map.player.currentHealth);
                        manaLabel.setText("MP: " + map.player.currentMana);
                        capacityLabel.setText("Capacity: " + map.player.inventory.returnQuantity());
                        takeEnemyTurn();
                    });
                    rightPanel.add(useButton);
                }
                JButton backButton = new JButton("Back");
                backButton.addActionListener(f->takePlayerTurn());
                rightPanel.add(backButton);
                rightPanel.add(new JPanel());
            } else
                JOptionPane.showMessageDialog(this, "Out of potions!");
        }
    }

    class MoveAction extends AbstractAction {
        String direction;
        public MoveAction(String direction) {
            this.direction = direction;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if(!character_is_busy) {
                switch (direction) {
                    case "north":
                        map.goNorth();
                        break;
                    case "south":
                        map.goSouth();
                        break;
                    case "east":
                        map.goEast();
                        break;
                    case "west":
                        map.goWest();
                        break;
                }
                String story = frame.game.showStory(map.current_cell);
                if (story == null)
                    story = " ";
                storyLabel.setText(story);
                positionLabel.setText("Your current cell is: " + map.player.x + ", " + map.player.y);
                revalidate();
                repaint();
                showOption();
            }
        }
    }

    void takePlayerTurn(){
        Enemy enemy = (Enemy) map.current_cell.cellType;
        if(enemy.currentHealth >= 0) {
            character_is_busy = true;
            rightPanel.removeAll();
            rightPanel.revalidate();
            rightPanel.repaint();
            rightPanel.setLayout(new GridLayout(6, 2));
            choiceLabel.setText("Your turn!");
            JLabel enemyHPLabel = new JLabel("Enemy's HP: " + enemy.currentHealth);
            JLabel enemyMPLabel = new JLabel("Enemy's MP: " + enemy.currentMana);
            JLabel basicAttackLabel = new JLabel("Use basic attack");
            JLabel useAbilityLabel = new JLabel("Use ability");
            JLabel usePotionLabel = new JLabel("Use potion");
            selectBasicButton = new JButton("Select");
            selectAbilityButton = new JButton("Select");
            selectPotionButton = new JButton("Select");
            selectBasicButton.addActionListener(this);
            selectAbilityButton.addActionListener(this);
            selectPotionButton.addActionListener(this);
            rightPanel.add(choiceLabel);
            rightPanel.add(new JPanel());
            rightPanel.add(enemyHPLabel);
            rightPanel.add(new JPanel());
            rightPanel.add(enemyMPLabel);
            rightPanel.add(new JPanel());
            rightPanel.add(basicAttackLabel);
            rightPanel.add(selectBasicButton);
            rightPanel.add(useAbilityLabel);
            rightPanel.add(selectAbilityButton);
            rightPanel.add(usePotionLabel);
            rightPanel.add(selectPotionButton);
        }
        else
            JOptionPane.showMessageDialog(this, "The enemy is dead!");
    }

    void takeEnemyTurn(){
        rightPanel.removeAll();
        rightPanel.revalidate();
        rightPanel.repaint();
        choiceLabel.setText("Enemy Turn.");
        rightPanel.add(choiceLabel);
        Enemy enemy = (Enemy) map.current_cell.cellType;
        int before = map.player.currentHealth;
        if (Math.random() < 0.5 && enemy.abilities.size() != 0) {
            Random randomAbility = new Random();
            Spell enemy_spell = enemy.abilities.get(randomAbility.nextInt(enemy.abilities.size()));
            if (enemy.currentMana >= enemy_spell.manaCost) {
                enemy.useAbility(enemy_spell, map.player);
                if (map.player.currentHealth == before)
                    JOptionPane.showMessageDialog(this, "The player has "
                            + enemy_spell.getClass().getSimpleName() + " resistance!");
                else
                    JOptionPane.showMessageDialog(this, "Enemy "
                            + enemy_spell.getClass().getSimpleName()  + " spell cast hit for "
                            + (before - map.player.currentHealth) + " HP!");
            }
            else {
                int damage = enemy.getDamage();
                map.player.receiveDamage(damage);
                if(damage == 10)
                    JOptionPane.showMessageDialog(this, "Normal hit on player for "
                            + (before - map.player.currentHealth) + " HP!");
                else
                    JOptionPane.showMessageDialog(this, "Critical hit on player for "
                            + (before - map.player.currentHealth) + " HP!");
            }
        }
        else {
            int damage = enemy.getDamage();
            map.player.receiveDamage(damage);
            if(damage == 10)
                JOptionPane.showMessageDialog(this, "Normal hit on player for "
                        + (before - map.player.currentHealth) + " HP!");
            else
                JOptionPane.showMessageDialog(this, "Critical hit on player for "
                        + (before - map.player.currentHealth) + " HP!");
        }
        rightPanel.removeAll();
        rightPanel.revalidate();
        rightPanel.repaint();
        rightPanel.setLayout(new GridLayout(4, 1));
        choiceLabel.setText("Enemy Turn.");
        rightPanel.add(choiceLabel);
        JLabel enemyHPLabel = new JLabel("Enemy's HP: " + enemy.currentHealth);
        JLabel enemyMPLabel = new JLabel("Enemy's MP: " + enemy.currentMana);
        rightPanel.add(enemyHPLabel);
        rightPanel.add(enemyMPLabel);
        JButton continueButton = new JButton("Continue");
        rightPanel.add(continueButton);
        healthLabel.setText("HP: " + map.player.currentHealth);
        if(map.player.currentHealth <= 0){
            JOptionPane.showMessageDialog(this, "You died!");
            System.exit(0);
        }
        continueButton.addActionListener(e-> takePlayerTurn());
    }

    void showOption() {
        System.out.println(map.height);
        if(map.current_cell.cellType.toCharacter() == 'N'){
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                    "north");
            getActionMap().put("north",new MoveAction("north"));
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                    "south");
            getActionMap().put("south",new MoveAction("south"));
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
                    "east");
            getActionMap().put("east",new MoveAction("east"));
            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
                    "west");
            getActionMap().put("west",new MoveAction("west"));
        }
        else if(map.current_cell.cellType.toCharacter() == 'S'){
            Shop shop = (Shop) map.current_cell.cellType;
            if(shop.potionList.size() != 0) {
                character_is_busy = true;
                rightPanel.removeAll();
                rightPanel.revalidate();
                rightPanel.repaint();
                rightPanel.setLayout(new GridLayout(3, 1));
                choiceLabel.setText("Look at the items in the shop?");
                yes = new JButton("Yes");
                no = new JButton("No");
                rightPanel.add(choiceLabel);
                rightPanel.add(yes);
                rightPanel.add(no);
                yes.addActionListener(this);
                no.addActionListener(this);
            }
            else
                JOptionPane.showMessageDialog(this, "The shop is empty!");
        }
        else if(map.current_cell.cellType.toCharacter() == 'E'){
            takePlayerTurn();
        }
        else if(map.current_cell.cellType.toCharacter() == 'F'){
            JOptionPane.showMessageDialog(this, "You Won!\nEarned: " + earnedGold + " Gold and "
                    + earnedExperience + " Exp.\n" + "Killed: " + killedEnemies + " enemies.\n"
                    + "Current level: " + map.player.level + ".");
            frame.selected_account.maps_completed++;
            System.exit(0);
        }
    }

    public class CellTableModel extends AbstractTableModel {
        Grid map;

        public CellTableModel(Grid map){
            this.map = map;
        }

        @Override
        public int getRowCount() {
            return map.width;
        }

        @Override
        public int getColumnCount() {
            return map.height;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Cell.class;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return map.get(rowIndex).get(columnIndex);
        }
    }

    public class CellRenderer extends JLabel implements TableCellRenderer {
        public CellRenderer(){
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Cell cell = (Cell) value;
            BufferedImage image_buf;
            if(cell == map.current_cell){
                if(map.current_cell.cellType.toCharacter() == 'N')
                    setBackground(Color.BLUE);
                else if(map.current_cell.cellType.toCharacter() == 'S')
                    setBackground(Color.YELLOW);
                else if(map.current_cell.cellType.toCharacter() == 'E')
                    setBackground(Color.RED);
                else if(map.current_cell.cellType.toCharacter() == 'F')
                    setBackground(Color.CYAN);
                try {
                    image_buf = loadImage(new File("src/unicons/user.svg"),60,60);
                    setIcon(new ImageIcon(image_buf));
                } catch (TranscoderException | FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else if(!cell.visited){
                setBackground(Color.GREEN);
                try {
                    image_buf = loadImage(new File("src/unicons/question.svg"),60,60);
                    setIcon(new ImageIcon(image_buf));
                } catch (TranscoderException | FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else {
                if(cell.cellType.toCharacter() == 'E'){
                    setBackground(Color.RED);
                    try {
                        image_buf = loadImage(new File("src/unicons/ninja.svg"),60,60);
                        setIcon(new ImageIcon(image_buf));
                    } catch (TranscoderException | FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else if(cell.cellType.toCharacter() == 'S'){
                    setBackground(Color.YELLOW);
                    try {
                        image_buf = loadImage(new File("src/unicons/store.svg"),60,60);
                        setIcon(new ImageIcon(image_buf));
                    } catch (TranscoderException | FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else if(cell.cellType.toCharacter() == 'F'){
                    setBackground(Color.CYAN);
                    try {
                        image_buf = loadImage(new File("src/unicons/medal.svg"),60,60);
                        setIcon(new ImageIcon(image_buf));
                    } catch (TranscoderException | FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else if(cell.cellType.toCharacter() == 'N'){
                    setBackground(Color.GRAY);
                    setIcon(null);
                }
            }
            return this;
        }
    }

    class BufferedImageTranscoder extends ImageTranscoder {
        @Override
        public BufferedImage createImage(int w, int h) {
            return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void writeImage(BufferedImage img, TranscoderOutput output) {
            this.img = img;
        }

        public BufferedImage getBufferedImage() {
            return img;
        }
        private BufferedImage img = null;
    }

    public BufferedImage loadImage(File svgFile, float width, float height) throws TranscoderException
            , FileNotFoundException {
        BufferedImageTranscoder imageTranscoder = new BufferedImageTranscoder();

        imageTranscoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width);
        imageTranscoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height);

        TranscoderInput input = new TranscoderInput(new FileInputStream(svgFile));
        imageTranscoder.transcode(input, null);

        return imageTranscoder.getBufferedImage();
    }
}