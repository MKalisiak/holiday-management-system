package pl.kalisiak.leave.domain;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import pl.kalisiak.leave.model.Education;
import pl.kalisiak.leave.model.EducationLevel;
import pl.kalisiak.leave.model.Employee;
import pl.kalisiak.leave.model.WorkExperience;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryRulesTests {

	@Test
	public void testCategoryChangeDateSimple() {
		Employee employee = new Employee();
		
		Education education = new Education();
		education.setStartDate(LocalDate.of(2015, Month.OCTOBER, 1));
		education.setFinishDate(LocalDate.of(2019, Month.FEBRUARY, 28));
		education.setLevel(EducationLevel.UNIVERSITY);
		
		employee.setEducation(education);
		
		WorkExperience experience = new WorkExperience();
		experience.setStartDate(LocalDate.of(2019, Month.MARCH, 1));
		experience.setFinishDate(LocalDate.of(2021, Month.MARCH, 1));
		
		employee.addWorkExperience(experience);
		
		LocalDate changeDate = CategoryRules.getCategoryChangeDate(employee);
		assertTrue(changeDate.isEqual(LocalDate.of(2021, Month.FEBRUARY, 28)));
	}
	
	@Test
	public void testCategoryChangeDateNotEnoughExperience() {
		Employee employee = new Employee();
		
		Education education = new Education();
		education.setStartDate(LocalDate.of(2015, Month.OCTOBER, 1));
		education.setFinishDate(LocalDate.of(2019, Month.FEBRUARY, 28));
		education.setLevel(EducationLevel.UNIVERSITY);
		
		employee.setEducation(education);
		
		WorkExperience experience = new WorkExperience();
		experience.setStartDate(LocalDate.of(2019, Month.MARCH, 1));
		experience.setFinishDate(LocalDate.of(2020, Month.MARCH, 1));
		
		employee.addWorkExperience(experience);
		
		employee.setEmploymentStartDate(LocalDate.of(2020, Month.MARCH, 1));
		
		LocalDate changeDate = CategoryRules.getCategoryChangeDate(employee);
		assertTrue(changeDate.isEqual(LocalDate.of(2021, Month.FEBRUARY, 28)));
	}
	
	@Test
	public void testCategoryChangeDateNoEducation() {
		Employee employee = new Employee();
		
		WorkExperience experience0 = new WorkExperience();
		experience0.setStartDate(LocalDate.of(2000, Month.MARCH, 1));
		experience0.setFinishDate(LocalDate.of(2003, Month.MARCH, 1));
		
		employee.addWorkExperience(experience0);
		
		WorkExperience experience1 = new WorkExperience();
		experience1.setStartDate(LocalDate.of(2004, Month.MARCH, 1));
		experience1.setFinishDate(LocalDate.of(2007, Month.MARCH, 1));
		
		employee.addWorkExperience(experience1);
		
		WorkExperience experience2 = new WorkExperience();
		experience2.setStartDate(LocalDate.of(2010, Month.MARCH, 1));
		experience2.setFinishDate(LocalDate.of(2015, Month.MARCH, 1));
		
		employee.addWorkExperience(experience2);
		
		LocalDate changeDate = CategoryRules.getCategoryChangeDate(employee);
		assertTrue(changeDate.isEqual(LocalDate.of(2014, Month.FEBRUARY, 28)));
	}
	
	@Test
	public void testCategoryChangeDateNoEducationNotEnoughExperience() {
		Employee employee = new Employee();
		
		WorkExperience experience0 = new WorkExperience();
		experience0.setStartDate(LocalDate.of(2000, Month.MARCH, 1));
		experience0.setFinishDate(LocalDate.of(2003, Month.MARCH, 1));
		
		employee.addWorkExperience(experience0);
		
		WorkExperience experience1 = new WorkExperience();
		experience1.setStartDate(LocalDate.of(2004, Month.MARCH, 1));
		experience1.setFinishDate(LocalDate.of(2007, Month.MARCH, 1));
		
		employee.addWorkExperience(experience1);
		
		employee.setEmploymentStartDate(LocalDate.of(2010, Month.MARCH, 1));
		
		LocalDate changeDate = CategoryRules.getCategoryChangeDate(employee);
		assertTrue(changeDate.isEqual(LocalDate.of(2014, Month.FEBRUARY, 28)));
	}

	@Test
	public void testCategoryChangeDateExperienceBeforeEducation() {
		Employee employee = new Employee();
		
		Education education = new Education();
		education.setStartDate(LocalDate.of(2015, Month.OCTOBER, 1));
		education.setFinishDate(LocalDate.of(2019, Month.FEBRUARY, 28));
		education.setLevel(EducationLevel.UNIVERSITY);
		
		employee.setEducation(education);
		
		WorkExperience experience = new WorkExperience();
		experience.setStartDate(LocalDate.of(2010, Month.MARCH, 1));
		experience.setFinishDate(LocalDate.of(2013, Month.MARCH, 1));
		
		employee.addWorkExperience(experience);
		
		LocalDate changeDate = CategoryRules.getCategoryChangeDate(employee);
		assertTrue(changeDate.isEqual(LocalDate.of(2019, Month.FEBRUARY, 28)));
	}

	@Test
	public void testCategoryChangeDateEnoughExperienceBeforeEducation() {
		Employee employee = new Employee();
		
		Education education = new Education();
		education.setStartDate(LocalDate.of(2015, Month.OCTOBER, 1));
		education.setFinishDate(LocalDate.of(2019, Month.FEBRUARY, 28));
		education.setLevel(EducationLevel.UNIVERSITY);
		
		employee.setEducation(education);
		
		WorkExperience experience0 = new WorkExperience();
		experience0.setStartDate(LocalDate.of(2000, Month.MARCH, 1));
		experience0.setFinishDate(LocalDate.of(2010, Month.MARCH, 1));
		
		employee.addWorkExperience(experience0);

		WorkExperience experience1 = new WorkExperience();
		experience1.setStartDate(LocalDate.of(2007, Month.MARCH, 1));
		experience1.setFinishDate(LocalDate.of(2013, Month.MARCH, 1));
		
		employee.addWorkExperience(experience1);
		
		LocalDate changeDate = CategoryRules.getCategoryChangeDate(employee);
		assertTrue(changeDate.isEqual(LocalDate.of(2010, Month.FEBRUARY, 27)));
	}
	
	@Test
	public void testCategoryChangeDateExperienceBeforeEducationOverlapping() {
		Employee employee = new Employee();
		
		Education education = new Education();
		education.setStartDate(LocalDate.of(2015, Month.OCTOBER, 1));
		education.setFinishDate(LocalDate.of(2019, Month.FEBRUARY, 28));
		education.setLevel(EducationLevel.UNIVERSITY);
		
		employee.setEducation(education);
		
		WorkExperience experience0 = new WorkExperience();
		experience0.setStartDate(LocalDate.of(2010, Month.MARCH, 1));
		experience0.setFinishDate(LocalDate.of(2011, Month.MARCH, 1));
		
		employee.addWorkExperience(experience0);
		
		WorkExperience experience1 = new WorkExperience();
		experience1.setStartDate(LocalDate.of(2014, Month.OCTOBER, 1));
		experience1.setFinishDate(LocalDate.of(2020, Month.MARCH, 1));
		
		employee.addWorkExperience(experience1);
		
		LocalDate changeDate = CategoryRules.getCategoryChangeDate(employee);
		assertTrue(changeDate.isEqual(LocalDate.of(2019, Month.FEBRUARY, 28)));
	}
	
	@Test
	public void testCategoryChangeDateStudiedWhileWorking() {
		Employee employee = new Employee();
		
		Education education = new Education();
		education.setStartDate(LocalDate.of(2015, Month.OCTOBER, 1));
		education.setFinishDate(LocalDate.of(2019, Month.FEBRUARY, 28));
		education.setLevel(EducationLevel.UNIVERSITY);
		
		employee.setEducation(education);
		
		WorkExperience experience = new WorkExperience();
		experience.setStartDate(LocalDate.of(2014, Month.OCTOBER, 1));
		experience.setFinishDate(LocalDate.of(2020, Month.MARCH, 1));
		
		employee.addWorkExperience(experience);
		
		LocalDate changeDate = CategoryRules.getCategoryChangeDate(employee);
		assertTrue(changeDate.isEqual(LocalDate.of(2020, Month.FEBRUARY, 28)));
	}

	@Test
	public void testCategoryChangeDateNoExperience() {
		Employee employee = new Employee();
		
		Education education = new Education();
		education.setStartDate(LocalDate.of(2015, Month.OCTOBER, 1));
		education.setFinishDate(LocalDate.of(2019, Month.FEBRUARY, 28));
		education.setLevel(EducationLevel.UNIVERSITY);
		
		employee.setEducation(education);
		
		employee.setEmploymentStartDate(LocalDate.of(2019, Month.MARCH, 1));
		
		LocalDate changeDate = CategoryRules.getCategoryChangeDate(employee);
		assertTrue(changeDate.isEqual(LocalDate.of(2021, Month.FEBRUARY, 28)));
	}

	@Test
	public void testCategoryChangeDateNoExperienceNoEducation() {
		Employee employee = new Employee();
		
		employee.setEmploymentStartDate(LocalDate.of(2000, Month.MARCH, 1));
		
		LocalDate changeDate = CategoryRules.getCategoryChangeDate(employee);
		assertTrue(changeDate.isEqual(LocalDate.of(2010, Month.FEBRUARY, 27)));
	}
}
