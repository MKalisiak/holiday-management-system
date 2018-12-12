package pl.kalisiak.leave.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import pl.kalisiak.leave.DTO.EducationDTO;
import pl.kalisiak.leave.DTO.EmployeeDTO;
import pl.kalisiak.leave.DTO.WorkExperienceDTO;
import pl.kalisiak.leave.exceptions.NoSuchEmployeeException;
import pl.kalisiak.leave.service.EmployeeService;

@Controller
public class ProfileController {
	
	@Autowired
	EmployeeService employeeService;

	@GetMapping({ "/profile", "/profile/{id}"})
	public String getProfile(@PathVariable Optional<Long> id, HttpServletRequest request) {
		EmployeeDTO employeeDTO;
		try {
			if (id.isPresent()) {
				employeeDTO = employeeService.findById(id.get());
			} else {
				String email = request.getUserPrincipal().getName();
				employeeDTO = employeeService.findByEmail(email);
			}
		} catch (NoSuchEmployeeException e) {
			return "404";
		}
		request.setAttribute("user", employeeDTO);
		return "profile";
	}

	@GetMapping({ "/profile/add-education", "/profile/{id}/add-education"})
	public String getAddEducationPage(@PathVariable Optional<Long> id, HttpServletRequest request) {
		EmployeeDTO employeeDTO;
		try {
			if (id.isPresent()) {
				employeeDTO = employeeService.findById(id.get());
			} else {
				String email = request.getUserPrincipal().getName();
				employeeDTO = employeeService.findByEmail(email);
			}
		} catch (NoSuchEmployeeException e) {
			return "404";
		}
		request.setAttribute("user", employeeDTO);
		return "add-education";
	}

	@GetMapping({ "/profile/add-experience", "/profile/{id}/add-experience"})
	public String getAddExperiencePage(@PathVariable Optional<Long> id, HttpServletRequest request) {
		EmployeeDTO employeeDTO;
		try {
			if (id.isPresent()) {
				employeeDTO = employeeService.findById(id.get());
			} else {
				String email = request.getUserPrincipal().getName();
				employeeDTO = employeeService.findByEmail(email);
			}
		} catch (NoSuchEmployeeException e) {
			return "404";
		}
		request.setAttribute("user", employeeDTO);
		return "add-experience";
	}

	@PostMapping({ "/profile/add-education", "/profile/{userId}/add-education"})
	public String addEducation(@PathVariable Optional<Long> userId, HttpServletRequest request, EducationDTO educationDTO) {
		EmployeeDTO employeeDTO;
		try {
			if (userId.isPresent()) {
				employeeDTO = employeeService.findById(userId.get());
			} else {
				String email = request.getUserPrincipal().getName();
				employeeDTO = employeeService.findByEmail(email);
			}
			employeeDTO = employeeService.addEducationToEmployee(employeeDTO, educationDTO);
		} catch (NoSuchEmployeeException e) {
			return "404";
		}
		
		request.setAttribute("user", employeeDTO);
		return "redirect:/profile/" + employeeDTO.getId();
	}

	@PostMapping({ "/profile/add-experience", "/profile/{userId}/add-experience"})
	public String addExperience(@PathVariable Optional<Long> userId, HttpServletRequest request, WorkExperienceDTO experienceDTO) {
		EmployeeDTO employeeDTO;
		try {
			if (userId.isPresent()) {
				employeeDTO = employeeService.findById(userId.get());
			} else {
				String email = request.getUserPrincipal().getName();
				employeeDTO = employeeService.findByEmail(email);
			}
			employeeDTO = employeeService.addExperienceToEmployee(employeeDTO, experienceDTO);
		} catch (NoSuchEmployeeException e) {
			return "404";
		}
		
		request.setAttribute("user", employeeDTO);
		return "redirect:/profile/" + employeeDTO.getId();
	}

}
