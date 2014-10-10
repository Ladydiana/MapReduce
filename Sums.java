import java.text.DecimalFormat;

public class Sums implements Comparable{
    public String docname;
    protected float sum;
    
    public Sums(String docname, float sum){
        this.docname = docname;
        this.sum = sum;
    }
    
    @Override
    public int compareTo(Object o) {
        Sums to = (Sums) o;
        
        if(to.sum > this.sum){
            return 1;
        } else {
            if(this.sum > to.sum){
                return -1;
            } else {
                return 0;
            }
        }
    }
    
    
     @Override
    public String toString(){
        DecimalFormat decimal = new DecimalFormat("#.###");
        return ""+decimal.format(this.sum);
    }
}
