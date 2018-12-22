package pl.kalisiak.leave.domain;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import pl.kalisiak.leave.model.Department;
import pl.kalisiak.leave.model.Education;
import pl.kalisiak.leave.model.EducationLevel;
import pl.kalisiak.leave.model.Employee;
import pl.kalisiak.leave.model.Leave;
import pl.kalisiak.leave.model.Role;
import pl.kalisiak.leave.model.WorkExperience;
import pl.kalisiak.leave.repository.EmployeeRepository;
import pl.kalisiak.leave.repository.LeaveRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LeaveCalculatorTests {

	@Autowired
	LeaveCalculator leaveCalculator;

	@Autowired
	EmployeeRepository employeeRepository;

	@Autowired
	LeaveRepository leaveRepository;

	private Employee employee;

	@Before
	public void setup() {
		leaveRepository.deleteAll();
		employeeRepository.deleteByEmail("john.doe@example.com");
		this.employee = new Employee();
		this.employee.setFirstname("John");
        this.employee.setLastname("Doe");
        this.employee.setPassword("test");
        this.employee.setEmail("john.doe@example.com");
		this.employee.addRole(Role.EMPLOYEE);
		this.employee.addRole(Role.CEO);
		this.employee.setDepartment(Department.SALES);
		this.employee.setEmploymentStartDate(LocalDate.now());
		employeeRepository.save(this.employee);
	}

	@Test
	public void testMinutesLeftSimpleFirstCategory() {		
		Education education = new Education();
		education.setSchoolName("Example School");
		education.setStartDate(LocalDate.of(2015, Month.OCTOBER, 1));
		education.setFinishDate(LocalDate.of(2019, Month.FEBRUARY, 28));
		education.setLevel(EducationLevel.UNIVERSITY);
		
		this.employee.setEducation(education);
		
		WorkExperience experience = new WorkExperience();
		experience.setCompanyName("Example Company");
		experience.setStartDate(LocalDate.of(2019, Month.MARCH, 1));
		experience.setFinishDate(LocalDate.of(2020, Month.MARCH, 1));
		
		this.employee.addWorkExperience(experience);
		
		this.employee.setEmploymentStartDate(LocalDate.of(2020, Month.APRIL, 1));

		employeeRepository.save(this.employee);

		int minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2020, Month.APRIL, 1));
		assertTrue(minutes == 15 * 8 * 60);
	}
	
	@Test
	public void testMinutesLeftSimpleSecondCategory() {		
		Education education = new Education();
		education.setSchoolName("Example School");
		education.setStartDate(LocalDate.of(2015, Month.OCTOBER, 1));
		education.setFinishDate(LocalDate.of(2019, Month.FEBRUARY, 28));
		education.setLevel(EducationLevel.UNIVERSITY);
		
		this.employee.setEducation(education);
		
		WorkExperience experience = new WorkExperience();
		experience.setCompanyName("Example Company");
		experience.setStartDate(LocalDate.of(2019, Month.MARCH, 1));
		experience.setFinishDate(LocalDate.of(2020, Month.MARCH, 1));
		
		this.employee.addWorkExperience(experience);
		
		this.employee.setEmploymentStartDate(LocalDate.of(2020, Month.APRIL, 1));

		employeeRepository.save(this.employee);

		int minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2021, Month.APRIL, 1));
		assertTrue(minutes == 41 * 8 * 60);
	}
	
	
	@Test
	public void testMinutesLeftSimpleBothCategoriesFourYears() {		
		Education education = new Education();
		education.setSchoolName("Example School");
		education.setStartDate(LocalDate.of(2015, Month.OCTOBER, 1));
		education.setFinishDate(LocalDate.of(2019, Month.FEBRUARY, 28));
		education.setLevel(EducationLevel.UNIVERSITY);
		
		this.employee.setEducation(education);
		
		WorkExperience experience = new WorkExperience();
		experience.setCompanyName("Example Company");
		experience.setStartDate(LocalDate.of(2019, Month.MARCH, 1));
		experience.setFinishDate(LocalDate.of(2020, Month.MARCH, 1));

		this.employee.addWorkExperience(experience);
		
		this.employee.setEmploymentStartDate(LocalDate.of(2020, Month.APRIL, 1));

		employeeRepository.save(this.employee);

		int minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2023, Month.APRIL, 1));
		assertTrue(minutes == 93 * 8 * 60);
	}

	@Test
	public void testMinutesLeftSimpleSecondCategoryOverDueYears() {		
		Education education = new Education();
		education.setSchoolName("Example School");
		education.setStartDate(LocalDate.of(2015, Month.OCTOBER, 1));
		education.setFinishDate(LocalDate.of(2019, Month.FEBRUARY, 28));
		education.setLevel(EducationLevel.UNIVERSITY);
		
		this.employee.setEducation(education);
		
		WorkExperience experience = new WorkExperience();
		experience.setCompanyName("Example Company");
		experience.setStartDate(LocalDate.of(2019, Month.MARCH, 1));
		experience.setFinishDate(LocalDate.of(2021, Month.MARCH, 1));

		this.employee.addWorkExperience(experience);
		
		this.employee.setEmploymentStartDate(LocalDate.of(2021, Month.APRIL, 1));

		employeeRepository.save(this.employee);

		int minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2021, Month.APRIL, 1));
		assertTrue(minutes == 19.5 * 8 * 60);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2022, Month.APRIL, 1));
		assertTrue(minutes == 45.5 * 8 * 60);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2023, Month.APRIL, 1));
		assertTrue(minutes == 71.5 * 8 * 60);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2024, Month.APRIL, 1));
		assertTrue(minutes == 97.5 * 8 * 60);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2025, Month.APRIL, 1));
		assertTrue(minutes == 104 * 8 * 60);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2026, Month.APRIL, 1));
		assertTrue(minutes == 104 * 8 * 60);
	}

	@Test
	public void testMinutesLeftFirstCategoryWithLeave() {		
		Education education = new Education();
		education.setSchoolName("Example School");
		education.setStartDate(LocalDate.of(2015, Month.OCTOBER, 1));
		education.setFinishDate(LocalDate.of(2019, Month.FEBRUARY, 28));
		education.setLevel(EducationLevel.UNIVERSITY);
		
		this.employee.setEducation(education);
		
		WorkExperience experience = new WorkExperience();
		experience.setCompanyName("Example Company");
		experience.setStartDate(LocalDate.of(2019, Month.MARCH, 1));
		experience.setFinishDate(LocalDate.of(2020, Month.MARCH, 1));
		
		this.employee.addWorkExperience(experience);
		
		this.employee.setEmploymentStartDate(LocalDate.of(2020, Month.APRIL, 1));

		employeeRepository.save(this.employee);

		Leave leave = new Leave();
		leave.setStartDate(LocalDate.of(2020, Month.APRIL, 6));
		leave.setFinishDate(LocalDate.of(2020, Month.APRIL, 10));
		leave.setDurationInMinutes(5 * 8 * 60);
		leave.setEmployee(employee);

		leaveRepository.save(leave);

		int minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2020, Month.APRIL, 1));
		assertTrue(minutes == 10 * 8 * 60);
	}

	@Test
	public void testMinutesLeftFirstCategoryWithLeaveContainingWeekend() {		
		Education education = new Education();
		education.setSchoolName("Example School");
		education.setStartDate(LocalDate.of(2015, Month.OCTOBER, 1));
		education.setFinishDate(LocalDate.of(2019, Month.FEBRUARY, 28));
		education.setLevel(EducationLevel.UNIVERSITY);
		
		this.employee.setEducation(education);
		
		WorkExperience experience = new WorkExperience();
		experience.setCompanyName("Example Company");
		experience.setStartDate(LocalDate.of(2019, Month.MARCH, 1));
		experience.setFinishDate(LocalDate.of(2020, Month.MARCH, 1));
		
		this.employee.addWorkExperience(experience);
		
		this.employee.setEmploymentStartDate(LocalDate.of(2020, Month.APRIL, 1));

		employeeRepository.save(this.employee);

		Leave leave = new Leave();
		leave.setStartDate(LocalDate.of(2020, Month.APRIL, 6));
		leave.setFinishDate(LocalDate.of(2020, Month.APRIL, 17));
		leave.setDurationInMinutes(10 * 8 * 60);
		leave.setEmployee(employee);

		leaveRepository.save(leave);

		int minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2020, Month.APRIL, 1));
		assertTrue(minutes == 5 * 8 * 60);
	}

	@Test
	public void testMinutesLeftFirstCategoryWithLeaveContainingNewYear() {		
		Education education = new Education();
		education.setSchoolName("Example School");
		education.setStartDate(LocalDate.of(2015, Month.OCTOBER, 1));
		education.setFinishDate(LocalDate.of(2019, Month.FEBRUARY, 28));
		education.setLevel(EducationLevel.UNIVERSITY);
		
		this.employee.setEducation(education);
		
		WorkExperience experience = new WorkExperience();
		experience.setCompanyName("Example Company");
		experience.setStartDate(LocalDate.of(2019, Month.MARCH, 1));
		experience.setFinishDate(LocalDate.of(2020, Month.MARCH, 1));
		
		this.employee.addWorkExperience(experience);
		
		this.employee.setEmploymentStartDate(LocalDate.of(2020, Month.APRIL, 1));

		employeeRepository.save(this.employee);

		Leave leave = new Leave();
		leave.setStartDate(LocalDate.of(2020, Month.DECEMBER, 28));
		leave.setFinishDate(LocalDate.of(2021, Month.JANUARY, 8));
		leave.setDurationInMinutes(10 * 8 * 60);
		leave.setEmployee(employee);

		leaveRepository.save(leave);

		int minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2020, Month.APRIL, 1));
		assertTrue(minutes == 11 * 8 * 60);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2021, Month.JANUARY, 1));
		assertTrue(minutes == 25 * 8 * 60);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2021, Month.APRIL, 1));
		assertTrue(minutes == 31 * 8 * 60);
	}

	@Test
	public void testMinutesLeftFirstJob() {		
		Education education = new Education();
		education.setSchoolName("Example School");
		education.setStartDate(LocalDate.of(2015, Month.OCTOBER, 1));
		education.setFinishDate(LocalDate.of(2019, Month.FEBRUARY, 28));
		education.setLevel(EducationLevel.UNIVERSITY);
		
		this.employee.setEducation(education);
		
		this.employee.setEmploymentStartDate(LocalDate.of(2020, Month.APRIL, 1));

		employeeRepository.save(this.employee);

		int minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2020, Month.APRIL, 1));
		assertTrue(minutes == 1 * 800);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2020, Month.MAY, 1));
		assertTrue(minutes == 2 * 800);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2020, Month.JUNE, 1));
		assertTrue(minutes == 3 * 800);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2020, Month.JULY, 1));
		assertTrue(minutes == 4 * 800);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2020, Month.AUGUST, 1));
		assertTrue(minutes == 5 * 800);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2020, Month.SEPTEMBER, 1));
		assertTrue(minutes == 6 * 800);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2020, Month.OCTOBER, 1));
		assertTrue(minutes == 7 * 800);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2020, Month.NOVEMBER, 1));
		assertTrue(minutes == 8 * 800);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2020, Month.DECEMBER, 1));
		assertTrue(minutes == 9 * 800);
		minutes = leaveCalculator.getMinutesLeftForEmployeeOnDate(this.employee, LocalDate.of(2021, Month.JANUARY, 1));
		assertTrue(minutes == 9 * 800 + 20 * 8 * 60);
	}

}
