package pl.kalisiak.leave.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import pl.kalisiak.leave.DTO.EmployeeDTO;
import pl.kalisiak.leave.exceptions.NoSuchEmployeeException;
import pl.kalisiak.leave.service.EmployeeService;

@Controller
public class ProfileController {
	
	@Autowired
	EmployeeService employeeService;

	@GetMapping("/profile")
	public String getHome(HttpServletRequest request) {
		String email = request.getUserPrincipal().getName();
		EmployeeDTO employeeDTO;
		try {
			employeeDTO = employeeService.findByEmail(email);
		} catch (NoSuchEmployeeException e) {
			return "404";
		}
		request.setAttribute("user", employeeDTO);
		return "profile";
	}

}
