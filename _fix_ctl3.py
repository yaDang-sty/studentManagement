with open("backend/src/main/java/com/studentmanagement/controller/AppUserController.java", "r", encoding="utf-8") as f:
    content = f.read()

old_search = """    @PostMapping("/search")
    public ResponseEntity<PageResult<Map<String, Object>>> search(@RequestBody Map<String, Object> body) {
        String keyword = (String) body.get("keyword");
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        Page<AppUser> p = appUserService.search(keyword, page, pageSize);
        List<Map<String, Object>> records = new ArrayList<>();
        for (AppUser user : p.getContent()) {
            records.add(toUserMap(user));
        }
        return ResponseEntity.ok(new PageResult<>(records, p.getTotalElements(), page, pageSize));
    }"""

new_search = """    @PostMapping("/search")
    public ResponseEntity<PageResult<Map<String, Object>>> search(@RequestBody Map<String, Object> body) {
        String keyword = body.get("keyword") != null ? ((String) body.get("keyword")).toLowerCase() : "";
        int page = body.get("page") != null ? ((Number) body.get("page")).intValue() : 1;
        int pageSize = body.get("pageSize") != null ? ((Number) body.get("pageSize")).intValue() : 10;
        List<AppUser> allUsers = appUserService.findAll();
        List<Map<String, Object>> filtered = new ArrayList<>();
        for (AppUser user : allUsers) {
            Map<String, Object> m = toUserMap(user);
            String name = (String) m.getOrDefault("name", "");
            String studentNo = (String) m.getOrDefault("studentNo", "");
            String typeDisplay = (String) m.getOrDefault("userTypeDisplay", "");
            if (keyword.isEmpty() || name.toLowerCase().contains(keyword) || studentNo.contains(keyword) || typeDisplay.contains(keyword)) {
                filtered.add(m);
            }
        }
        int total = filtered.size();
        int from = (page - 1) * pageSize;
        int to = Math.min(from + pageSize, total);
        List<Map<String, Object>> records = from < total ? filtered.subList(from, to) : new ArrayList<>();
        return ResponseEntity.ok(new PageResult<>(records, total, page, pageSize));
    }"""

if old_search in content:
    content = content.replace(old_search, new_search)
    with open("backend/src/main/java/com/studentmanagement/controller/AppUserController.java", "w", encoding="utf-8") as f:
        f.write(content)
    print("Controller search updated")
else:
    print("Pattern not found")
    idx = content.find("/search")
    if idx >= 0:
        print(repr(content[idx-20:idx+200]))
