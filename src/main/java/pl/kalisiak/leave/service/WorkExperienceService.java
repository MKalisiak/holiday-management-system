package pl.kalisiak.leave.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.kalisiak.leave.DTO.WorkExperienceDTO;
import pl.kalisiak.leave.model.WorkExperience;

@Service
public class WorkExperienceService extends GenericService<WorkExperience> {

    public static Set<WorkExperience> dtoToModelAll(Set<WorkExperienceDTO> experiencesDTO) {
        if (experiencesDTO == null)
            return null;
        return experiencesDTO.stream()
            .map(WorkExperienceService::dtoToModel)
            .collect(Collectors.toSet());
    }

    public static Set<WorkExperienceDTO> modelToDTOAll(Set<WorkExperience> experiences) {
        if (experiences == null)
            return null;
        return experiences.stream()
            .map(WorkExperienceService::modelToDTO)
            .collect(Collectors.toSet());
    }

    public static WorkExperience dtoToModel(WorkExperienceDTO workExperienceDTO) {
        if (workExperienceDTO == null)
            return null;
        WorkExperience workExperience = new WorkExperience();
        workExperience.setId(workExperienceDTO.getId());
        workExperience.setCompanyName(workExperienceDTO.getCompanyName());
        workExperience.setStartDate(workExperienceDTO.getStartDate());
        workExperience.setFinishDate(workExperienceDTO.getFinishDate());
        return workExperience;
    }

    public static WorkExperienceDTO modelToDTO(WorkExperience workExperience) {
        if (workExperience == null)
            return null;
        WorkExperienceDTO workExperienceDTO = new WorkExperienceDTO();
        workExperienceDTO.setId(workExperience.getId());
        workExperienceDTO.setCompanyName(workExperience.getCompanyName());
        workExperienceDTO.setStartDate(workExperience.getStartDate());
        workExperienceDTO.setFinishDate(workExperience.getFinishDate());
        return workExperienceDTO;
    }

}