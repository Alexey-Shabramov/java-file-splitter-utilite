package searcher.util;


import searcher.dict.Constants;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class FileUtil {
    private static final String RESULT_FILE_TYPE = ".txt";
    private static final String RESULT_FILE_NAME = "/Результаты_проверки_";
    private static StringBuilder stringBuilder = new StringBuilder();

    public static void saveResultAsFile(Map map, File selectedDirectory, File checkedFile) throws IOException {
        if (selectedDirectory.isDirectory()) {
            String currentLocalTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            File resultFile = new File(selectedDirectory.getPath() + RESULT_FILE_NAME + currentLocalTime + RESULT_FILE_TYPE);
            if (resultFile.createNewFile()) {
                stringBuilder.append(Constants.CHECK_DATE)
                        .append(currentLocalTime)
                        .append(Constants.CHECKED_FILE)
                        .append(checkedFile.getName());
                try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(resultFile.toPath()), true)) {
                    writer.println(stringBuilder.toString());
                    stringBuilder.setLength(0);
                    map.forEach((key, value) -> {
                        try {
                            stringBuilder.append(Constants.FIRST_INDEX)
                                    .append(key.toString())
                                    .append(Constants.INDEX_OF_THE_END)
                                    .append(value.toString());
                            writer.println(stringBuilder.toString());
                        } finally {
                            stringBuilder.setLength(0);
                        }
                    });
                }
            }
        }
    }
}
