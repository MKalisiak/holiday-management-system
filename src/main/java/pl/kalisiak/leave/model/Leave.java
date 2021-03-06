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

// TODO dont let leaves overlap
// TODO dont let leaves start before employment start date

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Leave extends GenericModel {

	@NotNull
	private LocalDate startDate;
	
	@NotNull
	private LocalDate finishDate;

	@NotNull
	private int durationInMinutes;
	
	@ManyToOne
	@JoinColumn(name = "employee_id")
	@NotNull
	@ToString.Exclude
	private Employee employee;

}
