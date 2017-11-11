package vu.cltl.storyteller.input;

import eu.kyotoproject.kaf.KafSaxParser;
import eu.kyotoproject.kaf.KafWordForm;
import org.apache.tools.bzip2.CBZip2InputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import vu.cltl.storyteller.util.Util;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by piek on 20/07/16.
 * This class creates and reads a NafTokenIndex file
 */
public class NafTokenLayerIndex extends DefaultHandler {

    public HashMap<String, ArrayList<KafWordForm>>  tokenMap;
    private String value = "";
    private KafWordForm kafWordForm;
    private ArrayList<KafWordForm> kafWordForms;
    private String urlString;
    private Vector<String> uriFilter;

    /**
     * The main function can be used to create a NafTokenLayerIndex file from a collection of NAF files
     * The function needs the following input parameters:
     *
     * --folder         to the folder that contains the NAF files (reads recursively)
     * --extension      extension of the naf files, e.g. ".naf"
     * --project        name of the project used for building the RDF files to match URLs for sources if not in the NAF header
     *
     * The output is an index in gzip format with the source URLs and the token XML from the NAF file
     * This index can be used to construct the snippets for querying the KnowledgeStore.
     *
     * @param args
     */
    static public void main (String[] args) {
        String folder = "";
        String filter = "";
        String project = "";
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("--folder") && args.length>(i+1)) {
                folder = args[i+1];
            }
            else if (arg.equals("--extension") && args.length>(i+1)) {
                filter = args[i+1];
            }
            else if (arg.equals("--project") && args.length>(i+1)) {
                project = args[i+1];
            }
        }
        try {
            createTokenIndex(new File(folder), filter, project);
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    public NafTokenLayerIndex () {
        tokenMap = new HashMap<String, ArrayList<KafWordForm>>();
        init();
    }

    public NafTokenLayerIndex (Vector<String> uriList) {
        tokenMap = new HashMap<String, ArrayList<KafWordForm>>();
        init();
        uriFilter = uriList;
       // System.err.println("uriList.size() = " + uriList.size());
       // System.out.println("uriList.toString() = " + uriList.toString());
    }

    void init() {
        kafWordForms = new ArrayList<KafWordForm>();
        kafWordForm = new KafWordForm();
        urlString = "";
        uriFilter = new Vector<String>();
    }

    /*
       Parse index file as a stream
     */
    public boolean parseFile(InputStream stream)
    {
        InputSource source = new InputSource(stream);
        boolean result = parseFile(source);
        try
        {
            stream.close();
        }
        catch (IOException e)
        {}
        return result;
    }

    /**
     * Parse filePath
     * @param source
     * @return
     */
    public boolean parseFile(String source)
    {
        return parseFile(new File (source));
    }

    /**
     * Parse file
     * @param source
     * @return
     */
    public boolean parseFile(File source)
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
            parser.parse(source, this);
            return true;
        }
        catch (FactoryConfigurationError factoryConfigurationError)
        {
            factoryConfigurationError.printStackTrace();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            //System.out.println("last value = " + previousvalue);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // e.printStackTrace();
        }
        return false;
    }

    /**
     * Parse input stream, e.g. a gzip file
     * @param source
     * @return
     */
    public boolean parseFile(InputSource source)
    {
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            SAXParser parser = factory.newSAXParser();
            parser.parse(source, this);
            return true;
        }
        catch (FactoryConfigurationError factoryConfigurationError)
        {
            factoryConfigurationError.printStackTrace();
        }
        catch (ParserConfigurationException e)
        {
            e.printStackTrace();
        }
        catch (SAXException e)
        {
            //System.out.println("last value = " + previousvalue);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // e.printStackTrace();
        }
        return false;
    }

    public void startElement(String uri, String localName,
                             String qName, Attributes attributes)
            throws SAXException {
        value = "";
        if ((qName.equalsIgnoreCase("text"))) {
            kafWordForms = new ArrayList<KafWordForm>();
        }
        else if (qName.equalsIgnoreCase("wf")) {
            kafWordForm = new KafWordForm();
            String wid = "";
            String sentenceId = "";
            for (int i = 0; i < attributes.getLength(); i++) {
                String name = attributes.getQName(i);
                if (name.equalsIgnoreCase("wid")) {
                    wid = attributes.getValue(i).trim();
                    kafWordForm.setWid(wid);
                }
                else if (name.equalsIgnoreCase("id")) {
                    wid = attributes.getValue(i).trim();
                    kafWordForm.setWid(wid);
                }
                else if (name.equalsIgnoreCase("para")) {
                    kafWordForm.setPara(attributes.getValue(i).trim());
                }
                else if (name.equalsIgnoreCase("page")) {
                    kafWordForm.setPage(attributes.getValue(i).trim());
                }
                else if (name.equalsIgnoreCase("sent")) {
                    sentenceId = attributes.getValue(i).trim();
                    kafWordForm.setSent(sentenceId);
                }
                else if (name.equalsIgnoreCase("charoffset")) {
                    kafWordForm.setCharOffset(attributes.getValue(i).trim());
                }
                else if (name.equalsIgnoreCase("charlength")) {
                    kafWordForm.setCharLength(attributes.getValue(i).trim());
                }
                else if (name.equalsIgnoreCase("offset")) {
                    kafWordForm.setCharOffset(attributes.getValue(i).trim());
                }
                else if (name.equalsIgnoreCase("length")) {
                    kafWordForm.setCharLength(attributes.getValue(i).trim());
                }
                else {
                    //  System.out.println("414 ********* FOUND UNKNOWN Attribute " + name + " *****************");
                }
            }
        }
    }


    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equals("text")) {

            if (uriFilter.contains(urlString) || uriFilter.isEmpty()) {
             //   System.out.println("urlString = " + urlString);
                tokenMap.put(urlString, kafWordForms);
            }
            else {
               // System.out.println("uriFilter = " + uriFilter);
               // System.out.println("cannot find urlString = " + urlString);
                //tokenMap.put(urlString, kafWordForms);

            }
            urlString = "";
            kafWordForms = new ArrayList<KafWordForm>();
        }
        else if (qName.equals("wf")) {
            kafWordForm.setWf(value);
            kafWordForms.add(kafWordForm);
            kafWordForm = new KafWordForm();
        }
        else if (qName.equals("url")) {
            urlString = value;
        }
    }

    public void characters(char ch[], int start, int length)
            throws SAXException {
        value += new String(ch, start, length);
    }

    /**
     *
     * @param folder
     * @param filter
     * @param project
     * @throws IOException
     */
    static void createTokenIndex (File folder, String filter, String project) throws IOException {
        final String nwrdata = "http://www.newsreader-project.eu/data/";

        File indexFile = new File("token.index.gz");
        GZIPOutputStream gzipOutputStream =
                new GZIPOutputStream(new FileOutputStream(indexFile));

      //  OutputStream stream = new FileOutputStream(indexFile);

        String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n<index>\n";
        gzipOutputStream.write(str.getBytes());

        ArrayList<File> nafFiles = Util.makeRecursiveFileList(folder, filter);

        KafSaxParser kafSaxParser = new KafSaxParser();
        for (int f = 0; f < nafFiles.size(); f++) {
            File file = nafFiles.get(f);
            if (f%100==0) System.out.println("file.getName() = " + file.getName());
            kafSaxParser.parseFile(file);

            String baseUrl = kafSaxParser.getKafMetaData().getUrl();
            if (baseUrl.isEmpty()) {
                baseUrl = nwrdata + project + "/" + file.getName();
            }
            else if (!baseUrl.toLowerCase().startsWith("http")) {
                //  System.out.println("baseUrl = " + baseUrl);
                baseUrl = nwrdata + project + "/" + kafSaxParser.getKafMetaData().getUrl();
            }
            /*String uri = kafSaxParser.getKafMetaData().getUrl();
            if (uri.isEmpty()) {
                uri = file.getName();
            }*/
            if (kafSaxParser.kafWordFormList.size()>0) {
                str = "<text>\n";
                str += "<url><![CDATA["+baseUrl+"]]></url>\n";
                for (int i = 0; i < kafSaxParser.kafWordFormList.size(); i++) {
                    KafWordForm kaf  = kafSaxParser.kafWordFormList.get(i);
                    //<wf id="w1" length="10" offset="0" para="1" sent="1">Resolucion</wf>
                    str += "<wf id=\""+kaf.getWid()+"\" sent=\""+kaf.getSent()+"\" length=\""+kaf.getCharLength()+"\" offset=\""+kaf.getCharOffset()+"\"><![CDATA["+kaf.getWf()+"]]></wf>\n";
                }
                str += "</text>\n";
                gzipOutputStream.write(str.getBytes());
            }
        }
        str = "</index>\n";
        gzipOutputStream.write(str.getBytes());
        gzipOutputStream.close();
    }

    /**
     * Given a set of JSON event objects it gets the text snippets for the mentions and creates offsets for the snippets
     * @param objects
     * @param pathToTokenIndex
     * @return
     * @throws JSONException
     */
    public static String createSnippetIndexFromMentions(ArrayList<JSONObject> objects,
                                                        String pathToTokenIndex) throws JSONException {
        String log = "";
        HashMap<String, ArrayList<String>> sourceUriList = new HashMap<String, ArrayList<String>>();
        Vector<String> urls = new Vector<String>();
        HashMap<String, Integer> eventIdObjectMap = new HashMap<String, Integer>();
        for (int i = 0; i < objects.size(); i++) {
            JSONObject jsonObject = objects.get(i);
            try {
                String eventId = jsonObject.getString("instance");
                eventIdObjectMap.put(eventId, i);
                JSONArray mentions = null;
                try {
                    mentions = (JSONArray) jsonObject.get("mentions");
                } catch (JSONException e) {
                    //e.printStackTrace();
                }
                if (mentions!=null) {
                    for (int j = 0; j < mentions.length(); j++) {
                        JSONObject mObject = mentions.getJSONObject(j);
                        String uString = mObject.getString("uri");
                        if (!urls.contains(uString)) {
                            urls.add(uString);
                        }
                        if (sourceUriList.containsKey(uString)) {
                            ArrayList<String> eventIds = sourceUriList.get(uString);
                            if (!eventIds.contains(eventId)) {
                                eventIds.add(eventId);
                                sourceUriList.put(uString, eventIds);
                            }
                        } else {
                            ArrayList<String> eventIds = new ArrayList<String>();
                            eventIds.add(eventId);
                            sourceUriList.put(uString, eventIds);
                        }
                    }
                }
            } catch (JSONException e) {
                //  e.printStackTrace();
            }
        }

        // System.out.println(" * Getting sourcedocuments for unique sources = " + sourceUriList.size());
        long startTime = System.currentTimeMillis();
      //  System.out.println("urls = " + urls);
        NafTokenLayerIndex nafTokenLayerIndex = new NafTokenLayerIndex(urls);


        if (pathToTokenIndex.toLowerCase().endsWith(".gz")) {
            try {
                InputStream fileStream = new FileInputStream(pathToTokenIndex);
                InputStream gzipStream = new GZIPInputStream(fileStream);
                nafTokenLayerIndex.parseFile(gzipStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (pathToTokenIndex.toLowerCase().endsWith(".bz2")) {
            try {
                InputStream fileStream = new FileInputStream(pathToTokenIndex);
                InputStream gzipStream = new CBZip2InputStream(fileStream);
                nafTokenLayerIndex.parseFile(gzipStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            nafTokenLayerIndex.parseFile(pathToTokenIndex);
        }

     //   System.out.println("pathToTokenIndex = " + pathToTokenIndex);
     //   System.out.println("nafTokenLayerIndex.tokenMap.size() = " + nafTokenLayerIndex.tokenMap.size());
        Set keySet = sourceUriList.keySet();
        Iterator<String> keys = keySet.iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            /// we first get the tokens for the single NAF file.
            /// next we serve each event with mentions in this NAF file
            //System.out.println("key = " + key);
            //key = http://www.coprocom.go.cr/resoluciones/2013/voto-18-2013-%20recurso-reconsideracion-lanco-sumario.pdf
            ArrayList<KafWordForm> wordForms = null;
            if (nafTokenLayerIndex.tokenMap.containsKey(key)) {
                wordForms = nafTokenLayerIndex.tokenMap.get(key);
            }
            ArrayList<String> eventIds = sourceUriList.get(key);
            for (int i = 0; i < eventIds.size(); i++) {
                String eventId = eventIds.get(i);
                //   System.out.println("eventId = " + eventId);
                int idx = eventIdObjectMap.get(eventId);
                JSONObject eventObject = objects.get(idx);
                JSONArray mentions = (JSONArray) eventObject.get("mentions");
                //  System.out.println("mentions.length() = " + mentions.length());
                for (int j = 0; j < mentions.length(); j++) {
                    JSONObject mObject  = mentions.getJSONObject(j);
                    String uString = mObject.getString("uri");
                    JSONArray offsetArray = mObject.getJSONArray("char");
                    Integer offsetBegin =  null;
                    try {
                        offsetBegin = Integer.parseInt(offsetArray.getString(0));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (uString.equals(key) && offsetBegin!=null &wordForms!=null) {
                        for (int k = 0; k < wordForms.size(); k++) {
                            KafWordForm kafWordForm = wordForms.get(k);
                            Integer kafOffset = Integer.parseInt(kafWordForm.getCharOffset());
                            if (kafOffset>offsetBegin) {
                                break;
                            }
                            if (kafOffset.equals(offsetBegin)) {
                                // we found the sentence and the word, now make the snippet
                                String wf = kafWordForm.getWf();
                                String sentenceId = kafWordForm.getSent();
                                String newText = kafWordForm.getWf();
                                if (k > 0) {
                                    int m = k-1;
                                    KafWordForm kafWordForm2 = wordForms.get(m);
                                    String sentenceId2 = kafWordForm2.getSent();
                                    while (sentenceId2.equals(sentenceId)) {
                                        newText = kafWordForm2.getWf() + " " + newText;
                                        m--;
                                        if (m >= 0) {
                                            kafWordForm2 = wordForms.get(m);
                                            sentenceId2 = kafWordForm2.getSent();
                                        }
                                        else {
                                            break;
                                        }
                                    }
                                }
                                offsetBegin = newText.lastIndexOf(wf);
                                int offsetEnd = offsetBegin + wf.length();
                                if ((k + 1) < wordForms.size()) {
                                    int m = k + 1;
                                    KafWordForm kafWordForm2 = wordForms.get(m);
                                    String sentenceId2 = sentenceId;
                                    while (sentenceId2.equals(sentenceId)) {
                                        newText = newText + " " + kafWordForm2.getWf();
                                        m++;
                                        if (m < wordForms.size()) {
                                            kafWordForm2 = wordForms.get(m);
                                            sentenceId2 = kafWordForm2.getSent();
                                        } else {
                                            break;
                                        }
                                    }

                                }
                               /* System.out.println("offsetBegin = " + offsetBegin);
                                System.out.println("offsetEnd = " + offsetEnd);
                                System.out.println("final newText = " + newText);*/
                                mObject.append("snippet", newText);
                                mObject.append("snippet_char", offsetBegin);
                                mObject.append("snippet_char", offsetEnd);

                                break;

                            } else {
                                ///not the word
                            }
                        }
                    }
                    else if (wordForms==null || offsetBegin==null) {
                       // System.out.println("uString = " + uString);
                        mObject.append("snippet", "Could not find the original text.");
                        mObject.append("snippet_char", 0);
                        mObject.append("snippet_char", 0);
                    }
                }
            }
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        log = " -- Time elapsed to get snippets from "+sourceUriList.size()+" source documents:"+estimatedTime/1000.0;
        return log;
    }

    /**
     * Given a set of JSON event objects it gets the text snippets for the mentions and creates offsets for the snippets
     * @param fileName
     * @param nContext
     * @param events
     */
    @Deprecated
    static public void ReadFileToUriTextArrayList(String fileName, int nContext, ArrayList<JSONObject> events) {
        HashMap<String, String> rawTextMap = new HashMap<String, String>();
        if (new File(fileName).exists() ) {
            try {
                InputStream fis = new FileInputStream(fileName);
                InputStreamReader isr = new InputStreamReader(fis, "UTF8");
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                String uri = "";
                String text = "";

                while (in.ready()&&(inputLine = in.readLine()) != null) {
                    //System.out.println(inputLine);
                    if (inputLine.trim().length()>0) {
                        if (inputLine.startsWith("http:")) {
                            //  System.out.println("inputLine = " + inputLine);
                            String[] fields = inputLine.split("\t");
                            if (fields.length > 1) {
                                if (!uri.isEmpty() && !text.isEmpty()) {
                                    rawTextMap.put(uri, text);
                                    uri = ""; text = "";
                                }
                                uri = fields[0];
                                text = fields[1];
                            }
                        }
                        else {
                            text += "\n"+inputLine;
                        }
                    }
                }
                if (!uri.isEmpty() && !text.isEmpty()) {
                    rawTextMap.put(uri, text);
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < events.size(); i++) {
                JSONObject jsonObject = events.get(i);

                try {
                    JSONArray myMentions = jsonObject.getJSONArray("mentions");
                    for (int m = 0; m < myMentions.length(); m++) {
                        JSONObject mentionObject = (JSONObject) myMentions.get(m);
                        String uri = mentionObject.getString("uri");
                        JSONArray offsetArray = mentionObject.getJSONArray("char");
                        Integer offsetBegin =  null;
                        try {
                            offsetBegin = Integer.parseInt(offsetArray.getString(0));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        Integer offsetEnd = null;
                        try {
                            offsetEnd = Integer.parseInt(offsetArray.getString(1));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        if (rawTextMap.containsKey(uri) && offsetBegin!=null && offsetEnd!=null) {
                            String text = rawTextMap.get(uri);
                            String newText = text;
                            int offsetLength = (offsetEnd-offsetBegin);
                            int textStart = 0;
                            int textEnd = offsetBegin+offsetLength+nContext;
                            //System.out.println("offsetBegin = " + offsetBegin);
                            //System.out.println("offsetEnd = " + offsetEnd);
                            //System.out.println("offsetLength = " + offsetLength);
                            if (offsetBegin>nContext) {
                                textStart = offsetBegin-nContext;
                                int idx = text.lastIndexOf(" ",textStart);
                                if (idx>-1 && idx<textStart) {
                                    //  System.out.println("idx = " + idx);
                                    //  System.out.println("textStart = " + textStart);
                                    if (offsetBegin>(textStart-idx)+nContext) {
                                        offsetBegin = (textStart-idx)+nContext;
                                        textStart = idx;
                                    }
                                    else {
                                        textStart = 0;
                                    }
                                }
                                else {
                                    offsetBegin = nContext;
                                }
                                offsetEnd = offsetBegin+offsetLength;
                            }
                            int idx = text.indexOf(" ", textEnd);
                            if (idx>-1) textEnd = idx;
                            if (text.length()<=textEnd) {
                                textEnd = text.length()-1;
                            }

                            newText = text.substring(textStart, textEnd);
                            if (offsetEnd>=newText.length()) {
                                offsetBegin = newText.length()-offsetLength;
                                offsetEnd = newText.length()-1;
                            }
/*
                            System.out.println("newText = " + newText);
                            System.out.println("offsetBegin = " + offsetBegin);
                            System.out.println("offsetEnd = " + offsetEnd);
*/
                            // System.out.println("mention = " + newText.substring(offsetBegin, offsetEnd));
                            //System.out.println("newText = " + newText);
                            try {
                                mentionObject.append("snippet", newText);
                                mentionObject.append("snippet_char", offsetBegin);
                                mentionObject.append("snippet_char", offsetEnd);
                                //jsonObject.append("mentions", newMentionObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        else {
                            // jsonObject.append("mentions", mentionObject);
                        }
                    }
                    // jsonObject.put("mentions", newMentions);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        else {
            System.out.println("Cannot find fileName = " + fileName);
        }
    }

    /**
     *
     * @param fileName
     * @return
     */
    @Deprecated
    static public ArrayList<JSONObject> ReadFileToUriTextArrayList(String fileName) {
        ArrayList<JSONObject> vector = new ArrayList<JSONObject>();
        if (new File(fileName).exists() ) {
            try {
                InputStream fis = new FileInputStream(fileName);
                InputStreamReader isr = new InputStreamReader(fis, "UTF8");
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                String uri = "";
                String text = "";

                while (in.ready()&&(inputLine = in.readLine()) != null) {
                    //System.out.println(inputLine);
                    if (inputLine.trim().length()>0) {
                        if (inputLine.startsWith("http:")) {
                            //  System.out.println("inputLine = " + inputLine);
                            String[] fields = inputLine.split("\t");
                            if (fields.length > 1) {
                                if (!uri.isEmpty() && !text.isEmpty()) {
                                    try {
                                        JSONObject jsonObject = new JSONObject();
                                        jsonObject.put("uri", uri);
                                        jsonObject.put("text", text);
                                        vector.add(jsonObject);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    uri = ""; text = "";
                                }
                                uri = fields[0];
                                text = fields[1];
                                //System.out.println("string = " + string);

                            }
                        }
                        else {
                            text += "\n"+inputLine;
                        }
                    }
                }
                if (!uri.isEmpty() && !text.isEmpty()) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("uri", uri);
                        jsonObject.put("text", text);
                        vector.add(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    uri = ""; text = "";
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Cannot find fileName = " + fileName);
        }
        return vector;
    }

    /**
     *
     * @param fileName
     * @return
     */
    @Deprecated
    static public ArrayList<JSONObject> ReadFileToUriTextArrayListOrg(String fileName) {
        ArrayList<JSONObject> vector = new ArrayList<JSONObject>();
        if (new File(fileName).exists() ) {
            try {
/*                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(fileName), "UTF8"));*/

                InputStream fis = new FileInputStream(fileName);
                InputStreamReader isr = new InputStreamReader(fis, "UTF8");
                BufferedReader in = new BufferedReader(isr);
                String inputLine;
                while (in.ready()&&(inputLine = in.readLine()) != null) {
                    //System.out.println(inputLine);
                    if (inputLine.trim().length()>0) {
                        //  System.out.println("inputLine = " + inputLine);
                        String[] fields = inputLine.split("\t");
                        if (fields.length > 1) {
                            String uri = fields[0];
                            String text = fields[1];
                            //System.out.println("string = " + string);
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("uri", uri);
                                jsonObject.put("text", text);
                                vector.add(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Cannot find fileName = " + fileName);
        }
        return vector;
    }

}
