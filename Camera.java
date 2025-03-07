public class Camera {
    public Vector3 position;
    private Vector3 directionVector; 
    public double fov = 90;
    public Vector3 up = new Vector3(0, 1, 0);
    //yaw
    private double hAngle; 
    //pitch
    private double vAngle; 
    //clippy plains
    double near = 0.01;
    double far = 10000;
    private double renderPlaneDistance = 50;
    

    private double renderPlaneWidth;
    Camera(Vector3 p, double f) {
        hAngle = 0;
        vAngle = 0;
        position = p;
        directionVector = Vector3.angleToVector(hAngle, vAngle);
        setFov(f);
        directionVector = Vector3.angleToVector(hAngle, vAngle);
    }
    public void setFov(double fovIn)
    {
        fov = Math.toRadians(fovIn);
        //calculates a bespoke value based on the FOV
        renderPlaneWidth = Math.tan(fov/2)*renderPlaneDistance*2;
    }

    public void translate(Vector3 t) {
        position = position.add(t);
    }
    public void lookAt(Vector3 pos)
    {
        hAngle = (pos.x-position.x < 0)? -Math.atan((pos.z-position.z)/(pos.x-position.x))-Math.PI/2 : Math.PI/2-Math.atan((pos.z-position.z)/(pos.x-position.x));

        vAngle = Math.atan((pos.y-position.y)/(Math.sqrt((pos.x-position.x)*(pos.x-position.x) + (pos.z-position.z)*(pos.z-position.z))));
        
        hAngle%=Math.PI;
        vAngle%=Math.PI;
        directionVector = Vector3.angleToVector(hAngle, vAngle);
    }
    public static double dotProduct(Vector3 a, Vector3 b)
    {
        return a.x*b.x+a.y*b.y+a.z*b.z;
    }
    public static Vector3 crossProduct(Vector3 a, Vector3 b)
    {
        return new Vector3(a.y*b.z-a.z*b.y, a.z*b.x-a.x*b.z, a.x*b.y-a.y*b.x);
    }
    public double getRenderPlaneWidth(){
        return renderPlaneWidth;
    }
    @Override
    public String toString() {
        return position.toString();
    }
    public Vector3 getDirectionVector() {
        return directionVector;
    }
    public double getHorientation()
    {
        return hAngle;
    }

    public double getVorientation()
    {
        return vAngle;
    }
    public double getRenderPlaneDistance() {
        return renderPlaneDistance;
    }
}