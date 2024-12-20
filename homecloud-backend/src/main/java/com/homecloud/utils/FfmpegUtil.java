package com.homecloud.utils;

import com.homecloud.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FfmpegUtil {
    private static final Logger logger = LoggerFactory.getLogger(FfmpegUtil.class);

    public static String executeCmd(String cmd, Boolean outprintLog) throws BusinessException {
        if (StringUtil.isEmpty(cmd)) {
            logger.error("--- Command execution failed because the provided FFmpeg command is empty! ---");
            return null;
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);
            PrintStream errorStream = new PrintStream(process.getErrorStream());
            PrintStream inputStream = new PrintStream(process.getInputStream());
            errorStream.start();
            inputStream.start();
            // Wait for the ffmpeg command to complete
            process.waitFor();
            // Get the result string
            String result = errorStream.stringBuffer.append(inputStream.stringBuffer + "\n").toString();
            // Log the executed command information

            if (outprintLog) {
                logger.info("Command: {}, has been executed, Result: {}", cmd, result);
            } else {
                logger.info("Command: {}, has been executed", cmd);
            }
            return result;
        } catch (Exception e) {

            e.printStackTrace();
            throw new BusinessException("Video conversion failed");
        } finally {
            if (null != process) {
                ProcessKiller ffmpegKiller = new ProcessKiller(process);
                runtime.addShutdownHook(ffmpegKiller);
            }
        }
    }


    private static class ProcessKiller extends Thread {
        private Process process;

        public ProcessKiller(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            this.process.destroy();
        }
    }

    static class PrintStream extends Thread {
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        StringBuffer stringBuffer = new StringBuffer();

        public PrintStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            try {
                if (null == inputStream) {
                    return;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line);
                }
            } catch (Exception e) {
                logger.error("Error reading the input stream! Error message: " + e.getMessage());
            } finally {
                try {
                    if (null != bufferedReader) {
                        bufferedReader.close();
                    }
                    if (null != inputStream) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    logger.error("Error closing the stream after reading with PrintStream!");
                }
            }
        }
    }
}
