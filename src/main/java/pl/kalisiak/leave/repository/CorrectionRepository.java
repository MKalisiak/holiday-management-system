package pl.kalisiak.leave.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import pl.kalisiak.leave.model.Correction;
import pl.kalisiak.leave.model.Employee;

@Repository
public interface CorrectionRepository extends GenericRepository<Correction>{
    public List<Correction> findAllByEmployee(Employee employee);
}
