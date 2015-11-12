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

/**
 * Created by sal on 11/11/15.
 */
public class WordsSplitter
{

    private Mat img = null;
    private Pane root = null;
    private int[] columnsHistogram = null;
    private int[] rowsHistogram = null;

    public WordsSplitter(File img, Pane root)
    {
        setImg(img);
        setRoot(root);
        binarize();
        calculateHistograms();
        showDebug();
    }

    public List<List<Mat>> split()
    {
        if (img == null) return new LinkedList<>();
        assert columnsHistogram != null : "Cannot proceed with null histogram";
        assert rowsHistogram != null : "Cannot proceed with null histogram";

        int rowStart = -1;
        int rowEnd = -1;

        int j = 0;
        while (j < rowsHistogram.length && rowStart == -1)
        {
            if (rowsHistogram[j] != 0) rowStart = j;
            j++;
        }

        j = rowsHistogram.length - 1;
        while (j > -1 && rowEnd == -1)
        {
            if (rowsHistogram[j] != 0) rowEnd = j;
            j--;
        }

        List<Pair<Integer, Integer>> boundaries = new LinkedList<>();
        List<Integer> distances = new LinkedList<>();
        int start = -1, end = -1;
        for (int i = 0; i < columnsHistogram.length; i++)
        {
            if (columnsHistogram[i] != 0 && start == -1)
            {
                start = i;
            }
            else if (start != -1 && columnsHistogram[i] == 0)
            {
                end = i;
            }
            if (start != -1 && end != -1)
            {
                boundaries.add(new Pair<>(start, end));
                // calculate dist between letters
                if (boundaries.size() > 1)
                {
                    Pair<Integer, Integer> previous = boundaries.get(boundaries.size() - 2);
                    distances.add(start - previous.getR());
                }
                start = -1;
                end = -1;
            }
        }
        // calculate average dist between all letters found in a line
        double average = 0;
        // find dist greater than the average distance found
        for (Integer dist: distances)
        {
            average += dist;
        }
        assert !distances.isEmpty();
        average = average/distances.size();

        // splitting letters
        List<Mat> letters = new LinkedList<>();
        List<List<Mat>> words = new LinkedList<>();
        int i = 0;
        for (Pair<Integer, Integer> p: boundaries)
        {
            letters.add(img.submat(rowStart, rowEnd, p.getL(), p.getR()));
            if (distances.size() > i && distances.get(i++) > average)
            {
                words.add(letters);
                letters = new LinkedList<>();
            }
        }
        return words;
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
        // show letters split
        VBox tmp = new VBox();
        for (List<Mat> l: split())
        {
            HBox tmp2 = new HBox();
            for (Mat m: l)
            {
                ImageManipulator.showMat(tmp2, m);
            }
            tmp.getChildren().add(tmp2);
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
