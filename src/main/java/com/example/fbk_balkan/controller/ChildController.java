package com.example.fbk_balkan.controller;
import com.example.fbk_balkan.dto.*;
import com.example.fbk_balkan.entity.User;
import com.example.fbk_balkan.repository.*;
import com.example.fbk_balkan.payload.ApiSuccess;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import com.example.fbk_balkan.service.ChildService;
import java.util.List;
@RestController
@PreAuthorize("hasRole('COACH')")
@RequestMapping("/api/coach/children")
@RequiredArgsConstructor
public class ChildController {






    private final ChildService childService;
    private final CoachTeamRepository coachTeamRepository;
    private final TeamMembershipRepository teamMembershipRepository;
    private final UserRepository userRepository;
    private final ChildRepository childRepository;
    private final TeamRepository teamRepository;






    @GetMapping
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<List<ChildDTO>> getCoachPlayers(Authentication authentication) {
        return ResponseEntity.ok(
                childService.getChildrenForCoach(authentication.getName())
        );
    }


    // ------------------------ CREATE child ------------------------


    @PostMapping
    @PreAuthorize("hasRole('COACH')")
    public ResponseEntity<ApiSuccess<ChildResponse>> createChild(
            @Valid @RequestBody CreateChildRequest request,
            Authentication authentication
    ) {
        ChildResponse created = childService.createChildForCoach(
                request,
                authentication.getName()
        );


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiSuccess<>("Barnet har skapats!", created));
    }


    // ------------------------ UPDATE child ------------------------
    @PutMapping("/{childId}")
    @PreAuthorize("hasRole('COACH')")
    @Transactional
    public ResponseEntity<ApiSuccess<ChildResponse>> updateChild(
            @PathVariable Long childId,
            @Valid @RequestBody UpdateChildRequest request,
            Authentication authentication
    ) {
        User coach = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Coach not found"));


        ChildResponse updated = childService.updateChildForCoach(
                childId,
                request,
                coach.getUserId(),
                coach.getEmail()
        );


        return ResponseEntity.ok(new ApiSuccess<>("Barnet har uppdaterats!", updated));
    }


    // ------------------------ DELETE child ------------------------
    @DeleteMapping("/{childId}")
    @PreAuthorize("hasRole('COACH')")
    @Transactional
    public ResponseEntity<ApiSuccess<Void>> deleteChild(
            @PathVariable Long childId,
            Authentication authentication
    ) {
        childService.deleteChild(authentication.getName(), childId);
        return ResponseEntity.ok(new ApiSuccess<>("Barnet har tagits bort!", null));
    }


}