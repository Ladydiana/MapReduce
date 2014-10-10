
import java.text.DecimalFormat;

public class Index{
    public String word;
    public int number_of_apps;
    public float frequency;
    
    public Index(String word, int nr){
        this.word = word;
        this.number_of_apps = nr;
        this.frequency = 0;
    }

   
    
    @Override
    public String toString(){
        DecimalFormat decimal = new DecimalFormat("#.###");
        return ""+decimal.format(this.frequency);
    }
}
