package vu.cltl.storyteller.objects;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by piek on 26/01/2017.
 */
public class NewsReaderInstance {

    private String uri;
    private String instanceType;
    private ArrayList<String> labels;
    private ArrayList<String> types;
    private HashMap<String, ArrayList<String>> projectMentions;
    private HashMap<String, Integer> projectCounts;
    private HashMap<String, Integer> projectDocs;
    private ArrayList<String> sources;
    private ArrayList<String> projects;

    public NewsReaderInstance() {
        this.uri = "";
        this.instanceType = "";
        this.labels = new ArrayList<String>();
        this.projectMentions = new HashMap<String, ArrayList<String>>();
        this.projectCounts = new HashMap<String, Integer>();
        this.projectDocs = new HashMap<String, Integer>();
        this.types = new ArrayList<String>();
        this.projects = new ArrayList<String>();
        this.sources = new ArrayList<String>();
    }

    public ArrayList<String> getProjects() {
        return projects;
    }

    public void setProjects(ArrayList<String> projects) {
        this.projects = projects;
    }

    public void addProjects(String project) {
        if (!projects.contains(project)) this.projects.add(project);
    }

    public HashMap<String, Integer> getProjectCounts() {
        return projectCounts;
    }

    public void setProjectCounts(HashMap<String, Integer> projectCounts) {
        this.projectCounts = projectCounts;
    }

    public void addProjectCounts(String project, Integer projectCount) {
        this.addProjects(project);
        if (this.projectCounts.containsKey(project)) {
            Integer mCount = this.projectCounts.get(project);
            mCount += projectCount;
            this.projectCounts.put(project, mCount);
        }
        else {
            this.projectCounts.put(project, projectCount);
        }
    }

    public HashMap<String, Integer> getProjectDocs() {
        return projectDocs;
    }

    public void setProjectDocs(HashMap<String, Integer> projectDocs) {
        this.projectDocs = projectDocs;
    }

    public void addProjectDocs(String project, Integer projectDoc) {
        this.addProjects(project);
        if (this.projectDocs.containsKey(project)) {
            Integer dCount = this.projectDocs.get(project);
            dCount += projectDoc;
            this.projectDocs.put(project, dCount);
        }
        else {
            this.projectCounts.put(project, projectDoc);
        }
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        if (instanceType!= null)
        this.instanceType = instanceType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        if (uri!=null)  this.uri = uri;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public void addLabel(String label) {
        if (label!=null && !labels.contains(label)) {
            this.labels.add(label);
        }
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public void addTypes(String type) {
        if (!types.contains(type)) {
            this.types.add(type);
        }
    }

    public HashMap<String, ArrayList<String>> getProjectMentions() {
        return projectMentions;
    }

    public void setProjectMentions(HashMap<String, ArrayList<String>> projectMentions) {
        this.projectMentions = projectMentions;
    }

    public void addProjectMentions(String project, String mention) {
        if (this.projectMentions.containsKey(project)) {
            ArrayList<String> mentions = this.projectMentions.get(project);
            if (!mentions.contains(mention)) {
                mentions.add(mention);
                this.projectMentions.put(project, mentions);
            }
        }
        else {
            ArrayList<String> mentions = new ArrayList<String>();
            mentions.add(mention);
            this.projectMentions.put(project, mentions);

        }
    }

    public ArrayList<String> getSources() {
        return sources;
    }

    public void setSources(ArrayList<String> sources) {
        this.sources = sources;
    }

    public void addSource(String source) {
        if (source!=null && !sources.contains(source)) {
            this.sources.add(source);
        }
    }

    public JSONObject toJSONObject () throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if (!uri.isEmpty()) jsonObject.put("uri", uri);
        if (!instanceType.isEmpty()) jsonObject.put("instance", instanceType);
        if (labels.size()>0 ) {
            for (int i = 0; i < labels.size(); i++) {
                String l = labels.get(i);
                jsonObject.append("labels", l);
            }
        }
        if (types.size()>0) {
            for (int i = 0; i < types.size(); i++) {
                String t = types.get(i);
                jsonObject.append("types", t);
            }
        }
        else {
            jsonObject.append("types", "http://www.newsreader-project.eu/ontologies/MISC");

        }
        if (sources.size()>0) {
            for (int i = 0; i < sources.size(); i++) {
                String s = sources.get(i);
                jsonObject.append("sources", s);
            }
        }
        if (projectMentions.size()>0) {
            Set keySet = projectMentions.keySet();
            Iterator<String> keys = keySet.iterator();
            while (keys.hasNext()) {
                String project = keys.next();
                JSONObject mObject = new JSONObject();
                mObject.put("project", project);
                ArrayList<String> mentions = projectMentions.get(project);
                for (int i = 0; i < mentions.size(); i++) {
                    String m = mentions.get(i);
                    mObject.append("mentions", m);
                }
                jsonObject.append("projects", mObject);
            }
        }
        if (this.projects.size()>0) {
            for (int i = 0; i < projects.size(); i++) {
                String project = projects.get(i);
                JSONObject mObject = new JSONObject();
                mObject.append("project", project);

                if (projectCounts.containsKey(project)) {
                        Integer count = projectCounts.get(project);
                        mObject.append("mentions", count);
                }
                else {
                    mObject.append("mentions", "0");
                }
                if (projectDocs.containsKey(project)) {
                        Integer count = projectDocs.get(project);
                        mObject.append("sources", count);
                }
                else {
                    mObject.append("sources", "0");
                }
                jsonObject.append("projects", mObject);

            }
        }
        return jsonObject;
    }
}
