package pl.kalisiak.leave.DTO;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.kalisiak.leave.model.Department;
import pl.kalisiak.leave.model.Role;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeRegistrationDTO extends GenericDTO {
	
	@NotEmpty
	private String firstname;
	
	@NotEmpty
	private String lastname;
	
	@NotEmpty
	@Email
	private String email;

	@NotEmpty
	private String password;
	
	@NotNull
	private Role role;

	@NotNull
	private Department department;

	private long supervisorId;

	@NotNull
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate employmentStartDate;

	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate employmentFinishDate;
}
