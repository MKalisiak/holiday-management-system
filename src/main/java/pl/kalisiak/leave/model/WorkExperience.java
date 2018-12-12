package pl.kalisiak.leave.model;

import java.time.LocalDate;
import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

// TODO nie liczyc zachodzacych na siebie okresow podwojnie
// TODO nie liczyc zachodzacej na siebie pracy i nauki

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class WorkExperience extends GenericModel {
	
	@NotEmpty
	private String companyName;
	
	@NotNull
	private LocalDate startDate;
	
	@NotNull
	private LocalDate finishDate;
	
	@ManyToOne
	@JoinColumn(name = "employee_id")
	@NotNull
	@ToString.Exclude
	private Employee employee;
	
}
