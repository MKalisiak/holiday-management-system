package pl.kalisiak.leave.model;

import java.time.LocalDate;

import javax.persistence.Entity;
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
public class Correction extends GenericModel {

	@NotNull
	private int year;

	@NotNull
	private int durationInMinutes;
	
	@ManyToOne
	@JoinColumn(name = "employee_id")
	@NotNull
	@ToString.Exclude
	private Employee employee;

}
