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
    public Vector3 add(Vector3 vectorIn)
    {
        return Vector3.add(this, vectorIn);
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
}