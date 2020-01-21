package com.ipiecoles.java.java230;

import com.ipiecoles.java.java230.exceptions.BatchException;
import com.ipiecoles.java.java230.model.Commercial;
import com.ipiecoles.java.java230.model.Employe;
import com.ipiecoles.java.java230.model.Manager;
import com.ipiecoles.java.java230.model.Technicien;
import com.ipiecoles.java.java230.repository.EmployeRepository;
import com.ipiecoles.java.java230.repository.ManagerRepository;

import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MyRunner implements CommandLineRunner {

    private static final String REGEX_MATRICULE = "^[MTC][0-9]{5}$";
    private static final String REGEX_NOM = ".*";
    private static final String REGEX_PRENOM = ".*";
    private static final int NB_CHAMPS_MANAGER = 5;
    private static final int NB_CHAMPS_TECHNICIEN = 7;
    private static final int NB_CHAMPS_COMMERCIAL = 7;
    private static final String REGEX_MATRICULE_MANAGER = "^M[0-9]{5}$";
    private static final String REGEX_MATRICULE_COMMERCIAL = "^C[0-9]{5}$";
    private static final String REGEX_MATRICULE_TECHNICIEN = "^T[0-9]{5}$";
    private static final String REGEX_TYPE = "^[MTC]{1}.*";
    
    

    @Autowired
    private EmployeRepository employeRepository;

    @Autowired
    private ManagerRepository managerRepository;

    private List<Employe> employes = new ArrayList<Employe>();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(String... strings) throws Exception {
        String fileName = "employes.csv";
        readFile(fileName);
        //readFile(strings[0]);
    }

    /**
     * Méthode qui lit le fichier CSV en paramètre afin d'intégrer son contenu en BDD
     * @param fileName Le nom du fichier (à mettre dans src/main/resources)
     * @return une liste contenant les employés à insérer en BDD ou null si le fichier n'a pas pu être le
     */
    public List<Employe> readFile(String fileName) throws Exception {
        Stream<String> stream;
        stream = Files.lines(Paths.get(new ClassPathResource(fileName).getURI()));
        //TODO
        Integer i = 0;
        for(String ligne : stream.collect(Collectors.toList())) {
            i++;
            try
            {
                processLine(ligne);
            }
            catch (BatchException e)
            {
                System.out.println("Ligne " + i + " : " + e.getMessage() + " => " + ligne);
            }
        }
        
        System.out.println(employes);
        return employes;
    }

    /**
     * Méthode qui regarde le premier caractère de la ligne et appelle la bonne méthode de création d'employé
     * @param ligne la ligne à analyser
     * @throws BatchException si le type d'employé n'a pas été reconnu
     */
    private void processLine(String ligne) throws BatchException {
        //TODO
    	if(!ligne.matches(REGEX_TYPE)) 
    	{
            throw new BatchException("Type d'employé inconnu : " + ligne.charAt(0));
        }
    	
    	
    	String[] elements = ligne.split(",");
    	
    	
    	if (ligne.charAt(0) == 'M' && elements.length == NB_CHAMPS_MANAGER) 
    	{
        	processManager(elements,ligne);
        } 
    	else if (ligne.charAt(0) == 'C' && elements.length == NB_CHAMPS_COMMERCIAL) 
    	{
        	processCommercial(elements,ligne);
        } 
    	else if (ligne.charAt(0) == 'T' && elements.length == NB_CHAMPS_TECHNICIEN) 
    	{
        	processTechnicien(elements,ligne);
        } 
    	else 
    	{
        	throw new BatchException("Nombre de champs incorrect");
        }
    	

    }
    
    
    
    

    /**
     * Méthode qui crée un Commercial à partir d'une ligne contenant les informations d'un commercial et l'ajoute dans la liste globale des employés
     * @param ligneCommercial la ligne contenant les infos du commercial à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processCommercial(String[] ligneCommercial, String ligneCom) throws BatchException {
        //TODO
    	
    	String date = ligneCommercial[3];
    	
    	if(!ligneCommercial[0].matches(REGEX_MATRICULE_COMMERCIAL)) 
    	{
    		throw new BatchException("Problème avec le matricule du commercial ");
    	} 
    	else 
    	{
    		
    		
    		Commercial nouveauCommercial = new Commercial();
    		/*le matricule ayant été testé juste au dessus, on peut le set directement*/
    		nouveauCommercial.setMatricule(ligneCommercial[0]);
    		
    		try // set nom et prénom
    		{
    			nouveauCommercial.setPrenom(ligneCommercial[2]);
    			nouveauCommercial.setNom(ligneCommercial[1]);
    		}
    		catch (Exception e)
    		{
    			throw new BatchException("Prénom ou nom incohérent");
    		}
    		
    		
    		try //set date d'embauche
    		{
    			nouveauCommercial.setDateEmbauche(DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(date));
    		} 
    		catch(Exception e) 
    		{
    			throw new BatchException("Problème de date");
    		}
    		
    		
    		try //set salaire 
    		{
    			nouveauCommercial.setSalaire(Double.parseDouble(ligneCommercial[4]));
    		}
    		catch (Exception e)
    		{
    			throw new BatchException("Salaire incohérent");
    		}
    		
    		
    		try // set CA Annuel
    		{
    			nouveauCommercial.setCaAnnuel(Double.parseDouble(ligneCommercial[5]));
    		}
    		catch (Exception e)
    		{
    			throw new BatchException("CA annuel incohérent");
    		}
    		
    		
    		try // set performance
    		{
    			nouveauCommercial.setPerformance(Integer.parseInt(ligneCommercial[6]));
    		} 
    		catch (Exception e)
    		{
    			throw new BatchException("Performance incohérente");
    		}
    		
    		employes.add(nouveauCommercial);
    	}
    	
    	
    	
    }
    
    
    
    

    /**
     * Méthode qui crée un Manager à partir d'une ligne contenant les informations d'un manager et l'ajoute dans la liste globale des employés
     * @param ligneManager la ligne contenant les infos du manager à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processManager(String[] ligneManager, String ligneMan) throws BatchException {
        //TODO
    	
    	String date = ligneManager[3];
    	
    	
    	
    	if(!ligneManager[0].matches(REGEX_MATRICULE_MANAGER)) 
    	{
    		throw new BatchException("Problème avec le matricule du manager ");
    	} 
    	else 
    	{
    		
    		Manager nouveauManager = new Manager();
    		nouveauManager.setMatricule(ligneManager[0]);
    		
    		
    		try //set nom et prénom
    		{
    			nouveauManager.setPrenom(ligneManager[2]);
    			nouveauManager.setNom(ligneManager[1]);
    		}
    		catch (Exception e)
    		{
    			throw new BatchException("Prénom ou nom incohérent");
    		}
    		
    		try 
    		{ // set date d'embauche
    			nouveauManager.setDateEmbauche(DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(date));
    		} 
    		catch(Exception e) 
    		{
    			throw new BatchException("Problème de date");
    		}
    		
    		
    		try //set salaire 
    		{
    			nouveauManager.setSalaire(Double.parseDouble(ligneManager[4]));
    		}
    		catch (Exception e)
    		{
    			throw new BatchException("Salaire incohérent");
    		}
    		
    		employes.add(nouveauManager);
    		
    	}
    }
    
    
    
    

    /**
     * Méthode qui crée un Technicien à partir d'une ligne contenant les informations d'un technicien et l'ajoute dans la liste globale des employés
     * @param ligneTechnicien la ligne contenant les infos du technicien à intégrer
     * @throws BatchException s'il y a un problème sur cette ligne
     */
    private void processTechnicien(String[] ligneTechnicien, String ligneTech) throws BatchException {
        //TODO
    	
    	String date = ligneTechnicien[3];
    	
    	if(!ligneTechnicien[0].matches(REGEX_MATRICULE_TECHNICIEN)) 
    	{
    		throw new BatchException("Problème avec le matricule du technicien ");
    	} 
    	else 
    	{
    		
    		
    		Technicien nouveauTechnicien = new Technicien();
    		nouveauTechnicien.setMatricule(ligneTechnicien[0]);
    		
    		
    		try //set nom et prénom
    		{
    			nouveauTechnicien.setPrenom(ligneTechnicien[2]);
    			nouveauTechnicien.setNom(ligneTechnicien[1]);
    		}
    		catch (Exception e)
    		{
    			throw new BatchException("Prénom ou nom incohérent");
    		}
    		
    		
    		try //set date d'embauche
    		{
    			nouveauTechnicien.setDateEmbauche(DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(date));
    		} 
    		catch(Exception e) 
    		{
    			throw new BatchException("Problème de date");
    		}
    		
    		
    		
    		try // set grade
    		{
    			nouveauTechnicien.setGrade(Integer.parseInt(ligneTechnicien[5]));
    		}
    		catch (Exception e)
    		{
    			throw new BatchException("Grade incohérent");
    		}
    		
    		
    		
    		try //set salaire 
    		{
    			nouveauTechnicien.setSalaire(Double.parseDouble(ligneTechnicien[4]));
    		}
    		catch (Exception e)
    		{
    			throw new BatchException("Salaire incohérent");
    		}
    		
    	
    	
    		
    		Manager manager = managerRepository.findByMatricule(ligneTechnicien[6]);
    		
    		if (manager == null) 
    		{
    			throw new BatchException("Manager introuvable");
    		}
    		
    		employes.add(nouveauTechnicien);
    		
    	}
    
    }

}
