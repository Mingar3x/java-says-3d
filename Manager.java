//imports
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;


import javax.swing.JFrame;
import javax.swing.JPanel;
import java.util.Random;
import javax.swing.Timer;
import java.util.ArrayList; 
import java.util.HashMap;
import java.util.Map;

//this ↓↓ makes the angry yellows go away :) 
//@SuppressWarnings("unused")

public class Manager extends JPanel implements KeyListener , MouseMotionListener {
    private JFrame myFrame;
    private double mouseSensitivity=1.0;
    private int timeBetweenGameTicks = 100; //milliseconds
    private Random r = new Random();
    Geometry geo = new Geometry();//geometry.java instance

    Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
    int screenWidth = (int)size.getWidth();
    int screenHeight = (int)size.getHeight();
    int screenCenterWidth = screenWidth/2;
    int screenCenterHeight = screenHeight/2;    

    Matrix projectionMatrix; //projection matrix, duh 
    
    Camera c; //camera object, will be initalized in initalizeScreen()

    //VertexPool will not work for moving vertices, probably
    static ArrayList<GeometryGroup> staticMeshMap = new ArrayList<>();


    //constructor
    private Manager() {
        myFrame = new JFrame("Game!");
        
        myFrame.add(this);
        this.addKeyListener(this);
        this.addMouseMotionListener(this);
        this.setFocusable(true);

        myFrame.setSize(screenWidth, screenHeight);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setUndecorated(true);
        myFrame.setVisible(true);
        
        initalizeScreen();
        
        Timer gameTimer = new Timer(timeBetweenGameTicks, e -> gameTick());
        gameTimer.start();
    }
    //this ↓↓ is the drawing method that is called every frame
    //
    //by the rivers of babyleon
    public void paintComponent(Graphics g) {
        //calculating camera matrix
        Matrix viewMatrix = c.calculateViewMatrix();
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.GREEN);
        g2.fillRect(0, 0, getWidth(), getHeight()); //fill the screen with black, then
        g2.translate(getWidth() / 2, getHeight() / 2);          //center the image

        //this converts the worldspace hashmap into a temporary screen space hashmap
        //recalculated each frame
        ArrayList<GeometryGroup> screenSpaceMap = new ArrayList<>();
        
        for (GeometryGroup gee : staticMeshMap) {
            //reset the screenSpaceMap each frame, not super efficent, but whatever
            screenSpaceMap.add(gee.clone());
        }
        //applying the projection matrix and view matrix to each vertex
        //basicly moving it from world space to screen space
        for (GeometryGroup entry : screenSpaceMap) { 
            VertexPool value = entry.vertexPool;
            for (Vector3 v : value.sharedVertices) {

                //make matrix from the vertex
                double[][] vertexArray = {{v.x},{v.y},{v.z},{1}};
                Matrix vertexMatrix = new Matrix(vertexArray);

                //applying view matrix
                Matrix viewCorrectedMatrix = viewMatrix.multiply(vertexMatrix);
                
                //projecting each vertex
                Matrix projectedMatrix = projectionMatrix.multiply(viewCorrectedMatrix);

                //homogenous division, this method returns an array with 3 axles
                double[] ScSpArray = BigUtils.to3D(new double[]{
                    projectedMatrix.get(0, 0), 
                    projectedMatrix.get(1, 0), 
                    projectedMatrix.get(2, 0), 
                    projectedMatrix.get(3, 0)});
                
                Matrix ScSpMatrix = new Matrix(new double[][]{{ScSpArray[0], ScSpArray[1], ScSpArray[2],1}});
                //ScSpMatrix.display();
                Vector3 ssv3 = new Vector3(ScSpArray[0], ScSpArray[1], ScSpArray[2]);
                

                v.toNewPosition(ssv3.x, ssv3.y, ssv3.z);
                //System.out.println(v.x+", "+v.y);
                g2.setColor(Color.BLACK);
                g2.fillRect((int)v.x,(int)v.y,5,5);
            }
        }

        //this is NOT the right way to rasterize polygons!!!! :( FIX LATER!!!!!!
        for (GeometryGroup entry : screenSpaceMap) { 
            TriangleGroup value = entry.triangleGroup;
            for(Triangle t: value.triangles){
                Path2D.Double path = new Path2D.Double();
                path.moveTo(t.v1.x, t.v1.y);
                path.lineTo(t.v2.x, t.v2.y);
                path.lineTo(t.v3.x, t.v3.y);
                path.lineTo(t.v1.x, t.v1.y);
                path.closePath();
                g2.setColor(t.color);
                g2.fill(path);
            }
         }
        System.out.println(c);
    }
    public void initalizeScreen(){
        // yo so this ↓↓ is the camera
        c = new Camera(0, 0, 1, new Vector3(0, 0, 0));
        projectionMatrix = c.calculateProjectionMatrix(screenWidth, screenHeight);
        
        geo.makeStaticPlane(-5,5,5,-5,-5,-5,Color.PINK,Color.ORANGE);
        geo.makeStaticPlane(-50,50,50,-50,-50,-50,Color.RED,Color.BLUE);
    }
    public Vector3 toRealScreenSpace(Vector3 v){
        return new Vector3((2 * v.x)/screenWidth, (2 * v.y)/screenHeight, v.z);
    }
    public void gameTick() {
        //other calculations and whatnot
        repaint();
    }

    public void keyTyped(KeyEvent e) { 
        //Hey! I'm not implemented! Fix that!
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode(); // Get the virtual key code
                switch (keyCode) {
                    case KeyEvent.VK_W:
                        c.translate(0,-50,0);
                        break;
                    case KeyEvent.VK_A:
                    c.translate(50,0,0);
                        break;
                    case KeyEvent.VK_S:
                    c.translate(0,50,0);
                        break;
                    case KeyEvent.VK_D:
                    c.translate(0,50,0);
                        break;
                    case KeyEvent.VK_SPACE:
                    c.translate(0,0,50);
                        break;
                    case KeyEvent.VK_SHIFT:
                        c.translate(0,0,-50);
                        break;
                    case KeyEvent.VK_ESCAPE:
                        System.out.println("Exit game!");
                        System.exit(0); // Quit the application
                        break;
                }
                myFrame.repaint();
                //System.out.println(c);
    }

    public void keyReleased(KeyEvent e) {
        //Hey! I'm not implemented! Fix that!
    }
    public void mouseDragged(MouseEvent e) {
        //Hey! I'm not implemented! Fix that!
        //System.out.println("hi"); 
}
    public void mouseMoved(MouseEvent e) {
        //nothing doing, buster
        myFrame.repaint();
    }
    public static void main(String[] args) {
        new Manager();
    }
}