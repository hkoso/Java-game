/*
 * READ ME:
 * This is a small shooting game with simulation of trajectory affected by the gravity
 *
 * How to play:
 * Use up and down arrows to increase or decrease the power of the artillery
 * Use left and right arrows to adjust the angle of the artillery
 * Press r to reload bullet
 * Press t to restart game
 * Press enter or space to fire
 */


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

// launch gui component and the detail of the game
public class Shooting extends JFrame{
    // Screen Size
    private final static int X = 900;
    private final static int Y = 500;

    private static Scene game = new Scene();
    public Shooting() {
        add(game);
        game.setFocusable(true);

        // key listener to each button
        game.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()){
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_SPACE:
                        game.fire();
                        break;
                    case KeyEvent.VK_UP:
                        game.powerUp();
                        break;
                    case KeyEvent.VK_DOWN:
                        game.powerDown();
                        break;
                    case KeyEvent.VK_LEFT:
                        game.liftArtillery();
                        break;
                    case KeyEvent.VK_RIGHT:
                        game.lowerArtillery();
                        break;
                }
                if(e.getKeyChar() == 'r'){
                    game.reload();
                }
            }

            public void keyTyped(KeyEvent e){
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ENTER:
                    case KeyEvent.VK_SPACE:
                        game.fire();
                        break;
                }
                if(e.getKeyChar() == 't'){
                    game.restartCommand();
                }
            }
        });

    }

    public static void main(String[] args) {
        Shooting frame = new Shooting();
        frame.setTitle("Game");
        frame.setSize(X, Y);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

}

class Scene extends JPanel {
    private final static int HORIZONTAL_ADJUSTMENT = 20;

    private final static double TARGET_NUM = 10;

    private boolean restart;
    private boolean bulletLaunched;
    private double speed;
    private int artilleryAngle;
    private double bulletX;
    private double bulletY;
    private double bulletDisplacementX;
    private double bulletDisplacementY;

    private int bulletCount = 0;

    private int[] targetStatus = new int[10];
    private int[] x = new int[10];
    private int[] y = new int[10];

    private Timer timer;

    private final static int DIAMETER = 15;
    private final static double GRAVITY = 0.0098;
    private Tool tool = new Tool();
    public Scene(){
        initial();
        timer.start();
    }

    // Purpose: simulating the trajectory with animation based on data given
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        drawArtillery(g);

        // Display the power of the artillery
        g.drawString("Bullet Speed: " + ((int)(speed * 80) - 220), 30, 400);

        // Display the remaining targets
        for(int i = 0; i < 10; i++){
            if(targetStatus[i] == 0) {
                g.drawOval(x[i], y[i], DIAMETER, DIAMETER);
            }
        }

        // Display the bullet
        g.fillOval((int)bulletX, (int)bulletY, 10, 10);
        if(!bulletLaunched) {
            bulletDisplacementX = (speed * Math.cos(artilleryAngle * Math.PI / 180));
            bulletDisplacementY = (speed * Math.sin(artilleryAngle * Math.PI / 180));
        }

        // Physical simulator after the bullet is shot
        if(bulletLaunched) {
            physics();
        }

        // Automatically reload if the bullet is out of the screen.
        // (only below the lower side and right of the right side)
        if(bulletY > getHeight() || bulletX > getWidth()){
            reload();
        }

        // Enable hit effect
        hit();

        // Display the result if all target is shot
        if(isAllShot()){
            g.setFont(new Font("Times", Font.BOLD, 20));
            g.drawString("You Win!\n",
                    getWidth() / 2 - 45, getHeight() / 2 - HORIZONTAL_ADJUSTMENT);

            g.drawString("Target Amount: " + 10, getWidth() / 2 - 80, getHeight() / 2);

            g.drawString("Fired Bullets: " + bulletCount,
                    getWidth() / 2 - 80, getHeight() / 2 + HORIZONTAL_ADJUSTMENT);

            g.drawString("Accuracy: " + (int)((TARGET_NUM / bulletCount) * 100) + "%",
                    getWidth() / 2 - 80, getHeight() / 2 + 2 * HORIZONTAL_ADJUSTMENT);

            g.drawString("Press t to restart",
                    getWidth() / 2 - 80, getHeight() / 2 + 3 * HORIZONTAL_ADJUSTMENT);

        }

        // Check if the user has pressed restarting command
        if(restart){
            initial();
        }
    }

    // Purpose: initializing basic data for the game
    private void initial() {
        for (int i = 0; i < 10; i++){
            x[i] = tool.random(900 / 3, 900 - 50);
            y[i] = tool.random(0, 500 / 2);
        }
        for(int i = 0; i < targetStatus.length; i++){
            targetStatus[i] = 0;
        }
        restart = false;
        bulletLaunched = false;
        speed = 3;
        artilleryAngle = 45;
        bulletX = 0;
        bulletY = 450;
        bulletCount = 0;
        timer = new Timer(1000, new TimerListener());
        bulletDisplacementX = (speed * Math.cos(artilleryAngle * Math.PI / 180));
    }

    // Purpose: trigger the game to restart
    public void restartCommand() {
        restart = true;
    }

    // Purpose: check if all the target is shot
    // Return: Whether all the target is shot
    private boolean isAllShot() {
        for(int i = 0; i < targetStatus.length; i++){
            if(targetStatus[i] == 0){
                return false;
            }
        }

        return true;
    }

    // Purpose: simulate the gravitational acceleration
    private void physics() {
        bulletX += bulletDisplacementX;
        bulletY -= bulletDisplacementY;
        bulletDisplacementY -= GRAVITY;
        repaint();
    }

    // Purpose: draw the artillery
    // Input: canvas
    private void drawArtillery(Graphics g) {
        double angle = (artilleryAngle * Math.PI / 180);
        int x1 = 0;
        int y1 = (int)(0.98 * getHeight());
        int x2 = (int)(0.02 * getWidth());
        int y2 = (int)(getHeight());
        int deltaX = (int)(50 * Math.cos(angle));
        int deltaY = (int)(50 * Math.sin(angle));
        g.drawLine(x1, y1, x1 + deltaX, y1 - deltaY);
        g.drawLine(x2, y2, x2 + deltaX, y2 - deltaY);
    }

    // Purpose: change the status of the bullet that got hit
    private void hit() {
        for(int i = 0; i < 10; i++){
            if(isIn(bulletX, bulletY, x[i], y[i])) {
                targetStatus[i] = 1;
                repaint();
            }
        }
    }

    /*
     * Purpose: check if the bullet hit the target
     * Input: position of a target and the bullet in x and y coordinate
     * Return: whether the bullet hit the target
     */
    private boolean isIn(double bulletX, double bulletY, double targetX, double targetY) {
        double distance = Math.sqrt(Math.pow(targetY - bulletY, 2) + Math.pow(targetX - bulletX, 2));
        return distance <= (5 + 7.5);
    }

    // Purpose: lift the artillery up
    public void liftArtillery() {
        if(artilleryAngle <= 90 && bulletLaunched == false) {
            artilleryAngle += 1;
        }
        repaint();
    }

    // Purpose: put the artillery down
    public void lowerArtillery() {
        if(artilleryAngle >= 0 && bulletLaunched == false) {
            artilleryAngle -= 1;
        }
        repaint();
    }

    // Purpose: Increase the power propelling the bullet
    public void powerUp() {
        if(bulletX == 0 && bulletY == 450 && speed < 4 && bulletLaunched == false) {
            speed += 0.02;
            repaint();
        }
    }
    // Purpose: Decrease the power propelling the bullet
    public void powerDown() {
        if(bulletX == 0 && bulletY == 450 && speed > 2 && bulletLaunched == false) {
            speed -= 0.02;
            repaint();
        }
    }

    // Purpose: shoot the bullet
    public void fire() {
        if(bulletLaunched)
            return;
        if(isAllShot()){
            return;
        }
        bulletLaunched = true;
        bulletCount++;
        repaint();
    }

    // Purpose: reload bullet
    public void reload() {
        // put the shot bullet back to original position
        bulletX = 0;
        bulletY = 450;
        bulletLaunched = false;
    }

    class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            repaint();
        }
    }

}

class Tool {

    /* Purpose: Generate random integer based on given range
     * Input: upper and lower bound of the random number
     * Output: random integer based on inputs
     */
    public int random(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }


}