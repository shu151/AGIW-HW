package prefilter;


public class ScoreGetter {
	private double threashold=1.4;

	private String[]listId={"such as","including","i\\.e\\.","like","both","for example","e\\.g\\."};

	private String[]numbersSymbol={" 1 "," 2 "," 3 "," 4 "," 5 "," 6 "," 7 "," 8 "," 9 "," 10 "};
	private String[]numberLiterals={"one","two","three","four","five","six","seven","eight","nine","ten"};

	private String[]listQuantifiers={"some","many","a lot","a lots","different","any","plenty","few"};

	private String entityRegex=".*\\[\\[[a-zA-Z0-9_()/,\\.&-]{0,70}\\|m.[a-zA-Z0-9_]*\\]\\].*";
	private String notEntityRegex="[a-zA-Z0-9_]{0,20}";
	
	//secondo me qui una almeno deve essere entià
	private String listThreeElementsRegex="((("+entityRegex+"|"+notEntityRegex+") ){1,5}, )+(("+entityRegex+"|"+notEntityRegex+") ){1,5}and( ("+entityRegex+"|"+notEntityRegex+")){1,5}";

	//secondo me qui una almeno deve essere entià
	private String containsColonFollowedByListRegex=".*: ((("+entityRegex+"|"+notEntityRegex+") ){1,5}, )*(("+entityRegex+"|"+notEntityRegex+") ){1,5}and( ("+entityRegex+"|"+notEntityRegex+")){1,5}.*";

	private String listThreeElementsRegex1=".*: ((("+entityRegex+"|"+notEntityRegex+") ){1,5}, )*(("+entityRegex+"|"+notEntityRegex+") ){1,5}and( ("+entityRegex+"|"+notEntityRegex+")){1,5}.*";
	private String listThreeElementsRegex2=".*: ((("+entityRegex+"|"+notEntityRegex+") ){1,5}, )*(("+entityRegex+") ){1,5}and( ("+entityRegex+"|"+notEntityRegex+")){1,5}.*";
	private String listThreeElementsRegex3=".*: ((("+entityRegex+"|"+notEntityRegex+") ){1,5}, )*(("+entityRegex+"|"+notEntityRegex+") ){1,5}and( ("+entityRegex+")){1,5}.*";



	public double getScore(String phrase){
/*		
		System.out.println(this.containsColonFollowedByListPattern(phrase)+" colonFollowedByListPatern");
		System.out.println(this.containsColon(phrase)+" containsColon");
		System.out.println(this.containsListIdFollwedByEntity(phrase)+" containsListIdFollowedEntity");
		System.out.println(this.containsListOfThreeElementsPattern(phrase)+" containsListOfThreeElementsPattern");
		System.out.println(this.containsQuantifiers(phrase)+" containsQuantifiers");
		System.out.println(this.containsNumberCommaPattern(phrase)+" containsNumberCommaPattern");
		System.out.println(this.getEntityCount(phrase)+" getEntityCount");
		System.out.println(this.getCommasCount(phrase)+" getCommasCount"); */
		 
		double globalVal=0;

		if(phrase.matches(this.entityRegex+this.entityRegex)){
			globalVal+=this.containsColonFollowedByListPattern(phrase)*(0.8);
			globalVal+=this.containsListIdFollwedByEntity(phrase)*(0.8); //tiene conto di quella che matcha, quindi devono essercene almeno 2 complessivamente

			if(globalVal>=threashold)
				return globalVal;

			globalVal+=this.containsQuantifiers(phrase)*(0.4);
			if(globalVal>=threashold)
				return globalVal;

			globalVal+=this.containsColon(phrase)*(0.4);
			if(globalVal>=threashold)
				return globalVal;

			globalVal+=this.containsNumberCommaPattern(phrase)*(0.4);
			if(globalVal>=threashold)
				return globalVal;

			globalVal+=this.getEntityCount(phrase)*(0.1);
			if(globalVal>=threashold)
				return globalVal;

			globalVal+=this.getCommasCount(phrase)*(0.05);
			if(globalVal>=threashold)
				return globalVal;

			globalVal+=this.containsListOfThreeElementsPattern(phrase)*(0.8);

		}


		return globalVal;
	}

	public int containsColonFollowedByListPattern(String phrase){
		//TODO 		String phrase="Besides rowing  , [[Clube_de_Regatas_do_Flamengo|m.019lty]] also plays an active role in several [[Summer_Olympics|m.06vvk]] sports , such as : Artistic gymnastica , athletics  , basketball  , judo , swimming  , volleyball and water polo .";

		if(phrase.matches(containsColonFollowedByListRegex)&&phrase.matches(".* :"+this.entityRegex))
			return 1;
		else 
			return 0;
	}

	public int containsColon(String phrase){
		for(int i=0;i<phrase.length();i++){
			if(phrase.charAt(i)==':')
				return 1;
		}
		return 0;
	}

	public int containsListIdFollwedByEntity(String phrase) {
		String listIdRegex;

		for(String s :listId){
			listIdRegex=".*"+s+this.entityRegex;
			if(phrase.matches(listIdRegex))
				return 1;
		}
		return 0;	
	}

	public int containsListOfThreeElementsPattern(String phrase){


		if(phrase.matches(this.listThreeElementsRegex1)||
				phrase.matches(this.listThreeElementsRegex2)||
				phrase.matches(this.listThreeElementsRegex3)){
			return 1;}
		else
			return 0;

	}

	public int containsQuantifiers(String phrase){
		for(String quantifiers:this.listQuantifiers){
			if (phrase.contains(quantifiers))
				return 1;
		}
		return 0;
	}

	public int containsNumberCommaPattern(String phrase){

		//TODO
		for(int i=0;i<10;i++){
			if (phrase.contains(" "+this.numberLiterals[i]+" ")||phrase.contains(this.numbersSymbol[i])){
				if(this.getCommasCount(phrase)>=(i-1)) //la numerazione parte da 0
					return 1;

			}

		}
		return 0;
	}

	public int getEntityCount(String phrase){
		int counter=0;
		for(int i=0;i<phrase.length();i++){
			if(phrase.charAt(i)=='[')
				counter++;
		}
		return counter/2;

	}

	public int getCommasCount(String phrase){
		int counter=0;
		for(int i=0;i<phrase.length();i++){
			if(phrase.charAt(i)==',')
				counter++;
		}
		return counter;
	}

	public double getThreashold() {
		return threashold;
	}

	public void setThreashold(double threashold) {
		this.threashold = threashold;
	}

}