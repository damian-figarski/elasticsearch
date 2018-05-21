import com.fasterxml.jackson.annotation.JsonProperty;

public class Bean {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("desc")
    private String desc;

    public Bean(
            @JsonProperty("id") Integer id,
            @JsonProperty("name") String name,
            @JsonProperty("desc") String desc) {
        this.id = id;
        this.name = name;
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
