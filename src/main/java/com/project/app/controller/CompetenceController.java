package com.project.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.model.Competence;
import com.project.app.service.CompetenceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/competences")
@RequiredArgsConstructor
public class CompetenceController {

    private final CompetenceService competenceService;

    @GetMapping
    public List<Competence> getAllCompetences() {
        return competenceService.getAll();
    }

    @PostMapping
    public ResponseEntity<?> createCompetence(@RequestBody Competence competence) {
        try {
            Competence created = competenceService.create(competence);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCompetence(@PathVariable("id") Long competenceId,
                                              @RequestBody Competence competence) {
        try {
            Competence updated = competenceService.update(competenceId, competence);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompetence(@PathVariable("id") Long competenceId) {
        try {
            competenceService.delete(competenceId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
