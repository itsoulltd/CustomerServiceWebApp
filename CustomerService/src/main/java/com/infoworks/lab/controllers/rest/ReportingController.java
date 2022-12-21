package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.entities.Customer;
import com.infoworks.lab.rest.models.Response;
import com.infoworks.lab.services.NotifyService;
import com.infoworks.lab.services.ReportExcelWriter;
import com.it.soul.lab.data.base.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

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

    @GetMapping("/all/customer")
    public ResponseEntity<Response> generateCustomersReport(@RequestParam String email) {
        //
        Customer[] customers = dataSource.readSync(0, dataSource.size());
        //Write to Excel and then email:
        excelWriter.writeAndEmail(Arrays.asList(customers), "customer_list.xlsx", email);
        return ResponseEntity.ok(new Response().setStatus(200)
                .setMessage("Please Check Your Email For Download Link."));
    }

}
