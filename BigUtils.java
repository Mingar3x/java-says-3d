public class BigUtils {
    public static double[] to3D(double[] vector) {
        //this method turns a 4d vector into a 3d vector in normalised device coordinats
        if (vector.length != 4) {
            throw new IllegalArgumentException("Input vector must have 4 axies");
        }

        double w = vector[3];
        if (w == 0) {
            throw new ArithmeticException("Cannot divide by zero; w-axis is 0.");
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
    public static Vector3 cross(Vector3 v1, Vector3 v2) {
        return new Vector3 { //do later, who ccares
            v1[1] * v2[2] - v1[2] * v2[1],
            v1[2] * v2[0] - v1[0] * v2[2],
            v1[0] * v2[1] - v1[1] * v2[0]
        };
    }

    //yes this method is basically the same as the one in the Matrix class, but this one is static
    //also this one doesn't really work so dont use it. no i don't remeber why just dont use this
    public static Matrix multiplyMatrices(Matrix A, Matrix B) {
        int aRows = A.getRows();
        int aCols = A.getColumns();
        int bRows = B.getRows();
        int bCols = B.getColumns();

        if (aCols != bRows) {
            throw new IllegalArgumentException("BEEP BEEP BEEP THESE MATRICES CANT BE MULTLIPLIETed");
        }

        double[][] result = new double[aRows][bCols];

        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bCols; j++) {
                for (int k = 0; k < aCols; k++) {
                    result[i][j] += A.get(i, k) * B.get(k, j);
                }
            }
        }
        return new Matrix(result);
    }
}