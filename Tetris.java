/*
 *  Simple Tetris example
 *  Author: Sladkov Mikhail
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

class Glass
{
  private int width = 10;
  private int height = 20;
  private int score = 0;
  private Color[] colors = new Color[] {Color.red, Color.orange, Color.yellow, Color.green, Color.cyan, Color.blue, Color.magenta};
  Position [][] positions;
  Figure figure;
  Figure nextFigure;
  boolean isFull = false;
  boolean isPaused = false;

  public Glass () {
    score = 0;
    isFull = false;
    positions = new Position[height][width];
    for (int i=0; i<height; i++) {
      for (int j=0; j<width; j++) {
        positions[i][j] = new Position(Position.EMPTY, Color.black);
      }
    }
    figure = generateFigure();
    nextFigure = generateFigure();
  }

  private Figure generateFigure() {
    Random random = new Random();
    int n = random.nextInt(7);
    //Color color = colors[random.nextInt(colors.length)];
    switch (n) {
    case 0:
      return new Figure(new int[][]{{1,1},{1,1}}, colors[n]);
    case 1:
      return new Figure(new int[][]{{1,0},{1,0},{1,1}}, colors[n]);
    case 2:
      return new Figure(new int[][]{{1,1,1},{0,1,0}}, colors[n]);
    case 3:
      return new Figure(new int[][]{{0,1},{0,1},{1,1}}, colors[n]);
    case 4:
      return new Figure(new int[][]{{1,0},{1,1},{0,1}}, colors[n]);
    case 5:
      return new Figure(new int[][]{{0,1},{1,1},{1,0}}, colors[n]);
    case 6:
      return new Figure(new int[][]{{1},{1},{1},{1}}, colors[n]);
    }
    return new Figure(new int[][]{{1,1},{1,1}}, colors[n]);
  }

  private boolean canPlacePositions(int testX, int testY, int[][] pos) {
    boolean canPlace = true;
    for (int i=0; i<pos.length; i++) {
      for (int j=0; j<pos[i].length; j++) {
        if ((testX+j)<0 || (testX+j)>=width || (testY+i)>=height) {
          canPlace = false;
          break;
        }
        if ( (testY+i>=0) && ((positions[testY+i][testX+j].value==Position.FULL) && (pos[i][j]==Position.FULL)) ) {
          canPlace = false;
          break;
        }
      }
    }
    return canPlace;
  }

  private void clearLines() 
  {
    int k = 0;
    for (int i=positions.length-1; i>=k; i--) 
    {
      boolean canClear = true;
      for (int j=0; j<positions[i].length; j++) 
      {
        if (positions[i][j].value==Position.EMPTY) {
          canClear = false;
        }
      }
      if (canClear) {
        for (int s=i; s>=k; s--)
        {
          for (int t=0; t<positions[s].length; t++) {
            if (s-1<k) {
              positions[s][t] = new Position(Position.EMPTY, Color.black);
            }
            else {
              positions[s][t] = positions[s-1][t];
            }
          }
        }
        i++;
        k++;
      }
    }
    if (k==1) {
      score+=100;
    }
    else if (k==2) {
      score+=300;
    }
    else if (k==3) {
      score+=700;
    }
    else if (k==4) {
      score+=1500;
    }
  }

  void holdFigure() {
    if (!canPlacePositions(figure.x, figure.y+1, figure.positions)) {
      if (figure.y<0) {
        isFull = true;
        return;
      }
      for (int i=0; i<figure.positions.length; i++) {
        for (int j=0; j<figure.positions[i].length; j++) {
          if (figure.positions[i][j]==1) {
            positions[figure.y+i][figure.x+j].value = Position.FULL;
            positions[figure.y+i][figure.x+j].color = figure.color;
          }
        }
      }
      clearLines();
      figure = nextFigure;
      nextFigure = generateFigure();
    }
  }

  void rotateFigure() {
    int[][] newPositions = new int[figure.positions[0].length][figure.positions.length];
    for (int i=0; i<figure.positions.length; i++) {
      for (int j=0; j<figure.positions[i].length; j++) {
        newPositions[j][i] = figure.positions[i][figure.positions[i].length-1-j];
      }
    }
    if (canPlacePositions(figure.x, figure.y, newPositions)) {
      figure.positions = newPositions;
    }
  }

  boolean moveFigure(int shiftX, int shiftY) {
    if (canPlacePositions(figure.x+shiftX, figure.y+shiftY, figure.positions)) {
      figure.x+=shiftX;
      figure.y+=shiftY;
      return true;
    }
    return false;
  }

  public int getScore() {
    return score;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }
}

class Figure
{
  int [][]positions = null;
  int x=3,y=-1;
  Color color = Color.white;

  public Figure (int[][] positions, Color color) {
    this.positions = positions;
    this.color = color;
  }
}

class Position
{
  static int EMPTY = 0;
  static int FULL = 1;
  Color color = Color.black;
  int value = EMPTY;
  public Position(int value, Color color) {
    this.value = value;
    this.color = color;
  }
}

public class Tetris extends JPanel implements KeyListener
{
  private final static Color bg = Color.black;
  private final static Color fg = Color.white;
  private int blockWidth = 0;
  private int blockHeight = 0;

  private Glass st = new Glass();
  
  public Glass getGlass() {
    return st;
  }

  public void drawGlass(Graphics2D g2) {
    for (int i=0; i<st.getHeight(); i++) {
      for (int j=0; j<st.getWidth(); j++) {
        if (st.positions[i][j].value==Position.FULL) {
          g2.setColor(st.positions[i][j].color);
          g2.fill(new Rectangle(blockWidth*(j)-1,blockHeight*(i)-1,blockWidth-1,blockHeight-1));
        }
      }
    }
    g2.setColor(fg);
    for (int i=0; i<=st.getHeight(); i++) {
      g2.drawLine(0,i*blockHeight-1,st.getWidth()*blockWidth-1,i*blockHeight-1);
    }
    for (int j=0; j<=st.getWidth(); j++) {
      g2.drawLine(j*blockWidth-1,0,j*blockWidth-1,j*st.getHeight()*blockHeight-1);
    }
    if (st.isFull || st.isPaused)
    {
      final Font f = new Font("SansSerif", Font.BOLD, 36);
      g2.setFont(f);
      FontMetrics fm = g2.getFontMetrics();
      String s = st.isFull?"GAME OVER!":"PAUSE";
      int x = (getWidth()*2/3-fm.stringWidth(s))/2;
      int y = (fm.getAscent()+(getHeight()-(fm.getAscent()+fm.getDescent()))/2);
      g2.setColor(bg);
      g2.fill(new Rectangle(x,y-fm.getAscent(),fm.stringWidth(s),(fm.getAscent()+fm.getDescent())));
      g2.setColor(Color.red);
      g2.drawString(s,x,y);
      g2.setColor(fg);
    }
  }

  public void drawScore(Graphics2D g2) {
    final Font f = new Font("SansSerif", Font.PLAIN, 24);
    g2.setFont(f);
    FontMetrics fm = g2.getFontMetrics();
    String s = "Score:";
    int x = getWidth()*2/3+(getWidth()*1/3-fm.stringWidth(s))/2;
    int y = (fm.getAscent()+(getHeight()-(fm.getAscent()+fm.getDescent()))/2);
    g2.setColor(fg);
    g2.drawString(s,x,y);
    s = ""+st.getScore();
    x = getWidth()*2/3+(getWidth()*1/3-fm.stringWidth(s))/2;
    y += (fm.getAscent()+fm.getDescent());
    g2.drawString(s,x,y);
  }

  public void drawFigure(Graphics2D g2) {
    g2.setColor(st.figure.color);
    for (int i=0; i<st.figure.positions.length; i++) {
      for (int j=0; j<st.figure.positions[i].length; j++) {
        if (st.figure.positions[i][j]==1) {
          g2.fill(new Rectangle(blockWidth*(j+st.figure.x)-1,blockHeight*(i+st.figure.y)-1,blockWidth-1,blockHeight-1));
        }
      }
    }
  }

  public void drawNextFigure(Graphics2D g2) {
    g2.setColor(st.nextFigure.color);
    for (int i=0; i<st.nextFigure.positions.length; i++) {
      for (int j=0; j<st.nextFigure.positions[i].length; j++) {
        if (st.nextFigure.positions[i][j]==1) {
          g2.fill(new Rectangle(blockWidth*(j+1+st.getWidth())-1,blockHeight*(i+1)-1,blockWidth-1,blockHeight-1));
        }
      }
    }
  }

  public void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    blockWidth = (int) getWidth()/3*2/st.getWidth();
    blockHeight = (int) getHeight()/st.getHeight();

    g2.setColor(bg);
    g2.fillRect(0, 0, getWidth(), getHeight());
    drawFigure(g2);
    drawNextFigure(g2);
    drawGlass(g2);
    drawScore(g2);
  }

  public void keyTyped(KeyEvent e) {
  }

  public void keyReleased(KeyEvent e) {
  }

  public void processKey(int keyCode) {
    if (!st.isFull && !st.isPaused) {
      if (keyCode == KeyEvent.VK_LEFT ) {
        st.moveFigure(-1,0);
      }
      else if (keyCode == KeyEvent.VK_RIGHT) {
        st.moveFigure(1,0);
      }
      else if (keyCode == KeyEvent.VK_DOWN) {
        st.moveFigure(0,1);
      }
      else if (keyCode == KeyEvent.VK_SPACE) {
        while(st.moveFigure(0,1)) {
        }
      }
      else if (keyCode == KeyEvent.VK_UP) {
        st.rotateFigure();
      }
    }
    if (st.isFull && keyCode == KeyEvent.VK_SPACE) {
      st = new Glass();
    }
    if (keyCode == KeyEvent.VK_F2) {
      st = new Glass();
    }
    else if (keyCode == KeyEvent.VK_PAUSE) {
      st.isPaused = !st.isPaused;
    }
    else if (keyCode == KeyEvent.VK_ESCAPE) {
      System.exit(0);
    }
    repaint();
  }

  public void hold() {
    if (!st.isPaused && !st.isFull) {
      st.holdFigure();
      repaint();
    }
  }


  public void keyPressed(KeyEvent e) {
    processKey(e.getKeyCode());
  }
  
  public Tetris() {
    super();
    setOpaque(true);
    setPreferredSize(new Dimension(240*3/2, 240*getGlass().getHeight()/getGlass().getWidth()));
    setBackground(Color.black);
  }

  private static void createAndShowGUI() {
    //JFrame.setDefaultLookAndFeelDecorated(true);
    final JFrame f = new JFrame("Tetris");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setResizable(false);
    f.setBackground(Color.black);

    final Tetris tetris = new Tetris();

    f.getContentPane().add(tetris,BorderLayout.CENTER);
    f.addKeyListener(tetris);
    f.setBackground(Color.black);
    f.pack();
    f.setVisible(true);
    new Thread (new Runnable() {
      public void run() {
        while (true) {
          try {
            synchronized (tetris) {
              tetris.processKey(KeyEvent.VK_DOWN);
              Thread.sleep(250);
              tetris.hold();
            }
          }
          catch (Exception e) {
          }
        }
      }
    }).start();
  }

  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          createAndShowGUI();
        }
    });
  }
};
