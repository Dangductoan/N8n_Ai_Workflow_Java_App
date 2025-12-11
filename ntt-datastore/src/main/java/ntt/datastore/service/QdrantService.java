/**
 * -----------------------------------------------------------------------------
 * Author      : TacjLee
 * Created On  : 2025-11-14
 * Description : In Java, QdrantHttpClient only work with port 6333
 * -----------------------------------------------------------------------------
 * Copyright (c) 2025 Ntt. All rights reserved.
 * -----------------------------------------------------------------------------
 */
package ntt.datastore.service;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Collections;
import org.springframework.stereotype.Service;

import ntt.datastore.properties.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.qdrant.client.grpc.Collections.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QdrantService {

    private static final Logger logger = LoggerFactory.getLogger(QdrantService.class);
    private final QdrantClient qdrantClient;
    private final  Settings settings;




    public QdrantService(Settings  settings, QdrantClient qdrantClient  ) {
        this.qdrantClient = qdrantClient;
        this.settings = settings;



    }
    // Create Qdrant collection

    public String createQdrantCollection(String collectionName) {

        try {
            logger.info("Checking if Qdrant collection exists: {}", collectionName);

            //Check if Collection exists
            boolean collectionExists = Boolean.TRUE.equals(qdrantClient.collectionExistsAsync(collectionName).get());

            if(!collectionExists) {
                logger.info("Collection {} does not exist, creating new one...", collectionName);

                String embeddingsMethod = settings.getEmbeddingsMethod(); // Get embeddings method from settings
                logger.info("Using embeddings method: {}", embeddingsMethod);

                //dense
                if ("dense".equals(embeddingsMethod)) {
                    logger.info("Creating dense collection : {}",collectionName);
                    Collections.CreateCollection request = Collections.CreateCollection.newBuilder()
                            .setCollectionName(collectionName)
                            .setVectorsConfig(
                                    Collections.VectorsConfig.newBuilder()
                                            .setParams(VectorParams.newBuilder()
                                                    .setSize(384)
                                                    .setDistance(Distance.Cosine))
                            )
                            .build();
                    qdrantClient.createCollectionAsync(request);
                    logger.info("Successfully created dense collection: {}", collectionName);
                }
                //hybrid
                else if ("hybrid".equals(embeddingsMethod)) {
                    logger.info("Creating hybrid collection: {}",collectionName );

                    Map<String, SparseVectorParams> namedSparseVectors = new HashMap<>();
                    namedSparseVectors.put("text-sparse", SparseVectorParams.newBuilder().setModifier(Modifier.Idf).build());

                    Collections.CreateCollection request = Collections.CreateCollection.newBuilder()
                            .setCollectionName(collectionName)
                            .setVectorsConfig(
                                    Collections.VectorsConfig.newBuilder()
                                            .setParams(VectorParams.newBuilder()
                                                    .setSize(384)
                                                    .setDistance(Distance.Cosine))
                            )
                            .setSparseVectorsConfig(SparseVectorConfig.newBuilder().putAllMap(namedSparseVectors))
                            .build();
                    qdrantClient.createCollectionAsync(request);
                    logger.info("Successfully created hybrid collection: {}", collectionName);
                }
                //unknow use default
                else {
                    logger.warn("Unknown embeddings method: {}, using dense as default", embeddingsMethod);
                    Collections.CreateCollection request = Collections.CreateCollection.newBuilder()
                            .setCollectionName(collectionName)
                            .setVectorsConfig(
                                    Collections.VectorsConfig.newBuilder()
                                            .setParams(VectorParams.newBuilder()
                                                    .setSize(384)
                                                    .setDistance(Distance.Cosine))
                            )
                            .build();
                    qdrantClient.createCollectionAsync(request);
                    logger.info("Successfully created default dense collection: {}", collectionName);
                }
            } else {
                logger.info("Qdrant collection already exists: {}", collectionName);

            }

            return collectionName;
        }
        catch (Exception e) {
            logger.error("Failed to create Qdrant collection {}: {}", collectionName, e.getMessage(), e);
            throw new RuntimeException("Failed to create Qdrant collection " + collectionName, e);
        }
    }


    public String deleteQdrantCollection(String collectionName) {
        try {
            boolean collectionExists = Boolean.TRUE.equals(qdrantClient.collectionExistsAsync(collectionName).get());
            if (!collectionExists) {
              qdrantClient.deleteCollectionAsync(collectionName);
            } else {
                logger.info("Qdrant collection does not exist: {}",collectionName);
            }
            return collectionName;
        }catch (Exception e) {
            logger.error("Failed to delete Qdrant collection {}: {}",collectionName,e.getMessage());
            throw new RuntimeException("Failed to delete Qdrant collection " + collectionName, e);
        }

    }

    public String deleteCompanyProjectCollection(String uuid) {

        try {
            // 1) Lấy danh sách tất cả collections
            List<String> collectionsResponse = qdrantClient.listCollectionsAsync().get();

            int deletedCount = 0;

            // 2) Duyệt và xóa các project collections thuộc company
            for (String collectionName : collectionsResponse) {

                // Kiểm tra: tên bắt đầu bằng "project_" và chứa companyUuid
                if (collectionName != null
                        && collectionName.startsWith("project_")
                        && collectionName.contains(uuid)) {
                    try {
                        qdrantClient.deleteCollectionAsync(collectionName);
                        logger.info("Deleted project collection: {}", collectionName);
                        deletedCount++;
                    } catch (Exception e) {
                        // Lỗi cục bộ khi xóa 1 collection => chỉ warn, tiếp tục các collection khác
                        logger.warn("Failed to delete project collection {}: {}", collectionName, e.getMessage(), e);
                    }
                }
            }

            // 3) Log tổng kết
            logger.info("Deleted {} project collections for company {}", deletedCount, uuid);


        } catch (Exception e) {
            // Lỗi tổng quát (ví dụ: thất bại khi getCollections) => log error, KHÔNG throw
            logger.error("Failed to delete project collections for company {}: {}", uuid, e.getMessage(), e);
        }
        return  uuid;
    }

}