package file.splitter.application;


import file.splitter.dict.Constants;
import file.splitter.util.*;
import file.splitter.validator.TextfieldByteValidator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileBytesSplitterApp extends Application {
    public static TextArea loggerTextArea;
    public static boolean interrupted = false;
    private static Button resultDirectoryChooser;
    private static TextField regExTextField;
    private static CheckBox strictInputCheckBox;
    private static CheckBox spliteFilesCheckBox;
    private static Button btnBeginConvertation;
    private static Button btnOpenFileChooser;
    private static File chosenFile;
    private static File selectedResultsDirectory;
    private static List<String> errorList = new ArrayList<>();
    private static Thread thread;

    public static void main(String[] args) {
        launch(args);
    }

    private static void blockUI() {
        Platform.runLater(() -> {
            resultDirectoryChooser.setDisable(true);
            spliteFilesCheckBox.setDisable(true);
            regExTextField.setDisable(true);
            btnOpenFileChooser.setDisable(true);
            strictInputCheckBox.setDisable(true);
        });
    }

    private static void unblockUI() {
        Platform.runLater(() -> {
            resultDirectoryChooser.setDisable(false);
            spliteFilesCheckBox.setDisable(false);
            btnOpenFileChooser.setDisable(false);
            regExTextField.setDisable(false);
            strictInputCheckBox.setDisable(false);
        });
    }

    @Override
    public void start(final Stage primaryStage) {
        final Label labelSelectedFile = new Label();
        btnOpenFileChooser = new Button();
        btnOpenFileChooser.setText(Constants.CHOOSE_FILE);

        final Label emptyLabel = new Label();
        final Label emptyLabel1 = new Label();
        final Label emptyLabel2 = new Label();
        final Label emptyLabel3 = new Label();
        final Label emptyLabel4 = new Label();
        final Label emptyLabel5 = new Label();
        final Label emptyLabel6 = new Label();

        final Label resultDirectoryLabel = new Label();
        resultDirectoryChooser = new Button();
        resultDirectoryChooser.setText(Constants.CHOOSE_RESULT_FOLDER);

        final Label regExLabel = new Label();
        regExTextField = new TextField();
        regExLabel.setText(Constants.REG_EX_VALUE);

        strictInputCheckBox = new CheckBox(Constants.CHECK_BOX_STRICT_INPUT);

        spliteFilesCheckBox = new CheckBox(Constants.CHECK_BOX_SPLIT);

        btnBeginConvertation = new Button();
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
            if (Constants.CANCEL_BUTTON.equals(btnBeginConvertation.getText())) {
                interrupted = true;
                thread.interrupt();
                btnBeginConvertation.setText(Constants.BEGIN_CONVERTATION);
                unblockUI();
            } else {
                if (chosenFile == null) {
                    errorList.add(Constants.ERROR_NO_FILE);
                }
                if (selectedResultsDirectory == null) {
                    errorList.add(Constants.ERROR_RESULT_FOLDER_NOT_SET);
                }
                if (strictInputCheckBox.isSelected()) {
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
                }
                if (!errorList.isEmpty()) {
                    AlertGuiUtil.prepareAlertMessage(errorList);
                } else {
                    btnBeginConvertation.setText(Constants.CANCEL_BUTTON);
                    try {
                        thread = new Thread(() -> {
                            try {
                                byte[] regExSelectedValue;
                                if (strictInputCheckBox.isSelected() && TextfieldByteValidator.validateStringToStrictInput(regExTextField.getText(), errorList).isEmpty()) {
                                    regExSelectedValue = HexConverterUtil.toByteArray(regExTextField.getText());
                                } else {
                                    regExSelectedValue = regExTextField.getText().getBytes();
                                }
                                blockUI();
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
                                    Platform.runLater(() -> loggerTextArea.appendText(Constants.LOGGER_SEARCH_IS_OVER));
                                    Platform.runLater(() -> loggerTextArea.appendText(Constants.LOGGER_SAVE_RESULT + map.size()));

                                    FileUtil.saveResultAsFile(map, selectedResultsDirectory, chosenFile);
                                    if (spliteFilesCheckBox.isSelected()) {
                                        FileSplitter.splitIntoMultiple(chosenFile, selectedResultsDirectory, map, regExSelectedValue);
                                        Platform.runLater(() -> loggerTextArea.appendText(Constants.LOGGER_SPLITTING_IS_OVER));
                                    }
                                } else {
                                    Platform.runLater(() -> loggerTextArea.appendText(Constants.LOGGER_NO_EQUALITY_FOUND));
                                }
                            } catch (Exception e) {
                                Platform.runLater(() -> {
                                    loggerTextArea.appendText(Constants.ERROR_HEADER + e);
                                    AlertGuiUtil.createAlert(Constants.ERROR_HEADER + e);
                                    e.printStackTrace();
                                });
                                Platform.runLater(() -> loggerTextArea.appendText(Constants.LOGGER_SEARCH_IS_OVER));

                            } finally {
                                FileSplitReader.resultsValues.clear();
                                unblockUI();
                                Platform.runLater(() -> {
                                    btnBeginConvertation.setText(Constants.BEGIN_CONVERTATION);
                                });
                            }
                            thread.interrupt();
                        });
                        thread.start();
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            AlertGuiUtil.createAlert(Constants.ERROR_HEADER + e);
                        });
                        unblockUI();
                    }
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
                emptyLabel6,
                strictInputCheckBox,
                emptyLabel5,
                spliteFilesCheckBox,
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

        Scene scene = new Scene(root, 600, 650);

        primaryStage.setTitle(Constants.APPLICATION_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setOnHidden(event -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }
    }
