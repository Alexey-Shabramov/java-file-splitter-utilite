package file.splitter.util;

import javafx.scene.control.Alert;

import java.util.List;


public class AlertGuiUtil {
    private static StringBuilder stringBuilder = new StringBuilder();

    public static void prepareAlertMessage(List<String> errorList) {
        for (String error : errorList) {
            stringBuilder.append("- ")
                    .append(error)
                    .append(" ")
                    .append("\n");
        }
        String preparedString = stringBuilder.toString();
        stringBuilder.setLength(0);
        errorList.clear();
        createAlert(preparedString);
    }

    public static void createAlert(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Constants.ERROR_TITLE);
        alert.setHeaderText(Constants.ERROR_HEADER);
        alert.setContentText(error);
        alert.showAndWait();
    }

    public static void createSuccessAlert(String mainText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(Constants.OPERATION_SUCCESSFULL_TITLE);
        alert.setHeaderText(Constants.OPERATION_SUCCESSFULL_HEADER);
        alert.setContentText(mainText);
        alert.showAndWait();
    }
}
