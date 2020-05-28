package Models;

public class Dog {
    public int id;
    public String breedOne;
    public String breedTwo;
    public String breedThree;
    public String percentageBreedOne;
    public String percentageBreedTwo;
    public String percentageThree ;
    public String selectedBreedStr;
    public int selectedBreed;
    public String uriImage;

    public Dog(int id,String breedOne,String breedTwo,String breedThree,String percentageBreedOne,
               String percentageBreedTwo, String percentageBreedThree,String uriImage,
               String selectedBreedStr){

        this.id = id;
        this.breedOne = breedOne;
        this.breedTwo = breedTwo;
        this.breedThree = breedThree;
        this.percentageBreedOne = percentageBreedOne;
        this.percentageBreedTwo = percentageBreedTwo;
        this.percentageThree = percentageBreedThree;
        this.uriImage = uriImage;
        this.selectedBreedStr = selectedBreedStr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBreedOne() {
        return breedOne;
    }

    public void setBreedOne(String breedOne) {
        this.breedOne = breedOne;
    }

    public String getBreedTwo() {
        return breedTwo;
    }

    public void setBreedTwo(String breedTwo) {
        this.breedTwo = breedTwo;
    }

    public String getBreedThree() {
        return breedThree;
    }

    public void setBreedThree(String breedThree) {
        this.breedThree = breedThree;
    }

    public String getPercentageBreedOne() {
        return percentageBreedOne;
    }

    public void setPercentageBreedOne(String percentageBreedOne) {
        this.percentageBreedOne = percentageBreedOne;
    }

    public String getPercentageBreedTwo() {
        return percentageBreedTwo;
    }

    public void setPercentageBreedTwo(String percentageBreedTwo) {
        this.percentageBreedTwo = percentageBreedTwo;
    }

    public String getPercentageThree() {
        return percentageThree;
    }

    public void setPercentageThree(String percentageThree) {
        this.percentageThree = percentageThree;
    }

    public String getSelectedBreedStr() {
        return selectedBreedStr;
    }

    public void setSelectedBreedStr(String selectedBreedStr) {
        this.selectedBreedStr = selectedBreedStr;
    }

    public int getSelectedBreed() {
        return selectedBreed;
    }

    public void setSelectedBreed(int selectedBreed) {
        this.selectedBreed = selectedBreed;
    }

    public String getUriImage() {
        return uriImage;
    }

    public void setUriImage(String uriImage) {
        this.uriImage = uriImage;
    }




}
