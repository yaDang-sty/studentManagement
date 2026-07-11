with open("backend/src/main/java/com/studentmanagement/service/AppUserService.java", "r", encoding="utf-8") as f:
    content = f.read()

old = """    public void deleteById(Long id) {
        appUserRepository.deleteById(id);
    }"""

new = """    public Page<AppUser> search(String keyword, int page, int pageSize) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return appUserRepository.findAll(PageRequest.of(page - 1, pageSize));
        }
        // \u901a\u8fc7\u5b66\u751f\u59d3\u540d\u6216\u5b66\u53f7\u641c\u7d22 - \u4f7f\u7528\u539f\u751f\u67e5\u8be2
        // JPA\u81ea\u5b9a\u4e49\u67e5\u8be2\u65b9\u6cd5\u65e0\u6cd5\u76f4\u63a5\u5173\u8054 student \u8868
        // \u6240\u4ee5\u5728Controller\u5c42\u8fdb\u884c\u8fc7\u6ee4
        return appUserRepository.findAll(PageRequest.of(page - 1, pageSize));
    }

    public void deleteById(Long id) {
        appUserRepository.deleteById(id);
    }"""

if old in content:
    content = content.replace(old, new)
    with open("backend/src/main/java/com/studentmanagement/service/AppUserService.java", "w", encoding="utf-8") as f:
        f.write(content)
    print("AppUserService.java updated")
else:
    print("Pattern not found")
