package sample.logic;

import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.*;

public class ArchiveTask extends Task {

    private String absolutePath;
    private String fileFormat;
    private LZW lzw;
    private ProgressBar progressBar;
    private Label label;

    public ArchiveTask(String absolutePath, String fileFormat, LZW lzw, ProgressBar progressBar, Label label) {
        this.absolutePath = absolutePath;
        this.fileFormat = fileFormat;
        this.lzw = lzw;
        this.progressBar = progressBar;
        this.label = label;
    }

    public void setLabelReady(){
        label.setText("Готово");
    }

    public void nullifyReady(){
        label.setText("В процессе");
    }

    @Override
    protected Object call() throws Exception {

        nullifyReady();
        System.out.println(absolutePath);

        RandomAccessFile f = null;
        byte[] b = new byte[0];

        byte[] encodedFile = new byte[0];
        byte[] decodedFile = new byte[0];

        try {
            f = new RandomAccessFile(absolutePath, "r");
            b = new byte[(int) f.length()];
            f.readFully(b);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (f != null) {
            InputStream inputStream = new ByteArrayInputStream(b);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            try {
                lzw.compress(f.length()/1000, inputStream, outputStream, this);

                try (FileOutputStream fos = new FileOutputStream(absolutePath + fileFormat)) {
                    fos.write(outputStream.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        setLabelReady();
        return null;
    }

    public void updateProperty(long workdone, long totalwork){
        this.updateProgress(workdone, totalwork);
    }
}
