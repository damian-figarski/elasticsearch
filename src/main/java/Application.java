import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application {

    public static void main(String[] args) {
        create(1, "Name of first record", "Description of first record");
        create(2, "Name of second record", "Description of second record");
        create(3, "Name of third record", "Description of thrid record");

        System.out.println("Searching by \"first\", should return only one record");
        List<Bean> list = search("first");

        for (Bean b : list) {
            System.out.println("ID: " + b.getId() + ", NAME: " + b.getName() + ", DESC: " + b.getDesc());
        }

        System.out.println();
        System.out.println("Searching by \"Name\", should return three records");

        list = search("Name");

        for (Bean b : list) {
            System.out.println("ID: " + b.getId() + ", NAME: " + b.getName() + ", DESC: " + b.getDesc());
        }
        
    }

    private static TransportClient client() {
        TransportClient client = null;

        try {
            Settings settings = Settings.builder().build();
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName("localhost"), 9300));
        } catch (UnknownHostException uhe) {
            Logger.getLogger(Application.class.getClass().getName()).log(Level.SEVERE, null, uhe);
        }

        return client;
    }


    public static void create(Integer id, String name, String desc) {
        ESClient esClient = new ESClient();

        try {
            esClient.setClient(client());

            Bean bean = new Bean(id, name, desc);
            String json = new ObjectMapper()
                    .writeValueAsString(bean);

            esClient.createRecord("elastic", "test", json);
        } catch (JsonProcessingException e) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE,     null, e);
        } finally {
            esClient.getClient().close();
        }
    }

    public static void update() {
        ESClient esClient = new ESClient();

        try {
            esClient.setClient(client());

            esClient.updateRecord("elastic", "test", "B38qqGMBStWNZwkcY5gE", "New description string");
        } catch (InterruptedException e) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE,     null, e);
        } catch (ExecutionException e) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, e);
        } catch (IOException e) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            esClient.getClient().close();
        }
    }

    public static void delete() {
        ESClient esClient = new ESClient();
        esClient.setClient(client());
        esClient.deleteRecord("elastic", "test", "_rbUEmMBMMaFSBrui0fI");
        esClient.getClient().close();
    }

    public static List<Bean> search(String searchQuery) {
        ESClient esClient = new ESClient();

        List<Bean> list = null;

        try {
            esClient.setClient(client());
            list = esClient.search(searchQuery);
        } catch (IOException e) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            esClient.getClient().close();
        }

        return list;
    }



}
