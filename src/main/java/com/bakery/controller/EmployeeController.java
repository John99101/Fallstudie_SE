package com.bakery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import java.util.List;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public String showOrdersAndInventory(Model model) {
        List<Order> orders = orderService.getAllOrders();
        List<Inventory> inventory = inventoryService.getAllInventory();
        model.addAttribute("orders", orders);
        model.addAttribute("inventory", inventory);
        return "employee/dashboard";
    }

    @PostMapping("/updateInventory")
    public String updateInventory(@RequestParam int productId, @RequestParam int quantity, Model model) {
        // Lagerbestandsaktualisierung
        return "redirect:/employee";
    }
}