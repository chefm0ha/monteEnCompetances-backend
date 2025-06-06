package com.competencesplateforme.formationmanagementservice.controller;

import com.competencesplateforme.formationmanagementservice.service.FormationStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/formations/stats")
public class FormationStatsController {

    private final FormationStatsService formationStatsService;

    @Autowired
    public FormationStatsController(FormationStatsService formationStatsService) {
        this.formationStatsService = formationStatsService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getFormationsStats() {
        Map<String, Object> stats = formationStatsService.getFormationsStats();
        return ResponseEntity.ok(stats);
    }
}