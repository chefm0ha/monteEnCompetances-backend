package com.competencesplateforme.collaborateurmanagementservice.controller;

import com.competencesplateforme.collaborateurmanagementservice.service.CollaborateurStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/collaborateurs/stats")
public class CollaborateurStatsController {

    private final CollaborateurStatsService collaborateurStatsService;

    @Autowired
    public CollaborateurStatsController(CollaborateurStatsService collaborateurStatsService) {
        this.collaborateurStatsService = collaborateurStatsService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCollaborateursStats() {
        Map<String, Object> stats = collaborateurStatsService.getCollaborateursStats();
        return ResponseEntity.ok(stats);
    }
}