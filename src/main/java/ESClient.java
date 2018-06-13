import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

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


    public void updateRecord(String index, String type, String id, String desc)
            throws IOException, InterruptedException, ExecutionException {

        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(index);
        updateRequest.type(type);
        updateRequest.id(id);
        updateRequest.doc(jsonBuilder()
                .startObject()
                .field("desc", desc)
                .endObject()
        );
        if (client != null)
            client.update(updateRequest).get();
    }

    public void deleteRecord(String index, String type, String id) {
        if (client != null)
            client.prepareDelete(index, type, id).get();
    }

}
