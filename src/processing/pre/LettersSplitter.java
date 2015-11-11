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
public class LettersSplitter {
    public LettersSplitter(File img, Pane root) {
        // load img
        Mat m = null;
        try
        {
            m = Imgcodecs.imread(img.getCanonicalPath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        }
        catch (IOException ioe)
        {
            ErrorHandling.logAndExit(Level.SEVERE, ioe.getMessage());
        }
        assert m != null;
        if (!m.empty())
        {
            // threshold
            m = ImageManipulator.applyOtsuBinarysation(m);

            int[] colunmsHistogram = ImageManipulator.manualCalculationHistogramColumns(m);
            int[] rowsHistogram = ImageManipulator.manualCalculationHistogramRows(m);
            ;
                    ImageManipulator.showMat(root, m);
            ImageManipulator.showMat(root, ImageManipulator.drawHistogram(colunmsHistogram, ImageManipulator.HistType.Columns));
            ImageManipulator.showMat(root, ImageManipulator.drawHistogram(rowsHistogram, ImageManipulator.HistType.Rows));
        }
        else
            ErrorHandling.log(Level.WARNING, "Not an image");
        // histograms
    }
}
