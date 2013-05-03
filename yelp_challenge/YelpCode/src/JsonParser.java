import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonParser {

	HashMap<String, Integer> userMap;
	HashMap<String, Integer> businessMap;
	long [][] ratings;
	double density;
	double averageReviews;

	public static final int NB_USERS = 45981;
	public static final int NB_BUSINESS = 11537;


	public JsonParser()
	{
		userMap = new HashMap<String,Integer>();
		businessMap = new HashMap<String,Integer>();
		ratings = new long [NB_USERS][NB_BUSINESS];
		density = 0;
	}
	
	public void saveMatrice(String fileName) throws FileNotFoundException
	{
		PrintWriter out = new PrintWriter(fileName);
		for (int i = 0; i < ratings.length ; i++)
		{
			for (int j = 0; j < ratings[0].length; j++)
			{
				out.print(ratings[i][j]);
				out.print(";");
			}
			out.println();
		}
		out.close();
	}
	
	public void loadMatrice(String fileName) throws FileNotFoundException
	{
		Scanner in = new Scanner(new File(fileName));
		for (int  i = 0; i < ratings.length ; i++)
		{
			String line = in.nextLine();
			String[] rate = line.split(";");
			for (int j =0 ; j < ratings.length; j++)
			{
				ratings[i][j] = Long.parseLong(rate[j]);
			}
		}
	}
	
	public void buildMatrice(String fileName)
	{
		int countUser = 0;
		int countBusiness = 0;

		try {
			JSONParser parser = new JSONParser();
			Scanner in = new Scanner(new File(fileName));
			while(in.hasNextLine())
			{
				String content = in.nextLine().replace("\\.", "");
				Object obj = parser.parse(content);
				JSONObject jsonObject = (JSONObject) obj;

				// read the json object
				String user_id = (String) jsonObject.get("user_id");
				String business_id = (String) jsonObject.get("business_id");
				Long stars = (Long)jsonObject.get("stars");

				// store the user
				if (!userMap.containsKey(user_id))
				{
					userMap.put(user_id, countUser);
					countUser++;
				}

				// store the businness
				if (!businessMap.containsKey(business_id))
				{
					businessMap.put(business_id, countBusiness);
					countBusiness++;
				}

				// store the rating
				ratings[userMap.get(user_id)][businessMap.get(business_id)] = stars;
			}

			// density of the matrix
			int nbNonZero = 0;
			for (int i = 0 ; i < NB_USERS; i++)
				for (int j = 0; j < NB_BUSINESS ; j++)
					if (ratings[i][j] != 0)
						nbNonZero++;
			
			this.averageReviews = nbNonZero / NB_USERS;
			this.density = (double)nbNonZero / (NB_USERS * NB_BUSINESS);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		catch (FileNotFoundException   e)
		{
			e.printStackTrace();
		}
	} 
	

	
	
	public static void main(String[] argv) throws FileNotFoundException
	{
		JsonParser myParser = new JsonParser();
		myParser.buildMatrice("/Users/sabrinerekik/Yelp/yelp_challenge/yelp_phoenix_academic_dataset/yelp_academic_dataset_review.json");
		myParser.saveMatrice("matrice.txt");
		System.out.println(myParser.density);
		System.out.println(myParser.averageReviews);
	}
}