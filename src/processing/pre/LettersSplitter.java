package processing.pre;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.opencv.core.Mat;
import utils.ErrorHandling;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by sal on 11/11/15.
 */
public class LettersSplitter extends Splitter
{
    public LettersSplitter(@NotNull Mat img)
    {
        this(img, null);
    }

    public LettersSplitter(@NotNull Mat img, @Nullable Pane root)
    {
        this(img, root, 0, 0);
    }

    public LettersSplitter(@NotNull Mat img, @Nullable Pane root, double colLimit, double rowLimit)
    {
        super(img, root, colLimit, rowLimit);
        setRootBackgroundColor(Color.GREEN);
    }

    @Override
    protected void showDebug()
    {
        super.showDebug();
        for (Mat letter: this.split())
        {
            ImageManipulator.showMat(root, letter);
        }
    }

    @Override
    public @NotNull List<Mat> split()
    {
        // get the row limits to the biggest letter in the word
        Pair<Integer, Integer> startEndRow = this.findStartAndEnd(getRowsHistogram());
        int startRow = startEndRow.getKey();
        int endRow = startEndRow.getValue();
        // verify if they are corrects
        if (startRow == -1 || endRow == -1)
        {
            ErrorHandling.log(Level.WARNING,
                    String.format("wrong values:(%s, %s): %s",
                            startRow,
                            endRow,
                            getClass().getName()));
            return new LinkedList<>();
        }
        // find boundaries of letters using the column histogram
        List<Pair<Integer, Integer>> boundaries = this.findBoundaries(getColumnsHistogram(), colLimit);
        // splitting letters and returning it as a new LinkedList
        return boundaries
                .stream()
                .map(p -> getImg().submat(startRow, endRow, p.getKey(), p.getValue()))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
