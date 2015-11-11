package processing.pre;

import javafx.scene.layout.Pane;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import utils.ErrorHandling;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by sal on 11/11/15.
 */
public class LettersSplitter
{

    private Mat img = null;
    private Pane root = null;
    private int[] columnsHistogram = null;
    private int[] rowsHistogram = null;

    public LettersSplitter(File img, Pane root)
    {
        this.setImg(img);
        this.setRoot(root);
        this.binarize();
        this.calculateHistograms();
        this.showDebug();
    }

    private void showDebug() {
        assert img != null : "Cannot proceed with null matrices";
        assert !img.empty() : "Not an image";
        assert columnsHistogram != null : "Cannot proceed with null histogram";
        assert rowsHistogram != null : "Cannot proceed with null histogram";
        // first draw our image
        ImageManipulator.showMat(root, img);
        // then draw its histograms
        Mat colsMat = ImageManipulator.drawHistogram(columnsHistogram, ImageManipulator.HistType.Columns);
        Mat rowsMat = ImageManipulator.drawHistogram(rowsHistogram, ImageManipulator.HistType.Rows);
        ImageManipulator.showMat(root, colsMat);
        ImageManipulator.showMat(root, rowsMat);
    }

    private void calculateHistograms()
    {
        assert img != null : "Cannot proceed with null matrices";
        assert !img.empty() : "Not an image";
        calculateColumnsHistogram();
        calculateRowsHistogram();
    }

    private void calculateRowsHistogram()
    {
        assert img != null : "Cannot proceed with null matrices";
        assert !img.empty() : "Not an image";
        rowsHistogram = ImageManipulator.manualCalculationHistogramRows(img);
    }

    private void calculateColumnsHistogram()
    {
        assert img != null : "Cannot proceed with null matrices";
        assert !img.empty() : "Not an image";
        columnsHistogram = ImageManipulator.manualCalculationHistogramColumns(img);
    }

    private void binarize()
    {
        assert img != null : "Cannot proceed with null matrices";
        assert !img.empty() : "Not an image";
        img = ImageManipulator.applyOtsuBinarysation(img);
    }

    private void setImg(File file)
    {
        assert file != null : "Invalid file: null";
        try
        {
            this.img = Imgcodecs.imread(file.getCanonicalPath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        }
        catch (IOException ioe)
        {
            ErrorHandling.logAndExit(Level.SEVERE, ioe.getMessage());
        }
    }

    private void setRoot(Pane root)
    {
        assert root != null : "Cannot use a null Parent to draw";
        this.root = root;
    }
}
