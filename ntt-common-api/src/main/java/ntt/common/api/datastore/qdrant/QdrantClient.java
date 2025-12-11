/**
 * -----------------------------------------------------------------------------
 * Author      : Toandd
 * Created On  : 2025-13-8
 * Description : Create common qdrant Client
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */
package ntt.common.api.datastore.qdrant;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


//For Docker Load balancer with Eureka or Consul
//@FeignClient("storage")

//If you don't need LoadBalancer just only want to call static URL:
@FeignClient(
        name = "qdrant-storage",
        url = "${clients.storage.qdrant.url}"
)
public interface QdrantClient {

    @PostMapping("/api/qdrant/create-collection/{name}")
    String createQdrantCollection(@PathVariable("name") String name);

    @DeleteMapping("/api/qdrant/delete-collection/{name}")
    String deleteQdrantCollection(@PathVariable("name") String name);

    @DeleteMapping("/api/qdrant/delete-project-collection/{uuid}")
    String deleteCompanyProjectCollection(@PathVariable("uuid") String uuid);

}
