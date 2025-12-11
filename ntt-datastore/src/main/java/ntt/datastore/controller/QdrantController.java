package ntt.datastore.controller;

import ntt.datastore.service.QdrantService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qdrant")
@CrossOrigin(origins = "*")
public class QdrantController {
    private final QdrantService qdrantService;

    public QdrantController(QdrantService qdrantService) {
        this.qdrantService = qdrantService;
    }

    @PostMapping("create-collection/{name}")
    public String createQdrantCollection(@PathVariable(value="name") String name) {
        return this.qdrantService.createQdrantCollection(name);
    }

    @DeleteMapping("delete-collection/{name}")
    String deleteQdrantCollection(@PathVariable("name") String name) {
        return this.qdrantService.deleteQdrantCollection(name);
    }

    @DeleteMapping("delete-project-collection/{uuid}")
    String deleteCompanyProjectCollection(@PathVariable("uuid") String uuid) {
        return this.qdrantService.deleteCompanyProjectCollection(uuid);
    }


}
