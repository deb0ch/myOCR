package processing.pre;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import utils.ErrorHandling;
import utils.Pair;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by sal on 12/11/15.
 */
public class LinesSplitter {

    private Mat img = null;
    private Pane root = null;
    private int[] columnsHistogram = null;
    private int[] rowsHistogram = null;

    public LinesSplitter(File img, Pane root)
    {
        setImg(img);
        setRoot(root);
        binarize();
        calculateHistograms();
        showDebug();
    }

    public List<Mat> split()
    {
//        if (img == null) return new LinkedList<>();
        assert columnsHistogram != null : "Cannot proceed with null histogram";
        assert rowsHistogram != null : "Cannot proceed with null histogram";

        // adapt it to fit portion of text
//        int colStart = -1;
//        int colEnd = -1;
//
//        int j = 0;
//        while (j < columnsHistogram.length && colStart == -1)
//        {
//            if (columnsHistogram[j] != 0) colStart = j;
//            j++;
//        }
//
//        j = columnsHistogram.length - 1;
//        while (j > -1 && colEnd == -1)
//        {
//            if (columnsHistogram[j] != 0) colEnd = j;
//            j--;
//        }

        List<Pair<Integer, Integer>> boundaries = new LinkedList<>();
        int start = -1, end = -1;
        for (int i = 0; i < rowsHistogram.length; i++)
        {
            if (rowsHistogram[i] != 0 && start == -1)
            {
                start = i;
            }
            else if (start != -1 && rowsHistogram[i] == 0)
            {
                end = i;
            }
            if (start != -1 && end != -1)
            {
                boundaries.add(new Pair<>(start, end));
                start = -1;
                end = -1;
            }
        }

//        // splitting letters
//        List<Mat> letters = new LinkedList<>();
        List<Mat> lines = new LinkedList<>();
        lines.addAll(boundaries
                .stream()
                .map(p -> img.submat(p.getL(), p.getR(), 0, img.cols() - 1))
                .collect(Collectors.toList()));
        return lines;
    }

    private void showDebug()
    {
        if ((img == null)
                || (img.empty())
                || (img.dataAddr() == 0x0)) return ;
        assert columnsHistogram != null : "Cannot proceed with null histogram";
        assert rowsHistogram != null : "Cannot proceed with null histogram";

        // first draw our image
        ImageManipulator.showMat(root, img);
        // then draw its histograms
        Mat colsMat = ImageManipulator.drawHistogram(columnsHistogram, ImageManipulator.HistType.Columns);
        Mat rowsMat = ImageManipulator.drawHistogram(rowsHistogram, ImageManipulator.HistType.Rows);
        ImageManipulator.showMat(root, colsMat);
        ImageManipulator.showMat(root, rowsMat);
        // show lines split
        VBox tmp = new VBox();
        for (Mat m: split())
        {
            ImageManipulator.showMat(tmp, m);
        }
        root.getChildren().add(tmp);
    }

    private void calculateHistograms()
    {
        if ((img == null)
                || (img.empty())
                || (img.dataAddr() == 0x0)) return ;
        calculateColumnsHistogram();
        calculateRowsHistogram();
    }

    private void calculateRowsHistogram()
    {
        if ((img == null)
                || (img.empty())
                || (img.dataAddr() == 0x0)) return ;
        rowsHistogram = ImageManipulator.manualCalculationHistogramRows(img);
    }

    private void calculateColumnsHistogram()
    {
        if ((img == null)
                || (img.empty())
                || (img.dataAddr() == 0x0)) return ;
        columnsHistogram = ImageManipulator.manualCalculationHistogramColumns(img);
    }

    private void binarize()
    {
        if ((img == null)
                || (img.empty())
                || (img.dataAddr() == 0x0)) return ;
        img = ImageManipulator.applyOtsuBinarysation(img);
    }

    private void setImg(File file)
    {
        assert file != null : "Invalid file: null";
        try
        {
            this.img = Imgcodecs.imread(file.getCanonicalPath(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
            if (this.img.dataAddr() == 0x0)
            {
                ErrorHandling.log(Level.WARNING, "img as 0x0 (null) as data address, probably bad image.");
                this.img = null;
            }
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
