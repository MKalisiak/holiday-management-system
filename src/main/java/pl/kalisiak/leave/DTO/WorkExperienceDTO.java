package pl.kalisiak.leave.DTO;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WorkExperienceDTO extends GenericDTO {
	
	@NotEmpty
	private String companyName;
	
	@NotNull
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate startDate;
	
	@NotNull
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate finishDate;

	@NotNull
	private Long employeeId;

}
