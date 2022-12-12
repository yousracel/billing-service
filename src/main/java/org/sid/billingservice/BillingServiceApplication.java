package org.sid.billingservice;

import org.sid.billingservice.entities.Bill;
import org.sid.billingservice.entities.ProductItem;
import org.sid.billingservice.feign.CustomerRestClient;
import org.sid.billingservice.feign.ProductItemRestClient;
import org.sid.billingservice.model.Customer;
import org.sid.billingservice.model.Product;
import org.sid.billingservice.repository.BillRepository;
import org.sid.billingservice.repository.ProductItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.PagedModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BillingServiceApplication.class, args);
	}

    @Bean
    CommandLineRunner start(BillRepository billRepository, ProductItemRepository productItemRepository,
                            CustomerRestClient customerRestClient, ProductItemRestClient productItemRestClient)
    {
        return args -> {
            for (long i = 1L; i < 4L; i++) {
                Customer customer = customerRestClient.getCustomerById(i);
                Bill bill = billRepository.save(new Bill(null, new Date(),null,customer.getId()));
                Collection<ProductItem> productItems = new ArrayList<>();
                PagedModel<Product> pagedProducts = productItemRestClient.pageProducts();
                pagedProducts.forEach(product -> {
                    if(Math.random()>=0.5){
                        ProductItem productItem = new ProductItem();
                        productItem.setProductName(product.getName());
                        productItem.setPrice(product.getPrice());
                        productItem.setQuantity(1 + new Random().nextInt(100));
                        productItem.setBill(bill);
                        productItem.setProductId(product.getId());
                        productItemRepository.save(productItem);
                        productItems.add(productItem);
                    }
                });
                bill.setProductItems(productItems);
                billRepository.save(bill);
            }
        };
    }
}
