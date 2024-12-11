package src.main.java.com.bakery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import src.main.java.com.bakery.service.ProductService;
import src.main.java.com.bakery.model.Product;

import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String showProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "customer";
    }

    @PostMapping("/order")
    public String placeOrder(@RequestParam int productId, @RequestParam int quantity, Model model) {
        // Place order logic
        return "redirect:/customer";
    }
}
