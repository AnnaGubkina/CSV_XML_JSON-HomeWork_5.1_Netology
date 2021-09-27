import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        //Задача 1
        // создаем файл data.csv
        File data = new File("/Users/SIREN-A/IdeaProjects/CSV XML JSON/data.csv");
        try {
            boolean created = data.createNewFile();
            if (created)
                System.out.println("File data.csv has been created");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        //получаем список сотрудников
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        // список преобразуем в строчку в формате JSON
        String json = listToJson(list);
        //записываем полученный JSON в файл
        writeString("/Users/SIREN-A/IdeaProjects/CSV XML JSON/data.json", json);

        // Задача 2
        // создаем файл data.xml
        File dataXml = new File("/Users/SIREN-A/IdeaProjects/CSV XML JSON/data.xml");
        try {
            boolean created = dataXml.createNewFile();
            if (created)
                System.out.println("File data.xml has been created");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        String fileXml = "data.xml";
        List<Employee> listStaff = parseXML(fileXml);
        //список преобразуем в строчку в формате JSON
        String json1 = listToJson(listStaff);
        //записываем полученный JSON в файл
        writeString("/Users/SIREN-A/IdeaProjects/CSV XML JSON/data2.json", json1);

        System.out.println();

        //Задача 3
        //Создаем файл new_data.json
        File dataJson = new File("/Users/SIREN-A/IdeaProjects/CSV XML JSON/new_data.json");
        try {
            boolean created = dataJson.createNewFile();
            if (created)
                System.out.println("File new_data.json has been created");
        } catch (IOException ex) {

            System.out.println(ex.getMessage());
        }
        // получение JSON из файла
        String json2 = readString("new_data.json");
        //System.out.println(json2);
        List<Employee> listEmloyee = jsonToList(json2);
        listEmloyee.forEach(System.out::println);

    }

    private static List<Employee> jsonToList(String json2) {
        List<Employee> listStaff = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(json2);
            JSONArray staff = (JSONArray) obj;
            for (Object i : staff) {
                listStaff.add(gson.fromJson(i.toString(), Employee.class));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return listStaff;
    }

    private static String readString(String jsonFile) {
        StringBuffer data = new StringBuffer();
        try (BufferedReader br = new BufferedReader(new FileReader(jsonFile))) {
            //чтение построчно
            String s;
            while ((s = br.readLine()) != null) {
                data.append(s).append("\n");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return data.toString();
    }


    private static List<Employee> parseXML(String fileXml) throws ParserConfigurationException, IOException, SAXException {
        List<String> elemetnsOfXml = new ArrayList<>();
        List<Employee> employees = new ArrayList<>();
        // документы document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileXml));
        // получаем корневой узел
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("employee")) {
                NodeList nodeListEml = node.getChildNodes();
                for (int j = 0; j < nodeListEml.getLength(); j++) {
                    Node node1 = nodeListEml.item(j);
                    if (Node.ELEMENT_NODE == node1.getNodeType()) {
                        Element employee = (Element) node1;
                        String s = employee.getTextContent();
                        elemetnsOfXml.add(s);
                    }
                }
                employees.add(new Employee(
                        Long.parseLong(elemetnsOfXml.get(0)),
                        elemetnsOfXml.get(1),
                        elemetnsOfXml.get(2),
                        elemetnsOfXml.get(3),
                        Integer.parseInt(elemetnsOfXml.get(4))));
                elemetnsOfXml.clear();
            }
        }
        return employees;
    }

    private static void writeString(String jsonFile, String json) throws IOException {
        FileWriter file = new FileWriter(jsonFile);
        try {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> staff = csv.parse();
            staff.forEach(System.out::println);
            return staff;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}


