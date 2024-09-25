import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//created this class. was not a part of the question originally.
public class  LeaderBoard{
    private String gameName;
    private HashMap<Integer, Integer> names;

    LeaderBoard(){}
    LeaderBoard(String gameName, HashMap<Integer, Integer>  names){
        this.gameName = gameName;
        this.names = names;
    }
    public void setGameName(String gameName){
        this.gameName = gameName;
    }
    public void setNames(HashMap<Integer, Integer>  names){
        this.names = names;
    }
    public String getGameName(){
        return gameName;
    }
    public HashMap<Integer, Integer>  getNames() {
        return names;
    }

}