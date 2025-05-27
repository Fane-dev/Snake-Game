package mySnakeGame;

import javax.swing.*;
import java.awt.*;

public class MySnakeGame {
    public static void main(String[] args) throws Exception{
        int width = 640;
        int height = 640;

        //Crei una finestra di gioco
        JFrame jFrame = new JFrame("Snake Game");

        //Scegli lla grandezza della finestra di gioco
        jFrame.setSize(width, height);

        //Posizioni lo schermo al centro
        jFrame.setLocationRelativeTo(null);

        //Non permetti di modificare la finestra
        jFrame.setResizable(false);

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Rendi visibile lla finestra di gioco
        jFrame.setVisible(true);

        //Crei un pannello di gioco (Inserisci elementi sulla finestra di gioco)
        MyPanel panel = new MyPanel(width, height);

        //Aggiungi il pannello alla finestra di gioco
        jFrame.add(panel);
        jFrame.pack();

        //Questo metodo richiede che il componente sia visualizzabile, focusabile, visibile e che tutti i suoi antenati siano visibili
        panel.requestFocus();
    }
}
