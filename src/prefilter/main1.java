package prefilter;

public class main1 {

	public static void main(String[] args) {
		
		String pathToInputFile="evaluationcorpus.tsv";
		boolean useMetrics=true; //le frasi del file di input devono essere etichettate!!! (tab Y otab N)
		
		ParallelFilter pf = new ParallelFilter();
		pf.filterSentences(pathToInputFile,useMetrics);
		
		


	}

}
