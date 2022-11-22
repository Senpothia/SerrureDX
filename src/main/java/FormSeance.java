/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Michel
 */
public class FormSeance {

    private Integer id;
    private String Description;
    private String date;
    private String type1;
    private String type2;
    private String type3;
    private Boolean actif1 = false;
    private Boolean actif2 = false;
    private Boolean actif3 = false;
    private Long compteur1 = 0L;
    private Long compteur2 = 0L;
    private Long compteur3 = 0L;

    public FormSeance() {
        super();
        // TODO Auto-generated constructor stub
    }

    public FormSeance(Integer id, String description, String date, String type1, String type2, String type3,
            Boolean actif1, Boolean actif2, Boolean actif3, Long compteur1, Long compteur2, Long compteur3) {
        super();
        this.id = id;
        Description = description;
        this.date = date;
        this.type1 = type1;
        this.type2 = type2;
        this.type3 = type3;
        this.actif1 = actif1;
        this.actif2 = actif2;
        this.actif3 = actif3;
        this.compteur1 = compteur1;
        this.compteur2 = compteur2;
        this.compteur3 = compteur3;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public String getType3() {
        return type3;
    }

    public void setType3(String type3) {
        this.type3 = type3;
    }

    public Boolean getActif1() {
        return actif1;
    }

    public void setActif1(Boolean actif1) {
        this.actif1 = actif1;
    }

    public Boolean getActif2() {
        return actif2;
    }

    public void setActif2(Boolean actif2) {
        this.actif2 = actif2;
    }

    public Boolean getActif3() {
        return actif3;
    }

    public void setActif3(Boolean actif3) {
        this.actif3 = actif3;
    }

    public Long getCompteur1() {
        return compteur1;
    }

    public void setCompteur1(Long compteur1) {
        this.compteur1 = compteur1;
    }

    public Long getCompteur2() {
        return compteur2;
    }

    public void setCompteur2(Long compteur2) {
        this.compteur2 = compteur2;
    }

    public Long getCompteur3() {
        return compteur3;
    }

    public void setCompteur3(Long compteur3) {
        this.compteur3 = compteur3;
    }

    @Override
    public String toString() {
        return "FormSeance{" + "id=" + id + ", Description=" + Description + ", date=" + date + ", type1=" + type1 + ", type2=" + type2 + ", type3=" + type3 + ", actif1=" + actif1 + ", actif2=" + actif2 + ", actif3=" + actif3 + ", compteur1=" + compteur1 + ", compteur2=" + compteur2 + ", compteur3=" + compteur3 + '}';
    }

}
