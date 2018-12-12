package pl.kalisiak.leave.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.kalisiak.leave.DTO.WorkExperienceDTO;
import pl.kalisiak.leave.model.WorkExperience;
import pl.kalisiak.leave.repository.EmployeeRepository;
import pl.kalisiak.leave.repository.WorkExperienceRepository;

@Service
public class WorkExperienceService {

    @Autowired
    WorkExperienceRepository repository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public Set<WorkExperience> dtoToModelAll(Set<WorkExperienceDTO> experiencesDTO) {
        if (experiencesDTO == null)
            return null;
        return experiencesDTO.stream()
            .map(dto -> dtoToModel(dto))
            .collect(Collectors.toSet());
    }

    public Set<WorkExperienceDTO> modelToDTOAll(Set<WorkExperience> experiences) {
        if (experiences == null)
            return null;
        return experiences.stream()
            .map(model -> modelToDTO(model))
            .collect(Collectors.toSet());
    }

    public WorkExperience dtoToModel(WorkExperienceDTO workExperienceDTO) {
        if (workExperienceDTO == null)
            return null;
        WorkExperience workExperience = new WorkExperience();
        workExperience.setId(workExperienceDTO.getId());
        workExperience.setCompanyName(workExperienceDTO.getCompanyName());
        workExperience.setStartDate(workExperienceDTO.getStartDate());
        workExperience.setFinishDate(workExperienceDTO.getFinishDate());
        workExperience.setEmployee(employeeRepository.findById(workExperienceDTO.getEmployeeId()).orElse(null));
        return workExperience;
    }

    public WorkExperienceDTO modelToDTO(WorkExperience workExperience) {
        if (workExperience == null)
            return null;
        WorkExperienceDTO workExperienceDTO = new WorkExperienceDTO();
        workExperienceDTO.setId(workExperience.getId());
        workExperienceDTO.setCompanyName(workExperience.getCompanyName());
        workExperienceDTO.setStartDate(workExperience.getStartDate());
        workExperienceDTO.setFinishDate(workExperience.getFinishDate());
        workExperienceDTO.setEmployeeId(workExperience.getEmployee().getId());
        return workExperienceDTO;
    }

}