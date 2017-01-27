package protege;

import java.io.InputStreamReader;
import java.io.Writer;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDFS;

public class ProtegeHandler {
	private final String NS;
	private OntModel model;
	private Writer writer;
	
	public ProtegeHandler(String source, InputStreamReader ontologySource, Writer writer) {
		this.NS = source + "#";
		this.writer = writer;
		model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_RULE_INF );
		model.read(ontologySource, source);
	}
	
	public void save() {
		model.write(writer, "RDF/XML-ABBREV");
	}
	
	public void addFrame(Frame frame) {
		OntClass category = model.getOntClass(NS + frame.getCategory());
		OntClass term = model.createClass(NS + frame.getTerm());
		model.add(term, RDFS.subClassOf, category);
		for(Info info : frame.getExtracted_info()){
			Individual individual = model.createIndividual(NS+info.getInfo(), term);
			//DatatypeProperty dateTime = model.getDatatypeProperty("dateTime");
			DatatypeProperty dateTime = model.createDatatypeProperty(NS+"dateTime");
			individual.addProperty(dateTime, model.createTypedLiteral(info.getDate()));
		}
		ObjectProperty hasOccurred = model.getObjectProperty(NS+"hasOccured");
		ObjectProperty isType = model.getObjectProperty(NS+"isType");
		ObjectProperty medicCohesion = model.getObjectProperty(NS+"medicCohesion");
		model.add(term, hasOccurred, ""+frame.getRecurrency());
		model.add(term, isType, ""+frame.getType());
		model.add(term, medicCohesion, ""+frame.getMedic_cohesion());
	}
	
}