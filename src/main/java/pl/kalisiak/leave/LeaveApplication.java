package pl.kalisiak.leave;

import java.time.LocalDate;
import java.time.Month;
import java.util.logging.Logger;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import pl.kalisiak.leave.DTO.EmployeeDTO;
import pl.kalisiak.leave.DTO.EmployeeRegistrationDTO;
import pl.kalisiak.leave.DTO.WorkExperienceDTO;
import pl.kalisiak.leave.exceptions.EmailAlreadyTakenException;
import pl.kalisiak.leave.exceptions.SupervisorMissingException;
import pl.kalisiak.leave.model.Department;
import pl.kalisiak.leave.model.Education;
import pl.kalisiak.leave.model.EducationLevel;
import pl.kalisiak.leave.model.Employee;
import pl.kalisiak.leave.model.Role;
import pl.kalisiak.leave.model.WorkExperience;
import pl.kalisiak.leave.repository.EmployeeRepository;
import pl.kalisiak.leave.service.EmployeeService;
import pl.kalisiak.leave.service.WorkExperienceService;

@SpringBootApplication
public class LeaveApplication implements ApplicationRunner {

	public static void main(String[] args) {
		SpringApplication.run(LeaveApplication.class, args);
	}

	private Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	EmployeeService service;

	@Autowired
	WorkExperienceService workExperienceService;

	@Autowired
	EmployeeRepository repo;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		EmployeeRegistrationDTO michal = new EmployeeRegistrationDTO();
		michal.setFirstname("Michał");
		michal.setLastname("Kalisiak");
		michal.setEmail("blackraider77@gmail.com");
		michal.setPassword("test");
		michal.setRole(Role.CEO);
		michal.setDepartment(Department.ADMINISTRATION);
		michal.setEmploymentStartDate(LocalDate.of(2018, Month.JULY, 16));

		try {
			service.register(michal);
		} catch (EmailAlreadyTakenException e) {
			logger.severe(e.getMessage());
			return;
		} catch (SupervisorMissingException e) {
			logger.severe(e.getMessage());
			return;
		}

		logger.info(michal.toString());

		Education education = new Education();
		education.setSchoolName("PW");
		education.setLevel(EducationLevel.UNIVERSITY);
		education.setStartDate(LocalDate.of(2018, 6, 15));
		education.setFinishDate(LocalDate.now());
		
		WorkExperience experience = new WorkExperience();
		experience.setCompanyName("PWPW");
		experience.setStartDate(LocalDate.of(2018, 6, 15));
		experience.setFinishDate(LocalDate.now());

		Employee michalE = repo.findByEmail("blackraider77@gmail.com").orElse(null);
		michalE.addWorkExperience(experience);
		michalE.setEducation(education);

		logger.info(experience.toString());
		logger.info(michalE.toString());

		repo.save(michalE);
		
		logger.info(michalE.toString());
		
		WorkExperience experience2 = new WorkExperience();
		experience2.setCompanyName("firma");
		experience2.setStartDate(LocalDate.of(2018, 12, 1));
		experience2.setFinishDate(LocalDate.now());

		michalE = repo.findByEmail("blackraider77@gmail.com").orElse(null);
		michalE.addWorkExperience(experience2);

		michalE = repo.save(michalE);

		WorkExperienceDTO exDTO = new WorkExperienceDTO();
		exDTO.setCompanyName("polska wytwornia poapierow wartosciowych");
		exDTO.setStartDate(LocalDate.of(2018, 12, 1));
		exDTO.setFinishDate(LocalDate.of(2018, 12, 31));
		exDTO.setEmployeeId(new Long(1));
		WorkExperience experience3 = workExperienceService.dtoToModel(exDTO);

		michalE = repo.findById(experience3.getEmployee().getId()).orElse(null);
		michalE.addWorkExperience(experience3);

		michalE = repo.save(michalE);

		logger.info(michalE.toString());
	}
}
