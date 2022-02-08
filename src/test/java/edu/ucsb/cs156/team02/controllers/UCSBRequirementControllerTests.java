package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBRequirement;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.UCSBRequirementRepository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType; // import media type
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

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

// Authorization tests for /api/UCSBRequirements/all
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
        UCSBRequirement req1 = UCSBRequirement.builder().requirementCode("requirementCode 1").requirementTranslation("requirementTranslation 1").collegeCode("collegeCode 1").objCode("objCode 1").courseCount(1).units(1).inactive(true).id(1L).build();
        UCSBRequirement req2 = UCSBRequirement.builder().requirementCode("requirementCode 2").requirementTranslation("requirementTranslation 2").collegeCode("collegeCode 2").objCode("objCode 2").courseCount(2).units(2).inactive(true).id(1L).build();

        ArrayList<UCSBRequirement> expectedUCSBRequirements = new ArrayList<>();
        expectedUCSBRequirements.addAll(Arrays.asList(req1, req2));
        when(ucsbRequirementRepository.findAll()).thenReturn(expectedUCSBRequirements);

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBRequirements/all"))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(ucsbRequirementRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedUCSBRequirements);
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
                .id(0L)
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
        //justMadeUCSBRequirements.addAll(Arrays.asList(req1));  // ArrayList<UCSBRequirement> justMadeUCSBRequirements = new ArrayList<>();
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

            when(ucsbRequirementRepository.findById(eq(7L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(get("/api/UCSBRequirements?id=7"))
                            .andExpect(status().isBadRequest()).andReturn();

            // assert

            verify(ucsbRequirementRepository, times(1)).findById(7L);
            String responseString = response.getResponse().getContentAsString();
            assertEquals("UCSBRequirement with id 7 not found", responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirement__user_logged_in__delete_UCSBRequirement() throws Exception {
        // arrange

        UCSBRequirement req1 = UCSBRequirement.builder().requirementCode("42e").id(7L).requirementTranslation("requirementTranslation").collegeCode("collegeCode").objCode("objCode").courseCount(1).courseCount(1).units(7).inactive(true).build();
        when(ucsbRequirementRepository.findById(eq(7L))).thenReturn(Optional.of(req1));


        // act
        MvcResult response = mockMvc.perform(
                delete("/api/UCSBRequirements?id=7")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(ucsbRequirementRepository, times(1)).findById(7L);
        verify(ucsbRequirementRepository, times(1)).deleteById(7L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("UCSBRequirement with id 7 deleted", responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_UCSBRequirement__user_logged_in__delete_UCSBRequirement_that_does_not_exist() throws Exception {
        // arrange
        when(ucsbRequirementRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/UCSBRequirements?id=7")
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(ucsbRequirementRepository, times(1)).findById(7L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("UCSBRequirement with id 7 not found", responseString);
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_Req__user_logged_in__put_UCSBRequirement() throws Exception {
        // arrange

        UCSBRequirement ucsbReq1 = UCSBRequirement.builder().requirementCode("UCSBRequirement requirementCode 7").requirementTranslation("UCSBRequirement requirementTranslation 7").collegeCode("UCSBRequirement collegeCode 7").objCode("UCSBRequirement objCode 7").courseCount(7).units(7).inactive(false).id(7L).build();
        // set the updatedUCSBRequirement id to wrong one
        // This shoudl get ignored and overwritten with id in putendpoint when UCSBRequirement is saved

        UCSBRequirement updatedUCSBRequirement = UCSBRequirement.builder().requirementCode("new requirementCode").requirementTranslation("new requirementTranslation").collegeCode("new collegeCode").objCode("new objCode").courseCount(1).units(1).inactive(false).id(55L).build();
        UCSBRequirement correctUCSBRequirement = UCSBRequirement.builder().requirementCode("new requirementCode").requirementTranslation("new requirementTranslation").collegeCode("new collegeCode").objCode("new objCode").courseCount(1).units(1).inactive(false).id(7L).build();

        String requestBody = mapper.writeValueAsString(updatedUCSBRequirement);
        String expectedReturn = mapper.writeValueAsString(correctUCSBRequirement);

        when(ucsbRequirementRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbReq1));
        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBRequirements?id=7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(ucsbRequirementRepository, times(1)).findById(7L);
        verify(ucsbRequirementRepository, times(1)).save(correctUCSBRequirement); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_Req__user_logged_in__cannot_put_UCSBRequirement_that_does_not_exist() throws Exception {
        // arrange

        UCSBRequirement updatedUCSBRequirement = UCSBRequirement.builder().requirementCode("new requirementCode").requirementTranslation("new requirementTranslation").collegeCode("new collegeCode").objCode("new objCode").courseCount(1).units(1).inactive(false).id(55L).build();

        String requestBody = mapper.writeValueAsString(updatedUCSBRequirement);
        when(ucsbRequirementRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/UCSBRequirements?id=7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(ucsbRequirementRepository, times(1)).findById(7L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("UCSBRequirement with id 7 not found", responseString);
    }

}
