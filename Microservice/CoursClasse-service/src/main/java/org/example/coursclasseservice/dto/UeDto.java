package org.example.coursclasseservice.dto;

import java.util.List;

/** Sans Lombok (incompatible Java 21).
 *  Une UE appartient directement a une Classe.
 */
public class UeDto {
    private Long id;
    private String codeUe;
    private String intitule;
    private Long classeId;
    private List<Long> coursIds;

    public UeDto() {}

    public Long getId()              { return id; }
    public String getCodeUe()        { return codeUe; }
    public String getIntitule()      { return intitule; }
    public Long getClasseId()        { return classeId; }
    public List<Long> getCoursIds()  { return coursIds; }

    public void setId(Long id)                  { this.id = id; }
    public void setCodeUe(String codeUe)        { this.codeUe = codeUe; }
    public void setIntitule(String intitule)    { this.intitule = intitule; }
    public void setClasseId(Long classeId)      { this.classeId = classeId; }
    public void setCoursIds(List<Long> coursIds){ this.coursIds = coursIds; }
}