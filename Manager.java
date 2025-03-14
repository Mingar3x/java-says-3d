//imports
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import javax.swing.*;

import java.util.Random;
import java.util.ArrayList; 
//@SuppressWarnings("unused")

public class Manager extends JPanel implements KeyListener , MouseMotionListener {
    private JFrame myFrame;
    private double mouseSensitivity=0.001;
    private int timeBetweenGameTicks = 10; //milliseconds
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
    private Quaternion pointRotationQuaternion;
    private Vector3 camCenterPoint;
    
    Camera c; //camera object, will be initalized in initalizeScreen()

    //VertexPool will not work for moving vertices, probably
    static ArrayList<GeometryGroup> staticMeshMap = new ArrayList<>();
    static ArrayList<Line> linesToDraw = new ArrayList<>();
    ArrayList<Triangle> screenSpaceMap = new ArrayList<>(); //used to store transformed triangles to render

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
    //by the rivers of babyleon
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;


        if (c==null){ //if the camera for some reason won't load, the screen goes grey until it does
            g2.setColor(Color.GRAY);
            g2.fillRect(0, 0, getWidth(), getHeight());
            return;
        }

        maxTriangleDistance = 0;
        minTriangleDistance = c.far;
        pixelsPerUnit = getWidth()/renderPlaneWidth;
        renderPlaneWidth = c.getRenderPlaneWidth();
        camDirection = c.getDirectionVector();
        camCenterPoint = Vector3.add(Vector3.multiply(camDirection, c.getRenderPlaneDistance()), c.getPosition());
        renderPlane = new Plane(Vector3.add(Vector3.multiply(camDirection, c.getRenderPlaneDistance()), c.getPosition()), camDirection);
        pointRotationQuaternion = BigUtils.createRotationQuaternion(c.getVorientation(), -c.getHorientation());
        renderPlane = new Plane(Vector3.add(Vector3.multiply(camDirection, c.getRenderPlaneDistance()), c.getPosition()), camDirection);
        

        g2.setColor(Color.GREEN);
        g2.fillRect(0, 0, getWidth(), getHeight()); //fill the screen with black, 
       // g2.translate(getWidth() / 2, getHeight() / 2); //then center the image

        //resetting the screen space buffer map thing
        screenSpaceMap = new ArrayList<Triangle>();

        //find screen space for each tri
        for (GeometryGroup entry : staticMeshMap) { 
            TriangleGroup value = entry.triangleGroup;
            for (Triangle t : value.triangles) {
                triToScreen(t);
            }
        }

        //TODO
        //DOES NOT WORK!!!
        int ix = 0;
        for (Triangle t : screenSpaceMap) { 
                Path2D.Double path = new Path2D.Double();
                path.moveTo(t.v1.x, t.v1.y);
                path.lineTo(t.v2.x, t.v2.y);
                path.lineTo(t.v3.x, t.v3.y);
                path.lineTo(t.v1.x, t.v1.y);
                path.closePath();
                g2.setColor(t.color);
                //g2.fill(path);
                ix++;
         }
         for (Line line : linesToDraw) {
            renderLine(g2, line);
        }
    }
    public void initalizeScreen(){
        // yo so this ↓↓ is the camera
        c = new Camera(new Vector3(0,0,0),90);
        c.lookAt(new Vector3(0,0,-1));
        renderPlaneWidth = c.getRenderPlaneWidth();        
        geo.makeStaticPlane(-5,5,5,-5,-5,-5,Color.PINK,Color.ORANGE);
        geo.makeStaticPlane(-50,50,50,-50,-50,-50,Color.RED,Color.BLUE);
        Line l = new Line(new Vector3(-1000000, 0, 0),new Vector3(1000000, 0, 0),Color.blue); //X AXIS
        linesToDraw.add(l);
        l = new Line(new Vector3(0, -1000000, 0),new Vector3(0, 1000000, 0),Color.RED); //Y AXIS
        linesToDraw.add(l);
        l = new Line(new Vector3(0, 0, -1000000),new Vector3(0, 0, 1000000),Color.PINK); //z AXIS
        linesToDraw.add(l);
        //invisable cursor
        myFrame.setCursor( myFrame.getToolkit().createCustomCursor(
                   new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB ),
                   new Point(),
                   null ) );
    }
    private void renderLine(Graphics2D g2, Line line) {
        Vector3 start = line.getPoint1();
        Vector3 end = line.getPoint2();
    
        start = transformToScreen(start);
        end = transformToScreen(end);
    
        g2.setColor(line.c);
        g2.drawLine((int)start.x, (int)start.y, (int)end.x, (int)end.y);
    }
    
    private Vector3 transformToScreen(Vector3 point) {
        point = Vector3.getIntersectionPoint(Vector3.subtract(point, c.getPosition()), c.getPosition(), renderPlane);
        point = Vector3.rotate(Vector3.subtract(point, camCenterPoint), pointRotationQuaternion);

        int screenX = (int) (getWidth() / 2 + point.x * pixelsPerUnit);
        int screenY = (int) (getHeight() / 2 - point.y * pixelsPerUnit);
    
        return new Vector3(screenX, screenY, point.z);
    }
    public void gameTick() {
        //other calculations and whatnot
        repaint();
    }

    public void keyTyped(KeyEvent e) { 
        //Hey! I'm not implemented! Fix that!
    }
    
    private void triToScreen(Triangle triangle)
    {
        Vector3 triangleCenter = triangle.getCenter();
        double distanceToTriangle = Vector3.subtract(triangleCenter, c.getPosition()).getMagnitude(); 
        if (distanceToTriangle > maxTriangleDistance)
            maxTriangleDistance = distanceToTriangle;
        else if (distanceToTriangle < minTriangleDistance)
            minTriangleDistance = distanceToTriangle;
        if 
        (
            Vector3.dotProduct(Vector3.subtract(triangleCenter, c.getPosition()), camDirection) <= 0 //is the triangle behind the camera?
            || distanceToTriangle >= c.far //is the triangle too far away?
            || distanceToTriangle <= c.near //is the triangle too close?
        ){
            System.out.println("skipped a triangle");
            System.out.println("near plane error:"+(distanceToTriangle <= c.near));
            System.out.println("far plane error:"+(distanceToTriangle >= c.far));
            System.out.println("behind camera error:"+(Vector3.dotProduct(Vector3.subtract(triangleCenter, c.getPosition()), camDirection) <= 0));
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
        triangleVertex1 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex1, c), c.getPosition(), renderPlane);

        //rotate the point
        triangleVertex1 = Vector3.rotate(Vector3.subtract(triangleVertex1, camCenterPoint), pointRotationQuaternion);

        //check if it's in fov
        if ((Math.abs(triangleVertex1.x) < renderPlaneWidth/2*1.2 && Math.abs(triangleVertex1.y) < renderPlaneWidth*((double)getHeight()/(double)getWidth())/2))
            shouldDrawTriangle = true;

        //scale to  screen coord
        p1ScreenCoords.x = (int)(getWidth()/2 + triangleVertex1.x*pixelsPerUnit);
        p1ScreenCoords.y = (int)(getHeight()/2 - triangleVertex1.y*pixelsPerUnit);

        //repeat for other points
        triangleVertex2 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex2, c.getPosition()), c.getPosition(), renderPlane);
        triangleVertex2 = Vector3.rotate(Vector3.subtract(triangleVertex2, camCenterPoint), pointRotationQuaternion);
        if ((Math.abs(triangleVertex2.x) < renderPlaneWidth/2 && Math.abs(triangleVertex2.y) < renderPlaneWidth*((double)getHeight()/getWidth())/2))
            shouldDrawTriangle = true;
        p2ScreenCoords.x = (int)(getWidth()/2 + triangleVertex2.x*pixelsPerUnit);
        p2ScreenCoords.y = (int)(getHeight()/2 - triangleVertex2.y*pixelsPerUnit);

        triangleVertex3 = Vector3.getIntersectionPoint(Vector3.subtract(triangleVertex3, c.getPosition()), c.getPosition(), renderPlane);
        triangleVertex3 = Vector3.rotate(Vector3.subtract(triangleVertex3, camCenterPoint), pointRotationQuaternion);
        if ((Math.abs(triangleVertex3.x) < renderPlaneWidth/2 && Math.abs(triangleVertex3.y) < renderPlaneWidth*((double)getHeight()/getWidth())/2))
            shouldDrawTriangle = true;
        p3ScreenCoords.x = (int)(getWidth()/2 + triangleVertex3.x*pixelsPerUnit);
        p3ScreenCoords.y = (int)(getHeight()/2 - triangleVertex3.y*pixelsPerUnit);

        if (shouldDrawTriangle){
            Triangle screenTri = new Triangle(new Vector3(p1ScreenCoords.x, p1ScreenCoords.y, Vector3.getDiagonalDistance(triangleVertex1, c.getPosition())),
            new Vector3(p2ScreenCoords.x, p2ScreenCoords.y, Vector3.getDiagonalDistance(triangleVertex2, c.getPosition())),
            new Vector3(p3ScreenCoords.x, p3ScreenCoords.y, Vector3.getDiagonalDistance(triangleVertex3, c.getPosition())), 
            triangle.color);
            screenSpaceMap.add(screenTri);
        }
    }
    public void quietMouseMove() {
        try {
            // Temporarily remove the listener
            myFrame.removeMouseMotionListener(this);
            
            // Move the mouse using Robot (without triggering mouseMoved)
            Robot robot = new Robot();
            Point center = myFrame.getLocationOnScreen();
            int x = center.x + myFrame.getWidth() / 2;
            int y = center.y + myFrame.getHeight() / 2;
            robot.mouseMove(x, y);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    myFrame.addMouseMotionListener(Manager.this);
                }
            });
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode(); // Get the virtual key code
                switch (keyCode) {
                    case KeyEvent.VK_W:
                        c.translate(new Vector3(50,0,0));
                        break;
                    case KeyEvent.VK_A:
                    c.translate(new Vector3(0,-50,0));
                        break;
                    case KeyEvent.VK_S:
                    c.translate(new Vector3(-50,0,0));
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
    }

    public void keyReleased(KeyEvent e) {
        //Hey! I'm not implemented! Fix that!
    }
    public void mouseDragged(MouseEvent e) {
        //Hey! I'm not implemented! Fix that!
}
    public void mouseMoved(MouseEvent e) {
        if (e.getX()==0&&e.getY()==0){
            return;
        }
        double turnAmountX = e.getX() - screenWidth/2;
        double turnAmountY = e.getY() - screenHeight/2;
        c.updateOrientation(turnAmountX, turnAmountY, mouseSensitivity);
        quietMouseMove();
    }
    public static void main(String[] args) {
        new Manager();
    }
}