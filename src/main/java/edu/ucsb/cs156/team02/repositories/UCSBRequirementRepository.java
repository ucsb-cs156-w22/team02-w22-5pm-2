package edu.ucsb.cs156.team02.repositories;

import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UCSBRequirementRepository extends CrudRepository<UCSBRequirement, Long> {
    Iterable<UCSBRequirement> findByRequirementCode(String requirementCode);
    Iterable<UCSBRequirement> findByRequirementTranslation(String requirementTranlsation);
    Iterable<UCSBRequirement> findByCollegeCode(String collegeCode);
    Iterable<UCSBRequirement> findByObjCode(String objCode);
    Iterable<UCSBRequirement> findByCourseCount(int courseCount);
    Iterable<UCSBRequirement> findByUnits(int units);
    Iterable<UCSBRequirement> findByInactive(boolean inactive);

    

}
