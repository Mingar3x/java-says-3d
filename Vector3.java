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

}