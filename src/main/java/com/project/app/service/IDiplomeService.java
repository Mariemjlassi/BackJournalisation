package com.project.app.service;

import java.util.List;
import java.util.Optional;

import com.project.app.model.Diplome;

public interface IDiplomeService {
	
	public Diplome saveDiplome(Long idType, String libelleTypeDiplome, String libelle);
	public Diplome saveOrAssignDiplome(Long employeId, String libelle, Long typeDiplomeId);
	public Diplome updateDiplomeEmploye(Long diplomeId, Long employeId, String newLibelle, Long newTypeDiplomeId);
	public void deleteDiplome(Long diplomeId, Long employeId);
	public List<Diplome> getDiplomesByEmploye(Long employeId);
	public List<Diplome>getAllDiplomes();
	public Optional<Diplome> getDiplomeById(Long id);
	public Diplome updateDiplome(Long id, Long idType, String libelleTypeDiplome, String libelle);
	public void deleteDiplomePermanently(Long id);
}
