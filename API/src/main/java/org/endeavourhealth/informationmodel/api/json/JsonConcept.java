package org.endeavourhealth.informationmodel.api.json;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonConcept {
    private Integer id = null;
    private String name = null;
    private byte status = 0;
    private String shortName = null;
    private String description = null;
    private Integer clazz = null;
//    private String structureType = null;
//    private Long structureId = null;
//    private Integer count = null;

    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public String getStructureType() {
//        return structureType;
//    }
//
//    public void setStructureType(String structureType) {
//        this.structureType = structureType;
//    }
//
//    public Long getStructureId() {
//        return structureId;
//    }
//
//    public void setStructureId(Long structureId) {
//        this.structureId = structureId;
//    }
//
//    public Integer getCount() {
//        return count;
//    }
//
//    public void setCount(Integer count) {
//        this.count = count;
//    }

    public Integer getClazz() {
        return clazz;
    }

    public void setClazz(Integer clazz) {
        this.clazz = clazz;
    }
}
