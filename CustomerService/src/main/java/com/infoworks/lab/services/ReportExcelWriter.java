package com.infoworks.lab.services;

import com.infoworks.lab.definition.ContentWriter;
import com.infoworks.lab.services.excel.ExcelWritingService;
import com.it.soul.lab.sql.entity.Entity;
import com.it.soul.lab.sql.query.models.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ReportExcelWriter {

    private static Logger LOG = LoggerFactory.getLogger(ReportExcelWriter.class.getSimpleName());
    private NotifyService notifyService;
    private String uploadDir;
    private boolean delayInWriteAndEmail;

    public ReportExcelWriter(NotifyService notifyService
            , @Value("${app.upload.dir}") String uploadDir
            , @Value("${test.async.add.delay.writeAndEmail}") boolean delayInWriteAndEmail) {
        this.notifyService = notifyService;
        this.uploadDir = uploadDir;
        this.delayInWriteAndEmail = delayInWriteAndEmail;
    }

    /**
     * Return the file saved path to caller
     * @param rows
     * @param outputFileName
     * @return file saved path
     * @throws Exception
     */
    protected String write(Map<Integer, List<String>> rows, String outputFileName) throws Exception {
        if (!outputFileName.endsWith(".xlsx")) outputFileName += ".xlsx";
        //Excel Writer:
        ExcelWritingService writingService = new ExcelWritingService();
        String saveFileAt = String.format("%s/%s", uploadDir, outputFileName);
        ContentWriter writer = writingService.createAsyncWriter(100, saveFileAt, true);
        writer.write("output", rows, true);
        writer.close();
        return saveFileAt;
    }

    /**
     * @Async has three limitations:
     * * It must be applied to public methods only.
     * * It must return either void or Future<T> as methods returnType.
     * * Self-invocation — calling the async method from within the same class — won't work.
     */

    @Async
    public void writeAsync(Map<Integer, List<String>> rows, String outputFileName) {
        try {
            write(rows, outputFileName);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Async("SequentialExecutor")
    public void writeAsyncAndEmail(List<? extends Entity> entities
            , String outputFileName
            , String from, String to
            , String subject
            , String template, Property...properties) {
        Map<Integer, List<String>> rows = new HashMap<>();
        AtomicInteger counter = new AtomicInteger(0);
        entities.forEach(entity -> {
            List<String> values = entity.getRow().getCloneProperties()
                    .stream()
                    //.filter(property -> property.getValue() != null)
                    .map(property -> Objects.isNull(property.getValue()) ? "" : property.getValue().toString())
                    .collect(Collectors.toList());
            rows.put(counter.getAndIncrement(), values);
        });
        if (delayInWriteAndEmail){
            makeDelayInThread(500);
        }
        if (rows.size() > 0){
            try {
                outputFileName = System.currentTimeMillis() + "_" + outputFileName;
                String savedAt = write(rows, outputFileName);
                LOG.info("{} saved location {}", outputFileName, savedAt);
                //Send Email with Download Link:
                Map<String, String> attachments = new HashMap<>();
                attachments.put(outputFileName, savedAt);
                notifyService.sendEmail(from, to
                        , subject, template
                        , attachments, properties);
                LOG.info("Email Dispatched to:{} using template:{}, attachment:{}, properties:{}"
                        , to, template
                        , attachments.size(), properties.length);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private void makeDelayInThread(int wait) {
        //Testing...make a long pause:
        try {
            final int randVal = new Random().nextInt(9) + 1;
            long emitInterval = wait * randVal;
            Thread.sleep(Duration.ofMillis(emitInterval).toMillis());
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
    }

}
