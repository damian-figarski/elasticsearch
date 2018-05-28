import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;

public final class ESClient {

    private TransportClient client;

    public TransportClient getClient() {
        return client;
    }

    public void setClient(TransportClient client) {
        this.client = client;
    }

    public String createRecord(String index, String type, String json) {

        IndexResponse response = null;
        if (client != null) {
            response = client.prepareIndex(index, type)
                    .setSource(json, XContentType.JSON)
                    .get();
        }

        return response.getId();
    }

}
