//this is a class intended to link a triangle group to its respective vertex group
public class GeometryGroup {
    public VertexPool vertexPool;
    public TriangleGroup triangleGroup;

    public GeometryGroup(VertexPool v, TriangleGroup t){
        vertexPool = v;
        triangleGroup = t;
    }
    public GeometryGroup clone(){
        return new GeometryGroup(vertexPool, triangleGroup);
    }
}
