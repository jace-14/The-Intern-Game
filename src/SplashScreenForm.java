
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SplashScreenForm extends javax.swing.JFrame {

    /**
     * Creates new form SplashScreenForm
     */
    int counter = 0;
    
    public SplashScreenForm() {
        initComponents();
        
        Timer wait = new Timer();
        
        TimerTask task = new TimerTask(){
            @Override
            public void run(){
                counter++;
                if (counter == 3){
                    try {
                        GameStateForm game = new GameStateForm();
                        game.setVisible(true);
                        dispose();
                    } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | URISyntaxException ex) {
                        Logger.getLogger(SplashScreenForm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        
        wait.scheduleAtFixedRate(task, 0, 1000);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgIMG = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("LOADING...");
        setMaximumSize(new java.awt.Dimension(600, 500));
        setMinimumSize(new java.awt.Dimension(600, 500));
        setPreferredSize(new java.awt.Dimension(600, 500));
        setResizable(false);
        getContentPane().setLayout(null);

        bgIMG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/splashScreenSmall(600x500).png"))); // NOI18N
        bgIMG.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        getContentPane().add(bgIMG);
        bgIMG.setBounds(0, 0, 600, 500);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new SplashScreenForm().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bgIMG;
    // End of variables declaration//GEN-END:variables
}
