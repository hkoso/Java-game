import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;


public class Shooting extends JFrame{
    private final static int X = 900;
    private final static int Y = 500;


    public Shooting() {
        add(new Scene());
    }

    public static void main(String[] args) {
        Shooting frame = new Shooting();
        frame.setTitle("Game");
        frame.setSize(X,Y);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);

    }

}

class Scene extends JPanel {
    int bulletx = 0;
    int bullety = 0;

    private int[] x = new int[10];
    private int[] y = new int[10];

    private Timer timer = new Timer(10, new TimerListener());
    private final static int DIAMETER = 15;
    private Tool tool = new Tool();

    public Scene(){
        timer.start();
        for (int i = 0; i < 10; i++){
            x[i] = tool.random(900 / 3, 900 - 50);
            y[i] = tool.random(0, 500 / 2);

        }
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        for(int i = 0; i < 10; i++){
            g.drawOval(x[i], y[i], DIAMETER, DIAMETER);
        }
        g.fillOval(0 + bulletx, 450 + bullety, 10, 10);
        bulletx += 2;
        bullety -= 2;
    }

    class TimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            repaint();
        }
    }
}

class Tool {
    public int random(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }


}