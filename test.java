public class test {
    public static void main(String[] args) {
        Camera camera = new Camera(0, 0, 5, new Vector3(0, 0, 0));

        Matrix viewMatrix = camera.calculateViewMatrix();

        System.out.println("View Matrix dimentions: (" + viewMatrix.columns+", "+ viewMatrix.rows+")");
        viewMatrix.display();
    }
}
//run and test arbitrary code here