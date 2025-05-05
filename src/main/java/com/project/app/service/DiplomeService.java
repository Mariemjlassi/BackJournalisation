package com.project.app.service;

import com.project.app.model.Diplome;
import com.project.app.model.Employe;
import com.project.app.model.TypeDiplome;
import com.project.app.repository.DiplomeRepository;
import com.project.app.repository.EmployeRepository;
import com.project.app.repository.TypeDiplomeRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiplomeService implements IDiplomeService{
    
	@Autowired
    private DiplomeRepository diplomeRepository;

    @Autowired
    private TypeDiplomeRepository typeDiplomeRepository;

    @Autowired
    private EmployeRepository employeRepository;

    @Transactional
    public Diplome saveOrAssignDiplome(Long employeId, String libelle, Long typeDiplomeId, LocalDate dateObtention) {
        Employe employe = employeRepository.findById(employeId)
            .orElseThrow(() -> new RuntimeException("Employé non trouvé"));

        TypeDiplome typeDiplome = typeDiplomeRepository.findById(typeDiplomeId)
            .orElseThrow(() -> new RuntimeException("Type de diplôme non trouvé"));

        System.out.println("Type de diplôme trouvé : " + typeDiplome.getLibelleTypeDiplome()); // Ajoutez ce log

        Optional<Diplome> existingDiplome = diplomeRepository.findByLibelleAndTypeDiplome(libelle, typeDiplome);

        if (existingDiplome.isPresent()) {
            Diplome diplome = existingDiplome.get();
            diplome.setEmploye(employe);
            diplome.setLibelle(libelle);
            diplome.setDateObtention(dateObtention);
            return diplomeRepository.save(diplome);
        } else {
            Diplome newDiplome = new Diplome();
            newDiplome.setLibelle(libelle);
            newDiplome.setEmploye(employe);
            newDiplome.setTypeDiplome(typeDiplome);
            newDiplome.setDateObtention(dateObtention);
            return diplomeRepository.save(newDiplome);
        }
    }

    public List<Diplome> getDiplomesByEmploye(Long employeId) {
    	return diplomeRepository.findByEmployeId(employeId).stream()
    			.map(diplome -> {
    			    System.out.println("Diplôme récupéré : " + diplome.getLibelle() + ", Type: " + diplome.getTypeDiplome());
    			    return diplome;
    			}).collect(Collectors.toList());

    }
    
    @Transactional
    public Diplome updateDiplomeEmploye(Long diplomeId, Long employeId, String newLibelle, Long newTypeDiplomeId, LocalDate dateObtention) {
        Diplome diplome = diplomeRepository.findById(diplomeId)
            .orElseThrow(() -> new RuntimeException("Diplôme non trouvé"));

        if (!diplome.getEmploye().getId().equals(employeId)) {
            throw new RuntimeException("Ce diplôme ne correspond pas à cet employé");
        }

        TypeDiplome typeDiplome = typeDiplomeRepository.findById(newTypeDiplomeId)
            .orElseThrow(() -> new RuntimeException("Type de diplôme non trouvé"));

        diplome.setLibelle(newLibelle);
        diplome.setTypeDiplome(typeDiplome);
        diplome.setDateObtention(dateObtention);
        return diplomeRepository.save(diplome);
    }
    
    @Override
    @Transactional
    public void deleteDiplome(Long diplomeId, Long employeId) {
        Diplome diplome = diplomeRepository.findById(diplomeId)
            .orElseThrow(() -> new RuntimeException("Diplôme non trouvé"));

        if (!diplome.getEmploye().getId().equals(employeId)) {
            throw new RuntimeException("Ce diplôme ne correspond pas à cet employé");
        }

        diplomeRepository.delete(diplome);
    }

	@Override
	@Transactional
    public Diplome saveDiplome(Long idType, String libelleTypeDiplome, String libelle) {
        Optional<TypeDiplome> typeDiplomeOpt = typeDiplomeRepository.findById(idType);

        TypeDiplome typeDiplome = typeDiplomeOpt.orElseGet(() -> {
            Optional<TypeDiplome> existingType = typeDiplomeRepository.findByLibelleTypeDiplome(libelleTypeDiplome);
            return existingType.orElseGet(() -> {
                TypeDiplome newType = new TypeDiplome();
                newType.setLibelleTypeDiplome(libelleTypeDiplome);
                return typeDiplomeRepository.save(newType);
            });
        });

        Diplome diplome = new Diplome();
        diplome.setLibelle(libelle);
        diplome.setTypeDiplome(typeDiplome);
        return diplomeRepository.save(diplome);
    }

	@Override
	public List<Diplome> getAllDiplomes() {
		
		return diplomeRepository.findAll();
	}

	@Override
	public Optional<Diplome> getDiplomeById(Long id) {
        return diplomeRepository.findById(id);
    }

	@Override
	@Transactional
    public Diplome updateDiplome(Long id, Long idType, String libelleTypeDiplome, String libelle) {
        Diplome diplome = diplomeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Diplôme non trouvé avec l'ID : " + id));

        TypeDiplome typeDiplome = typeDiplomeRepository.findById(idType)
            .orElseGet(() -> {
                Optional<TypeDiplome> existingType = typeDiplomeRepository.findByLibelleTypeDiplome(libelleTypeDiplome);
                return existingType.orElseGet(() -> {
                    TypeDiplome newType = new TypeDiplome();
                    newType.setLibelleTypeDiplome(libelleTypeDiplome);
                    return typeDiplomeRepository.save(newType);
                });
            });

        diplome.setLibelle(libelle);
        diplome.setTypeDiplome(typeDiplome);
        return diplomeRepository.save(diplome);
    }
	
	@Override
	@Transactional
	public void deleteDiplomePermanently(Long id) {
	    diplomeRepository.deleteById(id);
	}


}
