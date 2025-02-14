import java.util.ArrayList;
//the whole point of this class is to store vertices so
//they can be shared within continous masses, saving memory
public class VertexPool {
    //THIS DOES NOT WORK FOR VERTICES THAT MOVE IN WORLD SPACE I THINK
    //NEED TO FIND WORKAROUND LATER

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
        sharedVertices.add(Location);
        return Location;
    }

    //delete function
    public void clear() {
        sharedVertices.clear();
    }
    public VertexPool clone(){
        return new VertexPool(sharedVertices);
    }
}