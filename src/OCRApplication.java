/**
 * Created by sal on 02/11/15.
 */

import org.opencv.core.Core;

public class OCRApplication
{
    private static final String USAGE = "java OCRApplication pathToFile";
    /**
     * The function that is call to start the java program
     * @param args the arguments given to the program
     */
    public static void main(String... args)
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        if (args.length > 1)
        {

        }
        else
        {
            System.out.println(USAGE);
        }
    }
}
