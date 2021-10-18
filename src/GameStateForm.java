
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public final class GameStateForm extends javax.swing.JFrame {

    boolean clicked = false;

    String[] job = {
        "Intern",
        "Contract Staff",
        "Full Time Staff",
        "Assistant Project Leader",
        "Project Leader",
        "Assistant Project Manager",
        "Project Manager",
        "Senior Manager",
        "Managing Director",
        "Senior Managing Director",
        "Management Information Systems Director", //Management Information Systems (MIS) Director
        "Information Technology Director", //Information Technology (IT) Director
        "Chief Information Officer (CIO)",
        "Chief Technical Officer (CTO)",
        "Chief Operating Officer (COO)",
        "Chief Executive Officer (CEO)"
    };

    FileInputStream file;
    XSSFWorkbook wb;
    XSSFSheet sheet;
    XSSFRow selectedRow;
    ArrayList<Integer> list, chosenQuestions;
    String[] selectedRowValues;
    int totalRows, totalQuestions, totalColumns, number, currentQuestion, terminationDefianceCount = 0, techSupportCount = 0;
    boolean terminationDefianceAddedForLevel5 = false, techSupportAddedForLevel5 = false, terminationDefianceAddedForLevel10 = false, techSupportAddedForLevel10 = false;

    AudioInputStream audioInputStream;
    Clip backgroundMusic = AudioSystem.getClip(),
            correctAnswer = AudioSystem.getClip(),
            wrongAnswer = AudioSystem.getClip();

    /**
     * Creates new form GameStateForm
     * @throws java.io.FileNotFoundException
     * @throws javax.sound.sampled.LineUnavailableException
     * @throws javax.sound.sampled.UnsupportedAudioFileException
     * @throws java.net.URISyntaxException
     */
    public GameStateForm() throws FileNotFoundException, IOException, LineUnavailableException, UnsupportedAudioFileException, URISyntaxException {
        initComponents();

        audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/typing.wav"));
        backgroundMusic.open(audioInputStream);
        backgroundMusic();

//        URL in = this.getClass().getResource("/excel/tempQuestions.xlsx");
//        file = new FileInputStream(new File(in.toURI()));

        URL in = this.getClass().getResource("/excel/tempQuestions.xlsx");
        InputStream stream = in.openStream();

        //create workbook instance that refers to .xlsx file
        wb = new XSSFWorkbook(stream);

        //create a sheet object to retrieve the sheet
        sheet = wb.getSheetAt(0);

        totalRows = sheet.getLastRowNum();
        totalQuestions = totalRows + 1;
        totalColumns = 8; //preset of 8 columns only in excel file namely       'Question Number', 'Topic', 'Question', 'Choice A', 'Choice B', 'Choice C', 'Choice D', 'Correct Answer'

        //make the list of numbers corresponding to how many questions there are in the excel file
        list = makeListOfAllQuestions(totalRows);

        //choose 15 random questions from the list
        chosenQuestions = choose15RandomQuestions(list);

        currentQuestion = 0;

        //update values into game state frame
        updateFrame(currentQuestion);
    }

    public void backgroundMusic() throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        if (currentQuestion <= 4) {
            backgroundMusic.close();
            audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/typing.wav"));
            backgroundMusic.open(audioInputStream);
        } else if (currentQuestion <= 9) {
            backgroundMusic.close();
            audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/bass.wav"));
            backgroundMusic.open(audioInputStream);
        } else if (currentQuestion == 14) {
            backgroundMusic.close();
            audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/alarming.wav"));
            backgroundMusic.open(audioInputStream);
        }

        if (clicked == false) {
            backgroundMusic.start();
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } else if (clicked == true) {
            backgroundMusic.stop();
        }
    }

    public ArrayList<Integer> makeListOfAllQuestions(int size) {
        ArrayList<Integer> listt = new ArrayList<>();

        //create list of numbers corresponding to each row (question)
        for (int i = 0; i <= size; i++) {
            listt.add(i);
        }

        return listt;
    }

    //choose 15 random numbers (these represents the chosen questions) and store into a list
    public ArrayList<Integer> choose15RandomQuestions(ArrayList<Integer> list) {
        ArrayList<Integer> chosenQuestionss = new ArrayList<>();
        Random r = new Random();

        for (int i = 0; i < 15; i++) {
            int randomNumber = list.get(r.nextInt(list.size()));
            removeNumberFromList(list, randomNumber);
            chosenQuestionss.add(randomNumber);
        }

        return chosenQuestionss;
    }

    //get the values of the selected row from the excel sheet
    public XSSFRow getRowValues(XSSFSheet sheet, int number) {
        XSSFRow chosenRow = sheet.getRow(number);

        return chosenRow;
    }

    //store row values into an array
    public String[] storeRowValues(XSSFRow row) {
        String[] values = new String[8];
        Iterator<Cell> cellItr = row.iterator();

        for (int i = 0; cellItr.hasNext(); i++) {
            values[i] = String.valueOf(cellItr.next());
        }

        return values;
    }

    //select a random number from the list of numbers
    public int getRandomNumberFromList(ArrayList<Integer> list) {
        Random r = new Random();
        int randomRowNumber = list.get(r.nextInt(list.size()));

        return randomRowNumber;
    }

    //remove selected number from the list of numbers
    public void removeNumberFromList(ArrayList<Integer> list, int number) {
        int index = list.indexOf(number);
        list.remove(index);
    }

    //update the questions and choices in frame
    public void updateFrame(int currentQuestion) throws UnsupportedAudioFileException, IOException, LineUnavailableException {

        if (currentQuestion > 14) {
            backgroundMusic.stop();
            this.dispose();
        }

        //enable all buttons
        aButton.setEnabled(true);
        bButton.setEnabled(true);
        cButton.setEnabled(true);
        dButton.setEnabled(true);

        //get row values of current question
        selectedRow = getRowValues(sheet, chosenQuestions.get(currentQuestion));

        //store row values into array
        selectedRowValues = storeRowValues(selectedRow);

        //if player reaches level 5, add 1 charge to termination defiance and tech support
        if (currentQuestion == 4 && !terminationDefianceAddedForLevel5 && !techSupportAddedForLevel5) {
            terminationDefianceCount++;
            techSupportCount++;
            terminationDefianceAddedForLevel5 = true;
            techSupportAddedForLevel5 = true;
            JOptionPane.showMessageDialog(this, "Checkpoint saved!");
        }

        //if player reaches level 10, add 1 charge to termination defiance and tech support
        if (currentQuestion == 9 && !terminationDefianceAddedForLevel10 && !techSupportAddedForLevel10) {
            terminationDefianceCount++;
            techSupportCount++;
            terminationDefianceAddedForLevel10 = true;
            techSupportAddedForLevel10 = true;
            JOptionPane.showMessageDialog(this, "Checkpoint saved!");
        }

        //if player reaches level 15 (last level), disable all tools
        if (currentQuestion == 14) {
            //disable termination defiance and tech support
            terminationDefianceCount = 0;
            techSupportCount = 0;
            JOptionPane.showMessageDialog(this, "FINAL QUESTION! \nAll termination defiances and tech supports are now disabled! \nGOOD LUCK!");
        }

        if (currentQuestion > 14) {
            backgroundMusic.stop();
            this.dispose();
        }

        backgroundMusic();

        //reflect values into gui
        level.setText("<html> LEVEL " + (currentQuestion + 1) + "</html>");
        questionArea.setText("<html>" + selectedRowValues[2] + "</html>");
        aButton.setText("<html>" + selectedRowValues[3] + "</html>");
        bButton.setText("<html>" + selectedRowValues[4] + "</html>");
        cButton.setText("<html>" + selectedRowValues[5] + "</html>");
        dButton.setText("<html>" + selectedRowValues[6] + "</html>");
        terminationDefianceCounter.setText("<html>" + terminationDefianceCount + "</html>");
        currentJobLabel.setText("<html>" + job[currentQuestion] + "</html>");
        nextJobLabel.setText("<html>" + job[currentQuestion + 1] + "</html>");
        techSupportCounter.setText("<html>" + techSupportCount + "</html>");

        if (terminationDefianceCount <= 0) {
            terminationDefianceLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/noFire(60x60).png")));
        } else if (terminationDefianceCount > 0) {
            terminationDefianceLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/fire(60x60).png")));
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        currentJob = new javax.swing.JLabel();
        currentJobLabel = new javax.swing.JLabel();
        arrowBG = new javax.swing.JLabel();
        nextJob = new javax.swing.JLabel();
        nextJobLabel = new javax.swing.JLabel();
        arrowBG1 = new javax.swing.JLabel();
        techSupportCounter = new javax.swing.JLabel();
        techSupportButton = new javax.swing.JButton();
        techSupport = new javax.swing.JLabel();
        terminationDefianceCounter = new javax.swing.JLabel();
        terminationDefianceLabel = new javax.swing.JLabel();
        terminationDefiance = new javax.swing.JLabel();
        questionArea = new javax.swing.JLabel();
        aButton = new javax.swing.JButton();
        bButton = new javax.swing.JButton();
        cButton = new javax.swing.JButton();
        dButton = new javax.swing.JButton();
        audio = new javax.swing.JButton();
        level = new javax.swing.JLabel();
        bgIMG = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CAN YOU WIN?");
        setPreferredSize(new java.awt.Dimension(700, 635));
        setResizable(false);
        getContentPane().setLayout(null);

        currentJob.setFont(new java.awt.Font("Verdana", 1, 16)); // NOI18N
        currentJob.setText("CURRENT JOB:");
        getContentPane().add(currentJob);
        currentJob.setBounds(200, 30, 150, 40);

        currentJobLabel.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        currentJobLabel.setForeground(new java.awt.Color(255, 255, 255));
        currentJobLabel.setText("Management Information Systems Director");
        currentJobLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        currentJobLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        getContentPane().add(currentJobLabel);
        currentJobLabel.setBounds(350, 20, 360, 60);

        arrowBG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/arrow(50x50).png"))); // NOI18N
        getContentPane().add(arrowBG);
        arrowBG.setBounds(150, 70, 50, 50);

        nextJob.setFont(new java.awt.Font("Verdana", 1, 16)); // NOI18N
        nextJob.setText("NEXT JOB:");
        getContentPane().add(nextJob);
        nextJob.setBounds(240, 70, 110, 60);

        nextJobLabel.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        nextJobLabel.setForeground(new java.awt.Color(255, 255, 255));
        nextJobLabel.setText("Management Information Systems Director");
        nextJobLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        nextJobLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        getContentPane().add(nextJobLabel);
        nextJobLabel.setBounds(350, 80, 350, 40);

        arrowBG1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/arrow(50x50).png"))); // NOI18N
        getContentPane().add(arrowBG1);
        arrowBG1.setBounds(150, 20, 50, 50);

        techSupportCounter.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        techSupportCounter.setText("##");
        getContentPane().add(techSupportCounter);
        techSupportCounter.setBounds(670, 180, 30, 20);

        techSupportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/techSupportSmall(60x60).png"))); // NOI18N
        techSupportButton.setBorderPainted(false);
        techSupportButton.setMaximumSize(new java.awt.Dimension(30, 30));
        techSupportButton.setPreferredSize(new java.awt.Dimension(30, 30));
        techSupportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                techSupportButtonActionPerformed(evt);
            }
        });
        getContentPane().add(techSupportButton);
        techSupportButton.setBounds(610, 190, 60, 60);

        techSupport.setFont(new java.awt.Font("Verdana", 1, 15)); // NOI18N
        techSupport.setForeground(new java.awt.Color(255, 255, 255));
        techSupport.setText("TECH SUPPORT");
        getContentPane().add(techSupport);
        techSupport.setBounds(470, 200, 150, 30);

        terminationDefianceCounter.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        terminationDefianceCounter.setText("##");
        getContentPane().add(terminationDefianceCounter);
        terminationDefianceCounter.setBounds(430, 180, 30, 20);

        terminationDefianceLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/fire(60x60).png"))); // NOI18N
        getContentPane().add(terminationDefianceLabel);
        terminationDefianceLabel.setBounds(380, 190, 60, 60);

        terminationDefiance.setFont(new java.awt.Font("Verdana", 1, 15)); // NOI18N
        terminationDefiance.setForeground(new java.awt.Color(255, 255, 255));
        terminationDefiance.setText("TERMINATION DEFIANCE");
        getContentPane().add(terminationDefiance);
        terminationDefiance.setBounds(160, 200, 230, 30);

        questionArea.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        questionArea.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        questionArea.setText("TEMP");
        questionArea.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        questionArea.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        getContentPane().add(questionArea);
        questionArea.setBounds(30, 250, 640, 150);

        aButton.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        aButton.setText("TEMP A");
        aButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        aButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aButtonActionPerformed(evt);
            }
        });
        getContentPane().add(aButton);
        aButton.setBounds(30, 410, 310, 60);

        bButton.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        bButton.setText("TEMP B");
        bButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bButtonActionPerformed(evt);
            }
        });
        getContentPane().add(bButton);
        bButton.setBounds(30, 480, 310, 60);

        cButton.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        cButton.setText("TEMP C");
        cButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cButtonActionPerformed(evt);
            }
        });
        getContentPane().add(cButton);
        cButton.setBounds(360, 410, 310, 60);

        dButton.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        dButton.setText("TEMP D");
        dButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        dButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dButtonActionPerformed(evt);
            }
        });
        getContentPane().add(dButton);
        dButton.setBounds(360, 480, 310, 60);

        audio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/soundOn.jpg"))); // NOI18N
        audio.setPreferredSize(new java.awt.Dimension(45, 45));
        audio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                audioActionPerformed(evt);
            }
        });
        getContentPane().add(audio);
        audio.setBounds(10, 550, 45, 45);

        level.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
        level.setForeground(new java.awt.Color(240, 240, 240));
        level.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        level.setText("LEVEL 1");
        getContentPane().add(level);
        level.setBounds(560, 10, 130, 20);

        bgIMG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/gameStateFormSmall(700x600).png"))); // NOI18N
        getContentPane().add(bgIMG);
        bgIMG.setBounds(0, 0, 700, 600);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void techSupportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_techSupportButtonActionPerformed
        String A = selectedRowValues[3],
                B = selectedRowValues[4],
                C = selectedRowValues[5],
                D = selectedRowValues[6];

        if (techSupportCount == 0) { //if tech support charge is 0
            JOptionPane.showMessageDialog(this, "Tech Support not available!");
        } else { //if there is tech support charge available
            //show answer by highlighting it, show change in color in button or smth
            //reduce charge by 1

            if (A.equals(selectedRowValues[7])) { //if correct answer is choice a, disable the other choices
                bButton.setEnabled(false);
                cButton.setEnabled(false);
                dButton.setEnabled(false);
            }
            if (B.equals(selectedRowValues[7])) { //if correct answer is choice b, disable the other choices
                aButton.setEnabled(false);
                cButton.setEnabled(false);
                dButton.setEnabled(false);
            }
            if (C.equals(selectedRowValues[7])) { //if correct answer is choice c, disable the other choices
                aButton.setEnabled(false);
                bButton.setEnabled(false);
                dButton.setEnabled(false);
            }
            if (D.equals(selectedRowValues[7])) { //if correct answer is choice d, disable the other choices
                aButton.setEnabled(false);
                bButton.setEnabled(false);
                cButton.setEnabled(false);
            }

            techSupportCount--;
        }
    }//GEN-LAST:event_techSupportButtonActionPerformed

    private void audioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_audioActionPerformed
        // TODO add your handling code here:
        if (clicked) {
            try {
                audio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/soundOn.jpg")));
                clicked = false;
                backgroundMusic();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                audio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/soundOff.jpg")));
                clicked = true;
                backgroundMusic();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_audioActionPerformed

    private void aButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aButtonActionPerformed
        // TODO add your handling code here:
        String answer = selectedRowValues[3];

        //if player gets correct answer
        if (answer.equals(selectedRowValues[7])) {
            if (currentQuestion == 14) {
                try {
                    currentQuestion++;
                    audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/correctAnswer.wav"));
                    correctAnswer.open(audioInputStream);
                    correctAnswer.start();
                    JOptionPane.showMessageDialog(this, "CONGRATULATIONS! YOU WON! \nYou are now CEO of THE COMPANY! GREAT WORK! \nPress OK to return to the main menu.");
                    HomePageForm home = new HomePageForm();
                    backgroundMusic.stop();
                    home.setVisible(true);
                    this.dispose();
                } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                    Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    //play correct sound effect
                    audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/correctAnswer.wav"));
                    correctAnswer.open(audioInputStream);
                    correctAnswer.start();
                    JOptionPane.showMessageDialog(this, "You are correct! \nProceed to the next question!");
                    currentQuestion++;
                } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
                    Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                }

                correctAnswer.close();
            }
            try {
                updateFrame(currentQuestion);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                //if player gets it wrong
                //play wrong sound effect
                audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/wrongAnswer.wav"));
                wrongAnswer.open(audioInputStream);
                wrongAnswer.start();
                JOptionPane.showMessageDialog(this, "Oops! You got it wrong!");
                if (terminationDefianceCount != 0) { //if there is a charge in termination defiance
                    if (currentQuestion >= 4 && currentQuestion < 9) { //check if player is between level 5 and level 9
                        //return player to level 5
                        currentQuestion = 4;
                        terminationDefianceCount--;
                        JOptionPane.showMessageDialog(this, "Luckily, you still have a Termination Defiance Charge! \nProceed to last checkpoint!");
                        try {
                            updateFrame(currentQuestion);
                        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                            Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (currentQuestion >= 9 && currentQuestion < 14) { //check if player is between level 10 and level 14
                        //return player to level 10
                        currentQuestion = 9;
                        terminationDefianceCount--;
                        JOptionPane.showMessageDialog(this, "Luckily, you still have a Termination Defiance Charge! \nProceed to last checkpoint!");
                        try {
                            updateFrame(currentQuestion);
                        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                            Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    try {
                        //if there is no charge in termination defiance (0)
                        JOptionPane.showMessageDialog(this, "GAME OVER! \nYOU ARE FIRED! \nPress OK to go back to the Main Menu.");
                        HomePageForm home = new HomePageForm();
                        home.setVisible(true);
                        backgroundMusic.stop();
                        this.dispose();
                    } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                        Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
            }

            wrongAnswer.close();
        }
    }//GEN-LAST:event_aButtonActionPerformed

    private void bButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bButtonActionPerformed
        // TODO add your handling code here:
        String answer = selectedRowValues[4];

        //if player gets correct answer
        if (answer.equals(selectedRowValues[7])) {
            if (currentQuestion == 14) {
                try {
                    currentQuestion++;
                    audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/correctAnswer.wav"));
                    correctAnswer.open(audioInputStream);
                    correctAnswer.start();
                    JOptionPane.showMessageDialog(this, "CONGRATULATIONS! YOU WON! \nYou are now CEO of THE COMPANY! GREAT WORK! \nPress OK to return to the main menu.");
                    HomePageForm home = new HomePageForm();
                    backgroundMusic.stop();
                    home.setVisible(true);
                    this.dispose();
                } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                    Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    //play correct sound effect
                    audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/correctAnswer.wav"));
                    correctAnswer.open(audioInputStream);
                    correctAnswer.start();

                    JOptionPane.showMessageDialog(this, "You are correct! \nProceed to the next question!");
                    currentQuestion++;
                } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
                    Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                }

                correctAnswer.close();
            }
            try {
                updateFrame(currentQuestion);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                //if player gets it wrong
                audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/wrongAnswer.wav"));
                wrongAnswer.open(audioInputStream);
                wrongAnswer.start();
                JOptionPane.showMessageDialog(this, "Oops! You got it wrong!");
                if (terminationDefianceCount != 0) { //if there is a charge in termination defiance
                    if (currentQuestion >= 4 && currentQuestion < 9) { //check if player is between level 5 and level 9
                        //return player to level 5
                        currentQuestion = 4;
                        terminationDefianceCount--;
                        JOptionPane.showMessageDialog(this, "Luckily, you still have a Termination Defiance Charge! \nProceed to last checkpoint!");
                        try {
                            updateFrame(currentQuestion);
                        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                            Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (currentQuestion >= 9 && currentQuestion < 14) { //check if player is between level 10 and level 14
                        //return player to level 10
                        currentQuestion = 9;
                        terminationDefianceCount--;
                        JOptionPane.showMessageDialog(this, "Luckily, you still have a Termination Defiance Charge! \nProceed to last checkpoint!");
                        try {
                            updateFrame(currentQuestion);
                        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                            Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    try {
                        //if there is no charge in termination defiance (0)
                        JOptionPane.showMessageDialog(this, "GAME OVER! \nYOU ARE FIRED! \nPress OK to go back to the Main Menu.");
                        HomePageForm home = new HomePageForm();
                        home.setVisible(true);
                        backgroundMusic.stop();
                        this.dispose();
                    } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                        Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
            }

            wrongAnswer.close();
        }
    }//GEN-LAST:event_bButtonActionPerformed

    private void cButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cButtonActionPerformed
        // TODO add your handling code here:
        String answer = selectedRowValues[5];

        //if player gets correct answer
        if (answer.equals(selectedRowValues[7])) {
            if (currentQuestion == 14) {
                try {
                    currentQuestion++;
                    audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/correctAnswer.wav"));
                    correctAnswer.open(audioInputStream);
                    correctAnswer.start();
                    JOptionPane.showMessageDialog(this, "CONGRATULATIONS! YOU WON! \nYou are now CEO of THE COMPANY! GREAT WORK! \nPress OK to return to the main menu.");
                    HomePageForm home = new HomePageForm();
                    backgroundMusic.stop();
                    home.setVisible(true);
                    this.dispose();
                } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                    Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    //play correct sound effect
                    audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/correctAnswer.wav"));
                    correctAnswer.open(audioInputStream);
                    correctAnswer.start();
                    JOptionPane.showMessageDialog(this, "You are correct! \nProceed to the next question!");
                    currentQuestion++;
                } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
                    Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                }

                correctAnswer.close();
            }
            try {
                updateFrame(currentQuestion);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                //if player gets it wrong
                audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/wrongAnswer.wav"));
                wrongAnswer.open(audioInputStream);
                wrongAnswer.start();
                JOptionPane.showMessageDialog(this, "Oops! You got it wrong!");
                if (terminationDefianceCount != 0) { //if there is a charge in termination defiance
                    if (currentQuestion >= 4 && currentQuestion < 9) { //check if player is between level 5 and level 9
                        //return player to level 5
                        currentQuestion = 4;
                        terminationDefianceCount--;
                        JOptionPane.showMessageDialog(this, "Luckily, you still have a Termination Defiance Charge! \nProceed to last checkpoint!");
                        try {
                            updateFrame(currentQuestion);
                        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                            Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (currentQuestion >= 9 && currentQuestion < 14) { //check if player is between level 10 and level 14
                        //return player to level 10
                        currentQuestion = 9;
                        terminationDefianceCount--;
                        JOptionPane.showMessageDialog(this, "Luckily, you still have a Termination Defiance Charge! \nProceed to last checkpoint!");
                        try {
                            updateFrame(currentQuestion);
                        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                            Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    try {
                        //if there is no charge in termination defiance (0)
                        JOptionPane.showMessageDialog(this, "GAME OVER! \nYOU ARE FIRED! \nPress OK to go back to the Main Menu.");
                        HomePageForm home = new HomePageForm();
                        home.setVisible(true);
                        backgroundMusic.stop();
                        this.dispose();
                    } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                        Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
            }

            wrongAnswer.close();
        }
    }//GEN-LAST:event_cButtonActionPerformed

    private void dButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dButtonActionPerformed
        // TODO add your handling code here:
        String answer = selectedRowValues[6];

        //if player gets correct answer
        if (answer.equals(selectedRowValues[7])) {
            if (currentQuestion == 14) {
                try {
                    currentQuestion++;
                    audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/correctAnswer.wav"));
                    correctAnswer.open(audioInputStream);
                    correctAnswer.start();
                    JOptionPane.showMessageDialog(this, "CONGRATULATIONS! YOU WON! \nYou are now CEO of THE COMPANY! GREAT WORK! \nPress OK to return to the main menu.");
                    HomePageForm home = new HomePageForm();
                    backgroundMusic.stop();
                    home.setVisible(true);
                    this.dispose();
                } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                    Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                try {
                    //play correct sound effect
                    audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/correctAnswer.wav"));
                    correctAnswer.open(audioInputStream);
                    correctAnswer.start();
                    JOptionPane.showMessageDialog(this, "You are correct! \nProceed to the next question!");
                    currentQuestion++;
                } catch (LineUnavailableException | UnsupportedAudioFileException | IOException ex) {
                    Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                }
                correctAnswer.close();
            }
            try {
                updateFrame(currentQuestion);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                //if player gets it wrong
                audioInputStream = AudioSystem.getAudioInputStream(getClass().getResource("/audio/wrongAnswer.wav"));
                wrongAnswer.open(audioInputStream);
                wrongAnswer.start();

                JOptionPane.showMessageDialog(this, "Oops! You got it wrong!");
                if (terminationDefianceCount != 0) { //if there is a charge in termination defiance
                    if (currentQuestion >= 4 && currentQuestion < 9) { //check if player is between level 5 and level 9
                        //return player to level 5
                        currentQuestion = 4;
                        terminationDefianceCount--;
                        JOptionPane.showMessageDialog(this, "Luckily, you still have a Termination Defiance Charge! \nProceed to last checkpoint!");
                        try {
                            updateFrame(currentQuestion);
                        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                            Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (currentQuestion >= 9 && currentQuestion < 14) { //check if player is between level 10 and level 14
                        //return player to level 10
                        currentQuestion = 9;
                        terminationDefianceCount--;
                        JOptionPane.showMessageDialog(this, "Luckily, you still have a Termination Defiance Charge! \nProceed to last checkpoint!");
                        try {
                            updateFrame(currentQuestion);
                        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                            Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    try {
                        //if there is no charge in termination defiance (0)
                        JOptionPane.showMessageDialog(this, "GAME OVER! \nYOU ARE FIRED! \nPress OK to go back to the Main Menu.");
                        HomePageForm home = new HomePageForm();
                        home.setVisible(true);
                        backgroundMusic.stop();
                        this.dispose();
                    } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                        Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (LineUnavailableException | IOException | UnsupportedAudioFileException ex) {
                Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
            }
            wrongAnswer.close();
        }
    }//GEN-LAST:event_dButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new GameStateForm().setVisible(true);
            } catch (IOException | LineUnavailableException | UnsupportedAudioFileException | URISyntaxException ex) {
                Logger.getLogger(GameStateForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aButton;
    private javax.swing.JLabel arrowBG;
    private javax.swing.JLabel arrowBG1;
    private javax.swing.JButton audio;
    private javax.swing.JButton bButton;
    private javax.swing.JLabel bgIMG;
    private javax.swing.JButton cButton;
    private javax.swing.JLabel currentJob;
    private javax.swing.JLabel currentJobLabel;
    private javax.swing.JButton dButton;
    private javax.swing.JLabel level;
    private javax.swing.JLabel nextJob;
    private javax.swing.JLabel nextJobLabel;
    private javax.swing.JLabel questionArea;
    private javax.swing.JLabel techSupport;
    private javax.swing.JButton techSupportButton;
    private javax.swing.JLabel techSupportCounter;
    private javax.swing.JLabel terminationDefiance;
    private javax.swing.JLabel terminationDefianceCounter;
    private javax.swing.JLabel terminationDefianceLabel;
    // End of variables declaration//GEN-END:variables
}
