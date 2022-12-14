package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.entities.Customer;
import com.infoworks.lab.rest.models.ItemCount;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/v1/profile")
public class CustomerController {

    private SimpleDataSource<String, Customer> dataSource;

    @Autowired
    public CustomerController(@Qualifier("customerService") SimpleDataSource<String, Customer> dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/rowCount")
    public ItemCount getRowCount(){
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(dataSource.size()).longValue());
        return count;
    }

    @GetMapping
    public List<Customer> query(@RequestParam("limit") Integer limit
            , @RequestParam("offset") Integer offset){
        //TODO: Test with RestExecutor
        List<Customer> customers = Arrays.asList(dataSource.readSync(offset, limit));
        return customers;
    }

    @PostMapping @SuppressWarnings("Duplicates")
    public ItemCount insert(@Valid @RequestBody Customer customer){
        //TODO: Test with RestExecutor
        dataSource.put(customer.getName(), customer);
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(dataSource.size()).longValue());
        return count;
    }

    @PutMapping @SuppressWarnings("Duplicates")
    public ItemCount update(@Valid @RequestBody Customer customer){
        //TODO: Test with RestExecutor
        Customer old = dataSource.replace(customer.getName(), customer);
        ItemCount count = new ItemCount();
        if (old != null)
            count.setCount(Integer.valueOf(dataSource.size()).longValue());
        return count;
    }

    @DeleteMapping
    public Boolean delete(@RequestParam("name") String name){
        //TODO: Test with RestExecutor
        Customer deleted = dataSource.remove(name);
        return deleted != null;
    }

}
