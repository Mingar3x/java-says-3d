public class Camera {
    public double x;
    public double y;
    public double z;
    public Vector3 pointingAt;
    public double fov = 90;
    public Vector3 up = new Vector3(0, 1, 0);
    //clipping planes
    double near = 0.1;
    double far = 10000;
    Camera(double x, double y, double z, Vector3 pointingAt) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pointingAt = pointingAt;
    }
    //this needs the screen width and height to calculate the aspect ratio :)
    public Matrix calculateProjectionMatrix(double sw, double sh) {
        double aspectRatio = sw / sh;
        double f = 1 / Math.tan(Math.toRadians(fov) / 2);
        double[][] projectionMatrix = {
            {f / aspectRatio, 0, 0, 0},
            {0, f, 0, 0},
            {0, 0, (far + near) / (near - far), (2 * far * near) / (near - far)},
            {0, 0, -1, 0}
        };
        return new Matrix(projectionMatrix);
    }
    //for this method, i have decided to use double[] instead of Vector3, for convience accessing values
    public Matrix calculateViewMatrix() {
        
        double[] eye = {x, y, z};

        // Target position 
        double[] target = {pointingAt.x, pointingAt.y, pointingAt.z};

        // Up vector 
        double[] up = {this.up.x, this.up.y, this.up.z};

        
        double[] forward = BigUtils.normalize(BigUtils.subtract(target, eye));
        double[] right = BigUtils.normalize(BigUtils.cross(up, forward));
        double[] trueUp = BigUtils.cross(forward, right);

        
        Matrix rotation = new Matrix(new double[][] {
            {right[0], right[1], right[2], 0},
            {trueUp[0], trueUp[1], trueUp[2], 0},
            {-forward[0], -forward[1], -forward[2], 0},
            {0, 0, 0, 1}
        });
        // System.out.println("rotation dimentions: "+"("+rotation.rows+", "+rotation.columns+")");
        // rotation.display();

        Matrix translation = new Matrix(new double[][] {
            {1, 0, 0, -eye[0]},
            {0, 1, 0, -eye[1]},
            {0, 0, 1, -eye[2]},
            {0, 0, 0, 1}
        });
        // System.out.println("translation dimentions: "+"("+translation.rows+", "+translation.columns+")");
        // translation.display();

        // erm... 🤓 here goes nothing ☝️
        return rotation.multiply(translation);
    }
    public void translate(double x, double y, double z) {
        this.x+=x;
        this.y+=y;
        this.z+=z;
    }
    
    public void translate(Vector3 v) {
        this.x+=v.x;
        this.y+=v.y;
        this.z+=v.z;
    }
}