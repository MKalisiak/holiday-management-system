package pl.kalisiak.leave.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import pl.kalisiak.leave.model.Employee;
import pl.kalisiak.leave.model.LeaveRequest;

@Repository
public interface LeaveRequestRepository extends GenericRepository<LeaveRequest>{
    public List<LeaveRequest> findAllByEmployee(Employee employee);
}
