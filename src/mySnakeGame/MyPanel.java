package mySnakeGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;

public class MyPanel extends JPanel implements ActionListener, KeyListener {

    //Classe tile per la gestione dei blocchi
    private class Tile{
        int x;
        int y;

        Tile(int x, int y){
            this.x = x;
            this.y = y;
        }
    }
    int width;
    int height;
    int tileSize = 32;
    Tile snakeHead;
    Tile apple;
    Random random;
    Random randomApple = new Random();
    Color appleColor = Color.red;

    ArrayList<Tile> snakeBody;
    ArrayList<Graphics> apples;

    Timer gameLoop;

    int velocityX = 0;
    int velocityY = 1;

    boolean gameOver = false;

    MyPanel(int width, int height){
        this.width = width;
        this.height = height;
        this.setPreferredSize(new Dimension(this.width, this.height));
        this.setBackground(Color.black);

        snakeHead = new Tile(5, 5);
        apple = new Tile(10, 10);

        snakeBody = new ArrayList<Tile>();

        random = new Random();
        placeFood(snakeBody);

        gameLoop = new Timer(120, this);
        gameLoop.start();

        addKeyListener(this);
        setFocusable(true);
    }

    //Sceglie un colore per la mela in modo casuale
    public Color drawApples(){
        Color colors[] = {Color.red, Color.yellow, Color.green,Color.blue, Color.darkGray};
        int random = randomApple.nextInt(0, 5);
        return colors[random];
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    //Continua a disegnare il contenuto
    public void draw(Graphics g) {

        //Colore e creazione del serpente
        g.setColor(Color.green);
        g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);

        for (int i = 0; i < snakeBody.size(); i++){
            Tile snakePart = snakeBody.get(i);
            g.fillRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
            g.fill3DRect(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize,true);
        }

        //Colore e creazione della prima mela
        g.setColor(appleColor);
        g.fillOval(apple.x * tileSize, apple.y * tileSize, tileSize, tileSize);

        //Se e gameover mostra il messaggio
        if (gameOver){
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.setColor(Color.red);
            g.drawString("GAME OVER: " + String.valueOf(snakeBody.size()), width/4, height/2);
        }
        //Finche' non e' gameover continua a mostrare Mele mangiate
        else{
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.green);
            g.drawString("Mele Mangiate: " + String.valueOf(snakeBody.size()), tileSize - 16, tileSize);
        }
    }

    //Continua a posizionare le mele senza generarle sul serpente
    public void placeFood(ArrayList<Tile> snakeBody){

        apple.x = random.nextInt(this.width/tileSize);
        apple.y = random.nextInt(this.height/tileSize);
        while (true){
            boolean exit = true;
            for (int i = 0; i < snakeBody.size(); i++){
                if(snakeBody.get(i).x == apple.x && snakeBody.get(i).y == apple.y){
                    apple.x = random.nextInt(this.width/tileSize);
                    apple.y = random.nextInt(this.height/tileSize);
                    exit = false;
                    break;
                }
            }
            if (exit){
                break;
            }
        }
        if(appleColor == Color.darkGray){
            setTimeout(() -> {
                appleColor = drawApples();
                placeFood(snakeBody);
            }, 5000);
        }
    }

    //Set timeout come su javascript
    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }

    //Applica l'effetto alle mele che raccogli
    public void appleEffects(){
        gameLoop.setDelay(120);
        if (appleColor == Color.red){
            //Aggiunge un pezzo al serpente appena passa sopra la mela
            snakeBody.add(new Tile(apple.x, apple.y));
        }
        else if (appleColor == Color.yellow){
            snakeBody.add(new Tile(apple.x, apple.y));
            snakeBody.add(new Tile(apple.x, apple.y));
        }
        else if (appleColor == Color.green){
            gameLoop.setDelay(140);
            snakeBody.add(new Tile(apple.x, apple.y));
        }
        else if (appleColor == Color.blue){
            gameLoop.setDelay(100);
            snakeBody.add(new Tile(apple.x, apple.y));
        }
        else if(appleColor == Color.darkGray){
            gameOver = true;
        }
    }

    //Movimento del serpente
    public void move(){
        if (collision(snakeHead, apple)){
            //Richiamo dei metodi appartenenti alla mela
            appleEffects();
            appleColor = drawApples();
            placeFood(snakeBody);
        }

        //Gli elementi aggiunti seguiranno la testa del serpente
        for (int i = snakeBody.size() - 1; i >= 0; i--){
            Tile snakePart = snakeBody.get(i);
            if(i == 0){
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }
            else{
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        //Velocita' del serpente
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        //Collisione tra la testa e i componenti del corpo del serpente
        for (int i = 0; i < snakeBody.size(); i++){
            Tile snakePart = snakeBody.get(i);
            if (snakePart.x == snakeHead.x && snakePart.y == snakeHead.y){
                System.out.println("Game Over");
                gameOver = true;
            }
        }
        //Collisione con le pareti
        if (snakeHead.x * tileSize < 0 || snakeHead.x * tileSize> width - 1|| snakeHead.y * tileSize < 0 || snakeHead.y * tileSize > height - 1){
            System.out.println("Game Over");
            gameOver = true;
        }

    }

    //Check collisione tra la mela e la testa del serpente
    public boolean collision(Tile tile1, Tile tile2){
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    //Operazioni sui tasti della tastiera
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_W && velocityY != 1){
            velocityX = 0;
            velocityY = -1;
        }

        else if (e.getKeyCode() == KeyEvent.VK_S && velocityY != -1){
            velocityX = 0;
            velocityY = 1;
        }

        else if(e.getKeyCode() == KeyEvent.VK_A && velocityX != 1){
            velocityX = -1;
            velocityY = 0;
        }

        else if (e.getKeyCode() == KeyEvent.VK_D && velocityX != -1){
            velocityX = 1;
            velocityY = 0;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver){
            gameLoop.stop();
        }
    }

}
