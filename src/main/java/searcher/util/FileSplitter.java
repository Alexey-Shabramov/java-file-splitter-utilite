package searcher.util;

import javafx.application.Platform;
import searcher.application.FileBytesSplitterApp;
import searcher.dict.Constants;
import searcher.validator.FileValidator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FileSplitter {
    public static void splitIntoMultiple(File file, File selectedDirectory, Map<Long, Long> resultsMap, byte[] regExValue) throws Exception {
        RandomAccessFile raf = new RandomAccessFile(file.getPath(), "r");
        int maxReadBufferSize = 8 * 1024;
        String extension = "." + getExtension(file);
        Platform.runLater(() -> {
            FileBytesSplitterApp.loggerTextArea.appendText(Constants.LOGGER_FILE_SPLIT_BEGIN);
        });
        List<Long> resultIndexes = new ArrayList<>(resultsMap.keySet());
        int fileCounter = 0;
        for (int index = 0; index < resultIndexes.size(); index++) {
            try {
                if (FileBytesSplitterApp.interrupted) {
                    break;
                }
            } finally {
                FileBytesSplitterApp.interrupted = false;
            }
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(selectedDirectory + "\\" + fileCounter + extension, true));
            Long currentValue;
            if (index == 0) {
                currentValue = 0L;
            } else {
                currentValue = resultIndexes.get(index);
            }
            raf.seek(currentValue);
            long bytesPerSplit;
            if (resultIndexes.indexOf(currentValue) < (resultIndexes.size() - 1)) {
                if (resultIndexes.size() > 1) {
                    if (currentValue == 0) {
                        bytesPerSplit = resultIndexes.get(index) - currentValue;
                    } else {
                        bytesPerSplit = resultIndexes.get(index + 1) - currentValue;
                    }
                } else {
                    bytesPerSplit = resultIndexes.get(index).intValue() - currentValue.intValue();
                }
            } else {
                bytesPerSplit = file.length() - currentValue.intValue();
            }
            if (bytesPerSplit > maxReadBufferSize) {
                long numReads = bytesPerSplit / maxReadBufferSize;
                long numRemainingRead = bytesPerSplit % maxReadBufferSize;
                for (int i = 0; i < numReads; i++) {
                    readWrite(raf, bw, maxReadBufferSize);
                }
                if (numRemainingRead > 0) {
                    readWrite(raf, bw, numRemainingRead);
                }
            } else {
                readWrite(raf, bw, bytesPerSplit);
            }
            if (resultIndexes.size() == 1
                    && file.length() >= regExValue.length
                    && !FileValidator.validateFileBeginSymbols(regExValue, readBeginOfFile(raf, regExValue.length))) {
                ++fileCounter;
                BufferedOutputStream bwForOne = new BufferedOutputStream(new FileOutputStream(selectedDirectory + "\\" + fileCounter + extension, true));
                bytesPerSplit = file.length() - resultIndexes.get(0);
                raf.seek(resultIndexes.get(0));
                if (bytesPerSplit > maxReadBufferSize) {
                    long numReads = bytesPerSplit / maxReadBufferSize;
                    long numRemainingRead = bytesPerSplit % maxReadBufferSize;
                    for (int i = 0; i < numReads; i++) {
                        readWrite(raf, bwForOne, maxReadBufferSize);
                    }
                    if (numRemainingRead > 0) {
                        readWrite(raf, bwForOne, numRemainingRead);
                    }
                } else {
                    readWrite(raf, bwForOne, bytesPerSplit);
                }
                bwForOne.close();
            }
            bw.close();
            ++fileCounter;
        }
        raf.close();
    }

    private static byte[] readBeginOfFile(RandomAccessFile raf, int length) throws IOException {
        raf.seek(0);
        byte[] buf = new byte[length];
        raf.read(buf);
        return buf;
    }

    private static void readWrite(RandomAccessFile raf, BufferedOutputStream bw, long length) throws IOException {
        byte[][] fileParts;
        if (length > Integer.MAX_VALUE) {
            int numParts = (int) (length / Integer.MAX_VALUE);
            int numRemainingPart = (int) (length % Integer.MAX_VALUE);
            fileParts = new byte[numParts + numRemainingPart][];
            for (int i = 0; i < numParts + numRemainingPart; i++) {
                fileParts[i] = new byte[(int) length];
            }
            int lastValueIndex = fileParts.length - 1;
            for (int r = 0; r < fileParts.length; r++) {
                if (lastValueIndex == r) {
                    fileParts[r] = new byte[numRemainingPart];
                } else {
                    fileParts[r] = new byte[Integer.MAX_VALUE];
                }
                fileParts[r] = new byte[(int) length];
            }
        } else {
            fileParts = new byte[][]{new byte[(int) length]};
        }
        Platform.runLater(() -> {
            FileBytesSplitterApp.loggerTextArea.appendText(Constants.LOGGER_FILE_SPLIT_PART);
        });
        for (byte[] array : fileParts) {
            int val = raf.read(array);
            if (val != -1) {
                bw.write(array);
            }
        }
        fileParts = null;
    }

    private static String getExtension(File file) {
        String ext = null;
        String s = file.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
