package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.entities.Customer;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.services.NotifyService;
import com.it.soul.lab.data.base.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/v1/profile")
public class CustomerController {

    private DataSource<Integer, Customer> dataSource;
    private NotifyService notifyService;

    @Autowired
    public CustomerController(@Qualifier("customerService") DataSource<Integer, Customer> dataSource
            , NotifyService notifyService) {
        this.dataSource = dataSource;
        this.notifyService = notifyService;
    }

    @GetMapping("/rowCount")
    public ItemCount rowCount(){
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(dataSource.size()).longValue());
        return count;
    }

    @GetMapping
    public List<Customer> fetch(
            @RequestParam(value = "limit", defaultValue = "10", required = false) Integer limit
            , @RequestParam(value = "page", defaultValue = "0", required = false) Integer page){
        //
        if (limit < 0) limit = 10;
        if (page < 0) page = 0;
        int offset = page * limit;
        List<Customer> customers = Arrays.asList(dataSource.readSync(offset, limit));
        return customers;
    }

    @PostMapping
    public Customer insert(@Valid @RequestBody Customer customer){
        //
        dataSource.put(customer.getId(), customer);
        return customer;
    }

    @PutMapping
    public Customer update(@Valid @RequestBody Customer customer
            , @ApiIgnore @RequestParam(value = "userid", required = false) Integer userid){
        //
        dataSource.replace(customer.getId(), customer);
        return customer;
    }

    @DeleteMapping
    public Boolean delete(@RequestParam("userid") Integer userid){
        //TODO: Test with RestExecutor
        Customer deleted = dataSource.remove(userid);
        return deleted != null;
    }

}
