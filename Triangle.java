import java.awt.*;
public class Triangle {
    Vector3 v1;
    Vector3 v2;
    Vector3 v3;
    Color color;
    Triangle(Vector3 v1, Vector3 v2, Vector3 v3, Color color) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = color;
    }
    public Triangle clone(VertexPool vp){
        return new Triangle(vp.getSharedVertex(v1),vp.getSharedVertex(v2),vp.getSharedVertex(v3),color);
    }
    public Vector3 getCenter()
    {
        return new Vector3
        (
            (v1.x + v2.x + v3.x)/3, 
            (v1.y + v2.y + v3.y)/3,
            (v1.z + v2.z + v3.z)/3
        );
    }
}