package danilo.jovanovic.shoppinglist;

public class Item {
    private String title;
    private boolean shared;
    private String owner;

    public Item(String owner, String title, boolean shared) {
        this.title = title;
        this.shared = shared;
        this.owner = owner;
    }
     public String getTitle(){
        return title;
     }
     public void setTitle(String title){
        this.title = title;
     }

     public boolean getShared(){
        return shared;
     }
     public void setShared(boolean shared){
        this.shared = shared;
     }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
