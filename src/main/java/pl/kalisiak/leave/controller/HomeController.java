package pl.kalisiak.leave.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	@GetMapping("/")
	public String getHome() {
		return "home";
	}

	@GetMapping("/user")
	public String getUser() {
		return "user";
	}
	
}
