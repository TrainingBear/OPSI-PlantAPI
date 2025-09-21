package com.trbear9.deprecated;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;


///  Free plan API (Personal Usage)
///  [...](https://perenual.com/subscription-api-pricing)
///
/// API::load() akan memuat semua data dari database ke backend servers
/// di load pada startup
@Getter
public final class API {
    private static Object T;
    private static final int MAX_QUERY_SEARCH_RECUR = 12_69_9;
    @Setter static public String key = "sk-fh6s685f9f720fc7b11089";
    @Setter static JdbcTemplate template;
    final static RestTemplate rest = new RestTemplate();
    final public static ObjectMapper mapper = new ObjectMapper();
    final Table table;
    final boolean hasParent;
    final private Map<Integer, JsonNode> pages  = new HashMap<>();
    final private Map<Integer, JsonNode> children = new HashMap<>();
    final private Map<Integer, byte[]> images = new HashMap<>();
    final private Map<String, Set<JsonNode>> string_query = new HashMap<>();

    final public static API plant  = new API(Table.PLANT_LIST, true);
    final public static API guide  = new API(Table.PLANT_GUIDE_LIST, true);
    final public static API disease = new API(Table.PLANT_DISEASE_LIST, true);
    final public static API hardiness = new API(Table.PLANT_HARDINESS, false);
    final public static API plant_details = new API(Table.PLANT_DETAILS, false);
    final public static API[] content_as_array = {plant, guide, disease, hardiness, plant_details};

    private API(Table table, boolean hasParent) {
        this.table = table;
        this.hasParent = hasParent;
    }

    public void load() throws JsonProcessingException {
        Logger log = LoggerFactory.getLogger("DB Manager");
        log.warn("STARTING LOADING {}... .. . . .", table.name);
        long last = System.nanoTime();
        String sql = "SELECT * FROM "+table.name;
        List<Map<String, Object>> result = template.queryForList(sql);

        for (Map<String, Object> map : result) {
            int id = ((int) map.get("id"));

            if(table.equals(Table.PLANT_HARDINESS)){
                byte[] data = (byte[]) map.get("data");
                images.put(id, data);
                continue;
            }

            JsonNode data = mapper.readTree(((String) map.get("data")));
            String path = table.equals(Table.PLANT_GUIDE_LIST)? "species_id" : "id";
            if(hasParent){
                StringBuilder builder = new StringBuilder();
                pages.put(id, data);
                for (JsonNode child : data.path("data")) {
                    int child_id = child.path(path).asInt();
                    String commonName = child.path("common_name").asText().toLowerCase();
                    children.put(child_id, child);
                    string_query.computeIfAbsent(commonName, k -> new HashSet<>()).add(child);
                    builder.append(commonName).append(", ");
                }
                log.info("pruned page {} with entry: {}", id, builder);
            } else {
                String commonName = data.path("common_name").asText().toLowerCase();
                string_query.computeIfAbsent(commonName, k -> new HashSet<>()).add(data);
                children.put(id, data);
            }
        }
        log.info("DONE! {} took {}ms", table.name, (System.nanoTime() - last) / 1000000);
    }

    public void update(int id, JsonNode json) throws JsonProcessingException {
        String sql = "INSERT INTO "+table.name+"(id, data) VALUES(?, ?)";
        String string = mapper.writeValueAsString(json);
        template.update(sql, id, string);
        if (hasParent) this.pages.put(id, json);
        else this.children.put(id, json);
    }

    public JsonNode getPages(int page) throws JsonProcessingException {
        if(pages.containsKey(page)) return pages.get(page);
        if(hasParent) {
            JsonNode json = rest.getForObject(table.getUrl(key, "&page="+page), JsonNode.class);
            update(page, json);
            return json;
        }
        // berarti page = id. bukan page sebenarnya
        if(children.containsKey(page)) return children.get(page);
        if(table.equals(Table.PLANT_HARDINESS)) throw new IllegalArgumentException("Tipe data ini berbentuk byte buffer, tolong gunakan getHardness() untuk mendapatkan fullsize buffer!");
        JsonNode json = rest.getForObject(table.getUrl(key, String.valueOf(page)), JsonNode.class);
        update(page, json);
        return json;
    }

    public byte[] getHardness(int id) throws IOException {
        if(!table.equals(Table.PLANT_HARDINESS)) throw new IllegalStateException("Tipe data ini berbentuk byte buffer, tolong gunakan getChildren() untuk mendapatkan respon JsonNode.class");
        return getChildren(id, byte[].class);
    }
    public JsonNode getChildren(int id) throws IOException {
        if(table.equals(Table.PLANT_HARDINESS)) throw new IllegalStateException("Tipe data ini tidak berbentuk Json, tolong gunakan getHardness untuk mendapatkan fullsize buffer!");
        return getChildren(id, JsonNode.class);
    }
    public <T> T getChildren(int id, Class<T> clazz) throws IOException {
        if(table.equals(Table.PLANT_HARDINESS)) {
            if(images.containsKey(id)) return (T) images.get(id);
            byte[] bytes = rest.getForObject(Table.PLANT_HARDINESS.getUrl(key, String.valueOf(id)), byte[].class);
            images.put(id, bytes);
            String sql = "INSERT INTO "+Table.PLANT_HARDINESS.name+"(id, data) VALUES(?, ?)";
            template.update(sql, id, bytes);
            return (T) bytes;
        }

        if(children.containsKey(id)) return (T) children.get(id);
        if(!hasParent) return (T) getPages(id);

        String path = table.equals(Table.PLANT_GUIDE_LIST)? "species_id" : "id";
        JsonNode next = getPages(1);
        int last_page = next.path("last_page").asInt();
        JsonNode found = null;
        for (int i = 1; i <= last_page; i++) {
            if(pages.containsKey(i)) continue;
            JsonNode json = getPages(i);
            for (JsonNode child : json.path("data")) {
                int child_id = child.path(path).asInt();
                children.put(child_id, child);
                if(child_id == id) found = child;
            }
            if(found!=null) return (T) found;
        }
        throw new IllegalArgumentException("Tidak dapat menemukan id tanaman!");
    }

    public Set<JsonNode> getChildren(String query) throws IOException {
        query = query.toLowerCase();
        if(table.equals(Table.PLANT_HARDINESS)) throw new IllegalArgumentException("Maaf, tapi plant hardiness tidak dapat menggunakan query berbentuk string!");
        if(string_query.containsKey(query)) return string_query.get(query);

        Iterator<String> iterator = Set.copyOf(string_query.keySet()).iterator();
        while (iterator.hasNext()) {
            String query_set = iterator.next();
            if(query_set.equals(query)) return string_query.get(query_set);
            if(query_set.startsWith(query))
                string_query.computeIfAbsent(query, k -> new HashSet<>())
                        .addAll(string_query.get(query_set));
        }
        if(!hasParent){
            for (int i = 1;i <= MAX_QUERY_SEARCH_RECUR; i++) {
                try {
                    JsonNode child = getPages(i);
                    String commonName = child.path("common_name").asText();
//                    String scientificName = child.path("scientific_name").asText();
                    if(commonName.equals(query)
//                            || scientificName.equals(query)
                    ) return string_query.get(query);
                    if(commonName.startsWith(query)
//                            || scientificName.startsWith(query)
                    ) string_query.computeIfAbsent(query, k -> new HashSet<>()).add(child);
                }  catch (RestClientException e) {
                    e.printStackTrace();
                    return string_query.get(query);
                }
            }
            return string_query.get(query);
        }
        JsonNode next = getPages(1);
        int last_page = next.path("last_page").asInt();
        for (int i = 1; i <= last_page; i++) {
            JsonNode pages = getPages(i);
            for (JsonNode child : pages.path("data")) {
                String commonName = child.path("common_name").asText();
//                    String scientificName = child.path("scientific_name").asText();
                if(commonName.equals(query)
//                            || scientificName.equals(query)
                ) return string_query.get(query);
                if(commonName.startsWith(query)
//                            || scientificName.startsWith(query)
                ) string_query.computeIfAbsent(query, k -> new HashSet<>()).add(child);
            }
        }
        return string_query.get(query);
    }
}