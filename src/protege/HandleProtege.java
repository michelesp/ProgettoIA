package protege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.storage.clips.ClipsKnowledgeBaseFactory;

public class HandleProtege {

	private Project p;
	private KnowledgeBase kb;
	private Slot singleSlot1,singleSlot;
	private static int count=0;
	
	public HandleProtege(){
		  p = createNewClipsProject();
		 kb = p.getKnowledgeBase();
	    	 
	    	 kb.createCls("parameter", kb.getRootClses());
	    	 kb.createCls("desease", kb.getRootClses());
	    	 kb.createCls("equipment", kb.getRootClses());
	    	 kb.createCls("therapy", kb.getRootClses());
	    	 kb.createCls("person", kb.getRootClses());
	    	 
	    	 singleSlot = kb.createSlot("info");
	         singleSlot.setValueType(ValueType.STRING);
	         singleSlot.setAllowsMultipleValues(false); 
	    	 
	    	 singleSlot1 = kb.createSlot("date");
	         singleSlot1.setValueType(ValueType.STRING);
	         singleSlot1.setAllowsMultipleValues(true);
	}
	
	
	private static Project createNewClipsProject() {
        Collection errors = new ArrayList();
        ClipsKnowledgeBaseFactory factory = new ClipsKnowledgeBaseFactory();
        Project project = Project.createNewProject(factory, errors);
        handleErrors(errors);

        /* Set the necessary file names. Note that this doesn't really have to
         * be done until we do a save. Nevertheless, it is probably a good
         * idea to do it here unless we just need a temporary project that is
         * never going to get saved.
         */
        project.setProjectFilePath("C:\\Users\\Annalisa\\workspace\\Protege\\prot.pprj");

        return project;
    }
	
	 private static void handleErrors(Collection errors) {
	        Iterator i = errors.iterator();
	        while (i.hasNext()) {
	            System.out.println("Error: " + i.next());
	        }
	        if (!errors.isEmpty()) {
	            System.exit(-1);
	        }
	    }
	 
	 private static void saveProject(Project p) {
	        Collection errors = new ArrayList();
	        p.save(errors);
	        handleErrors(errors);
	    }
	 
	 public void populate(Frame f){
	    	
    	
    	 
    	
    	 Cls cat=kb.getCls(f.getCategory());
    	
    	 Cls cls = kb.createCls(f.getTerm(), Collections.singleton(cat));	//creo una sottoclasse della categoria del frame
    	 
    	/* Slot singleSlot = kb.createSlot("info");
         singleSlot.setValueType(ValueType.STRING);
         singleSlot.setAllowsMultipleValues(false); */
         cls.addDirectTemplateSlot(singleSlot); 
         
        /* Slot singleSlot1 = kb.createSlot("date");
         singleSlot1.setValueType(ValueType.STRING);
         singleSlot1.setAllowsMultipleValues(true); */
         cls.addDirectTemplateSlot(singleSlot1);
         
    	 for(int i=0;i<f.getExtracted_info().size();i++){
    		 Instance instance=null;
    		try{
    		  instance = kb.createInstance("instance"+count++, cls);
    		  instance.setOwnSlotValue(singleSlot1, f.getExtracted_info().get(i).getDate());
    		  instance.setOwnSlotValue(singleSlot, f.getExtracted_info().get(i).getInfo());
    		}catch(Exception e){
    			kb.getInstance(f.getExtracted_info().get(i).getInfo()).addOwnSlotValue(singleSlot1,f.getExtracted_info().get(i).getDate() );
    			
    		}
    		 //instance.setOwnSlotValue(singleSlot, f.getExtracted_info().get(i).getInfo());
    		
    	 }
    	 
    	
         //Instance instance = kb.createInstance(f.getMedic_cohesion(), cls);
		 
        // instance = kb.createInstance(""+f.getRecurrency(), cls);
		 saveProject(p);
		 
    }
}
