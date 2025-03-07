public class BigUtils {
    public static double[] to3D(double[] vector) {
        //this method turns a 4d vector into a 3d vector in normalised device coordinats
        if (vector.length != 4) {
            throw new IllegalArgumentException("Input vector must have 4 axies");
        }

        double w = vector[3];
        if (w == 0) {
            throw new ArithmeticException("Cannot divide by zero; w-axis is 0. FIX THGAT NOWWW!!!!  ");
        }

        return new double[] {
            vector[0] / w,  // x
            vector[1] / w,  // y
            vector[2] / w   // z
        };
    }
    // Normalize a "vector"
    public static double[] normalize(double[] vector) {
        double length = Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]);
        if (length != 0) {
            return new double[] { vector[0] / length, vector[1] / length, vector[2] / length };
        }
        return vector;  // Return original vector if length is zero
    }

    //subtract two "vectors"
    public static double[] subtract(double[] v1, double[] v2) {
        return new double[] { v1[0] - v2[0], v1[1] - v2[1], v1[2] - v2[2] };
    }


    //multiplying two vectors
    public static double[] cross(double[] v1, double[] v2) {
        return new double[] {
            v1[1] * v2[2] - v1[2] * v2[1],
            v1[2] * v2[0] - v1[0] * v2[2],
            v1[0] * v2[1] - v1[1] * v2[0]
        };
    }

    public static Quaternion createRotationQuaternion(double pitch, double yaw)
    {
        //x axis rotation first
        pitch = Math.sin(pitch/2);
        double w1 = Math.sqrt(1-pitch*pitch);

        //y axis rotation
        yaw = Math.sin(yaw/2);
        double w2 = Math.sqrt(1-yaw*yaw);

        return new Quaternion(w1*w2, w2*pitch, w1*yaw, pitch*yaw);  
    }
    
}
