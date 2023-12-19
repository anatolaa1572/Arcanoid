package com.shpp.p2p.cs.abashlaev.assignment4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.random.RandomGenerator;
import java.awt.event.MouseEvent;

/**
 * Breakout
 * Task: the task of implementing the classic arcade game "Breakout" in Java
 * @author  Bashlaev Anatoly
 * @version  1.0
 */
public class Assignment4Part1 {
    /** Width and height of application window in pixels */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /** Dimensions of the paddle */
    public static final int PADDLE_WIDTH = 110;
    public static final int PADDLE_HEIGHT = 10;

    /** Offset of the paddle up from the bottom */
    public static final int PADDLE_Y_OFFSET = 30;

    /** Number of bricks per row */
    public static final int NBRICKS_PER_ROW = 10;

    /** Number of rows of bricks */
    public static final int NBRICK_ROWS = 10;

    /** Separation between bricks */
    public static final int BRICK_SEP = 4;

    /** It's a bad idea to calculate brick width from APPLICATION_WIDTH */
    // private static final int BRICK_WIDTH =
    //        (APPLICATION_WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /** Height of a brick */
    private static final int BRICK_HEIGHT = 8;

    /** Radius of the ball in pixels */
    private static final int BALL_RADIUS = 10;

    /** Offset of the top brick row from the top */
    private static final int BRICK_Y_OFFSET = 70;

    /** Number of turns */
    private static final int NTURNS = 3;
    private static final int BUTTON_HEIGHT = 17;
    private static final int BUTTON_Y_OFFSET = 18;

    JFrame fm;
    JPanel panel;

    public int panelX;
    public int panelY;
    public int brickWidth;
    public int brickW;
    public int currentTuns = 1;
    public int amountBricks = NBRICKS_PER_ROW * NBRICK_ROWS;
    public int flagStartGame;
    JTextField textRoundValue;
    JTextField textBricks;
    JButton buttonPause;
    JButton buttonStart;
    JLabel labelRound;
    JLabel labelBricks;
    boolean bricks [][] = new boolean[NBRICK_ROWS][NBRICKS_PER_ROW];
    int paddleX;
    int paddleY;
    int bollX, bolLOldX, bollDeltaX;
    int bollY, bollOldY, bollDeltaY;

    int flag_ricochetBoll;
    int flag_pause;
    int flag_endRound;


    public Assignment4Part1() {
        fm  = new JFrame();
        fm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fm.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
        panel = new MyPanel();
        panel.setLayout(null);
        panel.setBackground(Color.lightGray);
        fm.add(panel);
        fm.setVisible(true);
        fm.setVisible(false);
        panelX = panel.getWidth();
        panelY = panel.getHeight();
        buttonPanel();
        startGame ();
        fm.setLocationRelativeTo(null);
        fm.setResizable(false);
        fm.setVisible(true);

    }
    /**
     * Button Panel
     */
    public void buttonPanel () {

        labelRound = new JLabel("Round:");
        labelRound.setForeground(Color.BLACK);
        textRoundValue = new JTextField();
        textRoundValue.setText(Integer.toString(currentTuns));
        textRoundValue.setForeground(Color.GREEN);
        textRoundValue.setBackground(Color.DARK_GRAY);
        textRoundValue.setHorizontalAlignment(JTextField.RIGHT);

        labelBricks = new JLabel("Bricks:");
        labelBricks.setForeground(Color.BLACK);

        textBricks = new JTextField();
        textBricks.setText(Integer.toString(amountBricks));
        textBricks.setForeground(Color.GREEN);
        textBricks.setBackground(Color.DARK_GRAY);
        textBricks.setHorizontalAlignment(JTextField.RIGHT);
        buttonStart = new JButton("New Game");
        buttonPause = new JButton("Unpause");


        labelRound.setBounds(10,panelY - BUTTON_Y_OFFSET,50,BUTTON_HEIGHT);
        textRoundValue.setBounds(56,panelY - BUTTON_Y_OFFSET,15,BUTTON_HEIGHT);
        labelBricks.setBounds(90,panelY - BUTTON_Y_OFFSET,50,BUTTON_HEIGHT);
        textBricks.setBounds(134,panelY - BUTTON_Y_OFFSET,30,BUTTON_HEIGHT);
        buttonStart.setBounds(170,panelY- BUTTON_Y_OFFSET, 100,BUTTON_HEIGHT);
        buttonPause.setBounds(270,panelY- BUTTON_Y_OFFSET, 110,BUTTON_HEIGHT);

        panel.add(labelRound);
        panel.add(textRoundValue);
        panel.add(labelBricks);
        panel.add(textBricks);
        panel.add(buttonStart);
        panel.add(buttonPause);

        /**
         * Button listener
         */
        ActionListener listenerButtonStop = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Pause");
                if (flag_pause == 0) {
                    buttonPause.setText("Pause");
                    buttonPause.setForeground(Color.RED);

                    flag_pause = 1;
                } else {
                    buttonPause.setText("Unpause");
                    buttonPause.setForeground(Color.black);
                    flag_pause = 0;
                }

            }
        };
        buttonPause.addActionListener(listenerButtonStop);

        /**
         * Button listener
         */
        ActionListener listenerButtonStart = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Start");
                startGame ();
                flag_endRound = 0;
                amountBricks = NBRICKS_PER_ROW * NBRICK_ROWS;
                textBricks.setText(Integer.toString(amountBricks));
                textRoundValue.setText(Integer.toString(currentTuns));
                fm.setResizable(false);
                fm.setVisible(true);
            }
        };
        buttonStart.addActionListener(listenerButtonStart);

    }

    /**
     *
     */
    public void definitionWidthBricks () {
        brickWidth = (panelX - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;
        brickW = (panelX - (NBRICKS_PER_ROW - 1) * BRICK_SEP) % NBRICKS_PER_ROW;
    }

    /**
     *
     */
    public void startGame () {
        flagStartGame = 1;
        flag_endRound = 0;
        flag_pause = 0;
        currentTuns = 1;
        paddleX = 162;
        paddleY = 500;
        bollX = 182;
        bollY = 220;
        if (Math.random() > 0.5) {
            bollDeltaX = (int) (Math.random() * 5) + 2;
            bollDeltaY = (int) (Math.random() * 4) + 2;
        } else {
            bollDeltaX = - (int) (Math.random() * 5) + 2;
            bollDeltaY =  (int) (Math.random() * 4) + 2;
        }

        amountBricks = NBRICKS_PER_ROW * NBRICK_ROWS;
        definitionWidthBricks();
        FillingBricks();
    }

    /**
     *
     */
    public void continuationGame () {
        flagStartGame = 1;
        flag_endRound = 0;
        flag_pause = 0;
        paddleX = 162;
        paddleY = 500;
        bollX = 182;
        bollY = 220;
        if (Math.random() > 0.5) {
            bollDeltaX = (int) (Math.random() * 5) + 2;
            bollDeltaY = (int) (Math.random() * 4) + 2;
        } else {
            bollDeltaX = - (int) (Math.random() * 5) + 2;
            bollDeltaY = - (int) (Math.random() * 4) + 2;
        }

    }
    /**
     *
     */
    public void FillingBricks () {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                bricks[i][j] = true;
            }

        }

    }

     /**
     *
     */
    public void ricochetBoll () {
        if (flag_ricochetBoll == 0) {  // Clean area
            //Wall  Up and Down
            if ((bollY + bollDeltaY < 0) || (bollY + bollDeltaY > panelY)) {
                bollDeltaY = - bollDeltaY;
            }
            // Wall right and  left
            if ((bollX + bollDeltaX < 0) || (bollX + bollDeltaX + BALL_RADIUS > panelX)) {
                bollDeltaX = - bollDeltaX;
            }
        }
        if (flag_ricochetBoll == 1 ) { // Bricks area
            bollDeltaY = - bollDeltaY;
            bollDeltaX = - bollDeltaX;
        }
        if (flag_ricochetBoll == 2) {  // Paddle area
            if ((bollX >= paddleX) && (bollX <= paddleX + PADDLE_WIDTH) ) {
                if (Math.random() > 0.5) {
                    bollDeltaY = - (bollDeltaY + 1);
                    bollDeltaX = (bollDeltaX + 2);
                } else {
                    bollDeltaY = - (bollDeltaY - 1);
                    bollDeltaX = (bollDeltaX - 2);
                }
            }
        }
        if (flag_ricochetBoll == 3) {  // End Tuns area
            bollDeltaY = - bollDeltaY;
            bollDeltaX = - bollDeltaX;

        }
        if (flag_ricochetBoll == 4) {  //  area
            bollX = bollX + bollDeltaX;
            bollY = bollY + bollDeltaY;
        }

        if (bollY <= panelY) {
            bollX = bollX + bollDeltaX;
            bollY = bollY + bollDeltaY;
        }
    }


    public int locatorBoll () {
        int locatorY = BRICK_Y_OFFSET + NBRICK_ROWS * BRICK_HEIGHT + (NBRICK_ROWS - 1) * BRICK_SEP;
        int row, rowMiddle;
        int column, columMiddle;
        int flag = 0;  // Clean area

        if ((bollY <= locatorY) && (bollY >= BRICK_Y_OFFSET ))   {
            flag = 1; // Bricks area
            rowMiddle = (int) (bollY + 2 * BRICK_SEP - BALL_RADIUS - BRICK_Y_OFFSET)/ (BRICK_HEIGHT + BRICK_SEP);
            columMiddle = (int) (bollX - BALL_RADIUS - brickW/2)/ (brickWidth + BRICK_SEP);
            if (bricks[rowMiddle][columMiddle] == true) {
                row = rowMiddle;
                column = columMiddle;
            } else {
                row = (int) (bollY + 2 * BRICK_SEP - BALL_RADIUS - BRICK_Y_OFFSET)/ (BRICK_HEIGHT + BRICK_SEP);
                column = (int) (bollX - BALL_RADIUS - brickW/2)/ (brickWidth + BRICK_SEP);
            }

            if (bricks[row][column] == true) {
                bricks[row][column] = false;
                amountBricks = amountBricks - 1;
                textBricks.setText(Integer.toString(amountBricks));
//                System.out.println("row = " + row);
//                System.out.println("column = " + column);
//                System.out.println("bollX = " + bollX);
//                System.out.println("bollY = " + bollY);
                flag = 3;
            } else {
                flag = 0;
            }

            //return flag;
        }


        if ((bollY >= panelY - PADDLE_Y_OFFSET - BALL_RADIUS- PADDLE_HEIGHT) && (bollY < panelY - PADDLE_Y_OFFSET) ) {
            flag = 2; // Paddle area
            //return flag;
        }


        if (bollY > panelY - PADDLE_Y_OFFSET && flag_endRound == 0) {   //
            flag = 4; // End Tuns
            if (currentTuns < NTURNS ) {
                flag_endRound = 1;
                currentTuns = currentTuns + 1;
                //amountBricks = NBRICKS_PER_ROW * NBRICK_ROWS;
                continuationGame();
                textRoundValue.setText(Integer.toString(currentTuns));
                fm.setResizable(false);
                fm.setVisible(true);


            } else {

                System.out.println("END");
                startGame ();
                flag_endRound = 0;
                amountBricks = NBRICKS_PER_ROW * NBRICK_ROWS;
                textBricks.setText(Integer.toString(amountBricks));
                textRoundValue.setText(Integer.toString(currentTuns));
                JOptionPane.showMessageDialog(fm,"Game over.");
                fm.setResizable(false);
                fm.setVisible(true);
                flag = 5; // End GAME
            }
        }
        return flag;

    }

    /**
     * Main program
     */
    public static void main (String [] args) {
        //
        Assignment4Part1 anm = new Assignment4Part1();
    }

    /**
     *
     */
class MyPanel extends JPanel {
    public  int mouseX, mouseY;

        /**
         *
         */
    class MyMouse implements MouseListener {


        @Override
        public void mousePressed(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY();
            movingPaddle();
        }
        @Override
        public void mouseClicked(MouseEvent e) {  }

        @Override
        public void mouseReleased(MouseEvent e) {  }

        @Override
        public void mouseEntered(MouseEvent e) {  }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }


        /**
         *
         */
    public MyPanel () {
        addMouseListener(new MyMouse());
        setFocusable(true);
        Timer tm = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (flag_pause == 1) {
                    flag_ricochetBoll = locatorBoll();
                    ricochetBoll();
                    repaint();
                }

            }
        });
        tm.start();
    }


    @Override
    public void paintComponent (Graphics gr) {
        super.paintComponent(gr);

        // paddle
        gr.setColor(Color.GRAY);
        gr.fillRect(paddleX, panelY - PADDLE_Y_OFFSET,PADDLE_WIDTH,PADDLE_HEIGHT);

        // boll
        gr.setColor(Color.BLACK);
        gr.fillOval(bollX, bollY,BALL_RADIUS,BALL_RADIUS);

        // Bricks
        drawBricks (gr);
    }
        /**
         *
         */
    public void movingPaddle () {
        paddleY = panelY - PADDLE_Y_OFFSET;
        if (mouseX < PADDLE_WIDTH/2 ) {
            paddleX = 0;
        } else if (mouseX > panelX - PADDLE_WIDTH/2) {
            paddleX = panelX - PADDLE_WIDTH;
        } else {
            paddleX = mouseX - PADDLE_WIDTH/2;
        }

    }
        /**
         *
         */
    public void drawBricks (Graphics gr) {

        for (int i = 0; i < NBRICK_ROWS ; i++) {
            for (int j = 0; j < NBRICKS_PER_ROW; j++) {
                if (i <= 1 ) {
                    gr.setColor(Color.RED);
                }
                if (i <= 3 && i > 1) {
                    gr.setColor(Color.ORANGE);
                }
                if (i <= 5 && i > 3) {
                    gr.setColor(Color.YELLOW);
                }
                if (i <= 7 && i > 5) {
                    gr.setColor(Color.GREEN);
                }
                if (i <= 9 && i > 7) {
                    gr.setColor(Color.CYAN);
                }
                if (bricks[i][j] == true) {
                    gr.fillRect(j * (34 + BRICK_SEP) + brickW/2, BRICK_Y_OFFSET + (BRICK_HEIGHT + BRICK_SEP) * i, brickWidth, BRICK_HEIGHT);
                }

            }

        }

    }
}

}
