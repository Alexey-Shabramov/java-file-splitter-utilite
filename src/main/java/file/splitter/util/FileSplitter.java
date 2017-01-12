package file.splitter.util;

import file.splitter.application.FileSplitterApp;

import java.io.*;


public class FileSplitter {
    public static void splitAndConvertToMultiple(File file, Long splitsCounter, String outputFileType) throws Exception {
        RandomAccessFile raf = new RandomAccessFile(file.getPath(), "r");
        long numSplits = splitsCounter;
        long sourceSize = raf.length();
        long bytesPerSplit = sourceSize / numSplits;
        long remainingBytes = sourceSize % numSplits;

        FileSplitterApp.loggerTextArea.appendText(Constants.LOGGER_FILE_CHECK_BEGIN);

        int maxReadBufferSize = 8 * 1024;
        for (int destIx = 1; destIx <= numSplits; destIx++) {
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(FileSplitterApp.tempDirectory + "\\" + (destIx - 1) + outputFileType));
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
            bw.close();
            FileSplitterApp.loggerTextArea.appendText(Constants.LOGGER_FILE_SPLIT_DONE + destIx);
        }
        if (remainingBytes > 0) {
            BufferedOutputStream bw = new BufferedOutputStream(new FileOutputStream(FileSplitterApp.tempDirectory + "\\" + numSplits + outputFileType));
            readWrite(raf, bw, remainingBytes);
            bw.close();
        }
        raf.close();
    }

    private static void readWrite(RandomAccessFile raf, BufferedOutputStream bw, long numBytes) throws IOException {
        byte[] buf = new byte[(int) numBytes];
        int val = raf.read(buf);
        if (val != -1) {
            bw.write(buf);
        }
    }
}
