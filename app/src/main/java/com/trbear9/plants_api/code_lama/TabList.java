package com.trbear9.plants_api.code_lama;

public abstract class TabList {
//    API api;
//    protected final JdbcTemplate template;
//    private final RestTemplate rest = new RestTemplate();
//    final Table table;
//    final boolean hasParent;
//    final public Map<Integer, JsonNode> pages  = new HashMap<>();
//    final public Map<Integer, JsonNode> children = new HashMap<>();
//
//    protected TabList(JdbcTemplate template, Table table, boolean hasParent) {
////        this.template = template;
////        this.table = table;
////        this.hasParent = hasParent;
//    }
//
//    public void load() throws JsonProcessingException {
//        Logger log = LoggerFactory.getLogger("DB Manager");
////        log.warn("Starting loading {}", table.name);
//        long last = System.nanoTime();
////        String sql = "SELECT * FROM "+table.name;
////        List<Map<String, Object>> result = template.queryForList(sql);
////        for (Map<String, Object> map : result) {
//            int id = ((int) map.get("id"));
//            JsonNode data = api.mapper.readTree(((String) map.get("data")));
//            String path = table.equals(Table.PLANT_GUIDE_LIST)? "species_id" : "id";
//            if(hasParent){
//                StringBuilder builder = new StringBuilder();
//                pages.put(id, data);
//                for (JsonNode child : data.path("data")) {
//                    int child_id = child.path(path).asInt();
//                    children.put(child_id, child);
//                    builder.append(child.path("common_name").asText()).append(", ");
//                }
//                log.info("pruned page {} with entry: {}", id, builder);
//            } else children.put(id, data);
//        }
//        log.info("Finished loading {} in {}ms", table.name, (System.nanoTime() - last) / 1000000);
//    }
//
//    public void update(int id, JsonNode json, boolean isPage) throws JsonProcessingException {
//        String sql = "INSERT INTO "+table.name+"(id, data) VALUES(?, ?)";
//        String string = api.mapper.writeValueAsString(json);
//        template.update(sql, id, string);
//        if (isPage) this.pages.put(id, json);
//        else this.children.put(id, json);
//    }
//
//    public JsonNode getPages(int page) throws JsonProcessingException {
//        if(!hasParent) throw new IllegalStateException("Tidak dapat mengambil halaman tanpa parent!");
//        boolean flag = true;
//        if(pages.containsKey(page)) return pages.get(page);
//        JsonNode json = rest.getForObject(table.getUrl(api.key, "&page="+page), JsonNode.class);
//        update(page, json, flag);
//        return json;
//    }
//
//    public JsonNode getChildren(int id) throws JsonProcessingException {
//        boolean flag = false;
//        if(children.containsKey(id)) return children.get(id);
//        if(!hasParent){
//            JsonNode child = rest.getForObject(table.getUrl(api.key, String.valueOf(id)), JsonNode.class);
//            update(id, child, flag);
//            return child;
//        }
//        String path = table.equals(Table.PLANT_GUIDE_LIST)? "species_id" : "id";
//        Set<Integer> key = pages.keySet();
//        for (int i : key)
//            for (JsonNode child : pages.get(i)) {
//                int child_id = child.path(path).asInt();
//                update(child_id, child, flag);
//                if (child_id == id) return child;
//            }
//
//        JsonNode next = pages.values().iterator().hasNext()? pages.values().iterator().next() : null;
//        if(next==null) next = getPages(1);
//        int last_page = next.path("last_page").asInt();
//        for (int i = 1; i <= last_page; i++) {
//            if(pages.containsKey(i)) continue;
//            JsonNode json = getPages(i);
//            for (JsonNode child : json.path("data")) {
//                int child_id = child.path(path).asInt();
//                update(child_id, child, flag);
//                if(child_id == id) return child;
//            }
//        }
//        throw new IllegalArgumentException("Tidak dapat menemukan id tanaman!");
//    }
}