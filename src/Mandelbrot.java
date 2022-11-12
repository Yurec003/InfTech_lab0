import java.awt.geom.Rectangle2D;

public class Mandelbrot extends FractalGenerator{
    public static final int MAX_ITERATIONS = 2000;

    public void getInitialRange(Rectangle2D.Double range){
        range.x = -2;
        range.y = -1.5;
        range.width = 3;
        range.height = 3;
    }

    public int numIterations(double x, double y){
        double xx = 0.0;
        double yy = 0.0;
        int iter = 0;
        while (xx * xx + yy * yy <= 4.0 && iter < MAX_ITERATIONS) {
            double temp = xx*xx - yy*yy + x;
            yy = 2.0 * xx * yy + y;
            xx = temp;
            iter ++;
        }
        if (iter == MAX_ITERATIONS){
            return -1;
        }
        return iter;
    }

}