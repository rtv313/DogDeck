package Models;

public class DogData {
    public String name = "name";
    public String origin = "origin";
    public String height = "height";
    public String weight = "weight";
    public String lifeSpan = "life_span";
    public String temperament = "temperament";
    public String health = "health";

    public DogData(String name,String origin,String height,String weight,String lifeSpan,String temperament,String health){
        this.name = name;
        this.origin = origin;
        this.height = height;
        this.weight = weight;
        this.lifeSpan = lifeSpan;
        this.temperament = temperament;
        this.health = health;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLifeSpan() {
        return lifeSpan;
    }

    public void setLifeSpan(String lifeSpan) {
        this.lifeSpan = lifeSpan;
    }

    public String getTemperament() {
        return temperament;
    }

    public void setTemperament(String temperament) {
        this.temperament = temperament;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }
}
