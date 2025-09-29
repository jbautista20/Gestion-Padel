package models;

public class Resultado {
    private String Set1;
    private String Set2;
    private String Set3;

    public Resultado(String Set1, String Set2, String Set3) {
        this.Set1 = Set1;
        this.Set2 = Set2;
        this.Set3 = Set3;
    }

    public String getSet1() { return Set1; }
    public void setSet1(String Set1) { this.Set1 = Set1; }

    public String getSet2() { return Set2; }
    public void setSet2(String Set2) { this.Set2 = Set2; }

    public String getSet3() { return Set3; }
    public void setSet3(String Set3) { this.Set3 = Set3; }


}
