package com.drevotyuk.controller;

import com.drevotyuk.model.Customer;
import com.drevotyuk.model.Order;
import com.drevotyuk.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private CustomerService service;

    @GetMapping
    public Iterable<Customer> getAllCustomers() {
        return service.getAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable int id) {
        return service.getCustomer(id);
    }

    @GetMapping("/{id}/orders")
    public Iterable<Order> getOrders(@PathVariable int id) {
        return service.getOrders(id);
    }

    @GetMapping("/{customerId}/order/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable int customerId, @PathVariable int orderId) {
        return service.getOrderById(customerId, orderId);
    }

    @PostMapping
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
        return service.addCustomer(customer);
    }

    @PostMapping("/{id}/order")
    public ResponseEntity<Order> addOrder(@PathVariable int id, @RequestBody Order order) {
        return service.addOrder(id, order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable int id, @RequestBody Customer customer) {
        return service.updateCustomer(id, customer);
    }

    @PutMapping("/{customerId}/order/{orderId}")
    public ResponseEntity<Order> updateOrder(
            @PathVariable int customerId, @PathVariable int orderId, @RequestBody Order order) {
        return service.updateOrder(customerId, orderId, order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Customer> deleteCustomer(@PathVariable int id) {
        return service.deleteCustomer(id);
    }

    @DeleteMapping("/{customerId}/order/{orderId}")
    public ResponseEntity<Order> deleteOrder(
            @PathVariable int customerId, @PathVariable int orderId) {
        return service.deleteOrder(customerId, orderId);
    }
}
