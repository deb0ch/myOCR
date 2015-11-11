package processing.pre;

import javafx.scene.layout.Pane;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

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
            Logger.getGlobal().log(Level.SEVERE, ioe.getMessage());
        }
        // threshold
        if (m != null && !m.empty())
        {
            m = ImageManipulator.applyOtsuBinarysation(m);

            ImageManipulator.showMat(root, m);
            ImageManipulator.showMat(root, ImageManipulator.drawHistogram(ImageManipulator.manualCalculationHistogramColumns(m), ImageManipulator.HistType.Columns));
            ImageManipulator.showMat(root, ImageManipulator.drawHistogram(ImageManipulator.manualCalculationHistogramRows(m), ImageManipulator.HistType.Rows));
        }
        else
            Logger.getGlobal().log(Level.SEVERE, "File is not an image");
        // histograms
    }
}
