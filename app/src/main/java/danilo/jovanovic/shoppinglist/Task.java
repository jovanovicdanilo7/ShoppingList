package danilo.jovanovic.shoppinglist;

public class Task {
    private String title;
    private String id;
    private boolean check;
    private String owner;

    public Task(String owner, String id, String title, boolean check){
        this.id = id;
        this.title = title;
        this.check = check;
        this.owner = owner;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCheck() {
        return check;
    }
    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
