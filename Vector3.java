import java.util.Vector;

public class Vector3 {
    public double x;
    public double y;
    public double z;
    Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public void toNewPosition(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
    public Vector3 clone(){
        return new Vector3(x, y, z);
    }
    public double getMagnitude()
    {
        return Math.sqrt(x*x+z*z + y*y);
    }
    public static Vector3 subtract(Vector3 a, Vector3 b) 
    { 
        return new Vector3(a.x-b.x, a.y-b.y, a.z-b.z);
    }
    public static Vector3 angleToVector(double yaw, double pitch)
    {
        double cosPitch = Math.cos(pitch);
        return new Vector3(Math.sin(yaw)*cosPitch, Math.sin(pitch), Math.cos(yaw)*cosPitch);
    }
    public static double dotProduct(Vector3 a, Vector3 b)
    {
        return a.x*b.x+a.y*b.y+a.z*b.z;
    }
    public void add(Vector3 vectorIn)
    {
        x = Vector3.add(this, vectorIn).x;
        y = Vector3.add(this, vectorIn).y;
        z = Vector3.add(this, vectorIn).z;
    }
    public static Vector3 add(Vector3 a, Vector3 b)
    {
        return new Vector3(a.x+b.x, a.y+b.y, a.z+b.z);
    }
    public static Vector3 multiply(Vector3 vector, double scalar)
    {
        return new Vector3(vector.x*scalar, vector.y*scalar, vector.z*scalar);
    }
    public static Vector3 getIntersectionPoint(Vector3 lineDirection, Vector3 linePoint, Plane plane)
    {
        return Vector3.add(linePoint, Vector3.multiply(lineDirection, Vector3.dotProduct(Vector3.subtract(plane.pointOnPlane, linePoint), plane.normal)/Vector3.dotProduct(lineDirection, plane.normal)));
    }
    public static Vector3 crossProduct(Vector3 a, Vector3 b)
    {
        return new Vector3(a.y*b.z-a.z*b.y, a.z*b.x-a.x*b.z, a.x*b.y-a.y*b.x);
    }
    public Vector3 getNormalized()
    {
        if (getSqrMagnitude() != 1)
        {
            double magnitude = getMagnitude();
            return new Vector3(x/magnitude, y/magnitude, z/magnitude);
        }
        else
            return this;
    }
    public double getSqrMagnitude()
    {
        return x*x+z*z+y*y;
    }
    public static Vector3 rotate(Vector3 v, Quaternion q)
    {
        final double w = -(q.x*v.x+q.y*v.y+q.z*v.z);
        final double x = q.w*v.x + q.y*v.z-q.z*v.y;
        final double y = q.w*v.y + q.z*v.x-q.x*v.z;
        final double z = q.w*v.z + q.x*v.y-q.y*v.x;

        return new Vector3
        (
            q.w*x - w*q.x - y*q.z + z*q.y,
            q.w*y - w*q.y - z*q.x + x*q.z,
            q.w*z - w*q.z - x*q.y + y*q.x
        );
    }
    //gets the distance between two points as a double, duh
    public static double getDiagonalDistance(Vector3 a, Vector3 b){
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double dz = b.z - a.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
    public Vector3 getPosition(){
        return this;
    }
}