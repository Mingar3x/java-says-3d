//this is a class intended to link a triangle group to its respective vertex group
public class GeometryGroup {
    public VertexPool vertexPool;
    public TriangleGroup triangleGroup;

    public GeometryGroup(VertexPool v, TriangleGroup t){
        vertexPool = v;
        triangleGroup = t;
    }
    public GeometryGroup clone(){
        VertexPool vp=vertexPool.clone();
        TriangleGroup tg=triangleGroup.clone(vp);
        return new GeometryGroup(vp, tg);
    }
}
