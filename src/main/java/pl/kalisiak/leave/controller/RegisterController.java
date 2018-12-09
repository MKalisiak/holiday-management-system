package pl.kalisiak.leave.controller;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import pl.kalisiak.leave.DTO.EmployeeDTO;
import pl.kalisiak.leave.DTO.EmployeeRegistrationDTO;
import pl.kalisiak.leave.exceptions.EmailAlreadyTakenException;
import pl.kalisiak.leave.exceptions.SupervisorMissingException;
import pl.kalisiak.leave.service.EmployeeService;


@Controller
public class RegisterController {
	
	@Autowired
	EmployeeService employeeService;

	@GetMapping("/register")
	public String getRegister(HttpServletRequest request) {
		Set<EmployeeDTO> employees = employeeService.findAll();
		request.setAttribute("potentialSupervisors", employees);
		return "register-employee";
	}

	@PostMapping("/register")
	public String registerEmployee(HttpServletRequest request, EmployeeRegistrationDTO registrationDTO) {
		try {
			employeeService.register(registrationDTO);
		} catch (EmailAlreadyTakenException e) {
			request.setAttribute("emailTaken", true);
		} catch (SupervisorMissingException e) {
			request.setAttribute("supervisorMissing", true);
		}
		return "register-employee";
	}
	
}
