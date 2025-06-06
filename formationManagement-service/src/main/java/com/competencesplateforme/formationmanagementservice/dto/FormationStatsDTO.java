package com.competencesplateforme.formationmanagementservice.dto;

public class FormationStatsDTO {
    private Integer id;
    private String title;
    private Long completions;

    public FormationStatsDTO() {
    }

    public FormationStatsDTO(Integer id, String title, Long completions) {
        this.id = id;
        this.title = title;
        this.completions = completions;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCompletions() {
        return completions;
    }

    public void setCompletions(Long completions) {
        this.completions = completions;
    }
}