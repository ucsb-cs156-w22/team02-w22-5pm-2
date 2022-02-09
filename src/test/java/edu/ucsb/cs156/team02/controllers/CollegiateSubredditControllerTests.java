package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.repositories.UserRepository;
import edu.ucsb.cs156.team02.testconfig.TestConfig;
import edu.ucsb.cs156.team02.ControllerTestCase;
import edu.ucsb.cs156.team02.entities.CollegiateSubreddit;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.repositories.CollegiateSubredditRepository;

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

@WebMvcTest(controllers = CollegiateSubredditController.class)
@Import(TestConfig.class)
public class CollegiateSubredditControllerTests extends ControllerTestCase {
    @MockBean
    CollegiateSubredditRepository collegiateSubredditRepository;

    @MockBean
    UserRepository userRepository; // Needed to run tests bc of interdependency


    // Authorization tests for /api/collegiateSubreddit/all
    @Test
    public void api_collegiateSubreddit_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/collegiateSubreddits/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddit_all__user_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/collegiateSubreddits/all"))
                .andExpect(status().isOk());
    }

    // Authorization tests for /api/collegiateSubreddit/post
    @Test
    public void api_collegiateSubreddit_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/collegiateSubreddits/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddit_post__logged_out__returns_200() throws Exception {
        mockMvc.perform(get("/api/collegiateSubreddits/all"))
                .andExpect(status().isOk());
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddit_all__user_logged_in() throws Exception {

        // arrange
        CollegiateSubreddit csr1 = CollegiateSubreddit.builder().name("CollegiateSubreddit 1").location("Location 1").subreddit("CollegiateSubreddit 1").id(1L).build();
        CollegiateSubreddit csr2 = CollegiateSubreddit.builder().name("CollegiateSubreddit 2").location("Location 2").subreddit("CollegiateSubreddit 2").id(2L).build();

        ArrayList<CollegiateSubreddit> expectedCollegiateSubreddits = new ArrayList<>();
        expectedCollegiateSubreddits.addAll(Arrays.asList(csr1, csr2));
        when(collegiateSubredditRepository.findAll()).thenReturn(expectedCollegiateSubreddits);

        // act
        MvcResult response = mockMvc.perform(get("/api/collegiateSubreddits/all"))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).findAll();
        String expectedJson = mapper.writeValueAsString(expectedCollegiateSubreddits);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }

    
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddit_post__user_logged_in() throws Exception {
        // arrange

        CollegiateSubreddit expectedCsr = CollegiateSubreddit.builder()
                .name("Test Name")
                .location("Test Location")
                .subreddit("Test Subreddit")
                .id(0L)
                .build();

        when(collegiateSubredditRepository.save(eq(expectedCsr))).thenReturn(expectedCsr);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/collegiateSubreddits/post?name=Test Name&location=Test Location&subreddit=Test Subreddit")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).save(expectedCsr);
        String expectedJson = mapper.writeValueAsString(expectedCsr);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }    


    //Mock test find a collegiateSubbreddit by id=7 where it exists
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddit__user_logged_in__returns_a_collegiateSubreddit_that_exists() throws Exception {

            // arrange

            CollegiateSubreddit collegiateSubreddit1 = CollegiateSubreddit.builder().name("CollegiateSubbreddit 1").location("Location 1").subreddit("Subreddit 1").id(7L).build();
            when(collegiateSubredditRepository.findById(eq(7L))).thenReturn(Optional.of(collegiateSubreddit1));

            // act
            MvcResult response = mockMvc.perform(get("/api/collegiateSubreddits?id=7"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(collegiateSubredditRepository, times(1)).findById(eq(7L));
            String expectedJson = mapper.writeValueAsString(collegiateSubreddit1);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    // Mock test find a collegiateSubbreddit by id=7 where it does NOT exist
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddit__user_logged_in__search_for_collegiateSubreddit_that_does_not_exist() throws Exception {

            // arrange


            when(collegiateSubredditRepository.findById(eq(7L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(get("/api/collegiateSubreddits?id=7"))
                            .andExpect(status().isBadRequest()).andReturn();

            // assert

            verify(collegiateSubredditRepository, times(1)).findById(eq(7L));
            String responseString = response.getResponse().getContentAsString();
            assertEquals("collegiateSubreddit with id 7 not found", responseString);
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddit__user_logged_in__delete_collegiateSubreddit() throws Exception {
        // arrange

        CollegiateSubreddit collegiateSubreddit1 = CollegiateSubreddit.builder().name("CollegiateSubbreddit 1").location("Location 1").subreddit("Subreddit 1").id(7L).build();
        when(collegiateSubredditRepository.findById(eq(7L))).thenReturn(Optional.of(collegiateSubreddit1));

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/collegiateSubreddits?id=7")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).findById(7L);
        verify(collegiateSubredditRepository, times(1)).deleteById(7L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("collegiateSubreddit with id 7 deleted", responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddit__user_logged_in__delete_collegiateSubreddit_that_does_not_exist() throws Exception {
        // arrange
        when(collegiateSubredditRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                delete("/api/collegiateSubreddits?id=7")
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).findById(7L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("collegiateSubreddit with id 7 not found", responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddit__user_logged_in__put_collegiateSubreddit() throws Exception {
        // arrange

        CollegiateSubreddit collegiateSubreddit1 = CollegiateSubreddit.builder()
                .name("CollegiateSubreddit Name 7")
                .location("CollegiateSubreddit Location 7")
                .subreddit("CollegiateSubreddit Subreddit 7")
                .id(7L)
                .build();
        // This shoudl get ignored and overwritten with id in putendpoint when UCSBSubject is saved

        CollegiateSubreddit updatedCollegiateSubreddit = CollegiateSubreddit.builder()                
                .name("Test Name")
                .location("Test Location")
                .subreddit("Test Subreddit")
                .id(55L)
                .build();
                CollegiateSubreddit correctCollegiateSubreddit = CollegiateSubreddit.builder()
                .name("Test Name")
                .location("Test Location")
                .subreddit("Test Subreddit")
                .id(7L)
                .build();

        String requestBody = mapper.writeValueAsString(updatedCollegiateSubreddit);
        String expectedReturn = mapper.writeValueAsString(correctCollegiateSubreddit);

        when(collegiateSubredditRepository.findById(eq(7L))).thenReturn(Optional.of(collegiateSubreddit1));
        // act
        MvcResult response = mockMvc.perform(
                put("/api/collegiateSubreddits?id=7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).findById(7L);
        verify(collegiateSubredditRepository, times(1)).save(correctCollegiateSubreddit); // should be saved with correct user
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedReturn, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_collegiateSubreddit__user_logged_in__cannot_put_collegiateSubreddit_that_does_not_exist() throws Exception {
        // arrange

        CollegiateSubreddit updatedCollegiateSubreddit = CollegiateSubreddit.builder()
        .name("New Name")
        .location("New Location")
        .subreddit("New Subreddit")
        .id(55L)
        .build();

        String requestBody = mapper.writeValueAsString(updatedCollegiateSubreddit);
        when(collegiateSubredditRepository.findById(eq(7L))).thenReturn(Optional.empty());

        // act
        MvcResult response = mockMvc.perform(
                put("/api/collegiateSubreddits?id=7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
                        .content(requestBody)
                        .with(csrf()))
                .andExpect(status().isBadRequest()).andReturn();

        // assert
        verify(collegiateSubredditRepository, times(1)).findById(7L);
        String responseString = response.getResponse().getContentAsString();
        assertEquals("CollegiateSubreddit with id 7 not found", responseString);
    }

}

