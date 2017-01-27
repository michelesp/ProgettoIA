package extraction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Test2 {

	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, IOException, Exception {
		// TODO Auto-generated method stub
		Extractor ex = new Extractor();
		String myTranslation = "The patient coming from the emergency room, already ventilated with Garat(PS18,PEEP5,FiO2 0.6), was hospitalized. Monitorized blood pressure 77/40, heart rate 113 and undetectable SPO2. Ventilated in vacuum and then proceeded to endotracheal intubation after administration of Midazolan 8mg, Fentanest 100γ, Nimbex 10mg. Tube diameter 7,5 uncuffed. Aspire secretion from TET, put in mechancial ventilation VT 450ml, FR 14, FiO2 0.6. Proceeded to Ultrasound-guided cannulation of the right internal jugular vein and asked for chest X-ray";
		ex.buildFrame(myTranslation,null);
	}
}
