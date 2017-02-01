package protege;


import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

public class ProtegeHandler {
	private final String NS;
	private OntModel model;
	private Writer writer;

	public ProtegeHandler(String source, InputStreamReader ontologySource, Writer writer) {
		this.NS = source + "#";
		this.writer = writer;
		model = ModelFactory.createOntologyModel( OntModelSpec.OWL_DL_MEM );
		model.read(ontologySource, source);
	}

	public void save() {
		model.write(writer, "RDF/XML");
	}

	public void addFrame(Frame frame) {
		OntClass category = model.getOntClass(NS + frame.getCategory());
		OntClass term = model.createClass(NS + frame.getTerm());
		model.add(term, RDFS.subClassOf, category);
		for(Info info : frame.getExtracted_info()){
			//if(model.getOntClass(NS+info.getInfo())==null)
			//	continue;
			try{
				Individual individual = model.createIndividual(NS+info.getInfo()+"Individual", term);
				DatatypeProperty dateTime = model.createDatatypeProperty(NS+"dateTime");
				individual.addProperty(dateTime, model.createTypedLiteral(info.getDate()));
			}catch (Exception e) {
				System.err.println("errore: "+info.getInfo());
				System.err.println(e.getMessage());
			}
		}
		ObjectProperty hasOccurred = model.getObjectProperty(NS+"hasOccured");
		ObjectProperty isType = model.getObjectProperty(NS+"isType");
		ObjectProperty medicCohesion = model.getObjectProperty(NS+"medicCohesion");
		model.add(term, hasOccurred, ""+frame.getRecurrency());
		model.add(term, isType, ""+frame.getType());
		model.add(term, medicCohesion, ""+frame.getMedic_cohesion());
	}
	
	/* -> query per rircercare tutti gli individuals 
		PREFIX owl:<http://www.w3.org/2002/07/owl#>
		PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>
		SELECT ?individual WHERE { ?individual rdf:type owl:NamedIndividual . ?class rdf:type owl:Class . }
	 * 
	 * ->query per ricercare gli individuals di una determinata classe in questo caso person
		 PREFIX uri:<http://www.semanticweb.org/vecch/ontologies/2016/3/standard-entity-representation#> SELECT ?x WHERE { ?x  a uri:person }
	   
	 * **/
	public void querySPARQL(String queryStr, String ontology){
		
		
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
	    model.read(ontology);
		Query query = QueryFactory.create(queryStr) ;
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
		    ResultSet results = qexec.execSelect() ;
		    for ( ; results.hasNext() ; )
		    {
		      QuerySolution soln = results.nextSolution() ;
		      RDFNode x = soln.get("x") ;         // Get a result variable by name.
		      Resource r = soln.getResource("x") ; // Get a result variable - must be a resource
		     // Literal l = soln.getLiteral("x") ;   // Get a result variable - must be a literal
		      System.out.println(x.toString());
		     // System.out.println(l.getString());
		      System.out.println(r.getLocalName());
		    }
		  
		    
		  }
		
	}

}
