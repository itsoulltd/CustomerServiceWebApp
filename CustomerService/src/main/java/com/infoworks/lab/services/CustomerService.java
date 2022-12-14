package com.infoworks.lab.services;

import com.infoworks.lab.domain.entities.Customer;
import com.infoworks.lab.domain.repositories.CustomerRepository;
import com.it.soul.lab.data.simple.SimpleDataSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("customerService")
public class CustomerService extends SimpleDataSource<Integer, Customer> {

    private CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Customer read(Integer key) {
        Optional<Customer> res = repository.findById(key);
        return res.isPresent() ? res.get() : null;
    }

    @Override
    public Customer[] readSync(int offset, int pageSize) {
        Page<Customer> finds = repository.findAll(PageRequest.of(offset, pageSize));
        return finds.getContent().toArray(new Customer[0]);
    }

    @Override
    public int size() {
        return Long.valueOf(repository.count()).intValue();
    }

    @Override
    public void put(Integer key, Customer customer) {
        repository.save(customer);
    }

    @Override
    public Customer replace(Integer key, Customer customer) {
        Customer existing = read(key);
        if (existing != null && customer != null) {
            customer.setId(existing.getId());
            existing.unmarshallingFromMap(customer.marshallingToMap(true), true);
            repository.save(existing);
        }
        return existing;
    }

    @Override
    public Customer remove(Integer key) {
        Customer existing = read(key);
        if (existing != null) {
            repository.deleteById(existing.getId());
        }
        return existing;
    }
}
