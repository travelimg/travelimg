package at.ac.tuwien.qse.sepm.service.impl;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class WikipediaInfoTest {

    public static void main(String[] args) {

        String sparqlQueryString1 = "PREFIX dbpedia: <http://dbpedia.org/resource/>" +
                "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "select ?comment ?thumbnail ?elevation ?areaTotal ?populationTotal where {\n" +
            "dbpedia:Gleisdorf rdfs:comment ?comment ;\n" +
            "dbpedia-owl:thumbnail ?thumbnail ;\n" +
            "dbpedia-owl:elevation ?elevation ;\n" +
            "dbpedia-owl:areaTotal ?areaTotal ;\n" +
            "dbpedia-owl:populationTotal ?populationTotal .\n" +
                    "filter(langMatches(lang(?comment),\"de\"))\n" +
            "}";

        Query query = QueryFactory.create(sparqlQueryString1);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

        ResultSet results = qexec.execSelect();
        ResultSetFormatter.out(System.out, results, query);
        System.out.println(results.getResultVars());
        qexec.close();
    }
}
