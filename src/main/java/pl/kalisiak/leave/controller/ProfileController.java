package pl.kalisiak.leave.controller;

import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import pl.kalisiak.leave.DTO.EducationDTO;
import pl.kalisiak.leave.DTO.EmployeeDTO;
import pl.kalisiak.leave.DTO.WorkExperienceDTO;
import pl.kalisiak.leave.exceptions.FinishBeforeStartException;
import pl.kalisiak.leave.exceptions.NoSuchEmployeeException;
import pl.kalisiak.leave.exceptions.SupervisorMissingException;
import pl.kalisiak.leave.service.EmployeeService;

@Controller
public class ProfileController {
	
	@Autowired
	EmployeeService employeeService;

	@GetMapping({ "/profile", "/profile/{id}"})
	public String getProfile(@PathVariable Optional<Long> id, HttpServletRequest request) {
		EmployeeDTO employeeDTO;
		EmployeeDTO supervisor = null;
		try {
			if (id.isPresent()) {
				employeeDTO = employeeService.findById(id.get(), true);
			} else {
				String email = request.getUserPrincipal().getName();
				employeeDTO = employeeService.findByEmail(email, true);
			}
			if (employeeDTO.getSupervisorId() != null) {
				supervisor = employeeService.findById(employeeDTO.getSupervisorId(), false);
			}
		} catch (NoSuchEmployeeException e) {
			return "404";
		}
		request.setAttribute("user", employeeDTO);
		request.setAttribute("supervisor", supervisor);
		return "profile";
	}

	@GetMapping({ "/profile/edit", "/profile/{id}/edit"})
	public String getProfileEdit(@PathVariable Optional<Long> id, HttpServletRequest request) {
		EmployeeDTO employeeDTO;
		try {
			if (id.isPresent()) {
				employeeDTO = employeeService.findById(id.get(), false);
			} else {
				String email = request.getUserPrincipal().getName();
				employeeDTO = employeeService.findByEmail(email, false);
			}
		} catch (NoSuchEmployeeException e) {
			return "404";
		}
		Set<EmployeeDTO> employees = employeeService.findAll(false);
		employees.remove(employeeDTO);
		request.setAttribute("potentialSupervisors", employees);
		request.setAttribute("user", employeeDTO);
		return "profile-edit";
	}

	@GetMapping({ "/profile/add-education", "/profile/{id}/add-education"})
	public String getAddEducationPage(@PathVariable Optional<Long> id, HttpServletRequest request) {
		EmployeeDTO employeeDTO;
		try {
			if (id.isPresent()) {
				employeeDTO = employeeService.findById(id.get(), false);
			} else {
				String email = request.getUserPrincipal().getName();
				employeeDTO = employeeService.findByEmail(email, false);
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
				employeeDTO = employeeService.findById(id.get(), false);
			} else {
				String email = request.getUserPrincipal().getName();
				employeeDTO = employeeService.findByEmail(email, false);
			}
		} catch (NoSuchEmployeeException e) {
			return "404";
		}
		request.setAttribute("user", employeeDTO);
		return "add-experience";
	}

	@PostMapping({ "/profile/add-education", "/profile/{userId}/add-education"})
	public String addEducation(@PathVariable Optional<Long> userId, HttpServletRequest request, EducationDTO educationDTO) {
		EmployeeDTO employeeDTO = null;
		try {
			if (userId.isPresent()) {
				employeeDTO = employeeService.findById(userId.get(), false);
			} else {
				String email = request.getUserPrincipal().getName();
				employeeDTO = employeeService.findByEmail(email, false);
			}
			employeeDTO = employeeService.addEducationToEmployee(employeeDTO, educationDTO, false);
		} catch (NoSuchEmployeeException e) {
			return "404";
		}catch (FinishBeforeStartException e) {
			request.setAttribute("dateOrderError", true);
			request.setAttribute("user", employeeDTO);
			return "add-education";
		}
		
		return "redirect:/profile/" + employeeDTO.getId();
	}

	@PostMapping({ "/profile/add-experience", "/profile/{userId}/add-experience"})
	public String addExperience(@PathVariable Optional<Long> userId, HttpServletRequest request, WorkExperienceDTO experienceDTO) {
		EmployeeDTO employeeDTO = null;
		try {
			if (userId.isPresent()) {
				employeeDTO = employeeService.findById(userId.get(), false);
			} else {
				String email = request.getUserPrincipal().getName();
				employeeDTO = employeeService.findByEmail(email, false);
			}
			employeeDTO = employeeService.addExperienceToEmployee(employeeDTO, experienceDTO, false);
		} catch (NoSuchEmployeeException e) {
			return "404";
		} catch (FinishBeforeStartException e) {
			request.setAttribute("dateOrderError", true);
			request.setAttribute("user", employeeDTO);
			return "add-experience";
		}
		
		return "redirect:/profile/" + employeeDTO.getId();
	}

	@PostMapping({ "/profile/edit", "/profile/{userId}/edit"})
	public String editEmployee(@PathVariable Optional<Long> userId, HttpServletRequest request, EmployeeDTO employeeDTO) {
		try {
			employeeDTO = employeeService.updateEmployee(employeeDTO, false);
		} catch (NoSuchEmployeeException e) {
			return "404";
		} catch (FinishBeforeStartException e) {
			request.setAttribute("dateOrderError", true);
			request.setAttribute("user", employeeDTO);
			Set<EmployeeDTO> employees = employeeService.findAll(false);
			employees.remove(employeeDTO);
			request.setAttribute("potentialSupervisors", employees);
			return "profile-edit";
		} catch (SupervisorMissingException e) {
			request.setAttribute("supervisorMissing", true);
			request.setAttribute("user", employeeDTO);
			Set<EmployeeDTO> employees = employeeService.findAll(false);
			employees.remove(employeeDTO);
			request.setAttribute("potentialSupervisors", employees);
			return "profile-edit";
		}
		
		return "redirect:/profile/" + employeeDTO.getId();
	}

}
