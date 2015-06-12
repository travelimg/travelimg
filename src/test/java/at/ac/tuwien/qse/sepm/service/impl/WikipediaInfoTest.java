package at.ac.tuwien.qse.sepm.service.impl;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.util.List;

public class WikipediaInfoTest {

    public static void main(String[] args) {

        String sparqlQueryString1 = "PREFIX dbpedia: <http://dbpedia.org/resource/>" +
                "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "select ?comment ?thumbnail ?elevation ?areaTotal ?state ?populationTotal ?utcOffset where {\n" +
                "dbpedia:Vienna rdfs:comment ?comment ;\n" +
                "dbpedia-owl:thumbnail ?thumbnail ;\n" +
                "dbpedia-owl:elevation ?elevation ;\n" +
                "dbpedia-owl:areaTotal ?areaTotal ;\n" +
                "dbpedia-owl:populationTotal ?populationTotal .\n" +
                "OPTIONAL {\n" +
                "dbpedia:Vienna dbpedia-owl:state ?state" +
                "}\n" +
                "OPTIONAL {\n" +
                "dbpedia:Vienna dbpedia-owl:utcOffset ?utcOffset" +
                "}\n" +
                "filter(langMatches(lang(?comment),\"de\"))\n" +
                "}";

        Query query = QueryFactory.create(sparqlQueryString1);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

        ResultSet results = qexec.execSelect();
        List<QuerySolution> values = ResultSetFormatter.toList(results);
        for (QuerySolution value : values) {
            System.out.println("chia: " + value.getLiteral("comment").getString());
            System.out.println("Total area: " + value.getLiteral("areaTotal").getDouble() / 1e6 + "km²");
            System.out.println("Bundesland: " + value.getLiteral("utcOffset").getString());
        }
        System.out.println(results.getResultVars());
        qexec.close();
    }
}
