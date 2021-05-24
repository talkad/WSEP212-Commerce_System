package Server.Domain.CommonClasses;
import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

/***
 * Logger class: messages will be of types INFO/WARNING/SEVERE
 */

public class Log {
    public java.util.logging.Logger logger;
    FileHandler fh;

    public Log(String file_name) {
        try {
            File f = new File(file_name);
            if(!f.exists())
                f.createNewFile();

            fh = new FileHandler(file_name, true);
            logger = java.util.logging.Logger.getLogger("test");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        }
        catch (IOException e1){
            fh.close();
        }
        finally {
            fh.close();
        }

    }
}
