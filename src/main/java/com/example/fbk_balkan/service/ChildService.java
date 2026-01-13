package com.example.fbk_balkan.service;
import com.example.fbk_balkan.dto.*;
import com.example.fbk_balkan.entity.*;
import com.example.fbk_balkan.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.data.rest.webmvc.ResourceNotFoundException;


import org.springframework.stereotype.Service;
import com.example.fbk_balkan.dto.TeamDTO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.example.fbk_balkan.dto.ChildDTO;
import com.example.fbk_balkan.repository.ChildRepository;
import com.example.fbk_balkan.repository.CoachTeamRepository;
import com.example.fbk_balkan.repository.TeamMembershipRepository;
import com.example.fbk_balkan.repository.TeamRepository;
import java.time.LocalDate;
import java.util.*;






@RequiredArgsConstructor
@Transactional
@Service
public class ChildService {


    private static final Logger log =
            LoggerFactory.getLogger(ChildService.class);


    private final ChildRepository childRepository;
    private final TeamRepository teamRepository;
    private final CoachTeamRepository coachTeamRepository;
    private final TeamMembershipRepository teamMembershipRepository;
    private final UserRepository userRepository;






    // ---------------- GET CHILDREN ----------------
    public List<ChildDTO> getChildrenForCoach(String coachEmail) {
        User coach = userRepository.findByEmail(coachEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Tränaren hittades inte"));


        return childRepository.findChildrenForCoach(coach.getUserId())
                .stream()
                .map(this::toDTO)
                .toList();
    }




    @Transactional
    public ChildResponse createChildForCoach(
            CreateChildRequest request,
            String coachEmail
    ) {
        //  Validate team ownership
        Team team = teamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));


        boolean coachOwnsTeam =
                coachTeamRepository.existsByCoach_EmailAndTeam_TeamId(
                        coachEmail,
                        team.getTeamId()
                );


        if (!coachOwnsTeam) {
            throw new AccessDeniedException("Du har inte behörighet till detta lag");
        }


        //  Create child
        Child child = new Child();
        child.setFullName(sanitize(request.getFullName()));
        child.setBirthYear(request.getBirthYear());
        child.setTeamCategory(sanitize(request.getTeamCategory()));
        child.setNotes(sanitize(request.getNotes()));


        //  Create membership
        TeamMembership membership = new TeamMembership();
        membership.setChild(child);
        membership.setTeam(team);
        //membership.setJoinedAt(LocalDate.now());


        child.getTeamMemberships().add(membership);


        //  Save
        Child savedChild = childRepository.save(child);


        return toChildResponse(savedChild);
    }






    // ---------------- UPDATE CHILD ----------------
    @Transactional
    public ChildResponse updateChildForCoach(
            Long childId,
            UpdateChildRequest request,
            Long coachUserId,
            String coachEmail
    ) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Child not found"));


        // Check coach owns at least one of the child's teams
        boolean ownsTeam = child.getTeamMemberships().stream()
                .anyMatch(m -> coachTeamRepository.existsByCoach_EmailAndTeam_TeamId(coachEmail, m.getTeam().getTeamId()));


        if (!ownsTeam) {
            throw new AccessDeniedException("Du kan inte ändra detta barn");
        }


        // Update allowed fields
        child.setFullName(sanitize(request.getFullName()));
        child.setBirthYear(request.getBirthYear());
        child.setTeamCategory(sanitize(request.getTeamCategory()));
        child.setNotes(sanitize(request.getNotes()));


        Child saved = childRepository.save(child);
        return toChildResponse(saved);
    }




    // ---------------- DELETE CHILD ----------------
    @Transactional
    public void deleteChild(String coachEmail, Long childId) {
        Child child = childRepository.findById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Child not found"));


        boolean belongsToCoachTeam = child.getTeamMemberships().stream()
                .anyMatch(tm -> coachTeamRepository.existsByCoach_EmailAndTeam_TeamId(coachEmail, tm.getTeam().getTeamId()));
        if (!belongsToCoachTeam) throw new AccessDeniedException("Cannot delete this child");


        childRepository.delete(child);
        log.info("Coach {} deleted child {}", coachEmail, childId);
    }


    // ---------------- UTILS ----------------
    private ChildDTO toDTO(Child child) {
        List<TeamDTO> teams = child.getTeamMemberships().stream()
                .map(tm -> tm.getTeam())
                .map(t -> new TeamDTO(t.getTeamId(), t.getName(), t.getDescription()))
                .toList();


        return new ChildDTO(
                child.getChildId(),
                child.getFullName(),
                child.getBirthYear(),
                child.getTeamCategory(),
                child.getNotes(),
                teams
        );
    }


    private ChildResponse toChildResponse(Child child) {
        return new ChildResponse(
                child.getChildId(),
                child.getFullName(),
                child.getBirthYear(),
                child.getTeamCategory(),
                child.getNotes(),
                child.getTeamMemberships().stream()
                        .map(tm -> new TeamDTO(tm.getTeam().getTeamId(), tm.getTeam().getName(), tm.getTeam().getDescription()))
                        .toList()
        );
    }


    private String sanitize(String input) {
        return input != null ? input.replaceAll("<[^>]*>", "") : null;
    }


}