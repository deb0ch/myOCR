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
        for (int row = 0; row < m.height() && result == -1; row++)
        {
            for (int col = 0; col < m.width() && result == -1; col++)
            {
                if (m.get(row, col)[0] != 255)
                    result = (row == 0) ? (row) : (row - 1);
            }
        }
        return result;
    }

    public static int findDownBound(@NotNull Mat m)
    {
        int result = -1;
        for (int row = m.height() -1; row >= 0 && result == -1; --row)
        {
            for (int col = 0; col < m.width() && result == -1; ++col)
            {
                if (m.get(row, col)[0] != 255)
                    result = (row == m.height() -1) ? (row) : (row + 1);
            }
        }
        return result;
    }

    public static int findRightBound(@NotNull Mat m)
    {
        int result = -1;
        for (int col = m.width() -1; col >= 0 && result == -1; --col)
        {
            for (int row = m.height() - 1; row >= 0 && result == -1; --row)
            {
                if (m.get(row, col)[0] != 255)
                    result = (col == m.width() - 1) ? (col) : (col + 1);
            }
        }
        return result;
    }

    public static int findLeftBound(@NotNull Mat m)
    {
        int result = -1;
        for (int col = 0; col < m.width() && result == -1; col++)
        {
            for (int row = 0; row < m.height() && result == -1; row++)
            {
                if (m.get(row, col)[0] != 255)
                    result = (col == 0) ? (col) : (col - 1);
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
