package me.armar.plugins.autorank.logger;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LogFile {

    DateTimeFormatter timeFormat = DateTimeFormatter.ISO_LOCAL_TIME;
    private String fileName = "log-file";
    private FileWriter fw;
    private BufferedWriter bw;
    private PrintWriter pw;
    private boolean fileReady = false;

    public LogFile(String fileName) {
        this.fileName = fileName;
    }

    public boolean isFileReady() {
        return this.fileReady;
    }

    public void loadFile() {
        try {
            File file = new File(this.fileName);

            // If the file exists, we use it. If not, we create it.
            if (file.exists()) {
                fw = new FileWriter(file, true);
            } else {

                // Generate the file and parent folders if the file does not exist.
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                // Then read the file.
                fw = new FileWriter(file);
            }

            bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);

            this.fileReady = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void writeToFile(String message) {
        if (!isFileReady()) return;

        if (message == null) return;

        pw.println("[" + LocalTime.now().format(timeFormat) + "]: " + message);
        pw.flush();
    }


}
