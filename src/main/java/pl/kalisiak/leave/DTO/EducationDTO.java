package pl.kalisiak.leave.DTO;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import pl.kalisiak.leave.model.EducationLevel;

@Data
@EqualsAndHashCode(callSuper = true)
public class EducationDTO extends GenericDTO {
	
	@NotEmpty
	private String schoolName;
	
	@NotNull
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate startDate;
	
	@NotNull
	@DateTimeFormat(iso = ISO.DATE)
	private LocalDate finishDate;
    
    @NotNull
	private EducationLevel level;

	@NotNull
	private Long employeeId;
	
}