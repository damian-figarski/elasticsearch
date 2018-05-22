import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParseTest {

    @Test
    public void parsingBeanToJSON()
            throws JsonProcessingException {
        Bean bean = new Bean(1, "Name of json file", "Description of json file");
        String json = new ObjectMapper()
                .writeValueAsString(bean);

        assertEquals("{\"id\":1,\"name\":\"Name of json file\",\"desc\":\"Description of json file\"}", json);
    }

    @Test
    public void parsingJSONToBean()
            throws IOException {
        String json = "{\"id\":1,\"name\":\"Name of json file\",\"desc\":\"Description of json file\"}";

        Bean bean = new ObjectMapper()
                .readerFor(Bean.class)
                .readValue(json);

        assertEquals((Object) 1, bean.getId());
        assertEquals("Name of json file", bean.getName());
        assertEquals("Description of json file", bean.getDesc());
    }


}
