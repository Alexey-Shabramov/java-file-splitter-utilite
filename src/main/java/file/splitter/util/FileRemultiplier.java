package file.splitter.util;


import file.splitter.application.FileSplitterApp;


import java.io.File;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

public class FileRemultiplier {
    private static StringBuilder stringBuilder = new StringBuilder();
    private static int fileNameCounter = 0;

    public static void readAllFilesFromFolder(String folderPath, String regEXValue, String outputFileType) throws IOException {
        try(Stream<Path> paths = Files.walk(Paths.get(folderPath))) {
            FileSplitterApp.loggerTextArea.appendText(Constants.LOGGER_FILE_SPLITED_FILES_ANALIZING);
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        fileRemultiplier(new String(Files.readAllBytes(filePath)), regEXValue, outputFileType);
                    } catch (IOException e) {
                        e.printStackTrace();
                        AlertGuiUtil.createAlert(Constants.ERROR_HEADER + e);
                    }finally {
                        stringBuilder.setLength(0);
                    }
                }
            });
        }finally{
            fileNameCounter = 0;
        }
    }

    public static void fileRemultiplier(String parsedValue, String regEx, String outputFileType) throws IOException {
        String[] strings = parsedValue.split(regEx);
        if (strings.length == 0) {
            writeToFile(parsedValue, outputFileType);
        } else if (strings.length > 1) {
            appendToPrevious(strings[0], outputFileType);
            for (int i = 1; i < strings.length; i++) {
                writeToFile(strings[i], outputFileType);
            }
        }else if(strings.length == 1){
            appendToPrevious(parsedValue, outputFileType);
        }
    }

    public static void writeToFile(String value, String outputFileType) throws IOException {
        File file = new File(FileSplitterApp.chosenDirectory +"\\" +fileNameCounter + outputFileType);
        file.createNewFile();
        Files.write(Paths.get(file.getPath()), value.getBytes(), StandardOpenOption.APPEND);
        FileSplitterApp.loggerTextArea.appendText(Constants.LOGGER_OUT_FILE_DONE + fileNameCounter);
        ++fileNameCounter;
    }

    public static void appendToPrevious(String value, String outputFileType) throws IOException{
        File file = new File(FileSplitterApp.chosenDirectory +"\\"+ fileNameCounter + outputFileType);
        if(file.createNewFile()){
            Files.write(Paths.get(file.getPath()), value.getBytes(), StandardOpenOption.APPEND);
        }
    }
}
