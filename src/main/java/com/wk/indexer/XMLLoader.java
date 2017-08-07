//package com.wk.indexer;
//
//
//import com.carrotsearch.ant.tasks.junit4.dependencies.com.google.common.collect.Lists;
//import org.apache.solr.common.SolrException;
//import org.apache.solr.common.SolrInputDocument;
//import org.apache.solr.common.util.StrUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.xml.stream.XMLStreamConstants;
//import javax.xml.stream.XMLStreamException;
//import javax.xml.stream.XMLStreamReader;
//import java.util.*;
//
//import static org.apache.solr.common.params.CommonParams.NAME;
//
//public class XMLLoader {
//
//    private static Logger log = LoggerFactory.getLogger(IndexRunner.class);
//
//    protected static SolrInputDocument readDoc(XMLStreamReader parser) throws XMLStreamException {
//        SolrInputDocument doc = new SolrInputDocument();
//
//        String attrName = "";
//        for (int i = 0; i < parser.getAttributeCount(); i++) {
//            attrName = parser.getAttributeLocalName(i);
//            if ("boost".equals(attrName)) {
//                doc.setDocumentBoost(Float.parseFloat(parser.getAttributeValue(i)));
//            } else {
//                log.warn("XML element <doc> has invalid XML attr:" + attrName);
//            }
//        }
//
//        StringBuilder text = new StringBuilder();
//        String name = null;
//        float boost = 1.0f;
//        boolean isNull = false;
//        String update = null;
//        Collection<SolrInputDocument> subDocs = null;
//        Map<String, Map<String, Object>> updateMap = null;
//        boolean complete = false;
//        while (!complete) {
//            int event = parser.next();
//            switch (event) {
//                // Add everything to the text
//                case XMLStreamConstants.SPACE:
//                case XMLStreamConstants.CDATA:
//                case XMLStreamConstants.CHARACTERS:
//                    text.append(parser.getText());
//                    break;
//
//                case XMLStreamConstants.END_ELEMENT:
//                    if ("doc".equals(parser.getLocalName())) {
//                        if (subDocs != null && !subDocs.isEmpty()) {
//                            doc.addChildDocuments(subDocs);
//                            subDocs = null;
//                        }
//                        complete = true;
//                        break;
//                    } else if ("field".equals(parser.getLocalName())) {
//                        // should I warn in some text has been found too
//                        Object v = isNull ? null : text.toString();
//                        if (update != null) {
//                            if (updateMap == null) updateMap = new HashMap<>();
//                            Map<String, Object> extendedValues = updateMap.get(name);
//                            if (extendedValues == null) {
//                                extendedValues = new HashMap<>(1);
//                                updateMap.put(name, extendedValues);
//                            }
//                            Object val = extendedValues.get(update);
//                            if (val == null) {
//                                extendedValues.put(update, v);
//                            } else {
//                                // multiple val are present
//                                if (val instanceof List) {
//                                    List list = (List) val;
//                                    list.add(v);
//                                } else {
//                                    List<Object> values = new ArrayList<>();
//                                    values.add(val);
//                                    values.add(v);
//                                    extendedValues.put(update, values);
//                                }
//                            }
//                            break;
//                        }
//                        doc.addField(name, v, boost);
//                        boost = 1.0f;
//                        // field is over
//                        name = null;
//                    }
//                    break;
//
//                case XMLStreamConstants.START_ELEMENT:
//                    text.setLength(0);
//                    String localName = parser.getLocalName();
//                    if ("doc".equals(localName)) {
//                        if (subDocs == null)
//                            subDocs = Lists.newArrayList();
//                        subDocs.add(readDoc(parser));
//                    }
//                    else {
//                        if (!"field".equals(localName)) {
//                            String msg = "XML element <doc> has invalid XML child element: " + localName;
//                            log.warn(msg);
//                            throw new SolrException(SolrException.ErrorCode.BAD_REQUEST,
//                                    msg);
//                        }
//                        boost = 1.0f;
//                        update = null;
//                        isNull = false;
//                        String attrVal = "";
//                        for (int i = 0; i < parser.getAttributeCount(); i++) {
//                            attrName = parser.getAttributeLocalName(i);
//                            attrVal = parser.getAttributeValue(i);
//                            if (NAME.equals(attrName)) {
//                                name = attrVal;
//                            } else if ("boost".equals(attrName)) {
//                                boost = Float.parseFloat(attrVal);
//                            } else if ("null".equals(attrName)) {
//                                isNull = StrUtils.parseBoolean(attrVal);
//                            } else if ("update".equals(attrName)) {
//                                update = attrVal;
//                            } else {
//                                log.warn("XML element <field> has invalid XML attr: " + attrName);
//                            }
//                        }
//                    }
//                    break;
//            }
//        }
//
//        if (updateMap != null)  {
//            for (Map.Entry<String, Map<String, Object>> entry : updateMap.entrySet()) {
//                name = entry.getKey();
//                Map<String, Object> value = entry.getValue();
//                doc.addField(name, value, 1.0f);
//            }
//        }
//
//        return doc;
//    }
//}
