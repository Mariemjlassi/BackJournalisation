package com.project.app.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.app.dto.UtilisateurResponseDto;
import com.project.app.model.Administrateur;
import com.project.app.model.Directeur;
import com.project.app.model.JournalAction;
import com.project.app.model.RH;
import com.project.app.model.Responsable;
import com.project.app.model.Utilisateur;
import com.project.app.repository.JournalActionRepository;
import com.project.app.repository.UtilisateurRepository;
import com.project.app.service.AuthServiceImpl;
import com.project.app.service.JournalActionService;

@RestController
@RequestMapping("/utilisateurs")
public class UtilisateurController {
	
	@Autowired
	private JournalActionRepository journalActionRepository;
	
	private final UtilisateurRepository utilisateurRepository;
	private final AuthServiceImpl authServiceImpl;
	private final JournalActionService journalActionService;
	
	@Autowired
    public UtilisateurController(UtilisateurRepository utilisateurRepository, AuthServiceImpl authServiceImpl,JournalActionService journalActionService) {
        this.utilisateurRepository = utilisateurRepository;
        this.authServiceImpl = authServiceImpl;
        this.journalActionService=journalActionService;
    }
	
	@GetMapping
	@PreAuthorize("hasAuthority('PERM_VOIR')") // Ajoutez la permission appropriée
	public ResponseEntity<List<UtilisateurResponseDto>> getAllUtilisateurs(Authentication authentication) {
	    Utilisateur currentUser = utilisateurRepository.findByUsername(authentication.getName())
	            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
	    
	    // Journalisation de l'action de consultation
	    journalActionService.logActionIfNeeded(currentUser, "Consultation", 
	            "Consultation de la liste des utilisateurs");
	    
	    List<UtilisateurResponseDto> utilisateurs = utilisateurRepository.findAll().stream()
	        .map(user -> new UtilisateurResponseDto(
	            user.getId(),
	            user.getNom(),
	            user.getPrenom(),
	            user.getEmail(),
	            user.getUsername(),
	            user.getRole(),
	            user.getPermissions(),
	            user.getLastLogin()
	        ))
	        .collect(Collectors.toList());

	    return ResponseEntity.ok(utilisateurs);
	}
	
    private UtilisateurResponseDto convertToDto(Utilisateur utilisateur) {
        String role = "";
        if (utilisateur instanceof Administrateur) {
            role = "ADMIN";
        } else if (utilisateur instanceof Directeur) {
            role = "DIRECTEUR";
        } else if (utilisateur instanceof RH) {
            role = "RH";
        } else if (utilisateur instanceof Responsable) {
            role = "RESPONSABLE";
        }

        return new UtilisateurResponseDto(utilisateur.getId(), utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getEmail(), utilisateur.getUsername(), role, utilisateur.getPermissions());
    }
    @GetMapping("/responsables")
    public ResponseEntity<List<UtilisateurResponseDto>> getResponsables() {
        // Filtrer les utilisateurs ayant le rôle Responsable
        List<Utilisateur> responsables = utilisateurRepository.findAll().stream()
                .filter(utilisateur -> utilisateur instanceof Responsable)
                .collect(Collectors.toList());

        // Convertir les utilisateurs filtrés en DTO
        List<UtilisateurResponseDto> responsablesDto = responsables.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responsablesDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_SUPPRIMER')")
    public ResponseEntity<?> deleteUtilisateur(@PathVariable("id") Long id, Authentication authentication) {
        Utilisateur currentUser = utilisateurRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
        utilisateurRepository.deleteById(id);
        
        journalActionService.logAction(currentUser, "Suppression", "Suppression de l'utilisateur : "+utilisateur.getNom()+" "+utilisateur.getPrenom());

        return ResponseEntity.ok("Utilisateur supprimé avec succès");
    }

    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERM_EDITER')")
    public ResponseEntity<UtilisateurResponseDto> updateUtilisateur(
            @PathVariable("id") Long id,
            @RequestBody Utilisateur updatedUtilisateur,
            Authentication authentication) {

        Utilisateur currentUser = utilisateurRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        return utilisateurRepository.findById(id)
                .map(utilisateur -> {
                    utilisateur.setNom(updatedUtilisateur.getNom());
                    utilisateur.setPrenom(updatedUtilisateur.getPrenom());
                    utilisateur.setEmail(updatedUtilisateur.getEmail());
                    utilisateur.setUsername(updatedUtilisateur.getUsername());
                    Utilisateur savedUser = utilisateurRepository.save(utilisateur);

                    //Journalisation de l'action MODIFICATION
                    journalActionService.logAction(currentUser, "Modification",
                            "Modification de l'utilisateur : " + utilisateur.getNom()+"."+utilisateur.getPrenom());

                    return ResponseEntity.ok(convertToDto(savedUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<?> resetUserPassword(@PathVariable("id") Long id,
    		Authentication authentication) {
    	
    	Utilisateur currentUser = utilisateurRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
    	
        String newPassword = authServiceImpl.resetPassword(id);
        
        Utilisateur utilisateur = utilisateurRepository.findById(id)
        	    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        
        journalActionService.logAction(currentUser, "Réinitialisation_MDP", 
                "Réinitialisation du mot de passe pour l'utilisateur: " + utilisateur.getUsername());
        
        
        	
        return ResponseEntity.ok(Map.of(
                "message", "Mot de passe réinitialisé avec succès",
                "newPassword", newPassword 
        ));
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Utilisateur>> getAllUsers() {
        return ResponseEntity.ok(utilisateurRepository.findAll());
    }
    
    @GetMapping("/{id}/journal")
    public ResponseEntity<List<JournalAction>> getJournalForUser(@PathVariable("id") Long id) {
        List<JournalAction> actions = journalActionRepository.findByUtilisateurIdOrderByTimestampDesc(id);
        return ResponseEntity.ok(actions);
    }
    
    @GetMapping("/{id}/journal-last-login")
    public ResponseEntity<List<JournalAction>> getActionsSinceLastLogin(@PathVariable("id") Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        List<JournalAction> actions = journalActionRepository
                .findByUtilisateurIdAndTimestampAfterOrderByTimestampDesc(id, utilisateur.getLastLogin());

        return ResponseEntity.ok(actions);
    }


    



}
