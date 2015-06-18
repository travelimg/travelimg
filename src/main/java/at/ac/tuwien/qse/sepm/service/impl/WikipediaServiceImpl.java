package at.ac.tuwien.qse.sepm.service.impl;

import at.ac.tuwien.qse.sepm.entities.Place;
import at.ac.tuwien.qse.sepm.entities.WikiPlaceInfo;
import at.ac.tuwien.qse.sepm.entities.validators.PlaceValidator;
import at.ac.tuwien.qse.sepm.entities.validators.ValidationException;
import at.ac.tuwien.qse.sepm.service.ServiceException;
import at.ac.tuwien.qse.sepm.service.WikipediaService;
import com.hp.hpl.jena.query.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class WikipediaServiceImpl implements WikipediaService {

    private static final Logger LOGGER = LogManager.getLogger(WikipediaServiceImpl.class);

    /**
     * Create and return a WikiPlaceInfo object for the given Place <tt>place</tt>.
     *
     * @param place must contain non-null and non-empty entries for <tt>place.city</tt>
     *              <tt>place.country</tt>
     * @return a new WikiPlaceInfo object for the given Place. The fields may not all be
     *     initialized - sometimes not all information can be found
     * @throws ServiceException if an exception occurs on service layer level
     */
    @Override
    public WikiPlaceInfo getWikiPlaceInfo(Place place) throws ServiceException {

        try {
            PlaceValidator.validate(place);
        } catch (ValidationException ex) {
            LOGGER.error("Failure: Invalid entity {}", place);
            throw new ServiceException("Invalid Place entity", ex);
        }

        //does this place already have a WikiPlaceInfo?
        if (place.getWikiPlaceInfo() != null) {
            LOGGER.info("{} already owns a WikiPlaceInfo: {}", place, place.getWikiPlaceInfo());
            return place.getWikiPlaceInfo();
        }

        String placeLabel = place.getCity();

        String queryString = "PREFIX dbpedia: <http://dbpedia.org/resource/>\n"
                + "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX dbo: <http://dbpedia.org/ontology/>\n"
                + "PREFIX dbpprop: <http://dbpedia.org/property/>\n"
                + "SELECT DISTINCT ?label ?countryLabel ?currency ?comment\n"
                + "?area ?elevation ?population ?utcOffsetC ?utcOffset ?language where {\n"
                + "?place a dbpedia-owl:PopulatedPlace .\n"
                + "{ ?place rdfs:label \"" + placeLabel + "\"@de }\n"
                + "UNION { ?place rdfs:label \"" + placeLabel + "\"@en }\n"
                + "?place rdfs:label ?label ;\n"
                + " rdfs:comment ?comment .\n"
                + "OPTIONAL { ?place dbpprop:utcOffset ?utcOffset }\n"
                + "OPTIONAL { ?place dbpedia-owl:elevation ?elevation }\n"
                + "OPTIONAL { ?place dbpedia-owl:areaTotal ?area }\n"
                + "OPTIONAL { ?place dbpedia-owl:populationTotal ?population }\n"
                + "OPTIONAL { ?place dbpedia-owl:country ?country .\n"
                + "  OPTIONAL { ?country rdfs:label ?countryLabel .\n"
                + "  FILTER (lang(?countryLabel) = 'de') }\n"
                + "  OPTIONAL { ?country dbpedia-owl:currency ?currencyResource.\n"
                + "    ?currencyResource rdfs:label ?currency.\n"
                + "    FILTER (lang(?currency) = 'de') }\n"
                + "  OPTIONAL { ?country dbpprop:utcOffset ?utcOffsetC }\n"
                + "  OPTIONAL { ?country dbpedia-owl:language ?lang.\n"
                + "    ?lang rdfs:label ?language.\n"
                + "    FILTER (lang(?language) = 'de') } }\n"
                + "FILTER (lang(?comment) = 'de' && lang(?label) = 'de') }\n";

        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory
                .sparqlService("http://dbpedia.org/sparql", query);

        ResultSet results = qexec.execSelect();
        List<QuerySolution> solutions = ResultSetFormatter.toList(results);

        if (solutions.isEmpty()) {
            LOGGER.warn("Query returned no results for {}", place);

            place.setWikiPlaceInfo(new WikiPlaceInfo(placeLabel, place.getCountry(),
                    "Keine Information verfügbar.", null, null, null, null, null, null));
            return place.getWikiPlaceInfo();
        }

        String placeName = null;
        String countryName = null;
        String description = null;
        String utcOffset = null;
        String currency = null;
        String languages = null;
        Double elevation = null;
        Double area = null;
        Integer population = null;

        for (QuerySolution solution : solutions) {
            if (placeName == null) {
                placeName = solution.getLiteral("label").getString();
                description = solution.getLiteral("comment").getString();

                if (solution.getLiteral("countryLabel") != null) {
                    countryName = solution.getLiteral("countryLabel").getString();

                    if (solution.getLiteral("currency") != null) {
                        currency = solution.getLiteral("currency").getString();
                    }
                    if (solution.getLiteral("language") != null) {
                        languages = solution.getLiteral("language").getString();
                    }
                } else { //no countryLabel
                    countryName = place.getCountry();
                }
                if (solution.getLiteral("utcOffset") != null) {
                    utcOffset = "GMT" + solution.getLiteral("utcOffset").getString();
                } else if (solution.getLiteral("utcOffsetC") != null) {
                    utcOffset = "GMT" + solution.getLiteral("utcOffsetC").getString();
                }


                if (solution.getLiteral("elevation") != null) {
                    elevation = solution.getLiteral("elevation").getDouble();
                }
                if (solution.getLiteral("area") != null) {
                    area = solution.getLiteral("area").getDouble();
                }
                if (solution.getLiteral("population") != null) {
                    population = solution.getLiteral("population").getInt();
                }
            } else {
                if (solution.getLiteral("language") != null) {
                    if (!languages.contains(solution.getLiteral("language").getString())) {
                        languages += ", " + solution.getLiteral("language").getString();
                    }
                }
            }
        }
        qexec.close();

        WikiPlaceInfo wikiPlaceInfo = new WikiPlaceInfo(placeName, countryName, description,
                elevation, area, population, utcOffset, currency, languages);
        place.setWikiPlaceInfo(wikiPlaceInfo);

        LOGGER.info("Retrieving Wikipedia Info successful: {} was provided with {}", place,
                wikiPlaceInfo);
        return wikiPlaceInfo;
    }
}
