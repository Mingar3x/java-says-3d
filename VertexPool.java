import java.util.ArrayList;
//the whole point of this class is to store vertices so
//they can be shared within continous masses, saving memory
public class VertexPool {
    //THIS DOES NOT WORK FOR VERTICES THAT MOVE IN WORLD SPACE
    //NEED TO FIND WORKAROUND LATER

    //this is a hashmap that stores vertices, and each vertex uses its location as a key. I dont recall why i chose to do this.
    //public Map<Vector3, Vector3> sharedVertices = new HashMap<>();
    public ArrayList<Vector3> sharedVertices = new ArrayList<>();
    public VertexPool(){

    }
    public VertexPool(ArrayList<Vector3> t){
        for (Vector3 v: t){
            sharedVertices.add(v.clone());
        }
    }
    // Retrieve or create a shared vertex
    public Vector3 getSharedVertex(Vector3 Location) {
        // If the vertex exists, return it
        //i REALLY hope this works because otherwise this whole project is cooked freaky style :(
        if (sharedVertices.contains(Location)) {
            int index = sharedVertices.indexOf(Location);
            return sharedVertices.get(index);
        }
        Vector3 newVertex = Location;
        sharedVertices.add(newVertex);
        return newVertex;
    }

    //delete function
    public void clear() {
        sharedVertices.clear();
    }
    public VertexPool clone(){
        return new VertexPool(sharedVertices);
    }
}