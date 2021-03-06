package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Api(description = "UCSBRequirements")
@RequestMapping("/api/UCSBRequirements")
@RestController
@Slf4j
public class UCSBRequirementController extends ApiController {

    //have to make an error class incase requiremetn is MIA
    //full disclosure: I wouldn't have thought to make this if it wasn't for the example
    public class UCSBRequirementOrError{
        Long id;
        UCSBRequirement ucsbRequirement;
        ResponseEntity<String> error;
        public UCSBRequirementOrError(long id){
            this.id =id;
            }
        }



    @Autowired
    UCSBRequirementRepository ucsbRequirementsRepository;
    
    @Autowired 
    ObjectMapper mapper;
    
//this will get and list all of the requirments in the db
//note there is no required Admin privl as this info is usefull to the end user
    @ApiOperation(value = "Get a list of UCSB requirements")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBRequirement> allUsersRequirements() {
        loggingService.logMethod();
        Iterable<UCSBRequirement> requirments = ucsbRequirementsRepository.findAll();
        return requirments;
    }


    //get single req. This uses the code
    // this will use the error or not class we made at the top of this file.
    @ApiOperation(value = "Find single requirement via requirementCode")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getRequirementViaCode(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();
    
        UCSBRequirementOrError  reqOrErr = new UCSBRequirementOrError(id);

        reqOrErr = doesRequirementExist(reqOrErr);
        if (reqOrErr.error != null) {
            return reqOrErr.error;
        }

        String body = mapper.writeValueAsString(reqOrErr.ucsbRequirement);
        return ResponseEntity.ok().body(body);
    }


    //post a new req
    @ApiOperation(value = "Create a new requirement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public UCSBRequirement postRequirement(
            @ApiParam("requirementCode") @RequestParam String requirementCode,
            @ApiParam("requirementTranslation") @RequestParam String requirementTranslation,
            @ApiParam("collegeCode") @RequestParam String collegeCode,
            @ApiParam("objCode") @RequestParam String objCode,
            @ApiParam("courseCount") @RequestParam int courseCount,
            @ApiParam("units") @RequestParam int units,
            @ApiParam("inactive") @RequestParam boolean inactive) {
        loggingService.logMethod();
        UCSBRequirement justMadeRequirment = new UCSBRequirement();
        justMadeRequirment.setRequirementCode(requirementCode);
        justMadeRequirment.setRequirementTranslation(requirementTranslation);
        justMadeRequirment.setCollegeCode(collegeCode);
        justMadeRequirment.setObjCode(objCode);
        justMadeRequirment.setCourseCount(courseCount);
        justMadeRequirment.setUnits(units);
        justMadeRequirment.setInactive(inactive);
        UCSBRequirement savedRequirement = ucsbRequirementsRepository.save(justMadeRequirment);
        return savedRequirement;        
    }
    public UCSBRequirementOrError doesRequirementExist(UCSBRequirementOrError reqOrErr){
        Optional<UCSBRequirement> optionalRequirement = ucsbRequirementsRepository.findById(reqOrErr.id);

        if (optionalRequirement.isEmpty()) {
            //reqOrErr.error = ResponseEntity.badRequest().body(String.format("UCSBRequirement with id "+reqOrErr.id+" not found"));
            reqOrErr.error = ResponseEntity.badRequest().body(String.format("UCSBRequirement with id %d not found", reqOrErr.id));
        } else {
            reqOrErr.ucsbRequirement  = optionalRequirement.get();
        }
        return reqOrErr;
    }   

    // put
    @ApiOperation(value = "Update a single UCSBRequirement")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PutMapping("")
    public ResponseEntity<String> putUCSBRequirement(
            @ApiParam("id") @RequestParam Long id,
            @RequestBody @Valid UCSBRequirement incomingUCSBRequirement) throws JsonProcessingException {
        loggingService.logMethod();

        UCSBRequirementOrError roe = new UCSBRequirementOrError(id);

        roe = doesRequirementExist(roe);
        if (roe.error != null) {
            return roe.error;
        }
        incomingUCSBRequirement.setId(id);
        ucsbRequirementsRepository.save(incomingUCSBRequirement);

        String body = mapper.writeValueAsString(incomingUCSBRequirement);
        return ResponseEntity.ok().body(body);
    }






//delete

@ApiOperation(value = "delete a UCSBRequirement ")
@PreAuthorize("hasRole('ROLE_USER')")
@DeleteMapping("")
public ResponseEntity<String> deleteUCSBRequirement(
        @ApiParam("id") @RequestParam Long id) {
    loggingService.logMethod();

    UCSBRequirementOrError roe = new UCSBRequirementOrError(id);

    roe = doesRequirementExist(roe);
    if (roe.error != null) {
        return roe.error;
    }

    ucsbRequirementsRepository.deleteById(id);
    return ResponseEntity.ok().body(String.format("UCSBRequirement with id %d deleted", id));

}




}
