package asd.logic;

import com.github.plushaze.traynotification.notification.Notification;
import com.github.plushaze.traynotification.notification.Notifications;
import com.github.plushaze.traynotification.notification.TrayNotification;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.*;

public class DearchiveTask extends Task {

    private String absolutePath;
    private String fileFormat;
    private LZW lzw;
    private ProgressBar progressBar;
    private Label label;

    public DearchiveTask(String absolutePath, String fileFormat, LZW lzw, ProgressBar progressBar, Label label) {
        this.absolutePath = absolutePath;
        this.fileFormat = fileFormat;
        this.lzw = lzw;
        this.progressBar = progressBar;
        this.label = label;
    }

    public void updateProperty(long workdone, long totalwork) {
        this.updateProgress(workdone, totalwork);
    }

    public void setLabelReady() {
        Platform.runLater(() -> {
            label.setText("Готово");
            String title = "Процесс выполнен";
            String message = "Разжатие произведено успешно";
            Notification notification = Notifications.SUCCESS;

            TrayNotification tray = new TrayNotification();
            tray.setTitle(title);
            tray.setMessage(message);
            tray.setNotification(notification);
            tray.showAndWait();
        });
    }

    public void nullifyReady() {
        Platform.runLater(() -> label.setText("В процессе"));
    }

    @Override
    protected Object call() throws Exception {
        nullifyReady();
        System.out.println(absolutePath);

        RandomAccessFile f = null;
        byte[] b = new byte[0];

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
                lzw.decompress(f.length() / 1000, inputStream, outputStream, this);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (FileOutputStream fos = new FileOutputStream(absolutePath.replace(fileFormat, ""))) {
                fos.write(outputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        setLabelReady();
        return null;
    }
}
