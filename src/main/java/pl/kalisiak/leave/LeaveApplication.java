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
		EmployeeRegistrationDTO grzesiek = new EmployeeRegistrationDTO();
		grzesiek.setFirstname("grzesiek");
		grzesiek.setLastname("sowa");
		grzesiek.setEmail("G.Sowa@pwpw.pl");
		grzesiek.setPassword("test");
		grzesiek.setRole(Role.CEO);
		grzesiek.setDepartment(Department.ADMINISTRATION);
		grzesiek.setEmploymentStartDate(LocalDate.of(2018, Month.JULY, 16));

		try {
			service.register(grzesiek);
		} catch (EmailAlreadyTakenException e) {
			logger.severe(e.getMessage());
			return;
		} catch (SupervisorMissingException e) {
			logger.severe(e.getMessage());
			return;
		}

		logger.info(grzesiek.toString());

		Education education = new Education();
		education.setSchoolName("PW");
		education.setLevel(EducationLevel.UNIVERSITY);
		education.setStartDate(LocalDate.of(2018, 6, 15));
		education.setFinishDate(LocalDate.now());
		
		WorkExperience experience = new WorkExperience();
		experience.setCompanyName("PWPW");
		experience.setStartDate(LocalDate.of(2018, 6, 15));
		experience.setFinishDate(LocalDate.now());

		Employee grzesiekE = repo.findByEmail("G.Sowa@pwpw.pl").orElse(null);
		grzesiekE.addWorkExperience(experience);
		grzesiekE.setEducation(education);

		logger.info(experience.toString());
		logger.info(grzesiekE.toString());

		repo.save(grzesiekE);
		
		logger.info(grzesiekE.toString());
		
		EmployeeRegistrationDTO seba = new EmployeeRegistrationDTO();
		seba.setFirstname("seba");
		seba.setLastname("druć");
		seba.setEmail("S.Druc@pwpw.pl");
		seba.setPassword("test");
		seba.setRole(Role.HR);
		seba.setDepartment(Department.IT);
		seba.setSupervisorId(service.findByEmail("G.Sowa@pwpw.pl").getId());
		seba.setEmploymentStartDate(LocalDate.of(2018, Month.JULY, 16));
		seba.setEmploymentFinishDate(LocalDate.of(2019, Month.JULY, 16));

		try {
			service.register(seba);
		} catch (EmailAlreadyTakenException e) {
			logger.severe(e.getMessage());
			return;
		} catch (SupervisorMissingException e) {
			logger.severe(e.getMessage());
			return;
		}

		logger.info(seba.toString());
		Employee sebaE = repo.findByEmail("S.Druc@pwpw.pl").orElse(null);
		logger.info(sebaE.toString());
		Employee sebaSupervisor = sebaE.getSupervisor();
		logger.info(sebaSupervisor.toString());

		Set<EmployeeDTO> supervisedByGrzesiek = service.findAllBySupervisorId(grzesiekE.getId());
		logger.info(supervisedByGrzesiek.toString());
		Set<EmployeeDTO> supervisedBySeba = service.findAllBySupervisorId(sebaE.getId());
		logger.info(supervisedBySeba.toString());

		WorkExperience experience2 = new WorkExperience();
		experience2.setCompanyName("firma");
		experience2.setStartDate(LocalDate.of(2018, 12, 1));
		experience2.setFinishDate(LocalDate.now());

		grzesiekE = repo.findByEmail("G.Sowa@pwpw.pl").orElse(null);
		grzesiekE.addWorkExperience(experience2);

		grzesiekE = repo.save(grzesiekE);

		logger.info(grzesiekE.toString());

		WorkExperienceDTO exDTO = new WorkExperienceDTO();
		exDTO.setCompanyName("polska wytwornia poapierow wartosciowych");
		exDTO.setStartDate(LocalDate.of(2018, 12, 1));
		exDTO.setFinishDate(LocalDate.of(2018, 12, 31));
		exDTO.setEmployeeId(new Long(1));
		WorkExperience experience3 = workExperienceService.dtoToModel(exDTO);

		grzesiekE = repo.findById(experience3.getEmployee().getId()).orElse(null);
		grzesiekE.addWorkExperience(experience3);

		grzesiekE = repo.save(grzesiekE);

		logger.info(grzesiekE.toString());
	}
}
