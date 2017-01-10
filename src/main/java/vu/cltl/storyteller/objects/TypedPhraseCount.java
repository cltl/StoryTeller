package vu.cltl.storyteller.objects;

import java.util.ArrayList;

/**
 * Created by piek on 05/01/2017.
 */
public class TypedPhraseCount extends PhraseCount {

    private ArrayList<String> types;
    private ArrayList<String> labels;
    public TypedPhraseCount(String phrase, Integer count) {
        super(phrase, count);
        types = new ArrayList<String>();
        labels = new ArrayList<String>();
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public void addType(String type) {
        if (!types.contains(type)) {
            this.types.add(type);
        }
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public void addLabel(String label) {
        if (!labels.contains(label) && label.charAt(label.length()-3)!='@') {      //!label.endsWith("@en")
            this.labels.add(label);
        }
    }

    public PhraseCount castToPhraseCount ()
    {   PhraseCount phraseCount = new PhraseCount(this.getPhrase(), this.getCount());
        return phraseCount;
    }
}
