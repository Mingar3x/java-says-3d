import java.awt.*;

public class Line {

    private Vector3 point1;
    private Vector3 point2;
    public Color c;
    //constructor
    public Line(Vector3 point1, Vector3 point2) {
        this.point1 = point1;
        this.point2 = point2;
        c = Color.BLACK;
    }
    public Line(Vector3 point1, Vector3 point2, Color c) {
        this.point1 = point1;
        this.point2 = point2;
        this.c = c;
    }
    public Vector3 getPoint1() {
        return point1;
    }

    public void setPoint1(Vector3 point1) {
        this.point1 = point1;
    }

    public Vector3 getPoint2() {
        return point2;
    }

    public void setPoint2(Vector3 point2) {
        this.point2 = point2;
    }

    public Vector3 getDirection() {
        return new Vector3(point2.x - point1.x,
                            point2.y - point1.y,
                            point2.z - point1.z);
    }

    public Vector3 getPointOnLine(double t) {
        double x = point1.x + t * getDirection().x;
        double y = point1.y + t * getDirection().y;
        double z = point1.z + t * getDirection().z;
        return new Vector3(x, y, z);
    }

    // Override 
    public String toString() {
        return "Line [point1=" + point1 + ", point2=" + point2 + "]";
    }
}
