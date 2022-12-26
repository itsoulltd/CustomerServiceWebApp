package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.entities.Customer;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.services.NotifyService;
import com.infoworks.lab.services.ReportExcelWriter;
import com.it.soul.lab.data.base.DataSource;
import com.it.soul.lab.sql.query.models.Property;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/reporting")
public class ReportingController {

    private DataSource<Integer, Customer> dataSource;
    private NotifyService notifyService;
    private ReportExcelWriter excelWriter;

    public ReportingController(@Qualifier("customerService") DataSource<Integer, Customer> dataSource
            , NotifyService notifyService
            , ReportExcelWriter excelWriter) {
        this.dataSource = dataSource;
        this.notifyService = notifyService;
        this.excelWriter = excelWriter;
    }

    @GetMapping("/all/customer/sequential")
    public ResponseEntity<Response> generateCustomersReport(@RequestParam String to) {
        //
        Customer[] customers = dataSource.readSync(0, dataSource.size());
        //Write to Excel and then email:
        excelWriter.writeAsyncAndEmail(Arrays.asList(customers)
                , "seq_customer_list.xlsx"
                , "noreply@customer.com", to
                , "Seq CustomerList Report!"
                , "welcome-email-sample.html"
                , new Property("name", "Mr/Mrs. Mohamed Lee"));
        return ResponseEntity.ok(new Response().setStatus(200)
                .setMessage("Please Check Your Email For Download Link."));
    }

    @GetMapping("/all/customer/async")
    public ResponseEntity<Response> generateAsyncCustomersReport(@RequestParam String to) throws Exception {
        //
        Customer[] customers = dataSource.readSync(0, dataSource.size());
        //Write to Excel:
        String savedAt = excelWriter.write(Arrays.asList(customers), "async_customer_list.xlsx");
        Map<String, String> attachments = new HashMap<>();
        attachments.put("async_customer_list.xlsx", savedAt);
        //And then email:
        excelWriter.sendEmail("noreply@customer.com", to
                , "Async CustomerList Report!"
                , "welcome-email-sample.html"
                , attachments
                , new Property("name", "Mr/Mrs. Mohamed Lee"));
        //
        return ResponseEntity.ok(new Response().setStatus(200)
                .setMessage("Please Check Your Email For Download Link."));
    }

}
