package com.project.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.dto.DirectionDTO;
import com.project.app.dto.DirectionRequest;
import com.project.app.dto.SiteRequest;
import com.project.app.model.Direction;
import com.project.app.model.Site;
import com.project.app.service.DirectionService;

@RestController
@RequestMapping("/api/directions")
public class DirectionController {
	
@Autowired
private DirectionService directionservice;


@GetMapping
public List<Direction> getAllDirectionsnonArchivés() {
    return directionservice.getAllDirectionsnonArchivés();
}



@PutMapping("/archiver")
public ResponseEntity<Direction> archiverDirection(@RequestBody DirectionRequest request) {
    try {
        // Utilisation du service pour archiver la direction avec l'ID dans le corps
        Direction direction = directionservice.archiverDirection(request.getId());
        return ResponseEntity.ok(direction);  // Retourner la direction archivée en réponse
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Retour d'une erreur 404 si direction non trouvée
    }
}








@GetMapping("/liste-directions-archivés")
public List<Direction> getAllDirectionsArchivés() {
    return directionservice.getAllDirectionsArchivés();
}

@PutMapping("/desarchiver")
public ResponseEntity<Direction> desarchiverDirection(@RequestBody DirectionRequest request) {
    try {
        // Utilisation du service pour désarchiver la direction avec l'ID dans le corps
        Direction direction = directionservice.desarchiverDirection(request.getId());
        return ResponseEntity.ok(direction);  // Retourner la direction désarchivée en réponse
    } catch (RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // Retour d'une erreur 404 si direction non trouvée
    }
}

@PutMapping
public Direction updateDirection(@RequestBody DirectionDTO directionDTO) {
    if (directionDTO.getId() == null) {
        throw new IllegalArgumentException("L'ID de la direction est requis pour la mise à jour");
    }
    return directionservice.updateDirection(directionDTO);
}


@PostMapping
public Direction createDirectionWithSites(@RequestBody DirectionDTO directionDTO) {
    return directionservice.createDirectionWithSites(directionDTO);
}

// Obtenir les sites associés à une direction
@GetMapping("/{directionId}/sites")
public List<Site> getSitesByDirection(@PathVariable("directionId") Long directionId) {
    return new ArrayList<>(directionservice.getSitesByDirection(directionId));
}
}

