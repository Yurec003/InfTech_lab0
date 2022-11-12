import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public class FractalExplorer {

    private int displaySize;
    private static JImageDisplay jImageDisplay;
    private FractalGenerator fractalGenerator;
    private Rectangle2D.Double rectangle2D;
    private JButton jButton;


    public FractalExplorer(int displaySize){
        this.displaySize = displaySize;
        fractalGenerator = new Mandelbrot();
        rectangle2D = new Rectangle2D.Double();
    }

    private void createAndShowGUI() {

        JFrame frame = new JFrame("Fractal Explorer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jImageDisplay = new JImageDisplay(displaySize, displaySize);
        frame.add(jImageDisplay, BorderLayout.CENTER);

        jButton = new JButton("Reset Display");
        frame.add(jButton,BorderLayout.SOUTH);

        initClickListeners();

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        drawFractal();
    }

    private void initClickListeners(){
        jButton.addActionListener(e -> {
            fractalGenerator.getInitialRange(rectangle2D);
            drawFractal();
        });

        jImageDisplay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                jImageDisplay.clearImage();
                int x = e.getX();
                double xCoord = FractalGenerator.getCoord(rectangle2D.getX(), rectangle2D.getX() +
                        rectangle2D.width, displaySize, x);
                int y = e.getY();
                double yCoord = FractalGenerator.getCoord(rectangle2D.getY(), rectangle2D.getY() +
                        rectangle2D.getHeight(), displaySize, y);
                fractalGenerator.recenterAndZoomRange(rectangle2D, xCoord, yCoord, 0.5);
                drawFractal();
            }
        });
    }

    private void drawFractal(){
        for (int x = 0; x < displaySize; x++){
            for (int y = 0; y < displaySize; y++){
                double xCoord = FractalGenerator.getCoord(rectangle2D.getX(), rectangle2D.getX() + rectangle2D.getWidth(),
                        displaySize, x);
                double yCoord = FractalGenerator.getCoord(rectangle2D.getY(), rectangle2D.getY() + rectangle2D.getHeight(),
                        displaySize, y);
                if (fractalGenerator.numIterations(xCoord, yCoord) == -1){
                    jImageDisplay.drawPixel(x, y, 0);
                } else {
                    float hue = 0.7f + (float) fractalGenerator.numIterations(xCoord, yCoord) / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    jImageDisplay.drawPixel(x, y, rgbColor);
                }
            }
            jImageDisplay.repaint();
        }
    }


    public static void main(String[] args) {
        FractalExplorer fractalExplorer = new FractalExplorer(700);
        fractalExplorer.createAndShowGUI();
    }
}

