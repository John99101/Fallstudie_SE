package src.main.java.com.bakery.controller;

import src.main.java.com.bakery.service.UserService;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model) {
        User loggedInUser = userService.loginUser(user.getUsername(), user.getPassword());
        if (loggedInUser != null) {
            if (loggedInUser.getRole().equals("customer")) {
                return "redirect:/customer";
            } else if (loggedInUser.getRole().equals("employee")) {
                return "redirect:/employee";
            }
        }
        model.addAttribute("error", "Ungültige Anmeldedaten");
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        boolean success = userService.registerUser(user.getUsername(), user.getPassword(), user.getRole());
        if (success) {
            return "redirect:/login";
        }
        model.addAttribute("error", "Registrierung fehlgeschlagen");
        return "register";
    }
}