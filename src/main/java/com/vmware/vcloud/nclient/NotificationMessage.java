package com.vmware.vcloud.nclient;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.InputSource;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

public class NotificationMessage implements Comparable<NotificationMessage>{

    final static String NS_URI = "http://www.vmware.com/vcloud/extension/v1.5";
    public static final String JSON_KEY_TYPE = "type";
    public static final String JSON_KEY_STATUS = "operationSuccess";
    public static final String JSON_KEY_TIMESTAMP = "timestamp";
    public static final String JSON_KEY_ENTITY = "entity";

    public static final String JSON_CONTENT_TYPE = "application/json";
    public static final String XML_CONTENT_TYPE = "application/xml";

    final String payload;
    final Map<String, Object> headers;
    final String contentType;

    String type;
    String entityType;
    String entityName;
    String entityHref;
    String orgName;
    String userName;
    Date timestamp;
    boolean success;

    private NotificationMessage(String payload, Map<String, Object> headers, String contentType) {
        this.payload = payload;
        this.headers = headers;
        this.contentType = contentType;
    }

    public static NotificationMessage createFromPayloadAndHeaders(String payload, Map<String, Object> headers) throws Exception {
        boolean isXml = payload.startsWith("<?xml");
        return isXml ? createFromXml(payload, headers) : createFromJson(payload, headers);
    }

    public String getContentType() {
        return contentType;
    }

    static NotificationMessage createFromXml(String payload, Map<String, Object> headers) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(new InputSource(new StringReader(payload)));
        NotificationMessage result = new NotificationMessage(payload, headers, XML_CONTENT_TYPE);
        Node root = doc.getFirstChild();
        result.type = root.getAttributes().getNamedItem("type").getNodeValue();
        Node resolver = doc.getElementsByTagNameNS(NS_URI, "Link").item(0);
        String resolverHref = resolver.getAttributes().getNamedItem("href").getNodeValue();
        NodeList links = doc.getElementsByTagNameNS(NS_URI, "EntityLink");
        Node org = getLinkByRel(links, "up");
        result.orgName = org.getAttributes().getNamedItem("name").getNodeValue();
        Node user = getLinkByRel(links, "down");
        if (user != null) {
            result.userName = user.getAttributes().getNamedItem("name").getNodeValue();
        }
        Node entity = getLinkByRel(links, "entity");
        if (entity != null) {
            result.entityType = entity.getAttributes().getNamedItem("type").getNodeValue();
            int ind = result.entityType.indexOf(':');
            if (ind >= 0) {
                result.entityType = result.entityType.substring(ind + 1);
            }
            Node namedItem = entity.getAttributes().getNamedItem("name");
            if (namedItem != null) {
                result.entityName = namedItem.getNodeValue();
            }
            result.entityHref = resolverHref + entity.getAttributes().getNamedItem("id").getNodeValue();
        }
        Node ts = doc.getElementsByTagNameNS(NS_URI, "Timestamp").item(0);
        XMLGregorianCalendar xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(ts.getTextContent());
        result.timestamp = xmlDate.toGregorianCalendar().getTime();
        Node success = doc.getElementsByTagNameNS(NS_URI, "OperationSuccess").item(0);
        result.success = Boolean.parseBoolean(success.getTextContent());
        return result;
    }

    static NotificationMessage createFromJson(String payload, Map<String, Object> headers) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> data = mapper.readValue(payload, Map.class);
        ObjectWriter writer = mapper.defaultPrettyPrintingWriter();
        payload = writer.writeValueAsString(data);
        NotificationMessage result = new NotificationMessage(payload, headers, JSON_CONTENT_TYPE);
        if (data.containsKey(JSON_KEY_TYPE)) {
            result.type = (String) data.get(JSON_KEY_TYPE);
        }
        if (data.containsKey(JSON_KEY_ENTITY)) {
            String entity = (String) data.get(JSON_KEY_ENTITY);
            Pattern p = Pattern.compile("urn:vcloud:([^:]+):.*");
            Matcher m = p.matcher(entity);
            if (m.find()) {
                result.entityType = m.group(1);
            }
        }
        if (data.containsKey(JSON_KEY_STATUS)) {
            result.success = (Boolean) data.get(JSON_KEY_STATUS);
        }
        if (data.containsKey(JSON_KEY_TIMESTAMP)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            result.timestamp = sdf.parse((String) data.get(JSON_KEY_TIMESTAMP));
        }
        return result;
    }

    static Node getLinkByRel(NodeList links, String rel) {
        for (int i = 0; i < links.getLength(); i++) {
            Node link = links.item(i);
            if (link.getAttributes().getNamedItem("rel").getNodeValue().equals(rel)) {
                return link;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }

    public String getEntityType() {
        return entityType == null ? "" : entityType;
    }

    public String getEntityName() {
        return entityName == null ? "" : entityName;
    }

    public String getEntityHref() {
        return entityHref == null ? "" : entityHref;
    }

    public String getOrgName() {
        return orgName == null ? "" : orgName;
    }

    public String getUserName() {
        return userName == null ? "" : userName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public boolean isOperationSuccess() {
        return success;
    }

    public String getPayload() {
        return payload;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    @Override
    public int compareTo(NotificationMessage o) {
        return getType().compareTo(o.getType());
    }

}
