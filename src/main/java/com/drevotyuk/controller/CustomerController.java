package com.drevotyuk.controller;

import com.drevotyuk.model.Customer;
import com.drevotyuk.repository.CustomerRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerRepository repository;

    @GetMapping
    public Iterable<Customer> getAllCustomers() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable int id) {
        Optional<Customer> optCustomer = repository.findById(id);
        if (!optCustomer.isPresent())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(optCustomer.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
        // we think of indentical customer's accounts as one person who registered
        // twice, for example using different e-mails
        return new ResponseEntity<>(repository.save(customer), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable int id, @RequestBody Customer customer) {
        Optional<Customer> optInitialCustomer = repository.findById(id);
        if (!optInitialCustomer.isPresent())
            return new ResponseEntity<>(repository.save(customer), HttpStatus.CREATED);

        Customer initialCustomer = optInitialCustomer.get();
        initialCustomer.setFirstname(customer.getFirstname());
        initialCustomer.setSurname(customer.getSurname());
        initialCustomer.setLastname(customer.getLastname());
        initialCustomer.setCreationDate(customer.getCreationDate());
        initialCustomer.setAddress(customer.getAddress());

        return new ResponseEntity<>(repository.save(initialCustomer), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable int id) {
        if (!repository.existsById(id))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        repository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
