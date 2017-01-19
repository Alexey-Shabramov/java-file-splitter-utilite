package searcher.application;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import searcher.dict.Constants;
import searcher.util.*;
import searcher.validator.TextfieldByteValidator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBytesSplitterApp extends Application {
    public static TextArea loggerTextArea;
    private static File chosenFile;
    private static File selectedResultsDirectory;
    private static List<String> errorList = new ArrayList<>();
    private static Thread thread;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) {
        final Label labelSelectedFile = new Label();
        Button btnOpenFileChooser = new Button();
        btnOpenFileChooser.setText(Constants.CHOOSE_FILE);

        final Label emptyLabel = new Label();
        final Label emptyLabel1 = new Label();
        final Label emptyLabel2 = new Label();
        final Label emptyLabel3 = new Label();
        final Label emptyLabel4 = new Label();

        final Label resultDirectoryLabel = new Label();
        Button resultDirectoryChooser = new Button();
        resultDirectoryChooser.setText(Constants.CHOOSE_RESULT_FOLDER);

        final Label regExLabel = new Label();
        TextField regExTextField = new TextField();
        regExLabel.setText(Constants.REG_EX_VALUE);

        CheckBox strictInputCheckBox = new CheckBox(Constants.CHECK_BOX_STRICT_INPUT);

        Button btnBeginConvertation = new Button();
        btnBeginConvertation.setText(Constants.BEGIN_CONVERTATION);

        final Label loggerLabel = new Label();
        loggerLabel.setText(Constants.LOGGING_TITLE);
        loggerTextArea = new TextArea();
        loggerTextArea.setMinHeight(300);
        loggerTextArea.setMinWidth(300);

        Button cleanLoggerButton = new Button();
        cleanLoggerButton.setText(Constants.CLEAN_LOGGER);

        cleanLoggerButton.setOnAction(event -> Platform.runLater(() -> loggerTextArea.setText(null)));

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

        resultDirectoryChooser.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory == null) {
                resultDirectoryLabel.setText(Constants.ERROR_RESULT_FOLDER_NOT_SET);
                selectedResultsDirectory = null;
            } else {
                selectedResultsDirectory = selectedDirectory;
                resultDirectoryLabel.setText(selectedDirectory.getAbsolutePath());
            }
        });

        btnBeginConvertation.setOnAction(event -> {
            if (chosenFile == null) {
                errorList.add(Constants.ERROR_NO_FILE);
            }
            if (selectedResultsDirectory == null) {
                errorList.add(Constants.ERROR_RESULT_FOLDER_NOT_SET);
            }
            if (regExTextField.getText() == null || "".equals(regExTextField.getText())) {
                errorList.add(Constants.ERROR_REGEX_FIELD_EMPTY);
            } else if (regExTextField.getText().length() < 2) {
                errorList.add(Constants.ERROR_REGEX_FIELD_LENGTH_SMALL);
            } else try {
                if (!TextfieldByteValidator.validateStringToStrictInput(regExTextField.getText(), errorList).isEmpty()) {
                    errorList.add(Constants.ERROR_REGEX_FIELD_LENGTH_SMALL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!errorList.isEmpty()) {
                AlertGuiUtil.prepareAlertMessage(errorList);
            } else {
                try {
                    thread = new Thread(() -> {
                        try {
                            byte[] regExSelectedValue;
                            if (strictInputCheckBox.isSelected() && TextfieldByteValidator.validateStringToStrictInput(regExTextField.getText(), errorList).isEmpty()) {
                                regExSelectedValue = HexConverterUtil.toByteArray(regExTextField.getText());
                            } else {
                                regExSelectedValue = regExTextField.getText().getBytes();
                            }
                            btnBeginConvertation.setDisable(true);
                            resultDirectoryChooser.setDisable(true);
                            regExTextField.setDisable(true);
                            btnOpenFileChooser.setDisable(true);
                            Map<Long, Long> map = FileSplitReader.readByteParts(chosenFile, regExSelectedValue);
                            if (!map.isEmpty()) {
                                if (map.size() <= 200) {
                                    Platform.runLater(() -> loggerTextArea.appendText(Constants.LOGGER_FOUNDED_VALUES_COUNT + map.size()));
                                    for (Map.Entry entry : map.entrySet()) {
                                        Platform.runLater(() -> loggerTextArea.appendText(Constants.LOGGER_EQUALITY_FOUND_FIRST_INDEX + entry.getKey() + Constants.LOGGER_EQUALITY_FOUND_LAST_INDEX + entry.getValue()));
                                    }
                                } else {
                                    Platform.runLater(() -> loggerTextArea.appendText(Constants.LOGGER_OUTPUT_IS_TO_BIG + map.size()));
                                }
                                FileUtil.saveResultAsFile(map, selectedResultsDirectory, chosenFile);
                                Platform.runLater(() -> loggerTextArea.appendText(Constants.LOGGER_CHECK_IS_OVER));

                                FileSplitter.splitIntoMultiple(chosenFile, selectedResultsDirectory, map, regExSelectedValue);
                                Platform.runLater(() -> loggerTextArea.appendText(Constants.LOGGER_SPLITTING_IS_OVER));
                            } else {
                                Platform.runLater(() -> loggerTextArea.appendText(Constants.LOGGER_NO_EQUALITY_FOUND));
                            }
                        } catch (Exception e) {
                            Platform.runLater(() -> {
                                loggerTextArea.appendText(Constants.ERROR_HEADER + e);
                                AlertGuiUtil.createAlert(Constants.ERROR_HEADER + e);
                                e.printStackTrace();
                            });
                        } finally {
                            FileSplitReader.resultsValues.clear();
                            btnBeginConvertation.setDisable(false);
                            resultDirectoryChooser.setDisable(false);
                            btnOpenFileChooser.setDisable(false);
                            regExTextField.setDisable(false);
                        }
                        thread.interrupt();
                    });
                    thread.start();
                } catch (Exception e) {
                    AlertGuiUtil.createAlert(Constants.ERROR_HEADER + e);
                }
            }
        });

        VBox vBox = new VBox();
        vBox.getChildren().addAll(labelSelectedFile,
                btnOpenFileChooser,
                emptyLabel,
                resultDirectoryLabel,
                resultDirectoryChooser,
                emptyLabel1,
                regExLabel,
                regExTextField,
                strictInputCheckBox,
                emptyLabel2,
                btnBeginConvertation,
                emptyLabel3,
                loggerLabel,
                loggerTextArea,
                emptyLabel4,
                cleanLoggerButton
        );

        StackPane root = new StackPane();
        root.getChildren().add(vBox);

        Scene scene = new Scene(root, 600, 600);

        primaryStage.setTitle(Constants.APPLICATION_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setOnHidden(event -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }
}
