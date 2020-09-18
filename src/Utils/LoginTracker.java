package Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Timestamp;

public class LoginTracker {
    static Path path = FileSystems.getDefault().getPath("");

    public static void log(String user, String status) throws IOException {
        String toWrite = "User \"" + user + "\" login attempt" + status + " @ " + new Timestamp(System.currentTimeMillis());
        FileWriter fileWriter = new FileWriter(path+"logTracker.txt",true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        PrintWriter printWriter = new PrintWriter(bufferedWriter);
        printWriter.println(toWrite);
        printWriter.close();
        bufferedWriter.close();
        fileWriter.close();
    }
}
