import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Application {

    public static void main(String[] args) {
        delete();
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

}
