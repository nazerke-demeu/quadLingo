package org.example.quadlingo.controller;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        model.addAttribute("loggedIn", loggedIn != null && loggedIn);
        return "home.html";
    }
}