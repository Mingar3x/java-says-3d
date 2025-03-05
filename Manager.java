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
    private double maxTriangleDistance;
    private double minTriangleDistance;
    private double pixelsPerUnit;
    private Vector3 camDirection;
    private double renderPlaneWidth;
    private Plane renderPlane;
    Matrix projectionMatrix; //projection matrix, duh
    private Quaternion pointRotationQuaternion;
    private Vector3 camCenterPoint;
    
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
        maxTriangleDistance = 0;
        minTriangleDistance = c.far;
        pixelsPerUnit = getWidth()/renderPlaneWidth;
        renderPlaneWidth = c.getRenderPlaneWidth();
        camDirection = c.getDirectionVector();
        camCenterPoint = Vector3.add(Vector3.multiply(camDirection, c.getRenderPlaneDistance()), c.position);
        renderPlane = new Plane(Vector3.add(Vector3.multiply(camDirection, c.getRenderPlaneDistance()), c.position), camDirection);
        pointRotationQuaternion = createRotationQuaternion(c.getVorientation(), -c.getHorientation());
        
        //calculating camera matrix
        //Matrix viewMatrix = c.calculateViewMatrix();
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

                double[][] vertexArray = {{v.x},{v.y},{v.z},{1}};
                Matrix vertexMatrix = new Matrix(vertexArray);

                //view matrix
                Matrix viewCorrectedMatrix = viewMatrix.multiply(vertexMatrix);
                
                //projecting matrix
                //i don't think this is doing the right thing :(
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
                //
                System.out.println(v.x+", "+v.y);
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
        Camera c = new Camera(new Vector3(1,1,1),90);
        renderPlaneWidth = c.getRenderPlaneWidth();
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
    private void calculateTriangle(Triangle triangle)
    {
        Vector3 triangleCenter = triangle.getCenter();
        double distanceToTriangle = Vector3.subtract(triangleCenter, c.position).getMagnitude(); 
        if (distanceToTriangle > maxTriangleDistance)
            maxTriangleDistance = distanceToTriangle;
        else if (distanceToTriangle < minTriangleDistance)
            minTriangleDistance = distanceToTriangle;
        if 
        (
            Vector3.dotProduct(Vector3.subtract(triangleCenter, c.position), camDirection) <= 0 //is the triangle behind the camera?
            || distanceToTriangle >= c.far //is the triangle too far away?
            || distanceToTriangle <= c.near //is the triangle too close?
        ){
            return;
            //if the trianle meets one of the above contitions it is not eligable for rendering at this time
        }
           
        //clone the triangle's vertices:
        Vector3 triangleVertex1 = triangle.v1.clone();
        Vector3 triangleVertex2 = triangle.v2.clone();
        Vector3 triangleVertex3 = triangle.v3.clone();
        //the screen coords of the triangle, to be determined by the rest of the method.
        Point p1ScreenCoords = new Point();
        Point p2ScreenCoords = new Point();
        Point p3ScreenCoords = new Point();
        //will be set true if atleast one of the verticies is in camera's view. 
        boolean shouldDrawTriangle = false;

        //get intersection with render plane 
        triangleVertex1 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex1, c.position), c.position, renderPlane);

        //rotate the point: 
        triangleVertex1 = Vector3.rotate(Vector3.subtract(triangleVertex1, camCenterPoint), pointRotationQuaternion);

        //check if it's in the fov
        if ((Math.abs(triangleVertex1.x) < renderPlaneWidth/2*1.2 && Math.abs(triangleVertex1.y) < renderPlaneWidth*((double)getHeight()/(double)getWidth())/2))
            shouldDrawTriangle = true;

        //scale to the screen coordinates
        p1ScreenCoords.x = (int)(getWidth()/2 + triangleVertex1.x*pixelsPerUnit);
        p1ScreenCoords.y = (int)(getHeight()/2 - triangleVertex1.y*pixelsPerUnit);

        //repeat for each of the other two vertices
        triangleVertex2 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex2, c.position), c.position, renderPlane);
        triangleVertex2 = Vector3.rotate(Vector3.subtract(triangleVertex2, camCenterPoint), pointRotationQuaternion);
        if ((Math.abs(triangleVertex2.x) < renderPlaneWidth/2 && Math.abs(triangleVertex2.y) < renderPlaneWidth*((double)getHeight()/getWidth())/2))
            shouldDrawTriangle = true;
        p2ScreenCoords.x = (int)(getWidth()/2 + triangleVertex2.x*pixelsPerUnit);
        p2ScreenCoords.y = (int)(getHeight()/2 - triangleVertex2.y*pixelsPerUnit);

        triangleVertex3 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex3, c.position), c.position, renderPlane);
        triangleVertex3 = Vector3.rotate(Vector3.subtract(triangleVertex3, camCenterPoint), pointRotationQuaternion);
        if ((Math.abs(triangleVertex3.x) < renderPlaneWidth/2 && Math.abs(triangleVertex3.y) < renderPlaneWidth*((double)getHeight()/getWidth())/2))
            shouldDrawTriangle = true;
        p3ScreenCoords.x = (int)(getWidth()/2 + triangleVertex3.x*pixelsPerUnit);
        p3ScreenCoords.y = (int)(getHeight()/2 - triangleVertex3.y*pixelsPerUnit);

    
        if (shouldDrawTriangle){
            //TODO
        }
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode(); // Get the virtual key code
                switch (keyCode) {
                    case KeyEvent.VK_W:
                        c.translate(new Vector3(0,-50,0));
                        break;
                    case KeyEvent.VK_A:
                    c.translate(new Vector3(50,0,0));
                        break;
                    case KeyEvent.VK_S:
                    c.translate(new Vector3(0,50,0));
                        break;
                    case KeyEvent.VK_D:
                    c.translate(new Vector3(0,50,0));
                        break;
                    case KeyEvent.VK_SPACE:
                    c.translate(new Vector3(0,0,50));
                        break;
                    case KeyEvent.VK_SHIFT:
                        c.translate(new Vector3(0,0,-50));
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