package file.splitter.application;


import file.splitter.util.FileRemultiplier;
import file.splitter.util.FileSplitter;
import file.splitter.util.AlertGuiUtil;
import file.splitter.util.Constants;
import file.splitter.view.NumericTextfield;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSplitterApp extends Application {
    public static String chosenDirectory;
    public static String tempDirectory;
    public static File chosenFile;
    public static javafx.scene.control.TextArea loggerTextArea;
    public static List<String> errorList = new ArrayList<>();

    @Override
    public void start(final Stage primaryStage) {
        final Label labelSelectedFile = new Label();
        Button btnOpenFileChooser = new Button();
        btnOpenFileChooser.setText(Constants.CHOOSE_FILE);

        final Label emptyLable = new Label();
        final Label emptyLable1 = new Label();
        final Label emptyLable2 = new Label();
        final Label emptyLable3 = new Label();
        final Label emptyLable4 = new Label();
        final Label emptyLable5 = new Label();
        final Label emptyLable6 = new Label();

        final Label labelSelectedDirectory = new Label();
        Button btnOpenDirectoryChooser = new Button();
        btnOpenDirectoryChooser.setText(Constants.CHOOSE_FOLDER);

        final Label regExLabel = new Label();
        TextField regExTextField = new TextField();
        regExLabel.setText(Constants.REG_EX_VALUE);

        final Label fileTypeLabel = new Label();
        TextField fileTypeTextField = new TextField();
        fileTypeLabel.setText(Constants.OUTPUT_FILE_LABEL);

        final Label splitCountLabel = new Label();
        NumericTextfield splitCountTextField = new NumericTextfield();
        splitCountLabel.setText(Constants.SPLIT_COUNT_VALUE_TITLE);

        final Label labelSelectedTempDirectory = new Label();
        Button tempDirectoryChooser = new Button();
        tempDirectoryChooser.setText(Constants.CHOOSE_TEMP_FOLDER);

        Button btnBeginConvertation = new Button();
        btnBeginConvertation.setText(Constants.BEGIN_CONVERTATION);

        final Label loggerLabel = new Label();
        loggerLabel.setText(Constants.LOGGING_TITLE);
        loggerTextArea = new TextArea();
        loggerTextArea.setMinHeight(200);
        loggerTextArea.setMinWidth(200);

        tempDirectoryChooser.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory == null) {
                labelSelectedTempDirectory.setText(Constants.ERROR_NO_DIRECTORY);
                tempDirectory = null;
            } else {
                tempDirectory = selectedDirectory.getPath();
                labelSelectedTempDirectory.setText(selectedDirectory.getAbsolutePath());
            }
        });

        btnOpenDirectoryChooser.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory == null) {
                labelSelectedDirectory.setText(Constants.ERROR_NO_DIRECTORY);
                chosenDirectory = null;
            } else {
                chosenDirectory = selectedDirectory.getPath();
                labelSelectedDirectory.setText(selectedDirectory.getAbsolutePath());
            }
        });

        btnOpenFileChooser.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile == null) {
                labelSelectedFile.setText(Constants.ERROR_NO_FILE);
                chosenFile = null;
            } else {
                chosenFile = selectedFile;
                labelSelectedFile.setText(selectedFile.getAbsolutePath());
            }
        });

        btnBeginConvertation.setOnAction(event -> {
            if (chosenFile == null) {
                errorList.add(Constants.ERROR_NO_FILE);
            }
            if (chosenDirectory == null || chosenDirectory.equals("")) {
                errorList.add(Constants.ERROR_NO_DIRECTORY);
            }
            if(regExTextField.getText() == null || regExTextField.getText().equals("")){
                errorList.add(Constants.ERROR_REGEX_FIELD_EMPTY);
            }
            if(fileTypeTextField.getText() == null
                    || fileTypeTextField.getText().equals("")
                    || !fileTypeTextField.getText().startsWith(".")){
                errorList.add(Constants.ERROR_TYPE_TEXTFIELD);
            }
            if(splitCountTextField.getText() == null || splitCountTextField.getText().equals("")){
                errorList.add(Constants.ERROR_SPLIT_COUNT_EMPTY);
            }
            if(tempDirectory == null || tempDirectory.equals("")){
                errorList.add(Constants.ERROR_NO_TEMP_FOLDER);
            }
            if (!errorList.isEmpty()) {
                AlertGuiUtil.prepareAlertMessage(errorList);
            } else {
                try {
                    new Thread(() -> {
                        loggerTextArea.appendText(Constants.LOGGER_BEGIN);
                        btnBeginConvertation.setDisable(true);
                        try {
                            FileSplitter.splitAndConvertToMultiple(chosenFile, Long.parseLong(splitCountTextField.getText()), fileTypeTextField.getText());
                            FileRemultiplier.readAllFilesFromFolder(tempDirectory, regExTextField.getText(), fileTypeTextField.getText());
                        } catch (Exception e) {
                            e.printStackTrace();
                            AlertGuiUtil.createAlert(Constants.ERROR_HEADER + e);
                        }
                        btnBeginConvertation.setDisable(false);
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                    AlertGuiUtil.createAlert(Constants.ERROR_HEADER + e);
                }
            }
        });

        VBox vBox = new VBox();
        vBox.getChildren().addAll(labelSelectedFile,
                btnOpenFileChooser,
                emptyLable,
                labelSelectedDirectory,
                btnOpenDirectoryChooser,
                emptyLable1,
                regExLabel,
                regExTextField,
                emptyLable2,
                fileTypeLabel,
                fileTypeTextField,
                emptyLable3,
                splitCountLabel,
                splitCountTextField,
                emptyLable4,
                labelSelectedTempDirectory,
                tempDirectoryChooser,
                emptyLable5,
                btnBeginConvertation,
                emptyLable6,
                loggerLabel,
                loggerTextArea
        );


        StackPane root = new StackPane();
        root.getChildren().add(vBox);

        Scene scene = new Scene(root, 400, 600);

        primaryStage.setTitle(Constants.APPLICATION_TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
