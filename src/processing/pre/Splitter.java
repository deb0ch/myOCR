package processing.pre;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javafx.application.Platform;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.opencv.core.Mat;
import utils.ErrorHandling;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by sal on 14/11/15.
 */
public abstract class Splitter
{
    @NotNull
    private Mat img; // the matrice image to process
    @NotNull
    private int[] columnsHistogram = new int[0]; // the histograms
    @NotNull
    private int[] rowsHistogram = new int[0];

    @Nullable
    protected Pane root = null; // used to prompt debug info in a javafx application

    public double colLimit = 0;
    public double rowLimit = 0;

    /**
     * A Splitter split an image pixel, in a given way.
     * @param img img the image to process
     */
    public Splitter(@NotNull Mat img)
    {
        this(img, null);
    }

    /**
     * A splitter split an image pixel, in a given way.
     * If you want to debug, you can pass a pane
     * in which all Images generated in debugs will be add.
     * Initialize function will be call only if the matrice is
     * a valid image.
     * Debug function is called only if the Pane is not null.
     * @param img the image to process
     * @param root expected not null for debug process.
     */
    public Splitter(@NotNull Mat img, @Nullable Pane root)
    {
        this(img, root, 0, 0);
    }

    /**
     * A splitter split an image pixel, in a given way.
     * If you want to debug, you can pass a pane
     * in which all Images generated in debugs will be add.
     * Initialize function will be call only if the matrice is
     * a valid image.
     * Debug function is called only if the Pane is not null.
     * @param img the image to process
     * @param root expected not null for debug process.
     */
    public Splitter(@NotNull Mat img, @Nullable Pane root, double colLimit, double rowLimit)
    {
        this.setImg(img);
        this.setRoot(root);
        this.colLimit = colLimit;
        this.rowLimit = rowLimit;
        if (this.isValidImage(img))
        {
            this.initialize();
        }
    }

    /**
     * Default routine of a Splitter
     */
    private void initialize()
    {
        this.binarise();
        this.calculateHistograms();
        if (this.root != null)
        {
            showDebug();
        }
    }

    /**
     * This function aims to binarise the matrice image,
     * if the image is not empty and not null.
     */
    private void binarise()
    {
        this.setImg(ImageManipulator.applyOtsuBinarysation(this.img));
    }

    /**
     * Calculate the histograms of the matrice image.
     * Both rows and columns
     */
    private void calculateHistograms()
    {
        calculateColumnsHistogram();
        calculateRowsHistogram();
    }

    /**
     * Calculate the row histogram of the matrice image.
     */
    private void calculateRowsHistogram()
    {
        this.rowsHistogram = ImageManipulator.manualCalculationHistogramRows(this.img);
    }

    /**
     * Calculate the columns histogram of the matrice image.
     */
    private void calculateColumnsHistogram()
    {
        this.columnsHistogram = ImageManipulator.manualCalculationHistogramColumns(this.img);
    }

    /**
     * This function aims to return a List of
     * Matrices depending of the nature of the Splitter.
     * LettersSplitter will return a list of split characters.
     * WordsSplitter will return a list of split words.
     * LinesSplitter will return a list of split lines.
     * ...
     * @return a List of Matrices
     */
    abstract public @NotNull List<Mat> split();

    /**
     * This function aims to let the Splitter
     * to show generated histograms of the image.
     * It add to the root Pane the binarised matrice
     * then the histograms
     * then the result of the split function in a
     * separate VBox.
     */
    protected void showDebug()
    {
        // first draw our image
        ImageManipulator.showMat(root, img);
        // then draw its histograms
        Mat colsMat = ImageManipulator.drawHistogram(columnsHistogram, ImageManipulator.HistType.Columns);
        Mat rowsMat = ImageManipulator.drawHistogram(rowsHistogram, ImageManipulator.HistType.Rows);
        ImageManipulator.showMat(root, colsMat);
        ImageManipulator.showMat(root, rowsMat);
    }

    /**
     * This function aims to proceed the image to set it.
     * @param img the Matrices image to process
     */
    private void setImg(@NotNull Mat img)
    {
        this.img = img;
    }

    /**
     * This function aims to check if the matrice image
     * is correct to be proceed after.
     * If the image is invalid, histograms will be set
     * to int[0] arrays and error will be logged.
     * @param img the image to verify
     * @return false if empty or data address is null
     */
    private boolean isValidImage(@NotNull Mat img)
    {
        if (img.empty() || (img.dataAddr() == 0x0))
        {
            ErrorHandling.log
                    (
                            Level.WARNING,
                            String.format("%s: img is empty: %s\ndata address is null: %d",
                                    getClass().getName(), this.img.empty(), this.img.dataAddr())
                    );
            return false;
        }
        return true;
    }

    /**
     * This function aims to set a root
     * if the root is not null, the debug function will be call.
     * The root will be used to prompt all the generated Matrices
     * from the split function.
     * @param root the root
     */
    private void setRoot(@Nullable Pane root)
    {
        this.root = root;
    }

    /**
     * Works with binarised images'hitograms.
     * This function aims to calculate the boundaries of an histogram.
     * It look for a non black pixel (non 0 value) as start index
     * and to a black pixel (0 value) as stop index.
     * @param anHistogram an int array
     * @param value limit bound
     * @return a List containing the Pair of start and end index found,
     *          or an empty list if none was found.
     */
    protected @NotNull List<Pair<Integer, Integer>> findBoundaries(@NotNull int[] anHistogram, double value)
    {
//        int max = Integer.MIN_VALUE;
//        int min = Integer.MAX_VALUE;
//        int average = 0;
//        for (int anAnHistogram : anHistogram)
//        {
//            max = Math.max(max, anAnHistogram);
//            min = Math.min(min, anAnHistogram);
//            average += anAnHistogram;
//        }
//        average = average/anHistogram.length;
//        System.out.println("min: "+ min);
//        System.out.println("max: "+ max);
//        System.out.println("ave: "+ average);

        List<Pair<Integer, Integer>> boundaries = new LinkedList<>();
        int start = -1, end = -1;
        for (int i = 0; i < anHistogram.length; i++)
        {
            if (anHistogram[i] > value && start == -1)
            {
                start = i;
            }
            else if (start != -1 && anHistogram[i] <= value)
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
        if (start != -1) // reach the end but no blank after
        {
            boundaries.add(new Pair<>(start, anHistogram.length));
        }
        return boundaries;
    }

    /**
     * Works with binarised images'hitograms.
     * It look for a non black pixel (non 0 value) as start index
     * and to a black pixel (0 value) as stop index.
     * start is found searching forward, end is found searching backwards.
     * If one value is equal to -1, then a value was not found.
     * @param anHistogram the histogram to search from start to end
     * @return a new Pair of found start and end.
     */
    protected @NotNull Pair<Integer, Integer> findStartAndEnd(@NotNull int[] anHistogram)
    {
        int rowStart = -1;
        int rowEnd = -1;

        int j = 0;
        while (j < anHistogram.length && rowStart == -1)
        {
            if (anHistogram[j] != 0) rowStart = j - 1;
            j++;
        }

        j = anHistogram.length - 1;
        while (j > -1 && rowEnd == -1)
        {
            if (anHistogram[j] != 0) rowEnd = j + 1;
            j--;
        }

        return new Pair<>(rowStart, rowEnd);
    }

    /**
     * The calculated columns histogram.
     * @return an empty array if the matrice image was invalid, non empty array otherwise.
     */
    public @NotNull int[] getColumnsHistogram()
    {
        return columnsHistogram;
    }

    /**
     * The calculated rows histogram.
     * @return an empty array if the matrice image was invalid, non empty array otherwise.
     */
    public @NotNull int[] getRowsHistogram()
    {
        return rowsHistogram;
    }

    /**
     * The matrice image to process
     * Empty matrice if invalid image
     * @return a Matrice
     */
    public @NotNull Mat getImg()
    {
        return img;
    }

    protected void setRootBackgroundColor(Color color)
    {
        if (this.root != null)
            this.root.setBackground(new Background(new BackgroundFill(color, null, null)));
    }
}