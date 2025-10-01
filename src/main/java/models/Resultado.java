package models;

public class Resultado {
    private int Id;
    private String Set1;
    private String Set2;
    private String Set3;
    private Partido partido;

    public Resultado(int Id, String set1, String set2, String set3, Partido partido) {
        Id = Id;
        Set1 = set1;
        Set2 = set2;
        Set3 = set3;
        this.partido = partido;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        Id = Id;
    }

    public String getSet1() {
        return Set1;
    }

    public void setSet1(String set1) {
        Set1 = set1;
    }

    public String getSet2() {
        return Set2;
    }

    public void setSet2(String set2) {
        Set2 = set2;
    }

    public String getSet3() {
        return Set3;
    }

    public void setSet3(String set3) {
        Set3 = set3;
    }

    public Partido getPartido() {
        return partido;
    }

    public void setPartido(Partido partido) {
        this.partido = partido;
    }
}
