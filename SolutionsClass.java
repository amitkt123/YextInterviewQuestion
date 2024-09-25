import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class SolutionsClass {
    public static final String PLAYERS = "https://codinginterview.yextengtest.com/api/8371408855101014003/players";
    public static final String BASE_URL ="https://codinginterview.yextengtest.com/api/8371408855101014003";
    public static final String GAME = "https://codinginterview.yextengtest.com/api/8371408855101014003/games";
    static HashMap<Integer, String> PlayersMap = new HashMap<>();
    static HashMap<Integer, String> GamesMap = new HashMap<>();

    public static String GetResponse(String urlString) throws MalformedURLException {
        URL url = new URL(urlString);
        HttpURLConnection conn;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("Content-Type", "application/json");
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Error reading data with status: " + status);
            }
            
            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            conn.disconnect();
            
            // Return the full response
            return sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return " ";
    }
    public static void writeToJsonFile(String jsonString, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName); PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.write(jsonString);
            System.out.println("JSON data written to " + fileName);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public static LeaderBoard CreateLeaderBoard(int gameId) throws MalformedURLException {
        //get the scores for all the players and sort
        HashMap<Integer, Integer> scoresMap = new HashMap<>();

        for(Map.Entry<Integer, String> obj : PlayersMap.entrySet()) {
            int playerId = obj.getKey();
            String scoresUrl = BASE_URL + "/players/" + playerId + "/scores";
            //fetch score for the given player Id
            String response = GetResponse(scoresUrl);
            JSONObject responseObject = new JSONObject(response);
            JSONArray responseArray = responseObject.getJSONArray("scores");
            int highestScore = Integer.MIN_VALUE;
            for(Object objs : responseArray){
                JSONObject jsonObject = (JSONObject) objs;
                if(jsonObject.get("gameId").equals(gameId)) {
                    highestScore = Math.max((int) jsonObject.get("score"), highestScore);
                }
            }
            scoresMap.put(playerId, highestScore);
        }
        LeaderBoard leaderBoard = new LeaderBoard();

        leaderBoard.setNames(scoresMap);
        leaderBoard.setGameName(GamesMap.get(gameId));
        return leaderBoard;

    }
     public static void main(String[] args) throws MalformedURLException     {
        try{
             String players = GetResponse(PLAYERS);
             // Pretty-print the JSON response
             JSONObject json = new JSONObject(players);
             JSONArray playersArray = json.getJSONArray("players");
             for(Object obj : playersArray) {
                JSONObject player = (JSONObject) obj;
                int id = (int) player.get("id");
                String name = (String) player.get("name");
                PlayersMap.put(id,name);
             }

             String games = GetResponse(BASE_URL + "/games");
             JSONObject gamesList = new JSONObject(games);
             JSONArray gameList = gamesList.getJSONArray("games");
             for(Object obj : gameList){
                 JSONObject game = (JSONObject) obj;
                 int id = (int)game.get("id");
                 String gameName = (String) game.get("name");

                 GamesMap.put(id, gameName);
             }
        }catch(MalformedURLException ex){
            System.out.println("invalid url");
        }

         LeaderBoard leaderBoard = CreateLeaderBoard(2);
         System.out.println(leaderBoard.getGameName());
         Map<Integer, Integer> sortedByValueMap = leaderBoard.getNames().entrySet()
                 .stream()
                 .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                 .limit(10)
                 .collect(Collectors.toMap(
                         Map.Entry::getKey,
                         Map.Entry::getValue,
                         (value1, value2) -> {
                             throw new IllegalStateException("Duplicate keys not expected.");
                         },
                         LinkedHashMap::new
                 ));

         for(Map.Entry<Integer, Integer> obj : sortedByValueMap.entrySet()){
             System.out.println(PlayersMap.get(obj.getKey()) + " "+ obj.getValue());
         }


    }
}
