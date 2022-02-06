package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.controllers.UCSBRequirementController.UCSBRequirementOrError;
import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import org.mockito.stubbing.OngoingStubbing;
import java.util.Optional;
import java.util.OptionalInt;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBRequirementController.class)
@Import(TestConfig.class)
public class UCSBRequirementControllerTests extends ControllerTestCase {
    @MockBean
    UCSBRequirementRepository ucsbRequirementRepository;

    @MockBean
    UserRepository userRepository; 

    @MockBean
    UCSBRequirementController reqCon;

// Authorization tests for /api/UCSBRequirements/admin/all
    @Test
    public void api_UCSBRequirement_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_all__user_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().isOk());
    }

    // Authorization tests for /api/UCSBRequirements/post
    @Test
    public void api_UCSBRequirements_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/UCSBRequirements/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_post__logged_out__returns_200() throws Exception {
        mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().isOk());
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_all__user_logged_in() throws Exception {

        // arrange
        UCSBRequirement req1 = UCSBRequirement.builder().requirementCode("requirementCode").requirementTranslation("requirementTranslation").collegeCode("collegeCode").objCode("objCode").courseCount(0).courseCount(0).units(0).inactive(true).build();
        UCSBRequirement req2 = UCSBRequirement.builder().requirementCode("requirementCode2").requirementTranslation("requirementTranslation2").collegeCode("collegeCode2").objCode("objCode2").courseCount(2).courseCount(2).units(2).inactive(false).build();

        
        ArrayList<UCSBRequirement> justMadeUCSBRequirements = new ArrayList<>();
        justMadeUCSBRequirements.addAll(Arrays.asList(req1, req2));
        when(ucsbRequirementRepository.findAll()).thenReturn(justMadeUCSBRequirements);

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(ucsbRequirementRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(justMadeUCSBRequirements);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_post__user_logged_in() throws Exception {
        // arrange

        UCSBRequirement expectedReq = UCSBRequirement.builder()
                .requirementCode("Test requirementCode")
                .requirementTranslation("Test requirementTranslation")
                .collegeCode("Test collegeCode")
                .objCode("Test objCode")
                .courseCount(1)
                .units(1)
                .inactive(true)
                .build();

        when(ucsbRequirementRepository.save(eq(expectedReq))).thenReturn(expectedReq);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/UCSBRequirements/post?requirementCode=Test requirementCode&requirementTranslation=Test requirementTranslation&collegeCode=Test collegeCode&objCode=Test objCode&courseCount=1&units=1&inactive=true")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(ucsbRequirementRepository, times(1)).save(expectedReq);
        String expectedJson = mapper.writeValueAsString(expectedReq);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }    


    // test find a req with id 7 that is made
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirements_test_valid_Code_42() throws Exception {

            // arrange

            User u = currentUserService.getCurrentUser().getUser();
            UCSBRequirement req1 = UCSBRequirement.builder().requirementCode("42e").id(7L).requirementTranslation("requirementTranslation").collegeCode("collegeCode").objCode("objCode").courseCount(1).courseCount(1).units(7).inactive(true).build();
            
           // ArrayList<UCSBRequirement> justMadeUCSBRequirements = new ArrayList<>();
        //justMadeUCSBRequirements.addAll(Arrays.asList(req1));
          

        when(ucsbRequirementRepository.findById(eq(7L))).thenReturn(Optional.of(req1));


            //when(todoRepository.findById(eq(7L))).thenReturn(Optional.of(todo1));
            // act
            MvcResult response = mockMvc.perform(get("/api/UCSBRequirements?id=7"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(ucsbRequirementRepository, times(1)).findById(7L);
            String expectedJson = mapper.writeValueAsString(req1);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    // test find a req by find by id where  7  is NOT valid
    
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_Req__user_logged_in__search_for_req_that_does_not_exist() throws Exception {

            // arrange
                                                                                //make 42e not f
            //UCSBRequirement req1 = UCSBRequirement.builder().requirementCode("42e").requirementTranslation("requirementTranslation").collegeCode("collegeCode").objCode("objCode").courseCount(1).courseCount(1).units(1).inactive(true).build();
            //User u = currentUserService.getCurrentUser().getUser();
            //UCSBRequirement req1 = UCSBRequirement.builder().requirementCode("42e").id(7L).requirementTranslation("requirementTranslation").collegeCode("collegeCode").objCode("objCode").courseCount(1).courseCount(1).units(666).inactive(true).build();
            //ArrayList<UCSBRequirement> justMadeUCSBRequirements = new ArrayList<>();
            //justMadeUCSBRequirements.addAll(Arrays.asList(req1));
            //when( ucsbRequirementRepository.findById(42L)).thenReturn(null);
            
            //HERE WHY ISN'T THE SWAP HAPPENING
            //when(ucsbRequirementRepository.findById(eq(666L))).thenReturn(null);
            //optional.isempty wasn't working. Wouldn't swap and it would still find the value;
            
            when(ucsbRequirementRepository.findById(eq(666L))).thenReturn(Optional.empty());
            //when(reqCon.doesRequirementExist()) .findById(eq(666L))).thenReturn(Optional.empty());

            //when(ucsbRequirementRepository.findByRequirementCode("42f")).thenReturn(justMadeUCSBRequirements);

            // act
            MvcResult response = mockMvc.perform(get("/api/UCSBRequirements?id=666"))
                            .andExpect(status().isBadRequest()).andReturn();

            // assert

            verify(ucsbRequirementRepository, times(1)).findById(666L);
            String responseString = response.getResponse().getContentAsString();
            assertEquals("UCSBRequirement with ID 666 not found", responseString);
    }

}

