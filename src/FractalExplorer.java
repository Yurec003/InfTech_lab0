import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;

public class FractalExplorer {

    private int displaySize;
    private int rowsRemaining;
    private JImageDisplay jImageDisplay;
    private FractalGenerator fractalGenerator;

    private Rectangle2D.Double rectangle2D;
    private JButton jButton, saveImage;
    private JComboBox jComboBox;


    public FractalExplorer(int displaySize){
        this.displaySize = displaySize;
        fractalGenerator = new Mandelbrot();
        rectangle2D = new Rectangle2D.Double();
    }

    private void createAndShowGUI() {

        JFrame frame = new JFrame("Fractal Explorer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        JLabel jLabel = new JLabel("Fractal:");
        jComboBox = new JComboBox<>();
        jComboBox.addItem(new Mandelbrot());
        jComboBox.addItem(new Tricor());
        jComboBox.addItem(new BurningShip());
        jPanel.add(jLabel);
        jPanel.add(jComboBox);
        frame.add(jPanel, BorderLayout.NORTH);

        jImageDisplay = new JImageDisplay(displaySize, displaySize);
        frame.add(jImageDisplay, BorderLayout.CENTER);


        JPanel jPanel1 = new JPanel();
        saveImage = new JButton("Save Image");
        jButton = new JButton("Reset Display");
        jPanel1.add(saveImage);
        jPanel1.add(jButton);


        frame.add(jPanel1,BorderLayout.SOUTH);

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
        saveImage.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            FileFilter filter = new FileNameExtensionFilter("PNG Images", "png");
            jFileChooser.setFileFilter(filter);
            jFileChooser.setAcceptAllFileFilterUsed(false);
            if (jFileChooser.showSaveDialog(jImageDisplay) == JFileChooser.APPROVE_OPTION){
                File file = jFileChooser.getSelectedFile();
                String path = file.toString();
                if (!path.contains(".png")){
                    file = new File(path + ".png");
                }
                try {
                    javax.imageio.ImageIO.write(jImageDisplay.getImage(), "png", file);
                } catch (Exception exception) {
                    JOptionPane.showMessageDialog(jImageDisplay, exception.getMessage(), "«Cannot Save Image", JOptionPane.ERROR_MESSAGE);
                }

            }

        });

        jComboBox.addActionListener(e -> {
            JComboBox source = (JComboBox) e.getSource();
            fractalGenerator = (FractalGenerator) source.getSelectedItem();
            fractalGenerator.getInitialRange(rectangle2D);
            jImageDisplay.clearImage();
            drawFractal();
        });

        jImageDisplay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (rowsRemaining == 0) {
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
            }
        });
    }

    private void drawFractal(){
        enableUI(false);
        rowsRemaining = displaySize;
        for (int y = 0; y < displaySize; y++){
            FractalWorker fractalWorker = new FractalWorker(y);
            fractalWorker.execute();
        }
    }


    public static void main(String[] args) {
        FractalExplorer fractalExplorer = new FractalExplorer(700);
        fractalExplorer.createAndShowGUI();
    }

    private void enableUI(boolean val){
        jComboBox.setEnabled(val);
        jButton.setEnabled(val);
        saveImage.setEnabled(val);
    }

    private class FractalWorker extends SwingWorker<Object, Object>{
        private int y;
        int [] colors;

        public FractalWorker(int y){
            this.y = y;
        }

        @Override
        protected Object doInBackground() throws Exception {
            colors = new int[displaySize];
            for (int x = 0; x < colors.length; x++) {
                double xCoord = FractalGenerator.getCoord(rectangle2D.getX(), rectangle2D.getX() + rectangle2D.getWidth(),
                        displaySize, x);
                double yCoord = FractalGenerator.getCoord(rectangle2D.getY(), rectangle2D.getY() + rectangle2D.getHeight(),
                        displaySize, y);
                if (fractalGenerator.numIterations(xCoord, yCoord) == -1) {
                    colors[x] = 0;
                } else {
                    float hue = 0.7f + (float) fractalGenerator.numIterations(xCoord, yCoord) / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    colors[x] = rgbColor;
                }
            }
            return null;
        }

        @Override
        protected void done() {
            super.done();
            for (int x = 0; x < colors.length; x++){
                jImageDisplay.drawPixel(x, y, colors[x]);
            }
            jImageDisplay.repaint(0, 0, y, displaySize, 1);
            rowsRemaining--;
            if (rowsRemaining == 0)
                enableUI(true);
        }
    }
}

