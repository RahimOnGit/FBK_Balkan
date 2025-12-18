package com.example.fbk_balkan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardData {
    // For Parents
    private List<ChildDTO> children;
    private List<CoachDTO> coaches; // Coaches of their children's teams

    // For Coaches
    private List<TeamDTO> teams;
    private List<ChildWithParentDTO> childrenWithParents; // Children in their teams with parent info
}

