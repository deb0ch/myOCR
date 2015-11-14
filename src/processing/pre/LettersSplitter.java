package processing.pre;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javafx.scene.layout.Pane;
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
    public LettersSplitter(@NotNull Mat img, @Nullable Pane root)
    {
        super(img, root);
    }

    @Override
    public @NotNull List<Mat> split()
    {
        // get the row limits to the biggest letter in the word
        Pair<Integer, Integer> startEndRow = this.findStartAndEnd(getRowsHistogram());
        int startRow = startEndRow.getKey();
        int endRow = startEndRow.getKey();
        // verify if they are corrects
        if (startRow == -1 || endRow == -1)
        {
            ErrorHandling.log(Level.WARNING,
                    String.format("wrong values:(%s, %s)",
                            startRow,
                            endRow));
            return new LinkedList<>();
        }
        // find boundaries of letters using the column histogram
        List<Pair<Integer, Integer>> boundaries = this.findBoundaries(getColumnsHistogram());
        // splitting letters and returning it as a new LinkedList
        return boundaries
                .stream()
                .map(p -> getImg().submat(startRow, endRow, p.getKey(), p.getValue()))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
