package poc.reflexion;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import poc.reflexion.annotation.Traductible;
import poc.reflexion.dto.Pays;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.security.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by martinaware on 23/10/15.
 */
public class TestReflexion {

    Map<String,Pays> paysList;

    @Before
    public void setUp() {
        paysList = new HashMap<String,Pays>(){
            {
                put("FR", new Pays("FR", "Pays_libelle_1", "Pays_description_1"));
                put("US", new Pays("US", "Pays_libelle_2", "Pays_description_2"));
            }};
    }

    @Test
    public void traduireAvecReflexionTest() {
        Pays pays = traduireAvecReflexion(paysList.get("FR"), Locale.FRANCE);
        assertEquals("France", pays.getLibelle());
        assertEquals("Le plus beau des pays de tous les temps.", pays.getDescription());

        pays = traduireAvecReflexion(paysList.get("US"), Locale.FRANCE);
        assertEquals("Etats-Unis", pays.getLibelle());
        assertEquals("Le pays des burgers.", pays.getDescription());

        pays = traduireAvecReflexion(paysList.get("FR"), Locale.US);
        assertEquals("France", pays.getLibelle());
        assertEquals("The country of wine.", pays.getDescription());

        pays = traduireAvecReflexion(paysList.get("US"), Locale.US);
        assertEquals("United States", pays.getLibelle());
        assertEquals("The best country ever.", pays.getDescription());
    }

    @Test
    public void traduireSansReflexionTest() {
        Pays pays = traduireSansReflexion(paysList.get("FR"), Locale.FRANCE);
        assertEquals("France", pays.getLibelle());
        assertEquals("Le plus beau des pays de tous les temps.", pays.getDescription());

        pays = traduireSansReflexion(paysList.get("US"), Locale.FRANCE);
        assertEquals("Etats-Unis", pays.getLibelle());
        assertEquals("Le pays des burgers.", pays.getDescription());

        pays = traduireSansReflexion(paysList.get("FR"), Locale.US);
        assertEquals("France", pays.getLibelle());
        assertEquals("The country of wine.", pays.getDescription());

        pays = traduireSansReflexion(paysList.get("US"), Locale.US);
        assertEquals("United States", pays.getLibelle());
        assertEquals("The best country ever.", pays.getDescription());
    }

    private final int NOMBRE_ESSAIS = 1000000;

    @Test
    public void performanceAvecReflexionTest() {

        LocalDateTime dateDebut = LocalDateTime.now();

        for (int i=0; i<NOMBRE_ESSAIS; i++) {
            Pays pays = traduireAvecReflexion(paysList.get("FR"), Locale.FRANCE);
            assertEquals("France", pays.getLibelle());
            assertEquals("Le plus beau des pays de tous les temps.", pays.getDescription());

            pays = traduireAvecReflexion(paysList.get("US"), Locale.FRANCE);
            assertEquals("Etats-Unis", pays.getLibelle());
            assertEquals("Le pays des burgers.", pays.getDescription());

            pays = traduireAvecReflexion(paysList.get("FR"), Locale.US);
            assertEquals("France", pays.getLibelle());
            assertEquals("The country of wine.", pays.getDescription());

            pays = traduireAvecReflexion(paysList.get("US"), Locale.US);
            assertEquals("United States", pays.getLibelle());
            assertEquals("The best country ever.", pays.getDescription());
        }

        LocalDateTime dateFin = LocalDateTime.now();
        long duree = ChronoUnit.MILLIS.between(dateDebut, dateFin);

        assertTrue("La durée est trop élevée : " + duree + " ms", duree < 0);
    }

    @Test
    public void performanceAvecReflexionOptimiseTest() {

        LocalDateTime dateDebut = LocalDateTime.now();

        for (int i=0; i<NOMBRE_ESSAIS; i++) {
            Pays pays = traduireAvecReflexionOptimise(paysList.get("FR"), Locale.FRANCE);
            assertEquals("France", pays.getLibelle());
            assertEquals("Le plus beau des pays de tous les temps.", pays.getDescription());

            pays = traduireAvecReflexionOptimise(paysList.get("US"), Locale.FRANCE);
            assertEquals("Etats-Unis", pays.getLibelle());
            assertEquals("Le pays des burgers.", pays.getDescription());

            pays = traduireAvecReflexionOptimise(paysList.get("FR"), Locale.US);
            assertEquals("France", pays.getLibelle());
            assertEquals("The country of wine.", pays.getDescription());

            pays = traduireAvecReflexionOptimise(paysList.get("US"), Locale.US);
            assertEquals("United States", pays.getLibelle());
            assertEquals("The best country ever.", pays.getDescription());
        }

        LocalDateTime dateFin = LocalDateTime.now();
        long duree = ChronoUnit.MILLIS.between(dateDebut, dateFin);

        assertTrue("La durée est trop élevée : " + duree + " ms", duree < 0);
    }

    @Test
    public void performanceSansReflexionTest() {

        LocalDateTime dateDebut = LocalDateTime.now();

        for (int i=0; i<NOMBRE_ESSAIS; i++) {
            Pays pays = traduireSansReflexion(paysList.get("FR"), Locale.FRANCE);
            assertEquals("France", pays.getLibelle());
            assertEquals("Le plus beau des pays de tous les temps.", pays.getDescription());

            pays = traduireSansReflexion(paysList.get("US"), Locale.FRANCE);
            assertEquals("Etats-Unis", pays.getLibelle());
            assertEquals("Le pays des burgers.", pays.getDescription());

            pays = traduireSansReflexion(paysList.get("FR"), Locale.US);
            assertEquals("France", pays.getLibelle());
            assertEquals("The country of wine.", pays.getDescription());

            pays = traduireSansReflexion(paysList.get("US"), Locale.US);
            assertEquals("United States", pays.getLibelle());
            assertEquals("The best country ever.", pays.getDescription());
        }

        LocalDateTime dateFin = LocalDateTime.now();
        long duree = ChronoUnit.MILLIS.between(dateDebut, dateFin);

        assertTrue("La durée est trop élevée : " + duree + " ms",duree < 0);
    }

    private Pays traduireAvecReflexion(Pays pays, Locale locale) {
        Pays paysTraduit = new Pays(pays.getCode(), pays.getLibelle(), pays.getDescription());
        Field[] fields = pays.getClass().getDeclaredFields();

        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(Traductible.class);

            if (annotation != null) {
                try {
                    field.setAccessible(true);
                    field.set(paysTraduit, traduirePropriete((String) field.get(pays), locale));
                }
                catch (IllegalAccessException exception) {
                    assertFalse("Le champ à traduire " + field.getName() + " n'est pas accessible",true);
                }
            }
        }
        return paysTraduit;
    }

    private HashMap<Class,ArrayList<Field>> fieldsMap = new HashMap<>();

    private Pays traduireAvecReflexionOptimise(Pays pays, Locale locale) {

        Pays paysTraduit = new Pays(pays.getCode(), pays.getLibelle(), pays.getDescription());
        Field[] fields = pays.getClass().getDeclaredFields();

        if (!fieldsMap.containsKey(Pays.class)){
            fieldsMap.put(Pays.class, listerChampsTraductible(Pays.class));
        }

        for (Field field : fieldsMap.get(Pays.class)) {
            try {
                field.set(paysTraduit, traduirePropriete((String) field.get(pays), locale));
            }
            catch (IllegalAccessException exception) {
                assertFalse("Le champ à traduire " + field.getName() + " n'est pas accessible",true);
            }
        }

        return paysTraduit;
    }

    private ArrayList<Field> listerChampsTraductible(Class classe) {
        ArrayList<Field> champsTraductibles = new ArrayList<>();
        Field[] fields = classe.getDeclaredFields();

        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(Traductible.class);

            if (annotation != null) {
                field.setAccessible(true);
                champsTraductibles.add(field);
            }
        }

        return champsTraductibles;
    }

    private Pays traduireSansReflexion(Pays pays, Locale locale) {
        Pays paysTraduit = new Pays(pays.getCode(), pays.getLibelle(), pays.getDescription());
        paysTraduit.setLibelle(traduirePropriete(pays.getLibelle(), locale));
        paysTraduit.setDescription(traduirePropriete(pays.getDescription(), locale));
        return paysTraduit;
    }

    private String traduirePropriete(String code, Locale locale) {
        switch (locale.toLanguageTag()) {
            case "fr-FR":
            case "fr":
                switch (code){
                    case "Pays_libelle_1" :
                        return "France";
                    case "Pays_libelle_2" :
                        return "Etats-Unis";
                    case "Pays_description_1" :
                        return "Le plus beau des pays de tous les temps.";
                    case "Pays_description_2" :
                        return "Le pays des burgers.";
                    default:
                        return "Traduction manquante : " + code;
                }
            case "en-US":
                switch (code){
                    case "Pays_libelle_1" :
                        return "France";
                    case "Pays_libelle_2" :
                        return "United States";
                    case "Pays_description_1" :
                        return "The country of wine.";
                    case "Pays_description_2" :
                        return "The best country ever.";
                    default:
                        return "Missing translation : " + code;
                }
            default:
                return "Locale manquante : " + locale.toLanguageTag();
        }
    }
}
