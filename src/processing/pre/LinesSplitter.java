package processing.pre;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javafx.scene.control.ScrollPane;
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
 * Created by sal on 12/11/15.
 * Used to Split Lines from a paragraph
 */
public class LinesSplitter extends Splitter {

    public LinesSplitter(@NotNull Mat img)
    {
        this(img, null);
    }

    public LinesSplitter(@NotNull Mat img, @Nullable Pane root)
    {
        this(img, root, 0, 0);
    }

    public LinesSplitter(@NotNull Mat img, @Nullable Pane root, double colLimit, double rowLimit)
    {
        super(img, root, colLimit, rowLimit);
        setRootBackgroundColor(Color.RED);
    }

    @Override
    protected void showDebug()
    {
        super.showDebug();
    }

    @Override
    public List<Mat> split()
    {
        // get the col limits to the biggest line in the paragraph
        Pair<Integer, Integer> startEndCol = MatManipulator.findLeftAndRightBounds(getImg());
//        Pair<Integer, Integer> startEndCol = this.findStartAndEnd(getColumnsHistogram());
        int startCol = startEndCol.getKey();
        int endCol = startEndCol.getValue();

        // verify if they are corrects
        if (startCol == -1 || endCol == -1)
        {
            ErrorHandling.log(Level.WARNING,
                    String.format("wrong values:(%s, %s): %s",
                            startCol,
                            endCol,
                            getClass().getName()));
            return new LinkedList<>();
        }

        int margin = 5;
        int i = 0;
        while (i < margin && startCol - i > 0)
        {
            i++;
        }
        final int startColFinal = startCol - i;
        i = endCol;
        while (i - endCol < margin && i < getImg().cols())
        {
            i++;
        }
        final int endColFinal = i;
        List<Pair<Integer, Integer>> boundaries = this.findBoundaries(getRowsHistogram(), rowLimit);
        return boundaries
                .stream()
                .map(p -> getImg().submat(p.getKey(), p.getValue(), startColFinal, endColFinal))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
