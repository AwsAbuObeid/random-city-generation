package maze.gen;

import javax.swing.*;
import java.awt.event.*;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("My City");
        int pixelSizeX = 1500;
        int pixelSizeY = 900;
        Random random = new Random();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(pixelSizeX + 17, pixelSizeY + 40);
        frame.setVisible(true);
        Window window=new Window(pixelSizeX,pixelSizeY, 1234567);
        System.out.println("sh");
        MouseListener ml = new MouseListener() {
            int x=0,y=0;
            @Override
            public void mouseClicked(MouseEvent e) {
                window.changeSeed(random.nextInt());
                window.repaint();
            }
            public void mousePressed(MouseEvent e) {
                x=e.getX();
                y=e.getY();
            }
            public void mouseReleased(MouseEvent e) {
                int movementX=e.getX()-x;
                int movementY=e.getY()-y;
                window.moveWindow(movementX,movementY);
            }
            public void mouseEntered(MouseEvent e) {}
            public void mouseExited(MouseEvent e) {}
        };
        MouseWheelListener mo = e -> {
            if (e.getWheelRotation()>0)
                window.zoomOut();
            else
                window.zoomIn();
        };
        KeyListener kl=new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(e.getKeyChar()=='w') window.movePlayer(0,-10);
                if (e.getKeyChar()=='s') window.movePlayer(0,+10);
                if (e.getKeyChar()=='d') window.movePlayer(+10,0);
                if (e.getKeyChar()=='a') window.movePlayer(-10,0);
                if (e.getKeyChar()=='q') window.decProb();
                if (e.getKeyChar()=='e') window.incProb();
            }
            public void keyPressed(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {}
        };

        frame.addKeyListener(kl);
        frame.addMouseWheelListener(mo);
        frame.addMouseListener(ml);

        window.repaint();
        frame.add(window);
    }
}