package service;
 
/**
 * @author Michelangelo Faretta, Matricola 789071
 * 
 */
 
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.ResponseList;
import facebook4j.TestUser;
import facebook4j.auth.AccessToken;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
 
@Path("/")
public class Status {
	
	private static final String TWITTER_CONSUMER_KEY = "OJoqFalqRMMhHZFXZiEqRS8gh";
	private static final String TWITTER_SECRET_KEY = "HoDf6GhnwyEX6u8YFSRYVB07SRBOO41gCHqDrKitymIhWn6ZgC";
	private static final String TWITTER_ACCESS_TOKEN = "721668759329619969-VCPXrp845JJHYyL6BzSrWrMOxqmQdnb";
	private static final String TWITTER_ACCESS_TOKEN_SECRET = "aSEJoowv0hnJNubItE93855UCFtqMViCOWF4onXaaoVdQ";
	
	@POST
    @Path("/sendStatus")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public String sendStatus( @FormParam("message") String name1,
    		@Context HttpServletRequest request) throws ServletException {
		
		System.out.println("logged");
		
//		Facebook facebook = new FacebookFactory().getInstance();
//		
//		facebook.setOAuthAppId(appId, appSecret);
//		facebook.setOAuthPermissions(commaSeparetedPermissions);
//		facebook.setOAuthAccessToken(new AccessToken(accessToken, null));
		
		Facebook facebook = new FacebookFactory().getInstance();
        request.getSession().setAttribute("facebook", facebook);
        
        facebook.setOAuthAppId("1157573580953812", "6a5dcd187c0988817a887a9595554919");
        facebook.setOAuthPermissions("public_profile");
     
        AccessToken accessToken;
        try {
        	accessToken = facebook.getOAuthAppAccessToken();

        } catch (Exception e) {
            throw new ServletException(e);
        }
        
        
        
//        facebook.setOAuthAccessToken(new AccessToken("CAAQczpoYGNQBAC09phucKQN2I0Pn1G8xSLKS0nZCZB53LRKgT7Vebow45SP7iuYC9706BsritcDQ1RlnZBZBz5s18yO8VNkTwdDBUB9iYEXE4oy2mnZC1QufrZCxJaiAxsaUo2pjQmUOwLzNnbvIEYW4SV5RZA02VkZCNlChOixdYZAZAGAOiYF4HsGkQ7HsEv3aB4Q1jEJHPTEwZDZD", null));
        facebook.setOAuthAccessToken(accessToken);
        
        ResponseList<TestUser> testUsers = null;
        
        try {
			testUsers = facebook.getTestUsers("1157573580953812");
		} catch (FacebookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        TestUser testUser1 = testUsers.get(0);
        
        System.out.println(testUsers.size());
       // System.out.println(testUser1.getEmail());
        

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
            .setOAuthConsumerKey(TWITTER_CONSUMER_KEY)
            .setOAuthConsumerSecret(TWITTER_SECRET_KEY)
            .setOAuthAccessToken(TWITTER_ACCESS_TOKEN)
            .setOAuthAccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();
        try {
        	
        	twitter4j.ResponseList<twitter4j.Status> tl = twitter.getHomeTimeline();
        	
//            Query query = new Query("MrEdPanama");
//            QueryResult result;
//            do {
//                result = twitter.search(query);
//                List<twitter4j.Status> tweets = result.getTweets();
                for (twitter4j.Status tweet : tl) {
                    System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
                }
//            } while ((query = result.nextQuery()) != null);
//            System.exit(0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to search tweets: " + te.getMessage());
//            System.exit(-1);
        }
        
        
		return "<html> " + "<title>" + "Hello Jersey" + "</title>"
          + "<body><h1>" + "Hello from helpdesk" + "</body></h1>" + "</html> ";
    }
	
	@POST
	@Path("/crunchifyService")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response crunchifyREST(InputStream incomingData) {
		StringBuilder crunchifyBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				crunchifyBuilder.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
		}
		System.out.println("Data Received: " + crunchifyBuilder.toString());
 
		// return HTTP response 200 in case of success
		return Response.status(200).entity(crunchifyBuilder.toString()).build();
	}
 
	@GET
	@Path("/verify")
	@Produces(MediaType.TEXT_PLAIN)
	public Response verifyRESTService(InputStream incomingData) {
		String result = "TweetBookRESTService Successfully started..";
 
		// return HTTP response 200 in case of success
		return Response.status(200).entity(result).build();
	}
 
}
