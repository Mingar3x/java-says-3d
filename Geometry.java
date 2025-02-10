import java.awt.*;
import java.util.*;

public class Geometry {
    public Geometry(){
        //constructor
    }
    @SuppressWarnings("unused")
    public void makeStaticPlane(double x1, double x2, double y1, double y2, double z1, double z2, Color c1, Color c2){
        VertexPool vp = new VertexPool();
        TriangleGroup tg = new TriangleGroup(new ArrayList<Triangle>());
        Vector3 v1 = vp.getSharedVertex(new Vector3(x1, y1, z1));
        Vector3 v2 = vp.getSharedVertex(new Vector3(x2, y2, z2));
        Vector3 corner1 = vp.getSharedVertex(new Vector3(x1, (y2+y1)/2, z2));
        Vector3 corner2 = vp.getSharedVertex(new Vector3(x2, (y2+y1)/2, z1));
        
        Triangle t1 = tg.add(new Triangle(v1, v2, corner1, c1));
        Triangle t2 = tg.add(new Triangle(v1, v2, corner2, c2));

        //adding this little mesh to the main hashmap
        Manager.staticMeshMap.add(new GeometryGroup(vp, tg));

    }
}