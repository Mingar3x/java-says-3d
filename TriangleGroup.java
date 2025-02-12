import java.util.ArrayList;

public class TriangleGroup {
    public ArrayList<Triangle> triangles = new ArrayList<Triangle>();
    //arraylist constructor
    public TriangleGroup(ArrayList<Triangle> triangles) {
        this.triangles = triangles;
    }
    //array constructor
    public TriangleGroup(Triangle[] triangles) {
        for (Triangle t : triangles) {
            this.triangles.add(t);
        }
    }

    //adding, and also returns
    public Triangle add(Triangle t) {
        triangles.add(t);
        return t;
    }

    //adding, plural
    public void add(Triangle[] t) {
        for (Triangle tri : t) {
            triangles.add(tri);
        }
    }
    public TriangleGroup clone(){
        ArrayList<Triangle> temp = new ArrayList<Triangle>();
        for (Triangle t: triangles){
            temp.add(t.clone());
        }
        return new TriangleGroup(temp);
    }
}