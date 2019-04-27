package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.logic.ArchiveTask;
import sample.logic.DearchiveTask;
import sample.logic.LZW;

import java.awt.Desktop;
import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private Desktop desktop = Desktop.getDesktop();

    private LZW lzw = new LZW();

    final static String fileFormat = ".lama";

    @Override
    public void start(final Stage stage) {
        stage.setTitle("File Chooser Sample");

        final FileChooser fileChooser = new FileChooser();

        final Button openButton = new Button("Выбрать файл для сжатия");
        final Label archiveLabel = new Label("");
        final Button openMultipleButton = new Button("Выбрать файл для разжатия");
        final Label dearchiveLabel = new Label("");
        final Button archiveButton = new Button("Сжать");
        final Button dearchiveButton = new Button("Разжать");
        final ProgressBar archiveProgressBar = new ProgressBar(0);
        final ProgressBar dearchiveProgressBar = new ProgressBar(0);
        final Label archiveReadyLabel = new Label("");
        final Label dearchivedReadyLabel = new Label("");

        openButton.setOnAction(
                e -> {
                    configureFileChooser(fileChooser);
                    File file = fileChooser.showOpenDialog(stage);
                    if (file != null) {
                        archiveLabel.setText(file.getAbsolutePath());
                    }
                });

        openMultipleButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        configureFileChooser(fileChooser);
                        File file = fileChooser.showOpenDialog(stage);
                        if (file != null) {
                            dearchiveLabel.setText(file.getAbsolutePath());
                        }
                    }
                });

        archiveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String absoluteArchivePath = archiveLabel.getText();

                if (absoluteArchivePath != null) {
                    if (!absoluteArchivePath.isEmpty()) {
                        ArchiveTask archiveTask = new ArchiveTask(
                                absoluteArchivePath, fileFormat, lzw, archiveProgressBar, archiveReadyLabel);
                        archiveProgressBar.progressProperty().unbind();
                        archiveProgressBar.progressProperty().bind(archiveTask.progressProperty());

                        new Thread(archiveTask).start();
                    }
                }
            }
        });

        dearchiveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String absoluteArchivePath = dearchiveLabel.getText();

                if (absoluteArchivePath != null) {
                    if (!absoluteArchivePath.isEmpty()) {
                        DearchiveTask dearchiveTask = new DearchiveTask(
                                absoluteArchivePath, fileFormat, lzw, dearchiveProgressBar, dearchivedReadyLabel);
                        dearchiveProgressBar.progressProperty().unbind();
                        dearchiveProgressBar.progressProperty().bind(dearchiveTask.progressProperty());

                        new Thread(dearchiveTask).start();
                    }
                }
            }
        });

        final GridPane inputGridPane = new GridPane();

        GridPane.setConstraints(openButton, 0, 0);
        GridPane.setConstraints(archiveLabel, 1, 0);
        GridPane.setConstraints(openMultipleButton, 0, 1);
        GridPane.setConstraints(dearchiveLabel, 1, 1);
        GridPane.setConstraints(archiveButton, 0, 2);
        GridPane.setConstraints(dearchiveButton, 1, 2);
        GridPane.setConstraints(archiveProgressBar, 0, 3);
        GridPane.setConstraints(dearchiveProgressBar, 1, 3);
        GridPane.setConstraints(archiveReadyLabel, 0, 4);
        GridPane.setConstraints(dearchivedReadyLabel, 1, 4);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(openButton, archiveLabel,
                openMultipleButton, dearchiveLabel, archiveButton,
                dearchiveButton, archiveProgressBar, dearchiveProgressBar,
                archiveReadyLabel, dearchivedReadyLabel);


        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(inputGridPane);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));

        stage.setScene(new Scene(rootGroup));
        stage.show();
    }

    public void archive(String absolutepath) {

    }

    public void dearchive(String absolutePath) {

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }


    private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            Logger.getLogger(
                    Main.class.getName()).log(
                    Level.SEVERE, null, ex
            );
        }
    }
}
