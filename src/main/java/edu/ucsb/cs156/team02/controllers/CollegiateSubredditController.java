package edu.ucsb.cs156.team02.controllers;

import edu.ucsb.cs156.team02.entities.CollegiateSubreddit;
import edu.ucsb.cs156.team02.entities.User;
import edu.ucsb.cs156.team02.models.CurrentUser;
import edu.ucsb.cs156.team02.repositories.CollegiateSubredditRepository;
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

@Api(description = "CollegiateSubreddits")
@RequestMapping("/api/collegiateSubreddits")
@RestController
@Slf4j
public class CollegiateSubredditController extends ApiController {
    
    //Inner class helps to check if a collegiate subreddit exists, and it helps with giving an error message.
    public class CollegiateSubbreditOrError {
        Long id;
        CollegiateSubreddit collegiateSubreddit;
        ResponseEntity<String> error;

        public CollegiateSubbreditOrError(Long id) {
            this.id = id;
        }
    }

    @Autowired
    CollegiateSubredditRepository collegiateSubredditRepository;

    @Autowired
    ObjectMapper mapper;

    @ApiOperation(value = "List all CollegiateSubreddits in database")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<CollegiateSubreddit> allCollegiateSubreddits() {
        loggingService.logMethod();
        Iterable<CollegiateSubreddit> csr = collegiateSubredditRepository.findAll();
        return csr;
    }

    @ApiOperation(value = "Get a single record in CollegiateSubbreddits table (if it exists)")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<String> getCollegiateSubbredditById(
            @ApiParam("id") @RequestParam Long id) throws JsonProcessingException {
        loggingService.logMethod();

        //coe to shorted "CollegiateSubbreddit or Error"

        CollegiateSubbreditOrError coe = new CollegiateSubbreditOrError(id);

        coe = doesCollegiateSubredditExist(coe);
        if (coe.error != null) {
            return coe.error;
        }
        String body = mapper.writeValueAsString(coe.collegiateSubreddit);
        return ResponseEntity.ok().body(body);
    }

    @ApiOperation(value = "Create a new CollegiateSubreddit")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public CollegiateSubreddit postCollegiateSubreddit(
            @ApiParam("name") @RequestParam String name,
            @ApiParam("location") @RequestParam String location,
            @ApiParam("subreddit") @RequestParam String subreddit) {
        loggingService.logMethod();
    
        CollegiateSubreddit csr = new CollegiateSubreddit();
        csr.setName(name);
        csr.setLocation(location);
        csr.setSubreddit(subreddit);
        CollegiateSubreddit savedCsr = collegiateSubredditRepository.save(csr);
        return savedCsr;
    }

    @ApiOperation(value = "Delete a CollegiateSubreddit")
    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("")
    public ResponseEntity<String> deleteCollegiateSubreddit(
            @ApiParam("id") @RequestParam Long id) {
        loggingService.logMethod();

        //coe to shorted "CollegiateSubbreddit or Error"
        CollegiateSubbreditOrError coe = new CollegiateSubbreditOrError(id);

        coe = doesCollegiateSubredditExist(coe);
        if (coe.error != null) {
            return coe.error;
        }

        collegiateSubredditRepository.deleteById(id);
        return ResponseEntity.ok().body(String.format("collegiateSubreddit with id %d deleted", id));
    }

    /**
     * Pre-conditions: coe.id is value to look up, coe.collegiatesubreddit and coe.error are null
     * 
     * Post-condition: if todo with id coe.id exists, coe.collegiateSubreddit now refers to it, and
     * error is null.
     * Otherwise, collegiateSubreddit with id coe.id does not exist, and error is a suitable return
     * value to
     * report this error condition.
     */
    public CollegiateSubbreditOrError doesCollegiateSubredditExist(CollegiateSubbreditOrError coe) {

        Optional<CollegiateSubreddit> optionalCollegiateSubbreddit = collegiateSubredditRepository.findById(coe.id);

        if (optionalCollegiateSubbreddit.isEmpty()) {
            coe.error = ResponseEntity
                    .badRequest()
                    .body(String.format("collegiateSubreddit with id %d not found", coe.id));
        } else {
            coe.collegiateSubreddit = optionalCollegiateSubbreddit.get();
        }
        return coe;
    }


}
