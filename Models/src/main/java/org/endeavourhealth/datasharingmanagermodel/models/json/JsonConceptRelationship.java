package org.endeavourhealth.datasharingmanagermodel.models.json;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonConceptRelationship {
    private Long id = null;
    private Integer sourceConcept = null;
    private String sourceConceptName = null;
    private String sourceConceptDescription = null;
    private String sourceConceptShortName = null;
    private Integer targetConcept = null;
    private String targetConceptName = null;
    private String targetConceptDescription = null;
    private String targetConceptShortName = null;
    private String targetLabel = null;
    private Integer relationship_order = null;
    private Integer relationship_type = null;
    private String relationshipTypeName = null;
    private String relationshipTypeDescription = null;
    private String relationshipTypeShortName = null;
    private Integer count = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSourceConcept() {
        return sourceConcept;
    }

    public void setSourceConcept(Integer sourceConcept) {
        this.sourceConcept = sourceConcept;
    }

    public String getSourceConceptName() {
        return sourceConceptName;
    }

    public void setSourceConceptName(String sourceConceptName) {
        this.sourceConceptName = sourceConceptName;
    }

    public String getSourceConceptDescription() {
        return sourceConceptDescription;
    }

    public void setSourceConceptDescription(String sourceConceptDescription) {
        this.sourceConceptDescription = sourceConceptDescription;
    }

    public String getSourceConceptShortName() {
        return sourceConceptShortName;
    }

    public void setSourceConceptShortName(String sourceConceptShortName) {
        this.sourceConceptShortName = sourceConceptShortName;
    }

    public Integer getTargetConcept() {
        return targetConcept;
    }

    public void setTargetConcept(Integer targetConcept) {
        this.targetConcept = targetConcept;
    }

    public String getTargetConceptName() {
        return targetConceptName;
    }

    public void setTargetConceptName(String targetConceptName) {
        this.targetConceptName = targetConceptName;
    }

    public String getTargetConceptDescription() {
        return targetConceptDescription;
    }

    public void setTargetConceptDescription(String targetConceptDescription) {
        this.targetConceptDescription = targetConceptDescription;
    }

    public String getTargetConceptShortName() {
        return targetConceptShortName;
    }

    public void setTargetConceptShortName(String targetConceptShortName) {
        this.targetConceptShortName = targetConceptShortName;
    }

    public String getTargetLabel() {
        return targetLabel;
    }

    public void setTargetLabel(String targetLabel) {
        this.targetLabel = targetLabel;
    }

    public Integer getRelationship_order() {
        return relationship_order;
    }

    public void setRelationship_order(Integer relationship_order) {
        this.relationship_order = relationship_order;
    }

    public Integer getRelationship_type() {
        return relationship_type;
    }

    public void setRelationship_type(Integer relationship_type) {
        this.relationship_type = relationship_type;
    }

    public String getRelationshipTypeName() {
        return relationshipTypeName;
    }

    public void setRelationshipTypeName(String relationshipTypeName) {
        this.relationshipTypeName = relationshipTypeName;
    }

    public String getRelationshipTypeDescription() {
        return relationshipTypeDescription;
    }

    public void setRelationshipTypeDescription(String relationshipTypeDescription) {
        this.relationshipTypeDescription = relationshipTypeDescription;
    }

    public String getRelationshipTypeShortName() {
        return relationshipTypeShortName;
    }

    public void setRelationshipTypeShortName(String relationshipTypeShortName) {
        this.relationshipTypeShortName = relationshipTypeShortName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
