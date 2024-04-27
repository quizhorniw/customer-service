package com.drevotyuk.controller;

import com.drevotyuk.model.Customer;
import com.drevotyuk.model.Order;
import com.drevotyuk.repository.CustomerRepository;
import com.drevotyuk.service.CustomerService;
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
    @Autowired
    private CustomerService service;

    @GetMapping
    public Iterable<Customer> getAllCustomers() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable int id) {
        return service.getCustomerById(id);
    }

    @GetMapping("/{id}/orders")
    public Iterable<Order> getOrders(@PathVariable int id) {
        return service.getOrdersById(id);
    }

    @PostMapping
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
        // we think of indentical customer's accounts as one person who registered
        // twice, for example using different e-mails
        return new ResponseEntity<>(repository.save(customer), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/order")
    public ResponseEntity<Order> addOrder(@PathVariable int id, @RequestBody Order order) {
        return service.addOrderById(id, order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable int id, @RequestBody Customer customer) {
        return service.updateCustomerById(id, customer);
    }

    @PutMapping("/{customerId}/order/{orderId}")
    public ResponseEntity<Order> updateOrder(
            @PathVariable int customerId, @PathVariable int orderId, @RequestBody Order order) {
        return service.updateOrderById(customerId, orderId, order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable int id) {
        return service.deleteCustomerById(id);
    }

    @DeleteMapping("/{customerId}/order/{orderId}")
    public ResponseEntity<Order> deleteOrder(
            @PathVariable int customerId, @PathVariable int orderId) {
        return service.deleteOrderById(customerId, orderId);
    }
}
