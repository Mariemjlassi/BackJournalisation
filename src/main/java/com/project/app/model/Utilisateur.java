package com.project.app.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Data;

	@Entity
	@Data
	@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
	public class Utilisateur {
		@Id
		@GeneratedValue(strategy = GenerationType.TABLE)
		private Long id;
		private String username;
		private String nom;
	    private String prenom;
	    private String email;
	    private String password;
	    private String role;
	    
	    private String telephone;
	    @ManyToMany(fetch = FetchType.EAGER)
	    @JoinTable(
	        name = "utilisateur_permissions",
	        joinColumns = @JoinColumn(name = "utilisateur_id"),
	        inverseJoinColumns = @JoinColumn(name = "permission_id")
	    )
	    private Set<Permission> permissions = new HashSet<>();
		
	    @Column(name = "last_login")
	    private LocalDateTime lastLogin;
	    

	    
	
	}
