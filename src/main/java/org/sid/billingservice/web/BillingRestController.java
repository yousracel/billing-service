package org.sid.billingservice.web;

import org.sid.billingservice.entities.Bill;
import org.sid.billingservice.feign.CustomerRestClient;
import org.sid.billingservice.feign.ProductItemRestClient;
import org.sid.billingservice.model.Product;
import org.sid.billingservice.repository.BillRepository;
import org.sid.billingservice.repository.ProductItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
public class BillingRestController {
    private  BillRepository billRepository;
    private  ProductItemRepository productItemRepository;
    private  CustomerRestClient customerRestClient;
    private  ProductItemRestClient productItemRestClient;

    public BillingRestController(BillRepository billRepository, ProductItemRepository productItemRepository, CustomerRestClient customerRestClient, ProductItemRestClient productItemRestClient) {
        this.billRepository = billRepository;
        this.productItemRepository = productItemRepository;
        this.customerRestClient = customerRestClient;
        this.productItemRestClient = productItemRestClient;
    }

    @GetMapping(path = "/fullbill/{id}")
    public Bill getBill(@PathVariable(name = "id") Long id){
        Bill bill=billRepository.findById(id).get();
        bill.getProductItems().forEach(pi->{
            Product product=productItemRestClient.getProductById(pi.getProductId());
            pi.setProductName(product.getName());
        });
        return bill;
    }

    @GetMapping(path = "/fullBills")
    public List<Bill> getBills(){
        List<Bill> bills = billRepository.findAll();
        bills.forEach((bill -> bill.getProductItems().forEach(productItem -> {
            Product product = productItemRestClient.getProductById(productItem.getProductId());
            productItem.setProductName(product.getName());
        })));
        return bills;
    }

    @DeleteMapping(path = "/fullBill/{id}")
    public void deleteBill(@PathVariable(name = "id") Long id){
        Bill bill = billRepository.findById(id).orElseThrow();
        productItemRepository.deleteAll(bill.getProductItems());
        billRepository.delete(bill);
    }
}
