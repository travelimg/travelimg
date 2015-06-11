package at.ac.tuwien.qse.sepm.service.impl;

import com.hp.hpl.jena.query.*;

public class WikipediaInfoTest {

    public static void main(String[] args) {

        String sparqlQueryString1 = "PREFIX dbpedia: <http://dbpedia.org/resource/>" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
                "select ?abstract where { " +
            "dbpedia:Gleisdorf dbpedia-owl:abstract ?abstract ;" +
                    "filter(langMatches(lang(?abstract),\"de\"))" +
        "}";

        Query query = QueryFactory.create(sparqlQueryString1);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query);

        ResultSet results = qexec.execSelect();
        ResultSetFormatter.out(System.out, results, query);

        qexec.close();
    }
}
