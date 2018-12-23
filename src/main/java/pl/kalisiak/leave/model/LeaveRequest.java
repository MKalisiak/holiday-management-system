package pl.kalisiak.leave.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class LeaveRequest extends GenericModel {

	@NotNull
	private LocalDate startDate;
	
	@NotNull
	private LocalDate finishDate;

	@NotNull
	private int durationInMinutes;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private RequestStatus status;

	@ManyToOne
	@JoinColumn(name = "employee_id")
	@NotNull
	@ToString.Exclude
	private Employee employee;

}
