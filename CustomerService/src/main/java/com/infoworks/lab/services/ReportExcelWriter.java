package com.infoworks.lab.services;

import com.infoworks.lab.definition.ContentWriter;
import com.infoworks.lab.domain.entities.Customer;
import com.infoworks.lab.services.excel.ExcelWritingService;
import com.it.soul.lab.sql.query.models.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportExcelWriter {

    private static Logger LOG = LoggerFactory.getLogger(ReportExcelWriter.class.getSimpleName());
    private NotifyService notifyService;
    private String downloadDir;

    public ReportExcelWriter(NotifyService notifyService
            , @Value("${app.upload.dir}") String downloadDir) {
        this.notifyService = notifyService;
        this.downloadDir = downloadDir;
    }

    protected void write(Map<Integer, List<String>> rows, String outputFileName) throws Exception {
        if (!outputFileName.endsWith(".xlsx")) outputFileName += ".xlsx";
        //Excel Writer:
        ExcelWritingService writingService = new ExcelWritingService();
        String fileName = String.format("%s/%s", downloadDir, outputFileName);
        ContentWriter writer = writingService.createAsyncWriter(100, fileName, true);
        writer.write("output", rows, true);
        writer.close();
    }

    @Async
    public void writeAsync(Map<Integer, List<String>> rows, String outputFileName) {
        try {
            write(rows, outputFileName);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Async("SequentialExecutor")
    public void writeAndEmail(Customer[] customers, String outputFileName, String email) {
        Map<Integer, List<String>> rows = new HashMap<>();
        AtomicInteger counter = new AtomicInteger(0);
        Stream.of(customers).forEach(customer -> {
            List<String> values = customer.getRow().getCloneProperties()
                    .stream()
                    .filter(property -> property.getValue() != null)
                    .map(property -> property.getValue().toString())
                    .collect(Collectors.toList());
            rows.put(counter.getAndIncrement(), values);
        });
        //Testing...make a long pause:
        try {
            final int randVal = new Random().nextInt(9) + 1;
            long emitInterval = 300 * randVal;
            Thread.sleep(Duration.ofMillis(emitInterval).toMillis());
        } catch (InterruptedException e) {
            LOG.error(e.getMessage(), e);
        }
        //
        if (rows.size() > 0){
            try {
                outputFileName = System.currentTimeMillis() + "_" + outputFileName;
                write(rows, outputFileName);
                LOG.info(outputFileName + " generated.");
                //Send Email with Download Link:
                notifyService.sendEmail("noreply@customer.com"
                        , email
                        , "Customer List Report!"
                        , "welcome-email-sample.html"
                        , new Property("name", outputFileName));
                LOG.info("Email Dispatched.");
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

}
