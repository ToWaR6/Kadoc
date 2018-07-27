package weka;

import java.io.File;

import weka.clusterers.HierarchicalClusterer;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class MainWeka {

	public static void main(String[] args) throws Exception {
		
		// load data
		 ArffLoader loader = new ArffLoader();
		 loader.setFile(new File(args[0]+File.separator+"TFIDF"+File.separator+"tfidf.arff"));
		 Instances structure = loader.getStructure();
		
		 String[] options = new String[1];
		 options[0] = "-p"; //print newink format
		 HierarchicalClusterer clusterer = new HierarchicalClusterer(); // default 2 classes (option : "-N 10" to set 10 classes) 
		 clusterer.setOptions(options);			// set the options
		 clusterer.buildClusterer(structure);	// build the clusterer
		 System.out.println("Nb clusters : "+clusterer.getNumClusters());
		
	}

}
