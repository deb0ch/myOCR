package processing.pre;

import com.sun.istack.internal.NotNull;
import javafx.util.Pair;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

/**
 * Created by sal on 15/11/15.
 */
public class MatManipulator
{
    public static int findUpBound(@NotNull Mat m)
    {
        int start_x = -1;

        for (int i = 0; i < m.width() && start_x == -1; i++)
        {
            for (int j = 0; j < m.height() && start_x == -1; j++)
            {
                if (m.get(j, i)[0] != 255)
                    start_x = i;
            }
        }
        return start_x;
    }

    public static int findDownBound(@NotNull Mat m)
    {
        int end_x = -1;
        for (int i = m.width() - 1; i >= 0 && end_x == -1; --i)
        {
            for (int j = 0; j < m.height() && end_x == -1; ++j)
            {
                if (m.get(j, i)[0] != 255)
                    end_x = i;
            }
        }
        return end_x;
    }

    public static Pair<Integer, Integer> findUpAndDownBounds(@NotNull Mat m)
    {
        return new Pair<>(findUpBound(m), findDownBound(m));
    }

    public static Pair<Integer, Integer> findLeftAndRightBounds(@NotNull Mat m)
    {
        return new Pair<>(findLeftBound(m), findRightBound(m));
    }

    private static int findRightBound(@NotNull Mat m)
    {
        int end = -1;
        for (int i = m.height() - 1; i >= 0 && end == -1; --i)
        {
            for (int j = 0; j < m.width() && end == -1; ++j)
            {
                if (m.get(i, j)[0] != 255)
                    end = i;
            }
        }
        return 0;
    }

    private static int findLeftBound(@NotNull Mat m)
    {
        int start = -1;
        for (int i = 0; i < m.height() && start == -1; i++)
        {
            for (int j = 0; j < m.width() && start == -1; j++)
            {
                if (m.get(i, j)[0] != 255)
                    start = i;
            }
        }
        return start;
    }

    public static Rect findBounds(@NotNull Mat m)
    {
        Pair<Integer, Integer> upAndDownBounds = findUpAndDownBounds(m);
        Pair<Integer, Integer> leftAndRightBounds = findLeftAndRightBounds(m);
        int start_x = upAndDownBounds.getKey();
        int end_x = upAndDownBounds.getValue();
        int start_y = leftAndRightBounds.getKey();
        int end_y = leftAndRightBounds.getValue();
        return new Rect(start_x, start_y, end_x - start_x, end_y - start_y);
    }
}
