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
        for (GeometryGroup entry : screenSpaceMap) { 
            VertexPool value = entry.vertexPool;
            for (Map.Entry<Vector3, Vector3> vertexEntry : value.sharedVertices.entrySet()) {
                Vector3 v = vertexEntry.getValue();
                Vector3 vertexKey = vertexEntry.getKey();

                //make matrix of vertex
                double[][] vertexArray = {
                    {v.x},
                    {v.y},
                    {v.z},
                    {1}
                };
                
                Matrix vertexMatrix = new Matrix(vertexArray);

                //applying view matrix
                Matrix viewCorrectedMatrix = viewMatrix.multiply(vertexMatrix);
                
                //projecting each vertex
                Matrix projectedMatrix = projectionMatrix.multiply(viewCorrectedMatrix);

                //testing
                // System.out.println("vertexMatrix rows: " + vertexMatrix.getRows() + ", columns: " + vertexMatrix.getColumns());
                // vertexMatrix.display();
                // System.out.println("viewMatrix rows: " + viewMatrix.getRows() + ", columns: " + viewMatrix.getColumns());
                // viewMatrix.display();
                // System.out.println("viewCorrectedMatrix rows: " + viewCorrectedMatrix.getRows() + ", columns: " + viewCorrectedMatrix.getColumns());
                // viewCorrectedMatrix.display();
                // System.out.println("projectionMatrix rows: " + projectionMatrix.getRows() + ", columns: " + projectionMatrix.getColumns());
                // projectionMatrix.display();
                // System.out.println("projectedMatrix rows: " + projectedMatrix.getRows() + ", columns: " + projectedMatrix.getColumns());
                // projectedMatrix.display();

                //homogenous division, this method returns an array with 3 axles
                double[] normalizedArray = BigUtils.to3D(new double[]{
                    projectedMatrix.get(0, 0), 
                    projectedMatrix.get(1, 0), 
                    projectedMatrix.get(2, 0), 
                    projectedMatrix.get(3, 0)});
                Matrix normalizedMatrix = new Matrix(new double[][]{{normalizedArray[0], normalizedArray[1], normalizedArray[2],1}});
                //normalizedMatrix.display();
                Vector3 ssv3 = new Vector3(normalizedArray[0], normalizedArray[1], normalizedArray[2]);
                //System.out.println(ssv3);
                // g.setColor(Color.RED);
                // System.out.println("ok, drawing vertex at ("+ssv3.x+", "+ssv3.y+")");
                // g.fillRect((int)ssv3.x, (int)ssv3.y, 5, 5);
                value.sharedVertices.remove(vertexKey);  // Remove the old entry
                v = ssv3;
                
                value.sharedVertices.add(v);
                

            }
        }

        //this is NOT the best way to rasterize polygons!!!! :( FIX LATER!!!
        for (Map.Entry<VertexPool, TriangleGroup> entry : screenSpaceMap.entrySet()) { 
            TriangleGroup value = entry.getValue();
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


        //orthogantic rendering, dont use
        // for (Map.Entry<VertexPool, TriangleGroup> entry : staticMeshHashmap.entrySet()) { 
        //     TriangleGroup value = entry.getValue();
        //     for(Triangle t: value.triangles){
        //         Path2D.Double path = new Path2D.Double();
        //         path.moveTo(t.v1.x, t.v1.y);
        //         path.lineTo(t.v2.x, t.v2.y);
        //         path.lineTo(t.v3.x, t.v3.y);
        //         path.lineTo(t.v1.x, t.v1.y);
        //         path.closePath();
        //         g2.setColor(t.color);
        //         g2.fill(path);
        //     }
        //  }
        
    }
    public void initalizeScreen(){
        // yo so this ↓↓ is the camera
        c = new Camera(0, 0, 150, new Vector3(0, 100, 0));
        projectionMatrix = c.calculateProjectionMatrix(screenWidth, screenHeight);
        //geo.makeStaticPlane(100,-100,100,-100,100,-100,Color.RED,Color.BLUE);
        geo.makeStaticPlane(-500,500,500,-500,-500,-500,Color.RED,Color.BLUE);
    }
    public void gameTick() {
        //other calculations and shit

        repaint();
    }

    public void keyTyped(KeyEvent e) { 
        //Hey! I'm not implemented! Fix that!
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode(); // Get the virtual key code
                switch (keyCode) {
                    case KeyEvent.VK_W:
                        c.translate(0,-5,0);
                        break;
                    case KeyEvent.VK_A:
                    c.translate(5,0,0);
                        break;
                    case KeyEvent.VK_S:
                    c.translate(0,5,0);
                        break;
                    case KeyEvent.VK_D:
                        System.out.println("Move right!");
                        break;
                    case KeyEvent.VK_SPACE:
                    c.translate(0,5,0);
                        break;
                    case KeyEvent.VK_SHIFT:
                        c.translate(0,-5,0);
                        break;
                    case KeyEvent.VK_ESCAPE:
                        System.out.println("Exit game!");
                        System.exit(0); // Quit the application
                        break;
                }
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