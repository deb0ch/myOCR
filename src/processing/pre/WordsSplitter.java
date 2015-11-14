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

/**
 * Created by sal on 11/11/15.
 */
public class WordsSplitter extends Splitter
{
    public WordsSplitter(@NotNull Mat img)
    {
        this(img, null);
    }

    public WordsSplitter(@NotNull Mat img, @Nullable Pane root)
    {
        super(img, root);
        setRootBackgroundColor(Color.ORANGE);
    }

    @Override
    public List<Mat> split()
    {
        int[] columnsHistogram = getColumnsHistogram();
        Pair<Integer, Integer> startAndEndRow = findStartAndEnd(getRowsHistogram());
        int rowStart = startAndEndRow.getKey();
        int rowEnd = startAndEndRow.getValue();
        if (rowEnd == -1 || rowStart == -1)
        {
            ErrorHandling.log(Level.WARNING,
                    String.format("wrong values:(%s, %s): %s",
                            rowStart,
                            rowEnd,
                            getClass().getName(),
                            getClass()));
            return new LinkedList<>();
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
                    distances.add(start - previous.getValue());
                }
                start = -1;
                end = -1;
            }
        }

        if (distances.isEmpty())
        {
            ErrorHandling.log(Level.WARNING,
                    String.format("No distances were found: %s", getClass().getName()));
            return new LinkedList<>();
        }

        // calculate average dist between all letters found in a line
        double average = 0;
        // find dist greater than the average distance found
        for (Integer dist: distances)
        {
            average += dist;
        }
        average = average/distances.size();

        // splitting words
        List<Mat> words = new LinkedList<>();
        int startIndex = -1;
        for (int i = 0; i < boundaries.size(); i++)
        {
            Pair<Integer, Integer> p = boundaries.get(i);
            if (startIndex == -1)
            {
                startIndex = p.getKey();
            }
            else if (distances.size() <= i || distances.get(i) > average + 2)
            {
                words.add(getImg().submat(rowStart, rowEnd, startIndex, p.getValue()));
                startIndex = -1;
            }
        }
        return words;
    }
}
