
package spaceship;

import java.io.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

public class Spaceship extends JFrame implements Runnable {
    static final int WINDOW_WIDTH = (420*3);
    static final int WINDOW_HEIGHT = (445*2);
    final int XBORDER = 20;
    final int YBORDER = 20;
    final int YTITLE = 25;
    boolean animateFirstTime = true;
    int xsize = -1;
    int ysize = -1;
    Image image;
    Graphics2D g;

    sound zsound = null;
    sound bgSound = null;
    Image outerSpaceImage;
    
    static final int numMissles = 10;
    Missile missile[] = new Missile[numMissles];

//variables for rocket.
    Image rocketImage;
    Image explosionImage;
    int rocketXPos;
    int rocketYPos;
    int rocketSpeed;
    int rocketSpeedY;
    int rocketRot;
    boolean rocketRight;
    
    
    
    int timeCount;
    
    int numStars = 10;
    
    int starX[] = new int[numStars];
    int starY[] = new int [numStars];
    boolean starHit[] = new boolean[numStars];
    int whichStarHit;

    static Spaceship frame;
    public static void main(String[] args) {
        frame = new Spaceship();
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public Spaceship() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.BUTTON1 == e.getButton()) {
                    //left button
                    
                    
// location of the cursor.
                    int xpos = e.getX();
                    int ypos = e.getY();

                }
                if (e.BUTTON3 == e.getButton()) {
                    //right button
                    reset();
                }
                repaint();
            }
        });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        repaint();
      }
    });

    addMouseMotionListener(new MouseMotionAdapter() {
      public void mouseMoved(MouseEvent e) {

        repaint();
      }
    });

        addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {
                if (e.VK_UP == e.getKeyCode()) {
                    rocketSpeedY++;
                } else if (e.VK_DOWN == e.getKeyCode()) {
                    rocketSpeedY--;    
                } else if (e.VK_LEFT == e.getKeyCode()) {
                    rocketSpeed++;
                } else if (e.VK_RIGHT == e.getKeyCode()) {
                    rocketSpeed--;
                }
                else if (e.VK_INSERT == e.getKeyCode()) {
                    zsound = new sound("ouch.wav");                    
                }
                else if (e.VK_O == e.getKeyCode()) {      
                    outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./osama.jpg");
                }
                
                repaint();
            }
        });
        init();
        start();
    }
    Thread relaxer;
////////////////////////////////////////////////////////////////////////////
    public void init() {
        requestFocus();
    }
////////////////////////////////////////////////////////////////////////////
    public void destroy() {
    }



////////////////////////////////////////////////////////////////////////////
    public void paint(Graphics gOld) {
        if (image == null || xsize != getSize().width || ysize != getSize().height) {
            xsize = getSize().width;
            ysize = getSize().height;
            image = createImage(xsize, ysize);
            g = (Graphics2D) image.getGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
//fill background
        g.setColor(Color.cyan);
        g.fillRect(0, 0, xsize, ysize);

        int x[] = {getX(0), getX(getWidth2()), getX(getWidth2()), getX(0), getX(0)};
        int y[] = {getY(0), getY(0), getY(getHeight2()), getY(getHeight2()), getY(0)};
//fill border
        g.setColor(Color.black);
        g.fillPolygon(x, y, 4);
// draw border
        g.setColor(Color.red);
        g.drawPolyline(x, y, 5);
        
      

        if (animateFirstTime) {
            gOld.drawImage(image, 0, 0, null);
            return;
        }

        g.drawImage(outerSpaceImage,getX(0),getY(0),
                getWidth2(),getHeight2(),this);
        
        
        
        for (int index = 0;index < numStars;index++) {
        drawCircle(starX[index],getYNormal(starY[index]),0,1,1);
            if (starHit[index])
            drawImage(explosionImage,starX[index],starY[index],30,30,
                getWidth2(),getHeight2(),this);
        }
        
        if (rocketRight)
            drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),0.0,1.0,1.0 );
        
        else
            drawRocket(rocketImage,getX(rocketXPos),getYNormal(rocketYPos),180.0,1.0,1.0 );
        
        gOld.drawImage(image, 0, 0, null);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawCircle(int xpos,int ypos,double rot,double xscale,double yscale)
    {
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.setColor(new Color ((int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)));
        g.fillOval(-10,-10,20,20);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
////////////////////////////////////////////////////////////////////////////
    public void drawRocket(Image image,int xpos,int ypos,double rot,double xscale,
            double yscale) {
        int width = rocketImage.getWidth(this);
        int height = rocketImage.getHeight(this);
        g.translate(xpos,ypos);
        g.rotate(rot  * Math.PI/180.0);
        g.scale( xscale , yscale );

        g.drawImage(image,-width/2,-height/2,
        width,height,this);

        g.scale( 1.0/xscale,1.0/yscale );
        g.rotate(-rot  * Math.PI/180.0);
        g.translate(-xpos,-ypos);
    }
    public void drawImage(Image image,int xpos, int ypos,int width,int height,int xscale,int yscale,Spaceship obj)
    {
        g.translate(xpos,getYNormal(ypos));
       
        g.drawImage(image,-width/2, -height/2, width, height, obj);

        g.translate(-xpos,-getYNormal(ypos));
    }          
////////////////////////////////////////////////////////////////////////////
// needed for     implement runnable
    public void run() {
        while (true) {
            animate();
            repaint();
            double seconds = 0.08;    //time that 1 frame takes.
            int miliseconds = (int) (1000.0 * seconds);
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {
            }
        }
    }
    
/////////////////////////////////////////////////////////////////////////
    public void reset() {

//init the location of the rocket to the center.
        whichStarHit = -1;
        rocketXPos = getWidth2()/2;
        rocketYPos = getHeight2()/2;
        rocketRight = true;
        
        for (int i=0;i < numMissles;i++)
        {
            missile[i] = new Missile();
        }
        
        
        rocketRot = 1;
        
        rocketSpeed = 0;
        rocketSpeedY = 0;
        
        for (int index = 0; index < numStars;index++) {
            starX[index] = (int)(Math.random()*getWidth2());
            starY[index] = (int)(Math.random()*getHeight2()); }
        

    }
/////////////////////////////////////////////////////////////////////////
    public void animate() {
        if (animateFirstTime) {
            animateFirstTime = false;
            if (xsize != getSize().width || ysize != getSize().height) {
                xsize = getSize().width;
                ysize = getSize().height;
            }
            outerSpaceImage = Toolkit.getDefaultToolkit().getImage("./outerSpace.GIF");
            
            explosionImage = Toolkit.getDefaultToolkit().getImage("./explosion.GIF");
            reset();
            bgSound = new sound("starwars.wav");
        }
        if (rocketSpeed == 0)
            rocketImage = Toolkit.getDefaultToolkit().getImage("./rocket.GIF");
        else
            rocketImage = Toolkit.getDefaultToolkit().getImage("./animRocket.GIF");
        
        if (rocketSpeed > 0)
            rocketRight = false;
        else if (rocketSpeed < 0)
            rocketRight = true;
        System.out.println(rocketRight);
        
        if (rocketSpeed <= 0)
            rocketRot = 1;
        else
            rocketRot = -1;
        
            
        if (bgSound.donePlaying)
            bgSound = new sound("starwars.wav");
        for (int index = 0; index < numStars;index++) { 
            starX[index]+= rocketSpeed;

            if (starX[index] < 0) {
            starX[index] = getWidth2();
            starY[index] = (int)(Math.random()*getHeight2());
            }
            
            else if (starX[index] > getWidth2()) {
                starX[index] =0;
                starY[index] = (int)(Math.random()*getHeight2());
                }
            
            if (rocketXPos+10  > starX[index] &&
                rocketXPos-10 < starX[index] &&  
                rocketYPos+10 > starY[index] &&
                rocketYPos-10 < starY[index])
                {
                starHit[index] = true;
                whichStarHit = index;
                System.out.println("Estoy aqui");
                }
            else
                whichStarHit = -1;
                    
        //if (timeCount % 2 == 0)
        rocketYPos +=rocketSpeedY;
        if (rocketYPos < 0 || rocketYPos > getHeight2() ) {
            rocketYPos -=rocketSpeedY;
            rocketSpeedY = 0;
        }
        
        }
       
        timeCount++;
}

////////////////////////////////////////////////////////////////////////////
    public void start() {
        if (relaxer == null) {
            relaxer = new Thread(this);
            relaxer.start();
        }
    }
////////////////////////////////////////////////////////////////////////////
    public void stop() {
        if (relaxer.isAlive()) {
            relaxer.stop();
        }
        relaxer = null;
    }
/////////////////////////////////////////////////////////////////////////
    public int getX(int x) {
        return (x + XBORDER);
    }

    public int getY(int y) {
        return (y + YBORDER + YTITLE);
    }

    public int getYNormal(int y) {
        return (-y + YBORDER + YTITLE + getHeight2());
    }
    
    
    public int getWidth2() {
        return (xsize - getX(0) - XBORDER);
    }

    public int getHeight2() {
        return (ysize - getY(0) - YBORDER);
    }
    public void readFile() {
        try {
            String inputfile = "info.txt";
            BufferedReader in = new BufferedReader(new FileReader(inputfile));
            String line = in.readLine();
            while (line != null) {
                String newLine = line.toLowerCase();
                if (newLine.startsWith("numstars"))
                {
                    String numStarsString = newLine.substring(9);
                    numStars = Integer.parseInt(numStarsString.trim());
                }
                line = in.readLine();
            }
            in.close();
        } catch (IOException ioe) {
        }
    }
    
}

class sound implements Runnable {
    Thread myThread;
    File soundFile;
    public boolean donePlaying = false;
    sound(String _name)
    {
        soundFile = new File(_name);
        myThread = new Thread(this);
        myThread.start();
    }
    
    
    public void run()
    {
        try {
        AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
        AudioFormat format = ais.getFormat();
    //    System.out.println("Format: " + format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine source = (SourceDataLine) AudioSystem.getLine(info);
        source.open(format);
        source.start();
        int read = 0;
        byte[] audioData = new byte[16384];
        while (read > -1){
            read = ais.read(audioData,0,audioData.length);
            if (read >= 0) {
                source.write(audioData,0,read);
            }
        }
        donePlaying = true;

        source.drain();
        source.close();
        }
        catch (Exception exc) {
            System.out.println("error: " + exc.getMessage());
            exc.printStackTrace();
        }
    }

}
class Missile
{
    static int currentMissle;
    
    boolean active;
    int xpos;
    int ypos;
    
    public void Missile () {
    active = false;
    
    }
    public void setCurrent(int _currentMissle)
    {
        currentMissle = _currentMissle;
    }
    public void setXPos(int _xpos)
    {
        xpos = _xpos;
    }
    public void setYPos(int _ypos)
    {
        ypos = _ypos;
    }
    
}