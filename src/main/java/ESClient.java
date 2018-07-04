import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequestBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public final class ESClient {

    private final String queryBody;
    private TransportClient client;

    public ESClient() {
        queryBody =
                        "{                                                                   " +
                        "    \"query\" : {                                                   " +
                        "       \"bool\": {                                                  " +
                        "            \"should\": [                                           " +
                        "                { \"match\": { \"name\": \"{{param}}\" }},          " +
                        "                { \"match\": { \"desc\": \"{{param}}\" }}           " +
                        "            ]                                                       " +
                        "        }                                                           " +
                        "    }                                                               " +
                        "}                                                                   ";
    }


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

    public List<Bean> search(String searchQuery)
            throws IOException {

        List<Bean> beans = new ArrayList<>();
        Map<String, Object> templateParams = new HashMap<>();
        templateParams.put("param", searchQuery);

        if (client != null) {
            SearchResponse sr = new SearchTemplateRequestBuilder(client)
                    .setScript(queryBody)
                    .setScriptType(ScriptType.INLINE)
                    .setScriptParams(templateParams)
                    .setRequest(new SearchRequest())
                    .get()
                    .getResponse();

            SearchHits hits = sr.getHits();

            for (SearchHit hit : hits) {
                Bean bean = new ObjectMapper()
                        .readerFor(Bean.class)
                        .readValue(hit.getSourceAsString());

                beans.add(bean);
            }
        }

        return beans;
    }

}
