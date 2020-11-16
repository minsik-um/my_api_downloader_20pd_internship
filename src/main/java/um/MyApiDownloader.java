package um;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class MyApiDownloader {

    private String serviceKey;

    public MyApiDownloader(String serviceKey) {
        this.serviceKey = serviceKey;

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean downloadApi(String baseUrl, Map<String, String> parameters, String itemTag) {
        try {
            String tblName = makeTblNameWithUrl(baseUrl);
            String[][][] data = requestGet(baseUrl, parameters, itemTag);
            String query = makeSqlInsert(tblName, data);

            executeQueryToDb(query);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    
    public List<String> getDistinctValues(String columnName, String tableName) {
        List<String> ret = null;

        try (
            Connection con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/DQLITE?allowMultiQueries=true", "WISEDA", "wise1012");
            PreparedStatement pstmt = con.prepareStatement(String.format("SELECT DISTINCT %s FROM %s", columnName, tableName));
            ResultSet rs = pstmt.executeQuery();
        ) {
            ret = new ArrayList<>();

            while (rs.next()) {                
                String value = rs.getString(columnName);
                ret.add(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    private URL makeUrl(String baseUrl, Map<String, String> parameters) {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        URL url = null;

        try {
            // url 생성
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + this.serviceKey);


            for (Map.Entry<String, String> param : parameters.entrySet()) {
                String key = param.getKey();
                String value = param.getValue();

                urlBuilder.append("&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
            }

            url = new URL(urlBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }

    /*
     * reference: https://howtodoinjava.com/java/xml/parse-string-to-xml-dom/
     */
    private Document convertStringToXml(String xmlStr) {
        Document doc = null;
        DocumentBuilder builder = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(xmlStr)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return doc;
    }

    private String[][][] convertXmlToStrArr(Document doc, String itemTag) {
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName(itemTag);
        String[][][] ret = new String[nList.getLength()][][];

        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);
            NodeList children = nNode.getChildNodes();

            ret[i] = new String[children.getLength()][];

            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                ret[i][j] = new String[] { child.getNodeName(), child.getTextContent() };
            }
        }

        return ret;
    }

    /*
     * reference: 공공데이터포털 제공 샘플 코드
     */
    private String[][][] requestGet(String baseUrl, Map<String, String> parameters, String itemTag) {
        StringBuilder sb = new StringBuilder();

        BufferedReader rd = null;
        HttpURLConnection conn = null;

        try {
            URL url = makeUrl(baseUrl, parameters);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-type", "application/json");

            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            rd.close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertXmlToStrArr(convertStringToXml(sb.toString()), itemTag);
    }

    private String makeSqlAddColsIfNotExists(String tableName, String[][][] data) {
        StringBuilder builder = new StringBuilder();

        String[][] row = data[0];
        for (String[] field : row) {

            builder.append("ALTER TABLE ");
            builder.append(tableName);
            builder.append(" ADD COLUMN IF NOT EXISTS ");
            builder.append(field[0]);
            builder.append(" VARCHAR(400);");
        }

        return builder.toString();
    }

    private String makeSqlCreateTbl(String tableName, String[][][] data) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append(tableName);
        builder.append(" (");

        String[][] row = data[0];
        List<String> keys = new ArrayList<>();
        for (String[] field : row) {
            keys.add(field[0] + " VARCHAR(400)");
        }
        builder.append(String.join(", ", keys));

        builder.append(");");

        return builder.toString();
    }

    private String makeSqlInsert(String tableName, String[][][] data) {
        StringBuilder allBuilder = new StringBuilder();
        allBuilder.append(makeSqlCreateTbl(tableName, data));
        allBuilder.append(makeSqlAddColsIfNotExists(tableName, data));

        for (String[][] row : data) {
            StringBuilder rowBuilder = new StringBuilder();
            List<String> keys = new ArrayList<>();
            List<String> values = new ArrayList<>();

            for (String[] field : row) {
                keys.add(field[0]);
                // single quote를 문자로서 sql에 넣을 수 있도록 변형
                String value = field[1];

                // 길이가 400이 넘으면 (어자피 검사 대상이 아닌) name 유형 컬럼임. 오류 안나게 자름.
                if (value.length() > 400) {
                    value = value.substring(0, Math.min(0, 400));
                }

                values.add(value.replaceAll("'", "''"));
            }

            rowBuilder.append("INSERT INTO ");
            rowBuilder.append(tableName);
            rowBuilder.append(" (");
            rowBuilder.append(String.join(", ", keys));
            rowBuilder.append(") VALUES ('");
            rowBuilder.append(String.join("', '", values));
            rowBuilder.append("');");

            allBuilder.append(rowBuilder.toString());
        }

        return allBuilder.toString();
    }

    /*
     * reference https://mariadb.com/kb/en/about-mariadb-connector-j/
     * http://www.gisdeveloper.co.kr/?p=4858 https://roqkffhwk.tistory.com/182
     */
    private void executeQueryToDb(String query) {
        try (
            Connection con = DriverManager.getConnection("jdbc:mariadb://localhost:3306/DQLITE?allowMultiQueries=true", "WISEDA", "wise1012");
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
        ) {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String makeTblNameWithUrl(String url) {
        String[] splited = url.split("/");
        String host = splited[2].replaceAll("\\.", "");
        String operation = splited[splited.length - 1];

        return String.format("%s_%s", host, operation);
    }
}