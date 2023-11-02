import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Hangman extends JFrame implements ActionListener {

    private final WordDB wordDB;
    private int incorrectGuesses;
    private String [] wordChallenge;
    private JLabel hangmanImage, categoryLabel, hiddenWordLabel, resultLabel, wordLabel;
    private JButton[] letterButtons;
    private JDialog resultDialog;
    private Font customFont;

    public Hangman(){
        super("The Hangman Game (JAVA)");
        setSize(CommonConstants.FRAME_SIZE );
        setDefaultCloseOperation(EXIT_ON_CLOSE );
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        getContentPane().setBackground(CommonConstants.BACKGROUND_COLOR);

        wordDB = new WordDB();
        letterButtons = new JButton[26];
        wordChallenge = wordDB.loadChallenge();
        customFont = CustomTools.createFont(CommonConstants.FONT_PATH);
        createResultDialog();
        addGUIComponents();
    }

    public void addGUIComponents(){

    hangmanImage = CustomTools.loadImage(CommonConstants.IMAGE_PATH);
    hangmanImage.setBounds(0,0,  hangmanImage.getPreferredSize().width, hangmanImage.getPreferredSize().height);

    //category display
    categoryLabel = new JLabel(wordChallenge[0]);
    categoryLabel.setFont(customFont.deriveFont(30f));
    categoryLabel.setHorizontalAlignment(SwingConstants.CENTER);
    categoryLabel.setOpaque(true);
    categoryLabel.setForeground(Color.WHITE);
    categoryLabel.setBackground(CommonConstants.SECONDARY_COLOR);
    categoryLabel.setBorder(BorderFactory.createLineBorder(CommonConstants.SECONDARY_COLOR));
    categoryLabel.setBounds(
            0,
            hangmanImage.getPreferredSize().height -28,
            CommonConstants.FRAME_SIZE.width,
            categoryLabel.getPreferredSize().height
    );

    //hidden words
    hiddenWordLabel =  new JLabel(CustomTools.hideWords(wordChallenge[1]));
    hiddenWordLabel.setFont(customFont.deriveFont(64f));
    hiddenWordLabel.setHorizontalAlignment(SwingConstants.CENTER);
    hiddenWordLabel.setForeground(Color.WHITE);
    hiddenWordLabel.setBounds(
            0,
            categoryLabel.getY() + categoryLabel.getPreferredSize().height + 50,
            CommonConstants.FRAME_SIZE.width,
            hiddenWordLabel.getPreferredSize().height
    );

    //letter buttons.

        GridLayout gridLayout = new GridLayout(4, 7);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(
                -5,
                hiddenWordLabel.getY() + hiddenWordLabel.getPreferredSize().height,
                CommonConstants.BUTTON_PANEL_SIZE.width,
                CommonConstants.BUTTON_PANEL_SIZE.height
        );


        buttonPanel.setLayout(gridLayout);

        //create the letter buttons
        for(char c = 'A'; c <= 'Z'; c++){
            JButton button = new JButton(Character.toString(c));
            button.setBackground(CommonConstants.PRIMARY_COLOR);
            button.setFont(customFont.deriveFont(22f));
            button.setForeground(Color.WHITE);
            button.addActionListener(this);

            //using ASCII values to calculate the current index
            int currentIndex = c - 'A';

            letterButtons[currentIndex] = button;
            buttonPanel.add(letterButtons[currentIndex]);
        }


        //reset button
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(customFont.deriveFont(22f));
        resetButton.setForeground(Color.WHITE);
        resetButton.setBackground(CommonConstants.SECONDARY_COLOR);
        resetButton.addActionListener(this);
        buttonPanel.add(resetButton);


        //quit button
        JButton quitButton = new JButton("Quit");
        quitButton.setFont(customFont.deriveFont(22f));
        quitButton.setForeground(Color.WHITE);
        quitButton.setBackground(CommonConstants.SECONDARY_COLOR);
        quitButton.addActionListener(this);
        buttonPanel.add(quitButton);







        getContentPane().add(categoryLabel);
        getContentPane().add(hangmanImage);
        getContentPane().add(hiddenWordLabel);
        getContentPane().add(buttonPanel);




    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(command.equals("Reset") || command.equals("Restart")){
            resetGame();

            if(command.equals("Restart")) {
                resultLabel.setFont(customFont.deriveFont(16f));
                resultDialog.setVisible(false);
            }

        }else if(command.equals("Quit")){
            dispose();
            return;
        }else {
            //letter buttons

            //disable buttons
            JButton button = (JButton) e.getSource();
            button.setEnabled(false);

            //check if the user's guess is in the word.
            if(wordChallenge[1].contains(command)){

                //show/indicate that the guess from the user was correct.
                button.setBackground(Color.GREEN);

                //store the hidden words in a char array.
                char[] hiddenWord = hiddenWordLabel.getText().toCharArray();

                for(int i = 0; i < wordChallenge[1].length(); i++){
                    //update to the correct letter.

                    if(wordChallenge[1].charAt(i) == command.charAt(0)){
                        hiddenWord[i] = command.charAt(0);
                    }
                }
                //update hiddenWordLabel
                hiddenWordLabel.setText(String.valueOf(hiddenWord));

                //the user guessed the word right.
                if(!hiddenWordLabel.getText().contains("*")){

                    //display the dialog containing the correct results.
                    resultLabel.setText("Yep! You are a champion.");
                    resultLabel.setFont(customFont.deriveFont(16f));
                    resultDialog.setVisible(true);

                }

            }else{
                //show that the user picked the wrong letter.
                button.setBackground(Color.RED);

                //increase the number of incorrect attempts.
                ++incorrectGuesses;

                //update the hangman image.
                CustomTools.updateImage(hangmanImage, "resources/" + (incorrectGuesses + 1) + ".png");

                //user failed to guess the word correctly.
                if(incorrectGuesses >= 6) {
                    //display result dialog with game over label.
                    resultLabel.setText("Opies..Try again.");
                    resultLabel.setFont(customFont.deriveFont(16f));
                    resultDialog.setVisible(true);

                }
            }

            wordLabel.setText("Word: " + wordChallenge[1]);

        }

    }

    private void createResultDialog() {
        resultDialog = new JDialog();
        resultDialog.setTitle("Outcome");
        resultDialog.setFont(customFont.deriveFont(16f));
        resultDialog.setSize(CommonConstants.RESULT_DIALOG_SIZE);
        resultDialog.getContentPane().setBackground(CommonConstants.BACKGROUND_COLOR);
        resultDialog.setResizable(false);
        resultDialog.setLocationRelativeTo(this);
        resultDialog.setLayout(new GridLayout(3, 1 ));
        resultDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                resetGame();
            }
        });

        resultLabel = new JLabel();
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        wordLabel = new JLabel();
        wordLabel.setForeground(Color.WHITE);
        wordLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JButton restartButton = new JButton("Restart");
        restartButton.setFont(customFont.deriveFont(16f));
        restartButton.setForeground(Color.WHITE);
        restartButton.setBackground(CommonConstants.SECONDARY_COLOR);
        restartButton.addActionListener(this);

        resultDialog.add(resultLabel);
        resultDialog.add(wordLabel);
        resultDialog.add(restartButton);

    }

    private void resetGame(){
        //load new challange
        wordChallenge = wordDB.loadChallenge();
        incorrectGuesses= 0;

        //load starting image
        CustomTools.updateImage(hangmanImage, CommonConstants.IMAGE_PATH);

        //update category
        categoryLabel.setText(wordChallenge[0]);

        //update hiddenWord
        String hiddenWord = CustomTools.hideWords(wordChallenge[1]);
        hiddenWordLabel.setText(hiddenWord);

        //enable the buttons, all of them.
        for(int i = 0; i < letterButtons.length; i++) {
            letterButtons[i].setEnabled(true);
            letterButtons[i].setBackground(CommonConstants.PRIMARY_COLOR);
        }
    }












}
