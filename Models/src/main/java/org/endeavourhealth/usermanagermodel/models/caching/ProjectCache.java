package org.endeavourhealth.usermanagermodel.models.caching;

import org.endeavourhealth.datasharingmanagermodel.models.database.ProjectEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectCache {

    private static Map<String, ProjectEntity> projectMap = new HashMap<>();

    public static List<ProjectEntity> getProjectDetails(List<String> projects) throws Exception {
        List<ProjectEntity> projectEntities = new ArrayList<>();
        List<String> missingProjects = new ArrayList<>();

        for (String org : projects) {
            if (projectMap.containsKey(org)) {
                projectEntities.add(projectMap.get(org));
            } else {
                missingProjects.add(org);
            }
        }

        if (missingProjects.size() > 0) {
            List<ProjectEntity> entities = ProjectEntity.getProjectsFromList(missingProjects);

            for (ProjectEntity org : entities) {
                projectMap.put(org.getUuid(), org);
                projectEntities.add(org);
            }
        }

        return projectEntities;

    }

    public static ProjectEntity getProjectDetails(String projectId) throws Exception {
        ProjectEntity projectEntity = null;

        if (projectMap.containsKey(projectId)) {
            projectEntity = projectMap.get(projectId);
        } else {
            projectEntity = ProjectEntity.getProject(projectId);
            projectMap.put(projectEntity.getUuid(), projectEntity);
        }

        return projectEntity;

    }
}
