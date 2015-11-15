package processing.pre;

import com.sun.istack.internal.NotNull;
import javafx.util.Pair;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;

/**
 * Created by sal on 15/11/15.
 */
public class MatManipulator
{
    public static int findUpBound(@NotNull Mat m)
    {
        int result = -1;

        for (int i = 0; i < m.width() && result == -1; i++)
        {
            for (int j = 0; j < m.height() && result == -1; j++)
            {
                if (m.get(j, i)[0] != 255)
                    result = i;
            }
        }
        return result;
    }

    public static int findDownBound(@NotNull Mat m)
    {
        int result = -1;
        for (int i = m.width() - 1; i >= 0 && result == -1; --i)
        {
            for (int j = 0; j < m.height() && result == -1; ++j)
            {
                if (m.get(j, i)[0] != 255)
                    result = i;
            }
        }
        return result;
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
        int result = -1;
        for (int i = m.height() - 1; i >= 0 && result == -1; --i)
        {
            for (int j = 0; j < m.width() && result == -1; ++j)
            {
                if (m.get(i, j)[0] != 255)
                    result = i;
            }
        }
        return result;
    }

    private static int findLeftBound(@NotNull Mat m)
    {
        int result = -1;
        for (int i = 0; i < m.height() && result == -1; i++)
        {
            for (int j = 0; j < m.width() && result == -1; j++)
            {
                if (m.get(i, j)[0] != 255)
                    result = i;
            }
        }
        return result;
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

    public static Mat squareMat(@NotNull Mat m)
    {
        int max = Math.max(m.width(), m.height());
        Mat nm = new Mat(
                max,
                max,
                CvType.CV_8U, new Scalar(255));

        if (m.width() == nm.width())
        {
            for (int i = 0; i < nm.width(); ++i)
            {
                for (int j = 0; j < m.height(); ++j)
                {
                    nm.put(j + (nm.height() - m.height()) / 2, i, m.get(j, i));
                }
            }
        }
        else if (m.height() == nm.height())
        {
            for (int i = 0; i < nm.height(); ++i)
            {
                for (int j = 0; j < m.width(); ++j)
                {
                    nm.put(i, j + (nm.width() - m.width()) / 2, m.get(i, j));
                }
            }
        }
        return nm;
    }
}
