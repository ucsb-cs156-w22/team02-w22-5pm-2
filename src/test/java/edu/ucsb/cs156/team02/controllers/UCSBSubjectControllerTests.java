package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.UCSBSubject;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.UCSBSubjectRepository;

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
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBSubjectController.class)
@Import(TestConfig.class)
public class UCSBSubjectControllerTests extends ControllerTestCase {
    @MockBean
    UCSBSubjectRepository ucsbSubjectRepository;

    @MockBean
    UserRepository userRepository; // Needed to run tests bc of interdependency

    // Mock test find a UCSBSubject by id=7 where it exists
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_ucsbSubject__user_logged_in__returns_a_ucsbSubject_that_exists() throws Exception {

        // arrange

        UCSBSubject ucsbSubject7 = UCSBSubject.builder().subjectCode("UCSBSubject Code 7")
                .subjectTranslation("UCSBsubject Translation 7").deptCode("UCSBSubject deptCode 7").collegeCode("UCSBSubject collegeCode 7").relatedDeptCode("UCSB relatedDeptCode 7").inactive(false).id(7L).build();
        when(ucsbSubjectRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbSubject7));

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects?id=7"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(ucsbSubjectRepository, times(1)).findById(eq(7L));
        String expectedJson = mapper.writeValueAsString(ucsbSubject7);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    // Mock test find a UCSBSubject by id=7 where it does NOT exist
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_ucsbSubject__user_logged_in__search_for_ucsbSubject_that_does_not_exist()
            throws Exception {

        // arrange

        when(ucsbSubjectRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(get("/api/UCSBSubjects?id=7"))
                .andExpect(status().isBadRequest()).andReturn();

        // assert

        verify(ucsbSubjectRepository, times(1)).findById(eq(7L));
        String responseString = response.getResponse().getContentAsString();
        assertEquals("UCSBSubject with id 7 not found", responseString);
    }
}