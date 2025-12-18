package com.example.fbk_balkan.service;

import com.example.fbk_balkan.dto.*;
import com.example.fbk_balkan.entity.*;
import com.example.fbk_balkan.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChildRepository childRepository;

    @Autowired
    private ParentChildRepository parentChildRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamMembershipRepository teamMembershipRepository;

    @Autowired
    private CoachTeamRepository coachTeamRepository;

    @Transactional
    public DashboardData getDashboardData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DashboardData dashboardData = new DashboardData();

        if (user.getRole() == User.Role.PARENT) {
            // Fetch children for parent
            List<Child> children = childRepository.findByParentId(userId);
            List<ChildDTO> childDTOs = new ArrayList<>();
            Set<Long> coachIds = new HashSet<>();

            for (Child child : children) {
                ChildDTO childDTO = new ChildDTO();
                childDTO.setChildId(child.getChildId());
                childDTO.setFullName(child.getFullName());
                childDTO.setBirthYear(child.getBirthYear());
                childDTO.setTeamCategory(child.getTeamCategory());
                childDTO.setNotes(child.getNotes());

                // Get teams for this child
                List<TeamMembership> memberships = teamMembershipRepository.findByChildId(child.getChildId());
                List<TeamDTO> teamDTOs = new ArrayList<>();

                for (TeamMembership membership : memberships) {
                    Team team = membership.getTeam();
                    TeamDTO teamDTO = new TeamDTO();
                    teamDTO.setTeamId(team.getTeamId());
                    teamDTO.setName(team.getName());
                    teamDTO.setDescription(team.getDescription());

                    // Get coach for this team
                    for (CoachTeam ct : team.getCoachTeams()) {
                        User coach = ct.getCoach();
                        CoachDTO coachDTO = new CoachDTO();
                        coachDTO.setUserId(coach.getUserId());
                        coachDTO.setFullName(coach.getFullName());
                        coachDTO.setEmail(coach.getEmail());
                        coachDTO.setPhone(coach.getPhone());
                        teamDTO.setCoach(coachDTO);
                        coachIds.add(coach.getUserId());
                        break; // Assuming one coach per team
                    }

                    teamDTOs.add(teamDTO);
                }

                childDTO.setTeams(teamDTOs);
                childDTOs.add(childDTO);
            }

            // Get unique coaches
            List<CoachDTO> coachDTOs = new ArrayList<>();
            for (Long coachId : coachIds) {
                User coach = userRepository.findById(coachId).orElse(null);
                if (coach != null) {
                    CoachDTO coachDTO = new CoachDTO();
                    coachDTO.setUserId(coach.getUserId());
                    coachDTO.setFullName(coach.getFullName());
                    coachDTO.setEmail(coach.getEmail());
                    coachDTO.setPhone(coach.getPhone());
                    coachDTOs.add(coachDTO);
                }
            }

            dashboardData.setChildren(childDTOs);
            dashboardData.setCoaches(coachDTOs);

        } else if (user.getRole() == User.Role.COACH) {
            // Fetch teams for coach
            List<Team> teams = teamRepository.findByCoachId(userId);
            List<TeamDTO> teamDTOs = new ArrayList<>();
            Set<Long> childIds = new HashSet<>();

            for (Team team : teams) {
                TeamDTO teamDTO = new TeamDTO();
                teamDTO.setTeamId(team.getTeamId());
                teamDTO.setName(team.getName());
                teamDTO.setDescription(team.getDescription());

                // Set coach info
                CoachDTO coachDTO = new CoachDTO();
                coachDTO.setUserId(user.getUserId());
                coachDTO.setFullName(user.getFullName());
                coachDTO.setEmail(user.getEmail());
                coachDTO.setPhone(user.getPhone());
                teamDTO.setCoach(coachDTO);

                // Get children in this team
                List<TeamMembership> memberships = teamMembershipRepository.findByTeamId(team.getTeamId());
                List<ChildDTO> childDTOs = new ArrayList<>();

                for (TeamMembership membership : memberships) {
                    Child child = membership.getChild();
                    ChildDTO childDTO = new ChildDTO();
                    childDTO.setChildId(child.getChildId());
                    childDTO.setFullName(child.getFullName());
                    childDTO.setBirthYear(child.getBirthYear());
                    childDTO.setTeamCategory(child.getTeamCategory());
                    childDTO.setNotes(child.getNotes());
                    childDTOs.add(childDTO);
                    childIds.add(child.getChildId());
                }

                teamDTO.setChildren(childDTOs);
                teamDTOs.add(teamDTO);
            }

            // Get children with their parents
            List<ChildWithParentDTO> childrenWithParents = new ArrayList<>();
            for (Long childId : childIds) {
                Child child = childRepository.findById(childId).orElse(null);
                if (child != null) {
                    ChildWithParentDTO childWithParentDTO = new ChildWithParentDTO();
                    childWithParentDTO.setChildId(child.getChildId());
                    childWithParentDTO.setFullName(child.getFullName());
                    childWithParentDTO.setBirthYear(child.getBirthYear());
                    childWithParentDTO.setTeamCategory(child.getTeamCategory());
                    childWithParentDTO.setNotes(child.getNotes());

                    // Get parents for this child
                    List<ParentDTO> parentDTOs = new ArrayList<>();

                    for (ParentChild pc : child.getParentChildren()) {
                        User parent = pc.getParent();
                        ParentDTO parentDTO = new ParentDTO();
                        parentDTO.setUserId(parent.getUserId());
                        parentDTO.setFullName(parent.getFullName());
                        parentDTO.setEmail(parent.getEmail());
                        parentDTO.setPhone(parent.getPhone());
                        parentDTOs.add(parentDTO);
                    }

                    childWithParentDTO.setParents(parentDTOs);
                    childrenWithParents.add(childWithParentDTO);
                }
            }

            dashboardData.setTeams(teamDTOs);
            dashboardData.setChildrenWithParents(childrenWithParents);
        }

        return dashboardData;
    }
}

