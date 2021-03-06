package pl.kalisiak.leave.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Education extends GenericModel {
	
	@NotEmpty
	private String schoolName;
	
	@NotNull
	private LocalDate startDate;
	
	@NotNull
	private LocalDate finishDate;
    
    @NotNull
	@Enumerated(EnumType.STRING)
	private EducationLevel level;

	@ManyToOne
	@JoinColumn(name = "employee_id")
	@NotNull
	@ToString.Exclude
	private Employee employee;
	
}